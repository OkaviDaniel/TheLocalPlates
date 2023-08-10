package com.example.thelocalplates8.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thelocalplates8.Models.ProductModel;
import com.example.thelocalplates8.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ProductResultAdapter extends RecyclerView.Adapter<ProductResultAdapter.ProductViewHolder> {

    private List<ProductModel> products;

    public ProductResultAdapter(List<ProductModel> products) {
        this.products = products;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product2, parent, false);
        return new ProductViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        ProductModel product = products.get(position);
        Picasso.get().load(products.get(position).getImageUri()).into(holder.ivProductPicture);
        holder.tvProductTitle.setText(product.getTitle());
        holder.tvProductPrice.setText(String.format("Price: $%.2f", product.getPrice()));
        holder.tvKosher.setText("Kosher: " + product.getKosher());
        holder.tvInventoryAmount.setText("Inventory: " + product.getInventoryAmount());
        holder.tvCulture.setText("Culture: " + product.getCulture());


    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProductPicture;
        TextView tvProductTitle, tvProductPrice, tvKosher, tvInventoryAmount, tvCulture;


        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProductPicture = itemView.findViewById(R.id.ivProductPicture);
            tvProductTitle = itemView.findViewById(R.id.tvProductTitle);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            tvKosher = itemView.findViewById(R.id.tvKosher);
            tvInventoryAmount = itemView.findViewById(R.id.tvInventoryAmount);
            tvCulture = itemView.findViewById(R.id.tvCulture);
        }
    }
}