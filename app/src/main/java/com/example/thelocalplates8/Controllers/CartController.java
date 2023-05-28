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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
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
                if(task.isSuccessful()){
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot != null){
                        for (QueryDocumentSnapshot documentSnapshot : querySnapshot){
                            HashMap<String, HashMap<String, Object>> productsMap = (HashMap<String, HashMap<String, Object>>) documentSnapshot.get("products");
                            if (productsMap != null){
                                for (Map.Entry<String, HashMap<String, Object>> entry : productsMap.entrySet()){
                                    String productId = entry.getKey();
                                    Log.d("CartController", "productid = " + productId + "length " + productId.length());
                                    HashMap<String, Object> productData = entry.getValue();

                                    String title = (String) productData.get("title");
                                    double price = Double.parseDouble(String.valueOf(productData.get("price")));
                                    int quantity = Integer.parseInt(String.valueOf(productData.get("quantity")));
                                    CartItemModel cartItemModel = new CartItemModel(productId, quantity, price, title);
                                    ans.add(cartItemModel);
                                }
                                callback.onGetCartProducts(ans);
                            }
                        }
                    }
                }else{
                    callback.onGetCartProducts(ans);
                }
            }
        });
    }


    public void removeProduct(String productId, String userId) {
        Query query = db.collection("shoppingCart").whereEqualTo("clientId", userId);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot != null){
                        for (QueryDocumentSnapshot documentSnapshot : querySnapshot){
                            HashMap<String, HashMap<String, Object>> productsMap = (HashMap<String, HashMap<String, Object>>) documentSnapshot.get("products");
                            productsMap.remove(productId);

                            documentSnapshot.getReference().update("products", productsMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d("CartController", "Product removed");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    e.printStackTrace();
                                    Log.d("CartController", "Product failed to be removed");
                                }
                            });
                        }
                    }
                }
            }
        });
    }

    /**
     * When the user change the quantity in the cart for a specific item
     * @param productId - product id
     * @param userId - user id
     * @param i - Increased or decreased the quantity
     */
    public void updateQuantity(String productId, String userId, int i) {
        Query query = db.collection("shoppingCart").whereEqualTo("clientId", userId);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot != null){
                        for (QueryDocumentSnapshot documentSnapshot : querySnapshot){
                            HashMap<String, HashMap<String, Object>> productsMap = (HashMap<String, HashMap<String, Object>>) documentSnapshot.get("products");
                            HashMap<String, Object> product = productsMap.get(productId);
                            int quantity = Integer.parseInt(String.valueOf(product.get("quantity")));
                            product.put("quantity", quantity+i);
                            productsMap.put(productId, product);
                            documentSnapshot.getReference().update("products", productsMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d("CartController", "Increased or decreased quantity");
                                }
                            });
                        }
                    }
                }
            }
        });
    }

    public interface GetCartProducts{
        void onGetCartProducts(ArrayList<CartItemModel> productModels);
    }
}