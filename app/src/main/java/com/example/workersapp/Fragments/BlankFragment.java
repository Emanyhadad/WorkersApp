package com.example.workersapp.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.workersapp.Adapters.ShowCategoryAdapter;
import com.example.workersapp.Utilities.Post;
import com.example.workersapp.databinding.FragmentBlankBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

public class BlankFragment extends Fragment {
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    FirebaseStorage firebaseStorage;
    ShowCategoryAdapter adapter;
    List <String> categoryList;
    List< Post > postList;
    String jobState,title,description,expectedWorkDuration,projectedBudget,jobLocation;

    public BlankFragment() {
    }
    public static BlankFragment newInstance() {
        BlankFragment fragment = new BlankFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
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
        FragmentBlankBinding binding = FragmentBlankBinding.inflate(inflater,container,false);
        firebaseFirestore=FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        categoryList=new ArrayList <>(  );
        postList = new ArrayList <>(  );

        firebaseFirestore.collection( "offers" ).get().addOnSuccessListener( queryDocumentSnapshots -> {
            for ( DocumentSnapshot documentSnapshot: queryDocumentSnapshots ){
                documentSnapshot.getId();
                Log.e( "DecoumentId",documentSnapshot.getId() );
                Log.e( "PostId",documentSnapshot.get( "postID" ).toString() );

                if (  documentSnapshot.get( "workerID" ).equals( firebaseUser.getPhoneNumber() ) ){
                    Log.e( "MyOffers",documentSnapshot.get( "offerDuration" ).toString() );

                }

                firebaseFirestore.collection( "users" ).document( documentSnapshot.get( "workerID" ).toString() )
                        .get().addOnSuccessListener( runnable -> Log.e( "workerId",  runnable.get( "accountType" ).toString()) );





            }
        } ).addOnFailureListener( runnable -> {} );
        return binding.getRoot();
    }
}