package com.example.thelocalplates8.Controllers;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.example.thelocalplates8.LocationManager;
import com.example.thelocalplates8.Models.BusinessModel;
import com.example.thelocalplates8.Models.CustomerModel;
import com.example.thelocalplates8.Models.ProductModel;
import com.example.thelocalplates8.activity.BusinessDetailsActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BusinessController {
    private FirebaseFirestore db;
    private Context context;
    private SharedPreferences sharedPreferences;

    public BusinessController(){
        db = FirebaseFirestore.getInstance();
    }

    public void getBusinessData(String userId, final BusinessModelCallback callback){

        Query usersBusiness = db.collection("business").whereEqualTo("userId", userId);
        usersBusiness.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for (DocumentSnapshot document : task.getResult()){
                        BusinessModel businessModel = document.toObject(BusinessModel.class);
                        callback.onBusinessModelCallback(businessModel);
                    }
                }
            }
        });
    }

    public void getBusinessById(String businessId, final BusinessModelCallback callback){

        Query usersBusiness = db.collection("business").whereEqualTo("businessId", businessId);
        usersBusiness.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for (DocumentSnapshot document : task.getResult()){
                        BusinessModel businessModel = document.toObject(BusinessModel.class);
                        callback.onBusinessModelCallback(businessModel);
                    }
                }
            }
        });
    }

    public void uploadImage(Uri imageUri, Context context, final BusinessUploadImage callback){
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Uploading file..");
        progressDialog.show();

        SharedPreferences sp = context.getSharedPreferences("MyUserPrefs", Context.MODE_PRIVATE);
        String userId = sp.getString("userId", "");
        Log.d("Add product screen", "UserId is: " + userId);

        StorageReference storageReference = FirebaseStorage.getInstance().getReference("images/"+userId+"/business");
        storageReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(context, "Successfully uploaded!", Toast.LENGTH_SHORT).show();
                callback.onBusinessUploadImage(true);
                if(progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Failed to upload!", Toast.LENGTH_SHORT).show();
                callback.onBusinessUploadImage(false);
                if(progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
            }
        });
    }


    /**
     * Creating a new business for the first time
     * @param firstName first name of the user
     * @param lastName last name of the user
     * @param email email of the user
     * @param userId the user ID
     * @param phone the phone number of the user
     * @param city city that the user lives in
     * @param destinationLimit the farthest city the user can deliver to
     * @param deliveryCost how much money the delivery cost
     * @param openTime open time
     * @param closedTime closed time
     */
    public void createBusiness(String firstName, String lastName, String email, String userId, String phone,
                               String city, String destinationLimit, int deliveryCost, String openTime, String closedTime, Context context){
        Map<String, Object> business = new HashMap<>();
        business.put("phone", phone);
        business.put("deliveryCost", deliveryCost);
        business.put("rating",0);
        business.put("city",city);
        business.put("destinationLimit",destinationLimit);
        business.put("lastName",lastName);
        business.put("firstName", firstName);
        business.put("ordersTime",""); // maybe to change this!
        business.put("street","");
        business.put("userId",userId);
        business.put("email",email);
        business.put("openTime", openTime);
        business.put("closedTime",closedTime);

        LocationManager locationManager = new LocationManager(context);
        LiveData<Location> currentLocationLiveData = locationManager.getGeoPoint();
        this.context = context;
        sharedPreferences = context.getSharedPreferences("MyUserPrefs", Context.MODE_PRIVATE);

        currentLocationLiveData.observe((LifecycleOwner) context, new Observer<Location>() {
            @Override
            public void onChanged(Location location) {
                // Do something with the location (e.g., convert it to a GeoPoint and save it)
                if (location != null) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    GeoPoint geoPoint = new GeoPoint(latitude, longitude);
                    business.put("location", geoPoint);
                    db.collection("business").add(business).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d("New user business", "DocumentSnapshot successfully written!");
                            HashMap<String, Object> business = new HashMap<>();
                            business.put("businessId", documentReference.getId());
                            db.collection("business").document(documentReference.getId()).update(business); // Check if it's good!!
                            business.put("city", city);
                            business.put("phone", phone);
                            db.collection("customers").document(userId).update(business);


                            // Here we save the businessId in the local storage
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("businessId", documentReference.getId());
                            editor.apply();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("New user business", "Error writing document", e);
                        }
                    });

                }
            }
        });
    }

    public void getBusinessImage(Context context, final BusinessGetImage callback){

        SharedPreferences sp = context.getSharedPreferences("MyUserPrefs", Context.MODE_PRIVATE);
        String userId = sp.getString("userId", "");

        StorageReference storageReference = FirebaseStorage.getInstance().getReference("images/"+userId+"/business");

        try{
            File localFile = File.createTempFile("tempfile", ".jpeg");
            storageReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    callback.onBusinessGetImage(bitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    e.printStackTrace();
                }
            });
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void getAllBusinesses(final GetAllBusinesses callback){
        ArrayList<BusinessModel> businessList = new ArrayList<>();
        db.collection("business")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            BusinessModel business = documentSnapshot.toObject(BusinessModel.class);
                            businessList.add(business);
                        }
                        callback.onGetAllBusinesses(businessList);
                    }
                });
    }

    public void getBusinessesByProduct(String productTitle, int radius,
                                       GeoPoint geoPoint,  final GetCloseProductBusiness callback){
        CollectionReference productsRef = db.collection("products");
        CollectionReference businessesRef = db.collection("business");

        ArrayList<BusinessModel> businessesWithinRadius = new ArrayList<>();

        Query productsQuery = productsRef.whereEqualTo("title", productTitle);
        // we can go over all the businesses around the radius and get extract the title and preform contains.
        // but for now let's just check if they equals
        productsQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    List<String> matchingBusinessIds = new ArrayList<>();

                    for (QueryDocumentSnapshot productDoc : task.getResult()) {
                        String businessId = productDoc.getString("businessId");
                        matchingBusinessIds.add(businessId);
                    }

//                    Log.d("BusinessController", "Size of equals title = " + matchingBusinessIds.size());
//                    for(String s: matchingBusinessIds){
//                        Log.d("BusinessController", "BusinessId =  " + s);
//                    }
                    if(matchingBusinessIds.size() == 0){
                        callback.onGetCloseProductBusiness(businessesWithinRadius);
                        return;
                    }
                    Query businessesQuery = businessesRef.whereIn("businessId", matchingBusinessIds);
                    businessesQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
//                                Log.d("BusinessController", "IN " + task.getResult().size());
                                for (QueryDocumentSnapshot businessDoc : task.getResult()){
                                    BusinessModel businessModel = businessDoc.toObject(BusinessModel.class);

                                    GeoPoint location = businessModel.getLocation();

                                    // Calculate the distance between the user's location and the business location
                                    float[] distanceResults = new float[1];
                                    Location.distanceBetween(geoPoint.getLatitude(), geoPoint.getLongitude(),
                                            location.getLatitude(), location.getLongitude(), distanceResults);
                                    float distance = distanceResults[0];
//                                    Log.d("BusinessController", "distance = " + distance + " radius = " + radius);
                                    if (distance <= radius) {
                                        businessesWithinRadius.add(businessModel);
                                    }
                                }
                                callback.onGetCloseProductBusiness(businessesWithinRadius);
                            }else{
                                callback.onGetCloseProductBusiness(businessesWithinRadius);
                            }
                        }
                    });
                }
            }
        });
    }


    public void getItemList(String title, String businessId, final GetItemListFromBusiness callback){
        CollectionReference productsRef = db.collection("products");
        Query query = productsRef.whereEqualTo("title", title)
                .whereEqualTo("businessId", businessId);
        ArrayList<ProductModel> productModels = new ArrayList<ProductModel>();
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot != null){
                        for (DocumentSnapshot document : querySnapshot.getDocuments()){
                            ProductModel productModel = document.toObject(ProductModel.class);
                            productModels.add(productModel);
                        }
                        callback.onGetItemListFromBusiness(productModels);
                    }
                }else{
                    callback.onGetItemListFromBusiness(productModels);
                }
            }
        });
    }

    public void getBusinessImage(String userId, BusinessDetailsActivity businessDetailsActivity, final BusinessGetImage callback) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("images/"+userId+"/business");

        try{
            File localFile = File.createTempFile("tempfile", ".jpeg");
            storageReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    callback.onBusinessGetImage(bitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    e.printStackTrace();
                }
            });
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public interface BusinessModelCallback{
        void onBusinessModelCallback(BusinessModel business);
    }

    public interface BusinessUploadImage{
        void onBusinessUploadImage(Boolean uploaded);
    }
    public interface BusinessGetImage {
        void onBusinessGetImage(Bitmap bitmap);
    }

    public interface GetAllBusinesses{
        void onGetAllBusinesses(ArrayList<BusinessModel> businessModels);
    }

    public interface GetCloseProductBusiness{
        void onGetCloseProductBusiness(ArrayList<BusinessModel> businessModels);
    }

    public interface GetItemListFromBusiness{
        void onGetItemListFromBusiness(ArrayList<ProductModel> productModels);
    }

}
