package com.example.thelocalplates8.Controllers;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ProductController {

    private FirebaseFirestore db;
    public ProductController(){
        db = FirebaseFirestore.getInstance();
    }


    public void addProduct(String businessId, String productName, double productPrice, String productCulture, String kosher, int preparationTime) {
        Map<String, Object> product = new HashMap<>();
        product.put("businessId", businessId);
        product.put("productName", productName);
        product.put("productPrice", productPrice);
        product.put("productCulture", productCulture);
        product.put("kosher", kosher);
        product.put("preparationTime", preparationTime);

        db.collection("products").add(product).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d("New product", "DocumentSnapshot successfully written!");

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("New product", "Error writing document", e);
            }
        });

    }
}
