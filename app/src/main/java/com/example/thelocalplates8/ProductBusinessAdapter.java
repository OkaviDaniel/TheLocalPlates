package com.example.thelocalplates8;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thelocalplates8.Controllers.ProductController;
import com.example.thelocalplates8.Models.ProductModel;

import java.util.ArrayList;

public class ProductBusinessAdapter extends RecyclerView.Adapter<ProductBusinessAdapter.ProductViewHolder> {

    private ArrayList<ProductModel> products;
    private Context context;
    public ProductBusinessAdapter(ArrayList<ProductModel> products, Context context) {

        this.products = products;
        this.context = context;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View productView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycleitem_product, parent, false);
        return new ProductViewHolder(productView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {

        ProductModel currentProduct = products.get(position);
        holder.title.setText(currentProduct.getTitle());

        ProductController p = new ProductController();
        p.getImage(currentProduct.getProductId(), context, new ProductController.GetProductImage() {
            @Override
            public void onGetProductImage(Bitmap bitmap) {
                holder.image.setImageBitmap(bitmap);
            }
        });
        holder.price.setText(String.valueOf(currentProduct.getPrice()));
        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
        public Button edit;
        public Button remove;
        public ImageView image;


        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.textview_productTitle);
            price = itemView.findViewById(R.id.textViewPrice);
            edit = itemView.findViewById(R.id.buttonEditProduct);
            remove = itemView.findViewById(R.id.buttonDeleteProduct);
            image = itemView.findViewById(R.id.imageViewProduct);
        }
    }
}
