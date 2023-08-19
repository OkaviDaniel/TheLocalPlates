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


    public void addProduct(String businessId, String productName, double productPrice, String productCulture, String kosher, String preparationTime, String ingredients, String productCategory, boolean glutenIncluded, final ProductIdInterface callback) {
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
        product.put("category", productCategory);
        product.put("glutenIncluded", glutenIncluded);
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
                        Toast.makeText(context, "Successfully failed!", Toast.LENGTH_SHORT).show();
                        callback.onUploadProductImage(false);
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
                        ProductModel productModel = document.toObject(ProductModel.class);
                        callback.onGetProduct(productModel);
                    }
                }
            }
        });

    }

    public void updateProduct(String productId, String title, String culture,
                              double inventoryAmount, String preparationTime,
                              String kosher, String ingredients,
                              double price, String description, final UpdateProduct callback) {

        CollectionReference collection = db.collection("products");
        Map<String, Object> updated = new HashMap<String, Object>();

        updated.put("culture", culture);
        updated.put("description", description);
        updated.put("ingredients", ingredients);
        updated.put("inventoryAmount", inventoryAmount);
        if(inventoryAmount > 0 ){
            updated.put("available", true);
        }else{
            updated.put("available", false);
        }
        updated.put("kosher", kosher);
        updated.put("preparationTime", preparationTime);
        updated.put("price", price);
        updated.put("title", title);

        collection.document(productId).update(updated).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                callback.onUpdateProduct(true);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
                callback.onUpdateProduct(false);
            }
        });
    }

    public void deleteProduct(String productId, final DeleteProduct callback){
        DocumentReference docRef = FirebaseFirestore.getInstance().collection("products").document(productId);
        docRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                callback.onDeleteProduct(true);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Firestore", "Error deleting document", e);
                callback.onDeleteProduct(false);
            }
        });
    }

    public void getBusinessProducts(String businessId, final GetBusinessProducts callback){
        ArrayList<ProductModel> products = new ArrayList<ProductModel>();
        ProductController productController = new ProductController();

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
                    callback.onGetBusinessProducts(products);
                }
            }
        });
    }

    public void getRandomProduct(final PickRandomProduct callback){
        CollectionReference productsCollectionRef = FirebaseFirestore.getInstance().collection("products");

        productsCollectionRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshot) {
                ArrayList<DocumentSnapshot> filteredProducts = new ArrayList<>();

                for (DocumentSnapshot productDoc : querySnapshot.getDocuments()) {
                    // Check if the "glutenIncluded" field is false
                    ProductModel productModel = productDoc.toObject(ProductModel.class);

                    boolean glutenIncluded = productModel.isGlutenIncluded();
                    int amount = productModel.getInventoryAmount();
                    if (!glutenIncluded && amount > 0) {
                        filteredProducts.add(productDoc);
                    }
                }

                int totalFilteredProducts = filteredProducts.size();

                if (totalFilteredProducts > 0) {
                    int randomIndex = (int) (Math.random() * totalFilteredProducts);
                    DocumentSnapshot randomProductDoc = filteredProducts.get(randomIndex);
                    ProductModel productModel = randomProductDoc.toObject(ProductModel.class);
                    callback.onPickRandomProduct(productModel);
                } else {
                    callback.onPickRandomProduct(null);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle the error if the query fails
                Log.e("Firestore", "Error querying products", e);
            }
        });
    }

    public void getProductsByIds(ArrayList<String> ids, final GetProductsByIds callback){
        // Get a reference to the "products" collection
        CollectionReference productsCollectionRef = FirebaseFirestore.getInstance().collection("products");

// Query for documents where the product ID is in the list of IDs
        Query query = productsCollectionRef.whereIn("productId", ids);
        ArrayList<ProductModel> ans = new ArrayList<ProductModel>();

        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshot) {
                for (QueryDocumentSnapshot documentSnapshot : querySnapshot) {
                    // LATER TO CHECK IF DOCUMENTSNAPSHOT EXISTS
                    ProductModel productModel = documentSnapshot.toObject(ProductModel.class);
                    productModel.setProductId(documentSnapshot.getId());
                    ans.add(productModel);
                }
                callback.onGetProductsByIds(ans);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle the error if the query fails
                Log.e("Firestore", "Error querying products", e);
            }
        });
    }

    public Task<QuerySnapshot> getProductsByTitle(String title) {
        return db.collection("products")
                .whereEqualTo("title", title)
                .get();
    }

    public Task<QuerySnapshot> getProductsByCategory(String category) {
        return db.collection("products")
                .whereEqualTo("category", category)
                .get();
    }
    public Task<QuerySnapshot> getProductsByCulture(String culture) {
        return db.collection("products")
                .whereEqualTo("culture", culture)
                .get();
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

    public interface UpdateProduct{
        void onUpdateProduct(boolean updated);
    }

    public interface DeleteProduct{
        void onDeleteProduct(boolean removed);
    }

    public interface GetBusinessProducts{
        void onGetBusinessProducts(ArrayList<ProductModel> products);
    }

    public interface GetProductsByIds{
        void onGetProductsByIds(ArrayList<ProductModel> products);
    }

    public interface PickRandomProduct{
        void onPickRandomProduct(ProductModel productModel);
    }

}
