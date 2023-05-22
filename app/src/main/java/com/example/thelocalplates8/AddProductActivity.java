package com.example.thelocalplates8;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.thelocalplates8.Controllers.ProductController;

public class AddProductActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        EditText productNameEditText = (EditText) findViewById(R.id.editTextProductName);
        EditText productPriceEditText = (EditText) findViewById(R.id.editTextProductPrice);
        EditText productCultureEditText = (EditText) findViewById(R.id.editTextProductCulture);
        EditText kosherEditText = (EditText) findViewById(R.id.editTextKosher);
        EditText preparationTimeEditText = (EditText) findViewById(R.id.editTextPreparationTime);

        Button selectImageButton = (Button) findViewById(R.id.buttonSelectProductImage);
        Button uploadImageButton = (Button) findViewById(R.id.buttonUploadProductImage);
        Button addProductButton = (Button) findViewById(R.id.buttonAddProduct);


        SharedPreferences sp = getSharedPreferences("MyUserPrefs", Context.MODE_PRIVATE);
        String businessId = sp.getString("businessId", "");


        // Need to think about how to upload an image

        addProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Need to implement a way that prevent from the user to create the same product twice.
                String productName = productNameEditText.getText().toString();
                double productPrice = Double.parseDouble(productPriceEditText.getText().toString());
                String productCulture = productCultureEditText.getText().toString();
                String kosher = kosherEditText.getText().toString();
                int preparationTime = Integer.parseInt(preparationTimeEditText.getText().toString());

                ProductController productController = new ProductController();
                productController.addProduct(businessId, productName, productPrice, productCulture,
                        kosher, preparationTime);
                goToBusinessScreen();
            }
        });

    }

    private void goToBusinessScreen() {
        Intent intent = new Intent(this, BusinessActivity.class);
        startActivity(intent);
        finish();
    }
}