package com.example.elawn_android;

import androidx.fragment.app.FragmentActivity;

import android.graphics.Color;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.elawn_android.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        LatLng montreal = new LatLng(45.5 , -73.56);
        mMap.addMarker(new MarkerOptions().position(montreal).title("Marker in Montreal"));

        //coordinates

        LatLng centerField = new LatLng(45.496264,-73.823371);

        LatLng m1 = new LatLng(45.496748,-73.823077);
        LatLng m2 = new LatLng(45.496315,-73.822631);
        LatLng m3 = new LatLng(45.495788,-73.823675);
        LatLng m4 = new LatLng(45.496220,-73.824112);

        LatLng m5 = new LatLng(45.495787,-73.823670);
        LatLng m6 = new LatLng(45.496220,-73.824108);
        LatLng m7 = new LatLng(45.496223,-73.824101);
        LatLng m8 = new LatLng(45.495795,-73.823661);

        //Polyline

        PolylineOptions polyOptions = new PolylineOptions()
                .add(m5)
                .add(m6)
                .add(m7)
                .add(m8)
                .width(7);

        //Polygons
        PolygonOptions rectOption= new PolygonOptions()
                .add(m1)
                .add(m2)
                .add(m3)
                .add(m4)
                .fillColor(0x7F00FF00)
                .strokeColor(Color.RED);

        Polygon polygon = mMap.addPolygon(rectOption);

        Polyline polyline = mMap.addPolyline(polyOptions) ;

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(centerField,18));
    }
}