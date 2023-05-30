package com.example.thelocalplates8;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.location.Location;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.thelocalplates8.Controllers.BusinessController;
import com.example.thelocalplates8.Models.BusinessModel;
import com.example.thelocalplates8.Models.ProductModel;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private MapView mapView;
    private GoogleMap googleMap;
    private SearchView searchView;
    private GeoPoint geoPoint;
    private int radius = 2000;
    private boolean searchState;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Initialize the MapView
        mapView = findViewById(R.id.mapView2);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        searchView = (SearchView) findViewById(R.id.mapSearchView);
        searchState = false;


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // This method is called when the user submits the search query
                // You can retrieve the entered text from the 'query' parameter
                String searchText = query.trim();
                processSearchQuery(searchText);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // This method is called when the text in the search view changes
                // You can retrieve the updated text from the 'newText' parameter
                String updatedText = newText.trim();
                return true;
            }
        });
    }

    // Map's functions
    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        initializeMap();

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                // If the user clicks on the current location it resets everything
                if(marker.getTitle().equals("My Location")){
                    initializeMap();
                    Toast.makeText(MapsActivity.this, "Map have been reset", Toast.LENGTH_SHORT).show();
                }
                else{
                    if(searchState){
                        Log.d("MapsActivity", "In search state, we should show product data");

                        BusinessController businessController = new BusinessController();
                        HashMap<String, Object> mp = (HashMap<String, Object>)marker.getTag();
                        businessController.getItemList((String)mp.get("title"),
                                (String)mp.get("businessId"),
                                new BusinessController.GetItemListFromBusiness() {
                                    @Override
                                    public void onGetItemListFromBusiness(ArrayList<ProductModel> productModels) {
                                        showBusinessProductWindow(mp, productModels);
                                    }
                                });
                    }
                    else{
                        showBusinessWindow((HashMap<String, Object>)marker.getTag());
                    }
                }
                return true;
            }
        });

    }


    private void showBusinessWindow(HashMap<String, Object> data) {
        String businessId = (String)data.get("businessId");
        // Start the BusinessInfoScreen activity with the business ID
        Intent intent = new Intent(MapsActivity.this, BusinessDetailsActivity.class);
        intent.putExtra("businessId", businessId);
        startActivity(intent);
    }


    private void showBusinessProductWindow(HashMap<String, Object> tag, ArrayList<ProductModel> productModels) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.product_map_window, null);
        dialogBuilder.setView(dialogView);

        RecyclerView recyclerView = dialogView.findViewById(R.id.recyclerViewMapProduct);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        Button close = dialogView.findViewById(R.id.buttonClose);
        Button businessPage = dialogView.findViewById(R.id.buttonGoToBusiness);

        recyclerView.setLayoutManager(layoutManager);
        ProductMapAdapter productMapAdapter = new ProductMapAdapter(productModels, MapsActivity.this);
        recyclerView.setAdapter(productMapAdapter);


        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        businessPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Move to BusinessDetails screen
                String businessId = (String)tag.get("businessId");
                // Start the BusinessInfoScreen activity with the business ID
                Intent intent = new Intent(MapsActivity.this, BusinessDetailsActivity.class);
                intent.putExtra("businessId", businessId);
                startActivity(intent);
            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }
    private void initializeMap() {
        showCurrentLocation();
        showBusiness();
        searchState = false;
    }

    private void showBusiness() {
        BusinessController businessController = new BusinessController();
        businessController.getAllBusinesses(new BusinessController.GetAllBusinesses() {
            @Override
            public void onGetAllBusinesses(ArrayList<BusinessModel> businessModels) {
                for(BusinessModel businessModel: businessModels){
                    VectorDrawableCompat vectorDrawable = VectorDrawableCompat.create(getResources(), R.drawable.baseline_food_bank_24, null);
                    BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(vectorDrawableToBitmap(vectorDrawable));

                    LatLng currLoc = new LatLng(businessModel.getLocation().getLatitude(),
                            businessModel.getLocation().getLongitude());
                    Marker marker = googleMap.addMarker(new MarkerOptions().position(currLoc).
                            title(businessModel.getFirstName() + " " + businessModel.getLastName())
                            .icon(bitmapDescriptor));
                    Map<String, Object> m = new HashMap<>();
                    m.put("businessId", businessModel.getBusinessId());
                    marker.setTag(m);
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
                VectorDrawableCompat vectorDrawable = VectorDrawableCompat.create(getResources(), R.drawable.baseline_person_pin_circle_24, null);
                BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(vectorDrawableToBitmap(vectorDrawable));
                geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                LatLng currLoc = new LatLng(location.getLatitude(),location.getLongitude());
                googleMap.addMarker(new MarkerOptions().position(currLoc).title("My Location").icon(bitmapDescriptor));

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

    private Bitmap vectorDrawableToBitmap(VectorDrawableCompat vectorDrawable) {
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);
        return bitmap;
    }

    private void processSearchQuery(String searchText) {
        BusinessController businessController = new BusinessController();
        businessController.getBusinessesByProduct(searchText, radius, geoPoint, new BusinessController.GetCloseProductBusiness() {
            @Override
            public void onGetCloseProductBusiness(ArrayList<BusinessModel> businessModels) {
                if(businessModels.size() > 0){
                    VectorDrawableCompat vectorDrawable = VectorDrawableCompat.create(getResources(), R.drawable.baseline_food_bank_24, null);
                    BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(vectorDrawableToBitmap(vectorDrawable));
                    googleMap.clear();
                    showCurrentLocation();
                    searchState = true;
                    for(BusinessModel businessModel: businessModels){
                        LatLng currLoc = new LatLng(businessModel.getLocation().getLatitude(),
                                businessModel.getLocation().getLongitude());
                        Marker marker = googleMap.addMarker(new MarkerOptions().position(currLoc).
                                title(businessModel.getFirstName() + " " + businessModel.getLastName())
                                .icon(bitmapDescriptor));
                        Map<String, Object> m = new HashMap<String, Object>();
                        m.put("businessId", businessModel.getBusinessId());
                        m.put("title", searchText);
                        marker.setTag(m);
                    }
                    Toast.makeText(MapsActivity.this, "Map refreshed according to search", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MapsActivity.this, "Product not found around your area!", Toast.LENGTH_SHORT).show();
                }
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