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
import com.bumptech.glide.request.RequestOptions;
import com.example.workersapp.Listeneres.clickListener;
import com.example.workersapp.R;
import com.example.workersapp.Utilities.Model;
import com.example.workersapp.databinding.ItemImgModelBinding;

import java.util.List;

public class ImageModelAdapter extends RecyclerView.Adapter<ImageModelAdapter.ImageViewHolder> {
//    List<String> imagesList;
    List<Model> models ;

    Context context;

    clickListener listener;

//    public ImageModelAdapter(List<String> imagesList, Context context, clickListener listener) {
//        this.imagesList = imagesList;
//        this.context = context;
//        this.listener = listener;
//    }


    public ImageModelAdapter(List<Model> models, Context context, clickListener listener) {
        this.models = models;
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
        Model model =models.get(position);

        if (models != null) {
            Glide.with(context)
                    .load(model.getImages().get(0))
//                    .centerCrop()
//                    .centerInside()
//                    .transform(new RoundedCorners(10))
//                    .transform(new RoundedCorners(10), new BorderTransformation(2, Color.WHITE))
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(30)))
                    .into(holder.image);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.click(model.getDocumentId());
                }
            });
        }else {
            Glide.with(context).load(R.drawable.user).into(holder.image);
        }
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    class ImageViewHolder extends RecyclerView.ViewHolder{
        ImageView image;

        public ImageViewHolder(ItemImgModelBinding binding) {

            super(binding.getRoot());
            image = binding.imgModel;

        }
    }
}