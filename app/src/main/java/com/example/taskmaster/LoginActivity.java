package com.example.taskmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.amplifyframework.core.Amplify;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText login_email_input = findViewById(R.id.login_email);
        EditText login_password_input = findViewById(R.id.login_password);
        Button login_submit_button = findViewById(R.id.login_button);
        Button login_to_signup_button = findViewById(R.id.signup_button);

        login_submit_button.setOnClickListener(v -> {
            String email = login_email_input.getText().toString();
            String password = login_password_input.getText().toString();
            if (!email.isEmpty() && !password.isEmpty()){
                signIn(email,password);
            }else {
                Toast.makeText(LoginActivity.this, "please fill the fields", Toast.LENGTH_SHORT).show();
            }
        });

        login_to_signup_button.setOnClickListener(v -> {
            Intent goToSignUp = new Intent(LoginActivity.this,SignupActivity.class);
            startActivity(goToSignUp);
        });

    }

    public void signIn(String username, String password) {
        Amplify.Auth.signIn(
                username,
                password,
                success -> {
                    Log.i(TAG, "signIn: worked " + success.toString());
                    Intent goToMain = new Intent(LoginActivity.this, MainActivity.class);
                    goToMain.putExtra("username",username);
                    startActivity(goToMain);
                },
                error -> Log.e(TAG, "signIn: failed" + error.toString()));
    }

}