package com.example.thelocalplates8.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thelocalplates8.Controllers.RatingController;
import com.example.thelocalplates8.Models.ProductOrderModel;
import com.example.thelocalplates8.Models.RatingBusinessModel;
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

        holder.productRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(context, "Rate product", Toast.LENGTH_SHORT).show();
                ShowProductRatingWindow(currentProduct);
            }
        });

        holder.businessRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(context, "Rate Business", Toast.LENGTH_SHORT).show();
                ShowBusinessReviewWindow(currentProduct);
            }
        });
    }

    private void ShowBusinessReviewWindow(ProductOrderModel currentProduct) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.business_rate_itemview, null);
        dialogBuilder.setView(dialogView);

        Button rateBtn = dialogView.findViewById(R.id.buttonRateB);
        Button closeBtn = dialogView.findViewById(R.id.buttonRatingB);
        EditText review = dialogView.findViewById(R.id.editTextTextMultiLineB);
        RatingBar ratingBar = dialogView.findViewById(R.id.ratingBarBusiness);


        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        RatingController ratingController = new RatingController(context);

        ratingController.checkIfRatedBusiness(currentProduct.getBusinessId(), new RatingController.CheckIfRatedBusiness() {
            @Override
            public void onCheckIfRatedBusiness(RatingBusinessModel ratingBusinessModel) {
                if(ratingBusinessModel != null){
                    rateBtn.setEnabled(false);
                    rateBtn.setText("Already rated");
                    ratingBar.setRating((float)ratingBusinessModel.getRating());
                    ratingBar.setEnabled(false);
                    review.setText(ratingBusinessModel.getReview());
                    review.setEnabled(false);
                }
            }
        });
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        rateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double rating = ratingBar.getRating();
                String r = review.getText().toString();

                ratingController.rateBusiness(currentProduct.getBusinessId(), rating, r, new RatingController.RateBusiness() {
                    @Override
                    public void onRateBusiness(boolean rated) {
                        Toast.makeText(context, "Business rated!\nThank you :)", Toast.LENGTH_SHORT).show();
                        alertDialog.dismiss();
                    }
                });
            }
        });
    }

    private void ShowProductRatingWindow(ProductOrderModel currentProduct) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.product_rate_itemview, null);
        dialogBuilder.setView(dialogView);

        Button closeBtn = dialogView.findViewById(R.id.buttonCloseWindow);
        Button rateBtn = dialogView.findViewById(R.id.buttonRateProduct);
        RatingBar ratingBar = dialogView.findViewById(R.id.ratingBarProduct);

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        RatingController ratingController = new RatingController(context);

        ratingController.checkIfRatedProduct(currentProduct.getProductId(), new RatingController.CheckIfRatedProduct() {
            @Override
            public void onCheckIfRatedProduct(boolean rated) {
                if(rated){
                    rateBtn.setEnabled(false);
                    rateBtn.setText("Already rated");
                }else{
                    rateBtn.setEnabled(true);
                    rateBtn.setText("Rate product");
                }
            }
        });

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        rateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double rating = ratingBar.getRating();
                ratingController.rateProduct(currentProduct, rating, new RatingController.RateProduct() {
                    @Override
                    public void onRateProduct(boolean rated) {
                        if(rated){
                            Toast.makeText(context, "Product rated!\nThank you :)", Toast.LENGTH_SHORT).show();
                            alertDialog.dismiss();
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
