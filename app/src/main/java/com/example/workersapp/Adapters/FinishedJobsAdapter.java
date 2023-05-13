package com.example.workersapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.workersapp.Listeneres.ItemClickListener;
import com.example.workersapp.R;
import com.example.workersapp.Utilities.Post;
import com.example.workersapp.databinding.ItemFinishedJobsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class FinishedJobsAdapter extends RecyclerView.Adapter<FinishedJobsAdapter.FinishedJobsHolder> {
    List < Post > postList;
    FirebaseFirestore firestore;
    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    Context context;
    ItemClickListener listener;

    public FinishedJobsAdapter( List < Post > postList , Context context , ItemClickListener listener ) {
        this.postList = postList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FinishedJobsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFinishedJobsBinding binding = ItemFinishedJobsBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new FinishedJobsHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull FinishedJobsHolder holder, int position) {
        firestore = FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        int pos = position;

        holder.tvWorkTitle.setText( postList.get( pos ).getTitle() );
        holder.tvBudget.setText( postList.get( pos ).getProjectedBudget() );

        //Todo: Put Post Time her

        postList.get( pos ).getPostId();
        holder.LL.setOnClickListener( view -> {
            listener.OnClick( pos );
        } );
        firestore.collection("users").document(postList.get( pos ).getWorkerId())
                .get()
                .addOnSuccessListener(documentSnapshot1 -> {
                    if (documentSnapshot1.exists()) {
                        String fullName = documentSnapshot1.getString("fullName");
                        holder.tvWorkerName.setText( fullName );
                        String image = documentSnapshot1.getString("image");
                        Glide.with(context)
                                .load(image)
                                .circleCrop()
                                .error( R.drawable.worker)
                                .into(holder.WorkerImage);
                    }
                })
                .addOnFailureListener(e -> {});



    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    class FinishedJobsHolder extends RecyclerView.ViewHolder {
        TextView tvBudget,tvDateOfPublication,tvWorkTitle,tvRate,tvWorkerName;
        LinearLayout LL;
        ImageView WorkerImage;

        public FinishedJobsHolder(@NonNull ItemFinishedJobsBinding binding) {
            super(binding.getRoot());
            tvBudget=binding.tvBudget;
            tvWorkerName=binding.tvOwnerName;
            tvWorkTitle=binding.tvWorkTitle;
            tvDateOfPublication=binding.tvDateOfPublication;
            tvRate=binding.tvRate;
            WorkerImage=binding.WorkerImage;

        }
    }
}
