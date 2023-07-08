package com.example.workersapp.Fragments;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.workersapp.Activities.FavouriteActivity;
import com.example.workersapp.Activities.PostActivity_forWorker;
import com.example.workersapp.Activities.SearchActivity;
import com.example.workersapp.Adapters.Post_forWorkerAdapter;
import com.example.workersapp.Adapters.ShowCategoryAdapter;
import com.example.workersapp.Listeneres.ItemClickListener;
import com.example.workersapp.R;
import com.example.workersapp.Utilities.AdClass;
import com.example.workersapp.Utilities.Post;
import com.example.workersapp.databinding.FragmentPostInWorkerBinding;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
    long addedTime;


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

        binding = FragmentPostInWorkerBinding.inflate(inflater, container, false);

//        //---> initializing Google Ad SDK
//        MobileAds.initialize(getContext(), initializationStatus -> {
//        });



        binding.etSearch.setOnClickListener(view -> startActivity(new Intent(getContext(), SearchActivity.class)));

        binding.favoriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), FavouriteActivity.class));
            }
        });


        return binding.getRoot();
    }


    @Override
    public void onResume() {
        super.onResume();
        getData();
        firebaseFirestore.collection("offers")
                .document("favorites")
                .collection("favorites")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<Post> updatedFavorites = queryDocumentSnapshots.toObjects(Post.class);
                        // تأكد من أن الـ binding ليس قيمة null قبل استخدامه
                        if (binding != null && binding.RV != null) {
                            binding.RV.setVisibility(View.VISIBLE);
                            binding.RV.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                            binding.RV.setAdapter(new Post_forWorkerAdapter(postList, getContext(), new ItemClickListener() {
                                @Override
                                public void OnClick(int pos) {

                                }
                            }));
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Todo Add LLField
                    }
                });
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
void getData(){

    firebaseFirestore = FirebaseFirestore.getInstance();
    firebaseAuth = FirebaseAuth.getInstance();
    user = firebaseAuth.getCurrentUser();
    binding.inculd.tvPageTitle.setText(getString(R.string.jobs));
    categoryList = new ArrayList<>();
    postList = new ArrayList<>();
    final String[] fieldName1 = new String[ 1 ];
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
                            Log.d("Field Name", fieldName);
                            Log.d("Field Data", fieldData.toString());
                            for (String cat : fieldData) {
                                categoryList.add(cat);
                            }
                            Log.e("Category", categoryList.toString());
                        }
                    }
                } else {
                    Log.d("Error", "No such document");
                }
            })
            .addOnFailureListener(e -> {
                Log.d("Error", "Error getting document: " + e.getMessage());
            });

    firebaseFirestore.collection( "users" ).get().
            addOnSuccessListener( queryDocumentSnapshots -> {
                for ( DocumentSnapshot documentSnapshot:queryDocumentSnapshots ){
                    firebaseFirestore.collection( "posts" ).
                            document( documentSnapshot.getId() ).collection( "userPost" )
                            .whereEqualTo( "jobState","open" )
//                            .whereEqualTo( "work" ,fieldName1[0])
                            .orderBy( "addedTime", Query.Direction.DESCENDING )
                            .get().addOnSuccessListener( queryDocumentSnapshots1 -> {
                                for ( DocumentSnapshot documentSnapshot1: queryDocumentSnapshots1.getDocuments()) {
                                    jobState = documentSnapshot1.getString("jobState");
                                    title = documentSnapshot1.getString("title");
                                    description = documentSnapshot1.getString("description");
                                    List<String> images = (List<String>) documentSnapshot1.get("images");
                                    List<String> categoriesList = (List<String>) documentSnapshot1.get("categoriesList");

                                    expectedWorkDuration = documentSnapshot1.getString("expectedWorkDuration");
                                    projectedBudget = documentSnapshot1.getString("projectedBudget");
                                    jobLocation = documentSnapshot1.getString("jobLocation");
                                    addedTime = documentSnapshot1.getLong("addedTime");

                                    Post post = new Post(title, description, images, categoriesList, expectedWorkDuration, projectedBudget, jobLocation, jobState,addedTime);
                                    post.setPostId(documentSnapshot1.getId());
                                    post.setOwnerId(documentSnapshot1.getId());

                                    postList.add(post);
                                    postAdapter = new Post_forWorkerAdapter(postList, getContext(), pos -> {
                                        Log.e("ItemClik", postList.get(pos).getPostId());
                                        Intent intent = new Intent(getActivity(), PostActivity_forWorker.class);
                                        intent.putExtra("PostId", postList.get(pos).getPostId());
                                        intent.putExtra("OwnerId", postList.get(pos).getOwnerId()); // pass data to new activity

                                        // pass data to new activity
                                        startActivity(intent);
                                    });

                                }
                                binding.RV.setAdapter(postAdapter);
                                binding.ProgressBar.setVisibility(View.GONE);
                                binding.RV.setVisibility(View.VISIBLE);
                                binding.RV.setLayoutManager(new LinearLayoutManager(getContext(),
                                        LinearLayoutManager.VERTICAL, false));

                            });

                }

            } );


}
//    private static final String TAG = "--->Native Ad";
//    private List<NativeAd> nativeAdList;
//    private ArrayList<Object> objects;
//
//    private void createNativeAd() {
//
//        objects = new ArrayList<>();
//        objects.addAll(postList);
//
//        AdClass adClass = new AdClass();
//
//        Log.d(TAG, "Google SDK Initialized");
//
//        AdLoader adLoader = new AdLoader.Builder(getContext(), "ca-app-pub-4163051235104500/1257264129")
//                .forNativeAd(nativeAd -> {
//                    Log.d(TAG, "Native Ad Loaded");
//
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
//                        if (getActivity().isDestroyed()) {
//                            nativeAd.destroy();
//                            Log.d(TAG, "Native Ad Destroyed");
//                            return;
//                        }
//                    }
//
//                    nativeAdList.add(nativeAd);
//
//                    if (!adClass.getAdLoader().isLoading()) {
////                        objects.add(postList.get(0));
////                        objects.add(postList.get(1));
////                        objects.add(postList.get(2));
////                        objects.add(nativeAdList.get(0));
////                        objects.add(postList.get(3));
////                        objects.add(postList.get(4));
////                        objects.add(postList.get(5));
////                        objects.add(nativeAdList.get(1));
////                        objects.add(postList.get(6));
//
//                        for (int i = 0; i < objects.size(); i++) {
//                            if (i%5==0 && ! (objects.get(i) instanceof NativeAd))
//                              objects.add(i,nativeAd);
//                        }
//                    }
//
//                })
//
//                .withAdListener(new AdListener() {
//                    @Override
//                    public void onAdFailedToLoad(@NonNull LoadAdError adError) {
//                        // Handle the failure by logging, altering the UI, and so on.
//                        Log.d(TAG, "Native Ad Failed To Load");
//                        Log.d(TAG, adError.getMessage());
//                        Log.d(TAG, adError.toString());
//
////                        new CountDownTimer(10000, 1000) {
////
////                            @Override
////                            public void onTick(long millisUntilFinished) {
////                                Log.d(TAG, "Timer : " + millisUntilFinished / 1000);
////                            }
////
////                            @Override
////                            public void onFinish() {
////                                Log.d(TAG, "Reloading NativeAd");
////
////                                createNativeAd();
////                            }
////                        }.start();
//
//                    }
//                })
//                .withNativeAdOptions(new NativeAdOptions.Builder()
//                        .build())
//                .build();
//
//        adLoader.loadAds(new AdRequest.Builder().build(), 2);
//        adClass.setAdLoader(adLoader);
//        postAdapter.setObject(objects);
//
//    }


}