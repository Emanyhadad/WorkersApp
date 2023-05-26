package com.example.workersapp.Adapters;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.workersapp.Listeneres.ItemClickListener;
import com.example.workersapp.R;
import com.example.workersapp.Utilities.Post;
import com.example.workersapp.databinding.ItemPostBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class Post_forWorkerAdapter extends RecyclerView.Adapter<Post_forWorkerAdapter.myViewHolder> {
    List<Post> postList;
    FirebaseFirestore firestore;
    public static SharedPreferences sp;
    public static SharedPreferences.Editor editor;
    Context context;
    ItemClickListener listener;
    Post currentPost;
    private FavoriteItemClickListener favoriteItemClickListener;


    public Post_forWorkerAdapter(List<Post> postList, Context context, ItemClickListener listener) {
        this.postList = postList;
        this.context = context;
        this.listener = listener;
    }

    public Post_forWorkerAdapter(List<Post> postList, Context context, ItemClickListener listener, FavoriteItemClickListener favoriteItemClickListener) {
        this.postList = postList;
        this.context = context;
        this.listener = listener;
        this.favoriteItemClickListener = favoriteItemClickListener;
    }

    public void removeImage(Post post) {
        post.setFavorite(false);
        postList.remove(post);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPostBinding binding = ItemPostBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new myViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
        firestore = FirebaseFirestore.getInstance();
        sp = context.getSharedPreferences("shared", MODE_PRIVATE);
        editor = sp.edit();
        currentPost = postList.get(holder.getAdapterPosition());

        holder.PostTitle.setText(currentPost.getTitle());
        holder.PostDescription.setText(currentPost.getDescription());
        holder.PostBudget.setText(currentPost.getProjectedBudget());
        holder.PostLoc.setText(currentPost.getJobLocation());
        holder.favoriteButton.setVisibility(View.VISIBLE);

        //Todo: Put Post Time her
        holder.CategoryRecycle.setAdapter(new ShowCategoryAdapter((ArrayList<String>) currentPost.getCategoriesList()));
        holder.CategoryRecycle.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));

        holder.favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int adapterPosition = holder.getAdapterPosition();
                Post clickedPost = postList.get(adapterPosition);

                if (clickedPost.isFavorite()) {
                    removeImageFromFavorites(clickedPost);
                    editor.putBoolean(clickedPost.getPostId(), false).apply();
                    if (favoriteItemClickListener != null) {
                        favoriteItemClickListener.onFavoriteItemRemoved(adapterPosition);
                    }
                } else {
                    addImageToFavorites(clickedPost);
                    editor.putBoolean(clickedPost.getPostId(), true).apply();
                }

                notifyDataSetChanged();
            }
        });


        boolean isFavorite = sp.getBoolean(currentPost.getPostId(), false);
        if (isFavorite) {
            holder.favoriteButton.setImageResource(R.drawable.ic_favorite);
        } else {
            holder.favoriteButton.setImageResource(R.drawable.ic_favorite_border);
        }

        currentPost.getPostId();
        holder.LL_item.setOnClickListener(view -> listener.OnClick(holder.getAdapterPosition()));
        firestore.collection("users").document(currentPost.getOwnerId())
                .get()
                .addOnSuccessListener(documentSnapshot1 -> {
                    if (documentSnapshot1.exists()) {
                        String fullName = documentSnapshot1.getString("fullName");
                        holder.ClintName.setText(fullName);
                        String image = documentSnapshot1.getString("image");
                        Glide.with(context)
                                .load(image)
                                .circleCrop()
                                .error(R.drawable.worker)
                                .into(holder.clintImage);
                    }
                })
                .addOnFailureListener(e -> {
                });


        firestore.collection("offers").document(currentPost.getPostId()).
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
    public int getItemCount() {
        return postList.size();
    }

    static class myViewHolder extends RecyclerView.ViewHolder {
        LinearLayout LL_item;
        AppCompatTextView PostDescription, PostBudget, PostTitle, PostTime, PostLoc;
        MaterialTextView ClosedJob, OffersCount;
        TextView ClintName;
        RecyclerView CategoryRecycle;
        ImageView clintImage;
        ImageButton favoriteButton;

        public myViewHolder(@NonNull ItemPostBinding binding) {
            super(binding.getRoot());
            PostTitle = binding.tvPostTitle;
            PostDescription = binding.tvPostDec;
            PostBudget = binding.tvPostBudget;
            PostLoc = binding.tvPostLoc;
            PostTime = binding.tvPostTime;
            CategoryRecycle = binding.CategoryRecycle;
            ClosedJob = binding.tvClosedJob;
            LL_item = binding.LLItem;
            ClintName = binding.tvClintName;
            clintImage = binding.itemImgClint;
            OffersCount = binding.tvCountOffers;
            favoriteButton = binding.favoriteButton;
        }
    }


    public void addImageToFavorites(Post post) {
        post.setFavorite(true);

        firestore.collection("offers")
                .document("favorites")
                .collection("favorites")
                .document(post.getPostId())
                .set(post)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Image added to favorites successfully with ID: " + post.getPostId());
                        Toast.makeText(context, "Added to favorites", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding image to favorites", e);
                        Toast.makeText(context, "Failed to add to favorites", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void removeImageFromFavorites(Post post) {
        post.setFavorite(false);

        firestore.collection("offers")
                .document("favorites")
                .collection("favorites")
                .document(post.getPostId())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Image removed from favorites with ID: " + post.getPostId());
                        Toast.makeText(context, "Removed from favorites", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error removing image from favorites", e);
                        Toast.makeText(context, "Failed to remove from favorites", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public interface FavoriteItemClickListener {
        void onFavoriteItemRemoved(int position);
    }

}
