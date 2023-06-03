package com.example.thelocalplates8.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thelocalplates8.Controllers.CartController;
import com.example.thelocalplates8.Controllers.ProductController;
import com.example.thelocalplates8.Models.ProductModel;
import com.example.thelocalplates8.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProductMapAdapter  extends RecyclerView.Adapter<ProductMapAdapter.ProductViewHolder> {
    private ArrayList<ProductModel> products;

    private Context context;

    public ProductMapAdapter(ArrayList<ProductModel> products, Context context){
        this.products = products;
        this.context = context;
    }

    @NonNull
    @Override
    public ProductMapAdapter.ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View productView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycleitem_product_map, parent, false);
        return new ProductMapAdapter.ProductViewHolder(productView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductMapAdapter.ProductViewHolder holder, int position) {
        ProductModel currentProduct = products.get(position);
        holder.title.setText(currentProduct.getTitle());
        Picasso.get().load(products.get(position).getImageUri()).into(holder.image);
        holder.price.setText(String.valueOf(currentProduct.getPrice()));
        holder.available.setText(String.valueOf(currentProduct.isAvailable()));
        holder.kosher.setText(currentProduct.getKosher());
        holder.addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CartController cartController = new CartController(context);
                cartController.addToCart(products.get(holder.getAdapterPosition()), new CartController.AddProductToCart() {
                    @Override
                    public void onAddProductToCart(boolean added) {

                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder{
        public ImageView image;
        public TextView title;
        public TextView price;
        public TextView kosher;
        public TextView available;
        public Button addToCart;
        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            this.image = itemView.findViewById(R.id.imageViewProductMap);
            this.title = itemView.findViewById(R.id.textViewTitleProductMap);
            this.price = itemView.findViewById(R.id.textViewPriceProductMap);
            this.kosher = itemView.findViewById(R.id.textViewKosherMap);
            this.available = itemView.findViewById(R.id.textViewAvailableMap);
            this.addToCart = itemView.findViewById(R.id.buttonAddToCart);
        }


    }

}
