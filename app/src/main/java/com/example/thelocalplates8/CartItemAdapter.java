package com.example.thelocalplates8;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thelocalplates8.Controllers.ProductController;
import com.example.thelocalplates8.Models.CartItemModel;

import java.util.ArrayList;

public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.CartItemViewHolder>{

    private ArrayList<CartItemModel> products;
    private Context context;

    public CartItemAdapter(ArrayList<CartItemModel> products, Context context){
        this.products = products;
        this.context = context;
    }

    @NonNull
    @Override
    public CartItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View productView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycleitem_cartitem, parent, false);
        return new CartItemViewHolder(productView);
    }

    @Override
    public void onBindViewHolder(@NonNull CartItemViewHolder holder, int position) {
        CartItemModel currentProduct = products.get(position);
        holder.title.setText(currentProduct.getTitle());
        ProductController p = new ProductController();

        p.getImage(currentProduct.getProductId(), context, new ProductController.GetProductImage() {
            @Override
            public void onGetProductImage(Bitmap bitmap) {
                holder.image.setImageBitmap(bitmap);
            }
        });
        holder.productPrice.setText(Double.toString(currentProduct.getPrice()));
        holder.totalPrice.setText(Double.toString(currentProduct.getTotalPrice()));
        holder.quantity.setText(Integer.toString(currentProduct.getQuantity()));
        holder.decrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        holder.increase.setOnClickListener(new View.OnClickListener() {
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


    public static class CartItemViewHolder extends RecyclerView.ViewHolder{
        public  ImageView image;
        public TextView title;
        public TextView productPrice;
        public TextView totalPrice;
        public TextView quantity;
        public ImageButton decrease;
        public ImageButton increase;
        public ImageButton remove;

        public CartItemViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.imageViewProductCart);
            title = itemView.findViewById(R.id.productNameCartTextView);
            productPrice = itemView.findViewById(R.id.cartItemPrice);
            totalPrice = itemView.findViewById(R.id.cartItemTotalPrice);
            quantity = itemView.findViewById(R.id.cartProductQuantity);
            decrease = itemView.findViewById(R.id.decreaseButton);
            increase = itemView.findViewById(R.id.increaseButtonCart);
            remove = itemView.findViewById(R.id.removeButton);
        }
    }
}
