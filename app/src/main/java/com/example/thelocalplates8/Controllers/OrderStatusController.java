package com.example.thelocalplates8.Controllers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.thelocalplates8.Models.CartItemModel;
import com.example.thelocalplates8.Models.OrderModel;
import com.example.thelocalplates8.Models.OrderStatusModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OrderStatusController {
    private FirebaseFirestore db;
    private SharedPreferences sharedPreferences;
    private Context context;
    private String userId;


    public OrderStatusController(){
        this.db = FirebaseFirestore.getInstance();
    }

    public OrderStatusController(Context context){
        this.db = FirebaseFirestore.getInstance();
        this.context = context;
        sharedPreferences = context.getSharedPreferences("MyUserPrefs", Context.MODE_PRIVATE);
        this.userId = sharedPreferences.getString("userId", "");
    }

    public void createOrderStatus(Map<String, Object> order, final CreateOrderStatus callback){
        String customerId = (String)order.get("customerId");
        String orderId= (String)order.get("orderId");


        for(String productId: ((Map<String, Object>)order.get("products")).keySet()){
            OrderStatusModel orderStatusModel = new OrderStatusModel();
            orderStatusModel.setCustomerId(customerId);
            orderStatusModel.setOrderId(orderId);
            orderStatusModel.setProductId(productId);
            CartItemModel productDetails = (CartItemModel) ((Map<String, Object>)order.get("products")).get(productId);
            orderStatusModel.setAmount(productDetails.getQuantity());
            orderStatusModel.setTitle(productDetails.getTitle());
            orderStatusModel.setTotalPrice(productDetails.getTotalPrice());
            orderStatusModel.setReady(false);

            DocumentReference customerRef = db.collection("customers").document(customerId);
            customerRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String firstName = documentSnapshot.getString("firstName");
                    String lastName = documentSnapshot.getString("lastName");

                    orderStatusModel.setFirstName(firstName);
                    orderStatusModel.setLastName(lastName);

                    // Step 2: Query to get businessId from the "products" collection
                    DocumentReference productRef = db.collection("products").document(productId);

                    productRef.get().addOnSuccessListener(documentSnapshot2 -> {
                        if (documentSnapshot2.exists()) {
                            String businessId = documentSnapshot2.getString("businessId");
                            orderStatusModel.setBusinessId(businessId);
                            db.collection("orderstatus").add(orderStatusModel).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    callback.onCreateOrderStatus(true);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    callback.onCreateOrderStatus(false);
                                }
                            });

                        }
                    }).addOnFailureListener(e -> {
                        callback.onCreateOrderStatus(false);
                    });

                }
            }).addOnFailureListener(e -> {
                callback.onCreateOrderStatus(false);
                e.printStackTrace();
            });
        }
    }

    public void getOrders(String businessId, final GetOrders callback){
        ArrayList<OrderStatusModel> orders = new ArrayList<OrderStatusModel>();

        Query query = db.collection("orderstatus")
                .whereEqualTo("businessId", businessId)
                .whereEqualTo("ready", false);

        query.get().addOnSuccessListener(querySnapshot -> {

            for (QueryDocumentSnapshot documentSnapshot : querySnapshot) {
                OrderStatusModel order = documentSnapshot.toObject(OrderStatusModel.class);
                // Add the order to the list
                orders.add(order);
            }
            callback.onGetOrders(orders);
        }).addOnFailureListener(e -> {
            e.printStackTrace();
            callback.onGetOrders(orders);
        });
    }

    public void sentNotification(OrderStatusModel orderStatusModel,  final SendNotification callback){
        String customerId = orderStatusModel.getCustomerId();

        DocumentReference customerRef = db.collection("customers").document(customerId);
        customerRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String token = document.getString("token");
                        // Need to implement
                    } else {
                       callback.onSendNotification(false);
                    }
                } else {
                    Log.d("Firestore", "Error getting document: " + task.getException());
                    callback.onSendNotification(false);
                }
            }
        });

    }

    public interface CreateOrderStatus{
        void onCreateOrderStatus(boolean created);
    }

    public interface GetOrders{
        void onGetOrders(ArrayList<OrderStatusModel> orders);
    }

    public interface SendNotification{
        void onSendNotification(boolean sent);
    }
}
