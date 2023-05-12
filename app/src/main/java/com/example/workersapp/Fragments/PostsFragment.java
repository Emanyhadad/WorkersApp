package com.example.workersapp.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.workersapp.Activities.PostActivity2;
import com.example.workersapp.Adapters.PostAdapter;
import com.example.workersapp.Adapters.Post_forWorkerAdapter;
import com.example.workersapp.Adapters.ShowCategoryAdapter;
import com.example.workersapp.R;
import com.example.workersapp.Utilities.Post;
import com.example.workersapp.databinding.FragmentPostsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PostsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PostsFragment extends Fragment {

    FirebaseFirestore firebaseFirestore;
    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    FirebaseStorage firebaseStorage;
    ShowCategoryAdapter adapter;
    List<String> categoryList;
    List<Post> postList;
    String jobState,title,description,expectedWorkDuration,projectedBudget,jobLocation;
//    FilterBottomSheetFragment filterButtonSheet = new FilterBottomSheetFragment();


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

        categoryList=new ArrayList <>(  );
        postList = new ArrayList <>(  );

        firebaseFirestore.collection("posts")
                .document(firebaseUser.getPhoneNumber())
                .collection("userPost")
                .whereIn("jobState", Arrays.asList("open", "close"))
                .get()
                .addOnCompleteListener(task -> {
                    if ( task.getResult().isEmpty() ){
                        binding.ProgressBar.setVisibility( View.GONE );
                        binding.LLEmpty.setVisibility( View.VISIBLE );
                        binding.btnAddpost.setOnClickListener(v -> {
                            // Replace the current fragment with the new fragment here
                            FragmentManager fragmentManager = getParentFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            NewJobFragment jobFragment = new NewJobFragment();
                            fragmentTransaction.replace( R.id.container, jobFragment);
                            fragmentTransaction.addToBackStack(null); // Add to back stack to allow user to navigate back to this fragment
                            fragmentTransaction.commit();
                        });
                    }
                    else {
                    for ( DocumentSnapshot document : task.getResult()) {
                        firebaseFirestore.document("posts/" + firebaseUser.getPhoneNumber()+ "/userPost/" + document.getId()).get()
                                .addOnSuccessListener( documentSnapshot -> {
                                    binding.ProgressBar.setVisibility( View.GONE );
                                    binding.LLEmpty.setVisibility( View.GONE );
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
                                        post.setOwnerId( firebaseUser.getPhoneNumber() );
                                        postList.add( post );

                                        binding.RV.setAdapter( new PostAdapter( postList , getContext( ), pos -> {
                                            Log.e( "ItemClik",postList.get( pos ).getPostId());
                                            Intent intent = new Intent(getActivity(), PostActivity2.class);
                                            intent.putExtra("PostId", postList.get( pos ).getPostId()); // pass data to new activity
                                            startActivity(intent);
                                        } ));

                                    } } )
                                .addOnFailureListener( e -> {
                                    Log.e( "Field",e.getMessage());
                                } );
                        binding.RV.setLayoutManager( new LinearLayoutManager(getContext(),
                                LinearLayoutManager.VERTICAL, false));
                        Toast.makeText( getContext() , ""+postList.size() , Toast.LENGTH_SHORT ).show( );


                    }}
                } ).addOnFailureListener( runnable -> {} );



        binding.button2.setOnClickListener( view -> {
            FilterBottomSheetDialog bottomSheetDialog = new FilterBottomSheetDialog();
            bottomSheetDialog.show(getChildFragmentManager(), "MyBottomSheetDialogFragment");
        } );

        return binding.getRoot();
    }
}