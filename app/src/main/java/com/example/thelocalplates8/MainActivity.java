package com.example.thelocalplates8;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import android.location.Location;
import android.os.Bundle;


import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import com.example.thelocalplates8.Controllers.BusinessController;
import com.example.thelocalplates8.Models.BusinessModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {


    private MapView mapView;
    private GoogleMap googleMap;

    Button businessBtn;
    Button cartButton;
    Button settingsButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar =findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Initialize Firebase Auth
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        // Initialize the MapView
        mapView = findViewById(R.id.mapViewMainActivity);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
    }

    private void logoutUser() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.Business) {
            // Handle item 1 selection
            Intent intent = new Intent(MainActivity.this, BusinessActivity.class);
            startActivity(intent);
            return true;
        }
        else if (itemId == R.id.cart) {
            // Handle item 2 selection
            Intent intent = new Intent(MainActivity.this, CartActivity.class);
            startActivity(intent);
            return true;
        }
        else if (itemId == R.id.setting) {
            // Handle item 3 selection
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }else if (itemId == R.id.logout) {
            // Handle item 3 selection
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Map's functions
    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        // Supposed to remove default markers
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        showCurrentLocation();
        showBusiness();
    }

    private void showBusiness() {
        BusinessController businessController = new BusinessController();
        businessController.getAllBusinesses(new BusinessController.GetAllBusinesses() {
            @Override
            public void onGetAllBusinesses(ArrayList<BusinessModel> businessModels) {
                for(BusinessModel businessModel: businessModels){
                    LatLng currLoc = new LatLng(businessModel.getLocation().getLatitude(),
                            businessModel.getLocation().getLongitude());
                    Marker marker = googleMap.addMarker(new MarkerOptions().position(currLoc).
                            title(businessModel.getFirstName() + " " + businessModel.getLastName()));
                    marker.setTag(businessModel.getBusinessId());
                }
            }
        });
    }

    private void showCurrentLocation() {
        LocationManager locationManager = new LocationManager(this);
        LiveData<Location> currentLocationLiveData = locationManager.getGeoPoint();

        currentLocationLiveData.observe(this, new Observer<Location>() {
            @Override
            public void onChanged(Location location) {
                LatLng currLoc = new LatLng(location.getLatitude(),location.getLongitude());
                googleMap.addMarker(new MarkerOptions().position(currLoc).title("My Location"));

                float zoomLevel = 17.0f;
                float bearing = 0.0f;
                float tiltAngle = 45.0f;
                // set the camera position in 3d
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(currLoc)  // Set the target LatLng (desired map center)
                        .zoom(zoomLevel) // Set the desired zoom level
                        .bearing(bearing) // Set the desired bearing (0 degrees points north)
                        .tilt(tiltAngle) // Set the desired tilt angle (between 0 and 90 degrees)
                        .build();
                CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
                googleMap.moveCamera(cameraUpdate);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }



}