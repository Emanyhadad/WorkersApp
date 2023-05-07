package com.example.workersapp.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.workersapp.Activities.OffersActivity;
import com.example.workersapp.Activities.PostActivity2;
import com.example.workersapp.Adapters.PostAdapter;
import com.example.workersapp.Adapters.ShowCategoryAdapter;
import com.example.workersapp.Adapters.ShowImagesAdapter;
import com.example.workersapp.R;
import com.example.workersapp.Utilities.Post;
import com.example.workersapp.databinding.FragmentPostsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PostsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PostsFragment extends Fragment {

    FirebaseFirestore firebaseFirestore;
    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    String firebaseUser1;
    FirebaseStorage firebaseStorage;
    ShowCategoryAdapter adapter;
    List<String> categoryList;
    List<Post> postList;
    String jobState,title,description,expectedWorkDuration,projectedBudget,jobLocation;


    public PostsFragment( ) {
    }

    public static PostsFragment newInstance( String param1 , String param2 ) {
        PostsFragment fragment = new PostsFragment( );
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
        FragmentPostsBinding binding= FragmentPostsBinding.inflate( inflater,container,false );
        firebaseFirestore=FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        firebaseUser1="+970595964511";
        categoryList=new ArrayList <>(  );
        postList = new ArrayList <>(  );

        firebaseFirestore.collection( "posts" ).document( firebaseUser1 ).collection( "userPost" ).get()
                .addOnCompleteListener( task -> {
                    for ( DocumentSnapshot document : task.getResult()) {
                        firebaseFirestore.document("posts/" + firebaseUser1+ "/userPost/" + document.getId()).get()
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
                                        postList.add( post );
                                        Toast.makeText( getContext() , ""+postList.size() , Toast.LENGTH_SHORT ).show( );
                                        binding.RV.setAdapter( new PostAdapter( postList,getContext() ) );
                                        binding.RV.setLayoutManager( new LinearLayoutManager(getContext(),
                                                LinearLayoutManager.HORIZONTAL, false));


                                    } else {
                                        // Handle the case when the document doesn't exist
                                    }
                                } )
                                .addOnFailureListener( e -> {
                                    // Handle the error
                                } );

                    }
                } ).addOnFailureListener( runnable -> {} );



        return binding.getRoot();
    }
}