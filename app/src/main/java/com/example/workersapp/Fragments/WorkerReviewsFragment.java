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
    private static final String ARG_WORKER = "workerID";
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    List<String> categoryList;
    List<WorkerReviews> reviewsList;
    private String workerID;


    public WorkerReviewsFragment() {
    }

    public static WorkerReviewsFragment newInstance(String workerID) {
        WorkerReviewsFragment fragment = new WorkerReviewsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_WORKER, workerID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            workerID = getArguments().getString(ARG_WORKER);
        }

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentWorkerReviewsBinding.inflate(inflater, container, false);

        getData();

        return binding.getRoot();
    }

    void getData() {
        firebaseFirestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();

        categoryList = new ArrayList<>();
        reviewsList = new ArrayList<>();
        List documentId = new ArrayList();

        if (workerID != null) {
            firebaseFirestore.collection("users").get().addOnSuccessListener(queryDocumentSnapshots -> {
                List<String> postIdList = new ArrayList<>();
                List<WorkerReviews> reviewsList = new ArrayList<>();

                for (DocumentSnapshot documentSnapshot1 : queryDocumentSnapshots) {
                    documentId.add(documentSnapshot1);

                    firebaseFirestore.collection("posts").document(documentSnapshot1.getId())
                            .collection("userPost")
                            .whereEqualTo("jobState", "done")
                            .whereEqualTo("workerId", workerID)
                            .orderBy("jobFinishDate", Query.Direction.DESCENDING)
                            .get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    for (DocumentSnapshot document : task.getResult()) {
                                        String postId = document.getString("postId");
                                        postIdList.add(postId); // تخزين postId في القائمة

                                        if (postId != null) {
                                            WorkerReviews workerReviews = new WorkerReviews();
                                            workerReviews.setJobId(postId);
                                            workerReviews.setRating_worker(String.valueOf(document.get("Rating-worker")));
                                            workerReviews.setComment_worker(document.getString("Comment-worker"));
                                            workerReviews.setJobTitle(document.getString("title"));
                                            workerReviews.setJobFinishDate(document.getString("jobFinishDate"));
                                            Log.e("Clint", documentSnapshot1.getId());
                                            workerReviews.setClintId(documentSnapshot1.getId());
                                            reviewsList.add(workerReviews);
                                            Log.e("Clint1", workerReviews.toString());

                                            binding.RV.setAdapter(new ReviewsAdapter(reviewsList, getActivity(), pos -> {
                                                Intent intent = new Intent(getActivity(), PostActivity_forWorker.class);
                                                String postIdPos = postIdList.get(pos);

                                                intent.putExtra("PostId", postIdPos);
                                                intent.putExtra("posWorker",workerID);
                                                intent.putExtra("OwnerId", documentSnapshot1.getId()); // pass data to new activity
                                                startActivity(intent);
                                            }));

                                            Log.d("PostId", postId + "");
                                            Log.d("OwnerId", documentSnapshot1.getId());
                                        }
                                    }
                                    binding.progressBar3.setVisibility(View.GONE);
                                    binding.RV.setVisibility(View.VISIBLE);
                                    binding.LLEmptyWorker.setVisibility(View.GONE);

                                } else {
                                    binding.LLEmptyWorker.setVisibility(View.VISIBLE);
                                    binding.progressBar3.setVisibility(View.GONE);
                                }

                            })
                            .addOnFailureListener(runnable -> {
                            });
                }
            });
        } else {

            firebaseFirestore.collection("users").get().addOnSuccessListener(queryDocumentSnapshots -> {
                List<String> postIdList = new ArrayList<>();
                List<WorkerReviews> reviewsList = new ArrayList<>();

                for (DocumentSnapshot documentSnapshot1 : queryDocumentSnapshots) {
                    documentId.add(documentSnapshot1);

                    firebaseFirestore.collection("posts").document(documentSnapshot1.getId())
                            .collection("userPost")
                            .whereEqualTo("jobState", "done")
                            .whereEqualTo("workerId", firebaseUser.getPhoneNumber())
                            .orderBy("jobFinishDate", Query.Direction.DESCENDING)
                            .get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    for (DocumentSnapshot document : task.getResult()) {
                                        String postId = document.getString("postId");
                                        postIdList.add(postId); // تخزين postId في القائمة

                                        if (postId != null) {
                                            WorkerReviews workerReviews = new WorkerReviews();
                                            workerReviews.setJobId(postId);
                                            workerReviews.setRating_worker(String.valueOf(document.get("Rating-worker")));
                                            workerReviews.setComment_worker(document.getString("Comment-worker"));
                                            workerReviews.setJobTitle(document.getString("title"));
                                            workerReviews.setJobFinishDate(document.getString("jobFinishDate"));
                                            Log.e("Clint", documentSnapshot1.getId());
                                            workerReviews.setClintId(documentSnapshot1.getId());
                                            reviewsList.add(workerReviews);
                                            Log.e("Clint1", workerReviews.toString());

                                            binding.RV.setAdapter(new ReviewsAdapter(reviewsList, getActivity(), pos -> {
                                                Intent intent = new Intent(getActivity(), PostActivity_forWorker.class);
                                                String postIdPos = postIdList.get(pos);

                                                intent.putExtra("PostId", postIdPos);
                                                intent.putExtra("OwnerId", documentSnapshot1.getId()); // pass data to new activity
                                                startActivity(intent);
                                            }));

                                            Log.d("PostId", postId + "");
                                            Log.d("OwnerId", documentSnapshot1.getId());
                                        }
                                    }
                                    binding.progressBar3.setVisibility(View.GONE);
                                    binding.RV.setVisibility(View.VISIBLE);
                                    binding.LLEmptyWorker.setVisibility(View.GONE);

                                } else {
                                    binding.LLEmptyWorker.setVisibility(View.VISIBLE);
                                    binding.progressBar3.setVisibility(View.GONE);
                                }

                            })
                            .addOnFailureListener(runnable -> {
                            });
                }
            });
        }

        binding.RV.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));

    }
}








