package com.example.workersapp.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.workersapp.Listeneres.DeleteListener;
import com.example.workersapp.databinding.ItemJobCategoryBinding;

import java.util.List;


public class JobCategoryAdapter extends RecyclerView.Adapter<JobCategoryAdapter.JobCategoryHolder> {
    List<String> jobCategoryList;
    DeleteListener listener;

    public JobCategoryAdapter(List<String> jobCategoryList, DeleteListener listener) {
        this.jobCategoryList = jobCategoryList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public JobCategoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemJobCategoryBinding binding = ItemJobCategoryBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new JobCategoryHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull JobCategoryHolder holder, int position) {
        int pos = position;
        if (jobCategoryList.size() != 0 && jobCategoryList != null) {
            holder.tvJobCategory.setText(jobCategoryList.get(pos));
            holder.imgDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onDelete(pos);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return jobCategoryList.size();
    }

    class JobCategoryHolder extends RecyclerView.ViewHolder {
        TextView tvJobCategory;
        ImageView imgDelete;

        public JobCategoryHolder(@NonNull ItemJobCategoryBinding binding) {
            super(binding.getRoot());
            tvJobCategory = binding.textViewCategory;
            imgDelete = binding.imageViewDelete;
        }
    }
}
