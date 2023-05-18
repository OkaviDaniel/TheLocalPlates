package com.example.thelocalplates8.Controllers;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.thelocalplates8.Models.BusinessModel;
import com.example.thelocalplates8.Models.CustomerModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BusinessController {
    private FirebaseFirestore db;

    public BusinessController(){
        db = FirebaseFirestore.getInstance();
    }

    public void getBusinessData(String userId, final BusinessModelCallback callback){

    }

    public void createBusiness(String firstName, String lastName, String email, String userId, String phone,
                               String city, String destinationLimit, int deliveryCost, String openTime, String closedTime){
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

        db.collection("business").add(business).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d("New user business", "DocumentSnapshot successfully written!");
                HashMap<String, Object> business = new HashMap<>();
                business.put("businessId", documentReference.getId());
                db.collection("customers").document(userId).update(business);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("New user business", "Error writing document", e);
            }
        });
    }

    public interface BusinessModelCallback{
        void onBusinessModelCallback(BusinessModel business);
    }
}
