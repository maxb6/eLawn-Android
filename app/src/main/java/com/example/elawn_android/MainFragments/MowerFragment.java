package com.example.elawn_android.MainFragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.elawn_android.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class MowerFragment extends Fragment {

    private View view;

    private TextView batteryTV;
    private TextView compassTV;
    private TextView currentTV;
    private TextView tempTV;
    private DatabaseReference ATMegaReference;
    private float mowerRotation;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ViewGroup root = (ViewGroup)inflater.inflate(R.layout.fragment_home, container, false);
        view =  inflater.inflate(R.layout.fragment_home, container, false);


        compassTV = root.findViewById(R.id.compassTV);
        batteryTV = root.findViewById(R.id.batteryTV);
        currentTV = root.findViewById(R.id.currentTV);
        tempTV = root.findViewById(R.id.tempTV);


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mower, container, false);
    }

    private void setMowerInfo() {

        //Set textview values to firebase values
        //Read the ATMega nodes in the firebase and set the values

        ATMegaReference.child("BATTERY").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                batteryTV.setText(snapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ATMegaReference.child("COMPASS").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                compassTV.setText(snapshot.getValue().toString());

                mowerRotation = Float.parseFloat(snapshot.getValue().toString());
                //rotate mow marker on compass data change

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ATMegaReference.child("CURRENT").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentTV.setText(snapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ATMegaReference.child("TEMPERATURE").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tempTV.setText(snapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}