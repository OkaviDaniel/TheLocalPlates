package com.example.thelocalplates8.adapters;

import android.content.Context;
import android.graphics.Color;
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

import java.util.ArrayList;

public class OtherBusinessAdapter extends RecyclerView.Adapter<OtherBusinessAdapter.ProductViewHolder>{

    private ArrayList<ProductModel> products;
    private Context context;

    public OtherBusinessAdapter(ArrayList<ProductModel> products, Context context){
        this.products = products;
        this.context = context;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View productView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycleitem_business_page, parent, false);
        return new ProductViewHolder(productView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        ProductModel currentProduct = products.get(position);
        holder.title.setText(currentProduct.getTitle());
        Picasso.get().load(products.get(position).getImageUri()).into(holder.prodImage);
        holder.price.setText(String.valueOf(currentProduct.getPrice()));
        holder.rating.setText(String.valueOf(currentProduct.getRating()));
        holder.kashrot.setText(currentProduct.getKosher());

        if(currentProduct.getInventoryAmount() == 0){
            holder.addToCart.setEnabled(false);
        }

        if(currentProduct.isGlutenIncluded()){
            holder.glutenFree.setText("No");
            holder.glutenFree.setTextColor(Color.RED);
        }else{
            holder.glutenFree.setText("Yes");
            holder.glutenFree.setTextColor(Color.GREEN);
        }

        holder.addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CartController cartController = new CartController(context);
                cartController.addToCart(currentProduct, new CartController.AddProductToCart() {
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

    public static class ProductViewHolder extends RecyclerView.ViewHolder{

        public TextView title;
        public TextView price;
        public TextView kashrot;
        public TextView rating;
        public TextView glutenFree;
        public Button addToCart;
        public ImageView prodImage;


        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.textViewA1);
            price = itemView.findViewById(R.id.textViewA2);
            rating = itemView.findViewById(R.id.textViewA3);
            kashrot = itemView.findViewById(R.id.textViewA4);
            glutenFree = itemView.findViewById(R.id.textViewA5);
            addToCart = itemView.findViewById(R.id.buttonAddToCartBtn);
            prodImage = itemView.findViewById(R.id.imageView2);
        }
    }


}
