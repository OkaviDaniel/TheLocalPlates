package com.example.thelocalplates8.Controllers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.thelocalplates8.Models.ProductOrderModel;
import com.example.thelocalplates8.Models.RatingBusinessModel;
import com.example.thelocalplates8.Models.RatingProductModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class RatingController {
    private FirebaseFirestore db;
    private SharedPreferences sharedPreferences;
    private Context context;
    private String userId, firstName, lastName;

    public RatingController(){
        this.db = FirebaseFirestore.getInstance();
    }

    public RatingController(Context context){
        this.db = FirebaseFirestore.getInstance();
        this.context = context;
        sharedPreferences = context.getSharedPreferences("MyUserPrefs", Context.MODE_PRIVATE);
        this.userId = sharedPreferences.getString("userId", "");
        this.firstName = sharedPreferences.getString("userFirstName", "");
        this.lastName = sharedPreferences.getString("userLastName", "");
    }

    public void checkIfRatedBusiness(String businessId, final CheckIfRatedBusiness callback){
        CollectionReference ratingsCollectionRef = FirebaseFirestore.getInstance().collection("businessRatings");
        Query query = ratingsCollectionRef.whereEqualTo("customerId", userId).whereEqualTo("businessId", businessId);
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshot) {
                if (!querySnapshot.isEmpty()) {
                    // The customer has rated the business
                    for (QueryDocumentSnapshot documentSnapshot : querySnapshot){
                        RatingBusinessModel ratingBusinessModel = documentSnapshot.toObject(RatingBusinessModel.class);
                        callback.onCheckIfRatedBusiness(ratingBusinessModel);
                    }

                } else {
                    // The customer has not rated the business
                    callback.onCheckIfRatedBusiness(null);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Firestore", "Error querying ratings", e);
                callback.onCheckIfRatedBusiness(null); // check this if it's okay
            }
        });
    }

    public void checkIfRatedProduct(String productId, final CheckIfRatedProduct callback){
        CollectionReference ratingsCollectionRef = FirebaseFirestore.getInstance().collection("productsRatings");
        Query query = ratingsCollectionRef.whereEqualTo("customerId", userId).whereEqualTo("productId", productId);
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshot) {
                if (!querySnapshot.isEmpty()) {
                    // The customer has rated the product
                    callback.onCheckIfRatedProduct(true);
                } else {
                    // The customer has not rated the product
                    callback.onCheckIfRatedProduct(false);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Firestore", "Error querying ratings", e);
            }
        });
    }

    public void rateProduct(ProductOrderModel product, double rating, final RateProduct callback){
        DocumentReference orderRef = FirebaseFirestore.getInstance().collection("productsRatings").document();
        Map<String, Object> productRating = new HashMap<String, Object>();
        productRating.put("businessId", product.getBusinessId());
        productRating.put("productId", product.getProductId());
        productRating.put("rating", rating);
        productRating.put("customerId", userId);

        orderRef.set(productRating).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                callback.onRateProduct(true);
                updateProductRating(product.getProductId(), product.getBusinessId());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                callback.onRateProduct(false);
            }
        });
    }

    private void updateProductRating(String productId, String businessId) {
        CollectionReference ratingsCollectionRef = FirebaseFirestore.getInstance().collection("productsRatings");
        Query query = ratingsCollectionRef.whereEqualTo("productId", productId);

        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int numberOfVote = 0;
                double ratings = 0;
                double ans = 0;

                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                    RatingProductModel ratingProductModel = documentSnapshot.toObject(RatingProductModel.class);
                    numberOfVote += 1;
                    ratings += ratingProductModel.getRating();
                }
                ans = (double)ratings / numberOfVote;

                // update the rating in the product
                DocumentReference productRef = FirebaseFirestore.getInstance().collection("products").document(productId);
                productRef.update("rating", ans)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("Firestore", "Error updating product rating", e);
                            }
                        });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    public void rateBusiness(String businessId, double rating, String review, final RateBusiness callback){
        DocumentReference orderRef = FirebaseFirestore.getInstance().collection("businessRatings").document();
        Map<String, Object> businessRating = new HashMap<String, Object>();
        businessRating.put("businessId", businessId);
        businessRating.put("rating", rating);
        businessRating.put("customerId", userId);
        businessRating.put("firstName", firstName);
        businessRating.put("lastName",lastName);
        businessRating.put("review", review);

        orderRef.set(businessRating).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                callback.onRateBusiness(true);
                updateBusinessRating(businessId);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                callback.onRateBusiness(false);
            }
        });

    }

    private void updateBusinessRating(String businessId) {
        CollectionReference ratingsCollectionRef = FirebaseFirestore.getInstance().collection("businessRatings");
        Query query = ratingsCollectionRef.whereEqualTo("businessId", businessId);

        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int numberOfVote = 0;
                double ratings = 0;
                double ans = 0;

                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                    RatingBusinessModel ratingBusinessModel = documentSnapshot.toObject(RatingBusinessModel.class);
                    numberOfVote += 1;
                    ratings += ratingBusinessModel.getRating();
                }
                ans = (double)ratings / numberOfVote;

                DocumentReference productRef = FirebaseFirestore.getInstance().collection("business").document(businessId);
                productRef.update("rating", ans)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    public interface CheckIfRatedProduct{
        void onCheckIfRatedProduct(boolean rated);
    }
    public interface CheckIfRatedBusiness{
        void onCheckIfRatedBusiness(RatingBusinessModel ratingBusinessModel);
    }

    public interface RateProduct{
        void onRateProduct(boolean rated);
    }
    public interface RateBusiness{
        void onRateBusiness(boolean rated);
    }


}
