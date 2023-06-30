package com.example.workersapp.Fragments;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.workersapp.Activities.PostActivity2;
import com.example.workersapp.Activities.PostActivity_forWorker;
import com.example.workersapp.Adapters.Post_forWorkerAdapter;
import com.example.workersapp.Adapters.ShowCategoryAdapter;
import com.example.workersapp.Listeneres.ItemClickListener;
import com.example.workersapp.R;
import com.example.workersapp.Utilities.AdClass;
import com.example.workersapp.Utilities.Post;
import com.example.workersapp.databinding.FragmentPostInWorkerBinding;
import com.example.workersapp.databinding.FragmentPostsBinding;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class PostFragment_inWorker extends Fragment {
    FragmentPostInWorkerBinding binding;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseStorage firebaseStorage;
    ShowCategoryAdapter adapter;

    Post_forWorkerAdapter postAdapter;
    List<String> categoryList;
    List<Post> postList;
    Post_forWorkerAdapter post_forWorkerAdapter;
    String jobState, title, description, expectedWorkDuration, projectedBudget, jobLocation;


    public PostFragment_inWorker() {
    }

    public static PostFragment_inWorker newInstance() {
        PostFragment_inWorker fragment = new PostFragment_inWorker();
        Bundle args = new Bundle();
        fragment.setArguments(args);
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

        FragmentPostInWorkerBinding binding = FragmentPostInWorkerBinding.inflate(inflater, container, false);

        //---> initializing Google Ad SDK
        MobileAds.initialize(getContext(), initializationStatus -> {
        });

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        categoryList = new ArrayList<>();
        postList = new ArrayList<>();
        List decoumtId = new ArrayList();
        List<String> Category = new ArrayList<>();
        firebaseFirestore.collection("workCategoryAuto").document("category")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Map<String, List<String>> categoryMap = (Map<String, List<String>>) documentSnapshot.get("category");
                        if (categoryMap != null) {
                            for (Map.Entry<String, List<String>> entry : categoryMap.entrySet()) {
                                String fieldName = entry.getKey();
                                List<String> fieldData = entry.getValue();
                                if (fieldName.equals(GetUserData())) {
                                    Log.d("Field Name", fieldName);
                                    Log.d("Field Data", fieldData.toString());
                                    for (String s : fieldData) {
                                        Category.add(s);
                                    }
                                    Log.e("Category", Category.toString());
                                }
                            }
                        }
                    } else {
                        Log.d("Error", "No such document");
                    }
                })
                .addOnFailureListener(e -> Log.d("Error", "Error getting document: " + e.getMessage()));

        firebaseFirestore.collection("users").get().addOnSuccessListener(queryDocumentSnapshots ->
        {
            for (DocumentSnapshot documentSnapshot1 : queryDocumentSnapshots) {
                decoumtId.add(documentSnapshot1);
                firebaseFirestore.collection("posts").document(documentSnapshot1.getId()).
                        collection("userPost").get()
                        .addOnCompleteListener(task -> {
                            for (DocumentSnapshot document : task.getResult()) {
                                if (document.getString("jobState").equals("open")) {
                                    Log.e("DecumentsCount", String.valueOf(task.getResult().size()));
                                    firebaseFirestore.document("posts/" + documentSnapshot1.getId() + "/userPost/" + document.getId()).get()
                                            .addOnSuccessListener(documentSnapshot -> {
                                                if (documentSnapshot.exists()) {
                                                    jobState = documentSnapshot.getString("jobState");
                                                    title = documentSnapshot.getString("title");
                                                    description = documentSnapshot.getString("description");
                                                    List<String> images = (List<String>) documentSnapshot.get("images");
                                                    List<String> categoriesList = (List<String>) documentSnapshot.get("categoriesList");

                                                    expectedWorkDuration = documentSnapshot.getString("expectedWorkDuration");
                                                    projectedBudget = documentSnapshot.getString("projectedBudget");
                                                    jobLocation = documentSnapshot.getString("jobLocation");

                                                    Post post = new Post(title, description, images, categoriesList, expectedWorkDuration, projectedBudget, jobLocation, jobState);
                                                    post.setPostId(document.getId());
                                                    post.setOwnerId(documentSnapshot1.getId());

                                                    postList.add(post);
                                                     postAdapter = new Post_forWorkerAdapter(postList, getContext(), new ItemClickListener() {
                                                        @Override
                                                        public void OnClick(int pos) {
                                                            Log.e("ItemClik", postList.get(pos).getPostId());
                                                            Intent intent = new Intent(getActivity(), PostActivity_forWorker.class);
                                                            intent.putExtra("PostId", postList.get(pos).getPostId());
                                                            intent.putExtra("OwnerId", postList.get(pos).getOwnerId()); // pass data to new activity

                                                            // pass data to new activity
                                                            startActivity(intent);
                                                        }
                                                    });
                                                }
                                                binding.RV.setAdapter(postAdapter);
                                                postAdapter.setList(postList);
                                                nativeAdList = new ArrayList<>();
                                                createNativeAd();
                                                binding.ProgressBar.setVisibility(View.GONE);
                                                binding.RV.setVisibility(View.VISIBLE);
                                            })
                                            .addOnFailureListener(e -> Log.e("Field", e.getMessage()));
                                    binding.RV.setLayoutManager(new LinearLayoutManager(getContext(),
                                            LinearLayoutManager.VERTICAL, false));



                                }
                            }
                        }).addOnFailureListener(runnable -> {
                        });


            }
        });


        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();

        firebaseFirestore.collection("offers")
                .document("favorites")
                .collection("favorites")
                .get()
                .addOnSuccessListener(new OnSuccessListener <QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<Post> updatedFavorites = queryDocumentSnapshots.toObjects(Post.class);
                        binding.RV.setAdapter( new Post_forWorkerAdapter(postList, getContext(), new ItemClickListener() {
                            @Override
                            public void OnClick(int pos) {

                            }
                        }));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Failed to retrieve favorite items", Toast.LENGTH_SHORT).show();
                    }
                });
        binding.RV.setLayoutManager( new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false));
    }

    String workType = null;

    String GetUserData() {

        // Get user details and set them in the view
        firebaseFirestore.collection("users")
                .document(Objects.requireNonNull(user.getPhoneNumber()))
                .get()
                .addOnSuccessListener(documentSnapshot1 -> {
                    if (documentSnapshot1.exists()) {
                        String w = documentSnapshot1.getString("work");
                        workType = w;
                    } else workType = "";
                })
                .addOnFailureListener(e -> {
                });
        return workType;

    }

    private static final String TAG = "--->Native Ad";
    private List<NativeAd> nativeAdList;
    private ArrayList<Object> objects;

    private void createNativeAd() {

        objects = new ArrayList<>();

        AdClass adClass = new AdClass();

        Log.d(TAG, "Google SDK Initialized");

        AdLoader adLoader = new AdLoader.Builder(getContext(), "ca-app-pub-4163051235104500/8636857436")
                .forNativeAd(nativeAd -> {
                    Log.d(TAG, "Native Ad Loaded");

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        if (getActivity().isDestroyed()) {
                            nativeAd.destroy();
                            Log.d(TAG, "Native Ad Destroyed");
                            return;
                        }
                    }

                    nativeAdList.add(nativeAd);

                    if (!adClass.getAdLoader().isLoading()) {
                        objects.add(postList.get(0));
                        objects.add(postList.get(1));
                        objects.add(postList.get(2));
                        objects.add(nativeAdList.get(0));
                        objects.add(postList.get(3));
                        objects.add(postList.get(4));
                        objects.add(postList.get(5));
                        objects.add(nativeAdList.get(1));
                        objects.add(postList.get(6));
                        postAdapter.setObject(objects);

                    }

                })

                .withAdListener(new AdListener() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                        // Handle the failure by logging, altering the UI, and so on.
                        Log.d(TAG, "Native Ad Failed To Load");

                        new CountDownTimer(10000, 1000) {

                            @Override
                            public void onTick(long millisUntilFinished) {
                                Log.d(TAG, "Timer : " + millisUntilFinished / 1000);
                            }

                            @Override
                            public void onFinish() {
                                Log.d(TAG, "Reloading NativeAd");

                                createNativeAd();
                            }
                        }.start();

                    }
                })
                .withNativeAdOptions(new NativeAdOptions.Builder()
                        .build())
                .build();

        adLoader.loadAds(new AdRequest.Builder().build(), 2);
        adClass.setAdLoader(adLoader);
    }


}