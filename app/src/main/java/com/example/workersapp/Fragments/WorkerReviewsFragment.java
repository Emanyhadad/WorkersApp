package com.example.workersapp.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.workersapp.Activities.PostActivity_forWorker;
import com.example.workersapp.Adapters.ReviewsAdapter;
import com.example.workersapp.Utilities.WorkerReviews;
import com.example.workersapp.databinding.FragmentWorkerReviewsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class WorkerReviewsFragment extends Fragment {
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    List <String> categoryList;
    List< WorkerReviews > reviewsList;

    public WorkerReviewsFragment() {
    }
    public static WorkerReviewsFragment newInstance() {
        WorkerReviewsFragment fragment = new WorkerReviewsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    @Override
    public View onCreateView( @NonNull LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
        FragmentWorkerReviewsBinding binding = FragmentWorkerReviewsBinding.inflate(inflater,container,false);
        firebaseFirestore=FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();

        categoryList=new ArrayList <>(  );
        reviewsList = new ArrayList <>(  );
        List documentId = new ArrayList(  );
        firebaseFirestore.collection("users").get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (DocumentSnapshot documentSnapshot1 : queryDocumentSnapshots) {
                documentId.add(documentSnapshot1);
                firebaseFirestore.collection("posts").document(documentSnapshot1.getId())
                        .collection("userPost").get()
                        .addOnCompleteListener(task -> {
                            for (DocumentSnapshot document : task.getResult()) {
                                String jobState = document.getString("jobState");
                                String workerId = document.getString("workerId");
                                if (jobState != null && jobState.equals("done") && workerId != null && workerId.equals(firebaseUser.getPhoneNumber())) {
                                    Log.e("workerId", workerId.equals(firebaseUser.getPhoneNumber()) + "");
                                    Log.e("DocumentCount", String.valueOf(task.getResult().size()));
                                    String postId = document.getString("postId");
                                    if (postId != null) {
                                        WorkerReviews workerReviews = new WorkerReviews();
                                        workerReviews.setJobId(postId);
                                        workerReviews.setRating_worker(String.valueOf(document.get("Rating-worker")));
                                        workerReviews.setComment_worker(document.getString("Comment-worker"));
                                        workerReviews.setJobTitle(document.getString("title"));
                                        workerReviews.setJobFinishDate(document.getString("jobFinishDate"));
                                        Log.e( "Clint",documentSnapshot1.getId() );
                                        workerReviews.setClintId( documentSnapshot1.getId() );
                                        reviewsList.add( workerReviews );
                                        Log.e( "Clint1",workerReviews.toString() );

                                        binding.RV.setAdapter(new ReviewsAdapter(reviewsList, getActivity(), pos -> {
                                            Intent intent = new Intent(getActivity(), PostActivity_forWorker.class);
                                            intent.putExtra("PostId", postId);
                                            intent.putExtra("OwnerId", documentSnapshot1.getId()); // pass data to new activity
                                            startActivity(intent);
                                        }));
                                        binding.progressBar3.setVisibility(View.GONE);
                                        binding.RV.setVisibility(View.VISIBLE);
                                    }
                                }
                            }
                        })
                        .addOnFailureListener(runnable -> {
                        });
            }
        });








        binding.RV.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));

        return binding.getRoot();
    }
}