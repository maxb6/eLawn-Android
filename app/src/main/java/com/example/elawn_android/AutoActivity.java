package com.example.elawn_android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class AutoActivity extends AppCompatActivity {

    private Button startButton;
    private Button offButton;
    private Button createPathButton;
    private Spinner pathSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto);

        createPathButton = findViewById(R.id.pathButton);
        pathSpinner = findViewById(R.id.pathSpinner);

        createPathButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMapsActivity();
            }
        });

        //path spinner settings
        //get the spinner from the xml.
        //create a list of items for the spinner.
        String[] items = new String[]{"Path 1", "Path 2", "Path 3"};
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        //set the spinners adapter to the previously created one.
        pathSpinner.setAdapter(adapter);


    }

    private void goToMapsActivity() {
        Intent intent = new Intent (this,MapsActivity.class);
        startActivity(intent);
    }


}