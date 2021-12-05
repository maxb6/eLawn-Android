package com.example.elawn_android;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ManualActivity extends AppCompatActivity {

    private Button forwardButton;
    private Button backButton;

    private DatabaseReference userControlReference;
    private DatabaseReference generalControlReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual);

        forwardButton = findViewById(R.id.fwdButton);
        backButton = findViewById(R.id.backButton);

        forwardButton = findViewById(R.id.fwdButton);

        userControlReference = FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Control");

        generalControlReference = FirebaseDatabase.getInstance().getReference("Control").child("MowControl");

        //value is set to 0 by default
        generalControlReference.setValue("0");

        //on forward touch control value is set to 100
        forwardButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()){
                        //while button being held
                    case MotionEvent.ACTION_DOWN:
                        generalControlReference.setValue("100");
                        v.setBackgroundColor(getResources().getColor(R.color.blue));
                        return true;

                        //while button released
                    case MotionEvent.ACTION_UP:
                        generalControlReference.setValue("0");
                        v.setBackgroundColor(Color.LTGRAY);
                        return true;

                }
                return false;
            }
        });

        //on back touch control value is set to -100
        backButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()){
                    //while button being held
                    case MotionEvent.ACTION_DOWN:
                        generalControlReference.setValue("-100");
                        v.setBackgroundColor(getResources().getColor(R.color.blue));
                        return true;

                    //while button released
                    case MotionEvent.ACTION_UP:
                        generalControlReference.setValue("0");
                        v.setBackgroundColor(Color.LTGRAY);
                        return true;

                }
                return false;
            }
        });



    }
}