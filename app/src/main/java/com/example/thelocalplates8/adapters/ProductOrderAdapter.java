package com.example.thelocalplates8.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thelocalplates8.Models.ProductOrderModel;
import com.example.thelocalplates8.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProductOrderAdapter extends RecyclerView.Adapter<ProductOrderAdapter.ProductOrderViewHolder>{
    private ArrayList<ProductOrderModel> products;
    private Context context;
    private String userId;

    public ProductOrderAdapter(ArrayList<ProductOrderModel> products, Context context){
        this.context = context;
        this.products = products;

        SharedPreferences sharedPreferences = context.getSharedPreferences("MyUserPrefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("userId", "");
    }

    @NonNull
    @Override
    public ProductOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View productView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycleitem_orders_products, parent, false);
        return new ProductOrderViewHolder(productView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductOrderViewHolder holder, int position) {
        ProductOrderModel currentProduct = products.get(position);
        holder.title.setText(currentProduct.getTitle());
        holder.price.setText(String.valueOf(currentProduct.getTotalPrice()));
        Picasso.get().load(currentProduct.getImageUri()).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }


    public static class ProductOrderViewHolder extends RecyclerView.ViewHolder{
        private TextView title;
        private TextView price;
        private ImageView image;
        private Button productRate;
        private Button businessRate;

        public ProductOrderViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.textView22);
            price = itemView.findViewById(R.id.textView26);
            image = itemView.findViewById(R.id.imageView4);
            productRate = itemView.findViewById(R.id.buttonRateProd);
            businessRate = itemView.findViewById(R.id.buttonRateBusiness);
        }
    }
}
