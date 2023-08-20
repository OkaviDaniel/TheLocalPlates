package com.example.thelocalplates8.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
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
import com.example.thelocalplates8.R;
import com.squareup.picasso.Picasso;

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
        p.getProduct(currentProduct.getProductId(), new ProductController.GetProduct() {
            @Override
            public void onGetProduct(ProductModel productModel) {
                Picasso.get().load(productModel.getImageUri()).into(holder.image);
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

        CartController cartController = new CartController(context);
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

        productController.getProduct(productId, new ProductController.GetProduct() {
            @Override
            public void onGetProduct(ProductModel productModel) {
                openProductDetailsWindow(productModel);
            }
        });
    }

    private void openProductDetailsWindow(ProductModel productModel) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.product_details_window, null);
        dialogBuilder.setView(dialogView);

        // Find and set the product details in the dialog views
        TextView title = dialogView.findViewById(R.id.textViewTitleP);
        TextView culture = dialogView.findViewById(R.id.textViewCultureP);
        TextView price = dialogView.findViewById(R.id.textViewPriceP);
        TextView kosher = dialogView.findViewById(R.id.textViewKosherP);
        TextView available = dialogView.findViewById(R.id.textViewAvailableP);
        TextView inventoryAmount = dialogView.findViewById(R.id.textViewInventoryAmountP);
        TextView rating = dialogView.findViewById(R.id.textViewRatingP);
        TextView preparationTime = dialogView.findViewById(R.id.textViewPreparationP);
        ImageView productImageView = dialogView.findViewById(R.id.imageViewProductDetails);
        TextView productIngredients = dialogView.findViewById(R.id.textViewIngredientsP);

        title.setText(productModel.getTitle());
        culture.setText(productModel.getCulture());
        price.setText(String.valueOf(productModel.getPrice()));
        kosher.setText(productModel.getKosher());
        available.setText(String.valueOf(productModel.isAvailable()));
        inventoryAmount.setText(String.valueOf(productModel.getInventoryAmount()));
//        rating.setText(String.valueOf(productModel.getRating()));
        rating.setText(String.format("%.2f",productModel.getRating()));
        preparationTime.setText(productModel.getPreparationTime());
        productIngredients.setText(productModel.getIngredients());

        String productImageUrl = productModel.getImageUri();
        Picasso.get().load(productImageUrl).into(productImageView);

        // Create and show the dialog
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        // Set a click listener for the close button
        Button closeButton = dialogView.findViewById(R.id.buttonCloseDetails);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss(); // Close the dialog
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
