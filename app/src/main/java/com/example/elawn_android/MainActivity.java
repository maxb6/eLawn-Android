package com.example.elawn_android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Button loginPageButton;
    private Button signOutButton;
    private Button mapsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginPageButton = findViewById(R.id.loginPage);
        signOutButton = findViewById(R.id.signOutButton);
        mapsButton = findViewById(R.id.mapsButton);
        mAuth = FirebaseAuth.getInstance();

        loginPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToLoginActivity();
            }
        });

        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                goToLoginActivity();
            }
        });

        mapsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMapsActivity();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser mFirebaseUser = mAuth.getCurrentUser();
        if(mFirebaseUser != null){
            //there is some user logged in
        }else{
            //no one is logged in
            goToLoginActivity();
        }
    }

    private void goToLoginActivity() {
        Intent intent = new Intent (this,LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void goToMapsActivity(){
        Intent intent = new Intent (this,MapsActivity.class);
        startActivity(intent);
    }

    private void openMapsFragment(){

    }

}