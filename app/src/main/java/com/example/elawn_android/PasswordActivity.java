package com.example.elawn_android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class PasswordActivity extends AppCompatActivity {

    private EditText emailPasswordEditText;
    private Button resetPasswordButton;
    private Button cancelResetPasswordButton;
    private ProgressBar passwordProgressBar;

    private FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        emailPasswordEditText = findViewById(R.id.emailPasswordEditText);
        resetPasswordButton = findViewById(R.id.resetPasswordButton);
        cancelResetPasswordButton = findViewById(R.id.cancelResetPasswordButton);
        passwordProgressBar = findViewById(R.id.passwordProgressBar);

        mAuth = FirebaseAuth.getInstance();

        passwordProgressBar.setVisibility(View.GONE);

        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });

        cancelResetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){ goToLoginActivity(); }
        });

    }

    private void resetPassword() {
        String email = emailPasswordEditText.getText().toString().trim();

        //check if email field is empty
        if(email.isEmpty()){
            emailPasswordEditText.setError("Email is required!");
            emailPasswordEditText.requestFocus();
            return;
        }

        //check if email is valid
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailPasswordEditText.setError("Please provide valid email.");
            emailPasswordEditText.requestFocus();
            return;
        }

        passwordProgressBar.setVisibility(View.VISIBLE);
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(PasswordActivity.this,"Check your email to reset your password", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(PasswordActivity.this,"Error occured! Email not sent", Toast.LENGTH_LONG).show();
                }
                passwordProgressBar.setVisibility(View.GONE);
                goToLoginActivity();
            }
        });
    }

    protected void goToLoginActivity(){
        Intent intent = new Intent (this,LoginActivity.class);
        startActivity(intent);
    }
}