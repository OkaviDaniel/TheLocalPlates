package com.example.thelocalplates8.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thelocalplates8.Models.OrderModel;
import com.example.thelocalplates8.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.OrderItemViewHolder>{
    private ArrayList<OrderModel> myOrders;
    private Context context;
    private String userId;

    public OrderHistoryAdapter(ArrayList<OrderModel> orders, Context context){
        this.context = context;
        this.myOrders = orders;

        SharedPreferences sharedPreferences = context.getSharedPreferences("MyUserPrefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("userId", "");
    }

    @NonNull
    @Override
    public OrderItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View productView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycleritem_history_order, parent,false);
        return new OrderItemViewHolder(productView);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderItemViewHolder holder, int position) {
        OrderModel currentOrder = myOrders.get(position);

        SimpleDateFormat formatter = new SimpleDateFormat("dd/M/yyyy hh:mm:ss");
        String strDate = formatter.format(currentOrder.getOrderDate());
        holder.date.setText(strDate);

        holder.totalPrice.setText(String.format("₪%.2f",currentOrder.getTotalOrderPrice()));

        holder.details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(context, "Clicked", Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public int getItemCount() {
        return myOrders.size();
    }

    public static class OrderItemViewHolder extends RecyclerView.ViewHolder{
        private TextView date;
        private TextView totalPrice;
        private Button details;

        public OrderItemViewHolder(@NonNull View itemView) {
            super(itemView);

            date = itemView.findViewById(R.id.textViewOrderDate);
            totalPrice = itemView.findViewById(R.id.textViewOrderTotalPrice);
            details = itemView.findViewById(R.id.buttonOrderDetails);
        }
    }
}
