package com.example.taskmaster;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class AddTask extends AppCompatActivity {

    private static final String TAG = "AddTask";

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addtask_activity);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

        Button newTaskCreateButton = findViewById(R.id.submitButtonAddTask);
        newTaskCreateButton.setOnClickListener(newTaskCreateListener);
    }

    private final View.OnClickListener newTaskCreateListener = new View.OnClickListener() {
        public void onClick(View v) {
            String TaskName = ((EditText) findViewById(R.id.inputTaskTitle)).getText().toString();
            Intent taskDetailIntent = new Intent(getApplicationContext(),TaskDetails.class);
            taskDetailIntent.putExtra("TaskName",TaskName);
            startActivity(taskDetailIntent);
            Toast.makeText(getApplicationContext(),"task was added",Toast.LENGTH_LONG).show();

        }
    };
}
