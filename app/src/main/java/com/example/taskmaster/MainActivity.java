package com.example.taskmaster;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.amplifyframework.AmplifyException;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.AWSDataStorePlugin;
import com.amplifyframework.datastore.appsync.AppSyncClient;
import com.amplifyframework.datastore.generated.model.Task;
import com.ea.async.Async;
//import com.example.taskmaster.DB.TaskDao;
//import com.example.taskmaster.DB.TaskDatabase;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {



    public static final String TASK_TITLE = "taskTitle";
    public static final String TASK_BODY = "taskBody";
    public static final String TASK_STATUS = "taskStatus";
    public static final String TASK_LIST = "TaskList";
    private static List<Task> taskList;
    private static Adapter adapter;
//
//    private TaskDatabase database;
//    private TaskDao taskDao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            Amplify.addPlugin(new AWSDataStorePlugin());
            Amplify.configure(getApplicationContext());

            Log.i("Tutorial", "Initialized Amplify");
        } catch (AmplifyException e) {
            Log.e("Tutorial", "Could not initialize Amplify", e);
        }


        setContentView(R.layout.activity_main);

        Button addTask = findViewById(R.id.addTask);
        addTask.setOnClickListener(getViewAddTask);

        Button allTask = findViewById(R.id.allTask);
        allTask.setOnClickListener(getViewAllTask);

//        Button taskDetail1 = findViewById(R.id.taskDetails1);
//        taskDetail1.setOnClickListener(getViewTaskDetail1);
//
//        Button taskDetail2 = findViewById(R.id.taskDetails2);
//        taskDetail2.setOnClickListener(getViewTaskDetail2);
//
//        Button taskDetail3 = findViewById(R.id.taskDetails3);
//        taskDetail3.setOnClickListener(getViewTaskDetail3);

        Button setting = findViewById(R.id.setting);
        setting.setOnClickListener(getViewSetting);

        //Amplify AWS



        // Database initialization
//        database = Room.databaseBuilder(getApplicationContext(),TaskDatabase.class, TASK_LIST)
//                .allowMainThreadQueries().build();
//        taskDao = database.taskDao();

        // recycle view setup
        RecyclerView recyclerView = findViewById(R.id.recycler_task);

        // add task to the taskList
        // get all task from database


    taskList = getDataFromAmplify();

        // link list to the adapter
        adapter = new Adapter(taskList, new Adapter.onTaskClickedListener() {
            @Override
            public void addTaskToTheList() {
//                taskList.add(new Task("task 4","this task 4 for testing ","new"));
                listItemChanged();

            }

            @Override
            public void onTaskClicked(int position) {
                Intent taskDetailsIntent = new Intent(getApplicationContext(),TaskDetailsActivity.class);
                taskDetailsIntent.putExtra(TASK_TITLE,taskList.get(position).getTitle());
                taskDetailsIntent.putExtra(TASK_BODY,taskList.get(position).getDescription());
                taskDetailsIntent.putExtra(TASK_STATUS,taskList.get(position).getStatus());
                startActivity(taskDetailsIntent);

            }

            @Override
            public void onDeleteTask(int position) {

                Amplify.DataStore.delete(taskList.get(position),
                        success -> Log.i("Tutorial", "item deleted: " + success.item().toString()),
                        failure -> Log.e("Tutorial", "Could not query DataStore", failure));

                taskList.remove(position);
                listItemChanged();
                Toast.makeText(MainActivity.this, "Item deleted", Toast.LENGTH_SHORT).show();

            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);


    }

    public static void  saveDataToAmplify(String title,String body ,String status){
        Task item = Task.builder().title(title).description(body).status(status).build();

        Amplify.DataStore.save(item,
                success -> Log.i("Tutorial", "Saved item: " + success.item().toString()),
                error -> Log.e("Tutorial", "Could not save item to DataStore", error)
        );
        listItemChanged();
    }

    public synchronized static List<Task> getDataFromAmplify(){
        System.out.println("In get data");
        List<Task> list = new ArrayList<>();
        Amplify.DataStore.query(Task.class,todos ->{
            while (todos.hasNext()) {
                Task todo = todos.next();
                list.add(todo);
                Log.i("Tutorial", "==== Task ====");
                        Log.i("Tutorial", "Name: " + todo.getTitle());
                        Log.i("Tutorial", "status: " + todo.getStatus());
                        Log.i("Tutorial", "==== Task End ====");
            }
        },                failure -> Log.e("Tutorial", "Could not query DataStore", failure)

        );

        return list;
    }
    private static void listItemChanged() {
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String username = preference.getString("userName", "user") + "'s Tasks";
        TextView userNameText = (findViewById(R.id.usernameInHomePage));
        userNameText.setText(username);
        getDataFromAmplify();
    }

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

}