package com.example.workersapp.Adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
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
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(50)))
                    .into(holder.img);
        holder.imgDelete.setOnClickListener( view -> listener.onDelete(pos) );
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
