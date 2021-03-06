package com.example.taskmaster;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.amplifyframework.core.Amplify;

public class VerificationActivity extends AppCompatActivity {



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

        verification_button.setOnClickListener(v -> {
            String verification_number = verification_code_input.getText().toString();
            verification(username,verification_number,password);
        });

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
                error -> Log.e(TAG, "verification: failed" + error.toString()));
    }

   public void silentSignIn(String username, String password) {
        Amplify.Auth.signIn(
                username,
                password,
                success -> Log.i(TAG, "signIn: worked " + success.toString()),
                error -> Log.e(TAG, "signIn: failed" + error.toString()));
    }
}