package com.example.thelocalplates8;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.thelocalplates8.Controllers.BusinessController;
import com.example.thelocalplates8.Controllers.ProductController;
import com.example.thelocalplates8.Models.BusinessModel;
import com.example.thelocalplates8.Models.ProductModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class BusinessActivity extends AppCompatActivity {

    private TextView welcomeText;
    private Button createBusiness;
    private TextView nameTextView;
    private TextView cityTextView;
    private TextView phoneTextView;
    private TextView emailTextView;
    private Button addProductButton;
    private ImageView businessImageView;
    private RecyclerView recyclerView;
    private ArrayList<ProductModel> products;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business);

        initializeVars();
        setVisibilityGone();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String userID = mAuth.getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);

        FirebaseCommunicator.checkBusinessExist(db, userID, new FirebaseCommunicator.OnBusinessCheckCompleteListener(){

            @Override
            public void onBusinessCheckComplete(boolean isBusinessCreated) {
                // if user created a business
                if(isBusinessCreated){
                    // Initialize controllers
                    BusinessController businessController = new BusinessController();
                    ProductController productController = new ProductController();

                    productController.getProducts(BusinessActivity.this, new ProductController.GetProductsInterface() {
                        @Override
                        public void onGetProductsInterface(ArrayList<ProductModel> productModels) {
                            products.addAll(productModels);
//                            Log.d("Products", "number of products" + products.size());
                            recyclerView.setLayoutManager(layoutManager);
                            ProductBusinessAdapter productBusinessAdapter = new ProductBusinessAdapter(products, BusinessActivity.this);
                            recyclerView.setAdapter(productBusinessAdapter);
                        }
                    });

                    businessController.getBusinessData(userID, new BusinessController.BusinessModelCallback() {
                        @Override
                        public void onBusinessModelCallback(BusinessModel business) {
                            setData(business);
                            businessController.getBusinessImage(BusinessActivity.this, new BusinessController.BusinessGetImage() {
                                @Override
                                public void onBusinessGetImage(Bitmap bitmap) {
                                    setVisibilityVisible(bitmap);
                                }
                            });

                            addProductButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(BusinessActivity.this, AddProductActivity.class);
                                    startActivity(intent);
                                }
                            });

                        }
                    });

                }
                else {   // User didn't created a business
                    welcomeText.setText("Do you wish to create a business?");
                    welcomeText.setVisibility(View.VISIBLE);
                    createBusiness.setVisibility(View.VISIBLE);

                    createBusiness.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(BusinessActivity.this, BusinessCreationActivity.class);
                            intent.putExtra("userId", userID);
                            startActivity(intent);
                            finish();
                        }
                    });
                }
            }
        });

    }

    private void setData(BusinessModel business) {
        welcomeText.setText("Welcome back " + business.getFirstName() + " to your business!");
        nameTextView.setText(business.getFirstName() + " " + business.getLastName());
        cityTextView.setText(business.getCity());
        phoneTextView.setText(business.getPhone());
        emailTextView.setText(business.getEmail());
    }

    private void setVisibilityVisible(Bitmap bitmap) {
        businessImageView.setImageBitmap(bitmap);
        businessImageView.setVisibility(View.VISIBLE);
        nameTextView.setVisibility(View.VISIBLE);
        cityTextView.setVisibility(View.VISIBLE);
        phoneTextView.setVisibility(View.VISIBLE);
        emailTextView.setVisibility(View.VISIBLE);
        addProductButton.setVisibility(View.VISIBLE);
        welcomeText.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    private void setVisibilityGone() {
        welcomeText.setVisibility(View.GONE);
        createBusiness.setVisibility(View.GONE);
        nameTextView.setVisibility(View.GONE);
        cityTextView.setVisibility(View.GONE);
        phoneTextView.setVisibility(View.GONE);
        emailTextView.setVisibility(View.GONE);
        addProductButton.setVisibility(View.GONE);
        businessImageView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
    }

    private void initializeVars() {
        welcomeText = (TextView)findViewById(R.id.BusinessScreenTextView);
        createBusiness = (Button)findViewById(R.id.createBusinessButton);
        nameTextView = (TextView)findViewById(R.id.BusinessOwnerName);
        cityTextView = (TextView)findViewById(R.id.BusinessCityTextView);
        phoneTextView = (TextView) findViewById(R.id.BusinessPhoneTextView);
        emailTextView = (TextView) findViewById(R.id.BusinessEmailTextView);
        addProductButton = (Button)findViewById(R.id.AddProductButton);
        businessImageView = (ImageView)findViewById(R.id.BusinessImageView);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview_products);
        products = new ArrayList<ProductModel>();
    }
}