package com.example.workersapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.workersapp.Adapters.ShowCategoryAdapter;
import com.example.workersapp.Adapters.ShowImagesAdapter;
import com.example.workersapp.R;
import com.example.workersapp.Utilities.Offer;
import com.example.workersapp.databinding.ActivityPostForWorkerBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressLint( "Registered" )
public class PostActivity_forWorker extends AppCompatActivity {
    String offerDes,offerPrice,offerDuration;
    ShowCategoryAdapter showCategoryAdapter;
    ShowImagesAdapter showImagesAdapter;
    String jobState,title,description,expectedWorkDuration,projectedBudget,jobLocation,projectState;

    FirebaseFirestore firestore;
    DocumentReference documentReference;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    String clintID,postId,path,duration,budget,formPath;
    int formsCount;



ActivityPostForWorkerBinding binding;
    @Override
    protected void onCreate( @NonNull Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        binding = ActivityPostForWorkerBinding.inflate( getLayoutInflater() );
        setContentView( binding.getRoot() );
        binding.progressBar.setVisibility( View.VISIBLE );

        //For duration
        //Todo put in Util
        String[] durationList = {"يوم", "يومين", "3 ايام", "4 ايام", "5 ايام", "اسبوع", "اسبوعين", "3 اسابيع", "شهر", "شهرين"};
        ArrayAdapter <String> durationAdapter = new ArrayAdapter<>(getBaseContext(), R.layout.list_item, durationList);
        AutoCompleteTextView autoCompleteDuration = (AutoCompleteTextView) binding.PAsplOfferDuration.getEditText();
        autoCompleteDuration.setAdapter(durationAdapter);
        autoCompleteDuration.setOnItemClickListener( ( adapterView , view , i , l ) -> duration = adapterView.getItemAtPosition(i).toString() );

        //For budgetList
        //Todo put in Util
        String[] budgetList = {"50", "100", "150", "200", "250", "300"};
        ArrayAdapter<String> budgetAdapter = new ArrayAdapter<>(getBaseContext(), R.layout.list_item, budgetList);
        AutoCompleteTextView autoCompleteBudget = (AutoCompleteTextView) binding.PASpLProjectedBudget.getEditText();
        autoCompleteBudget.setAdapter(budgetAdapter);
        autoCompleteBudget.setOnItemClickListener( ( adapterView , view , i , l ) -> budget = adapterView.getItemAtPosition(i).toString() );




        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();


        //GetPost
        clintID = "+970595964511";
        postId = "b2nYHmf9PCV4NhRWd0la2Wf9lqk11683192375017"; //TODO GET FROM INTENT
        firestore.collection( "forms" ).document( clintID).collection( "userForm" ).get().addOnSuccessListener( runnable -> {
            formsCount = runnable.size();
            Log.e( "sizesizesize",runnable.size()+"" );
        } );

        path = "posts/" + clintID + "/userPost/" + postId ;
        documentReference = firestore.collection("posts").document( clintID ).
                collection("userPost").document(postId);

        firestore.document(path).get()
                .addOnSuccessListener( documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        binding.progressBar.setVisibility( View.GONE );
                        binding.ScrollView.setVisibility( View.VISIBLE );
                        jobState = documentSnapshot.getString("jobState");
                        projectState=jobState;

                        title = documentSnapshot.getString("title");
                        description= documentSnapshot.getString( "description" );
                        List<String> images = (List<String>) documentSnapshot.get("images");
                        List<String> categoriesList = (List<String>) documentSnapshot.get("categoriesList");

                        expectedWorkDuration= documentSnapshot.getString( "expectedWorkDuration" );
                        projectedBudget= documentSnapshot.getString( "projectedBudget" );
                        jobLocation= documentSnapshot.getString( "jobLocation" );
                        binding.tvPostTitle.setText( title );
                        binding.tvPostDec.setText( description );
                        //TODO GET Timestamp
                        binding.tvPostPrice.setText( projectedBudget );
                        binding.tvPostLoc.setText( jobLocation );
                        binding.tvJobTime.setText( expectedWorkDuration );

                        showCategoryAdapter = new ShowCategoryAdapter( ( ArrayList < String > ) categoriesList );
                        binding.CategoryRecycle.setAdapter( showCategoryAdapter );
                        binding.CategoryRecycle.setLayoutManager( new LinearLayoutManager(getBaseContext(),
                                LinearLayoutManager.HORIZONTAL, false));

                        if ( images.size() == 0 ){binding.ImageRecycle.setVisibility( View.GONE );}
                        showImagesAdapter = new ShowImagesAdapter(images, this);
                        binding.ImageRecycle.setAdapter(showImagesAdapter);
                        binding.ImageRecycle.setLayoutManager(new LinearLayoutManager(this,
                                LinearLayoutManager.HORIZONTAL, false));
                    } else {
                        // Handle the case when the document doesn't exist
                    } } )
                .addOnFailureListener( e -> {
                    Log.e("getPot", "Error getting documents: ", e);

                } );




        CollectionReference offersRef = firestore.collection("users").document(clintID).collection("JobApplied");
        binding.PB2.setVisibility( View.VISIBLE );

        offersRef.document(postId).get().addOnSuccessListener(documentSnapshot -> {
            binding.PB2.setVisibility( View.GONE );
            //TODO chang clintID to user.getPhoneNumber in All her
            if (documentSnapshot.exists()) {
                // The document exists, so show a message that the user has already applied
                Toast.makeText(getApplicationContext(), "You have already applied for a job", Toast.LENGTH_SHORT).show();
                binding.PB2.setVisibility( View.VISIBLE );
                binding.tvWriteOffer.setText("العرض الخاص بك");

                // Get user details and set them in the view
                firestore.collection("users").document(Objects.requireNonNull(clintID))
                        .get()
                        .addOnSuccessListener(documentSnapshot1 -> {
                            if (documentSnapshot1.exists()) {
                                binding.PB2.setVisibility( View.GONE );
                                binding.LLWriteOffer.setVisibility( View.GONE );
                                binding.LLSendedOffer.setVisibility( View.VISIBLE );
                                String fullName = documentSnapshot1.getString("fullName");
                                binding.tvOfferWorkerName.setText(fullName);
                                String image = documentSnapshot1.getString("image");
                                Glide.with(getBaseContext())
                                        .load(image)
                                        .circleCrop()
                                        .error(R.drawable.worker)
                                        .into(binding.OfferImgWorker);
                            }
                        })
                        .addOnFailureListener(e -> {Log.e( "DataUser",e.toString() );});

                binding.tvCountWorks.setText(formsCount + "");


                firestore.collection("users").document(clintID).collection("JobApplied").
                        document(postId).get().addOnSuccessListener( runnable -> {
                    binding.tvSendOfferDuration.setText(runnable.get( "offerDuration" ).toString());
                    binding.tvSendOfferDec.setText(runnable.get( "offerDescription" ).toString() );
                    binding.tvSendOfferPrice.setText(runnable.get( "offerBudget" ).toString());
                } );
            } else {
                binding.LLWriteOffer.setVisibility( View.VISIBLE );

                // The document does not exist, so proceed to add the offer
                binding.btnSendOffer.setOnClickListener( view -> {

                offerDes=  Objects.requireNonNull( binding.etOfferDes.getText( ) ).toString();
                offerPrice=binding.PASpOfferPrice.getText().toString();
                offerDuration=binding.PAspOfferDuration.getText().toString();
                Offer offer = new Offer( offerPrice,offerDuration,offerDes,String.valueOf( clintID )  );

                new AlertDialog.Builder(PostActivity_forWorker.this)
                        .setMessage("هل أنت متأكد أنك تريد تقديم عرضك؟")
                        .setNegativeButton("لا, تعديل", (dialogInterface, i) -> {
                        })
                        .setPositiveButton("نعم ، أرسل", (dialogInterface, i) -> {
                            binding.PB2.setVisibility( View.VISIBLE );

                            // Create a new offer object and set it in the "Offers" collection
                            firestore.document(path).collection("Offers").document(String.valueOf(clintID)).set(offer);

                            // Set the offer in the "JobApplied" collection
                            firestore.collection("users").document(clintID).collection("JobApplied")
                                    .document(postId).set(offer);

                            binding.LLWriteOffer.setVisibility(View.GONE);
                            binding.tvWriteOffer.setText("العرض الخاص بك");

                            binding.tvSendOfferDuration.setText(offerDuration);
                            binding.tvSendOfferDec.setText(offerDes);
                            binding.tvSendOfferPrice.setText(offerPrice);
                            binding.tvCountWorks.setText(formsCount + "");

                            // Get user details and set them in the view
                            firestore.collection("users").document(Objects.requireNonNull(clintID))
                                    .get()
                                    .addOnSuccessListener(documentSnapshot1 -> {
                                        binding.PB2.setVisibility( View.GONE );
                                        binding.LLSendedOffer.setVisibility(View.VISIBLE);

                                        if (documentSnapshot1.exists()) {
                                            String fullName = documentSnapshot1.getString("fullName");
                                            binding.tvOfferWorkerName.setText(fullName);
                                            String image = documentSnapshot1.getString("image");
                                            Glide.with(getBaseContext())
                                                    .load(image)
                                                    .circleCrop()
                                                    .error(R.drawable.worker)
                                                    .into(binding.OfferImgWorker);
                                        }
                                    })
                                    .addOnFailureListener(e -> {});
                        }).create().show();} );
            }
        }).addOnFailureListener(e -> {});

        //Send Offer
//        CollectionReference offersRef = firestore.collection("users").document(user.getPhoneNumber()).collection("JobApplied");
//
//        offersRef.document( user.getPhoneNumber() ).get().addOnSuccessListener( runnable1 -> {
//                        binding.LLWriteOffer.setVisibility( View.GONE );
//                        binding.tvWriteOffer.setText( "العرض الخاص بك" );
//                        binding.LLSendedOffer.setVisibility( View.VISIBLE );
//                        binding.tvSendOfferDuration.setText( offerDuration );
//                        binding.tvSendOfferDec.setText( offerDes );
//                        binding.tvSendOfferPrice.setText( offerPrice );
//                        binding.tvCountWorks.setText( formsCount+"");
//                        firestore.collection("users").document(Objects.requireNonNull(user.getPhoneNumber()))
//                                .get()
//                                .addOnSuccessListener( documentSnapshot -> {
//                                    if (documentSnapshot.exists()) {
//                                        String fullName = documentSnapshot.getString("fullName");
//                                        binding.tvOfferWorkerName.setText( fullName );
//                                        String image = documentSnapshot.getString("image");
//                                        Glide.with(getBaseContext())
//                                                .load(image)
//                                                .circleCrop()
//                                                .error(R.drawable.worker)
//                                                .into(binding.OfferImgWorker); } } )
//                                .addOnFailureListener( e -> {});
//        } ).addOnFailureListener( runnable1 -> {
//            binding.btnSendOffer.setOnClickListener( view -> {
//
//                offerDes=  Objects.requireNonNull( binding.etOfferDes.getText( ) ).toString();
//                offerPrice=binding.PASpOfferPrice.getText().toString();
//                offerDuration=binding.PAspOfferDuration.getText().toString();
//                Offer offer = new Offer( offerPrice,offerDuration,offerDes,String.valueOf( user.getPhoneNumber() )  );
//
//                //TODO: GET USERID
//
//                new AlertDialog.Builder(PostActivity_forWorker.this)
//                        .setMessage("هل أنت متأكد من عرض العمل الخاص بك؟")
//                        .setNegativeButton("لا،تعديل", ( dialogInterface , i ) -> {
//                        } )
//                        .setPositiveButton("نعم تقديم", ( dialogInterface , i ) -> {
//                            //Todo Send Offer
//
//                            firestore.document( path ).collection( "Offers" ).document( String.valueOf( user.getPhoneNumber() ) ).set( offer )
//                                    .addOnCompleteListener( runnable -> {
//                                        binding.LLWriteOffer.setVisibility( View.GONE );
//                                        binding.tvWriteOffer.setText( "العرض الخاص بك" );
//                                        binding.LLSendedOffer.setVisibility( View.VISIBLE );
//                                        binding.tvSendOfferDuration.setText( offerDuration );
//                                        binding.tvSendOfferDec.setText( offerDes );
//                                        binding.tvSendOfferPrice.setText( offerPrice );
//                                        binding.tvCountWorks.setText( formsCount+"");
//                                        firestore.collection("users").document(Objects.requireNonNull(user.getPhoneNumber()))
//                                                .get()
//                                                .addOnSuccessListener( documentSnapshot -> {
//                                                    if (documentSnapshot.exists()) {
//                                                        String fullName = documentSnapshot.getString("fullName");
//                                                        binding.tvOfferWorkerName.setText( fullName );
//                                                        String image = documentSnapshot.getString("image");
//                                                        Glide.with(getBaseContext())
//                                                                .load(image)
//                                                                .circleCrop()
//                                                                .error(R.drawable.worker)
//                                                                .into(binding.OfferImgWorker); } } )
//                                                .addOnFailureListener( e -> {}); } );
//
//                            firestore.collection("user").document(user.getPhoneNumber()).collection("JobApplied").document( user.getPhoneNumber() ).set( offer );
//                        } ).create().show();
//
//            } );

  //      } );


    }}

