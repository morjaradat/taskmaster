package com.example.taskmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button addTask = findViewById(R.id.addTask);
        addTask.setOnClickListener(getViewAddTask);

        Button allTask = findViewById(R.id.allTask);
        allTask.setOnClickListener(getViewAllTask);
    }

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