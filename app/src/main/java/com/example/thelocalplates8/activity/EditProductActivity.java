package com.example.thelocalplates8.activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.thelocalplates8.Controllers.ProductController;
import com.example.thelocalplates8.Models.ProductModel;
import com.example.thelocalplates8.R;
import com.squareup.picasso.Picasso;

public class EditProductActivity extends AppCompatActivity {

    private ImageView image;
    private Button chooseImageBtn;
    private EditText title;
    private EditText culture;
    private EditText inventoryAmount;
    private EditText preparationTime;
    private EditText kosher;
    private EditText ingredients;
    private EditText price;
    private EditText description;
    private Button saveBtn;
    private String productId = "";

    private Uri imageUri = null;

    private String[] kashrotList = {"Badatz", "Mehadrin", "Beit Yosef", "None", "Other"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        ProductController productController = new ProductController();

        Bundle resultIntent = getIntent().getExtras();

        if(resultIntent != null)
        {
            productId = resultIntent.getString("prod");
        }

        initializeViews();
        showProductInfo(productId, productController);
        chooseImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double price2 = Double.parseDouble(price.getText().toString());
                double inventoryAmount2 = Integer.parseInt(inventoryAmount.getText().toString());

                productController.updateProduct(productId,
                        title.getText().toString(),
                        culture.getText().toString(),
                        inventoryAmount2,
                        preparationTime.getText().toString(),
                        kosher.getText().toString(),
                        ingredients.getText().toString(),
                        price2,
                        description.getText().toString(), new ProductController.UpdateProduct() {
                            @Override
                            public void onUpdateProduct(boolean updated) {
                                if(updated == true && imageUri != null){
                                    productController.uploadImage(productId,
                                            imageUri, EditProductActivity.this,
                                            new ProductController.UploadProductImage() {
                                        @Override
                                        public void onUploadProductImage(Boolean uploaded) {
                                            finishActivity();
                                        }
                                    });
                                }else{
                                    finishActivity();
                                }
                            }
                        });
            }
        });
    }

    private void finishActivity(){
        Toast.makeText(EditProductActivity.this, "Product updated!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(EditProductActivity.this, BusinessActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(EditProductActivity.this, BusinessActivity.class);
        startActivity(intent);
        finish();
    }

    private void showProductInfo(String productId, ProductController productController) {
        productController.getProduct(productId, new ProductController.GetProduct() {
            @Override
            public void onGetProduct(ProductModel productModel) {
                title.setText(productModel.getTitle());
                culture.setText(productModel.getCulture());
                inventoryAmount.setText(String.valueOf(productModel.getInventoryAmount()));
                preparationTime.setText(productModel.getPreparationTime());
                kosher.setText(productModel.getKosher());
                ingredients.setText(productModel.getIngredients());
                price.setText(String.valueOf(productModel.getPrice()));
                description.setText(productModel.getDescription());
                Picasso.get().load(productModel.getImageUri()).into(image);
            }
        });
    }

    private void initializeViews() {
        image = (ImageView) findViewById(R.id.imageViewEditProd);
        chooseImageBtn = (Button) findViewById(R.id.buttonChooseImage);
        title = (EditText) findViewById(R.id.editTextProdTitle);
        culture = (EditText) findViewById(R.id.editTextProdCulture);
        inventoryAmount = (EditText) findViewById(R.id.editTextInventoryAmount);
        preparationTime = (EditText) findViewById(R.id.editTextPreparationTime2);
        kosher = (EditText) findViewById(R.id.editTextProdKosher);
        ingredients = (EditText) findViewById(R.id.editTextProdIngredients);
        price = (EditText) findViewById(R.id.editTextProdPrice);
        description = (EditText) findViewById(R.id.editTextProdDescription);
        saveBtn = (Button) findViewById(R.id.buttonSaveProdUpdate);
    }


    private void selectImage() {
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
                        Picasso.get().load(data.getData()).into(image);
                    }
                }
            });
}