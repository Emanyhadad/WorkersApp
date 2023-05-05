package com.example.workersapp.Adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.workersapp.databinding.ItemCategoryModelBinding;

import java.util.ArrayList;

public class categoryAdapter extends RecyclerView.Adapter<categoryAdapter.CategoryViewHolder> {

    ArrayList<String> categoryArrayList = new ArrayList<>();
    public categoryAdapter(ArrayList<String> categoryArrayList) {
        this.categoryArrayList = categoryArrayList;
    }
    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCategoryModelBinding binding = ItemCategoryModelBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false);
        return new CategoryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        holder.nameCat.setText(categoryArrayList.get(holder.getAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        return categoryArrayList.size();
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder{
        TextView nameCat;

        public CategoryViewHolder(ItemCategoryModelBinding binding) {

            super(binding.getRoot());
            nameCat = binding.itemRvTv;

        }
    }
}
