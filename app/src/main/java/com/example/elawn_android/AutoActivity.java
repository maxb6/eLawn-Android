package com.example.elawn_android;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AutoActivity extends AppCompatActivity {

    private Button startButton;
    private Button offButton;
    private Button createPathButton;
    private Spinner pathSpinner;
    public int currentPath;
    private SharedPreferencesHelper spHelper;

    private static DatabaseReference statusReference;
    private DatabaseReference pathReference;
    private DatabaseReference userReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto);

        startButton = findViewById(R.id.startButton);
        createPathButton = findViewById(R.id.pathButton);
        pathSpinner = findViewById(R.id.pathSpinner);

        statusReference = FirebaseDatabase.getInstance().getReference("Control").child("Status");
        pathReference = FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Mower Paths");
        userReference = FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        createPathButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMapsActivity();
            }
        });

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                statusReference.setValue("Mowing");
                goToMainActivity();
            }
        });

        //path spinner settings
        //create a list of items for the spinner.
        ArrayList<String> spinnerOptions = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, spinnerOptions);
        //set the spinners adapter to the previously created one.
        pathSpinner.setAdapter(adapter);

        //Read paths in database and place path options in spinner
        pathReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                spinnerOptions.add("Path "+ snapshot.getKey());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //get path number from selected path spinner
        pathSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentPath = Integer.parseInt(pathSpinner.getSelectedItem().toString().substring(5));
                //Log.i("Current Path","Current Path: "+ currentPath);
                //set current path in firebase
                userReference.child("Current Path").setValue(currentPath);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                currentPath = 0;
            }
        });


        //read firebase to set proper spinner value
        //snapshot value must be subtracted since the array index begins with 0 then 1,.....
        userReference.child("Current Path").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pathSpinner.setSelection(Integer.parseInt(snapshot.getValue().toString())-1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void goToMapsActivity() {
        Intent intent = new Intent (this,MapsActivity.class);
        startActivity(intent);
    }

    private void goToMainActivity() {
        Intent intent = new Intent (this,MainActivity.class);
        startActivity(intent);
    }


}