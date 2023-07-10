package com.example.workersapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.workersapp.Adapters.ImageModelAdapter;
import com.example.workersapp.Adapters.ImageModelFragAdapter;
import com.example.workersapp.Fragments.BusinessModelsFragment;
import com.example.workersapp.Fragments.WorkerReviewsFragment;
import com.example.workersapp.R;
import com.example.workersapp.Utilities.Model;
import com.example.workersapp.databinding.ActivityWorkerProfileForOwnerBinding;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WorkerProfileForOwner extends AppCompatActivity {
    ActivityWorkerProfileForOwnerBinding binding;
    FirebaseFirestore db;
    FirebaseStorage storage;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    private boolean jobCount;
    private boolean userData;
    List<String> DoneList;
    List<String> inWorkList;
    String workerID;
    ImageModelFragAdapter modelFragAdapter;

    ImageModelAdapter modelAdapter;
    List<String> imagesList;
    List<Model> models;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWorkerProfileForOwnerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        DoneList = new ArrayList<>();
        inWorkList = new ArrayList<>();
        ArrayList<Fragment> fragments = new ArrayList<>();
        ArrayList<String> tabs = new ArrayList<>();
        imagesList = new ArrayList<>();
        models = new ArrayList<>();

        Intent intent = getIntent();
        workerID = intent.getStringExtra("posWorker");

        tabs.add(getString(R.string.TvReviews));
        tabs.add(getString(R.string.businessToolBar));



        fragments.add(WorkerReviewsFragment.newInstance(workerID));
        fragments.add(BusinessModelsFragment.newInstance(workerID));

        modelFragAdapter = new ImageModelFragAdapter(this, fragments);
        binding.FragPager.setAdapter(modelFragAdapter);

        new TabLayoutMediator(binding.FragTab, binding.FragPager, (tab, position) -> {
            if (position < tabs.size()) {
                tab.setText(tabs.get(position));
            }
        }).attach();

        if (jobCount && userData) {
            binding.ProgressBar.setVisibility(View.GONE);
            binding.ScrollView.setVisibility(View.VISIBLE);
        }

        getData();

    }

    private void getData() {
        List decoumtId = new ArrayList();

        if (workerID != null) {

            binding.ProgressBar.setVisibility(View.VISIBLE);
            binding.ScrollView.setVisibility(View.GONE);

            db.collection("users").document(workerID)
                    .get().addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {

                            binding.ProgressBar.setVisibility(View.GONE);
                            binding.ScrollView.setVisibility(View.VISIBLE);

                            String fullName = documentSnapshot.getString("fullName");
                            String nickName = documentSnapshot.getString("nickName");
                            String work = documentSnapshot.getString("work");
                            String cv = documentSnapshot.getString("cv");
                            String city = documentSnapshot.getString("city");
                            String image = documentSnapshot.getString("image");
                            binding.pWorkerUserName.setText(fullName);
                            binding.pWorkerNickName.setText("( " + nickName + " )");
                            binding.pWorkerJobName.setText(work);
                            binding.pWorkerCv.setText(cv);
                            binding.pWorkerLocation.setText(city);
                            binding.pWorkerPhone.setText(workerID);

                            binding.inculd.tvPageTitle.setText(fullName);

                            Glide.with(getApplicationContext()).load(image).circleCrop().error(R.drawable.worker).into(binding.pWorkerImg);

                            long timestamp = firebaseUser.getMetadata().getCreationTimestamp();
                            // حولنا long -> date
                            Date date = new Date(timestamp);
                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                            String formattedDate = dateFormat.format(date);
                            binding.pWorkerJoinDate.setText(formattedDate);
                            userData = true;
                        }
                    }).addOnFailureListener(e -> {
                        binding.LLNoWifi.setVisibility(View.VISIBLE);
                        binding.ProgressBar.setVisibility(View.GONE);
                    });

            db.collection("users").get().addOnSuccessListener(queryDocumentSnapshots -> {
                for (DocumentSnapshot documentSnapshot1 : queryDocumentSnapshots) {
                    decoumtId.add(documentSnapshot1);
                    db.collection("posts").document(documentSnapshot1.getId())
                            .collection("userPost").get()
                            .addOnCompleteListener(task -> {
                                jobCount = true;
                                for (DocumentSnapshot document : task.getResult()) {
                                    String jobState = document.getString("jobState");
                                    String workerId = document.getString("workerId");
                                    if (jobState != null && jobState.equals("done") && workerId != null && workerId.equals(workerID)){
                                        Log.e("workerId", workerId.equals(firebaseUser.getPhoneNumber()) + "");
                                        Log.e("DecumentsCount", String.valueOf(task.getResult().size()));
                                        String postId = document.getString("postId");
                                        if (postId != null) {
                                            DoneList.add(postId);
                                        }
                                    }
                                    if (jobState != null && jobState.equals("inWork") && workerId != null && workerId.equals(workerID)) {
                                        Log.e("workerId", workerId.equals(firebaseUser.getPhoneNumber()) + "");
                                        Log.e("DecumentsCount", String.valueOf(task.getResult().size()));
                                        String postId = document.getString("postId");
                                        if (postId != null) {
                                            inWorkList.add(postId);
                                        }
                                    }
                                    int jobCount = inWorkList.size() + DoneList.size();
                                    binding.pWorkerJobNum.setText(jobCount + "");
                                    binding.pWorkerEndNum.setText(DoneList.size() + "");
                                    binding.pWorkerCurrentNum.setText(inWorkList.size() + "");

                                }
                            })
                            .addOnFailureListener(runnable -> {
                            });
                }
            });

            List<Long> RatingWorkerList = new ArrayList<>();
            db.collection("users").get().addOnSuccessListener(queryDocumentSnapshots -> {
                for (DocumentSnapshot documentSnapshot1 : queryDocumentSnapshots) {
                    db.collection("posts").document(documentSnapshot1.getId())
                            .collection("userPost").whereEqualTo("jobState", "done")
                            .whereEqualTo("workerId", workerID).get()
                            .addOnCompleteListener(task -> {
                                for (DocumentSnapshot document : task.getResult()) {
                                    RatingWorkerList.add(document.getLong("Rating-worker"));

                                    Log.d("tag", document.getId());

                                    long sum = 0;
                                    for (Long value : RatingWorkerList) {
                                        sum += value;
                                    }
                                    if (RatingWorkerList.size() != 0) {
                                        int x = (int) (sum / RatingWorkerList.size());
                                        binding.pWorkerRate.setText(x + "");
                                        Log.d("tag", String.valueOf(x));
                                    } else {
                                        binding.pWorkerRate.setText("0");
                                    }
                                }
                            })
                            .addOnFailureListener(runnable -> {
                            });
                }
            });
        }
    }

}