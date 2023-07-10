package com.example.workersapp.Adapters;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
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
import com.example.workersapp.Utilities.NativeTemplateStyle;
import com.example.workersapp.Utilities.Post;
import com.example.workersapp.Utilities.TemplateView;
import com.example.workersapp.databinding.ItemPostBinding;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class Post_forWorkerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<Post> postList;
    FirebaseFirestore firestore;
    public static SharedPreferences sp;
    public static SharedPreferences.Editor editor;
    Context context;
    ItemClickListener listener;
    Post currentPost;
    private FavoriteItemClickListener favoriteItemClickListener;
    //
    private static final int IS_AD = 0;
    private static final int NOT_Ad = 1;

    private final ArrayList<Object> objects = new ArrayList<>();

    public void setList(List<Post> list) {
        this.objects.addAll(list);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setAd(List<NativeAd> nativeAd) {
        this.objects.addAll(nativeAd);
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setObject(ArrayList<Object> object) {
        this.objects.clear();
        this.objects.addAll(object);
        notifyDataSetChanged();
    }

    //
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
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

//        if(viewType == IS_AD){
//            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ad,parent,false);
//            return new AdViewHolder(view);
//        }else{
//            ItemPostBinding binding = ItemPostBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
//            return new myViewHolder(binding);
//        }
        ItemPostBinding binding = ItemPostBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new myViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {


        myViewHolder ivh = (myViewHolder) holder;

        firestore = FirebaseFirestore.getInstance();
        sp = context.getSharedPreferences("shared", MODE_PRIVATE);
        editor = sp.edit();
        currentPost = (Post) postList.get(holder.getAdapterPosition());

        ivh.PostTitle.setText(currentPost.getTitle());
        ivh.PostDescription.setText(currentPost.getDescription());
        ivh.PostBudget.setText(currentPost.getProjectedBudget());
        ivh.PostLoc.setText(currentPost.getJobLocation());
        ivh.favoriteButton.setVisibility(View.VISIBLE);

        //Todo: Put Post Time her
        ivh.CategoryRecycle.setAdapter(new ShowCategoryAdapter((ArrayList<String>) currentPost.getCategoriesList()));
        ivh.CategoryRecycle.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        long currentTimeMillis = System.currentTimeMillis();
        long storageTimeMillis = currentPost.getAddedTime();
        long timeDifferenceMillis = currentTimeMillis - storageTimeMillis;

        long seconds = timeDifferenceMillis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;

        String timeDifference;
        if (hours > 0) {
            timeDifference = hours + " قبل ساعة";
        } else if (minutes > 0) {
            timeDifference = minutes + " قبل دقيقة";
        } else {
            timeDifference = seconds + " قبل ثانية";
        }

        ivh.PostTime.setText(timeDifference);


        //Todo: Put Post Time her
        ivh.CategoryRecycle.setAdapter(new ShowCategoryAdapter((ArrayList<String>) currentPost.getCategoriesList()));
        ivh.CategoryRecycle.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));

        ivh.favoriteButton.setOnClickListener(new View.OnClickListener() {
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
            ivh.favoriteButton.setImageResource(R.drawable.ic_favorite);
        } else {
            ivh.favoriteButton.setImageResource(R.drawable.ic_favorite_border);
        }

        currentPost.getPostId();
        ivh.LL_item.setOnClickListener(view -> listener.OnClick(holder.getAdapterPosition()));

        firestore.collection("users").document(currentPost.getOwnerId())
                .get()
                .addOnSuccessListener(documentSnapshot1 -> {
                    if (documentSnapshot1.exists()) {
                        String fullName = documentSnapshot1.getString("fullName");
                        ivh.ClintName.setText(fullName);
                        String image = documentSnapshot1.getString("image");
                        Glide.with(context)
                                .load(image)
                                .circleCrop()
                                .error(R.drawable.worker)
                                .into(ivh.clintImage);
                    }
                });



        firestore.collection("offers").document(currentPost.getPostId()).
                collection("workerOffers")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int count = task.getResult().size();
                        ivh.OffersCount.setText(count == 0 ? "0" : String.valueOf(count));
                    }
                });
//        if(getItemViewType(position)==IS_AD){
//            AdViewHolder adv =  (AdViewHolder) holder;
//            adv.setNativeAd((NativeAd) objects.get(position));
//        }else{
//            myViewHolder ivh = (myViewHolder) holder;
//
//            firestore = FirebaseFirestore.getInstance();
//            sp = context.getSharedPreferences("shared", MODE_PRIVATE);
//            editor = sp.edit();
//            currentPost = (Post) objects.get(holder.getAdapterPosition());
//
//            ivh.PostTitle.setText(currentPost.getTitle());
//            ivh.PostDescription.setText(currentPost.getDescription());
//            ivh.PostBudget.setText(currentPost.getProjectedBudget());
//            ivh.PostLoc.setText(currentPost.getJobLocation());
//            ivh.favoriteButton.setVisibility(View.VISIBLE);
//
//            //Todo: Put Post Time her
//            ivh.CategoryRecycle.setAdapter(new ShowCategoryAdapter((ArrayList<String>) currentPost.getCategoriesList()));
//            ivh.CategoryRecycle.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
//
//            ivh.favoriteButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    int adapterPosition = holder.getAdapterPosition();
//                    Post clickedPost = postList.get(adapterPosition);
//
//                    if (clickedPost.isFavorite()) {
//                        removeImageFromFavorites(clickedPost);
//                        editor.putBoolean(clickedPost.getPostId(), false).apply();
//                        if (favoriteItemClickListener != null) {
//                            favoriteItemClickListener.onFavoriteItemRemoved(adapterPosition);
//                        }
//                    } else {
//                        addImageToFavorites(clickedPost);
//                        editor.putBoolean(clickedPost.getPostId(), true).apply();
//                    }
//
//                    notifyDataSetChanged();
//                }
//            });
//
//
//            boolean isFavorite = sp.getBoolean(currentPost.getPostId(), false);
//            if (isFavorite) {
//                ivh.favoriteButton.setImageResource(R.drawable.ic_favorite);
//            } else {
//                ivh.favoriteButton.setImageResource(R.drawable.ic_favorite_border);
//            }
//
//            currentPost.getPostId();
//            ivh.LL_item.setOnClickListener(view -> listener.OnClick(holder.getAdapterPosition()));
//            firestore.collection("users").document(currentPost.getOwnerId())
//                    .get()
//                    .addOnSuccessListener(documentSnapshot1 -> {
//                        if (documentSnapshot1.exists()) {
//                            String fullName = documentSnapshot1.getString("fullName");
//                            ivh.ClintName.setText(fullName);
//                            String image = documentSnapshot1.getString("image");
//                            Glide.with(context)
//                                    .load(image)
//                                    .circleCrop()
//                                    .error(R.drawable.worker)
//                                    .into(ivh.clintImage);
//                        }
//                    })
//                    .addOnFailureListener(e -> {
//                    });
//
//
//            firestore.collection("offers").document(currentPost.getPostId()).
//                    collection("workerOffers")
//                    .get()
//                    .addOnCompleteListener(task -> {
//                        if (task.isSuccessful()) {
//                            int count = task.getResult().size();
//                            ivh.OffersCount.setText(count == 0 ? "0" : String.valueOf(count));
//                        }
//                    });
//        }

    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

//    @Override
//    public int getItemViewType(int position) {
//        if (objects.get(position) instanceof NativeAd) {
//            return IS_AD;
//        } else {
//            return NOT_Ad;
//        }
//    }

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

//    public class AdViewHolder extends RecyclerView.ViewHolder {
//        public TemplateView template;
//
//        public AdViewHolder(@NonNull View itemView) {
//            super(itemView);
//
//            template = itemView.findViewById(R.id.id_ad_template_view);
//
//            NativeTemplateStyle styles = new
//                    NativeTemplateStyle.Builder().build();
//
//            template.setStyles(styles);
//        }
//
//        public void setNativeAd(NativeAd nativeAd) {
//
//            template.setNativeAd(nativeAd);
//        }
//    }

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