package com.example.thelocalplates8.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.thelocalplates8.Controllers.BusinessController;
import com.example.thelocalplates8.Models.BusinessModel;
import com.example.thelocalplates8.R;
import com.squareup.picasso.Picasso;

public class BusinessDetailsActivity extends AppCompatActivity {

    private String businessId;
    private TextView ownerTextView;
    private TextView cityTextView;
    private TextView ratingTextView;
    private TextView mailTextView;
    private TextView phoneTextView;
    private ImageView businessImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_details);

        Intent intent = getIntent();
        businessId = intent.getStringExtra("businessId");
        Log.d("BusinessDetailsActivity","Business - " + businessId);

        initializeVariables();

        showBusinessInfo();
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
        mailTextView.setText(business.getEmail());
        phoneTextView.setText(business.getPhone());
    }

    private void initializeVariables() {
        ownerTextView = (TextView) findViewById(R.id.textViewOwnerB);
        cityTextView = (TextView) findViewById(R.id.textViewLocationB);
        ratingTextView = (TextView) findViewById(R.id.textViewRatingB);
        mailTextView = (TextView) findViewById(R.id.textViewMailB);
        phoneTextView = (TextView) findViewById(R.id.textViewPhoneB);
        businessImage = (ImageView) findViewById(R.id.imageViewBusinessDetails);
    }

}