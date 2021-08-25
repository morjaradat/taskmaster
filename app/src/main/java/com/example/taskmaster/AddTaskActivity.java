package com.example.taskmaster;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.FileUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Task;
import com.amplifyframework.datastore.generated.model.Team;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;


public class AddTaskActivity extends AppCompatActivity {

    private FirebaseAnalytics mFirebaseAnalytics;


    static String pattern = "yyMMddHHmmssZ";
    @SuppressLint("SimpleDateFormat")
    static SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    private static final String TAG = "AddTask";
    private String spinner_task_status = null;
    private Team teamData = null;
    private static String FileUploadName= simpleDateFormat.format(new Date());
    private static String fileUploadExtention = null;
    private static File uploadFile = null;


    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addtask_activity);



        Objects.requireNonNull(getSupportActionBar()).setDefaultDisplayHomeAsUpEnabled(true);

        Log.i(TAG, "FileUploadName: => "+ FileUploadName);

        Button uploadFile = findViewById(R.id.upload_file);
        uploadFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFileFromDevice();


            }
        });

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
        newTaskCreateButton.setOnClickListener(v -> {
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
                    .fileName(FileUploadName +"."+ fileUploadExtention.split("/")[1])
                    .build();

           saveTaskToAPI(item);

//                MainActivity.saveDataToAmplify(taskTitle, taskBody, taskStatus);
            Toast.makeText(getApplicationContext(), "task was added", Toast.LENGTH_LONG).show();

            //room method
//                taskDao.insertOne(new Task(taskTitle,taskBody,taskStatus));
        });


        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.SUCCESS,TAG);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

    public static void saveTaskToAPI(Task item) {
        Amplify.Storage.uploadFile(
                FileUploadName +"."+ fileUploadExtention.split("/")[1],
                uploadFile,
                success -> {
                    Log.i(TAG, "uploadFileToS3: succeeded " + success.getKey());
                },
                error -> {
                    Log.e(TAG, "uploadFileToS3: failed " + error.toString());
                }
        );
        Amplify.API.mutate(ModelMutation.create(item),
                success -> Log.i(TAG, "Saved item to api : " + success.getData()),
                error -> Log.e(TAG, "Could not save item to API/dynamodb", error));
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 999 && resultCode == RESULT_OK) {
//            FileUploadNmae = data.getData()
            Uri uri = data.getData();
            fileUploadExtention = getContentResolver().getType(uri);

            Log.i(TAG, "onActivityResult: gg is " +fileUploadExtention);
            Log.i(TAG, "onActivityResult: returned from file explorer");
            Log.i(TAG, "onActivityResult: => " + data.getData());
            Log.i(TAG, "onActivityResult:  data => " + data.getType());

            uploadFile = new File(getApplicationContext().getFilesDir(), "uploadFile");

            try {
                InputStream inputStream = getContentResolver().openInputStream(data.getData());
                FileUtils.copy(inputStream, new FileOutputStream(uploadFile));
            } catch (Exception exception) {
                Log.e(TAG, "onActivityResult: file upload failed" + exception.toString());
            }

        }
    }

    private void getFileFromDevice() {
        Intent upload = new Intent(Intent.ACTION_GET_CONTENT);
        upload.setType("*/*");
        upload = Intent.createChooser(upload, "Choose a File");
        startActivityForResult(upload, 999); // deprecated
    }


    @SuppressLint("NonConstantResourceId")
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
