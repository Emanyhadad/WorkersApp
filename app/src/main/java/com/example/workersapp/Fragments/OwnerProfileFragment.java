package com.example.workersapp.Fragments;

import static androidx.constraintlayout.widget.ConstraintLayoutStates.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.workersapp.Activities.PostActivity2;
import com.example.workersapp.Adapters.FinishedJobsAdapter;
import com.example.workersapp.Adapters.ShowCategoryAdapter;
import com.example.workersapp.R;
import com.example.workersapp.Utilities.Post;
import com.example.workersapp.databinding.FragmentWorkOwnerProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
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
    List <String> categoryList;
    List< Post > postList;
    String jobState,title,description,expectedWorkDuration,projectedBudget,jobLocation;

    int openCount,inWorkCount,doneCount,jobsCount;
    boolean isWorkCountDone=false;
    boolean isdoneCountDone=false;
    boolean isjobsCountDone=false;

    boolean userData = false;
    boolean jobData = false;

    public OwnerProfileFragment() {
        // Required empty public constructor
    }


    public static OwnerProfileFragment newInstance( ) {
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
        firebaseFirestore=FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();

        categoryList=new ArrayList <>(  );
        postList = new ArrayList <>(  );


        //For User Data:
        DocumentReference userRef = firebaseFirestore.collection("users").document(firebaseUser.getPhoneNumber());

        userRef.get().addOnCompleteListener( task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    binding.ProgressBar.setVisibility( View.GONE );
                    binding.AppBar.setVisibility( View.VISIBLE );
                    String nickName = document.getString("nickName");
                    binding.tvWorkOwnerNickName.setText( nickName );
                    String city = document.getString("city");
                    binding.tvWorkOwnerCity.setText( city );
                    String image = document.getString("image");

                    if (getContext() != null) {
                        Glide.with(OwnerProfileFragment.this)
                                .load(image)
                                .circleCrop()
                                .error(R.drawable.worker)
                                .into(binding.imgProfileOwner);                    }

                    String fullName = document.getString("fullName");
                    binding.tvWorkOwnerName.setText( fullName );
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
        } );

        CollectionReference userPostsRef = firebaseFirestore.collection("posts")
                .document(firebaseUser.getPhoneNumber())
                .collection("userPost");

        //For JosCount
        userPostsRef.get().addOnSuccessListener( queryDocumentSnapshots -> { jobsCount = queryDocumentSnapshots.size();
            isjobsCountDone=true;
            setRate(  );
            binding.tvNumberOfJobs.setText( jobsCount+"" ); } ).addOnFailureListener( e -> Log.d(TAG, "Error getting documents: ", e) );

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

                isWorkCountDone=true;
                setRate(  );
            } else {
                Log.d(TAG, "Error getting documents: ", task.getException());
            }
        });

        //For DoneJobs
        Query doneJobPostsQuery = userPostsRef.whereEqualTo("jobState", "done");
        doneJobPostsQuery.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                doneCount = task.getResult().size();
                isdoneCountDone=true;
                setRate(  );
            } else {
                Log.d(TAG, "Error getting documents: ", task.getException());
            }
        });

        //For Finished Job:
        firebaseFirestore.collection("posts")
                .document(firebaseUser.getPhoneNumber())
                .collection("userPost").whereEqualTo( "jobState","done" )
                .get()
                .addOnCompleteListener(task -> {
                    jobData = true;
                    Log.e( "Posts",task.getResult().toString() );

                    if ( task.getResult().isEmpty() ){
                        binding.PB.setVisibility( View.GONE );
                        binding.SV.setVisibility( View.GONE );
                        binding.LLEmpty.setVisibility( View.VISIBLE );
                    }
                    else {
                        for ( DocumentSnapshot document : task.getResult()) {
                            firebaseFirestore.document("posts/" + firebaseUser.getPhoneNumber()+ "/userPost/" + document.getId()).get()
                                    .addOnSuccessListener( documentSnapshot -> {
                                        binding.PB.setVisibility( View.GONE );
                                        binding.LLEmpty.setVisibility( View.GONE );
                                        binding.SV.setVisibility( View.VISIBLE );
                                        if (documentSnapshot.exists()) {

                                            jobState = documentSnapshot.getString("jobState");
                                            title = documentSnapshot.getString("title");
                                            description= documentSnapshot.getString( "description" );
                                            List<String> images = (List<String>) documentSnapshot.get("images");
                                            List<String> categoriesList = (List<String>) documentSnapshot.get("categoriesList");

                                            expectedWorkDuration= documentSnapshot.getString( "expectedWorkDuration" );
                                            projectedBudget= documentSnapshot.getString( "projectedBudget" );
                                            jobLocation= documentSnapshot.getString( "jobLocation" );

                                            Post post = new Post( title,description,images,categoriesList,expectedWorkDuration,projectedBudget,jobLocation,jobState );
                                            post.setPostId( document.getId() );
                                            post.setOwnerId( firebaseUser.getPhoneNumber() );
                                            post.setWorkerId( documentSnapshot.getString( "workerId" ) );
                                            postList.add( post );

                                            binding.rcFinishedJobs.setAdapter( new FinishedJobsAdapter( postList , getContext( ), pos -> {
                                                Intent intent = new Intent(getActivity(), PostActivity2.class);
                                                intent.putExtra("PostId", postList.get( pos ).getPostId()); // pass data to new activity
                                                startActivity(intent);
                                            } ));

                                        } } )
                                    .addOnFailureListener( e -> Log.e( "Field",e.getMessage()) );
                            binding.rcFinishedJobs.setLayoutManager( new LinearLayoutManager(getContext(),
                                    LinearLayoutManager.VERTICAL, false));


                        }}
                } ).addOnFailureListener( runnable -> {} );




        return binding.getRoot();
    }

    private void setRate( ) {
        Log.d( "inWorkCount",inWorkCount+"" );
        Log.d( "doneCount",doneCount+"" );
        Log.d( "jobsCount",jobsCount+"" );
        if(isWorkCountDone&&isjobsCountDone&&isdoneCountDone) {
            float employmentRate = 0, sum = 0, divide = 0;
            
            if ( jobsCount > 0 ) {
                sum  =  ( inWorkCount + doneCount )  ;
                divide =  sum / jobsCount ;
                employmentRate=divide*100;
            } else {
                employmentRate = 0;
            }

            Log.d( "sum",sum+"" );
            Log.d( "divide",divide+"" );
            Log.d( "employmentRate",employmentRate+"" );
            int roundedRate = ( int ) Math.round( employmentRate ); // round to nearest integer
            Log.d( "roundedRate",roundedRate+"" );
            // String rateString = Integer.toString( roundedRate ); // convert to string
            binding.tvEmploymentRate.setText( roundedRate+"%" );
        }
    }
}