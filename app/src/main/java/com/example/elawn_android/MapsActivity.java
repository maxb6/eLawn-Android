package com.example.elawn_android;

import androidx.fragment.app.FragmentActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Switch;
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

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,GoogleMap.OnMarkerClickListener,GoogleMap.OnMarkerDragListener, GoogleMap.OnInfoWindowClickListener {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private Marker marker;
    private int markerCount = 0;
    private static String TAG = "Path";
    private static DatabaseReference pathReference;
    private static DatabaseReference perimeterReference;
    private static DatabaseReference gpsReference;


    private LatLng m1;
    private LatLng m2;
    private LatLng m3;
    private LatLng m4;

    private Coordinate vertex = new Coordinate(0,0);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pathReference = FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Mower Paths");
        perimeterReference = FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Perimeter");

        gpsReference = FirebaseDatabase.getInstance().getReference("GPS").child("Perimeter");

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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

        /*
        LatLng m1 = new LatLng(45.496748, -73.823077);
        LatLng m2 = new LatLng(45.496315, -73.822631);
        LatLng m3 = new LatLng(45.495788, -73.823675);
        LatLng m4 = new LatLng(45.496220, -73.824112);

         */

        LatLng m5 = new LatLng(45.495787, -73.823670);
        LatLng m6 = new LatLng(45.496220, -73.824108);
        LatLng m7 = new LatLng(45.496223, -73.824101);
        LatLng m8 = new LatLng(45.495795, -73.823661);

        //Polyline

        PolylineOptions polyOptions = new PolylineOptions()
                .add(m5)
                .add(m6)
                .add(m7)
                .add(m8)
                .width(7);

        Polyline polyline = mMap.addPolyline(polyOptions);

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
        pathReference.child("Path 1").push().setValue("Latitude: "+marker.getPosition().latitude + " || Longitude: "+marker.getPosition().longitude);

        //Setting vertex coordinates in the firebase
        vertex.setLat((float) marker.getPosition().latitude);
        vertex.setLon((float) marker.getPosition().longitude);
        perimeterReference.child("V"+markerCount).setValue(vertex);
        gpsReference.child("V"+markerCount).setValue(vertex);

        //assign the user selected coordinate points to LatLng variables m1 to m4
        switch(markerCount) {
            case 1:
                m1 = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
            case 2:
                m2 = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
            case 3:
                m3 = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
            case 4:
                m4 = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);

        }
        //once user places 4 markers, create a polygon to fill the area
        if(markerCount >=4) {
            PolygonOptions mowArea = new PolygonOptions()
                    .add(m1)
                    .add(m2)
                    .add(m3)
                    .add(m4)
                    .fillColor(0x7F00FF00)
                    .strokeColor(Color.RED);

            Polygon polygon = mMap.addPolygon(mowArea);
        }

    }

}