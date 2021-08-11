package com.example.taskmaster;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddTaskActivity extends AppCompatActivity {

    private static final String TAG = "AddTask";

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addtask_activity);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

        Spinner spinner = (Spinner) findViewById(R.id.spinner_status);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.planets_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

//        Button newTaskCreateButton = findViewById(R.id.submitButtonAddTask);
//        newTaskCreateButton.setOnClickListener(newTaskCreateListener);
    }

//    private final View.OnClickListener newTaskCreateListener = new View.OnClickListener() {
//        public void onClick(View v) {
//            String TaskName = ((EditText) findViewById(R.id.inputTaskTitle)).getText().toString();
//            Intent taskDetailIntent = new Intent(getApplicationContext(), TaskDetailsActivity.class);
//            taskDetailIntent.putExtra("taskName",TaskName);
//            startActivity(taskDetailIntent);
//            Toast.makeText(getApplicationContext(),"task was added",Toast.LENGTH_LONG).show();
//
//        }
//    };
}
