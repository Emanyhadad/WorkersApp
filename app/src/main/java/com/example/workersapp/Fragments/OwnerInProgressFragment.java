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
import com.example.workersapp.Adapters.ShowCategoryAdapter;
import com.example.workersapp.Adapters.WorkInProgressAdapter;
import com.example.workersapp.R;
import com.example.workersapp.Utilities.Post;
import com.example.workersapp.databinding.FragmentWorkInProgressBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;


public class OwnerInProgressFragment extends Fragment {
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    List <String> categoryList;
    List< Post > postList;
    String jobState,title,description,expectedWorkDuration,projectedBudget,jobLocation;
    public OwnerInProgressFragment() {
        // Required empty public constructor
    }


    public static OwnerInProgressFragment newInstance( ) {
        OwnerInProgressFragment fragment = new OwnerInProgressFragment();
        Bundle args = new Bundle();
        fragment.setArguments( args );
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
        FragmentWorkInProgressBinding binding = FragmentWorkInProgressBinding.inflate(inflater, container, false);
        firebaseFirestore=FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();

        categoryList=new ArrayList <>(  );
        postList = new ArrayList <>(  );

        firebaseFirestore.collection("posts")
                .document(firebaseUser.getPhoneNumber())
                .collection("userPost")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<Post> postList = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult().getDocuments()) {
                            firebaseFirestore.document("posts/" + firebaseUser.getPhoneNumber()+
                                            "/userPost/" + document.getId()).get()
                                    .addOnSuccessListener(documentSnapshot -> {
                                        if (documentSnapshot.exists()) {
                                            String jobState = documentSnapshot.getString("jobState");
                                            if ( jobState.equals( "inWork" ) ){
                                            String title = documentSnapshot.getString("title");
                                            String description = documentSnapshot.getString("description");
                                            List<String> images = (List<String>) documentSnapshot.get("images");
                                            List<String> categoriesList = (List<String>) documentSnapshot.get("categoriesList");
                                            String expectedWorkDuration = documentSnapshot.getString("expectedWorkDuration");
                                            String projectedBudget = documentSnapshot.getString("projectedBudget");
                                            String jobLocation = documentSnapshot.getString("jobLocation");

                                            Post post = new Post(title, description, images, categoriesList, expectedWorkDuration, projectedBudget, jobLocation, jobState);
                                            post.setPostId(document.getId());
                                            post.setOwnerId(firebaseUser.getPhoneNumber());
                                            postList.add(post);
                                        }

                                        WorkInProgressAdapter adapter = new WorkInProgressAdapter(postList, getContext(), pos -> {
                                            Intent intent = new Intent(getActivity(), PostActivity2.class);
                                            intent.putExtra("PostId", postList.get(pos).getPostId());
                                            startActivity(intent);
                                        });
                                        binding.rcWorkInProgress.setAdapter(adapter);
                                        binding.rcWorkInProgress.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                                        binding.Pb.setVisibility(View.GONE);
                                        if (postList.isEmpty()) {
                                            binding.LLEmpty.setVisibility(View.VISIBLE);
                                            binding.btnAddpost.setOnClickListener(v -> {
                                                FragmentManager fragmentManager = getParentFragmentManager();
                                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                                PostsFragment postsFragment = new PostsFragment();
                                                fragmentTransaction.replace(R.id.container, postsFragment);
                                                fragmentTransaction.addToBackStack(null);
                                                fragmentTransaction.commit();
                                            });
                                        } else {
                                            binding.LLEmpty.setVisibility(View.GONE);
                                            binding.rcWorkInProgress.setVisibility(View.VISIBLE);
                                        }}
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("Field", e.getMessage());
                                    });
                        }
                    }
                })
                .addOnFailureListener(runnable -> {});


        return binding.getRoot();
    }
}