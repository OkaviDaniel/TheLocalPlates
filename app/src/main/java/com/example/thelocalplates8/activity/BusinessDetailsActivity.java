package com.example.thelocalplates8.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.example.thelocalplates8.R;
import com.example.thelocalplates8.adapters.OtherBusinessAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class BusinessDetailsActivity extends AppCompatActivity {

    private String businessId;
    private TextView ownerTextView;
    private TextView cityTextView;
    private TextView ratingTextView;
    private TextView mailTextView;
    private TextView phoneTextView;
    private ImageView businessImage;

    private RecyclerView recyclerView;

    private Button chatBtn;

    private ArrayList<ProductModel> productsM;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_details);

        Intent intent = getIntent();
        businessId = intent.getStringExtra("businessId");
        Log.d("BusinessDetailsActivity","Business - " + businessId);

        initializeVariables();

        showBusinessInfo();

        initializeProducts();

        chatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(BusinessDetailsActivity.this, SettingsActivity.class);
                startActivity(intent1);
            }
        });
    }

    private void initializeProducts() {
//        BusinessController businessController = new BusinessController();
        ProductController productController = new ProductController();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
       productController.getBusinessProducts(businessId, new ProductController.GetBusinessProducts() {
           @Override
           public void onGetBusinessProducts(ArrayList<ProductModel> products) {
               productsM.addAll(products);
               recyclerView.setLayoutManager(layoutManager);
               OtherBusinessAdapter otherBusinessAdapter = new OtherBusinessAdapter(products, BusinessDetailsActivity.this);
               recyclerView.setAdapter(otherBusinessAdapter);
           }
       });


    }

    private void showBusinessInfo() {
        BusinessController businessController = new BusinessController();

        businessController.getBusinessById(businessId, new BusinessController.BusinessModelCallback() {
            @Override
            public void onBusinessModelCallback(BusinessModel business) {
                setInfo(business);
                businessController.getBusinessImage(business.getUserId(), BusinessDetailsActivity.this, new BusinessController.BusinessGetImage() {
                    @Override
                    public void onBusinessGetImage(Bitmap bitmap) {
                        businessImage.setImageBitmap(bitmap);
                    }
                });
            }
        });

    }

    private void setInfo(BusinessModel business) {
        ownerTextView.setText(business.getFirstName() + " " + business.getLastName());
        cityTextView.setText(business.getCity());
        ratingTextView.setText(String.valueOf(business.getRating()));
//        mailTextView.setText(business.getEmail());
//        phoneTextView.setText(business.getPhone());
    }

    private void initializeVariables() {
        ownerTextView = (TextView) findViewById(R.id.textViewOwnerB);
        cityTextView = (TextView) findViewById(R.id.textViewLocationB);
        ratingTextView = (TextView) findViewById(R.id.textViewRatingB);
        businessImage = (ImageView) findViewById(R.id.imageViewBusinessDetails);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView2);
        chatBtn = (Button)findViewById(R.id.buttonChatO);
        productsM = new ArrayList<ProductModel>();
    }

}