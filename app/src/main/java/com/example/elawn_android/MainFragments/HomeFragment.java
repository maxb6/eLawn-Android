package com.example.elawn_android.MainFragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.elawn_android.Database.DatabaseHelper;
import com.example.elawn_android.LoginActivity;
import com.example.elawn_android.MainActivity2;
import com.example.elawn_android.ManualActivity;
import com.example.elawn_android.MapsActivity;
import com.example.elawn_android.MyService;
import com.example.elawn_android.R;
import com.example.elawn_android.Service.Coordinate;
import com.example.elawn_android.Service.PathFinding;
import com.example.elawn_android.Service.SharedPreferencesHelper;
import com.example.elawn_android.Service.User;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import eo.view.batterymeter.BatteryMeter;

public class HomeFragment extends Fragment implements OnMapReadyCallback {

    private static final int LOCATION_REQUEST_CODE = 99;
    private GoogleMap mMap;
    private MarkerOptions mowMarkerOptions = new MarkerOptions();
    private Marker mowMarker;
    private MarkerOptions chargingMarkerOptions = new MarkerOptions();
    private Marker chargingMarker;
    private static final String TAG = "MainActivity";
    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";
    private FirebaseAuth mAuth;
    private Button autoButton;
    private Button manualButton;
    private Button dynamicButton;
    private MapView mMapView;
    private Button powerButton;
    private Button chargeButton;
    private TextView statusTV;
    private TextView batteryTV2;
    private BatteryMeter batteryMeter2;
    private String currentStatus;
    protected int currentPath;
    private SharedPreferencesHelper spHelper;
    private Spinner mainSpinner;
    ArrayList<Coordinate> pathAlgo = new ArrayList<Coordinate>();
    private ImageButton settingsButton;
    private View view;
    private Timer mowTimer = new Timer();

    private ArrayList<String> coordList = new ArrayList<String>();
    int coordCounter=0;

    private HashMap<String, Coordinate> vertexCoordinates = new HashMap<String, Coordinate>();
    //protected ArrayList<Coordinate> vertexCoordinates = new ArrayList<Coordinate>();
    protected ArrayList<Coordinate> vCoordinates = new ArrayList<Coordinate>();
    private int childCount;
    private int coordCount = 0;

    private LatLng m1;
    private LatLng m2;
    private LatLng m3;
    private LatLng m4;
    private LatLng mCharge;
    private Timer timerMovement = new Timer();

    private Coordinate v1 = new Coordinate();
    private Coordinate v2 = new Coordinate();
    private Coordinate v3 = new Coordinate();
    private Coordinate v4 = new Coordinate();

    private float mowerRotation;
    private LatLng mowerLocation = new LatLng(0,0);

    private static DatabaseReference statusReference;
    private DatabaseReference userReference;
    private DatabaseReference gpsReference;
    private DatabaseReference pathReference;
    private DatabaseReference ATMegaReference;
    private DatabaseReference modeReference;
    private DatabaseReference bladeReference;
    private DatabaseReference chargeReference;

    private FusedLocationProviderClient fusedLocationProviderClient;


    private DatabaseHelper dbHelper;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup)inflater.inflate(R.layout.fragment_home, container, false);

        view =  inflater.inflate(R.layout.fragment_home, container, false);

        spHelper = new SharedPreferencesHelper(getActivity());

        batteryTV2 = root.findViewById(R.id.batteryTV2);
        batteryMeter2 = root.findViewById(R.id.batteryMeter3);

        powerButton = root.findViewById(R.id.powerButton);
        chargeButton = root.findViewById(R.id.chargeButton);
        autoButton = root.findViewById(R.id.auto_button);
        manualButton = root.findViewById(R.id.manual_button);
        //dynamicButton = root.findViewById(R.id.dynamicButton);

        statusTV = root.findViewById(R.id.statusTV);
        mainSpinner = root.findViewById(R.id.mainSpinner);

        //get current path from firebase
        userReference = FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        userReference.child("Current Path").setValue(0);

        gpsReference = FirebaseDatabase.getInstance().getReference("GPS");
        bladeReference = FirebaseDatabase.getInstance().getReference("Control").child("Blade");

        pathReference = FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Mower Paths");

        modeReference = FirebaseDatabase.getInstance().getReference("Control").child("Mode");
        chargeReference = FirebaseDatabase.getInstance().getReference("Control").child("Charging");

        ATMegaReference = FirebaseDatabase.getInstance().getReference("ATMega");

        mAuth = FirebaseAuth.getInstance();

        dbHelper = new DatabaseHelper(getActivity());

        //gpsReference.child("CMW").setValue("45.495782289355525@-73.82366750389338");

        //get battery info
        getBatteryLevel();
        //placeMowerMarker();

        //spinner code --------------------------------------------------------------------------
        ArrayList<String> spinnerOptions = new ArrayList<>();
        if(dbHelper.getAllPathNames()!=null) {
            spinnerOptions = dbHelper.getAllPathNames();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, spinnerOptions);
        //set the spinners adapter to the previously created one.
        mainSpinner.setAdapter(adapter);

        //get path number from selected path spinner
        mainSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentPath = dbHelper.getPathIdFromName(mainSpinner.getSelectedItem().toString());
                //mowTimer.cancel();
                Log.i("current path", ""+currentPath);
                //Log.i("Current Path","Current Path: "+ currentPath);
                //set current path in firebase
                if(currentPath!=0) {
                    userReference.child("Current Path").setValue(currentPath);
                }

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                currentPath = 0;
            }
        });

        //manual and auto button, when mode = 0 -> manual ,when mode=1 -> auto mode

       modeReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int mode = Integer.parseInt(snapshot.getValue().toString());
                if (mode == 0){
                    manualButton.setBackgroundResource(R.drawable.white_button_clicked);
                    autoButton.setBackgroundResource(R.drawable.white_button);
                    //manualMode();
                }
                if (mode == 1){
                    manualButton.setBackgroundResource(R.drawable.white_button);
                    autoButton.setBackgroundResource(R.drawable.white_button_clicked);
                    //autoMode();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        manualButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modeReference.setValue(0);
                manualButton.setBackgroundResource(R.drawable.white_button_clicked);
                autoButton.setBackgroundResource(R.drawable.white_button);
                //manualMode();
            }
        });
        autoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modeReference.setValue(1);
                manualButton.setBackgroundResource(R.drawable.white_button);
                autoButton.setBackgroundResource(R.drawable.white_button_clicked);
                //autoMode();
            }
        });



        //mapView code ---------------------------------------------------------------------------

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }

        mMapView = (MapView) root.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();
        mMapView.getMapAsync(this);


        //check if user has allowed location permission
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        }

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
                                if(spHelper.getPower() && currentPath != 0){
                                    //simulateMowerMovement();
                                    statusReference.setValue("Mowing");
                                    bladeReference.setValue(1);
                                }
                                if (spHelper.getPower() == false){
                                    Toast.makeText(getActivity(),"Power is off, please turn power on",Toast.LENGTH_SHORT).show();
                                }
                                if (currentPath == 0){
                                    Toast.makeText(getActivity(),"No path exists, please create a path",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        chargeButton.setText("CHARGE");
                        chargeButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                statusReference.setValue("Charging");
                                bladeReference.setValue(0);
                                chargeReference.setValue(1);
                                batteryMeter2.setCharging(true);
                            }
                        });
                    }

                    else if(currentStatus.equals("Mowing")){
                        statusTV.setTextColor(Color.BLUE);
                        powerButton.setText("TURN OFF");
                        powerButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                statusReference.setValue("Off");
                                bladeReference.setValue(0);
                                //timerMovement.cancel();
                                mowTimer=null;
                            }
                        });
                        chargeButton.setText("CHARGE");
                        chargeButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                statusReference.setValue("Charging");
                                modeReference.setValue(2);
                                bladeReference.setValue(0);
                                chargeReference.setValue(1);
                                batteryMeter2.setCharging(true);
                            }
                        });
                    }

                    else if(currentStatus.equals("Charging")){
                        powerButton.setText("TURN ON");
                        statusTV.setTextColor(Color.GREEN);
                        batteryMeter2.setCharging(true);
                        powerButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                statusReference.setValue("Mowing");
                                bladeReference.setValue(1);
                                chargeReference.setValue(0);
                                batteryMeter2.setCharging(false);
                            }
                        });

                        chargeButton.setText("END CHARGE");
                        chargeButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                statusReference.setValue("Off");
                                chargeReference.setValue(0);
                                batteryMeter2.setCharging(false);
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

        if(currentPath == 0){
            placeCameraOnUserLocation();
        }

        return root;
    }

    private void simulateMowerMovement() {

        //Timer timerMovement = new Timer();

        timerMovement.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                if(coordCounter<coordList.size()) {
                    gpsReference.child("CMW").setValue(coordList.get(coordCounter));
                    coordCounter++;
                }

            }
        }, 0, 1500);//put here time 1000 milliseconds=1 second


    }

    private void manualMode() {
        dynamicButton.setText("CONTROLLER");
        dynamicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToControlActivity();
            }
        });
    }

    private void autoMode() {
        dynamicButton.setText("CREATE PATH");
        dynamicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMapsActivity();
            }
        });
    }


    private void goToControlActivity() {
        Intent intent = new Intent (getActivity(), ManualActivity.class);
        startActivity(intent);
    }


    private void goToLoginActivity() {
        Intent intent = new Intent (getActivity(), LoginActivity.class);
        startActivity(intent);
    }

    private void goToMapsActivity(){
        Intent intent = new Intent (getActivity(), MapsActivity.class);
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
                    //chargingMarker();
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
                                mCharge = new LatLng(coordinate.getLat(),coordinate.getLon());

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

                            //for each different path chosen, clear the firebase node, reset the coord count,
                            //write the new path coordinates to the firebase and move the camera to the current path
                            coordCount = 0;
                            gpsReference.child("PC").setValue("0");
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(m1,19));

                            //iterate through the arraylist of coordinates and fill the map with the coordinates
                            //map example (m1,lat-lng)

                            PolylineOptions mowPath = new PolylineOptions();

                            //use an array list to keep track of the amount of path points
                            ArrayList mowPathArray = new ArrayList();
                            ArrayList arrayLat = new ArrayList();
                            ArrayList arrayLon = new ArrayList();

                            for (Coordinate s : pathAlgo) {
                                ++coordCount;
                                Log.i("PATH-FINDING","Path finding algorithm : LAT:  " + s.getLat() + "--------LON: "+ s.getLon());
                                //add each coordinate point to the polyline path
                                mowPath.add(new LatLng(s.getLat(),s.getLon())).width(4);
                                mowPathArray.add(new LatLng(s.getLat(),s.getLon()));
                                arrayLat.add(s.getLat());
                                arrayLon.add(s.getLon());
                                coordList.add(s.getLat()+"@"+s.getLon());
                                //gpsReference.child("Path Coordinates").child(String.valueOf(coordCount)).setValue(s);
                                gpsReference.child("PC").child(String.valueOf(coordCount)).setValue(s.getLat()+"@"+s.getLon());

                            }

                            Log.i(TAG,"created path" +pathCoordinates);

                            //add the charge point to the end of the mowpath
                            mowPath.add(mCharge);
                            mowPathArray.add(mCharge);

                            Log.i("ARRAY LAT: ", arrayLat.toString());
                            Log.i("ARRAY LON: ", arrayLon.toString());

                            //add the charge point to the end of the firebase coordinates
                            Coordinate vCharge = new Coordinate(mCharge.latitude,mCharge.longitude);
                            gpsReference.child("PC").child(String.valueOf(coordCount+1)).setValue(vCharge.getLat()+"@"+vCharge.getLon());
                            coordList.add(vCharge.getLat()+"@"+vCharge.getLon());

                            //set the value of the amount of path points in the firebase
                            gpsReference.child("NOP").setValue(mowPathArray.size());

                            //insert the created polyline onto the map
                            Polyline polyline = mMap.addPolyline(mowPath);
                        }

                        //vertexCoordinates.put(String.valueOf(childCount),coordinate);
                        vCoordinates.add(coordinate);
                        chargingMarker();
                        placeMowerMarker();
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

    private void getBatteryLevel(){

        ATMegaReference.child("BATTERY").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String batteryLevel = snapshot.getValue().toString();
                Log.i(TAG,snapshot.getValue().toString());
                double batteryDouble = Double.parseDouble(batteryLevel);
                int batteryInt = (int) (((batteryDouble - 22) * (100 - 0)) / (28 - 22));
                if(batteryDouble<22){
                    batteryInt = 0;
                }
                batteryMeter2.setChargeLevel(batteryInt);
                batteryTV2.setText(batteryInt+"%");
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

    private void placeCameraOnUserLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        //check if user has permission set
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    Coordinate userLocation = new Coordinate(location.getLatitude(), location.getLongitude());
                    //gpsReference.child("CurrentMowerCoordinate").setValue(userLocation);
                    //gpsReference.child("CUL").setValue(userLocation.getLat()+"@"+userLocation.getLon());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(userLocation.getLat(), userLocation.getLon()),19));
                }
            });
        }

        //request permissions if user hasn't allowed yet
        else {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            }
        }
    }

    private void placeMowerMarker(){

        gpsReference.child("CUL").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //obtain coordinate from firebase as latlng value
                //Coordinate mowerCoordinate = snapshot.getValue(Coordinate.class);

                String [] mowerCoord = snapshot.getValue().toString().split("@");
                double mowerLat = Double.parseDouble(mowerCoord[0]);
                double mowerLon = Double.parseDouble(mowerCoord[1]);
                Log.i("MOWER COORD extracted",mowerLat+"-----"+mowerLon);
                //mowerCoord[0].replace("{coord=","");
                //mowerCoord[1].replace("}","");
                Log.i("MOWER COORD actual",mowerCoord[0]+"-----"+mowerCoord[1]);
                //double mowerLat = Double.parseDouble(mowerCoord[0]);


                mowerLocation = new LatLng(mowerLat, mowerLon);

                //make mower icon marker smaller in size
                BitmapDrawable bitmapDraw = (BitmapDrawable) view.getResources().getDrawable(R.drawable.blue_circle);
                Bitmap b = bitmapDraw.getBitmap();
                Bitmap mowerIcon = Bitmap.createScaledBitmap(b, 100, 100, false);
                mowMarkerOptions.icon(BitmapDescriptorFactory.fromBitmap(mowerIcon));

                //if there is no marker, add the marker to the app
                if (mowMarker == null ){
                    mowMarkerOptions.position(mowerLocation);
                    mowMarker = mMap.addMarker(mowMarkerOptions);
                    //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mowerLocation,19));
                }

                //once there is a marker
                //set updated location, everytime the current mower location changes
                //mowMarker.setRotation();
                mowMarker.setPosition(mowerLocation);
                //move camera on update
                //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mowerLocation,19));

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


                BitmapDrawable bitmapDraw = (BitmapDrawable) view.getResources().getDrawable(R.drawable.charging_icon_blue);
                Bitmap b = bitmapDraw.getBitmap();
                Bitmap chargingIcon = Bitmap.createScaledBitmap(b, 100, 100, false);
                //chargingMarkerOptions.icon(BitmapDescriptorFactory.fromBitmap(chargingIcon));
                chargingMarkerOptions.icon(BitmapDescriptorFactory.fromBitmap(chargingIcon));


                /*
                byte [] chargingIconByteArray = getArguments().getByteArray("chargingIcon");
                Bitmap chargingIcon = BitmapFactory.decodeByteArray(chargingIconByteArray,0,chargingIconByteArray.length);

                */

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

    protected void startService(){
        Intent serviceIntent = new Intent(getActivity(), MyService.class);
        ContextCompat.startForegroundService(getActivity(),serviceIntent);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(currentPath == 0) {
            placeCameraOnUserLocation();
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
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

}