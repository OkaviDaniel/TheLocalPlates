package com.example.thelocalplates8;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.thelocalplates8.Controllers.BusinessController;
import com.example.thelocalplates8.Models.BusinessModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class BusinessActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business);

        TextView welcomeText = (TextView)findViewById(R.id.BusinessScreenTextView);
        welcomeText.setVisibility(View.GONE);
        Button createBusiness = (Button)findViewById(R.id.createBusinessButton);
        createBusiness.setVisibility(View.GONE);

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