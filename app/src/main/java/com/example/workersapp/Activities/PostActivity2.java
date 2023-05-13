package com.example.workersapp.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.workersapp.Adapters.ShowCategoryAdapter;
import com.example.workersapp.Adapters.ShowImagesAdapter;
import com.example.workersapp.R;
import com.example.workersapp.databinding.ActivityPost2Binding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class PostActivity2 extends AppCompatActivity {
    ShowCategoryAdapter showCategoryAdapter;

    ShowImagesAdapter showImagesAdapter;
    List < Uri > imageList;

    private Dialog evaluationDialog;
    private AlertDialog deleteDialog;
    AlertDialog.Builder deleteDialog_builder;
    View deleteDialogView;
    FirebaseFirestore firestore;
    DocumentReference documentReference;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    String postId,path;
    float rating;
    String comment;

    String jobState,title,description,expectedWorkDuration,projectedBudget,jobLocation,projectState;
    List<String> images,categoriesList;
    ActivityPost2Binding binding;
    @SuppressLint( "UseCompatLoadingForDrawables" )
    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        binding=ActivityPost2Binding.inflate( getLayoutInflater() );
        setContentView( binding.getRoot() );

        binding.progressBar.setVisibility( View.VISIBLE );

        //For Rating
        evaluationDialog = new Dialog(this);
        evaluationDialog.setContentView( R.layout.dialog_worker_evaluation);

        //Delete Dialog
        deleteDialog_builder = new AlertDialog.Builder(this);
        deleteDialogView = getLayoutInflater().inflate(R.layout.deluge_delete_work, null);
        deleteDialog_builder.setView(deleteDialogView);
        deleteDialog = deleteDialog_builder.create();



        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

//        userId = "+970594461722";//TODO GET UserCurrent
        postId = getIntent().getStringExtra( "PostId" ).trim(); //TODO GET FROM INTENT

                path = "posts/" + user + "/userPost/" + Objects.requireNonNull(postId) ;
        documentReference = firestore.collection("posts").document( user.getPhoneNumber() ).
                collection("userPost").document(postId);

        Toast.makeText( this , Objects.requireNonNull(postId) , Toast.LENGTH_SHORT ).show( );

        firestore.collection("posts").document( user.getPhoneNumber() ).
                collection("userPost").document(Objects.requireNonNull(postId)).get()
                .addOnSuccessListener( documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        jobState = documentSnapshot.getString("jobState");
                        projectState=jobState;

                        switch ( projectState ){
                            case "open":
                                binding.APbtnCloseProject.setVisibility( View.VISIBLE );
                                binding.APbtnComments.setVisibility( View.VISIBLE );
                                binding.APTvJobData.setVisibility( View.GONE );
                                binding.APCLInWork.setVisibility( View.GONE );
                                DeleteJob( );
                                binding.APbtnComments.setOnClickListener( view ->{
                                        Intent intent = new Intent( PostActivity2.this, OffersActivity.class);
                                        intent.putExtra( "postID",postId );
                                        startActivity(intent  );});
                                binding.APbtnCloseProject.setOnClickListener( view -> deleteDialog.show() );
                                break;

                            case "close":
                                binding.PAtvProjectstate.setText( "مغلق" );
                                binding.PAtvProjectstate.setBackground(getResources().getDrawable( R.drawable.close_bg ));
                                binding.APbtnCloseProject.setVisibility( View.GONE );
                                binding.APbtnComments.setVisibility( View.GONE );
                                binding.APTvJobData.setVisibility( View.GONE );
                                binding.APCLInWork.setVisibility( View.GONE );

                                Map <String, Object> updates = new HashMap <>();
                                updates.put("jobState", "close");
                                documentReference.update(updates).addOnSuccessListener( aVoid -> {
                                    // Handle the case when the update is successful
                                } ).addOnFailureListener( e -> {
                                    // Handle the case when the update fails
                                } );
                                break;

                            case "inWork":
                                binding.PAtvProjectstate.setText("قيد العمل");
                                binding.PAtvProjectstate.setBackground(getResources().getDrawable(R.drawable.inwork_bg));
                                binding.APbtnCloseProject.setVisibility(View.GONE);
                                binding.APbtnComments.setVisibility(View.GONE);
                                binding.APTvJobData.setVisibility(View.VISIBLE);
                                binding.APCLInWork.setVisibility(View.VISIBLE);
                                binding.ApBtnFinishJob.setVisibility(View.VISIBLE);
                                binding.APTvIWJStartDate.setText(documentSnapshot.getString("jobStartDate"));
                                //Todo get data from offer (price)
                                Rating();
                                firestore.collection("users")
                                        .document(documentSnapshot.getString("workerId"))
                                        .get()
                                        .addOnSuccessListener(documentSnapshot1 -> {
                                            if (documentSnapshot1.exists()) {
                                                String fullName = documentSnapshot1.getString("fullName");
                                                binding.tvIWJWorkerName.setText(fullName);
                                                String image = documentSnapshot1.getString("image");
                                                Glide.with(PostActivity2.this)
                                                        .load(image)
                                                        .circleCrop()
                                                        .error(R.drawable.worker)
                                                        .into(binding.imgIWJWorker);
                                            }
                                        })
                                        .addOnFailureListener(e -> {});

                                binding.ApBtnFinishJob.setOnClickListener(view -> evaluationDialog.show() );
                                break;

                            case "done":
                                binding.PAtvProjectstate.setText( "مكتمل" );
                                binding.PAtvProjectstate.setBackground(getResources().getDrawable(R.drawable.bg_done));
                                binding.APbtnCloseProject.setVisibility(View.GONE);
                                binding.APbtnComments.setVisibility(View.GONE);
                                binding.APTvJobData.setVisibility(View.VISIBLE);
                                binding.APCLInWork.setVisibility(View.VISIBLE);
                                binding.ApBtnFinishJob.setVisibility(View.GONE);
                                binding.linearLayoutRateClint.setVisibility( View.VISIBLE );
                                binding.linearLayoutRateWorker.setVisibility( View.VISIBLE );
                                binding.tvWorkerComment.setText( documentSnapshot.getString( "Comment-worker" ) );
                                double ratingWorker =  documentSnapshot.getDouble("Rating-worker");
                                binding.ratingBarWorker.setProgress((int) ratingWorker*2);
                                binding.tvClintComment.setText( documentSnapshot.getString( "Comment-clint" ) );
                                double ratingClint =  documentSnapshot.getDouble("Rating-clint");
                                binding.ratingBarClint.setProgress((int) ratingClint*2);
                                break;

                        }

                        title = documentSnapshot.getString("title");
                        binding.APTvJobTitle.setText( title );

                        description= documentSnapshot.getString( "description" );
                        binding.APTvJobDec.setText( description );

                        List<String> images = (List<String>) documentSnapshot.get("images");
                        if (images == null || images.isEmpty()) {
                            binding.APImageRV.setVisibility(View.GONE);
                        } else {
                            showImagesAdapter = new ShowImagesAdapter(images, this);
                            binding.APImageRV.setAdapter(showImagesAdapter);
                            binding.APImageRV.setLayoutManager(new LinearLayoutManager(this,
                                    LinearLayoutManager.HORIZONTAL, false));
                        }

                        List<String> categoriesList = (List<String>) documentSnapshot.get("categoriesList");
                        showCategoryAdapter = new ShowCategoryAdapter( ( ArrayList < String > ) categoriesList );
                        binding.APCategoryRV.setAdapter( showCategoryAdapter );
                        binding.APCategoryRV.setLayoutManager( new LinearLayoutManager(getBaseContext(),
                                LinearLayoutManager.HORIZONTAL, false));


                        expectedWorkDuration= documentSnapshot.getString( "expectedWorkDuration" );
                        binding.APTvJobTime.setText( expectedWorkDuration );

                        projectedBudget= documentSnapshot.getString( "projectedBudget" );
                        binding.APtvJopPrice.setText( projectedBudget );

                        jobLocation= documentSnapshot.getString( "jobLocation" );
                        binding.APTvJobLoc.setText( jobLocation );
//TODO GET Timestamp
                        binding.progressBar.setVisibility( View.GONE );
                        binding.SV.setVisibility( View.VISIBLE );

                    } else {
                        Toast.makeText( this , "Prop" , Toast.LENGTH_SHORT ).show( );                    }
                } )
                .addOnFailureListener( e -> {
                    Log.e( "GetingPost",e.getMessage().toString() );
                } );




    }

void Rating(){
    RatingBar ratingBar = evaluationDialog.findViewById(R.id.ratingBar);
    EditText commentEditText = evaluationDialog.findViewById(R.id.et_comment);
    Button sendButton = evaluationDialog.findViewById(R.id.sendButton);

    sendButton.setOnClickListener(v -> {
        rating = ratingBar.getRating();
        comment = commentEditText.getText().toString();

        Map<String, Object> updates1 = new HashMap<>();
        updates1.put("Comment-worker", comment);
        updates1.put("Rating-worker", (int) rating);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", new Locale("ar"));
            String currentDate = dateFormat.format(new Date());
            updates1.put("jobFinishDate", currentDate);
        } else {
            // For older devices
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            DateFormatSymbols arabicDFS = new DateFormatSymbols(new Locale("ar"));
            String[] arabicMonthNames = arabicDFS.getMonths();
            String monthInArabic = arabicMonthNames[month - 1];
            String date = String.format(Locale.getDefault(), "%d %s %d", day, monthInArabic, year);
            updates1.put("jobFinishDate", date);
        }
        updates1.put("jobState", "done");

        documentReference.update(updates1).addOnSuccessListener(aVoid -> {
            // Handle the case when the update is successful
        }).addOnFailureListener(e -> {
            // Handle the case when the update fails
        });

        evaluationDialog.dismiss();
    });

}
    void DeleteJob( ){

        // Set the click listeners for the delete and cancel buttons
        Button deleteButton = deleteDialogView.findViewById(R.id.deleteButton);

        deleteButton.setOnClickListener( v -> {
            // TODO: delete the work and change its state to closed & delete job

            closeProject();

            deleteDialog.dismiss();
        } );

        Button cancelButton = deleteDialogView.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener( v -> deleteDialog.dismiss() );
    }
    void openProject(){
        binding.APbtnCloseProject.setVisibility( View.VISIBLE );
        binding.APbtnComments.setVisibility( View.VISIBLE );

        DeleteJob( );

        binding.APbtnComments.setOnClickListener( view ->
                startActivity( new Intent( PostActivity2.this, OffersActivity.class) ) );

        binding.APbtnCloseProject.setOnClickListener( view -> {
            // Show the delete deluge
            deleteDialog.show();
        } );


    }

    void closeProject(){
        binding.PAtvProjectstate.setText( "مغلق" );
        binding.PAtvProjectstate.setBackgroundColor( getResources().getColor( R.color.red ) );
        binding.APbtnCloseProject.setVisibility( View.GONE );
        Map <String, Object> updates = new HashMap <>();
        updates.put("jobState", "close");
        documentReference.update(updates).addOnSuccessListener( aVoid -> {
            // Handle the case when the update is successful
        } ).addOnFailureListener( e -> {
            // Handle the case when the update fails
        } );
    }

}