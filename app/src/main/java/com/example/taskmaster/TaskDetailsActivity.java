package com.example.taskmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class TaskDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);

        Intent intent = getIntent();

        TextView taskTitle = findViewById(R.id.singleTaskTitle);
        taskTitle.setText(intent.getExtras().get(MainActivity.TASK_TITLE).toString());

        TextView taskBody = findViewById(R.id.task_body);
        taskBody.setText(intent.getExtras().get(MainActivity.TASK_BODY).toString());

        TextView taskStatus = findViewById(R.id.task_status);
        taskStatus.setText(intent.getExtras().get(MainActivity.TASK_STATUS).toString());
//        if (intent.getExtras() != null) {
//            taskTitle.setText(intent.getExtras().get("taskName").toString());
//        }

    }
}