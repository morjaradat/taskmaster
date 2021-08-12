package com.example.taskmaster;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.taskmaster.DB.Task;
import com.example.taskmaster.DB.TaskDao;
import com.example.taskmaster.DB.TaskDatabase;

public class AddTaskActivity extends AppCompatActivity {

    private static final String TAG = "AddTask";
    private String spinner_task_status=null;

    private TaskDatabase database;
    private TaskDao taskDao;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addtask_activity);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

        database = Room.databaseBuilder(getApplicationContext(), TaskDatabase.class, MainActivity.TASK_LIST)
                .allowMainThreadQueries().build();
        taskDao = database.taskDao();

        Spinner spinner = findViewById(R.id.spinner_status);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.planets_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinner_task_status = (String) parent.getItemAtPosition(position);
                System.out.println(spinner_task_status);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        Button newTaskCreateButton = findViewById(R.id.submit_Button_AddTask);
        newTaskCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String taskTitle = ((EditText) findViewById(R.id.task_title_input)).getText().toString();
                String taskBody = ((EditText) findViewById(R.id.task_body_input)).getText().toString();
                String taskStatus = spinner_task_status;

                taskDao.insertOne(new Task(taskTitle,taskBody,taskStatus));
            Toast.makeText(getApplicationContext(),"task was added",Toast.LENGTH_LONG).show();
            }
        });
    }

}
