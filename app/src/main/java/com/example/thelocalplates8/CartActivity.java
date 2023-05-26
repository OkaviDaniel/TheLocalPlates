package com.example.thelocalplates8;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Button;

import com.example.thelocalplates8.Controllers.CartController;
import com.example.thelocalplates8.Models.CartItemModel;
import com.example.thelocalplates8.Models.ProductModel;

import java.util.ArrayList;

public class CartActivity extends AppCompatActivity {

    private Button checkoutButton;
    private RecyclerView recyclerView;
    private ArrayList<CartItemModel> products;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        initializeVars();

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);

        CartController cartController = new CartController(this);
        cartController.getItems(new CartController.GetCartProducts() {
            @Override
            public void onGetCartProducts(ArrayList<CartItemModel> productModels) {
                products.addAll(productModels);

                recyclerView.setLayoutManager(layoutManager);
                CartItemAdapter cartItemAdapter = new CartItemAdapter(products, CartActivity.this);
                recyclerView.setAdapter(cartItemAdapter);
            }
        });

    }

    private void initializeVars() {
        checkoutButton = (Button)findViewById(R.id.cheackoutButton);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewCart);
        products = new ArrayList<CartItemModel>();
    }
}