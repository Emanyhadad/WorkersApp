package com.example.workersapp.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.workersapp.Activities.PostActivity_forWorker;
import com.example.workersapp.Adapters.SubmittedJobAdapter;
import com.example.workersapp.R;
import com.example.workersapp.Utilities.Offer;
import com.example.workersapp.databinding.FragmentBlank3Binding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class WorkerSubmittedJobFragment extends Fragment {
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    List <String> categoryList;
    List< Offer > offerList;
    FragmentBlank3Binding binding;
    boolean getData1;

    public WorkerSubmittedJobFragment() { }

    public static WorkerSubmittedJobFragment newInstance() {
        WorkerSubmittedJobFragment fragment = new WorkerSubmittedJobFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView( @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         binding = FragmentBlank3Binding.inflate(inflater, container, false);
        return binding.getRoot();
    }
void getData(){
    firebaseFirestore = FirebaseFirestore.getInstance();
    categoryList = new ArrayList<>();
    offerList = new ArrayList<>();
    firebaseAuth = FirebaseAuth.getInstance();
    firebaseUser = firebaseAuth.getCurrentUser();
    getData1=false;
    firebaseFirestore.collection("users").get().addOnSuccessListener(queryDocumentSnapshots -> {
        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
            firebaseFirestore.collection("posts")
                    .document(documentSnapshot.getId())
                    .collection("userPost")
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots1 -> {
                        for (DocumentSnapshot postDocumentSnapshot : queryDocumentSnapshots1) {
                            firebaseFirestore.collection("offers")
                                    .document(postDocumentSnapshot.getId())
                                    .collection("workerOffers")
                                    .document(Objects.requireNonNull(firebaseUser.getPhoneNumber()))
                                    .get()
                                    .addOnSuccessListener(documentSnapshot2 -> {
                                        String clintID = documentSnapshot2.getString("clintID");
                                        String offerBudget = documentSnapshot2.getString("offerBudget");
                                        String offerDescription = documentSnapshot2.getString("offerDescription");
                                        String offerDuration = documentSnapshot2.getString("offerDuration");
                                        String postID = documentSnapshot2.getString("postID");
                                        String workerID = documentSnapshot2.getString("workerID");
                                        Offer offer = new Offer(offerBudget, offerDuration, offerDescription, workerID, clintID, postID);
                                        if (offerBudget != null) {
                                            offerList.add(offer);
                                        }
                                        Log.e("offers", offer.toString());
                                        Log.e("OffersList", offerList.size() + "");

                                        binding.RV.setAdapter(new SubmittedJobAdapter(offerList, getContext(), pos -> {
                                            Intent intent = new Intent(getActivity(), PostActivity_forWorker.class);
                                            intent.putExtra("PostId", offerList.get(pos).getPostID());
                                            intent.putExtra("OwnerId", offerList.get(pos).getClintID());
                                            startActivity(intent);
                                        }));
                                        if (offerList.size() == 0) {
                                            binding.progressBar4.setVisibility(View.GONE);
                                            binding.RV.setVisibility(View.GONE);
                                            binding.LLEmptyWorker.setVisibility(View.VISIBLE);
                                        } else {
                                            binding.RV.setVisibility(View.VISIBLE);
                                            binding.LLEmptyWorker.setVisibility(View.GONE);
                                            binding.progressBar4.setVisibility(View.GONE);
                                        }
                                        Log.e( "offerList.size()",offerList.size()+"" );
                                    }
                                    )
                                    .addOnFailureListener(runnable -> {
                                        binding.LLEmptyWorker.setVisibility(View.VISIBLE);
                                        binding.progressBar4.setVisibility(View.GONE);

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

                            Log.e( "offerList.size()1",offerList.size()+"" );

                        }
                        getData1 = true;


                        Log.e( "getData2",getData1+"" );
                        Log.e( "getData",getData1+"" );


                        binding.RV.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                    });

        }


    });


}
    @Override
    public void onResume( ) {
        super.onResume( );
        getData();
    }
}