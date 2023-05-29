package com.example.thelocalplates8.Controllers;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
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
        product.put("title", productName);
        product.put("price", productPrice);
        product.put("culture", productCulture);
        product.put("kosher", kosher);
        product.put("preparationTime", preparationTime);
        product.put("ingredients", ingredients);
        product.put("available", false);
        product.put("inventoryAmount", 0);
        product.put("rating", 0);
        product.put("description", "");
        db.collection("products").add(product).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d("New product", "DocumentSnapshot successfully written!");
                db.collection("products").document(documentReference.getId()).update("productId", documentReference.getId());
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
                taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        String imageUri = uri.toString();
                        Map<String, Object> imageMap = new HashMap<String, Object>();
                        imageMap.put("imageUri", imageUri);
                        db.collection("products").document(productId).update(imageMap);
                        Toast.makeText(context, "Successfully uploaded!", Toast.LENGTH_SHORT).show();
                        callback.onUploadProductImage(true);
                        if(progressDialog.isShowing()){
                            progressDialog.dismiss();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Successfully uploaded!", Toast.LENGTH_SHORT).show();
                        callback.onUploadProductImage(true);
                        if(progressDialog.isShowing()){
                            progressDialog.dismiss();
                        }
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Failed to upload!", Toast.LENGTH_SHORT).show();
                callback.onUploadProductImage(false);
                if(progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
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
                        ProductModel productModel = documentSnapshot.toObject(ProductModel.class);
                        productModel.setProductId(documentSnapshot.getId());
                        products.add(productModel);
                    }
                    callback.onGetProductsInterface(products);
                }
            }
        });
    }

    public void getImage(String productId, Context context, final GetProductImage callback){
//        Log.d("ProductController", "" + productId.length());
        SharedPreferences sp = context.getSharedPreferences("MyUserPrefs", Context.MODE_PRIVATE);
        String userId = sp.getString("userId", "");

        StorageReference storageReference = FirebaseStorage.getInstance().getReference("images/"+userId+"/Products/"+productId);

        try{
            File localFile = File.createTempFile("tempfile", ".jpeg");
            storageReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    callback.onGetProductImage(bitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void getProduct(String productId, final GetProduct callback){
        ProductModel productModel = new ProductModel();
        CollectionReference collection = db.collection("products");
        collection.document(productId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()){
//                        Map<String, Object> productMap = document.getData();
//                        productModel.setTitle((String)productMap.get("title"));
//                        productModel.setCulture((String)productMap.get("culture"));
//                        productModel.setPrice(Integer.parseInt((String)productMap.get("price")));
//                        productModel.setAvailable((boolean)productMap.get("available") );
//                        productModel.setKosher((String)productMap.get("kosher"));
//                        productModel.setInventoryAmount(Integer.parseInt((String)productMap.get("inventoryAmount")));
//                        productModel.setPreparationTime((String)productMap.get("preparationTime"));
//                        productModel.setRating(Integer.parseInt((String)productMap.get("rating")));
//                        productModel.setIngredients((String)productMap.get("ingredients"));
                        ProductModel productModel = document.toObject(ProductModel.class);

                        callback.onGetProduct(productModel);
                    }
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

    public interface GetProductImage{
        void onGetProductImage(Bitmap bitmap);
    }

    public interface GetProduct{
        void onGetProduct(ProductModel productModel);
    }


}
