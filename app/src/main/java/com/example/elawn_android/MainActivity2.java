package com.example.elawn_android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.example.elawn_android.MainFragments.HomeFragment;
import com.example.elawn_android.MainFragments.MowerFragment;
import com.example.elawn_android.MainFragments.SettingsFragment;
import com.example.elawn_android.Service.Coordinate;
import com.example.elawn_android.Service.SharedPreferencesHelper;
import com.example.elawn_android.databinding.ActivityMain2Binding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Timer;
import java.util.TimerTask;


public class  MainActivity2 extends AppCompatActivity {

    private static final int LOCATION_REQUEST_CODE = 99;
    private DatabaseReference gpsReference;
    private DatabaseReference batteryReference;
    private SharedPreferencesHelper spHelper;
    FusedLocationProviderClient fusedLocationProviderClient;

    ActivityMain2Binding binding;

    @SuppressLint("WrongThread")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        gpsReference = FirebaseDatabase.getInstance().getReference("GPS");
        batteryReference = FirebaseDatabase.getInstance().getReference("ATMega").child("BATTERY");

        spHelper = new SharedPreferencesHelper(this);

        binding = ActivityMain2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new HomeFragment());

        batteryReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String batteryLevel = snapshot.getValue().toString();
                double batteryDouble = Double.parseDouble(batteryLevel);
                int batteryInt = (int) (((batteryDouble - 22) * (100 - 0)) / (28 - 22));
                if(batteryDouble<22){
                    batteryInt = 0;
                }
                //send notification when battery less then 20
                if (batteryInt < 20){
                    Log.i("NOTIFY","NOTIFICATION PUSHED");
                    if(spHelper.getNotification()){
                        pushNotification();
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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
                    gpsReference.child("CurrentMowerCoordinate").setValue(userLocation);
                    gpsReference.child("CUL").setValue(userLocation.getLat()+"@"+userLocation.getLon());
                    Log.i("LOCATION ", "SUCCESS");
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

    private void pushNotification(){

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel("channel_1","My notification", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity2.this,"channel_1");
        builder.setContentTitle("E-LAWN")
                .setContentText("Battery low, please charge now")
                .setSmallIcon(R.mipmap.e_lawn_icon);

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(MainActivity2.this);
        managerCompat.notify(1,builder.build());

    }





}