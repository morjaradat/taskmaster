package com.example.taskmaster;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class AllTasks extends AppCompatActivity {


    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alltasks_activity);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
    }

}
