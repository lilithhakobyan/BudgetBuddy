package com.example.budgetbuddy.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budgetbuddy.R; // Import your project's R class

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private String[] categories;
    private int[] categoryIcons;

    public CategoryAdapter(String[] categories, int[] categoryIcons) {
        this.categories = categories;
        this.categoryIcons = categoryIcons;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(categories[position], categoryIcons[position]);
    }

    @Override
    public int getItemCount() {
        return categories.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView nameTextView;
        private ImageView iconImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.category_name);
            iconImageView = itemView.findViewById(R.id.category_icon);
        }

        public void bind(String category, int iconResource) {
            nameTextView.setText(category);
            iconImageView.setImageResource(iconResource);
        }
    }
}
