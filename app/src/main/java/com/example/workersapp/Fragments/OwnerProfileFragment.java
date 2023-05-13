package com.example.workersapp.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.workersapp.Activities.PostActivity2;
import com.example.workersapp.Adapters.FinishedJobsAdapter;
import com.example.workersapp.Adapters.ShowCategoryAdapter;
import com.example.workersapp.Adapters.WorkInProgressAdapter;
import com.example.workersapp.R;
import com.example.workersapp.Utilities.Post;
import com.example.workersapp.databinding.FragmentWorkOwnerProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

public class OwnerProfileFragment extends Fragment {
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    FirebaseStorage firebaseStorage;
    ShowCategoryAdapter adapter;
    List <String> categoryList;
    List< Post > postList;
    String jobState,title,description,expectedWorkDuration,projectedBudget,jobLocation;


    public OwnerProfileFragment() {
        // Required empty public constructor
    }


    public static OwnerProfileFragment newInstance( ) {
        OwnerProfileFragment fragment = new OwnerProfileFragment();
        Bundle args = new Bundle();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentWorkOwnerProfileBinding binding = FragmentWorkOwnerProfileBinding.inflate(inflater, container, false);
        firebaseFirestore=FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();

        categoryList=new ArrayList <>(  );
        postList = new ArrayList <>(  );

        firebaseFirestore.collection("posts")
                .document(firebaseUser.getPhoneNumber())
                .collection("userPost").whereEqualTo( "jobState","done" )
                .get()
                .addOnCompleteListener(task -> {
                    Log.e( "Posts",task.getResult().toString() );

                    if ( task.getResult().isEmpty() ){
                        binding.PB.setVisibility( View.GONE );
                        binding.rcFinishedJobs.setVisibility( View.GONE );
                        binding.LLEmpty.setVisibility( View.VISIBLE );
                    }
                    else {
                        for ( DocumentSnapshot document : task.getResult()) {
                            firebaseFirestore.document("posts/" + firebaseUser.getPhoneNumber()+ "/userPost/" + document.getId()).get()
                                    .addOnSuccessListener( documentSnapshot -> {
                                        binding.PB.setVisibility( View.GONE );
                                        binding.LLEmpty.setVisibility( View.GONE );
                                        binding.rcFinishedJobs.setVisibility( View.VISIBLE );
                                        if (documentSnapshot.exists()) {

                                            jobState = documentSnapshot.getString("jobState");
                                            title = documentSnapshot.getString("title");
                                            description= documentSnapshot.getString( "description" );
                                            List<String> images = (List<String>) documentSnapshot.get("images");
                                            List<String> categoriesList = (List<String>) documentSnapshot.get("categoriesList");

                                            expectedWorkDuration= documentSnapshot.getString( "expectedWorkDuration" );
                                            projectedBudget= documentSnapshot.getString( "projectedBudget" );
                                            jobLocation= documentSnapshot.getString( "jobLocation" );

                                            Post post = new Post( title,description,images,categoriesList,expectedWorkDuration,projectedBudget,jobLocation,jobState );
                                            post.setPostId( document.getId() );
                                            post.setOwnerId( firebaseUser.getPhoneNumber() );
                                            postList.add( post );

                                            binding.rcFinishedJobs.setAdapter( new WorkInProgressAdapter( postList , getContext( ), pos -> {
                                                Intent intent = new Intent(getActivity(), PostActivity2.class);
                                                intent.putExtra("PostId", postList.get( pos ).getPostId()); // pass data to new activity
                                                startActivity(intent);
                                            } ));

                                        } } )
                                    .addOnFailureListener( e -> {
                                        Log.e( "Field",e.getMessage());
                                    } );
                            binding.rcFinishedJobs.setLayoutManager( new LinearLayoutManager(getContext(),
                                    LinearLayoutManager.VERTICAL, false));
                            Toast.makeText( getContext() , ""+postList.size() , Toast.LENGTH_SHORT ).show( );


                        }}
                } ).addOnFailureListener( runnable -> {} );




        return binding.getRoot();
    }
}