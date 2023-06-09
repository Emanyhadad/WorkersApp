package com.example.workersapp.Fragments;

import static android.content.Context.MODE_PRIVATE;
import static androidx.constraintlayout.widget.ConstraintLayoutStates.TAG;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.workersapp.Activities.EditWorkerProfileActivity;
import com.example.workersapp.Activities.LoginActivity;
import com.example.workersapp.Activities.PostActivity2;
import com.example.workersapp.Adapters.FinishedJobsAdapter;
import com.example.workersapp.Adapters.ShowCategoryAdapter;
import com.example.workersapp.R;
import com.example.workersapp.Utilities.Post;
import com.example.workersapp.databinding.FragmentWorkOwnerProfileBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OwnerProfileFragment extends Fragment {
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    FirebaseStorage firebaseStorage;
    ShowCategoryAdapter adapter;
    public static SharedPreferences sp;
    public static SharedPreferences.Editor editor;

    public static SharedPreferences sp1;
    public static SharedPreferences.Editor editor1;

    List<String> categoryList;
    List<Post> postList;
    String jobState, title, description, expectedWorkDuration, projectedBudget, jobLocation;

    int openCount, inWorkCount, doneCount, jobsCount;
    boolean isWorkCountDone = false;
    boolean isdoneCountDone = false;
    boolean isjobsCountDone = false;

    boolean userData = false;
    boolean jobData = false;
    long addedTime;
    double rate = 0;
    int count = 0;

    public OwnerProfileFragment() {
        // Required empty public constructor
    }


    public static OwnerProfileFragment newInstance() {
        OwnerProfileFragment fragment = new OwnerProfileFragment();
        Bundle args = new Bundle();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    FragmentWorkOwnerProfileBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentWorkOwnerProfileBinding.inflate(inflater, container, false);
        firebaseFirestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        sp = getContext().getSharedPreferences("MyPreferences", MODE_PRIVATE);
        editor = sp.edit();

        sp1 = getContext().getSharedPreferences("MyPreferencesBoarding", MODE_PRIVATE);
        editor1 = sp1.edit();

        categoryList = new ArrayList<>();
        postList = new ArrayList<>();


        //For User Data:
        DocumentReference userRef = firebaseFirestore.collection("users").document(firebaseUser.getPhoneNumber());

        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    binding.ProgressBar.setVisibility(View.GONE);
                    binding.AppBar.setVisibility(View.VISIBLE);
                    String nickName = document.getString("nickName");
                    binding.tvWorkOwnerNickName.setText(nickName);
                    String city = document.getString("city");
                    binding.tvWorkOwnerCity.setText(city);
                    String image = document.getString("image");

                    if (getContext() != null) {
                        Glide.with(OwnerProfileFragment.this)
                                .load(image)
                                .circleCrop()
                                .error(R.drawable.worker)
                                .into(binding.imgProfileOwner);
                    }

                    String fullName = document.getString("fullName");
                    binding.tvWorkOwnerName.setText(fullName);
                    long timestamp = firebaseUser.getMetadata().getCreationTimestamp();
                    // حولنا long -> date
                    Date date = new Date(timestamp);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    String formattedDate = dateFormat.format(date);

                    binding.tvDateOfJoin.setText(formattedDate);
                    // Do something with the retrieved data
                } else {
                    Log.d(TAG, "No such document");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });

        CollectionReference userPostsRef = firebaseFirestore.collection("posts")
                .document(firebaseUser.getPhoneNumber())
                .collection("userPost");

        //For JosCount
        userPostsRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            jobsCount = queryDocumentSnapshots.size();
            isjobsCountDone = true;
            setRate();
            binding.tvNumberOfJobs.setText(jobsCount + "");
        }).addOnFailureListener(e -> Log.d(TAG, "Error getting documents: ", e));

        //For OpenJobs
        Query openJobPostsQuery = userPostsRef.whereEqualTo("jobState", "open");
        openJobPostsQuery.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                openCount = task.getResult().size();
                binding.tvOpenedJobs.setText(openCount + "");

                // calculate employment rate
            } else {
                Log.d(TAG, "Error getting documents: ", task.getException());
            }
        });

        //For inWorkJobs
        Query inWorkJobPostsQuery = userPostsRef.whereEqualTo("jobState", "inWork");
        inWorkJobPostsQuery.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                inWorkCount = task.getResult().size();

                isWorkCountDone = true;
                setRate();
            } else {
                Log.d(TAG, "Error getting documents: ", task.getException());
            }
        });

        //For DoneJobs
        Query doneJobPostsQuery = userPostsRef.whereEqualTo("jobState", "done");
        doneJobPostsQuery.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                doneCount = task.getResult().size();
                isdoneCountDone = true;
                setRate();
            } else {
                Log.d(TAG, "Error getting documents: ", task.getException());
            }
        });

        //For Finished Job:
        firebaseFirestore.collection("posts")
                .document(firebaseUser.getPhoneNumber())
                .collection("userPost").whereEqualTo("jobState", "done")
                .get()
                .addOnCompleteListener(task -> {
                    jobData = true;
                    if (task.getResult().isEmpty()) {
                        binding.PB.setVisibility(View.GONE);
                        binding.SV.setVisibility(View.GONE);
                        binding.LL.setVisibility(View.VISIBLE);
                        binding.LLEmpty.setVisibility(View.VISIBLE);
                    } else {
                        for (DocumentSnapshot document : task.getResult()) {
                            firebaseFirestore.document("posts/" + firebaseUser.getPhoneNumber() + "/userPost/" + document.getId()).get()
                                    .addOnSuccessListener(documentSnapshot -> {
                                        binding.PB.setVisibility(View.GONE);
                                        binding.LL.setVisibility(View.VISIBLE);
                                        binding.LLEmpty.setVisibility(View.GONE);
                                        binding.SV.setVisibility(View.VISIBLE);
                                        if (documentSnapshot.exists()) {

                                            jobState = documentSnapshot.getString("jobState");
                                            title = documentSnapshot.getString("title");
                                            description = documentSnapshot.getString("description");
                                            List<String> images = (List<String>) documentSnapshot.get("images");
                                            List<String> categoriesList = (List<String>) documentSnapshot.get("categoriesList");

                                            expectedWorkDuration = documentSnapshot.getString("expectedWorkDuration");
                                            projectedBudget = documentSnapshot.getString("projectedBudget");
                                            jobLocation = documentSnapshot.getString("jobLocation");
                                            addedTime = documentSnapshot.getLong("addedTime");

                                            Post post = new Post(title, description, images, categoriesList, expectedWorkDuration, projectedBudget, jobLocation, jobState, addedTime);
                                            post.setPostId(document.getId());
                                            post.setOwnerId(firebaseUser.getPhoneNumber());
                                            post.setWorkerId(documentSnapshot.getString("workerId"));
                                            postList.add(post);

                                            binding.rcFinishedJobs.setAdapter(new FinishedJobsAdapter(postList, getContext(), pos -> {
                                                Intent intent = new Intent(getActivity(), PostActivity2.class);
                                                intent.putExtra("PostId", postList.get(pos).getPostId()); // pass data to new activity
                                                startActivity(intent);
                                            }));

                                        }
                                    })
                                    .addOnFailureListener(e -> Log.e("Field", e.getMessage()));
                            binding.rcFinishedJobs.setLayoutManager(new LinearLayoutManager(getContext(),
                                    LinearLayoutManager.VERTICAL, false));


                        }
                    }
                }).addOnFailureListener(runnable -> {
                });

        binding.pWorkerImgMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(view);
            }
        });
////التقيم

        firebaseFirestore.collection("posts")
                .document(firebaseUser.getPhoneNumber()).
                collection("userPost").whereEqualTo("jobState", "done")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            count = task.getResult().size();

                            // null object
//                            for (DocumentSnapshot document : task.getResult()) {
//                                rate = rate + document.getLong("Rating-clint");
//                            }

//                            for (DocumentSnapshot document : task.getResult()) {
//                                rate = rate + document.getLong("Rating-clint");
//                            }

                            for (DocumentSnapshot document : task.getResult()) {
                                Long rating = document.getLong("Rating-clint");
                                if (rating != null) {
                                    rate = rate + rating.longValue();
                                } else {
                                    // إجراء للتعامل مع القيمة الناقصة (مثلاً رمي استثناء أو إيقاف الحساب)
                                }
                            }

                            Log.d("tag", String.valueOf(rate));
                            Log.d("tag", String.valueOf(count));
                            if (rate != 0 && count != 0) {
                                int tvRate = (int) (rate / count);
                                binding.tvRate.setText(String.valueOf(tvRate));
                            } else {
                                binding.tvRate.setText("0");
                            }
                        }

                    }
                });


        return binding.getRoot();
    }

    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(getContext(), view);
        popupMenu.inflate(R.menu.edit_logout);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.menu_edit) {
                    Intent intent = new Intent(getContext(), EditWorkerProfileActivity.class);
                    startActivity(intent);
                    return true;
                } else if (itemId == R.id.menu_logout) {
                    FirebaseAuth.getInstance().signOut();
                    editor.clear();
                    editor.apply();

                    editor1.clear();
                    editor1.apply();

                    startActivity(new Intent(getContext(), LoginActivity.class));
                    getActivity().finish();
                    return true;
                }
                return false;
            }
        });
        popupMenu.show();
    }

    private void setRate() {
        Log.d("inWorkCount", inWorkCount + "");
        Log.d("doneCount", doneCount + "");
        Log.d("jobsCount", jobsCount + "");
        if (isWorkCountDone && isjobsCountDone && isdoneCountDone) {
            float employmentRate = 0, sum = 0, divide = 0;

            if (jobsCount > 0) {
                sum = (inWorkCount + doneCount);
                divide = sum / jobsCount;
                employmentRate = divide * 100;
            } else {
                employmentRate = 0;
            }

            Log.d("sum", sum + "");
            Log.d("divide", divide + "");
            Log.d("employmentRate", employmentRate + "");
            int roundedRate = (int) Math.round(employmentRate); // round to nearest integer
            Log.d("roundedRate", roundedRate + "");
            // String rateString = Integer.toString( roundedRate ); // convert to string
            binding.tvEmploymentRate.setText(roundedRate + "%");
        }
    }
}