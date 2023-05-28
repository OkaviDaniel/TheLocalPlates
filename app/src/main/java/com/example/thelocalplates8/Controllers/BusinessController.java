package com.example.thelocalplates8.Controllers;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.thelocalplates8.Models.BusinessModel;
import com.example.thelocalplates8.Models.CustomerModel;
import com.example.thelocalplates8.Models.ProductModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
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
import java.util.Map;

public class BusinessController {
    private FirebaseFirestore db;
    private Context context;
    private SharedPreferences sharedPreferences;

    public BusinessController(){
        db = FirebaseFirestore.getInstance();
    }



    public void getBusinessData(String userId, final BusinessModelCallback callback){
//        DocumentReference docRef = db.collection("business").document(userId);

        Query usersBusiness = db.collection("business").whereEqualTo("userId", userId);
        usersBusiness.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for (DocumentSnapshot document : task.getResult()){

                        BusinessModel businessModel = new BusinessModel();
                        businessModel.setFirstName(document.getString("firstName"));
                        businessModel.setLastName(document.getString("lastName"));
                        businessModel.setEmail(document.getString("email"));
                        businessModel.setCity(document.getString("city"));
                        businessModel.setDeliveryCost(document.getLong("DeliveryCost").intValue());
                        businessModel.setRating(document.getDouble("Rating"));
                        businessModel.setPhone(document.getString("phone"));
                        businessModel.setOpenTime(document.getString("openTime"));
                        businessModel.setClosedTime(document.getString("closedTime"));
                        businessModel.setDestinationLimit(document.getString("DestinationLimit"));
                        businessModel.setLocation(document.getGeoPoint("location"));
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
        business.put("DeliveryCost", deliveryCost);
        business.put("Rating",0);
        business.put("city",city);
        business.put("DestinationLimit",destinationLimit);
        business.put("lastName",lastName);
        business.put("firstName", firstName);
        business.put("ordersTime",""); // maybe to change this!
        business.put("Products",new ArrayList<String>());
        business.put("street","");
        business.put("userId",userId);
        business.put("email",email);
        business.put("openTime", openTime);
        business.put("closedTime",closedTime);

        this.context = context;
        sharedPreferences = context.getSharedPreferences("MyUserPrefs", Context.MODE_PRIVATE);

        db.collection("business").add(business).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d("New user business", "DocumentSnapshot successfully written!");
                HashMap<String, Object> business = new HashMap<>();
                business.put("businessId", documentReference.getId());
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

}
