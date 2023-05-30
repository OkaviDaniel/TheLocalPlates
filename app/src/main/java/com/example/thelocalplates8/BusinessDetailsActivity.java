package com.example.thelocalplates8;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class BusinessDetailsActivity extends AppCompatActivity {

    private String businessId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_details);

        Intent intent = getIntent();
        businessId = intent.getStringExtra("businessId");


    }
}