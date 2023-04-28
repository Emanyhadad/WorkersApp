package com.example.workersapp.Utilities;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.workersapp.databinding.ItemRvBinding;

import java.util.ArrayList;

public class categoryAdapter extends RecyclerView.Adapter<categoryAdapter.CategoryViewHolder> {

    ArrayList<String> categoryArrayList = new ArrayList<>();

    public categoryAdapter(ArrayList<String> categoryArrayList) {
        this.categoryArrayList = categoryArrayList;
    }
    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRvBinding binding = ItemRvBinding.inflate(LayoutInflater.from(parent.getContext()),
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


        public CategoryViewHolder(ItemRvBinding binding) {
            super(binding.getRoot());
            nameCat = binding.itemRvTv;

        }
    }
}
