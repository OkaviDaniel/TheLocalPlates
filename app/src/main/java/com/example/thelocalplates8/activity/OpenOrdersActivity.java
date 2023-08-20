package com.example.thelocalplates8.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.thelocalplates8.Controllers.OrderStatusController;
import com.example.thelocalplates8.Models.OrderModel;
import com.example.thelocalplates8.Models.OrderStatusModel;
import com.example.thelocalplates8.R;
import com.example.thelocalplates8.adapters.OrderStatusAdapter;

import java.util.ArrayList;

public class OpenOrdersActivity extends AppCompatActivity {

    private String businessId;

    private RecyclerView recyclerView;

    private ArrayList<OrderStatusModel> orders;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_orders);

        this.businessId = getIntent().getStringExtra("BUSINESSID");

        initializeVariables();

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        OrderStatusController orderStatusController = new OrderStatusController(this);

        orderStatusController.getOrders(businessId, new OrderStatusController.GetOrders() {
            @Override
            public void onGetOrders(ArrayList<OrderStatusModel> orders2) {

                recyclerView.setLayoutManager(layoutManager);
                OrderStatusAdapter orderStatusAdapter = new OrderStatusAdapter(orders2, OpenOrdersActivity.this);
                recyclerView.setAdapter(orderStatusAdapter);
            }
        });

    }

    private void initializeVariables() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewOpenOrders);
    }
}