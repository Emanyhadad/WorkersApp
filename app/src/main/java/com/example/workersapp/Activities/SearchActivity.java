package com.example.workersapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.workersapp.Adapters.Post_forWorkerAdapter;
import com.example.workersapp.Utilities.Post;
import com.example.workersapp.databinding.ActivitySearchBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

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

        binding.searchEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    binding.ProgressBarSearch.setVisibility(View.VISIBLE);
                    String searchTerm = binding.searchEt.getText().toString();
                    Toast.makeText(SearchActivity.this, "searchTerm: " + searchTerm, Toast.LENGTH_SHORT).show();
                    SearchTitle(searchTerm);

                    return true;
                }
                return false;
            }
        });




    }

    void SearchTitle(String searchTerm) {

        firebaseFirestore.collection("users").get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                firebaseFirestore.collection("posts").document(documentSnapshot.getId()).collection("userPost")
                        .whereEqualTo("title", searchTerm)

                        .get().addOnSuccessListener(queryDocumentSnapshots1 -> {

                            Log.d("query", queryDocumentSnapshots1.getDocuments() + "");
                            postList.clear();
                            for (DocumentSnapshot documentSnapshot1 : queryDocumentSnapshots1.getDocuments()) {
                                String title = documentSnapshot1.getString("title");
                                Log.d("title", title);
                                Post post = documentSnapshot1.toObject(Post.class);
                                postList.add(post);
                                Log.d("documentActivity", documentSnapshot1.getData().toString());
                                Log.d("postList", postList.size() + "");

                            }

                            binding.RVSearch.setAdapter(postAdapter);

                            binding.RVSearch.setLayoutManager(new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false));
                            postAdapter.notifyDataSetChanged();
                            binding.RVSearch.setVisibility(View.VISIBLE);
                            binding.imageView7.setVisibility(View.GONE);
                            binding.ProgressBarSearch.setVisibility(View.GONE);

                        });
            }
        });
    }

}