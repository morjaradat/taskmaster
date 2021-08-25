package com.example.taskmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.amplifyframework.auth.AuthUserAttributeKey;
import com.amplifyframework.auth.options.AuthSignUpOptions;
import com.amplifyframework.core.Amplify;
import com.google.firebase.analytics.FirebaseAnalytics;

public class SignupActivity extends AppCompatActivity {

    private FirebaseAnalytics mFirebaseAnalytics;

    private static final String TAG = "signup";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        EditText email_input = findViewById(R.id.signup_email);
        EditText username_input = findViewById(R.id.signup_username);
        EditText password_input = findViewById(R.id.signup_password);
        Button   signupSubmitButton = findViewById(R.id.signup_submit_button);
        Button   backToLoginButton = findViewById(R.id.login_back_button);

        signupSubmitButton.setOnClickListener(v -> {
            String email = email_input.getText().toString();
            String username = username_input.getText().toString();
            String password = password_input.getText().toString();
            if (!email.isEmpty() && !username.isEmpty() && !password.isEmpty()){
                signUp(username,email,password);
            }else {
                Toast.makeText(SignupActivity.this, "please fill the fields", Toast.LENGTH_SHORT).show();
            }
        });
        backToLoginButton.setOnClickListener(v -> {
            Intent goToLogin = new Intent(SignupActivity.this,LoginActivity.class);
            startActivity(goToLogin);
        });

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.SUCCESS,TAG);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

    }
     public void signUp(String username, String email, String password) {
        Amplify.Auth.signUp(
                username,
                password,
                AuthSignUpOptions.builder()
                        .userAttribute(AuthUserAttributeKey.email(), email)
                        .build(),
                success -> {
                    Log.i(TAG, "signUp successful: " + success.toString());
                    Intent goToVerification = new Intent(SignupActivity.this, VerificationActivity.class);
                    goToVerification.putExtra("username", username);
                    goToVerification.putExtra("password", password);
                    startActivity(goToVerification);
                },
                error -> {
                    Log.e(TAG, "signUp failed: " + error.toString());
                });
    }
}