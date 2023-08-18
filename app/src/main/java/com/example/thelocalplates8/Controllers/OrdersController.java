package com.example.thelocalplates8.Controllers;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.example.thelocalplates8.Models.OrderModel;
import com.example.thelocalplates8.Models.ProductModel;
import com.example.thelocalplates8.Models.ProductOrderModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class OrdersController {

    private FirebaseFirestore db;
    private SharedPreferences sharedPreferences;
    private Context context;
    private String userId;

    public OrdersController(){
        this.db = FirebaseFirestore.getInstance();
    }

    public OrdersController(Context context){
        this.db = FirebaseFirestore.getInstance();
        this.context = context;
        sharedPreferences = context.getSharedPreferences("MyUserPrefs", Context.MODE_PRIVATE);
        this.userId = sharedPreferences.getString("userId", "");
    }

    public void getUserOrders(final GetUserOrders callback){
        ArrayList<OrderModel> ans = new ArrayList<>();
        Query query = db.collection("orders").whereEqualTo("customerId", userId);
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                    OrderModel temp = documentSnapshot.toObject(OrderModel.class);
                    ans.add(temp);
                }
                callback.onGetUserOrders(ans);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                callback.onGetUserOrders(ans);
            }
        });
    }

    public void getOrderProducts(Map<String, Object> productsMap, final GetProducts callback){
        ArrayList<ProductOrderModel> ans = new ArrayList<ProductOrderModel>();
        ProductController productController = new ProductController();
        productController.getProductsByIds(new ArrayList<>(productsMap.keySet()), new ProductController.GetProductsByIds() {
            @Override
            public void onGetProductsByIds(ArrayList<ProductModel> products) {
                for(ProductModel pm: products){
                    Map<String, Object> currentPM = (Map<String,Object>)(productsMap.get(pm.getProductId()));
                    ProductOrderModel temp = new ProductOrderModel();
                    temp.setTitle(pm.getTitle());
                    temp.setBusinessId(pm.getBusinessId());
                    temp.setPrice((double)currentPM.get("price"));
                    temp.setQuantity((long)currentPM.get("quantity"));
                    temp.setTotalPrice((double)currentPM.get("totalPrice"));
                    temp.setImageUri(pm.getImageUri());

                    ans.add(temp);
                }
                callback.onGetProducts(ans);
            }
        });

    }

    public interface GetUserOrders{
        void onGetUserOrders(ArrayList<OrderModel> orders);
    }

    public interface GetProducts{
        void onGetProducts(ArrayList<ProductOrderModel> products);
    }
}
