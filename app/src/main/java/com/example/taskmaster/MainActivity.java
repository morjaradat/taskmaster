package com.example.taskmaster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.amplifyframework.AmplifyException;
import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.AWSDataStorePlugin;
import com.amplifyframework.datastore.generated.model.Task;
import com.amplifyframework.datastore.generated.model.Team;


import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    public static final String TASK_TITLE = "taskTitle";
    public static final String TASK_BODY = "taskBody";
    public static final String TASK_STATUS = "taskStatus";
    public static final String TASK_LIST = "TaskList";
    private static final String TAG = "MainActivity";
    private static List<Task> taskList = new ArrayList<>();
    private static Adapter adapter;
    private Handler handler;
    private Team teamData = null;
    private String selectedTeam=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        taskList= new ArrayList<>();
        try {
//            Amplify.addPlugin(new AWSDataStorePlugin());
            Amplify.addPlugin(new AWSApiPlugin());
            Amplify.configure(getApplicationContext());

            Log.i(TAG, "Initialized Amplify");
        } catch (AmplifyException e) {
            Log.e(TAG, "Could not initialize Amplify", e);
        }

        Log.i(TAG, "onCreate: called");

        setContentView(R.layout.activity_main);

        Button addTask = findViewById(R.id.addTask);
        addTask.setOnClickListener(getViewAddTask);

        Button allTask = findViewById(R.id.allTask);
        allTask.setOnClickListener(getViewAllTask);

        Button setting = findViewById(R.id.setting);
        setting.setOnClickListener(getViewSetting);

//        Button taskDetail1 = findViewById(R.id.taskDetails1);
//        taskDetail1.setOnClickListener(getViewTaskDetail1);
//
//        Button taskDetail2 = findViewById(R.id.taskDetails2);
//        taskDetail2.setOnClickListener(getViewTaskDetail2);
//
//        Button taskDetail3 = findViewById(R.id.taskDetails3);
//        taskDetail3.setOnClickListener(getViewTaskDetail3);



        RecyclerView recyclerView = findViewById(R.id.recycler_task);

//              Team team = Team.builder().name("team 3").build();
//
//        Amplify.API.mutate(ModelMutation.create(team),
//                success -> Log.i(TAG, "Saved Team to api : " + success.getData()),
//                error -> Log.e(TAG, "Could not save Team to API/dynamodb", error));

        handler = new Handler(Looper.getMainLooper(),
                new Handler.Callback() {
                    @Override
                    public boolean handleMessage(@NonNull Message message) {
                        listItemChanged();
                        return false;
                    }
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
            public void addTaskToTheList() {
//                taskList.add(new Task("task 4","this task 4 for testing ","new"));
                listItemChanged();
            }

            @Override
            public void onTaskClicked(int position) {
                Intent taskDetailsIntent = new Intent(getApplicationContext(), TaskDetailsActivity.class);
                taskDetailsIntent.putExtra(TASK_TITLE, taskList.get(position).getTitle());
                taskDetailsIntent.putExtra(TASK_BODY, taskList.get(position).getDescription());
                taskDetailsIntent.putExtra(TASK_STATUS, taskList.get(position).getStatus());
                startActivity(taskDetailsIntent);

            }

            @Override
            public void onDeleteTask(int position) {
//                Task item = Task.builder().title(taskList.get(position).getTitle()).build();

               Amplify.API.mutate(ModelMutation.delete(taskList.get(position)),
                       response -> Log.i(TAG, "item deleted from API:" ),
                       error -> Log.e(TAG, "Delete failed", error)
               );

//                Amplify.DataStore.delete(taskList.get(position),
//                        success -> Log.i(TAG, "item deleted from datastore: " + success.item().toString()),
//                        failure -> Log.e(TAG, "Could not query DataStore", failure));

                taskList.remove(position);
                listItemChanged();
                Toast.makeText(MainActivity.this, "Item deleted", Toast.LENGTH_SHORT).show();
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);


    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String username = preference.getString("userName", "user") + "'s Tasks";
        String teamName =preference.getString("teamName", "choose your team");
        TextView userNameText = (findViewById(R.id.usernameInHomePage));
        TextView teamText = (findViewById(R.id.Team));
        teamText.setText(teamName);
        userNameText.setText(username);
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

    public static void saveDataToAmplify(String title, String body, String status) {
        Task item = Task.builder().title(title).description(body).status(status).build();

        Amplify.DataStore.save(item,
                success -> Log.i(TAG, "Saved item to data store : " + success.item().toString()),
                error -> Log.e(TAG, "Could not save item to DataStore", error)
        );
        listItemChanged();
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

    private static void listItemChanged() {
        adapter.notifyDataSetChanged();
    }

    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager =
                ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager
                .getActiveNetworkInfo().isConnected();
    }

    public static void saveTaskToAPI(Task item) {
        Amplify.API.mutate(ModelMutation.create(item),
                success -> Log.i(TAG, "Saved item to api : " + success.getData()),
                error -> Log.e(TAG, "Could not save item to API/dynamodb", error));
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

    private final View.OnClickListener getViewSetting = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent settingIntent = new Intent(getBaseContext(), SettingActivity.class);
            startActivity(settingIntent);
        }
    };

    private final View.OnClickListener getViewAddTask = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent addTaskIntent = new Intent(getBaseContext(), AddTaskActivity.class);
            startActivity(addTaskIntent);
        }
    };

    private final View.OnClickListener getViewAllTask = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent allTaskIntent = new Intent(getBaseContext(), AllTasksActivity.class);
            startActivity(allTaskIntent);
        }
    };

    //    private final View.OnClickListener getViewTaskDetail1 = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            Button task1 = findViewById(R.id.taskDetails1);
//            String taskName = task1.getText().toString();
//            Intent taskDetailIntent = new Intent(getBaseContext(), TaskDetailsActivity.class);
//            taskDetailIntent.putExtra("taskName", taskName);
//            startActivity(taskDetailIntent);
//        }
//    };
//
//    private final View.OnClickListener getViewTaskDetail2 = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            Button task1 = findViewById(R.id.taskDetails2);
//            String taskName = task1.getText().toString();
//            Intent taskDetailIntent = new Intent(getBaseContext(), TaskDetailsActivity.class);
//            taskDetailIntent.putExtra("taskName", taskName);
//            startActivity(taskDetailIntent);
//        }
//    };
//
//    private final View.OnClickListener getViewTaskDetail3 = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            Button task1 = findViewById(R.id.taskDetails3);
//            String taskName = task1.getText().toString();
//            Intent taskDetailIntent = new Intent(getBaseContext(), TaskDetailsActivity.class);
//            taskDetailIntent.putExtra("taskName", taskName);
//            startActivity(taskDetailIntent);
//        }
//    };
}