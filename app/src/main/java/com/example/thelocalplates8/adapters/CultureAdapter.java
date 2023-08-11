package com.example.thelocalplates8.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thelocalplates8.Models.CultureModel;
import com.example.thelocalplates8.R;

import java.util.ArrayList;

public class CultureAdapter extends RecyclerView.Adapter<CultureAdapter.ViewHolder>{
    ArrayList<CultureModel> cultureModels;
    Context context;

    OnClickListener onClickListener;

    public CultureAdapter(ArrayList<CultureModel> cultureModels, Context context) {
        this.cultureModels = cultureModels;
        this.context = context;
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

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onClickListener != null) {
                    onClickListener.onClick(holder.getAdapterPosition(), currentCulture);
                }
            }
        });
    }
    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }
    public interface OnClickListener {
        void onClick(int position, CultureModel model);
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
