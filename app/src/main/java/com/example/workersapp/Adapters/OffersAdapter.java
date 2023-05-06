package com.example.workersapp.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.workersapp.Listeneres.DeleteListener;
import com.example.workersapp.Utilities.Offer;
import com.example.workersapp.databinding.ItemOfferBinding;

import java.util.List;

public class OffersAdapter extends RecyclerView.Adapter<OffersAdapter.myHolder> {
    List< Offer > offerList;
    DeleteListener listener;

    public OffersAdapter( List < Offer > offerList , DeleteListener listener ) {
        this.offerList = offerList;
        this.listener = listener;
    }


    @NonNull
    @Override
    public myHolder onCreateViewHolder( @NonNull ViewGroup parent , int viewType ) {
        ItemOfferBinding binding = ItemOfferBinding.inflate( LayoutInflater.from( parent.getContext() ),parent,false );
        return new myHolder( binding );
    }

    @Override
    public void onBindViewHolder( @NonNull myHolder holder , int position ) {

    }


    @Override
    public int getItemCount( ) {
        return 0;
    }

    class myHolder extends RecyclerView.ViewHolder{

        public myHolder( @NonNull ItemOfferBinding binding ) {
            super( binding.getRoot() );
        }
    }
}
