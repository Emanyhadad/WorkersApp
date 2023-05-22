package com.example.workersapp.Adapters;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.workersapp.Listeneres.ItemClickListener;
import com.example.workersapp.R;
import com.example.workersapp.Utilities.WorkerReviews;

import com.example.workersapp.databinding.ItemReviewsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ReviewsAdapter extends RecyclerView.Adapter< ReviewsAdapter.myViewHolder> {
    List < WorkerReviews > reviewsList;
    FirebaseFirestore firestore;
    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    Context context;
    ItemClickListener listener;

    public ReviewsAdapter( List < WorkerReviews > reviewsList , Context context , ItemClickListener listener ) {
        this.reviewsList = reviewsList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ReviewsAdapter.myViewHolder onCreateViewHolder( @NonNull ViewGroup parent , int viewType ) {
        ItemReviewsBinding binding = ItemReviewsBinding.inflate( LayoutInflater.from( parent.getContext() ),parent,false );
        return new ReviewsAdapter.myViewHolder( binding );
    }

    @Override
    public void onBindViewHolder( @NonNull myViewHolder holder , int position ) {
        firestore = FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();

        int pos = position;
        holder.ItemJobTitle.setText( reviewsList.get( pos ).getJobTitle() );
        holder.ItemjobFinishDate.setText( reviewsList.get( pos ).getJobFinishDate() );
        holder.ItemComment_worker.setText( reviewsList.get( pos ).getComment_worker());
        holder.ItemRate.setText( reviewsList.get( pos ).getRating_worker() );
        firestore.collection("users").document( reviewsList.get( pos ).getClintId())
                .get()
                .addOnSuccessListener(documentSnapshot1 -> {
                    if (documentSnapshot1.exists()) {
                        String fullName = documentSnapshot1.getString("fullName");
                        holder.ItemClintName.setText( fullName );
                        String image = documentSnapshot1.getString("image");
                        Glide.with(context)
                                .load(image)
                                .circleCrop()
                                .error( R.drawable.worker)
                                .into(holder.clintImage);
                    }
                })
                .addOnFailureListener(e -> {});

        holder.Item.setOnClickListener( view -> {
            listener.OnClick( pos );
        } );




    }



    @Override
    public int getItemCount( ) {
        return reviewsList.size();
    }

    class myViewHolder extends RecyclerView.ViewHolder{

        TextView ItemJobTitle,
                ItemjobFinishDate,ItemRate,
                ItemComment_worker,ItemClintName;
        CardView Item;
        ImageView clintImage;
        public myViewHolder( @NonNull ItemReviewsBinding binding) {
            super( binding.getRoot() );

            clintImage=binding.ItemImgClint;
            ItemJobTitle=binding.ItemJobTitle;
            ItemjobFinishDate=binding.ItemjobFinishDate;
            ItemRate=binding.ItemRate;
            ItemComment_worker=binding.ItemCommentWorker;
            ItemClintName=binding.ItemClintName;
            Item=binding.Item;
        }
    }
}