package com.example.thelocalplates8.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.example.thelocalplates8.Controllers.OrdersController;
import com.example.thelocalplates8.Models.OrderModel;
import com.example.thelocalplates8.R;
import com.example.thelocalplates8.adapters.OrderHistoryAdapter;

import java.util.ArrayList;

public class OrderHistoryActivity extends AppCompatActivity {


    private String userId;
    private RecyclerView recyclerView;
    private ArrayList<OrderModel> myOrders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);
        userId = getIntent().getStringExtra("USERID");

        initializeViews();

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);

        OrdersController ordersController = new OrdersController(this);

        ordersController.getUserOrders(new OrdersController.GetUserOrders() {
            @Override
            public void onGetUserOrders(ArrayList<OrderModel> orders) {
//                Toast.makeText(OrderHistoryActivity.this, "Success", Toast.LENGTH_SHORT).show();
                myOrders.addAll(orders);

                recyclerView.setLayoutManager(layoutManager);
                OrderHistoryAdapter orderHistoryAdapter = new OrderHistoryAdapter(myOrders, OrderHistoryActivity.this);
                recyclerView.setAdapter(orderHistoryAdapter);
            }
        });

    }

    private void initializeViews() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewOrderHistory);
        myOrders = new ArrayList<>();
    }
}