package com.example.taskmaster;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String TASK_TITLE = "taskTitle";
    public static final String TASK_BODY = "taskBody";
    public static final String TASK_STATUS = "taskStatus";
    private List<Task> taskList;
    private Adapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button addTask = findViewById(R.id.addTask);
        addTask.setOnClickListener(getViewAddTask);

        Button allTask = findViewById(R.id.allTask);
        allTask.setOnClickListener(getViewAllTask);

        Button taskDetail1 = findViewById(R.id.taskDetails1);
        taskDetail1.setOnClickListener(getViewTaskDetail1);

        Button taskDetail2 = findViewById(R.id.taskDetails2);
        taskDetail2.setOnClickListener(getViewTaskDetail2);

        Button taskDetail3 = findViewById(R.id.taskDetails3);
        taskDetail3.setOnClickListener(getViewTaskDetail3);

        Button setting = findViewById(R.id.setting);
        setting.setOnClickListener(getViewSetting);

        // recycle view setup
        RecyclerView recyclerView = findViewById(R.id.recycler_task);

        // add task to the taskList
        taskList = new ArrayList<>();
        taskList.add(new Task("task 1","this task 1 for testing ","new"));
        taskList.add(new Task("task 2","this task 2 for testing ","new"));
        taskList.add(new Task("task 3","this task 3 for testing ","new"));
        taskList.add(new Task("task 4","this task 4 for testing ","new"));
        taskList.add(new Task("task 5","this task 5 for testing ","new"));
        taskList.add(new Task("task 6","this task 6 for testing ","new"));

        // link list to the adapter
        adapter = new Adapter(taskList, new Adapter.onTaskClickedListener() {
            @Override
            public void addTaskToTheList() {
                taskList.add(new Task("task 4","this task 4 for testing ","new"));
                listItemChanged();

            }

            @Override
            public void onTaskClicked(int position) {
                Intent taskDetailsIntent = new Intent(getApplicationContext(),TaskDetailsActivity.class);
                taskDetailsIntent.putExtra(TASK_TITLE,taskList.get(position).getTitle());
                taskDetailsIntent.putExtra(TASK_BODY,taskList.get(position).getBody());
                taskDetailsIntent.putExtra(TASK_STATUS,taskList.get(position).getState());
                startActivity(taskDetailsIntent);

            }

            @Override
            public void onDeleteTask(int position) {

            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

    }

    private void listItemChanged() {
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String username = preference.getString("userName", "user") + "'s Tasks";
        TextView userNameText = (findViewById(R.id.usernameInHomePage));
        userNameText.setText(username);
    }

    private final View.OnClickListener getViewTaskDetail1 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Button task1 = findViewById(R.id.taskDetails1);
            String taskName = task1.getText().toString();
            Intent taskDetailIntent = new Intent(getBaseContext(), TaskDetailsActivity.class);
            taskDetailIntent.putExtra("taskName", taskName);
            startActivity(taskDetailIntent);
        }
    };

    private final View.OnClickListener getViewTaskDetail2 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Button task1 = findViewById(R.id.taskDetails2);
            String taskName = task1.getText().toString();
            Intent taskDetailIntent = new Intent(getBaseContext(), TaskDetailsActivity.class);
            taskDetailIntent.putExtra("taskName", taskName);
            startActivity(taskDetailIntent);
        }
    };

    private final View.OnClickListener getViewTaskDetail3 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Button task1 = findViewById(R.id.taskDetails3);
            String taskName = task1.getText().toString();
            Intent taskDetailIntent = new Intent(getBaseContext(), TaskDetailsActivity.class);
            taskDetailIntent.putExtra("taskName", taskName);
            startActivity(taskDetailIntent);
        }
    };

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