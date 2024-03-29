package com.example.thelocalplates8.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.thelocalplates8.Controllers.CartController;
import com.example.thelocalplates8.Models.CartItemModel;
import com.example.thelocalplates8.R;
import com.example.thelocalplates8.adapters.CartItemAdapter;

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

        checkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cartController.completeOrder(CartActivity.this, products, new CartController.CheckoutOrder() {
                    @Override
                    public void onCheckoutOrder(boolean ordered) {
                        cartController.emptyTheCart(CartActivity.this, new CartController.EmptyCart() {
                            @Override
                            public void onEmptyCart(boolean isEmpty) {
                                Toast.makeText(CartActivity.this, "Checkout", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });
                    }
                });

            }
        });

    }

    private void initializeVars() {
        checkoutButton = (Button)findViewById(R.id.cheackoutButton);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewCart);
        products = new ArrayList<CartItemModel>();
    }
}