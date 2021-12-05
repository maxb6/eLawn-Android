package com.example.elawn_android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";
    private FirebaseAuth mAuth;
    private Button loginPageButton;
    private Button signOutButton;
    private Button mapsButton;
    private MapView mMapView;
    private Button powerButton;

    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signOutButton = findViewById(R.id.signOutButton);
        mapsButton = findViewById(R.id.mapsButton);
        powerButton = findViewById(R.id.powerButton);


        mAuth = FirebaseAuth.getInstance();

        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                goToLoginActivity();
            }
        });

        mapsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMapsActivity();
            }
        });


        //mapView code

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }

        mMapView = (MapView) findViewById(R.id.mapView);
        mMapView.onCreate(mapViewBundle);
        mMapView.getMapAsync(this);


        //power button

        powerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToModeActivity();
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        mMapView.onStart();

        FirebaseUser mFirebaseUser = mAuth.getCurrentUser();
        if(mFirebaseUser != null){
            //there is some user logged in
        }else{
            //no one is logged in
            goToLoginActivity();
        }
    }

    private void goToLoginActivity() {
        Intent intent = new Intent (this,LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void goToMapsActivity(){
        Intent intent = new Intent (this,MapsActivity.class);
        startActivity(intent);
    }

    private void goToModeActivity(){
        Intent intent = new Intent (this,ModeActivity.class);
        startActivity(intent);
    }

    @Override
    public void onMapReady(GoogleMap mMap) {

        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        // Add a marker in Sydney and move the camera
        LatLng montreal = new LatLng(45.5, -73.56);
        mMap.addMarker(new MarkerOptions().position(montreal).title("Marker in Montreal"));

        LatLng centerField = new LatLng(45.496264, -73.823371);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(centerField, 17));
        LatLng m1 = new LatLng(45.496748, -73.823077);
        LatLng m2 = new LatLng(45.496315, -73.822631);
        LatLng m3 = new LatLng(45.495788, -73.823675);
        LatLng m4 = new LatLng(45.496220, -73.824112);

        PolygonOptions mowArea = new PolygonOptions()
                .add(m1)
                .add(m2)
                .add(m3)
                .add(m4)
                .fillColor(0x7F00FF00)
                .strokeColor(Color.RED);

        Polygon polygon = mMap.addPolygon(mowArea);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
        }

        mMapView.onSaveInstanceState(mapViewBundle);
    }
    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mMapView.onStop();
    }
    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }
    @Override
    protected void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }


}