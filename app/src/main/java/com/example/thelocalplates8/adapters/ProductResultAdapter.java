package com.example.thelocalplates8.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thelocalplates8.Controllers.CartController;
import com.example.thelocalplates8.Models.ProductModel;
import com.example.thelocalplates8.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ProductResultAdapter extends RecyclerView.Adapter<ProductResultAdapter.ProductViewHolder> {

    private List<ProductModel> products;

    private Context context;

    public ProductResultAdapter(List<ProductModel> products, Context context) {
        this.products = products;
        this.context = context;
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
        holder.tvProductPrice.setText(String.format("Price: ₪%.2f", product.getPrice()));
        holder.tvKosher.setText("Kosher: " + product.getKosher());
        holder.tvInventoryAmount.setText("Inventory: " + product.getInventoryAmount());
        holder.tvCulture.setText("Culture: " + product.getCulture());
        holder.addToCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CartController cartController = new CartController(context);
                cartController.addToCart(products.get(holder.getAdapterPosition()), new CartController.AddProductToCart() {
                    @Override
                    public void onAddProductToCart(boolean added) {
                        if(added){
                            Toast.makeText(context,"Product added to your cart :)",Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(context,"The product is already in your cart",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });


    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProductPicture;
        TextView tvProductTitle, tvProductPrice, tvKosher, tvInventoryAmount, tvCulture;
        Button addToCartBtn;


        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProductPicture = itemView.findViewById(R.id.ivProductPicture);
            tvProductTitle = itemView.findViewById(R.id.tvProductTitle);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            tvKosher = itemView.findViewById(R.id.tvKosher);
            tvInventoryAmount = itemView.findViewById(R.id.tvInventoryAmount);
            tvCulture = itemView.findViewById(R.id.tvCulture);
            addToCartBtn = itemView.findViewById(R.id.addToCartBtn);
        }
    }
}