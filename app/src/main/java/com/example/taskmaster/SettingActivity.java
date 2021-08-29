package com.example.taskmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;

public class SettingActivity extends AppCompatActivity {

    private FirebaseAnalytics mFirebaseAnalytics;

    private static final String TAG = "SettingActivity";
    private String teamName= null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        Button updateUserName = findViewById(R.id.updateUserNameButton);
        updateUserName.setOnClickListener(v -> {

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            SharedPreferences.Editor editor = sharedPreferences.edit();
            String userName = ((EditText) findViewById(R.id.updateFormUserName)).getText().toString();
            editor.putString("userName", userName);
            editor.putString("teamName", teamName);
            editor.apply();

            Toast.makeText(SettingActivity.this, "UserName and Team Updated", Toast.LENGTH_SHORT).show();

//                Intent newIntent = new Intent(getApplicationContext(),MainActivity.class);
//                startActivity(newIntent);
        });
//        Bundle bundle = new Bundle();
//        bundle.putString(FirebaseAnalytics.Param.SUCCESS,TAG);
//        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

    @SuppressLint("NonConstantResourceId")
    public void ChooseTeam(View view){
        boolean checked = ((RadioButton) view).isChecked();
        switch (view.getId()){

            case R.id.chooseTeam1:
                if (checked)
                    Log.i(TAG, "onClickRadioButton: team 1");
                teamName= "team 1";

                break;
            case R.id.chooseTeam2:
                if (checked)
                    Log.i(TAG, "onClickRadioButton: team 2");
                teamName= "team 2";

                break;
            case R.id.chooseTeam3:
                if (checked)
                    Log.i(TAG, "onClickRadioButton: team 3");
                teamName= "team 3";

                break;
        }
    }
}