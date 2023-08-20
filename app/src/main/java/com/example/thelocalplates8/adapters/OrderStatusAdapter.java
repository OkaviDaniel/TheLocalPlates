package com.example.thelocalplates8.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thelocalplates8.Controllers.OrderStatusController;
import com.example.thelocalplates8.Controllers.ProductController;
import com.example.thelocalplates8.Models.OrderStatusModel;
import com.example.thelocalplates8.Models.ProductModel;
import com.example.thelocalplates8.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class OrderStatusAdapter extends RecyclerView.Adapter<OrderStatusAdapter.OrderStatusViewHolder>{

    private ArrayList<OrderStatusModel> orders;

    private Context context;

    private String userId;

    public OrderStatusAdapter(ArrayList<OrderStatusModel> orders, Context context){
        this.orders = orders;
        this.context = context;

        SharedPreferences sharedPreferences = context.getSharedPreferences("MyUserPrefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("userId", "");
    }

    @NonNull
    @Override
    public OrderStatusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View orderView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycleitem_open_orders, parent, false);
        return new OrderStatusViewHolder(orderView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderStatusViewHolder holder, int position) {
        OrderStatusModel currentOrder = orders.get(position);
        holder.fullCustomerName.setText(currentOrder.getFirstName() + " " + currentOrder.getLastName());
        holder.productTitle.setText(currentOrder.getTitle());
        holder.amount.setText(String.valueOf(currentOrder.getAmount()));
        holder.totalPrice.setText(String.format("₪%.2f", currentOrder.getTotalPrice()));
        ProductController p = new ProductController();
        p.getProduct(currentOrder.getProductId(), new ProductController.GetProduct() {
            @Override
            public void onGetProduct(ProductModel productModel) {
                Picasso.get().load(productModel.getImageUri()).into(holder.imageView);
            }
        });

    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    private void removeItem(int position) {
        orders.remove(position);
        notifyItemRemoved(position);

        Toast.makeText(context, "A notification sent to the customer.", Toast.LENGTH_SHORT).show();

        OrderStatusController orderStatusController = new OrderStatusController(context);

    }

    public static class OrderStatusViewHolder extends RecyclerView.ViewHolder{

        private TextView fullCustomerName;
        private TextView productTitle;
        private TextView amount;
        private TextView totalPrice;
        private CheckBox isReady;
        private ImageView imageView;
        private OrderStatusAdapter adapter;

        public OrderStatusViewHolder(@NonNull View itemView, OrderStatusAdapter orderStatusAdapter) {
            super(itemView);
            fullCustomerName = itemView.findViewById(R.id.textViewCustomerName);
            productTitle = itemView.findViewById(R.id.textView29);
            amount = itemView.findViewById(R.id.textViewAmountOrdered);
            totalPrice = itemView.findViewById(R.id.textView34);
            isReady = itemView.findViewById(R.id.checkBoxIsReady);
            imageView = itemView.findViewById(R.id.imageView7);
            adapter = orderStatusAdapter;


            isReady.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            // Call a method to remove the item from the cart
                            adapter.removeItem(position);
                        }
                    }
                }
            });
        }
    }


}
