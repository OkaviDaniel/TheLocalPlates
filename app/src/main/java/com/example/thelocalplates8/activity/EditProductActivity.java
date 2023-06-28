package com.example.thelocalplates8.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        ProductController productController = new ProductController();

        Bundle resultIntent = getIntent().getExtras();
        String productId = "";
        if(resultIntent != null)
        {
            productId = resultIntent.getString("prod");
        }

        initializeViews();
        showProductInfo(productId, productController);


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
    }

}