package com.example.thelocalplates8;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.thelocalplates8.Controllers.BusinessController;
import com.example.thelocalplates8.Controllers.CustomerController;
import com.example.thelocalplates8.Models.CustomerModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class BusinessCreationActivity extends AppCompatActivity implements CustomerController.CustomerModelCallback {

    // User's basic data
    private String Firstname;
    private String lastName;
    private String email;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_creation);

        EditText phoneNumber = (EditText) findViewById(R.id.editTextPhoneBusiness);
        EditText cityText = (EditText) findViewById(R.id.editTextTextCityBusiness);
        EditText destinationLimit = (EditText) findViewById(R.id.editTextTextDestinationLimit);
        EditText deliveryCost = (EditText) findViewById(R.id.editTextDeliveryCost);
        EditText openTimeText = (EditText) findViewById(R.id.editTextOpenTime);
        EditText closedTimeText = (EditText) findViewById(R.id.editTextClosedTime);

        Button createBusiness = (Button) findViewById(R.id.createBsButton);

        Intent intent = getIntent();
        String userId = intent.getStringExtra("userId");

        // Get the user's name, lastName and Email
        CustomerController customerController = new CustomerController();
        Log.d("BusinessCreationActivity", "UserId = " + userId);
        customerController.getCustomerData(userId, this);


        createBusiness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // need to create a function that checks for invalid cases!
                String phone = phoneNumber.getText().toString();
                String city = cityText.getText().toString();
                String dstLimit = destinationLimit.getText().toString();
                int dvct = Integer.parseInt(deliveryCost.getText().toString());
                String openTime = openTimeText.getText().toString();
                String closedTime = closedTimeText.getText().toString();
                // email
                // firstName
                // lastName
                // userId
                BusinessController businessController = new BusinessController();
                businessController.createBusiness(Firstname, lastName, email, userId, phone, city, dstLimit, dvct, openTime,closedTime);

                goToBusinessScreen();

            }
        });
    }

    private void goToBusinessScreen() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onCustomerModelCallback(CustomerModel customer) {
        // set the user's data
        this.Firstname = customer.getFirstName();
        this.lastName = customer.getLastName();
        this.email =  customer.getEmail();

    }
}