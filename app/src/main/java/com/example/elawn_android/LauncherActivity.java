package com.example.elawn_android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;

public class LauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        SharedPreferencesHelper spHelper = new SharedPreferencesHelper(this);
        if (spHelper.isUserLoggedIn())
        {
            new CountDownTimer(3000, 1000) {
                public void onTick(long millisUntilFinished) {

                }
                public void onFinish() {
                    goToMainActivity();
                }
            }.start();

        }
        else{
            goToLoginActivity();
        }


    }

    protected void goToMainActivity(){
        Intent intent = new Intent (this,MainActivity.class);
        startActivity(intent);
    }

    protected void goToLoginActivity(){
        Intent intent = new Intent (this,LoginActivity.class);
        startActivity(intent);
    }
}