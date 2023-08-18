package com.example.thelocalplates8.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.example.thelocalplates8.Controllers.OrdersController;
import com.example.thelocalplates8.Models.ProductOrderModel;
import com.example.thelocalplates8.R;
import com.example.thelocalplates8.adapters.ProductOrderAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

public class OrderProductsActivity extends AppCompatActivity {

    private String userId, orderId;
    private RecyclerView recyclerView;
    private ArrayList<ProductOrderModel> products;
    private Map<String, Object> productsMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_products);

        userId = getIntent().getStringExtra("USERID");
        orderId = getIntent().getStringExtra("ORDERID");
        Serializable serializable = getIntent().getSerializableExtra("PRODUCTS");
        if (serializable != null && serializable instanceof Map) {
            // Cast the serializable object to a Map
            productsMap = (Map<String, Object>) serializable;
//            Toast.makeText(this, ""+products.keySet().size(), Toast.LENGTH_SHORT).show();
        }

        initializeViews();

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        OrdersController ordersController = new OrdersController(this);

        ordersController.getOrderProducts(productsMap, new OrdersController.GetProducts() {
            @Override
            public void onGetProducts(ArrayList<ProductOrderModel> productsA) {
                products.addAll(productsA);

                recyclerView.setLayoutManager(layoutManager);
                ProductOrderAdapter productOrderAdapter = new ProductOrderAdapter(products, OrderProductsActivity.this);
                recyclerView.setAdapter(productOrderAdapter);

            }
        });




    }

    private void initializeViews() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewOrderProd);
        products = new ArrayList<ProductOrderModel>();
    }
}