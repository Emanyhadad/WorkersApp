package com.example.workersapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.workersapp.Listeneres.clickListener;
import com.example.workersapp.R;
import com.example.workersapp.databinding.ItemImgModelBinding;

import java.util.List;

public class ImageModelAdapter extends RecyclerView.Adapter<ImageModelAdapter.ImageViewHolder> {
    List<String> imagesList;
    
    Context context;
    clickListener listener;

    public ImageModelAdapter(List<String> imagesList, Context context, clickListener listener) {
        this.imagesList = imagesList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemImgModelBinding binding = ItemImgModelBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false);
        return new ImageViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {

        if (imagesList != null) {
            Glide.with(context)
                    .load(imagesList.get(holder.getAdapterPosition()))
                    .centerCrop()
//                    .centerInside()
                    .transform(new RoundedCorners(10))
//                    .transform(new RoundedCorners(10), new BorderTransformation(2, Color.WHITE))
                    .into(holder.image);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.click(holder.getAdapterPosition());
//                    Toast.makeText(context, "pos: "+holder.getAdapterPosition(), Toast.LENGTH_SHORT).show();
                }
            });
//            Toast.makeText(holder.itemView.getContext(), "position: "+holder.getAdapterPosition(), Toast.LENGTH_SHORT).show();
        }else {
//            Toast.makeText(holder.itemView.getContext(), "null", Toast.LENGTH_SHORT).show();
            Glide.with(context).load(R.drawable.user).into(holder.image);
        }
    }

    @Override
    public int getItemCount() {
        return imagesList.size();
    }

    class ImageViewHolder extends RecyclerView.ViewHolder{
        ImageView image;

        public ImageViewHolder(ItemImgModelBinding binding) {

            super(binding.getRoot());
            image = binding.imgModel;

        }
    }
}