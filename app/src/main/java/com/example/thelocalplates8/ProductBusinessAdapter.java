package com.example.thelocalplates8;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thelocalplates8.Models.ProductModel;

import java.util.ArrayList;

public class ProductBusinessAdapter{

    private ArrayList<ProductModel> productModels;
    public ProductBusinessAdapter(ArrayList<ProductModel> products) {
        this.productModels = products;
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder{
        public TextView title;
        public TextView price;
        public Button edit;
        public Button remove;
        public ImageView uri;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
