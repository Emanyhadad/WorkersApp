package com.example.workersapp.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.workersapp.databinding.ItemWorkInProgressBinding;

public class WorkInProgressAdapter extends RecyclerView.Adapter<WorkInProgressAdapter.WorkInProgressHolder> {


    @NonNull
    @Override
    public WorkInProgressHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemWorkInProgressBinding binding = ItemWorkInProgressBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new WorkInProgressHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkInProgressHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 3;
    }

    class WorkInProgressHolder extends RecyclerView.ViewHolder {
        public WorkInProgressHolder(@NonNull ItemWorkInProgressBinding binding) {
            super(binding.getRoot());
        }
    }
}
