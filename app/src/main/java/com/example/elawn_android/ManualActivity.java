package com.example.elawn_android;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

public class ManualActivity extends AppCompatActivity {

    private Button startButton;
    private Button offButton;
    private Button createPathButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual);

    }
}