package com.example.workersapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.workersapp.Utilities.Post;
import com.example.workersapp.databinding.ItemPostBinding;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.myViewHolder> {
    List < Post > postList;
    FirebaseFirestore firestore;
    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    Context context;

    public PostAdapter( List < Post > offerList , Context context ) {
        this.postList = offerList;
        this.context = context;
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
        auth=FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();

        int pos = position;
        holder.PostTitle.setText( postList.get( pos ).getTitle() );
        holder.PostDescription.setText( postList.get( pos ).getDescription() );
        holder.PostBudget.setText( postList.get( pos ).getProjectedBudget() );
        holder.PostLoc.setText( postList.get( pos ).getJobLocation() );
        holder.PostDuration.setText( postList.get( pos ).getExpectedWorkDuration() );
        holder.CategoryRecycle.setAdapter( new ShowCategoryAdapter( ( ArrayList < String > ) postList.get( pos ).getCategoriesList() ) );
        holder.CategoryRecycle.setLayoutManager( new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        if ( postList.get( pos ).getJobState().equals( "close" ) ){ holder.ClosedJob.setVisibility( View.VISIBLE ); }




    }

    @Override
    public int getItemCount( ) {
        return postList.size();
    }

    class myViewHolder extends RecyclerView.ViewHolder{
        AppCompatTextView PostDuration;
        AppCompatTextView PostDescription;
        AppCompatTextView PostBudget;
        MaterialTextView ClosedJob;
        AppCompatTextView PostTitle;
        AppCompatTextView PostTime;
        AppCompatTextView PostLoc;

        RecyclerView CategoryRecycle;
        public myViewHolder( @NonNull ItemPostBinding binding) {
            super( binding.getRoot() );
            PostTitle = binding.tvPostTitle;
            PostDescription = binding.tvPostDec;
            PostBudget = binding.tvPostBudget;
            PostDuration = binding.tvJobTime;
            PostLoc = binding.tvPostLoc;
            PostTime=binding.tvPostTime;
            CategoryRecycle = binding.CategoryRecycle;
            ClosedJob = binding.tvClosedJob;
        }
    }
}
