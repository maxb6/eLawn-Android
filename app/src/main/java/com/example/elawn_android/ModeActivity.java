package com.example.elawn_android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ModeActivity extends AppCompatActivity {

    private Button autoButton;
    private Button manualButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mode);

        autoButton = findViewById(R.id.autoButton);
        manualButton = findViewById(R.id.manualButton);

        manualButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToManualActivity();
            }
        });

        autoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToAutoActivity();
            }
        });
    }

    private void goToAutoActivity(){
        Intent intent = new Intent (this, AutoActivity.class);
        startActivity(intent);
    }

    private void goToManualActivity(){
        Intent intent = new Intent (this, ManualActivity.class);
        startActivity(intent);
    }


}