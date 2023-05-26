package com.example.workersapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.workersapp.Listeneres.ItemClickListener;
import com.example.workersapp.R;
import com.example.workersapp.Utilities.Post;
import com.example.workersapp.databinding.ItemPostBinding;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class Post_forWorkerAdapter extends RecyclerView.Adapter< Post_forWorkerAdapter.myViewHolder> {
    List < Post > postList;
    FirebaseFirestore firestore;
    Context context;
    ItemClickListener listener;

    public Post_forWorkerAdapter( List < Post > postList , Context context , ItemClickListener listener ) {
        this.postList = postList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder( @NonNull ViewGroup parent , int viewType ) {
        ItemPostBinding binding = ItemPostBinding.inflate( LayoutInflater.from( parent.getContext() ),parent,false );
        return new myViewHolder( binding );
    }

    @Override
    public void onBindViewHolder( @NonNull myViewHolder holder , int position ) {
        firestore = FirebaseFirestore.getInstance();


        int pos = position;
        holder.PostTitle.setText( postList.get( pos ).getTitle() );
        holder.PostDescription.setText( postList.get( pos ).getDescription() );
        holder.PostBudget.setText( postList.get( pos ).getProjectedBudget() );
        holder.PostLoc.setText( postList.get( pos ).getJobLocation() );
        holder.favoriteButton.setVisibility( View.VISIBLE );


        //Todo: Put Post Time her
        holder.CategoryRecycle.setAdapter( new ShowCategoryAdapter( ( ArrayList < String > ) postList.get( pos ).getCategoriesList() ) );
        holder.CategoryRecycle.setLayoutManager( new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));

        postList.get( pos ).getPostId();
        holder.LL_item.setOnClickListener( view -> listener.OnClick( pos ) );
        firestore.collection("users").document(postList.get( pos ).getOwnerId() )
                .get()
                .addOnSuccessListener(documentSnapshot1 -> {
                    if (documentSnapshot1.exists()) {
                        String fullName = documentSnapshot1.getString("fullName");
                        holder.ClintName.setText( fullName );
                        String image = documentSnapshot1.getString("image");
                        Glide.with(context)
                                .load(image)
                                .circleCrop()
                                .error( R.drawable.worker)
                                .into(holder.clintImage);
                    }
                })
                .addOnFailureListener(e -> {});

        firestore.collection("offers").document(postList.get( pos ).getPostId()).
                collection("workerOffers")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int count = task.getResult().size();
                        holder.OffersCount.setText(count == 0 ? "0" : String.valueOf(count));
                    }
                });


    }

    @Override
    public int getItemCount( ) {
        return postList.size();
    }

    static class myViewHolder extends RecyclerView.ViewHolder{
        LinearLayout LL_item;
        AppCompatTextView PostDescription,PostBudget,PostTitle,PostTime,PostLoc;
        MaterialTextView ClosedJob,OffersCount;
        TextView ClintName;
        RecyclerView CategoryRecycle;
        ImageView clintImage;
        ToggleButton favoriteButton;
        public myViewHolder( @NonNull ItemPostBinding binding) {
            super( binding.getRoot() );
            PostTitle = binding.tvPostTitle;
            PostDescription = binding.tvPostDec;
            PostBudget = binding.tvPostBudget;
            PostLoc = binding.tvPostLoc;
            PostTime=binding.tvPostTime;
            CategoryRecycle = binding.CategoryRecycle;
            ClosedJob = binding.tvClosedJob;
            LL_item=binding.LLItem;
            ClintName=binding.tvClintName;
            clintImage=binding.itemImgClint;
            OffersCount = binding.tvCountOffers;
            favoriteButton=binding.favoriteButton;
        }
    }
}
