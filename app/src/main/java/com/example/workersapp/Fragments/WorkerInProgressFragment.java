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
import java.util.Objects;


public class WorkerInProgressFragment extends Fragment {
    FirebaseFirestore firebaseFirestore;
    List <String> categoryList;
    List< Post > postList;
    FirebaseAuth firebaseAuth;    List< Offer > offerList;

    FirebaseUser firebaseUser;
    String jobState,title,description,expectedWorkDuration,projectedBudget,jobLocation;
    long addedTime;


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

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        List<DocumentSnapshot> documentList = new ArrayList<>();

        firebaseFirestore.collection("users").get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                firebaseFirestore.collection("posts").document(documentSnapshot.getId())
                        .collection("userPost").whereEqualTo( "jobState","inWork" )
                        .get().addOnSuccessListener(queryDocumentSnapshots1 -> {
                            if ( queryDocumentSnapshots1.isEmpty() ){
                                binding.LLEmptyWorker.setVisibility( View.VISIBLE );
                                binding.btnAddpost.setOnClickListener(v -> {
                                    // Replace the current fragment with the new fragment here
                                    FragmentManager fragmentManager = getParentFragmentManager();
                                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                    PostFragment_inWorker jobFragment = new PostFragment_inWorker();
                                    fragmentTransaction.replace( R.id.frame, jobFragment);
                                    fragmentTransaction.addToBackStack(null); // Add to back stack to allow user to navigate back to this fragment
                                    fragmentTransaction.commit();
                                });

                            }
                            for (DocumentSnapshot postDocumentSnapshot : queryDocumentSnapshots1) {
                                if (  postDocumentSnapshot.get( "workerId" ).equals( firebaseUser.getPhoneNumber() ) ){
                                    jobState = postDocumentSnapshot.getString("jobState");
                                    title = postDocumentSnapshot.getString("title");
                                    description= postDocumentSnapshot.getString( "description" );
                                    List<String> images = (List<String>) postDocumentSnapshot.get("images");
                                    List<String> categoriesList = (List<String>) postDocumentSnapshot.get("categoriesList");

                                    expectedWorkDuration= postDocumentSnapshot.getString( "expectedWorkDuration" );
                                    projectedBudget= postDocumentSnapshot.getString( "projectedBudget" );
                                    jobLocation= postDocumentSnapshot.getString( "jobLocation" );
                                    addedTime = documentSnapshot.getLong("addedTime");

                                    Post post = new Post( title,description,images,categoriesList,expectedWorkDuration,projectedBudget,jobLocation,jobState,addedTime );
                                    post.setPostId( postDocumentSnapshot.getId() );
                                    post.setOwnerId( firebaseUser.getPhoneNumber() );
                                    post.setWorkerId( postDocumentSnapshot.getString( "workerId" ) );
                                    postList.add( post );
                                    binding.RV.setAdapter( new WorkInProgressAdapter( postList , getContext( ), pos -> {
                                        Log.e( "ItemClik",postList.get( pos ).getPostId());
                                        Intent intent = new Intent(getActivity(), PostActivity_forWorker.class);
                                        intent.putExtra("PostId", postList.get(pos).getPostId());
                                        intent.putExtra("OwnerId", postList.get(pos).getOwnerId());
                                        startActivity(intent);
                                    } ));
                                }

                            }
                        });
            }

            binding.progressBar5.setVisibility(View.GONE);
            binding.RV.setVisibility(View.VISIBLE);
            binding.RV.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        });





        return binding.getRoot();
    }
}