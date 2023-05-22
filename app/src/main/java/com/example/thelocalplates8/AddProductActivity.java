package com.example.thelocalplates8;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import com.example.thelocalplates8.Controllers.ProductController;


public class AddProductActivity extends AppCompatActivity {

    private ImageView imageView;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        EditText productNameEditText = (EditText) findViewById(R.id.editTextProductName);
        EditText productPriceEditText = (EditText) findViewById(R.id.editTextProductPrice);
        EditText productCultureEditText = (EditText) findViewById(R.id.editTextProductCulture);
        EditText kosherEditText = (EditText) findViewById(R.id.editTextKosher);
        EditText preparationTimeEditText = (EditText) findViewById(R.id.editTextPreparationTime);
        EditText ingredientsEditText = (EditText) findViewById(R.id.contains);

        Button selectImageButton = (Button) findViewById(R.id.buttonSelectProductImage);
        Button addProductButton = (Button) findViewById(R.id.buttonAddProduct);
        imageView = (ImageView) findViewById(R.id.imageViewProduct);



        SharedPreferences sp = getSharedPreferences("MyUserPrefs", Context.MODE_PRIVATE);
        String businessId = sp.getString("businessId", "");


        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        // Need to think about how to upload an image

        addProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Need to implement a way that prevent from the user to create the same product twice.
                String productName = productNameEditText.getText().toString();
                double productPrice = Double.parseDouble(productPriceEditText.getText().toString());
                String productCulture = productCultureEditText.getText().toString();
                String kosher = kosherEditText.getText().toString();
                String preparationTime = preparationTimeEditText.getText().toString();
                String ingredients = ingredientsEditText.getText().toString();

                ProductController productController = new ProductController();
                productController.addProduct(businessId, productName, productPrice, productCulture,
                        kosher, preparationTime, ingredients, new ProductController.ProductIdInterface() {
                            @Override
                            public void onProductIdInterface(String productId) {
                                Log.d("Add product", "adding product!");
                                productController.uploadImage(productId, imageUri, AddProductActivity.this, new ProductController.UploadProductImage() {
                                    @Override
                                    public void onUploadProductImage(Boolean uploaded) {
                                        if(uploaded){
                                                goToBusinessScreen();
                                        }
                                    }
                                });
                            }
                        });
            }
        });

    }

    private void goToBusinessScreen() {
        Intent intent = new Intent(this, BusinessActivity.class);
        startActivity(intent);
        finish();
    }

    private void selectImage(){
        Intent intent = new Intent();
        intent.setType("image/");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        onActivityResultLauncher.launch(intent);
    }

    ActivityResultLauncher<Intent> onActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        imageUri = data.getData();
                        imageView.setImageURI(imageUri);
                    }
                }
            });
}