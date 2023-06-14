package com.example.thelocalplates8.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;


import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.thelocalplates8.Controllers.CustomerController;
import com.example.thelocalplates8.Models.CategoryModel;
import com.example.thelocalplates8.Models.CultureModel;
import com.example.thelocalplates8.Models.CustomerModel;
import com.example.thelocalplates8.R;
import com.example.thelocalplates8.adapters.CategoryAdapter;
import com.example.thelocalplates8.adapters.CultureAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
      ImageView mapBtn;

      private ImageView profileImage;
      private TextView helloName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar =findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Initialize Firebase Auth
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        mapBtn = (ImageView) findViewById(R.id.imageView5);
        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });

        initializeVariables();
        setUserDataOnScreen();
        recyclerViewCategory();
        recyclerViewCulture();

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

    }

    private void recyclerViewCulture() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        RecyclerView recyclerViewCultureList = findViewById(R.id.view2);
        recyclerViewCultureList.setLayoutManager(linearLayoutManager);

        ArrayList<CultureModel> cultureList = new ArrayList<>();
        cultureList.add(new CultureModel("Asia", "noodles"));
        cultureList.add(new CultureModel("Arab", "falafel"));
        cultureList.add(new CultureModel("French", "french_fries"));
        cultureList.add(new CultureModel("Moroccan", "harira"));
        cultureList.add(new CultureModel("Israel", "wine"));

        CultureAdapter adapter = new CultureAdapter(cultureList);
        recyclerViewCultureList.setAdapter(adapter);
    }

    private void recyclerViewCategory() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        RecyclerView recyclerViewCategoryList = findViewById(R.id.view1);
        recyclerViewCategoryList.setLayoutManager(linearLayoutManager);

        ArrayList<CategoryModel> categoryList = new ArrayList<>();
        categoryList.add(new CategoryModel("Diet", "diet"));
        categoryList.add(new CategoryModel("Pasta", "pasta"));
        categoryList.add(new CategoryModel("Fish", "fish"));
        categoryList.add(new CategoryModel("Burger", "burger"));
        categoryList.add(new CategoryModel("Healthy", "healthy_eating"));
        categoryList.add(new CategoryModel("Soup", "soup"));

        CategoryAdapter adapter = new CategoryAdapter(categoryList);
        recyclerViewCategoryList.setAdapter(adapter);
    }

    private void setUserDataOnScreen() {
        SharedPreferences sp = getSharedPreferences("MyUserPrefs", Context.MODE_PRIVATE);
        String userId = sp.getString("userId", "");

        CustomerController customerController = new CustomerController();
        customerController.checkIfImageExist(userId, new CustomerController.CheckProfileImageExist() {
            @Override
            public void onCheckProfileImageExist(String imageUrl) {
                Picasso.get().load(imageUrl).into(profileImage);
            }
        });
        customerController.getCustomerData(userId, new CustomerController.CustomerModelCallback(){
            @Override
            public void onCustomerModelCallback(CustomerModel customer) {
                helloName.setText("Hi " + customer.getFirstName() + " " + customer.getLastName());
            }
        });
    }

    private void initializeVariables() {
        profileImage = (ImageView) findViewById(R.id.imageViewMainProfile);
        helloName = (TextView) findViewById(R.id.textViewHelloName);
    }

    private void logoutUser() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.Business) {
            // Handle item 1 selection
            Intent intent = new Intent(MainActivity.this, BusinessActivity.class);
            startActivity(intent);
            return true;
        }
        else if (itemId == R.id.cart) {
            // Handle item 2 selection
            Intent intent = new Intent(MainActivity.this, CartActivity.class);
            startActivity(intent);
            return true;
        }
        else if (itemId == R.id.setting) {
            // Handle item 3 selection
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }else if (itemId == R.id.logout) {
            // Handle item 3 selection
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}