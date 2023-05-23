package com.example.thelocalplates8.Controllers;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.thelocalplates8.AddProductActivity;
import com.example.thelocalplates8.Models.ProductModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProductController {

    private FirebaseFirestore db;
    private StorageReference storageReference;
    private ProgressDialog progressDialog;
    public ProductController(){
        db = FirebaseFirestore.getInstance();
    }


    public void addProduct(String businessId, String productName, double productPrice, String productCulture, String kosher, String preparationTime, String ingredients, final ProductIdInterface callback) {
        Map<String, Object> product = new HashMap<>();
        product.put("businessId", businessId);
        product.put("productName", productName);
        product.put("productPrice", productPrice);
        product.put("productCulture", productCulture);
        product.put("kosher", kosher);
        product.put("preparationTime", preparationTime);
        product.put("ingredients", ingredients);

        db.collection("products").add(product).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d("New product", "DocumentSnapshot successfully written!");
                callback.onProductIdInterface(documentReference.getId());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("New product", "Error writing document", e);
            }
        });
    }

    public void uploadImage(String productId, Uri imageUri, Context context, final UploadProductImage callback) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Uploading file..");
        progressDialog.show();


        SharedPreferences sp = context.getSharedPreferences("MyUserPrefs", Context.MODE_PRIVATE);
        String userId = sp.getString("userId", "");
        Log.d("Add product screen", "UserId is: " + userId);

        storageReference = FirebaseStorage.getInstance().getReference("images/"+userId+"/Products/"+productId);
        storageReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Task<Uri> downloadUriTask = taskSnapshot.getStorage().getDownloadUrl();
                downloadUriTask.addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        HashMap<String, Object> imageUriHashMap = new HashMap<String, Object>();
                        imageUriHashMap.put("imageUri", uri.toString());
                        db.collection("products").document(productId).update(imageUriHashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(context, "Successfully uploaded!", Toast.LENGTH_SHORT).show();
                                callback.onUploadProductImage(true);
                                if(progressDialog.isShowing()){
                                    progressDialog.dismiss();
                                }
                            }
                        });
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                HashMap<String, Object> imageUriHashMap = new HashMap<String, Object>();
                imageUriHashMap.put("imageUri", "");
                db.collection("products").document(productId).update(imageUriHashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(context, "Failed to upload!", Toast.LENGTH_SHORT).show();
                        callback.onUploadProductImage(false);
                        if(progressDialog.isShowing()){
                            progressDialog.dismiss();
                        }
                    }
                });
            }
        });
    }

    public void getProducts(Context context, final GetProductsInterface callback){
        ArrayList<ProductModel> products = new ArrayList<ProductModel>();
        ProductController productController = new ProductController();

        SharedPreferences sp = context.getSharedPreferences("MyUserPrefs", Context.MODE_PRIVATE);
        String businessId = sp.getString("businessId", "");

        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("products");
        Query query = db.collection("products").whereEqualTo("businessId", businessId);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot documentSnapshot: task.getResult()){
                        Log.d("CHECKKKK!!", "CHECKKKKKKKKKKKKKKKKKKKKKKKKKK");
                        ProductModel productModel = documentSnapshot.toObject(ProductModel.class);
                        products.add(productModel);
                    }
                    callback.onGetProductsInterface(products);
                }
            }
        });
    }


    public interface ProductIdInterface{
        void onProductIdInterface(String productId);
    }

    public interface UploadProductImage{
        void onUploadProductImage(Boolean uploaded);
    }

    public interface GetProductsInterface{
         void onGetProductsInterface(ArrayList<ProductModel> productModels);
    }


}
