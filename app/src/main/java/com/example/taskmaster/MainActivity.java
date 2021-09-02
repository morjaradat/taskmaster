package com.example.taskmaster;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.Callback;
import com.amazonaws.mobile.client.UserStateDetails;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.pinpoint.PinpointConfiguration;
import com.amazonaws.mobileconnectors.pinpoint.PinpointManager;
import com.amplifyframework.AmplifyException;
import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Task;
import com.amplifyframework.datastore.generated.model.Team;
import com.amplifyframework.storage.s3.AWSS3StoragePlugin;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String TASK_LATITUDE = "taskLatitude";
    public static final String TASK_LONGITUDE = "taskLongitude";

    private static PinpointManager pinpointManager;

    public static final String TASK_TITLE = "taskTitle";
    public static final String TASK_BODY = "taskBody";
    public static final String TASK_STATUS = "taskStatus";
    public static final String TASK_FILE = "taskFile";
    private static final String TAG = "MainActivity";
    private static List<Task> taskList = new ArrayList<>();
    private static Adapter adapter;
    private Handler handler;
    private Team teamData = null;
    private String selectedTeam=null;

    public static PinpointManager getPinpointManager(final Context applicationContext) {
        if (pinpointManager == null) {
            final AWSConfiguration awsConfig = new AWSConfiguration(applicationContext);
            AWSMobileClient.getInstance().initialize(applicationContext, awsConfig, new Callback<UserStateDetails>() {
                @Override
                public void onResult(UserStateDetails userStateDetails) {
                    Log.i("INIT", userStateDetails.getUserState().toString());
                }

                @Override
                public void onError(Exception e) {
                    Log.e("INIT", "Initialization error.", e);
                }
            });

            PinpointConfiguration pinpointConfig = new PinpointConfiguration(
                    applicationContext,
                    AWSMobileClient.getInstance(),
                    awsConfig);

            pinpointManager = new PinpointManager(pinpointConfig);

            FirebaseMessaging.getInstance().getToken()
                    .addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }
                        final String token = task.getResult();
                        Log.d(TAG, "Registering push notifications token: " + token);
                        pinpointManager.getNotificationClient().registerDeviceToken(token);
                    });
        }
        return pinpointManager;
    }


    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        // Initialize PinpointManager
        getPinpointManager(getApplicationContext());

        taskList= new ArrayList<>();
        try {
//            Amplify.addPlugin(new AWSDataStorePlugin());
            Amplify.addPlugin(new AWSCognitoAuthPlugin());
            Amplify.addPlugin(new AWSApiPlugin());
            Amplify.addPlugin(new AWSS3StoragePlugin());
            Amplify.configure(getApplicationContext());

            Log.i(TAG, "Initialized Amplify");
        } catch (AmplifyException e) {
            Log.e(TAG, "Could not initialize Amplify", e);
        }

        if (Amplify.Auth.getCurrentUser()!= null){
            Log.i(TAG, "Auth: " + Amplify.Auth.getCurrentUser().toString());
        }else {
            Log.i(TAG, "Auth:  no user " + Amplify.Auth.getCurrentUser());
            Intent goToLogin= new Intent(this,LoginActivity.class);
            startActivity(goToLogin);
        }

//        TextView userNameText = (findViewById(R.id.usernameInHomePage));


        Log.i(TAG, "onCreate: called");

        setContentView(R.layout.activity_main);

        Button addTask = findViewById(R.id.addTask);
        addTask.setOnClickListener(getViewAddTask);

        Button allTask = findViewById(R.id.allTask);
        allTask.setOnClickListener(getViewAllTask);

        Button setting = findViewById(R.id.setting);
        setting.setOnClickListener(getViewSetting);

        Button logout = findViewById(R.id.logout);
       logout.setOnClickListener(v -> logout());


        RecyclerView recyclerView = findViewById(R.id.recycler_task);

        handler = new Handler(Looper.getMainLooper(),
                message -> {
                    listItemChanged();
                    return false;
                });

        if (isNetworkAvailable(getApplicationContext())) {
            getTaskDataFromAPI();
            Log.i(TAG, "NET: the network is available");
        } else {
            getDataFromAmplify();
            Log.i(TAG, "NET: net down");
        }


        // link list to the adapter
        adapter = new Adapter(taskList, new Adapter.onTaskClickedListener() {


            @Override
            public void onTaskClicked(int position) {
                Intent taskDetailsIntent = new Intent(getApplicationContext(), TaskDetailsActivity.class);

                taskDetailsIntent.putExtra(TASK_TITLE, taskList.get(position).getTitle());
                taskDetailsIntent.putExtra(TASK_BODY, taskList.get(position).getDescription());
                taskDetailsIntent.putExtra(TASK_STATUS, taskList.get(position).getStatus());
                taskDetailsIntent.putExtra(TASK_LATITUDE, taskList.get(position).getLocationLat());
                taskDetailsIntent.putExtra(TASK_LONGITUDE, taskList.get(position).getLocationLon());

                taskDetailsIntent.putExtra(TASK_FILE, taskList.get(position).getFileName());

                startActivity(taskDetailsIntent);

            }

            @Override
            public void onDeleteTask(int position) {
//                Task item = Task.builder().title(taskList.get(position).getTitle()).build();

               Amplify.API.mutate(ModelMutation.delete(taskList.get(position)),
                       response -> Log.i(TAG, "item deleted from API:" ),
                       error -> Log.e(TAG, "Delete failed", error)
               );



                taskList.remove(position);
                listItemChanged();
                Toast.makeText(MainActivity.this, "Item deleted", Toast.LENGTH_SHORT).show();
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.SUCCESS,TAG);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

//        throw new RuntimeException("Test Crash");
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//        String username = preference.getString("userName", "user") + "'s Tasks";
        String teamName =preference.getString("teamName", "choose your team");


        if (Amplify.Auth.getCurrentUser()!= null){
            TextView userNameText = (findViewById(R.id.usernameInHomePage));
            userNameText.setText(Amplify.Auth.getCurrentUser().getUsername()+ "'s Tasks");
        }else {
            Intent goToLogin= new Intent(this,LoginActivity.class);
            startActivity(goToLogin);
        }

        TextView teamText = (findViewById(R.id.Team));
        teamText.setText(teamName);

        selectedTeam= preference.getString("teamName",null);
        if (selectedTeam!= null){
            getTeamFromApiByName();

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            taskList.clear();
            Log.i(TAG, "-----selected team true-------- ");
            Log.i(TAG, selectedTeam);
            getTaskDataFromAPIByTeam();
        }
        Log.i(TAG, "onResume: called");
    }



    public synchronized static void getDataFromAmplify() {
        Amplify.DataStore.query(Task.class, todos -> {
                    while (todos.hasNext()) {
                        Task todo = todos.next();
                        taskList.add(todo);
                        Log.i(TAG, "Name FROM DATAStore : " + todo.getTitle());
                        Log.i(TAG, "status  FROM DATAStore: " + todo.getStatus());
                    }
                }, failure -> Log.e(TAG, "Could not query DataStore", failure)

        );

    }

    @SuppressLint("NotifyDataSetChanged")
    private static void listItemChanged() {
        adapter.notifyDataSetChanged();
    }


    @SuppressLint("MissingPermission")
    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager =
                ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager
                .getActiveNetworkInfo().isConnected();
    }

    public  void getTaskDataFromAPI()  {
        Log.i(TAG, "getTaskDataFromAPI: get all data");
            Amplify.API.query(ModelQuery.list(Task.class),
                    response -> {
                        for (Task task : response.getData()) {
                            taskList.add(task);
                            Log.i(TAG, "getFrom api: the Task from api are => " + task);
//                            Log.i(TAG, "getFrom api: the Task from api are => " + task.getTeam());
                        }
                        handler.sendEmptyMessage(1);
                    },
                    error -> Log.e(TAG, "getFrom api: Failed to get Task from api => " + error.toString())
            );
    }

    public  void  getTaskDataFromAPIByTeam(){
        Log.i(TAG, "getTaskDataFromAPIByTeam: get task by team");

        Amplify.API.query(ModelQuery.list(Task.class, Task.TEAM.contains(teamData.getId())),
                response -> {
                    for (Task task : response.getData()) {

                            Log.i(TAG, "task-team-id: " + task.getTeam().getId());
                            Log.i(TAG, "team-id: "+ teamData.getId());
                            taskList.add(task);

                        Log.i(TAG, "getFrom api by team: the Task from api are => " + task);
                    }
                    handler.sendEmptyMessage(1);
                },
                error -> Log.e(TAG, "getFrom api: Failed to get Task from api => " + error.toString())
        );
    }

    public void getTeamFromApiByName() {
            Amplify.API.query(
                    ModelQuery.list(Team.class, Team.NAME.contains(selectedTeam)),
                    response -> {
                        for (Team teamDetail : response.getData()) {
                            Log.i(TAG, "selected team is  =>"+teamDetail);
                            teamData=teamDetail;
                        }
                    },
                    error -> Log.e(TAG, "Query failure", error)
            );
    }

    private final View.OnClickListener getViewSetting = v -> {
        Intent settingIntent = new Intent(getBaseContext(), SettingActivity.class);
        startActivity(settingIntent);
    };

    private final View.OnClickListener getViewAddTask = v -> {
        Intent addTaskIntent = new Intent(getBaseContext(), AddTaskActivity.class);
        startActivity(addTaskIntent);
    };

    private final View.OnClickListener getViewAllTask = v -> {
        Intent allTaskIntent = new Intent(getBaseContext(), AllTasksActivity.class);
        startActivity(allTaskIntent);
    };


    public void logout(){
        Amplify.Auth.signOut(
                () ->{
                    Log.i("AuthQuickstart", "Signed out successfully");
                    Intent goToLogin = new Intent(getBaseContext(), LoginActivity.class);
                    startActivity(goToLogin);
                } ,
                error -> Log.e("AuthQuickstart", error.toString())
        );
    }
}