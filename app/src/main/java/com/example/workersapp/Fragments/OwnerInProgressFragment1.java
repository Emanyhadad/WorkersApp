package com.example.workersapp.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.workersapp.Activities.PostActivity2;
import com.example.workersapp.Adapters.WorkInProgressAdapter;
import com.example.workersapp.R;
import com.example.workersapp.Utilities.Post;
import com.example.workersapp.databinding.FragmentBlank5Binding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class OwnerInProgressFragment1 extends Fragment {
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    List<String> categoryList;
    List<Post> postList;
    long addedTime;

    String jobState, title, description, expectedWorkDuration, projectedBudget, jobLocation;

    FragmentBlank5Binding binding;

    public OwnerInProgressFragment1() {
    }


    public static OwnerInProgressFragment1 newInstance() {
        OwnerInProgressFragment1 fragment = new OwnerInProgressFragment1();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentBlank5Binding.inflate(inflater, container, false);
        firebaseFirestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        binding.inculd.tvPageTitle.setText("وظائف  قيد العمل");
        binding.inculd.editIcon.setVisibility(View.GONE);
        categoryList = new ArrayList<>();
        postList = new ArrayList<>();
        firebaseFirestore.collection("posts")
                .document(firebaseUser.getPhoneNumber())
                .collection("userPost")
                .whereEqualTo("jobState", "inWork")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.getResult().isEmpty()) {
                        binding.ProgressBar.setVisibility(View.GONE);
                        binding.LLEmpty.setVisibility(View.VISIBLE);
                        binding.btnAddpost.setOnClickListener(v -> {
                            // Replace the current fragment with the new fragment here
                            FragmentManager fragmentManager = getParentFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            NewJobFragment jobFragment = new NewJobFragment();
                            fragmentTransaction.replace(R.id.container, jobFragment);
                            fragmentTransaction.addToBackStack(null); // Add to back stack to allow user to navigate back to this fragment
                            fragmentTransaction.commit();
                        });
                    } else {
                        for (DocumentSnapshot document : task.getResult()) {
                            firebaseFirestore.document("posts/"
                                            + firebaseUser.getPhoneNumber() + "/userPost/"
                                            + document.getId()).get()
                                    .addOnSuccessListener(documentSnapshot -> {
                                        binding.ProgressBar.setVisibility(View.GONE);
                                        binding.LLEmpty.setVisibility(View.GONE);
                                        binding.RV.setVisibility(View.VISIBLE);
                                        if (documentSnapshot.exists()) {

                                            jobState = documentSnapshot.getString("jobState");
                                            title = documentSnapshot.getString("title");
                                            description = documentSnapshot.getString("description");
                                            List<String> images = (List<String>) documentSnapshot.get("images");
                                            List<String> categoriesList = (List<String>) documentSnapshot.get("categoriesList");

                                            expectedWorkDuration = documentSnapshot.getString("expectedWorkDuration");
                                            projectedBudget = documentSnapshot.getString("projectedBudget");
                                            jobLocation = documentSnapshot.getString("jobLocation");
                                            addedTime = documentSnapshot.getLong("addedTime");

                                            Post post = new Post(title, description, images, categoriesList, expectedWorkDuration, projectedBudget, jobLocation, jobState, addedTime);
                                            post.setPostId(document.getId());
                                            post.setOwnerId(firebaseUser.getPhoneNumber());
                                            post.setWorkerId(documentSnapshot.getString("workerId"));
                                            if (jobState.equals("inWork")) {
                                                postList.add(post);
                                                binding.RV.setAdapter(new WorkInProgressAdapter(postList, getContext(), pos -> {
                                                    Log.e("ItemClik", postList.get(pos).getPostId());
                                                    Intent intent = new Intent(getActivity(), PostActivity2.class);
                                                    intent.putExtra("PostId", postList.get(pos).getPostId()); // pass data to new activity
                                                    startActivity(intent);
                                                }));

                                            }


                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("Field", e.getMessage());
                                    });
                            binding.RV.setLayoutManager(new LinearLayoutManager(getContext(),
                                    LinearLayoutManager.VERTICAL, false));


                        }
                    }
                }).addOnFailureListener(runnable -> {
                });


        return binding.getRoot();
    }
}