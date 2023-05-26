package com.example.thelocalplates8.Controllers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.thelocalplates8.Models.CartItemModel;
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
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CartController {
    private FirebaseFirestore db;
    private SharedPreferences sharedPreferences;
    private Context context;
    private String userId;

    public CartController(){
        this.db = FirebaseFirestore.getInstance();
    }

    public CartController(Context context){
        this.db = FirebaseFirestore.getInstance();
        this.context = context;
        sharedPreferences = context.getSharedPreferences("MyUserPrefs", Context.MODE_PRIVATE);
        this.userId = sharedPreferences.getString("userId", "");
    }


    public void initializeCart() {
        Map<String, Object> shoppingCart = new HashMap<String, Object>();
        shoppingCart.put("products", new ArrayList<HashMap<String,Object>>());
        shoppingCart.put("clientId", userId);

        db.collection("shoppingCart").add(shoppingCart).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d("Cart controller", "Initialized the shopping cart for the current user");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void getItems(final GetCartProducts callback){
        ArrayList<CartItemModel> ans = new ArrayList<CartItemModel>();
        Query query = db.collection("shoppingCart").whereEqualTo("clientId", userId);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot != null && !querySnapshot.isEmpty()){
                        DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);
                        // Document exists, extract the products ArrayList
                        ArrayList<HashMap<String, Object>> productsList = (ArrayList<HashMap<String, Object>>) documentSnapshot.get("products");
                        if (productsList != null){
                            for (HashMap<String, Object> product : productsList) {
                                String productId = (String) product.get("productId");
                                int quantity = Integer.parseInt(String.valueOf(product.get("quantity")));
                                double price = Double.parseDouble(String.valueOf(product.get("price")));
                                String title = (String)product.get("title");
                                CartItemModel cartItemModel = new CartItemModel(productId, quantity, price, title);
                                ans.add(cartItemModel);
                                // Perform operations with productId, quantity, and price
                            }
                            callback.onGetCartProducts(ans);
                        }
                    }
                }
                else{
                    callback.onGetCartProducts(ans);
                }
            }
        });
    }

    public interface GetCartProducts{
        void onGetCartProducts(ArrayList<CartItemModel> productModels);
    }
}
