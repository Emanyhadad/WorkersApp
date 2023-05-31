package com.example.workersapp.Activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.workersapp.Adapters.Post_forWorkerAdapter;
import com.example.workersapp.Listeneres.ItemClickListener;
import com.example.workersapp.Utilities.Post;
import com.example.workersapp.databinding.ActivityFavouriteBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class FavouriteActivity extends AppCompatActivity {
    ActivityFavouriteBinding binding;
    FirebaseFirestore firestore;
    private Post_forWorkerAdapter adapter;
    List<Post> favouritePosts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFavouriteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.inculd.editIcon.setVisibility( View.GONE );
        binding.inculd.tvPageTitle.setText( "الوظائف المحفوظة" );
        firestore = FirebaseFirestore.getInstance();
        favouritePosts = new ArrayList<>();

        firestore.collection("offers")
                .document("favorites")
                .collection("favorites")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        favouritePosts = queryDocumentSnapshots.toObjects(Post.class);

                        adapter = new Post_forWorkerAdapter(favouritePosts, FavouriteActivity.this, new ItemClickListener() {
                            @Override
                            public void OnClick(int position) {

                            }
                        }, new Post_forWorkerAdapter.FavoriteItemClickListener() {
                            @Override
                            public void onFavoriteItemRemoved(int position) {
                                Post removedPost = favouritePosts.get(position);
                                adapter.removeImage(removedPost);
                                favouritePosts.remove(removedPost);
                                adapter.notifyDataSetChanged();

                                firestore.collection("offers")
                                        .document("favorites")
                                        .collection("favorites")
                                        .document(removedPost.getPostId())
                                        .delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d("FavouriteActivity","Item removed successfully");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.d("FavouriteActivityFail","Failed to remove item");
                                            }
                                        });
                            }
                        });
                        binding.FavRv.setLayoutManager(new LinearLayoutManager(FavouriteActivity.this));
                        binding.FavRv.setAdapter(adapter);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(FavouriteActivity.this, "Failed to retrieve favourite posts", Toast.LENGTH_SHORT).show();
                    }
                });
    }



}