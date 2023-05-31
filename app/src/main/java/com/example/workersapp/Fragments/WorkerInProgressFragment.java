package com.example.workersapp.Fragments;

import static androidx.constraintlayout.widget.ConstraintLayoutStates.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.workersapp.Activities.PostActivity2;
import com.example.workersapp.Activities.PostActivity_forWorker;
import com.example.workersapp.Adapters.PostAdapter;
import com.example.workersapp.Adapters.Post_forWorkerAdapter;
import com.example.workersapp.Adapters.ShowCategoryAdapter;
import com.example.workersapp.Adapters.SubmittedJobAdapter;
import com.example.workersapp.Adapters.WorkInProgressAdapter;
import com.example.workersapp.R;
import com.example.workersapp.Utilities.Offer;
import com.example.workersapp.Utilities.Post;
import com.example.workersapp.databinding.FragmentBlank2Binding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

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
            for (DocumentSnapshot documentSnapshot1 : queryDocumentSnapshots) {
                documentList.add(documentSnapshot1); }
            for (DocumentSnapshot documentSnapshot : documentList) {
                firebaseFirestore.collection("posts").document(documentSnapshot.getId())
                        .collection("userPost").whereEqualTo( "jobState","inWork" )
                        .get().addOnSuccessListener(queryDocumentSnapshots1 -> {
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

                                    Post post = new Post( title,description,images,categoriesList,expectedWorkDuration,projectedBudget,jobLocation,jobState );
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