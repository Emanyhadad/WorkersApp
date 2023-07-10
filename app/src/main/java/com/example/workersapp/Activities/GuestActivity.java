package com.example.workersapp.Activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.workersapp.Adapters.Post_forWorkerAdapter;
import com.example.workersapp.R;
import com.example.workersapp.Utilities.Post;
import com.example.workersapp.databinding.ActivityGuestBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class GuestActivity extends AppCompatActivity {
    ActivityGuestBinding binding;
    FirebaseFirestore firebaseFirestore;

    AlertDialog.Builder builder;
    Post_forWorkerAdapter postAdapter;
    List<String> categoryList;
    List<Post> postList;
    long addedTime;
    String jobState, title, description, expectedWorkDuration, projectedBudget, jobLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGuestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseFirestore = FirebaseFirestore.getInstance();

        binding.inculd.tvPageTitle.setText(getString(R.string.jobs));

        categoryList = new ArrayList<>();
        postList = new ArrayList<>();

        firebaseFirestore.collection("users").get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                firebaseFirestore.collection("posts").document(documentSnapshot.getId()).collection("userPost")
                        .whereEqualTo("jobState", "open")
                        .orderBy("addedTime", Query.Direction.DESCENDING)
                        .get().addOnSuccessListener(queryDocumentSnapshots1 -> {
                            for (DocumentSnapshot documentSnapshot1 : queryDocumentSnapshots1.getDocuments()) {
                                jobState = documentSnapshot1.getString("jobState");
                                title = documentSnapshot1.getString("title");
                                description = documentSnapshot1.getString("description");
                                List<String> images = (List<String>) documentSnapshot1.get("images");
                                List<String> categoriesList = (List<String>) documentSnapshot1.get("categoriesList");

                                expectedWorkDuration = documentSnapshot1.getString("expectedWorkDuration");
                                projectedBudget = documentSnapshot1.getString("projectedBudget");
                                jobLocation = documentSnapshot1.getString("jobLocation");
                                addedTime = documentSnapshot1.getLong("addedTime");

                                Post post = new Post(title, description, images, categoriesList, expectedWorkDuration, projectedBudget, jobLocation, jobState, addedTime);
                                post.setPostId(documentSnapshot1.getId());
                                post.setOwnerId(documentSnapshot.getId());

                                postList.add(post);
                                postAdapter = new Post_forWorkerAdapter(postList, getBaseContext(), pos -> {
                                    Log.e("ItemClik", postList.get(pos).getPostId());
                                    RegisterDialog();
                                });

                                }
                            binding.RV.setAdapter(postAdapter);
                            binding.ProgressBar.setVisibility(View.GONE);
                            binding.RV.setVisibility(View.VISIBLE);
                            binding.RV.setLayoutManager(new LinearLayoutManager(getBaseContext(),
                                    LinearLayoutManager.VERTICAL, false));
                        });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                binding.LLNoWifi.setVisibility(View.VISIBLE);
                binding.ProgressBar.setVisibility(View.GONE);
            }
        });

        binding.etSearch.setOnClickListener(view -> RegisterDialog());

        binding.favoriteBtn.setOnClickListener(view -> RegisterDialog());

    }

    void RegisterDialog() {
        builder = new AlertDialog.Builder(GuestActivity.this);
        builder.setMessage(getString(R.string.DoNotHave))
                .setNegativeButton(getString(R.string.tvInAnotherTime), (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    builder = null;
                }).setPositiveButton(getString(R.string.tvRegisterNow), (dialogInterface, i) -> {
                    startActivity(new Intent(getBaseContext(), LoginActivity.class));
                    finish();
                    dialogInterface.dismiss();
                    builder = null;
                }).setCancelable(false).create().show();
    }

}