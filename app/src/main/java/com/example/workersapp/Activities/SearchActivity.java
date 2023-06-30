package com.example.workersapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.workersapp.Adapters.Post_forWorkerAdapter;
import com.example.workersapp.Utilities.Post;
import com.example.workersapp.databinding.ActivitySearchBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class SearchActivity extends AppCompatActivity {
    ActivitySearchBinding binding;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    List<Post> postList;
    Post_forWorkerAdapter postAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        postList = new ArrayList<>();

        postAdapter = new Post_forWorkerAdapter(postList, getBaseContext(), pos -> {
            Intent intent = new Intent(getBaseContext(), PostActivity_forWorker.class);
            intent.putExtra("PostId", postList.get(pos).getPostId());
            intent.putExtra("OwnerId", postList.get(pos).getOwnerId());
            startActivity(intent);
        });

        binding.RVSearch.setAdapter(postAdapter);

        binding.searchEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    String searchTerm = binding.searchEt.getText().toString();
                    SearchTitle(searchTerm);
                    return true;
                }
                return false;
            }
        });
    }

    private void SearchTitle(String searchTerm) {
        try {
            final AtomicBoolean hasResults = new AtomicBoolean(false);

            binding.ProgressBarSearch.setVisibility(View.VISIBLE);

            firebaseFirestore.collection("posts")
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                String phoneNumber = document.getId();
                                System.out.println(phoneNumber);

                                firebaseFirestore.collection("posts")
                                        .document(phoneNumber)
                                        .collection("userPost")
                                        .whereEqualTo("title", searchTerm)
                                        .get()
                                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                            @Override
                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                postList.clear();
                                                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                                    Post post = document.toObject(Post.class);
                                                    postList.add(post);
                                                    System.out.println(post.getDescription());
                                                    Log.d("documentActivity", document.getData().toString());
                                                }

                                                if (!queryDocumentSnapshots.isEmpty()) {
                                                    hasResults.set(true);
                                                }

                                                if (postList.isEmpty() || !hasResults.get()) {
                                                    SearchDescription(searchTerm);
                                                } else {
                                                    updateRecyclerView();
                                                }
                                            }
                                        });
                            }
                        }
                    });

        } catch (Exception e) {
            System.out.printf(e.toString());
        }

    }


    private void SearchDescription(String searchTerm) {
        try {
            final AtomicBoolean hasResults = new AtomicBoolean(false);

            firebaseFirestore.collection("posts")
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            postList.clear();


                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                String phoneNumber = document.getId();
                                System.out.println(phoneNumber);

                                firebaseFirestore.collection("posts")
                                        .document(phoneNumber)
                                        .collection("userPost")
                                        .whereEqualTo("description",searchTerm)
                                        .get()
                                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                            @Override
                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                                    Post post = document.toObject(Post.class);
                                                    postList.add(post);
                                                    System.out.println(post.getDescription());
                                                    Log.d("documentActivity", document.getData().toString());
                                                }

                                                if (!queryDocumentSnapshots.isEmpty()) {
                                                    hasResults.set(true);
                                                }

                                                if (postList.isEmpty() || !hasResults.get()) {
                                                    SearchCategory(searchTerm);
                                                } else {
                                                    updateRecyclerView();
                                                }
                                            }
                                        });
                            }
                        }
                    });

        } catch (Exception e) {
            System.out.printf(e.toString());
        }

    }

    private void SearchCategory(String searchTerm) {
        try {
            final AtomicBoolean hasResults = new AtomicBoolean(false);

            firebaseFirestore.collection("posts")
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            postList.clear();


                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                String phoneNumber = document.getId();
                                System.out.println(phoneNumber);

                                firebaseFirestore.collection("posts")
                                        .document(phoneNumber)
                                        .collection("userPost")
                                        .whereArrayContains("categoriesList", searchTerm)
                                        .get()
                                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                            @Override
                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                                    Post post = document.toObject(Post.class);
                                                    postList.add(post);
                                                    System.out.println(post.getDescription());
                                                    Log.d("documentActivity", document.getData().toString());
                                                }

                                                if (!queryDocumentSnapshots.isEmpty()) {
                                                    hasResults.set(true);
                                                }

                                                if (postList.isEmpty() || !hasResults.get()) {
                                                    binding.imageView7.setVisibility(View.VISIBLE);
                                                    binding.RVSearch.setVisibility(View.GONE);
                                                    binding.ProgressBarSearch.setVisibility(View.GONE);
                                                } else {
                                                    updateRecyclerView();
                                                }
                                            }
                                        });
                            }
                        }
                    });

        } catch (Exception e) {
            System.out.printf(e.toString());
        }

    }

    private void updateRecyclerView() {
        binding.RVSearch.setLayoutManager(new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false));
        postAdapter.notifyDataSetChanged();
        binding.RVSearch.setVisibility(View.VISIBLE);
        binding.imageView7.setVisibility(View.GONE);
        binding.ProgressBarSearch.setVisibility(View.GONE);

        if (postList.isEmpty()) {
            binding.imageView7.setVisibility(View.VISIBLE);
            binding.RVSearch.setVisibility(View.GONE);
            binding.ProgressBarSearch.setVisibility(View.GONE);
        }

    }

}
