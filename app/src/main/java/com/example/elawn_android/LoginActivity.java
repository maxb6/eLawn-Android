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
import android.widget.TextView;
import android.widget.Toast;

import com.example.elawn_android.Service.SharedPreferencesHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;

public class LoginActivity extends AppCompatActivity {

    private EditText emailET;
    private EditText passwordET;
    private TextView forgotPasswordTV;
    private Button loginButton;
    private Button registerButton;
    private ProgressBar loginProgressBar;
    private FirebaseAuth mAuth;

    private SharedPreferencesHelper spHelper;

    /*//check if user is signed in
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            goToMainActivity();
        }
    }

     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailET = findViewById(R.id.emailEditText);
        passwordET = findViewById(R.id.passwordEditText);
        forgotPasswordTV = findViewById(R.id.forgotPasswordTextView);
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);
        loginProgressBar = findViewById(R.id.loginProgressBar);

        spHelper = new SharedPreferencesHelper(getApplicationContext());
        mAuth = FirebaseAuth.getInstance();

        loginProgressBar.setVisibility(View.GONE);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userLogin();
            }
        });

        forgotPasswordTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToPasswordActivity();
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToRegisterActivity();
            }
        });

    }

    protected void userLogin(){

        String email = emailET.getText().toString().trim();
        String password = passwordET.getText().toString().trim();

        //check if fields are empty
        if(email.isEmpty()){
            emailET.setError("Email is required!");
            emailET.requestFocus();
            return;
        }

        if(password.isEmpty()){
            passwordET.setError("Password is required!");
            passwordET.requestFocus();
            return;
        }

        //check if email is valid
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailET.setError("Please provide valid email.");
            emailET.requestFocus();
            return;
        }
        //check if password length is sufficient
        if(password.length() < 6){
            passwordET.setError("Password must be at least 6 characters!");
            passwordET.requestFocus();
            return;
        }

        loginProgressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){
                    //edit shared preferences to set activity_executed to true
                    spHelper.setUserLogIn(true);
                    //redirect to main activity
                    goToMainActivity();

                }
                else{
                    try{
                        FirebaseAuthException e = (FirebaseAuthException)task.getException();
                        Toast.makeText(LoginActivity.this,"Failed to login! Please check your credentials. ", Toast.LENGTH_LONG).show();
                    }
                    catch(Exception e){
                        Toast.makeText(LoginActivity.this,"Connection Failure! ", Toast.LENGTH_LONG).show();
                    }

                }
                loginProgressBar.setVisibility(View.GONE);
            }
        });
    }

    protected void goToMainActivity(){
        Intent intent = new Intent (this,MainActivity2.class);
        startActivity(intent);
    }

    protected void goToPasswordActivity(){
        Intent intent = new Intent (this,PasswordActivity.class);
        startActivity(intent);
    }

    protected void goToRegisterActivity(){
        Intent intent = new Intent (this,RegisterActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        return;
    }

}