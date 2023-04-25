package com.example.workersapp.Adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.workersapp.Listeneres.DeleteListener;
import com.example.workersapp.databinding.ItemImegBinding;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageHolder> {

    List<Uri> uriList;
    Context context;

    DeleteListener listener;

    public ImageAdapter(List<Uri> uriList, Context context, DeleteListener listener) {
        this.uriList = uriList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ImageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemImegBinding binding = ItemImegBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false);
        return new ImageHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageHolder holder, int position) {
        int pos = position;
        if (!(uriList.size() == 0)) {
            Glide.with(context).load(uriList.get(pos))
                    .into(holder.img);
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
        return uriList.size();
    }

    class ImageHolder extends RecyclerView.ViewHolder {
        ImageView img, imgDelete;

        public ImageHolder(@NonNull ItemImegBinding binding) {
            super(binding.getRoot());
            img = binding.img;
            imgDelete = binding.imgDelete;
        }
    }
}
