package com.example.workersapp.Adapters;

import static androidx.constraintlayout.widget.ConstraintLayoutStates.TAG;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
        return new FinishedJobsHolder( binding );
    }

    @Override
    public void onBindViewHolder(@NonNull FinishedJobsHolder holder, int position) {
        firestore = FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        int pos = position;

        holder.tvWorkTitle.setText( postList.get( pos ).getTitle() );

        List<Long> RatingWorkerList = new ArrayList<>();
        firestore.collection("users").get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (DocumentSnapshot documentSnapshot1 : queryDocumentSnapshots) {
                firestore.collection("posts").document(documentSnapshot1.getId())
                        .collection("userPost").whereEqualTo("jobState", "done")
                        .whereEqualTo("workerId", postList.get(pos).getWorkerId()).get()
                        .addOnCompleteListener(task -> {
                            for (DocumentSnapshot document : task.getResult()) {
                                RatingWorkerList.add(document.getLong("Rating-worker"));

                                Log.d("tag", document.getId());

                                long sum = 0;
                                for (Long value : RatingWorkerList) {
                                    sum += value;
                                }
                                if (RatingWorkerList.size() != 0) {
                                    int x = (int) (sum / RatingWorkerList.size());
                                    holder.tvRate.setText(x + "");
                                    Log.d("tag", String.valueOf(x));
                                } else {
                                    holder.tvRate.setText("0");
                                }
                            }
                        })
                        .addOnFailureListener(runnable -> {
                        });
            }
        });


        firestore.collection("posts").document( postList.get( pos ).getOwnerId() ).
                collection("userPost").document(Objects.requireNonNull(postList.get( pos ).getPostId())).get()
                .addOnSuccessListener( documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        holder.tvDateOfPublication.setText(documentSnapshot.getString( "jobFinishDate" ) );
                        holder.tvRate.setText( documentSnapshot.get( "Rating-worker" )+"" );
                    }});

        CollectionReference workerOffersRef = firestore.collection("offers").document(postList.get( pos ).getPostId()).collection("workerOffers");
        workerOffersRef.document( postList.get( pos ).getWorkerId() ).get().addOnCompleteListener( task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    //Todo get data from offer (price)
                    String offerBudget = document.getString("offerBudget");
                    holder.tvBudget.setText( offerBudget+"" );
                } else Log.d(TAG, "No such document");
            } else  Log.d(TAG, "Error getting document: ", task.getException()); });

        //Todo: Put Post Time her

        holder.LL.setOnClickListener( view -> listener.OnClick( pos ) );

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

    static class FinishedJobsHolder extends RecyclerView.ViewHolder {
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
            LL=binding.LL;

        }
    }
}
