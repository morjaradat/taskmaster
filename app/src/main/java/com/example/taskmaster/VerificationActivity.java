package com.example.taskmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.amplifyframework.core.Amplify;
import com.google.firebase.analytics.FirebaseAnalytics;

public class VerificationActivity extends AppCompatActivity {

    private FirebaseAnalytics mFirebaseAnalytics;


    private static final String TAG = "verification";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        EditText verification_code_input= findViewById(R.id.verification_code);
        Button verification_button = findViewById(R.id.verification_submitButton);

        Intent intent = getIntent();
        String username = intent.getExtras().getString("username", "");
        String password = intent.getExtras().getString("password", "");

        verification_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String verification_number = verification_code_input.getText().toString();
                verification(username,verification_number,password);
            }
        });

//        Bundle bundle = new Bundle();
//        bundle.putString(FirebaseAnalytics.Param.SUCCESS,TAG);
//        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

    }
   public void verification(String username, String confirmationNumber,String password) {
        Amplify.Auth.confirmSignUp(
                username,
                confirmationNumber,
                success -> {
                    Log.i(TAG, "verification: succeeded" + success.toString());
                    Intent goToSignIn = new Intent(VerificationActivity.this, MainActivity.class);
                    goToSignIn.putExtra("username", username);
                    startActivity(goToSignIn);

                    silentSignIn(username, password);
                },
                error -> {
                    Log.e(TAG, "verification: failed" + error.toString());
                });
    }

   public void silentSignIn(String username, String password) {
        Amplify.Auth.signIn(
                username,
                password,
                success -> {
                    Log.i(TAG, "signIn: worked " + success.toString());
                },
                error -> Log.e(TAG, "signIn: failed" + error.toString()));
    }
}