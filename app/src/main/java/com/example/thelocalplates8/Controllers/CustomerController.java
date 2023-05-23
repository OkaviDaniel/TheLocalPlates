package com.example.thelocalplates8.Controllers;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.example.thelocalplates8.Models.CustomerModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;

public class CustomerController {
    private FirebaseFirestore db;

    public CustomerController(){
        db = FirebaseFirestore.getInstance();
    }

    public void getCustomerData(String userId, final CustomerModelCallback callback){
        DocumentReference docRef = db.collection("customers").document(userId);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()){
                        CustomerModel customer = new CustomerModel();
                        customer.setFirstName(document.getString("firstName"));
                        customer.setLastName(document.getString("lastName"));
                        customer.setEmail(document.getString("email"));
                        customer.setCity(document.getString("city"));
                        customer.setPhone(document.getString("phone"));
                        if(document.contains("businessId")){
                            customer.setBusinessId(document.getString("businessId"));
                        }
                        callback.onCustomerModelCallback(customer);
                    }
                }
            }
        });
    }

    public void uploadImage(Uri imageUri, Context context, final UploadProfileImage callback){

        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Uploading file..");
        progressDialog.show();

        SharedPreferences sp = context.getSharedPreferences("MyUserPrefs", Context.MODE_PRIVATE);
        String userId = sp.getString("userId", "");
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("images/"+userId+"/profile");
        storageReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                callback.onUploadProfileImage(true);
                if(progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                callback.onUploadProfileImage(false);
                if(progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
            }
        });
    }

    public void checkIfImageExist(String uid, final CheckProfileImageExist callback){
        String imagePath = "images/"+uid+"/profile";
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child(imagePath);

        StorageReference storageReference = FirebaseStorage.getInstance().getReference("images/"+uid+"/profile");
        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Handle the success case
                String imageUrl = uri.toString();
                callback.onCheckProfileImageExist(imageUrl);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                callback.onCheckProfileImageExist("");
            }
        });
    }

    public interface CustomerModelCallback{
        void onCustomerModelCallback(CustomerModel customer);
    }

    public interface UploadProfileImage{
        void onUploadProfileImage(Boolean uploaded);
    }

    public interface CheckProfileImageExist{
        void onCheckProfileImageExist(String imageUrl);
    }
}
