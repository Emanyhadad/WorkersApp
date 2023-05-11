package com.example.workersapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.workersapp.Listeneres.DeleteListener;
import com.example.workersapp.Listeneres.OfferListener;
import com.example.workersapp.R;
import com.example.workersapp.Utilities.Offer;
import com.example.workersapp.databinding.ItemOfferBinding;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Objects;

public class OffersAdapter extends RecyclerView.Adapter<OffersAdapter.myHolder> {
    List< Offer > offerList;
    OfferListener listener;
    FirebaseFirestore firestore;
    Context context;


    public OffersAdapter( List < Offer > offerList , OfferListener listener , Context context ) {
        this.offerList = offerList;
        this.listener = listener;
        this.context = context;
    }

    @NonNull
    @Override
    public myHolder onCreateViewHolder( @NonNull ViewGroup parent , int viewType ) {
        ItemOfferBinding binding = ItemOfferBinding.inflate( LayoutInflater.from( parent.getContext() ),parent,false );
        return new myHolder( binding );
    }

    @Override
    public void onBindViewHolder( @NonNull myHolder holder , int position ) {
        firestore = FirebaseFirestore.getInstance();

        int pos = position;
        holder.offerDescription.setText(offerList.get( pos ).offerDescription  );
        holder.offerBudget.setText(offerList.get( pos ).offerBudget);
        holder.offerDuration.setText(offerList.get( pos ).offerDuration);
        String workerId =offerList.get( pos ).workerID;
        firestore.collection("users").document( Objects.requireNonNull(workerId))
                .get()
                .addOnSuccessListener(documentSnapshot1 -> {
                    if (documentSnapshot1.exists()) {
                        String fullName = documentSnapshot1.getString("fullName");
                        holder.workerName.setText( fullName );
                        String image = documentSnapshot1.getString("image");
                        Glide.with(context)
                                .load(image)
                                .circleCrop()
                                .error( R.drawable.worker)
                                .into(holder.WorkerImage);
                    }
                })
                .addOnFailureListener(e -> {});
        firestore.collection( "forms" ).document( offerList.get( pos ).workerID).collection( "userForm" ).get().addOnSuccessListener( runnable -> {
            runnable.size();
        } );
        holder.deleteButton.setOnClickListener( view -> {listener.onDelete( pos );} );
        holder.hireButton.setOnClickListener( view -> {listener.onHire( pos );} );



    }


    @Override
    public int getItemCount( ) {
        return offerList.size();
    }

    class myHolder extends RecyclerView.ViewHolder{
        MaterialTextView offerDuration,offerDescription,offerBudget,workerName
                ,formsCount;
        TextView workerRating;
        ShapeableImageView WorkerImage;
        AppCompatButton hireButton,deleteButton;

        public myHolder( @NonNull ItemOfferBinding binding ) {
            super( binding.getRoot() );
            offerDescription=binding.itemTvSendOfferDec;
            offerDuration=binding.itemTvSendOfferDuration;
            offerBudget=binding.itemTvSendOfferPrice;
            WorkerImage=binding.itemImgWorker;
            workerName=binding.tvOfferWorkerName;
            workerRating=binding.itemTvWorkerRating;
            formsCount=binding.itemTvCountWorks;
            deleteButton=binding.itemBtnDelete;
            hireButton=binding.itemBtnHireMe;



        }
    }
}
