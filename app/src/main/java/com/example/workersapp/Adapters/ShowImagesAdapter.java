package com.example.workersapp.Adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.workersapp.databinding.ItemShowimageBinding;

import java.util.List;

public class ShowImagesAdapter extends RecyclerView.Adapter<ShowImagesAdapter.ImageHolder> {

    List < String > uriList;
    Context context;


    public ShowImagesAdapter( List < String > uriList, Context context) {
        this.uriList = uriList;
        this.context = context;

    }

    @NonNull
    @Override
    public ImageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemShowimageBinding binding = ItemShowimageBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false);
        return new ImageHolder( binding );
    }

    @Override
    public void onBindViewHolder(@NonNull ImageHolder holder, int position) {
        int pos = position;
        if (!(uriList.size() == 0)) {
            Glide.with(context).load(uriList.get(pos))
                    .into(holder.img);
        }

    }

    @Override
    public int getItemCount() {
        return uriList.size();
    }

    static class ImageHolder extends RecyclerView.ViewHolder {
        ImageView img;

        public ImageHolder( @NonNull ItemShowimageBinding binding) {
            super(binding.getRoot());
            img = binding.imageView;

        }
    }
}
