package com.example.workersapp.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.workersapp.Listeneres.clickListener;
import com.example.workersapp.databinding.ItemSliderImgBinding;

import java.util.ArrayList;

public class SliderImgAdapter extends RecyclerView.Adapter<SliderImgAdapter.SliderViewHolder> {
    ArrayList<String> sliderArrayList;

    Context context;

    clickListener listener;

    public SliderImgAdapter(ArrayList<String> sliderArrayList, Context context, clickListener listener) {
        this.sliderArrayList = sliderArrayList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSliderImgBinding binding = ItemSliderImgBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false);
        return new SliderViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {
        String image = sliderArrayList.get(holder.getAdapterPosition());

        if (image != null) {
            Glide.with(context).load(image).into(holder.img);

            holder.nextImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.next(holder.getAdapterPosition());
                }
            });

            if (holder.getAdapterPosition() == sliderArrayList.size()-1) {
                holder.nextImg.setVisibility(View.GONE);
            }else {
                holder.nextImg.setVisibility(View.VISIBLE);
            }

            if (holder.getAdapterPosition() > 0) {
                holder.backImg.setVisibility(View.VISIBLE);
                holder.backImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.back(holder.getAdapterPosition());
                    }
                });
            }else {
                holder.backImg.setVisibility(View.GONE);
            }
        }

    }

    @Override
    public int getItemCount() {
        return sliderArrayList.size();
    }

    class SliderViewHolder extends RecyclerView.ViewHolder {
        ImageView img, nextImg, backImg;

        public SliderViewHolder(ItemSliderImgBinding binding) {
            super(binding.getRoot());
            img = binding.businessShowImg;
            nextImg = binding.businessNextImg;
            backImg = binding.businessBackImg;
        }
    }
}