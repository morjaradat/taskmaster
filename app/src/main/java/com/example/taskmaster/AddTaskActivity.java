package com.example.taskmaster;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Task;
import com.amplifyframework.datastore.generated.model.Team;


public class AddTaskActivity extends AppCompatActivity {

    private static final String TAG = "AddTask";
    private String spinner_task_status = null;
    private Team teamData = null;



    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addtask_activity);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);



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
                spinner_task_status = (String) parent.getItemAtPosition(0);

            }
        });
        Button newTaskCreateButton = findViewById(R.id.submit_Button_AddTask);
        newTaskCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String taskTitle = ((EditText) findViewById(R.id.task_title_input)).getText().toString();
                String taskBody = ((EditText) findViewById(R.id.task_body_input)).getText().toString();
                String taskStatus = spinner_task_status;

//                Team team = Team.builder().name(teamName).build();

                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Task item = Task.builder()
                        .title(taskTitle)
                        .description(taskBody)
                        .status(taskStatus)
                        .team(teamData)
                        .build();

                MainActivity.saveTaskToAPI(item);

//                MainActivity.saveDataToAmplify(taskTitle, taskBody, taskStatus);
                Toast.makeText(getApplicationContext(), "task was added", Toast.LENGTH_LONG).show();

                //room method
//                taskDao.insertOne(new Task(taskTitle,taskBody,taskStatus));
            }
        });
    }

    public void onClickRadioButton(View view){
        boolean checked = ((RadioButton) view).isChecked();
        switch (view.getId()){

            case R.id.team1:
                if (checked)
                    Log.i(TAG, "onClickRadioButton: team 1");
                getTeamDetailFromAPIByName("team 1");
                    break;
            case R.id.team2:
                if (checked)
                    Log.i(TAG, "onClickRadioButton: team 2");
                getTeamDetailFromAPIByName("team 2");

                break;
            case R.id.team3:
                if (checked)
                    Log.i(TAG, "onClickRadioButton: team 3");
                getTeamDetailFromAPIByName("team 3");

                break;
        }
    }

    public void getTeamDetailFromAPIByName(String name) {
        Amplify.API.query(
                ModelQuery.list(Team.class, Team.NAME.contains(name)),
                response -> {
                    for (Team teamDetail : response.getData()) {
                        Log.i(TAG, "the team name is =>"+teamDetail.getName());
                        teamData=teamDetail;
                    }
                },
                error -> Log.e(TAG, "Query failure", error)
        );
    }

}
