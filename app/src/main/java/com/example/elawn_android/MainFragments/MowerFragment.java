package com.example.elawn_android.MainFragments;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.elawn_android.R;
import com.example.elawn_android.Service.Coordinate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import eo.view.batterymeter.BatteryMeter;

public class MowerFragment extends Fragment {

    private static final String TAG = "MOWER";
    private View view;

    private TextView batteryTV;
    private TextView compassTV;
    private TextView currentTV;
    private TextView tempTV;
    private TextView locationTV;
    private DatabaseReference ATMegaReference;
    private DatabaseReference GPSReference;
    private DatabaseReference statusReference;
    private float mowerRotation;
    private BatteryMeter batteryMeter;
    private String batteryLevel ;
    private Geocoder geocoder;
    private TextView location;
    private TextView current;
    private TextView status;
    private TextView statusTV;
    private String currentStatus;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ViewGroup root = (ViewGroup)inflater.inflate(R.layout.fragment_mower, container, false);
        view =  inflater.inflate(R.layout.fragment_mower, container, false);


        compassTV = root.findViewById(R.id.compassTV);
        batteryTV = root.findViewById(R.id.batteryTV);
        currentTV = root.findViewById(R.id.currentTV);
        tempTV = root.findViewById(R.id.tempTV);
        locationTV = root.findViewById(R.id.locationTV);
        statusTV = root.findViewById(R.id.statusTV);
        batteryMeter = root.findViewById(R.id.batteryMeter);
        location = root.findViewById(R.id.location);
        current = root.findViewById(R.id.current);
        status = root.findViewById(R.id.status);
        geocoder = new Geocoder(getActivity(), Locale.getDefault());

        location.bringToFront();
        locationTV.bringToFront();
        current.bringToFront();
        currentTV.bringToFront();
        status.bringToFront();
        statusTV.bringToFront();

        ATMegaReference = FirebaseDatabase.getInstance().getReference("ATMega");
        GPSReference = FirebaseDatabase.getInstance().getReference("GPS");
        statusReference = FirebaseDatabase.getInstance().getReference("Control").child("Status");

        setMowerInfo();
        getPathStatus();


        // Inflate the layout for this fragment
        return root;
    }

    private void setMowerInfo() {

        //Set textview values to firebase values
        //Read the ATMega nodes in the firebase and set the values
        //get current address

        GPSReference.child("Current Mower Location").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Coordinate mowerCoordinate = snapshot.getValue(Coordinate.class);

                List<Address> addresses;
                try {
                    addresses = geocoder.getFromLocation(mowerCoordinate.getLat(), mowerCoordinate.getLon(),1);
                    String address = addresses.get(0).getAddressLine(0);
                    Log.i("GEO LOCATION SUCCESS",address);
                    locationTV.setText(address);

                } catch (IOException e) {
                    e.printStackTrace();
                    Log.i("GEO LOCATION FAILED","");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        ATMegaReference.child("BATTERY").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                batteryLevel = snapshot.getValue().toString();
                batteryTV.setText(batteryLevel+"%");
                Log.i(TAG,snapshot.getValue().toString());
                batteryMeter.setChargeLevel(Integer.parseInt(batteryLevel));
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

    private void getPathStatus(){
        //Change status text view and power button dependent on mode
        // Modes: Off, Charging and Mowing
        // status text view: read firebase status and display in the textview
        //Power button: allow user to turn on, or off the device

        statusReference = FirebaseDatabase.getInstance().getReference("Control").child("Status");

        statusReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()) {
                    statusTV.setText("No Reading");
                }
                else {
                    currentStatus = snapshot.getValue().toString().trim();
                    statusTV.setText(currentStatus);

                    if(currentStatus.equals("Off")){
                        statusTV.setTextColor(Color.RED);


                    }

                    else if(currentStatus.equals("Mowing")){
                        statusTV.setTextColor(Color.BLUE);

                    }

                    else if(currentStatus.equals("Charging")){
                        statusTV.setTextColor(Color.GREEN);

                    }

                    else{
                        statusTV.setTextColor(Color.BLACK);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}