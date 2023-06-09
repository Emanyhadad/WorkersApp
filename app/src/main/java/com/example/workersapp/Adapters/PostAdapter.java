package com.example.workersapp.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.workersapp.Listeneres.ItemClickListener;
import com.example.workersapp.R;
import com.example.workersapp.Utilities.Post;
import com.example.workersapp.databinding.ItemPostBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter< PostAdapter.myViewHolder> {
    List < Post > postList;
    FirebaseFirestore firestore;
    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    Context context;
    ItemClickListener listener;

    public PostAdapter( List < Post > postList , Context context , ItemClickListener listener ) {
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
        auth=FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();

        int pos = position;
        holder.PostTitle.setText( postList.get( pos ).getTitle() );
        holder.PostDescription.setText( postList.get( pos ).getDescription() );
        holder.PostBudget.setText( postList.get( pos ).getProjectedBudget() );
        holder.PostLoc.setText( postList.get( pos ).getJobLocation() );
        long currentTimeMillis = System.currentTimeMillis();
        long storageTimeMillis = postList.get( pos ).getAddedTime();
        long timeDifferenceMillis = currentTimeMillis - storageTimeMillis;

        long seconds = timeDifferenceMillis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;

        String timeDifference;
        if (hours > 0) {
            timeDifference ="قبل "+ hours + "  ساعة";
        } else if (minutes > 0) {
            timeDifference = "قبل "+minutes + "  دقيقة";
        } else {
            timeDifference = "قبل "+seconds + " ثانية";
        }
        holder.PostTime.setText( timeDifference );
        //Todo: Put Post Time her
        holder.CategoryRecycle.setAdapter( new ShowCategoryAdapter( ( ArrayList < String > ) postList.get( pos ).getCategoriesList() ) );
        holder.CategoryRecycle.setLayoutManager( new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        if ( postList.get( pos ).getJobState().equals( "close" ) ){ holder.ClosedJob.setVisibility( View.VISIBLE ); }
        postList.get( pos ).getPostId();
        holder.LL_item.setOnClickListener( view -> {
        listener.OnClick( pos );
        } );
        firestore.collection("users").document(firebaseUser.getPhoneNumber() )
                .get()
                .addOnSuccessListener(documentSnapshot1 -> {
                    if (documentSnapshot1.exists()) {
                        String fullName = documentSnapshot1.getString("fullName");
                        holder.ClintName.setText( fullName );
                        String image = documentSnapshot1.getString("image");
                        if (context!=null){
                            Glide.with(context)
                                    .load(image)
                                    .circleCrop()
                                    .error( R.drawable.worker)
                                    .into(holder.clintImage);
                        }

                    }
                })
                .addOnFailureListener(e -> {});

        firestore.collection("offers").document(postList.get( pos ).getPostId()).collection("workerOffers")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int count = task.getResult().size();
                        holder.OffersCount.setText(count == 0 ? "0" : String.valueOf(count));
                    }
                });

        firestore.collection("posts")
                .document(firebaseUser.getPhoneNumber()).
                collection("userPost").whereEqualTo("jobState", "done")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            double rate = 0;
                            int count = 0;
                            count = task.getResult().size();

                            // null object
//                            for (DocumentSnapshot document : task.getResult()) {
//                                rate = rate + document.getLong("Rating-clint");
//                            }

                            for (DocumentSnapshot document : task.getResult()) {
                                Long rating = document.getLong("Rating-clint");
                                if (rating != null) {
                                    rate = rate + rating.longValue();
                                } else {
                                    // إجراء للتعامل مع القيمة الناقصة (مثلاً رمي استثناء أو إيقاف الحساب)
                                }
                            }

                            Log.d("tag", String.valueOf(rate));
                            Log.d("tag", String.valueOf(count));
                            if (rate != 0 && count != 0) {
                                int tvRate = (int) (rate / count);
                                holder.itemTvClintRating.setText(String.valueOf(tvRate));
                            } else {
                                holder.itemTvClintRating.setText("0");
                            }
                        }
                    }
                });

    }

    @Override
    public int getItemCount( ) {
        return postList.size();
    }

    class myViewHolder extends RecyclerView.ViewHolder{
        AppCompatTextView PostDuration;
        LinearLayout LL_item;
        AppCompatTextView PostDescription;
        AppCompatTextView PostBudget;
        MaterialTextView ClosedJob;
        AppCompatTextView PostTitle;
        AppCompatTextView PostTime;
        AppCompatTextView PostLoc;
        TextView ClintName ,itemTvClintRating;
        RecyclerView CategoryRecycle;
        ImageView clintImage;
        MaterialTextView OffersCount;
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
            itemTvClintRating = binding.itemTvClintRating;
        }
    }
}
