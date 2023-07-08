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
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class WorkerReviewsFragment extends Fragment {
    FragmentWorkerReviewsBinding binding;
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
        binding = FragmentWorkerReviewsBinding.inflate(inflater,container,false);

        return binding.getRoot();
    }
    void getData(){
        firebaseFirestore=FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();

        categoryList=new ArrayList <>(  );
        reviewsList = new ArrayList <>(  );


        firebaseFirestore.collection("users").get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<WorkerReviews> reviewsList = new ArrayList<>();

            for (DocumentSnapshot documentSnapshot1 : queryDocumentSnapshots) {
                firebaseFirestore.collection("posts").document(documentSnapshot1.getId())
                        .collection("userPost")
                        .whereEqualTo("jobState", "done")
                        .whereEqualTo("workerId", firebaseUser.getPhoneNumber())
                        .orderBy("jobFinishDate", Query.Direction.DESCENDING)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                    for (DocumentSnapshot document : task.getResult()) {
                                        WorkerReviews workerReviews = new WorkerReviews();
                                        workerReviews.setJobId(document.getString("postId"));
                                        workerReviews.setRating_worker(String.valueOf(document.get("Rating-worker")));
                                        workerReviews.setComment_worker(document.getString("Comment-worker"));
                                        workerReviews.setJobTitle(document.getString("title"));
                                        workerReviews.setJobFinishDate(document.getString("jobFinishDate"));
                                        Log.e("Clint", documentSnapshot1.getId());
                                        workerReviews.setClintId(documentSnapshot1.getId());
                                        reviewsList.add(workerReviews);
                                        Log.e("Clint1", workerReviews.toString());
                                    }

                                    binding.RV.setAdapter(new ReviewsAdapter(reviewsList, getActivity(), pos -> {
                                        Intent intent = new Intent(getActivity(), PostActivity_forWorker.class);
                                        intent.putExtra("PostId", reviewsList.get( pos ).getJobId());
                                        intent.putExtra("OwnerId", reviewsList.get( pos ).getClintId()); // pass data to new activity
                                        startActivity(intent);
                                    }));

                                    Log.d("OwnerId", documentSnapshot1.getId());
                                    binding.progressBar3.setVisibility(View.GONE);
                                    binding.RV.setVisibility(View.VISIBLE);
                                    binding.LLEmptyWorker.setVisibility(View.GONE);

                            } else {
                                binding.LLEmptyWorker.setVisibility( View.VISIBLE );
                                binding.progressBar3.setVisibility( View.GONE );
                            }
                        });
            }
        });

        binding.RV.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));


    }

    @Override
    public void onResume( ) {
        super.onResume( );
        getData();
    }
}
