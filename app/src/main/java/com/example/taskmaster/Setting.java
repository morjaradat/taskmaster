package com.example.taskmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Setting extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        Button updateUserName = findViewById(R.id.updateUserNameButton);
        updateUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                SharedPreferences.Editor editor = sharedPreferences.edit();
                String userName = ((EditText) findViewById(R.id.updateFormUserName)).getText().toString();
                editor.putString("userName",userName);
                editor.apply();

                Toast.makeText(Setting.this, "UserName Updated", Toast.LENGTH_SHORT).show();

//                Intent newIntent = new Intent(getApplicationContext(),MainActivity.class);
//                startActivity(newIntent);
            }
        });
    }
}