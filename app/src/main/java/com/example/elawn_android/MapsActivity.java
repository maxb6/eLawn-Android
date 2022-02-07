package com.example.elawn_android;

import androidx.fragment.app.FragmentActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.elawn_android.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,GoogleMap.OnMarkerClickListener,GoogleMap.OnMarkerDragListener, GoogleMap.OnInfoWindowClickListener {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private Marker marker;
    private int markerCount = 0;
    private int coordCount = 0;
    private static String TAG = "pathAlgo";
    private static DatabaseReference pathReference;
    private static DatabaseReference perimeterReference;
    private static DatabaseReference gpsReference;
    private static DatabaseReference userReference;

    private SharedPreferencesHelper spHelper;

    private String pathNumber;
    private String currentPath = "4" ;
    private String nextPathNumber;
    private boolean allowWrite;


    private LatLng m1;
    private LatLng m2;
    private LatLng m3;
    private LatLng m4;

    private Coordinate vertex = new Coordinate();
    private Coordinate v1 = new Coordinate();
    private Coordinate v2 = new Coordinate();
    private Coordinate v3 = new Coordinate();
    private Coordinate v4 = new Coordinate();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        spHelper = new SharedPreferencesHelper(this);

        pathReference = FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Mower Paths");
        perimeterReference = FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Perimeter");

        gpsReference = FirebaseDatabase.getInstance().getReference("GPS");

        userReference = FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //setCurrentPath();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        // Add a marker in Sydney and move the camera
        LatLng montreal = new LatLng(45.5, -73.56);
        mMap.addMarker(new MarkerOptions().position(montreal).title("Marker in Montreal"));

        //coordinates

        LatLng centerField = new LatLng(45.496264, -73.823371);

        //Markers
        //When the user clicks the map, add a marker with a clickable snippet
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {


                if (marker != null) {
                    marker.remove();
                }

                //allows user to pick 4 points with markers
                if (markerCount < 4) {
                    marker = mMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .draggable(true)
                            .title("Select mowing points")
                            .snippet("Click to confirm point selection"));
                    marker.showInfoWindow();
                }
            }
        });

        mMap.setOnMarkerClickListener(this);
        mMap.setOnMarkerDragListener(this);
        mMap.setOnInfoWindowClickListener(this);

        //Default camera position

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(centerField, 18));
    }

    //Marker override functions

    @Override
    public boolean onMarkerClick(Marker marker) {

        Toast.makeText(this, "My position " + marker.getPosition(), Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

        Toast.makeText(this, "My position " + marker.getPosition(), Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        markerCount++;
        Toast.makeText(this, "Marker Count: "+ String.valueOf(markerCount), Toast.LENGTH_SHORT).show();
        marker = mMap.addMarker(new MarkerOptions()
                .position(marker.getPosition())
                .title("Point " + String.valueOf(markerCount)));
        marker.showInfoWindow();
        Log.i(TAG, "Marker Count: " + String.valueOf(markerCount));
        //pathReference.child("Path 1").setValue("Latitude: "+marker.getPosition().latitude + " || Longitude: "+marker.getPosition().longitude);

        //Setting vertex coordinates in the firebase
        vertex.setLat( marker.getPosition().latitude);
        vertex.setLon(marker.getPosition().longitude);

        /*
        //if the path number exists, get previous path number, add 1 otherwise set the path number to 1
        if(spHelper.getPathNumber() != null) {
            finalPathNumber = spHelper.getPathNumber();
            Log.i(TAG, "Path number pre:" + finalPathNumber);
        }
        else{
            finalPathNumber = "1";
            spHelper.setPathNumber("1");
        }

        pathReference.child(finalPathNumber).child("V"+markerCount).setValue(vertex);


         */

        //assign the user selected coordinate points to LatLng variables m1 to m4
        switch(markerCount) {
            case 1:
                m1 = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
                v1.setLat(marker.getPosition().latitude);
                v1.setLon(marker.getPosition().longitude);
                Log.i(TAG,"Lat " + v1.getLat());
                Log.i(TAG,"Lon "+ v1.getLon());

            case 2:
                m2 = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
                v2.setLat(marker.getPosition().latitude);
                v2.setLon(marker.getPosition().longitude);

            case 3:
                m3 = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
                v3.setLat(marker.getPosition().latitude);
                v3.setLon(marker.getPosition().longitude);
            case 4:
                m4 = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
                v4.setLat(marker.getPosition().latitude);
                v4.setLon(marker.getPosition().longitude);

        }
        //once user places 4 markers, create a polygon to fill the area
        if(markerCount >=4) {

            //write the selected coordinates to the firebase

            //if the path number exists, get previous path number, add 1 otherwise set the path number to 1
            if(spHelper.getPathNumber() != null) {
                //add one to the current path number
                int pathNumber = Integer.parseInt(spHelper.getPathNumber())+1;
                nextPathNumber = String.valueOf(pathNumber);
                Log.i(TAG, "Path number pre:" + nextPathNumber);
            }
            else{
                nextPathNumber = "1";
                spHelper.setPathNumber("1");
            }

            pathReference.child(nextPathNumber).child("V1").setValue(v1);
            pathReference.child(nextPathNumber).child("V2").setValue(v2);
            pathReference.child(nextPathNumber).child("V3").setValue(v3);
            pathReference.child(nextPathNumber).child("V4").setValue(v4);



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

            ArrayList<Coordinate> algo = new ArrayList<Coordinate>();
            algo = p.pathAlgorithm();

            //create a map with the coordinate names as the key and the LatLng objects as the values
            //this map will hold all coordinates generated from the algorithm

            HashMap<String, LatLng> pathCoordinates = new HashMap<String, LatLng>();
            PolylineOptions mowPath = new PolylineOptions();

            //iterate through the arraylist of coordinates and fill the map with the coordinates
            //map example (m1,lat-lng)

            for (Coordinate s : algo) {
                ++coordCount;
                Log.i("PATH-FINDING","Path finding algorithm : LAT:  " + s.getLat() + "--------LON: "+ s.getLon());
                //add each coordinate point to the polyline path
                mowPath.add(new LatLng(s.getLat(),s.getLon())).width(7);

                pathCoordinates.put("m"+coordCount,new LatLng(s.getLat(),s.getLon()));

                //place path coordinates into firebase user node
                //userReference.child("Path Coordinates").child(nextPathNumber).child(String.valueOf(coordCount)).setValue(s);

                //place current path coordinates in firebase gps node
                //gpsReference.child("Path Coordinates").child(String.valueOf(coordCount)).setValue(s);
            }

            Log.i(TAG,"created path" +pathCoordinates);

            //insert the created polyline onto the map
            Polyline polyline = mMap.addPolyline(mowPath);

            Log.i(TAG,"Lat " + v1.getLat());
            Log.i(TAG,"Lon "+ v1.getLon());


            //once the 4 points have been placed we can increment to the next path number

            int pathNumber = Integer.parseInt(spHelper.getPathNumber())+1;
            spHelper.setPathNumber(String.valueOf(pathNumber));
            Log.i(TAG,"Path number f: "+ pathNumber);



        }

    }

    //a function to set the current path from the firebase

    private void setCurrentPath(String currentPath){

        userReference.child("Number of Paths").setValue(currentPath);
        spHelper.setPathNumber(currentPath);

    }


}
