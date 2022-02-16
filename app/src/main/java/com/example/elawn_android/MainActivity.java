package com.example.elawn_android;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private MarkerOptions mowMarkerOptions = new MarkerOptions();
    private Marker mowMarker;
    private MarkerOptions chargingMarkerOptions = new MarkerOptions();
    private Marker chargingMarker;
    private static final String TAG = "MainActivity";
    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";
    private FirebaseAuth mAuth;
    private Button loginPageButton;
    private Button signOutButton;
    private Button mapsButton;
    private MapView mMapView;
    private Button powerButton;
    private Button chargeButton;
    private TextView statusTV;
    private String currentStatus;
    protected int currentPath;
    private SharedPreferencesHelper spHelper;
    private Spinner mainSpinner;
    ArrayList<Coordinate> pathAlgo = new ArrayList<Coordinate>();

    private HashMap<String, Coordinate> vertexCoordinates = new HashMap<String, Coordinate>();
    //protected ArrayList<Coordinate> vertexCoordinates = new ArrayList<Coordinate>();
    protected ArrayList<Coordinate> vCoordinates = new ArrayList<Coordinate>();
    private int childCount;
    private int coordCount = 0;

    private LatLng m1;
    private LatLng m2;
    private LatLng m3;
    private LatLng m4;

    private Coordinate v1 = new Coordinate();
    private Coordinate v2 = new Coordinate();
    private Coordinate v3 = new Coordinate();
    private Coordinate v4 = new Coordinate();


    private static DatabaseReference statusReference;
    private DatabaseReference userReference;
    private DatabaseReference gpsReference;
    private DatabaseReference pathReference;


    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spHelper = new SharedPreferencesHelper(this);

        signOutButton = findViewById(R.id.signOutButton);
        mapsButton = findViewById(R.id.mapsButton);
        powerButton = findViewById(R.id.powerButton);
        chargeButton = findViewById(R.id.chargeButton);
        statusTV = findViewById(R.id.statusTV);
        mainSpinner = findViewById(R.id.mainSpinner);

        //get current path from firebase
        userReference = FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        userReference.child("Current Path").setValue(0);

        gpsReference = FirebaseDatabase.getInstance().getReference("GPS");

        pathReference = FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Mower Paths");

        mAuth = FirebaseAuth.getInstance();

        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                spHelper.setUserLogIn(false);
                goToLoginActivity();
            }
        });

        mapsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMapsActivity();
            }
        });

        //spinner code --------------------------------------------------------------------------
        ArrayList<String> spinnerOptions = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, spinnerOptions);
        //set the spinners adapter to the previously created one.
        mainSpinner.setAdapter(adapter);

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
        mainSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentPath = Integer.parseInt(mainSpinner.getSelectedItem().toString().substring(5));
                //Log.i("Current Path","Current Path: "+ currentPath);
                //set current path in firebase
                userReference.child("Current Path").setValue(currentPath);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                currentPath = 0;
            }
        });


        //mapView code ---------------------------------------------------------------------------

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }

        mMapView = (MapView) findViewById(R.id.mapView);
        mMapView.onCreate(mapViewBundle);
        mMapView.getMapAsync(this);

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
                        powerButton.setText("TURN ON");
                        powerButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //simulateMowerMovement();
                                goToModeActivity();
                            }
                        });

                        chargeButton.setText("CHARGE");
                        chargeButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                statusReference.setValue("Charging");
                            }
                        });
                    }

                    else if(currentStatus.equals("Mowing")){
                        statusTV.setTextColor(Color.GREEN);
                        powerButton.setText("TURN OFF");
                        powerButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                statusReference.setValue("Off");
                            }
                        });
                        chargeButton.setText("CHARGE");
                        chargeButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                statusReference.setValue("Charging");
                            }
                        });
                    }

                    else if(currentStatus.equals("Charging")){
                        powerButton.setText("TURN ON");
                        statusTV.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.chargeOrange));
                        powerButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                goToModeActivity();
                            }
                        });

                        chargeButton.setText("END CHARGE");
                        chargeButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                statusReference.setValue("Off");
                            }
                        });
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

        countMowerPaths();

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
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        // everytime a new path is selected:
        // read firebase for current path and retrieve coordinates of that path, clear the map, and reset child count

        userReference.child("Current Path").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentPath = Integer.parseInt(snapshot.getValue().toString());
                Log.i(TAG, "Current Path String: " + currentPath);
                if (currentPath != 0) {
                    childCount = 1;
                    mMap.clear();
                    mowMarker = null;
                    chargingMarker = null;
                    setGPSPathCoordinates(currentPath);
                    mowerMarker();
                    chargingMarker();
                    //Log.i(TAG, "Vertex Coordinates Array: Lat:" + vertexCoordinates.get(1).getLat()
                    //  + "     Lon:" + vertexCoordinates.get(1).getLon());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void setGPSPathCoordinates(int path) {

        //read the current path coordinates from the user node and copy it to the gps node

        userReference.child("Mower Paths").child(String.valueOf(path))
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                        Coordinate coordinate = snapshot.getValue(Coordinate.class);
                        //gpsReference.child("Perimeter").child(snapshot.getKey()).setValue(coordinate);

                        switch(childCount) {
                            case 1:

                                gpsReference.child("Current Charger Location").setValue(coordinate);

                            case 2:
                                m1 = new LatLng(coordinate.getLat(),coordinate.getLon());
                                v1.setLat(coordinate.getLat());
                                v1.setLon(coordinate.getLon());
                                gpsReference.child("Perimeter").child("V1").setValue(coordinate);

                            case 3:
                                m2 = new LatLng(coordinate.getLat(),coordinate.getLon());
                                v2.setLat(coordinate.getLat());
                                v2.setLon(coordinate.getLon());
                                gpsReference.child("Perimeter").child("V2").setValue(coordinate);

                            case 4:
                                m3 = new LatLng(coordinate.getLat(),coordinate.getLon());
                                v3.setLat(coordinate.getLat());
                                v3.setLon(coordinate.getLon());
                                gpsReference.child("Perimeter").child("V3").setValue(coordinate);


                            case 5:
                                m4 = new LatLng(coordinate.getLat(),coordinate.getLon());
                                v4.setLat(coordinate.getLat());
                                v4.setLon(coordinate.getLon());
                                gpsReference.child("Perimeter").child("V4").setValue(coordinate);

                        }

                        if (childCount > 4) {
                            PolygonOptions mowArea = new PolygonOptions()
                                    .add(m1)
                                    .add(m2)
                                    .add(m3)
                                    .add(m4)
                                    .fillColor(0x7F00FF00)
                                    .strokeColor(Color.RED);

                            Polygon polygon = mMap.addPolygon(mowArea);

                            //use path finding algorithm to find coordinates for all polyline paths

                            PathFinding p = new PathFinding(v1,v2,v3,v4);

                            pathAlgo = p.pathAlgorithm();

                            //create a map with the coordinate names as the key and the LatLng objects as the values
                            //this map will hold all coordinates generated from the algorithm

                            HashMap<String, LatLng> pathCoordinates = new HashMap<String, LatLng>();
                            PolylineOptions mowPath = new PolylineOptions();

                            //for each different path chosen, clear the firebase node, reset the coord count,
                            //write the new path coordinates to the firebase and move the camera to the current path
                            coordCount = 0;
                            gpsReference.child("Path Coordinates").setValue("0");
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(m1,19));

                            //iterate through the arraylist of coordinates and fill the map with the coordinates
                            //map example (m1,lat-lng)

                            for (Coordinate s : pathAlgo) {
                                ++coordCount;
                                Log.i("PATH-FINDING","Path finding algorithm : LAT:  " + s.getLat() + "--------LON: "+ s.getLon());
                                //add each coordinate point to the polyline path
                                mowPath.add(new LatLng(s.getLat(),s.getLon())).width(4);
                                gpsReference.child("Path Coordinates").child(String.valueOf(coordCount)).setValue(s);

                            }

                            Log.i(TAG,"created path" +pathCoordinates);

                            //insert the created polyline onto the map
                            Polyline polyline = mMap.addPolyline(mowPath);
                        }

                        //vertexCoordinates.put(String.valueOf(childCount),coordinate);
                        vCoordinates.add(coordinate);
                        childCount++;
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

    }

    private void countMowerPaths(){
        //Count the number of mower paths and update the firebase number of paths node

        userReference.child("Mower Paths").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String pathNumber = String.valueOf(snapshot.getChildrenCount());
                userReference.child("Number of Paths").setValue(pathNumber);
                //set path number to amount of mower paths
                spHelper.setPathNumber(pathNumber);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void mowerMarker(){

        gpsReference.child("Current Mower Location").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                //obtain coordinate from firebase as latlng value
                Coordinate mowerCoordinate = snapshot.getValue(Coordinate.class);

                LatLng mowerLocation = new LatLng(mowerCoordinate.getLat(), mowerCoordinate.getLon());

                //make mower icon marker smaller in size
                BitmapDrawable bitmapDraw = (BitmapDrawable)getResources().getDrawable(R.drawable.mower_icon);
                Bitmap b = bitmapDraw.getBitmap();
                Bitmap mowerIcon = Bitmap.createScaledBitmap(b, 100, 100, false);
                mowMarkerOptions.icon(BitmapDescriptorFactory.fromBitmap(mowerIcon));

                //if there is no marker, add the marker to the app
                if (mowMarker == null){
                    mowMarkerOptions.position(mowerLocation);
                    mowMarker = mMap.addMarker(mowMarkerOptions);
                    //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mowerLocation,17));
                }

                //once there is a marker
                //set updated location, everytime the current mower location changes
                mowMarker.setPosition(mowerLocation);
                //move camera on update
                //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mowerLocation,17));


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void chargingMarker(){

        gpsReference.child("Current Charger Location").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                //obtain coordinate from firebase as latlng value
                Coordinate chargerCoordinate = snapshot.getValue(Coordinate.class);

                LatLng chargerLocation = new LatLng(chargerCoordinate.getLat(), chargerCoordinate.getLon());

                //make mower icon marker smaller in size
                BitmapDrawable bitmapDraw = (BitmapDrawable) getResources().getDrawable(R.drawable.charging_icon_blue);
                Bitmap b = bitmapDraw.getBitmap();
                Bitmap chargingIcon = Bitmap.createScaledBitmap(b, 100, 100, false);
                chargingMarkerOptions.icon(BitmapDescriptorFactory.fromBitmap(chargingIcon));

                //if there is no marker, add the marker to the app
                if (chargingMarker == null) {
                    chargingMarkerOptions.position(chargerLocation);
                    chargingMarker = mMap.addMarker(chargingMarkerOptions);
                }

                //once there is a marker
                //set updated location, everytime the current charger location changes
                chargingMarker.setPosition(chargerLocation);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setCurrentPath(String currentPath){

        userReference.child("Number of Paths").setValue(currentPath);
        spHelper.setPathNumber(currentPath);

    }

    private void simulateMowerMovement() {


        for (Coordinate coordinate : pathAlgo) {
            new CountDownTimer(1000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    gpsReference.child("Current Mower Location").setValue(coordinate);
                }

                @Override
                public void onFinish() {
                }
            }.start();
        }




        for (Coordinate coordinate : pathAlgo) {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Do something after amount of seconds below (each 1000 = 1s)
                    // for (Coordinate coordinate : pathAlgo) {
                    gpsReference.child("Current Mower Location").setValue(coordinate);
                    // }
                }
            }, 1 * 1000);
        }
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