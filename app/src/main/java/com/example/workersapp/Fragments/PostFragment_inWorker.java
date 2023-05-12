package com.example.workersapp.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.workersapp.Activities.PostActivity2;
import com.example.workersapp.Activities.PostActivity_forWorker;
import com.example.workersapp.Adapters.Post_forWorkerAdapter;
import com.example.workersapp.Adapters.ShowCategoryAdapter;
import com.example.workersapp.R;
import com.example.workersapp.Utilities.Post;
import com.example.workersapp.databinding.FragmentPostInWorkerBinding;
import com.example.workersapp.databinding.FragmentPostsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PostFragment_inWorker extends Fragment {
    FirebaseFirestore firebaseFirestore;
    FirebaseStorage firebaseStorage;
    ShowCategoryAdapter adapter;
    List<String> categoryList;
    List<Post> postList;
    String jobState,title,description,expectedWorkDuration,projectedBudget,jobLocation;

    public PostFragment_inWorker( ) {
    }

    public static PostFragment_inWorker newInstance( ) {
        PostFragment_inWorker fragment = new PostFragment_inWorker( );
        Bundle args = new Bundle( );
        fragment.setArguments( args );
        return fragment;
    }

    @Override
    public void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        if ( getArguments( ) != null ) {

        }
    }

    @Override
    public View onCreateView( LayoutInflater inflater , ViewGroup container ,
                              Bundle savedInstanceState ) {

        FragmentPostInWorkerBinding binding= FragmentPostInWorkerBinding.inflate( inflater,container,false );

        firebaseFirestore=FirebaseFirestore.getInstance();
        categoryList=new ArrayList <>(  );
        postList = new ArrayList <>(  );
        List decoumtId = new ArrayList(  );
        firebaseFirestore.collection( "users" ).get().addOnSuccessListener( queryDocumentSnapshots ->
        {
            for ( DocumentSnapshot documentSnapshot1: queryDocumentSnapshots ){
                decoumtId.add( documentSnapshot1 );
                firebaseFirestore.collection( "posts" ).document( documentSnapshot1.getId() ).
                        collection( "userPost" ).get()
                        .addOnCompleteListener( task -> {
                            for (DocumentSnapshot document : task.getResult()) {
                                if (document.getString("jobState").equals("open")) {
                                Log.e( "DecumentsCount", String.valueOf( task.getResult().size() ) );
                                firebaseFirestore.document("posts/" + documentSnapshot1.getId()+ "/userPost/" + document.getId()).get()
                                        .addOnSuccessListener( documentSnapshot -> {
                                            binding.ProgressBar.setVisibility( View.GONE );
                                            binding.RV.setVisibility( View.VISIBLE );
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
                                                post.setOwnerId( documentSnapshot1.getId() );

                                                postList.add( post );

                                                binding.RV.setAdapter( new Post_forWorkerAdapter( postList , getContext( ), pos -> {
                                                    Log.e( "ItemClik",postList.get( pos ).getPostId());
                                                    Intent intent = new Intent(getActivity(), PostActivity_forWorker.class);
                                                    intent.putExtra("PostId", postList.get( pos ).getPostId());
                                                    intent.putExtra("OwnerId", postList.get( pos ).getOwnerId()); // pass data to new activity

                                                    // pass data to new activity
                                                    startActivity(intent);
                                                } ));

                                            } else {
                                            }
                                        } )
                                        .addOnFailureListener( e -> {
                                            Log.e( "Field",e.getMessage());
                                        } );
                                binding.RV.setLayoutManager( new LinearLayoutManager(getContext(),
                                        LinearLayoutManager.VERTICAL, false));
                                Toast.makeText( getContext() , ""+postList.size() , Toast.LENGTH_SHORT ).show( );


                            }}
                        } ).addOnFailureListener( runnable -> {} );



            }
        });



        return binding.getRoot();
    }
}