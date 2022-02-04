package com.example.elawn_android;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";
    private EditText emailET;
    private EditText passwordET;
    private EditText nameET;
    private Button signUpButton;
    private Button loginReturnButton;
    private ProgressBar registerProgressBar;

    private FirebaseAuth mAuth;

    SharedPreferencesHelper spHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        nameET = findViewById(R.id.nameSignUpEditText);
        emailET = findViewById(R.id.emailSignUpEditText);
        passwordET = findViewById(R.id.passwordSignUpEditText);
        signUpButton = findViewById(R.id.signUpButton);
        loginReturnButton = findViewById(R.id.loginReturnButton);
        registerProgressBar = findViewById(R.id.signUpProgressBar);

        spHelper = new SharedPreferencesHelper(this);

        mAuth = FirebaseAuth.getInstance();

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        loginReturnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToLoginActivity();
            }
        });

    }

    private void registerUser() {

        //convert user inputs to strings
        String name = nameET.getText().toString().trim();
        String email = emailET.getText().toString().trim();
        String password = passwordET.getText().toString().trim();

        //check if fields are empty
        if(name.isEmpty()){
            nameET.setError("Name is required!");
            nameET.requestFocus();
            return;
        }

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

        //add user to firebase and check if task has been completed
        registerProgressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                           User user = new User(name,email);

                            //obtain id of newly registered user
                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(RegisterActivity.this,"User has been registered successfully",Toast.LENGTH_LONG).show();
                                        //goToLoginActivity();
                                    }else{
                                        Toast.makeText(RegisterActivity.this,"Failed to register! Try again. ",Toast.LENGTH_LONG).show();
                                    }
                                    spHelper.setPathNumber(null);
                                    registerProgressBar.setVisibility(View.GONE);
                                }
                            });
                        }
                        else{
                            try {
                                FirebaseAuthException e = (FirebaseAuthException)task.getException();
                                Toast.makeText(RegisterActivity.this, "Failed to register!", Toast.LENGTH_LONG).show();
                                registerProgressBar.setVisibility(View.GONE);
                            }
                            catch(Exception e){
                                Toast.makeText(RegisterActivity.this, "Connection Failure! ", Toast.LENGTH_LONG).show();
                                registerProgressBar.setVisibility(View.GONE);
                            }
                        }
                    }
                });
    }

    protected void goToLoginActivity(){
        Intent intent = new Intent (this,LoginActivity.class);
        startActivity(intent);
    }
}