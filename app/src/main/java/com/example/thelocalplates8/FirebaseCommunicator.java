package com.example.thelocalplates8;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FirebaseCommunicator {
    private String name;
    private String lastName;
    private String email;

    public FirebaseCommunicator(){

    }
    public FirebaseCommunicator(String name, String lastName, String email){
        this.name=name;
        this.lastName = lastName;
        this.email=email;
    }
    public  void initializeNewUser(FirebaseFirestore db, String userId){
//        initializeCartDocument(db, userId);           // Initialize the shopping cart document for this user
          initializeCustomersDocument(db,userId);       // Initialize the customer document for this user
//          initializeBusinessDocument(db,userId);          // Initialize the Business screen, it's only for testing
    }

    private void initializeCartDocument(FirebaseFirestore db, String userId){
        Map<String, Object> user = new HashMap<>();
        user.put("customerId", userId);
        user.put("Products", new ArrayList<>());
        db.collection("shoppingCart").document(userId)
                .set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("New user cart", "DocumentSnapshot successfully written!");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("New user cart", "Error writing document", e);
                    }
                });
    }


    private  void initializeCustomersDocument(FirebaseFirestore db, String userId){
        Map<String, Object> user = new HashMap<>();
//        user.put("businessId", "");
        user.put("city", "");
        user.put("lastName", lastName);
        user.put("firstName", name);
        user.put("phone", "");
        user.put("email", email);

        db.collection("customers").document(userId)
                .set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("New user customer", "DocumentSnapshot successfully written!");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("New user customer", "Error writing document", e);
                    }
                });
    }
/*
    private void initializeBusinessDocument(FirebaseFirestore db, String userId) {
        Map<String, Object> business = new HashMap<>();
        business.put("DeliveryCost", 0); // done
        business.put("Rating",0); // done
        business.put("city",""); // done
        business.put("DestinationLimit",""); // done
        business.put("lastName",lastName); // done
        business.put("firstName",name); // done
        business.put("ordersTime",""); // done
        business.put("phone",""); // done
        business.put("Products",new ArrayList<String>()); // done
        business.put("street",""); // done
        business.put("userId",userId); // done
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
*/
    public static void checkBusinessExist(FirebaseFirestore db, String userId, OnBusinessCheckCompleteListener listener){

        DocumentReference userRef = db.collection("customers").document(userId);

        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists() && documentSnapshot.contains("businessId")){
                            String businessId = (String)documentSnapshot.get("businessId");
                            listener.onBusinessCheckComplete(businessId);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        listener.onBusinessCheckComplete("");
                    }
                });
    }

    public interface OnBusinessCheckCompleteListener {
        void onBusinessCheckComplete(String businessId);
    }


    public  void createBusiness(FirebaseFirestore db, String userId, String phone, String city, String destinationLimit, String deliveryCost,
                                      String openTime, String closedTime){
        Map<String, Object> business = new HashMap<>();
        business.put("phone", phone);
        business.put("DeliveryCost", Integer.parseInt(deliveryCost));
        business.put("Rating",0);
        business.put("city",city);
        business.put("DestinationLimit",destinationLimit);
        business.put("lastName",lastName);
        business.put("firstName",name);
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

}
