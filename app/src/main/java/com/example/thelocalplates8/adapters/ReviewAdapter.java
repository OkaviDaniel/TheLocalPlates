package com.example.thelocalplates8.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thelocalplates8.Models.ProductOrderModel;
import com.example.thelocalplates8.Models.RatingBusinessModel;
import com.example.thelocalplates8.R;

import java.util.ArrayList;

public class ReviewAdapter extends  RecyclerView.Adapter<ReviewAdapter.RatingViewHolder>{

    private ArrayList<RatingBusinessModel> ratings;
    private Context context;
    private String userId;

    public ReviewAdapter(ArrayList<RatingBusinessModel> ratings, Context context){
        this.context = context;
        this.ratings = ratings;

        SharedPreferences sharedPreferences = context.getSharedPreferences("MyUserPrefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("userId", "");
    }

    @NonNull
    @Override
    public RatingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View ratingView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycleitem_review, parent, false);
        return new RatingViewHolder(ratingView);
    }

    @Override
    public void onBindViewHolder(@NonNull RatingViewHolder holder, int position) {
        RatingBusinessModel currentRating = ratings.get(position);
        String temp = currentRating.getFirstName() + " " + currentRating.getLastName();
        holder.name.setText("Review by: " + temp);
        holder.ratingBar.setRating((float)currentRating.getRating());
        holder.ratingBar.setEnabled(false);
        holder.review.setText(currentRating.getReview());
        holder.review.setEnabled(false);
    }

    @Override
    public int getItemCount() {
        return ratings.size();
    }


    public static class RatingViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private RatingBar ratingBar;
        private TextView review;

        public RatingViewHolder(@NonNull View itemView) {
            super(itemView);
            this.name = itemView.findViewById(R.id.textViewReviewBy);
            this.ratingBar = itemView.findViewById(R.id.ratingBar2);
            this.review = itemView.findViewById(R.id.editTextTextMultiLineR);
        }
    }
}
