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

import com.example.workersapp.Activities.PostActivity_forWorker;
import com.example.workersapp.Adapters.WorkInProgressAdapter;
import com.example.workersapp.R;
import com.example.workersapp.Utilities.Offer;
import com.example.workersapp.Utilities.Post;
import com.example.workersapp.databinding.FragmentBlank2Binding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;


public class WorkerInProgressFragment extends Fragment {
    FirebaseFirestore firebaseFirestore;
    List <String> categoryList;
    List< Post > postList;
    FirebaseAuth firebaseAuth;    List< Offer > offerList;

    FirebaseUser firebaseUser;
    String jobState,title,description,expectedWorkDuration,projectedBudget,jobLocation;
    long addedTime;
    boolean getData;

    public WorkerInProgressFragment() {
    }

    public static WorkerInProgressFragment newInstance() {
        WorkerInProgressFragment fragment = new WorkerInProgressFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) { } }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentBlank2Binding binding= FragmentBlank2Binding.inflate( getLayoutInflater() );
        firebaseFirestore=FirebaseFirestore.getInstance();
        categoryList=new ArrayList <>(  );
        postList = new ArrayList <>(  );
        offerList = new ArrayList<>();
        List<String> count= new ArrayList <>(  );
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        firebaseFirestore.collection("users")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        firebaseFirestore.collection("posts")
                                .document(documentSnapshot.getId())
                                .collection("userPost")
                                .whereEqualTo("jobState", "inWork")
                                .whereEqualTo("workerId", firebaseUser.getPhoneNumber())
                                .get()
                                .addOnSuccessListener(queryDocumentSnapshots1 -> {
                                    Log.e( "queryDocumentSnapshots1",queryDocumentSnapshots1.size()+"" );
                                        for (DocumentSnapshot postDocumentSnapshot : queryDocumentSnapshots1) {
                                            if (postDocumentSnapshot.exists()) {
                                                jobState = postDocumentSnapshot.getString("jobState");
                                                title = postDocumentSnapshot.getString("title");
                                                description = postDocumentSnapshot.getString("description");
                                                List<String> images = (List<String>) postDocumentSnapshot.get("images");
                                                List<String> categoriesList = (List<String>) postDocumentSnapshot.get("categoriesList");

                                                expectedWorkDuration = postDocumentSnapshot.getString("expectedWorkDuration");
                                                projectedBudget = postDocumentSnapshot.getString("projectedBudget");
                                                jobLocation = postDocumentSnapshot.getString("jobLocation");

                                                if (postDocumentSnapshot.contains("addedTime")) {
                                                    addedTime = postDocumentSnapshot.getLong("addedTime");
                                                    // Perform any additional operations on addedTime here
                                                } else {
                                                    // Execute appropriate error handling code or set a default value for addedTime
                                                }

                                                Post post = new Post(title, description, images, categoriesList, expectedWorkDuration, projectedBudget, jobLocation, jobState, addedTime);
                                                post.setPostId(postDocumentSnapshot.getId());
                                                post.setOwnerId(firebaseUser.getPhoneNumber());
                                                post.setWorkerId(postDocumentSnapshot.getString("workerId"));
                                                postList.add(post);
                                            }
                                            Log.e( "Post",postList.size()+"" );
                                        }
                                        Log.e( "Post1",postList.size()+"" );

                                        if (postList.size()!=0) {
                                            binding.LLEmptyWorker.setVisibility(View.GONE);
                                            binding.progressBar5.setVisibility( View.GONE );
                                            binding.RV.setVisibility( View.VISIBLE );
                                            binding.RV.setAdapter(new WorkInProgressAdapter(postList, getContext(), pos -> {
                                                Log.e("ItemClick", postList.get(pos).getPostId());
                                                Intent intent = new Intent(getActivity(), PostActivity_forWorker.class);
                                                intent.putExtra("PostId", postList.get(pos).getPostId());
                                                intent.putExtra("OwnerId", postList.get(pos).getOwnerId());
                                                startActivity(intent);
                                            }));
                                            binding.RV.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                                        }else {
                                            binding.LLEmptyWorker.setVisibility(View.VISIBLE);
                                            binding.progressBar5.setVisibility(View.GONE);
                                            binding.RV.setVisibility( View.GONE );
                                            binding.btnAddpost.setOnClickListener(v -> {
                                                // Replace the current fragment with the new fragment here
                                                FragmentManager fragmentManager = getParentFragmentManager();
                                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                                PostFragment_inWorker jobFragment = new PostFragment_inWorker();
                                                fragmentTransaction.replace(R.id.frame, jobFragment);
                                                fragmentTransaction.addToBackStack(null); // Add to back stack to allow user to navigate back to this fragment
                                                fragmentTransaction.commit();
                                            });
                                        }

                                    getData=true;
                                })
                                .addOnFailureListener(runnable -> {
                                    binding.RV.setVisibility( View.GONE );
                                    binding.LLEmptyWorker.setVisibility(View.VISIBLE);
                                    binding.progressBar5.setVisibility(View.GONE);
                                    binding.btnAddpost.setOnClickListener(v -> {
                                        // Replace the current fragment with the new fragment here
                                        FragmentManager fragmentManager = getParentFragmentManager();
                                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                        PostFragment_inWorker jobFragment = new PostFragment_inWorker();
                                        fragmentTransaction.replace(R.id.frame, jobFragment);
                                        fragmentTransaction.addToBackStack(null); // Add to back stack to allow user to navigate back to this fragment
                                        fragmentTransaction.commit();
                                    });
                                });
                    }
                });





        return binding.getRoot();
    }
}