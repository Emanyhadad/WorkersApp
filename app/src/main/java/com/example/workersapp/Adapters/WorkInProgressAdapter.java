package com.example.workersapp.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.workersapp.Listeneres.ItemClickListener;
import com.example.workersapp.R;
import com.example.workersapp.Utilities.Post;
import com.example.workersapp.databinding.ItemWorkInProgressBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class WorkInProgressAdapter extends RecyclerView.Adapter<WorkInProgressAdapter.WorkInProgressHolder> {

    List < Post > postList;
    FirebaseFirestore firestore;
    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    Context context;
    ItemClickListener listener;

    public WorkInProgressAdapter( List < Post > postList , Context context , ItemClickListener listener ) {
        this.postList = postList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public WorkInProgressHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemWorkInProgressBinding binding = ItemWorkInProgressBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new WorkInProgressHolder(binding);
    }

    public void onBindViewHolder(@NonNull WorkInProgressAdapter.WorkInProgressHolder holder, int position) {
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();

        int pos = position;
        holder.tvWorkTitle.setText(postList.get(pos).getTitle());
        holder.tvWorkDescription.setText(postList.get(pos).getDescription());
        holder.tvBudget.setText(postList.get(pos).getProjectedBudget());
        holder.tvLocation.setText(postList.get(pos).getJobLocation());
        // Todo: Put Post Time here

        String workerId = postList.get(pos).getWorkerId();
        if (workerId != null && !workerId.isEmpty()) {
            firestore.collection("users").document(workerId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String fullName = documentSnapshot.getString("fullName");
                            holder.tvWorkerName.setText(fullName);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Field", e.getMessage());
                    });
        } else {
            holder.tvWorkerName.setText("N/A");
        }

        holder.LL.setOnClickListener(view -> {
            listener.OnClick(pos);
        });
    }

    @Override
    public int getItemCount( ) {
        return postList.size();
    }

    class WorkInProgressHolder extends RecyclerView.ViewHolder {
        TextView tvBudget,tvDuration,tvWorkDescription,tvWorkTitle,tvLocation,tvWorkerName;
        LinearLayout LL;
        public WorkInProgressHolder(@NonNull ItemWorkInProgressBinding binding) {
            super(binding.getRoot());
            LL=binding.LL;
            tvBudget=  binding.tvBudget;
            tvDuration=   binding.tvDuration;
            tvWorkDescription=  binding.tvWorkDescription;
            tvWorkTitle= binding.tvWorkTitle;
            tvLocation=binding.tvLocation;
            tvWorkerName=binding.tvWorkerName;
        }
    }
}
