package com.example.taskmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.amplifyframework.core.Amplify;
import com.bumptech.glide.Glide;

import java.io.File;
import java.io.OutputStream;
import java.net.URL;

public class TaskDetailsActivity extends AppCompatActivity {

    private static final String TAG = "TaskDetailsActivity";
    private URL url =null;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);

        Intent intent = getIntent();
        Log.i(TAG, "onCreate: file name " + intent.getExtras().get(MainActivity.TASK_FILE));
        String fileName = intent.getExtras().get(MainActivity.TASK_FILE).toString();

            getFileFromS3Storage(fileName);

        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String linkedText = String.format("<a href=\"%s\">download File</a> ", url);

        TextView test = findViewById(R.id.link);
        test.setText(Html.fromHtml(linkedText));
        test.setMovementMethod(LinkMovementMethod.getInstance());
        ImageView imageView = findViewById(R.id.testImg);
//

        handler = new Handler(Looper.getMainLooper(),
                message -> {
                    Glide.with(getBaseContext())
                            .load(url.toString())
                            .centerCrop()
                            .into(imageView);
                    return false;
                });


        TextView taskTitle = findViewById(R.id.singleTaskTitle);
        taskTitle.setText(intent.getExtras().get(MainActivity.TASK_TITLE).toString());

        TextView taskBody = findViewById(R.id.task_body);
        taskBody.setText(intent.getExtras().get(MainActivity.TASK_BODY).toString());

        TextView taskStatus = findViewById(R.id.task_status);
        taskStatus.setText(intent.getExtras().get(MainActivity.TASK_STATUS).toString());

    }
    private void getFileFromS3Storage(String key) {
        Amplify.Storage.downloadFile(
                key,
                new File(getApplicationContext().getFilesDir() + key),
                result -> {
                    Log.i(TAG, "Successfully downloaded: " + result.getFile().getAbsoluteFile());
                },
                error -> Log.e(TAG,  "Download Failure", error)
        );

        Amplify.Storage.getUrl(
                key,
                result -> {
                    Log.i(TAG, "Successfully generated: " + result.getUrl());
                    url= result.getUrl();
                    handler.sendEmptyMessage(1);
                },
                error -> Log.e(TAG, "URL generation failure", error)
        );
    }
}