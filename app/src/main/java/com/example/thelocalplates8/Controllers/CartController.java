package com.example.thelocalplates8.Controllers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log; 
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.thelocalplates8.Models.CartItemModel;
import com.example.thelocalplates8.Models.ProductModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
        shoppingCart.put("products", new HashMap<String,HashMap<String,Object>>());
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

    public void getItems2(final GetCartProducts callback){
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

    public void getItems(final GetCartProducts callback) {
        ArrayList<CartItemModel> ans = new ArrayList<CartItemModel>();
        Query query = db.collection("shoppingCart").whereEqualTo("clientId", userId);
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshot) {
                List<Task<?>> tasks = new ArrayList<>();
                for (QueryDocumentSnapshot documentSnapshot : querySnapshot) {
                    HashMap<String, Object> documentData = (HashMap<String, Object>) documentSnapshot.getData();
                    if (documentData != null && documentData.containsKey("products")) {
                        HashMap<String, HashMap<String, Object>> productsMap = (HashMap<String, HashMap<String, Object>>) documentData.get("products");
                        if (productsMap != null) {
                            for (Map.Entry<String, HashMap<String, Object>> entry : productsMap.entrySet()) {
                                String productId = entry.getKey();
                                HashMap<String, Object> productData = entry.getValue();
                                int quantity = Integer.parseInt(String.valueOf(productData.get("quantity")));

                                DocumentReference productRef = FirebaseFirestore.getInstance().collection("products").document(productId);
                                Task<DocumentSnapshot> task = productRef.get();
                                tasks.add(task);
                                task.addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        if(documentSnapshot.exists()){
                                            String title = documentSnapshot.getString("title");
                                            double price = documentSnapshot.getDouble("price");
                                            CartItemModel cartItemModel = new CartItemModel(productId, quantity, price, title);
                                            ans.add(cartItemModel);
                                        }else{
                                            //delete the doc
                                            Log.d("CartController", "Need to remove prod");
                                        }
                                    }
                                });
                            }
                        }
                    }
                }

                Tasks.whenAllSuccess(tasks).addOnSuccessListener(new OnSuccessListener<List<Object>>() {
                    @Override
                    public void onSuccess(List<Object> results) {
                        callback.onGetCartProducts(ans);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                callback.onGetCartProducts(ans);
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
                                    Toast.makeText(context, "Product removed", Toast.LENGTH_SHORT).show();
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

    public void addToCart(ProductModel productModel, final AddProductToCart callback){
        Query query = db.collection("shoppingCart").whereEqualTo("clientId", userId);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot != null){
                        for (QueryDocumentSnapshot documentSnapshot : querySnapshot){
                            HashMap<String, HashMap<String, Object>> productsMap = (HashMap<String, HashMap<String, Object>>) documentSnapshot.get("products");
                            HashMap<String, Object> prodInfo = new HashMap<String, Object>();
                            prodInfo.put("price", productModel.getPrice());
                            prodInfo.put("quantity", 1);
                            prodInfo.put("title", productModel.getTitle());
                            productsMap.put(productModel.getProductId(), prodInfo);
                            documentSnapshot.getReference().update("products", productsMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    callback.onAddProductToCart(true);
                                }
                            });
                        }
                    }
                }else{

                }
            }
        });
    }

    public void emptyTheCart(Context context, final EmptyCart callback){
        SharedPreferences sp = context.getSharedPreferences("MyUserPrefs", Context.MODE_PRIVATE);
        String userId = sp.getString("userId", "");

        CollectionReference cartCollectionRef = FirebaseFirestore.getInstance().collection("shoppingCart");

        Query query = cartCollectionRef.whereEqualTo("clientId", userId);

        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshot) {
                for (QueryDocumentSnapshot documentSnapshot : querySnapshot) {
                    // Get the reference to the document with the matching userId
                    DocumentReference cartRef = cartCollectionRef.document(documentSnapshot.getId());

                    // Create an empty HashMap to set the "products" field
                    Map<String, Object> emptyProductsMap = new HashMap<>();

                    // Update the shoppingCart document with the empty "products" field
                    cartRef.update("products", emptyProductsMap)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    callback.onEmptyCart(true);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e("Firestore", "Error updating shopping cart", e);
                                    callback.onEmptyCart(false);
                                }
                            });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Firestore", "Error querying shopping cart", e);
                callback.onEmptyCart(false);
            }
        });
    }

    public void completeOrder(Context context, ArrayList<CartItemModel> products, final CheckoutOrder callback){
        SharedPreferences sp = context.getSharedPreferences("MyUserPrefs", Context.MODE_PRIVATE);
        String userId = sp.getString("userId", "");

        DocumentReference orderRef = FirebaseFirestore.getInstance().collection("orders").document();
        Map<String, Object> order = new HashMap<String, Object>();
        order.put("customerId", userId);
        order.put("orderDate", new Date());
        order.put("orderId", orderRef.getId());

        double totalPrice = 0;
        Map<String, Object> products2 = new HashMap<String, Object>();

        for(CartItemModel c: products){
            products2.put(c.getProductId(), c);
            totalPrice += c.getTotalPrice();
        }
        order.put("products", products2);
        order.put("totalOrderPrice", totalPrice);
        orderRef.set(order).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        callback.onCheckoutOrder(true);
                        createOrderStatus(order);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Firestore", "Error adding order", e);
                    }
                });

    }

    private void createOrderStatus(Map<String, Object> order) {
        OrderStatusController orderStatusController = new OrderStatusController(context);
        orderStatusController.createOrderStatus(order, new OrderStatusController.CreateOrderStatus() {
            @Override
            public void onCreateOrderStatus(boolean created) {

            }
        });

    }

    public interface GetCartProducts{
        void onGetCartProducts(ArrayList<CartItemModel> productModels);
    }

    public interface AddProductToCart{
        void onAddProductToCart(boolean added);
    }

    public interface CheckoutOrder{
        void onCheckoutOrder(boolean ordered);
    }

    public interface EmptyCart{
        void onEmptyCart(boolean isEmpty);
    }
}
