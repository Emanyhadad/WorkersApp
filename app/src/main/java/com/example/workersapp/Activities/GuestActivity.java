package com.example.workersapp.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.workersapp.Adapters.Post_forWorkerAdapter;
import com.example.workersapp.Adapters.ShowCategoryAdapter;
import com.example.workersapp.Listeneres.ItemClickListener;
import com.example.workersapp.R;
import com.example.workersapp.Utilities.Post;
import com.example.workersapp.databinding.ActivityGuestBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class GuestActivity extends AppCompatActivity {
    ActivityGuestBinding binding ;
    FirebaseFirestore firebaseFirestore;

    AlertDialog.Builder builder;
    Post_forWorkerAdapter postAdapter;
    List <String> categoryList;
    List< Post > postList;
    long addedTime;
    String jobState, title, description, expectedWorkDuration, projectedBudget, jobLocation;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        binding = ActivityGuestBinding.inflate( getLayoutInflater() );
        setContentView( binding.getRoot() );

        firebaseFirestore = FirebaseFirestore.getInstance();

        binding.inculd.tvPageTitle.setText( getString( R.string.jobs ));

        categoryList = new ArrayList <>();
        postList = new ArrayList<>();
        List decoumtId = new ArrayList();
        List<String> Category = new ArrayList<>();
//        firebaseFirestore.collection("workCategoryAuto").
//                document("category")
//                .get()
//                .addOnSuccessListener(documentSnapshot -> {
//                    if (documentSnapshot.exists()) {
//                        Map <String, List<String>> categoryMap =
//                                (Map<String, List<String>>) documentSnapshot.get("category");
//                        if (categoryMap != null) {
//                            for (Map.Entry<String, List<String>> entry : categoryMap.entrySet()) {
//                                List<String> fieldData = entry.getValue();
//                                for (String s : fieldData) {
//                                    Category.add(s);
//                                }
//                            }
//                        }
//                    } else {
//                        Log.d("Error", "No such document");
//                    }
//                })
//                .addOnFailureListener(e -> {
//                    ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService( Context.CONNECTIVITY_SERVICE);
//                    NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
//                    boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
//
//                    if (!isConnected) {
//                        binding.LLNoWifi.setVisibility(View.VISIBLE);
//                        binding.ProgressBar.setVisibility(View.GONE);
//                        binding.RV.setVisibility(View.GONE);
//                    }});

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
                                                    addedTime = documentSnapshot.getLong("addedTime");

                                                    Post post = new Post(title, description, images, categoriesList, expectedWorkDuration, projectedBudget, jobLocation, jobState,addedTime);
                                                    post.setPostId(document.getId());
                                                    post.setOwnerId(documentSnapshot1.getId());

                                                    postList.add(post);
                                                    postAdapter = new Post_forWorkerAdapter(postList, getBaseContext(), new ItemClickListener() {
                                                        @Override
                                                        public void OnClick(int pos) {
                                                            Log.e("ItemClik", postList.get(pos).getPostId());
                                                        RegisterDialog();
                                                        }
                                                    });
                                                }
                                                binding.RV.setAdapter(postAdapter);
                                                postAdapter.setList(postList);
                                                binding.ProgressBar.setVisibility(View.GONE);
                                                binding.RV.setVisibility(View.VISIBLE);
                                            })
                                            .addOnFailureListener(e -> Log.e("Field", e.getMessage()));
                                    binding.RV.setLayoutManager(new LinearLayoutManager(getBaseContext(),
                                            LinearLayoutManager.VERTICAL, false));



                                }
                            }
                        }).addOnFailureListener(runnable -> {
                        });
            }
        });
//        firebaseFirestore.collection("users").get().addOnSuccessListener(queryDocumentSnapshots ->
//        {
//            for ( DocumentSnapshot documentSnapshot1 : queryDocumentSnapshots) {
//                decoumtId.add(documentSnapshot1);
//                Log.e( "users" ,documentSnapshot1.getId());
//                firebaseFirestore.collection("posts").document(documentSnapshot1.getId()).
//                        collection("userPost")
//                        .whereEqualTo( "jobState","open" )
//                        .orderBy( "addedTime", Query.Direction.DESCENDING )
//                        .get()
//                        .addOnCompleteListener(task -> {
//                            for (DocumentSnapshot document : task.getResult()) {
//                                Log.e( "posts" ,document.getId());
//                                Log.e( "AddedTime",document.getLong("addedTime")+"");
//                                //Todo Category
//                                Log.e("DecumentsCount", String.valueOf(task.getResult().size()));
//                                jobState = document.getString("jobState");
//                                title = document.getString("title");
//                                description = document.getString("description");
//                                List<String> images = (List<String>) document.get("images");
//                                List<String> categoriesList = (List<String>) document.get("categoriesList");
//
//                                expectedWorkDuration = document.getString("expectedWorkDuration");
//                                projectedBudget = document.getString("projectedBudget");
//                                jobLocation = document.getString("jobLocation");
//                                addedTime = document.getLong("addedTime");
//                                Post post = new Post(title, description, images, categoriesList,
//                                        expectedWorkDuration, projectedBudget, jobLocation, jobState,addedTime);
//                                post.setPostId(document.getId());
//                                post.setOwnerId(documentSnapshot1.getId());
//
//                                postList.add(post);
//                                postAdapter = new Post_forWorkerAdapter(postList, getBaseContext(), pos -> RegisterDialog() );
//                                if (postList.isEmpty()) {
//                                    binding.RV.setVisibility( View.GONE );
//                                    binding.ProgressBar.setVisibility( View.GONE );
//                                    binding.LLEmpty.setVisibility(View.VISIBLE);
//                                } else {
//                                    binding.LLEmpty.setVisibility(View.GONE);
//                                    binding.RV.setAdapter(postAdapter);
//
//                                    postAdapter.setList(postList);
//                                    binding.ProgressBar.setVisibility( View.GONE);
//                                    binding.RV.setVisibility(View.VISIBLE);
//                                } }
//
//                            binding.RV.setLayoutManager(new LinearLayoutManager(getBaseContext(),
//                                    LinearLayoutManager.VERTICAL, false));
//                        }).addOnFailureListener(runnable -> {
//                            Log.e( "Error",runnable.getMessage() );
//                            binding.RV.setVisibility( View.GONE );
//                            binding.ProgressBar.setVisibility( View.GONE );
//                            binding.LLEmpty.setVisibility(View.VISIBLE);
//
//                        });
//            }
//        }).addOnFailureListener( runnable -> {
//            Log.e( "Error1",runnable.getMessage() );
//            binding.ProgressBar.setVisibility(View.GONE);
//
//            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService( Context.CONNECTIVITY_SERVICE);
//            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
//            boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
//
//            if (!isConnected) {
//                binding.LLNoWifi.setVisibility(View.VISIBLE);
//                binding.ProgressBar.setVisibility(View.GONE);
//                binding.RV.setVisibility(View.GONE);
//            }} );

        binding.etSearch.setOnClickListener( view -> RegisterDialog()  );

        binding.favoriteBtn.setOnClickListener( view -> RegisterDialog() );

    }
    void RegisterDialog(){
        builder = new AlertDialog.Builder( GuestActivity.this );
        builder.setMessage( getString( R.string.DoNotHave ) )
                .setNegativeButton( getString( R.string.tvInAnotherTime ) , ( dialogInterface , i ) -> {
                    dialogInterface.dismiss( );
                    builder = null;
                } ).setPositiveButton( getString( R.string.tvRegisterNow ) , ( dialogInterface , i ) -> {
                    startActivity( new Intent( getBaseContext(),LoginActivity.class ) );
                    finish();
                    dialogInterface.dismiss( );
                    builder = null;
                } ).setCancelable( false ).create( ).show( );
    }

}