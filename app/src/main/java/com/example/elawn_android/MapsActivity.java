package com.example.elawn_android;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.example.elawn_android.Database.DatabaseHelper;
import com.example.elawn_android.Service.Coordinate;
import com.example.elawn_android.Service.Path;
import com.example.elawn_android.Service.PathFinding;
import com.example.elawn_android.Service.SharedPreferencesHelper;
import com.example.elawn_android.databinding.ActivityMapsBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnMarkerDragListener, GoogleMap.OnInfoWindowClickListener {

    private static final int LOCATION_REQUEST_CODE = 99;
    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private Marker vertexMarker;
    private int markerCount;
    private int coordCount = 0;
    private static String TAG = "maps";
    private static DatabaseReference pathReference;
    private static DatabaseReference perimeterReference;
    private static DatabaseReference gpsReference;
    private static DatabaseReference userReference;

    private SharedPreferencesHelper spHelper;
    private DatabaseHelper dbHelper;

    private String pathNumber;
    private String currentPath = "4";
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
    private Coordinate vCharge = new Coordinate();
    private Geocoder geocoder;


    private PolylineOptions mowPath = new PolylineOptions();
    private MarkerOptions chargingMarkerOptions = new MarkerOptions();
    private MarkerOptions chargingMarkerOptionsStatic = new MarkerOptions();
    private Marker chargingMarker;

    FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        spHelper = new SharedPreferencesHelper(this);
        dbHelper = new DatabaseHelper(this);

        geocoder = new Geocoder(this, Locale.getDefault());

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

        //use location tracking to track users location
        getUserLocation();

        markerCount = 1;


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        LatLng centerField = new LatLng(45.496264, -73.823371);

        //Markers
        //When the user clicks the map, add a marker with a clickable snippet
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                double lat = latLng.latitude;
                double lon = latLng.longitude;

                if (vertexMarker != null) {
                    vertexMarker.remove();
                }

                //allows user to pick 4 points with markers
                if (markerCount <= 4) {
                    vertexMarker = mMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .draggable(true)
                            .title("Select mowing points")
                            .snippet("Click to confirm point selection"));
                    vertexMarker.showInfoWindow();
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

                }

                if (markerCount == 5) {
                    //set charging icon marker and make it smaller in size
                    BitmapDrawable bitmapDraw = (BitmapDrawable) getResources().getDrawable(R.drawable.charging_icon_blue);
                    Bitmap b = bitmapDraw.getBitmap();
                    Bitmap chargingIcon = Bitmap.createScaledBitmap(b, 100, 100, false);
                    chargingMarkerOptions.icon(BitmapDescriptorFactory.fromBitmap(chargingIcon));
                    chargingMarkerOptionsStatic.icon(BitmapDescriptorFactory.fromBitmap(chargingIcon));


                    if (chargingMarker != null) {
                        chargingMarker.remove();
                    }

                    //place a charging marker if the marker doesn't exist
                    chargingMarker = mMap.addMarker(chargingMarkerOptions
                            .position(latLng)
                            .draggable(true)
                            .title("Select charging point")
                            .snippet("Click to confirm charging point selection"));
                    chargingMarker.showInfoWindow();
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                }

                if (markerCount > 5) {
                    chargingMarker.remove();
                }

            }
        });

        mMap.setOnMarkerClickListener(this);
        mMap.setOnMarkerDragListener(this);
        mMap.setOnInfoWindowClickListener(this);

        //Read firebase current user location and set camera

        gpsReference.child("Current User Location").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Coordinate userLocationFB = snapshot.getValue(Coordinate.class);

                LatLng userLocation = new LatLng(userLocationFB.getLat(), userLocationFB.getLon());

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 18));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //Marker override functions

    @Override
    public boolean onMarkerClick(Marker marker) {

        //Toast.makeText(this, "My position " + marker.getPosition(), Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {

        if (markerCount == 1) {
            m1 = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
            v1.setLat(marker.getPosition().latitude);
            v1.setLon(marker.getPosition().longitude);
            Log.i(TAG, "Lat " + v1.getLat());
            Log.i(TAG, "Lon " + v1.getLon());
            marker = mMap.addMarker(new MarkerOptions()
                    .position(marker.getPosition())
                    .title("Point " + String.valueOf(markerCount)));
            marker.showInfoWindow();
        }

        if (markerCount == 2) {
            m2 = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
            v2.setLat(marker.getPosition().latitude);
            v2.setLon(marker.getPosition().longitude);
            marker = mMap.addMarker(new MarkerOptions()
                    .position(marker.getPosition())
                    .title("Point " + String.valueOf(markerCount)));
            marker.showInfoWindow();
        }

        if (markerCount == 3) {
            m3 = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
            v3.setLat(marker.getPosition().latitude);
            v3.setLon(marker.getPosition().longitude);
            marker = mMap.addMarker(new MarkerOptions()
                    .position(marker.getPosition())
                    .title("Point " + String.valueOf(markerCount)));
            marker.showInfoWindow();
        }

        if (markerCount == 4) {
            m4 = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
            v4.setLat(marker.getPosition().latitude);
            v4.setLon(marker.getPosition().longitude);
            marker = mMap.addMarker(new MarkerOptions()
                    .position(marker.getPosition())
                    .title("Point " + String.valueOf(markerCount)));

            //write the selected coordinates to the firebase
            Toast.makeText(this, "Mowing area successfully placed ", Toast.LENGTH_LONG).show();
            //if the path number exists, get previous path number, add 1 otherwise set the path number to 1
            if (spHelper.getPathNumber() != null) {
                //add one to the current path number
                int pathNumber = Integer.parseInt(spHelper.getPathNumber()) + 1;
                nextPathNumber = String.valueOf(pathNumber);
                Log.i(TAG, "Path number pre:" + nextPathNumber);
            } else {
                nextPathNumber = "1";
                spHelper.setPathNumber("1");
            }

            /*
            pathReference.child(nextPathNumber).child("V1").setValue(v1);
            pathReference.child(nextPathNumber).child("V2").setValue(v2);
            pathReference.child(nextPathNumber).child("V3").setValue(v3);
            pathReference.child(nextPathNumber).child("V4").setValue(v4);

             */


            PolygonOptions mowArea = new PolygonOptions()
                    .add(m1)
                    .add(m2)
                    .add(m3)
                    .add(m4)
                    .fillColor(0x7F00FF00)
                    .strokeColor(Color.RED);

            Polygon polygon = mMap.addPolygon(mowArea);

            //use path finding algorithm to find coordinates for all polyline paths

            PathFinding p = new PathFinding(v1, v2, v3, v4);

            ArrayList<Coordinate> algo = new ArrayList<Coordinate>();
            algo = p.pathAlgorithm();

            //create a map with the coordinate names as the key and the LatLng objects as the values
            //this map will hold all coordinates generated from the algorithm

            HashMap<String, LatLng> pathCoordinates = new HashMap<String, LatLng>();

            //iterate through the arraylist of coordinates and fill the map with the coordinates
            //map example (m1,lat-lng)

            for (Coordinate s : algo) {
                ++coordCount;
                Log.i("PATH-FINDING", "Path finding algorithm : LAT:  " + s.getLat() + "--------LON: " + s.getLon());
                //add each coordinate point to the polyline path
                mowPath.add(new LatLng(s.getLat(), s.getLon())).width(7);

                pathCoordinates.put("m" + coordCount, new LatLng(s.getLat(), s.getLon()));

            }
            Log.i(TAG, "created path" + pathCoordinates);

        }

        if (markerCount == 5) {
            Toast.makeText(this, "Charging point successfully placed ", Toast.LENGTH_LONG).show();
            Marker chargeMarker = mMap.addMarker(chargingMarkerOptionsStatic
                    .position(marker.getPosition())
                    .title("Charging Point"));

            //write the charging point to firebase
            vCharge.setLat(chargeMarker.getPosition().latitude);
            vCharge.setLon(chargeMarker.getPosition().longitude);
            //pathReference.child(nextPathNumber).child("Charge Point").setValue(vCharge);

            //add the charging point to the end of the mow path
            mowPath.add(new LatLng(vCharge.getLat(), vCharge.getLon()));

            //insert the created polyline onto the map
            Polyline polyline = mMap.addPolyline(mowPath);

            List<Address> addresses;

            try {
                addresses = geocoder.getFromLocation(vCharge.getLat(), vCharge.getLon(),1);
                String address = addresses.get(0).getAddressLine(0);
                Log.i("GEO LOCATION SUCCESS",address);
                userInputPathName(address);

            } catch (IOException e) {
                e.printStackTrace();
                Log.i("GEO LOCATION FAILED","");
            }


        }

        //Toast.makeText(this, "Marker Count: "+ String.valueOf(markerCount), Toast.LENGTH_SHORT).show();
        //iterate marker count for each time a window is clicked
        markerCount++;
    }

    private void userInputPathName(String pathAddress){

        //ask user for pathName, save path to database, return user to home
        EditText pathName = new EditText(this);
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Enter your path name");

        // Set an EditText view to get user input

        alert.setView(pathName);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String pathNameInput = pathName.getText().toString();
                //Create new path with inserted information
                //insert path into the database, get the generated path id, then insert the path into the firebase
                Path newPath = new Path(-1,pathNameInput,pathAddress);
                dbHelper.insertPath(newPath);
                int pathId = dbHelper.getRecentPathId();

                pathReference.child(String.valueOf(pathId)).child("V1").setValue(v1);
                pathReference.child(String.valueOf(pathId)).child("V2").setValue(v2);
                pathReference.child(String.valueOf(pathId)).child("V3").setValue(v3);
                pathReference.child(String.valueOf(pathId)).child("V4").setValue(v4);
                pathReference.child(String.valueOf(pathId)).child("Charge Point").setValue(vCharge);

                goToMainActivity();


            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.show();


    }

    private void goToMainActivity() {
        Intent intent = new Intent (this,MainActivity2.class);
        startActivity(intent);
    }

    private void getUserLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        //check if user has permission set
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    Coordinate userLocation = new Coordinate(location.getLatitude(), location.getLongitude());
                    gpsReference.child("Current User Location").setValue(userLocation);
                }
            });
        }

        //request permissions if user hasn't allowed yet
        else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
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
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

    }
}
