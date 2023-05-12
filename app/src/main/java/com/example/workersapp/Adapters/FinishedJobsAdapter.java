package com.example.workersapp.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.workersapp.databinding.ItemFinishedJobsBinding;

public class FinishedJobsAdapter extends RecyclerView.Adapter<FinishedJobsAdapter.FinishedJobsHolder> {

    @NonNull
    @Override
    public FinishedJobsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFinishedJobsBinding binding = ItemFinishedJobsBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new FinishedJobsHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull FinishedJobsHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 10;
    }

    class FinishedJobsHolder extends RecyclerView.ViewHolder {

        public FinishedJobsHolder(@NonNull ItemFinishedJobsBinding binding) {
            super(binding.getRoot());
        }
    }
}
