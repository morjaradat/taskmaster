package com.example.taskmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button addTask = findViewById(R.id.addTask);
        addTask.setOnClickListener(getViewAddTask);

        Button allTask = findViewById(R.id.allTask);
        allTask.setOnClickListener(getViewAllTask);

        Button taskDetail = findViewById(R.id.taskDetails);
        taskDetail.setOnClickListener(getViewTaskDetail);

        Button setting = findViewById(R.id.setting);
        setting.setOnClickListener(getViewSetting);


    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String username = preference.getString("userName", "user") + "'s Tasks";
        TextView userNameText = (findViewById(R.id.usernameInHomePage));
        userNameText.setText(username);
    }

    private final View.OnClickListener getViewTaskDetail= new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Intent taskDetailIntent = new Intent(getBaseContext(),TaskDetails.class);
            startActivity(taskDetailIntent);
        }
    };

    private final View.OnClickListener getViewSetting= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent settingIntent = new Intent(getBaseContext(),Setting.class);
            startActivity(settingIntent);
        }
    };

    private final View.OnClickListener getViewAddTask = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent addTaskIntent = new Intent(getBaseContext(),AddTask.class);
            startActivity(addTaskIntent);
        }
    };

    private final View.OnClickListener getViewAllTask = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent allTaskIntent = new Intent(getBaseContext(),AllTasks.class);
            startActivity(allTaskIntent);
        }
    };

}