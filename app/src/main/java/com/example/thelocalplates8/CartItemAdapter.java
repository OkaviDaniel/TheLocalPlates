package com.example.thelocalplates8;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thelocalplates8.Controllers.CartController;
import com.example.thelocalplates8.Controllers.ProductController;
import com.example.thelocalplates8.Models.CartItemModel;
import com.example.thelocalplates8.Models.ProductModel;

import java.util.ArrayList;

public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.CartItemViewHolder>{

    private ArrayList<CartItemModel> products;
    private Context context;
    private String userId;

    public CartItemAdapter(ArrayList<CartItemModel> products, Context context){
        this.products = products;
        this.context = context;

        SharedPreferences sharedPreferences = context.getSharedPreferences("MyUserPrefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("userId", "");
    }

    @NonNull
    @Override
    public CartItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View productView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycleitem_cartitem, parent, false);
        return new CartItemViewHolder(productView, this);
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
    }
    private  void removeItem(int position) {
        String productId = products.get(position).getProductId();

        products.remove(position);
        notifyItemRemoved(position);

        CartController cartController = new CartController();
        cartController.removeProduct(productId, userId);
    }

    private void decrease(int position) {
        String productId = products.get(position).getProductId();
        int quantity = products.get(position).getQuantity();

        if(quantity <=1 ){
            return;
        }
        products.get(position).setQuantity(quantity-1);
        products.get(position).setTotalPrice((quantity - 1)*products.get(position).getPrice());
        notifyItemChanged(position);

        // Need to update the FireStore
        CartController cartController = new CartController();
        cartController.updateQuantity(productId, userId, -1);
    }


    private void getDetails(int position) {
        String productId = products.get(position).getProductId();
        ProductController productController = new ProductController();
        productController.getProduct(productId, userId, new ProductController.GetProduct() {
            @Override
            public void onGetProduct(ProductModel productModel) {

            }
        });
    }

    private void increase(int position) {
        String productId = products.get(position).getProductId();
        int quantity = products.get(position).getQuantity();
        products.get(position).setQuantity(quantity+1);
        products.get(position).setTotalPrice((quantity + 1)*products.get(position).getPrice());
        notifyItemChanged(position);

        // Need to update the FireStore
        CartController cartController = new CartController();
        cartController.updateQuantity(productId, userId, 1);
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

        private CartItemAdapter adapter;

        public CartItemViewHolder(@NonNull View itemView, CartItemAdapter adapter) {
            super(itemView);
            this.adapter = adapter;
            image = itemView.findViewById(R.id.imageViewProductCart);
            title = itemView.findViewById(R.id.productNameCartTextView);
            productPrice = itemView.findViewById(R.id.cartItemPrice);
            totalPrice = itemView.findViewById(R.id.cartItemTotalPrice);
            quantity = itemView.findViewById(R.id.cartProductQuantity);
            decrease = itemView.findViewById(R.id.decreaseButton);
            increase = itemView.findViewById(R.id.increaseButtonCart);
            remove = itemView.findViewById(R.id.removeButton);

            decrease.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        adapter.decrease(position);
                    }
                }
            });

            increase.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        adapter.increase(position);
                    }
                }
            });

            remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        // Call a method to remove the item from the cart
                        adapter.removeItem(position);
                    }
                }
            });

            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        // Call a method to remove the item from the cart
                        adapter.getDetails(position);
                    }
                }
            });

        }
    }
}
