package com.example.workersapp.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.workersapp.Listeneres.ItemClickListener;
import com.example.workersapp.Listeneres.clickListener;
import com.example.workersapp.R;
import com.example.workersapp.Utilities.Offer;
import com.example.workersapp.databinding.ItemOfferBinding;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class SubmittedJobAdapter extends RecyclerView.Adapter<SubmittedJobAdapter.myHolder> {
    List < Offer > offerList;
    FirebaseFirestore firestore;
    Context context;
    ItemClickListener listener;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    public SubmittedJobAdapter( List < Offer > offerList
            , Context context , ItemClickListener listener ) {
        this.offerList = offerList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public myHolder onCreateViewHolder( @NonNull ViewGroup parent , int viewType ) {
        ItemOfferBinding binding = ItemOfferBinding.inflate( LayoutInflater.from( parent.getContext() ),parent,false );
        return new myHolder( binding );
    }

    @Override
    public void onBindViewHolder(@NonNull myHolder holder, int position) {
        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        int pos = position;
        holder.offerDescription.setText(offerList.get(pos).getOfferDescription());
        holder.offerBudget.setText(offerList.get(pos).getOfferBudget());
        holder.offerDuration.setText(offerList.get(pos).getOfferDuration());
        String workerId = offerList.get(pos).getWorkerID();
        holder.LL_btn.setVisibility(View.GONE);
        holder.Line.setVisibility(View.VISIBLE);
        holder.tvPostTitle.setVisibility(View.VISIBLE);

        firestore.collection("users").document(workerId)
                .get()
                .addOnSuccessListener(documentSnapshot1 -> {
                    if (documentSnapshot1.exists()) {
                        String fullName = documentSnapshot1.getString("fullName");
                        holder.workerName.setText(fullName);
                        String image = documentSnapshot1.getString("image");
                        Glide.with(context)
                                .load(image)
                                .circleCrop()
                                .error(R.drawable.worker)
                                .into(holder.WorkerImage);
                    }
                })
                .addOnFailureListener(e -> {});

        firestore.collection("forms").document(offerList.get(pos).getWorkerID()).collection("userForm").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int numDocs = queryDocumentSnapshots.size();
                    holder.formsCount.setText(String.valueOf(numDocs));
                })
                .addOnFailureListener(e -> {
                    // handle the case when the query fails
                });

        firestore.collection("posts")
                .document(offerList.get(pos).getClintID())
                .collection("userPost")
                .document(offerList.get(pos).getPostID())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String title = documentSnapshot.getString("title");
                        holder.tvPostTitle.setText(title);
                    } else {
                        System.out.println("Document does not exist");
                    }
                })
                .addOnFailureListener(e -> {});

        holder.LL.setOnClickListener(view -> {
            listener.OnClick(pos);
        });

        List<Long> RatingWorkerList = new ArrayList<>();
        firestore.collection("users").get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (DocumentSnapshot documentSnapshot1 : queryDocumentSnapshots) {
                firestore.collection("posts").document(documentSnapshot1.getId())
                        .collection("userPost").whereEqualTo("jobState", "done")
                        .whereEqualTo("workerId", firebaseUser.getPhoneNumber()).get()
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
                                    holder.workerRating.setText(x + "");
                                    Log.d("tag", String.valueOf(x));
                                } else {
                                    holder.workerRating.setText("0");
                                }
                            }
                        });
            }
        });
    }


    @Override
    public int getItemCount( ) {
        return offerList.size();
    }

    class myHolder extends RecyclerView.ViewHolder{
        MaterialTextView offerDuration,offerDescription,offerBudget,workerName
                ,formsCount;
        TextView workerRating,tvPostTitle;
        ShapeableImageView WorkerImage;
        AppCompatButton hireButton,deleteButton;
        LinearLayout LL_btn,Line;
        ConstraintLayout LL;

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
            LL_btn=binding.LLBtn;
            Line=binding.Line;
            tvPostTitle=binding.tvTitleJob;
           LL= binding.LLBtns;




        }
    }
}
