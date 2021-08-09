package com.example.taskmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class TaskDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);
         Intent intent = getIntent();
        TextView taskTitle = findViewById(R.id.singleTaskTitle);
        if (intent.getExtras() != null) {
            taskTitle.setText(intent.getExtras().get("TaskName").toString());
        }

    }
}