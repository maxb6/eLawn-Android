package com.example.elawn_android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.example.elawn_android.MainFragments.HomeFragment;
import com.example.elawn_android.MainFragments.MowerFragment;
import com.example.elawn_android.MainFragments.SettingsFragment;
import com.example.elawn_android.Service.Coordinate;
import com.example.elawn_android.databinding.ActivityMain2Binding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Timer;
import java.util.TimerTask;


public class  MainActivity2 extends AppCompatActivity {

    private static final int LOCATION_REQUEST_CODE = 99;
    private DatabaseReference gpsReference;
    FusedLocationProviderClient fusedLocationProviderClient;

    ActivityMain2Binding binding;

    @SuppressLint("WrongThread")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        gpsReference = FirebaseDatabase.getInstance().getReference("GPS");

        binding = ActivityMain2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new HomeFragment());

        binding.bottomNavigationView.setOnNavigationItemSelectedListener(item -> {

            switch( item.getItemId()){
                case R.id.home:
                    replaceFragment(new HomeFragment());
                    break;
                case R.id.mower:
                    replaceFragment(new MowerFragment());
                    break;
                case R.id.settings:
                    replaceFragment(new SettingsFragment());
                    break;
            }
            return true;
        });

        Bundle args = new Bundle();
        HomeFragment homeFragment = new HomeFragment();

        Handler handler = new Handler();
        //Runnable runnable;
        int delay = 1000;

        handler.postDelayed(new Runnable() {
            public void run() {
                getUserLocation();          // this method will contain your almost-finished HTTP calls
                Log.i("USER LOCATION", " UPDATED ");
                handler.postDelayed(this, delay);
            }
        }, delay);

        /*
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                getUserLocation();
            }
        }, 0, 1000);//put here time 1000 milliseconds=1 second
        */
    }

    //this function will replace the frame layout with the aprticular fragment selected from the menu
    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment);
        fragmentTransaction.commit();
    }

    private void getUserLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        //check if user has permission set
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    Coordinate userLocation = new Coordinate(location.getLatitude(), location.getLongitude());
                    Log.i("LOCATION ", "SUCCESS");
                    gpsReference.child("CUL").setValue(userLocation.getLat()+"@"+userLocation.getLon());
                }
            });
        }

        //request permissions if user hasn't allowed yet
        else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
                Log.i("LOCATION ", "FAILED");

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getUserLocation();
            } else {

            }
        }
    }

    @Override
    public void onBackPressed() {
        return;
    }




}