package com.example.thelocalplates8;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.thelocalplates8.Controllers.BusinessController;
import com.example.thelocalplates8.Models.BusinessModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

public class BusinessActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business);

        TextView welcomeText = (TextView)findViewById(R.id.BusinessScreenTextView);
        welcomeText.setVisibility(View.GONE);
        Button createBusiness = (Button)findViewById(R.id.createBusinessButton);
        createBusiness.setVisibility(View.GONE);
        TextView nameTextView = (TextView)findViewById(R.id.BusinessOwnerName);
        nameTextView.setVisibility(View.GONE);
        TextView cityTextView = (TextView)findViewById(R.id.BusinessCityTextView);
        cityTextView.setVisibility(View.GONE);
        TextView phoneTextView = (TextView) findViewById(R.id.BusinessPhoneTextView);
        phoneTextView.setVisibility(View.GONE);
        TextView emailTextView = (TextView) findViewById(R.id.BusinessEmailTextView);
        emailTextView.setVisibility(View.GONE);
        Button addProductButton = (Button)findViewById(R.id.AddProductButton);
        addProductButton.setVisibility(View.GONE);
        ImageView businessImageView = (ImageView)findViewById(R.id.BusinessImageView);
        businessImageView.setVisibility(View.GONE);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String userID = mAuth.getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();



        FirebaseCommunicator.checkBusinessExist(db, userID, new FirebaseCommunicator.OnBusinessCheckCompleteListener(){

            @Override
            public void onBusinessCheckComplete(boolean isBusinessCreated) {
                if(isBusinessCreated){  // if user created a business
                    BusinessController businessController = new BusinessController();
                    businessController.getBusinessData(userID, new BusinessController.BusinessModelCallback() {
                        @Override
                        public void onBusinessModelCallback(BusinessModel business) {
                            welcomeText.setText("Welcome back " + business.getFirstName() + " to your business!");
                            welcomeText.setVisibility(View.VISIBLE);
                            nameTextView.setText(business.getFirstName() + " " + business.getLastName());
                            cityTextView.setText(business.getCity());
                            phoneTextView.setText(business.getPhone());
                            emailTextView.setText(business.getEmail());

                            nameTextView.setVisibility(View.VISIBLE);
                            cityTextView.setVisibility(View.VISIBLE);
                            phoneTextView.setVisibility(View.VISIBLE);
                            emailTextView.setVisibility(View.VISIBLE);
                            businessImageView.setVisibility(View.VISIBLE);


                            addProductButton.setVisibility(View.VISIBLE);
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
}