package com.example.thelocalplates8.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thelocalplates8.Models.CategoryModel;
import com.example.thelocalplates8.Models.CultureModel;
import com.example.thelocalplates8.R;

import java.util.ArrayList;

public class CultureAdapter extends RecyclerView.Adapter<CultureAdapter.ViewHolder>{
    ArrayList<CultureModel> cultureModels;

    public CultureAdapter(ArrayList<CultureModel> cultureModels) {
        this.cultureModels = cultureModels;
    }

    @NonNull
    @Override
    public CultureAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_culture2, parent, false);
        return new CultureAdapter.ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull CultureAdapter.ViewHolder holder, int position) {
        CultureModel currentCulture = cultureModels.get(position);
        holder.cultureName.setText(currentCulture.getTitle());
        holder.culturePic.setImageResource(
                holder.cultureName.getResources().getIdentifier(currentCulture.getPic(),
                        "drawable",
                        holder.cultureName.getContext().getPackageName())
        );
    }

    @Override
    public int getItemCount() {
        return cultureModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView cultureName;
        ImageView culturePic;
        ConstraintLayout mainLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cultureName = itemView.findViewById(R.id.cultureName);
            culturePic = itemView.findViewById(R.id.culturePic);
            mainLayout = itemView.findViewById(R.id.mainLayout2);
        }
    }
}
