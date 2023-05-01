package com.example.workersapp.Activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.workersapp.Adapters.ShowCategoryAdapter;
import com.example.workersapp.Adapters.ShowImagesAdapter;
import com.example.workersapp.R;
import com.example.workersapp.databinding.ActivityPostBinding;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostActivity extends AppCompatActivity {
    ShowCategoryAdapter showCategoryAdapter;

    ShowImagesAdapter showImagesAdapter;
    List < Uri > imageList;
    private Dialog evaluationDialog;

    private AlertDialog deleteDialog;
    AlertDialog.Builder deleteDialog_builder;
    View deleteDialogView;
    ActivityPostBinding binding;

    FirebaseFirestore firestore;
    DocumentReference documentReference;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    String userId,postId,path;


    String jobState,title,description,expectedWorkDuration,projectedBudget,jobLocation;
    List<String> images,categoriesList;
    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        binding=ActivityPostBinding.inflate( getLayoutInflater() );
        setContentView( binding.getRoot() );



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

         userId = "+970595560706";
         postId = "y6MM0PPBhyTlzUJ4VaXCjEuII6m11682882808221"; //TODO GET FROM INTENT

         path = "posts/" + userId + "/userPost/" + postId ;
//         path = "posts/" + user+ "/userPost/" + postId ;

        firestore.document(path).get()
                .addOnSuccessListener( documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                         jobState = documentSnapshot.getString("jobState");
                         title = documentSnapshot.getString("title");
                         description= documentSnapshot.getString( "description" );
                        List<String> images = (List<String>) documentSnapshot.get("images");
                        List<String> categoriesList = (List<String>) documentSnapshot.get("categoriesList");
                         expectedWorkDuration= documentSnapshot.getString( "description" );
                         projectedBudget= documentSnapshot.getString( "projectedBudget" );
                         jobLocation= documentSnapshot.getString( "jobLocation" );
                         //TODO
                        Timestamp timestamp = documentSnapshot.getTimestamp("timestamp");

                    } else {
                        // Handle the case when the document doesn't exist
                    }
                } )
                .addOnFailureListener( e -> {
                    // Handle the error
                } );

         documentReference = firestore.collection("posts").document( userId ).
                collection("userPost").document(postId);



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

        imageList=new ArrayList<>();
        //TODO: GET IMAGE LIST FROM FIREBASE
        if ( imageList.size() == 0 ){binding.ImageRecycle.setVisibility( View.GONE );}
        showImagesAdapter = new ShowImagesAdapter(images, this);
        binding.ImageRecycle.setAdapter(showImagesAdapter);
        binding.ImageRecycle.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false));



        String projectState = jobState;

        switch ( projectState ){
            case "open":
                openProject();
                break;
            case "inWork":
                inWorkProject();
                break;
            case "done":
                doneProject();
                break;
            case "close":
                closeProject();
                break;
        }

    }

    void Rating(){
        RatingBar ratingBar = evaluationDialog.findViewById(R.id.ratingBar);
        EditText commentEditText = evaluationDialog.findViewById(R.id.et_comment);
        Button sendButton = evaluationDialog.findViewById(R.id.sendButton);

        sendButton.setOnClickListener( v -> {
            float rating = ratingBar.getRating();
            String comment = commentEditText.getText().toString();

            // TODO: store the evaluation data in the job description

            evaluationDialog.dismiss();
        } );
    }

    void DeleteJob( ){

        // Set the click listeners for the delete and cancel buttons
        Button deleteButton = deleteDialogView.findViewById(R.id.deleteButton);

        deleteButton.setOnClickListener( v -> {
            // TODO: delete the work and change its state to closed & delete job
            binding.btnCloseProject.setVisibility( View.GONE );
            binding.btnComment.setVisibility( View.GONE );
            closeProject();

            deleteDialog.dismiss();
        } );

        Button cancelButton = deleteDialogView.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener( v -> deleteDialog.dismiss() );
    }
    void openProject(){
        binding.btnCloseProject.setVisibility( View.VISIBLE );
        binding.btnComment.setVisibility( View.VISIBLE );

        DeleteJob( );

        binding.btnComment.setOnClickListener( view ->
                startActivity( new Intent( PostActivity.this, OffersActivity.class) ) );

        binding.btnCloseProject.setOnClickListener( view -> {
            // Show the delete deluge
            deleteDialog.show();
        } );


        }

    void inWorkProject(){
        binding.tvProjectstate.setText( "قيد العمل" );
        binding.tvProjectstate.setBackgroundResource(R.color.yellow);
        binding.LLInWork.setVisibility( View.VISIBLE );

        Map <String, Object> updates = new HashMap <>();
        updates.put("title", "inWork");
        documentReference.update(updates).addOnSuccessListener( aVoid -> {
            // Handle the case when the update is successful
        } ).addOnFailureListener( e -> {
            // Handle the case when the update fails
        } );

        Rating();

        binding.btnFinishProject.setOnClickListener( view -> {
            // Show the evaluation dialog
            evaluationDialog.show();
            doneProject();
        } );

    }
    void doneProject(){
        binding.tvProjectstate.setText( "منتهي" );
        binding.tvProjectstate.setBackgroundResource(R.color.blue);
        binding.tvDjData.setVisibility( View.VISIBLE );
        binding.CLDoneWork.setVisibility( View.VISIBLE );
        binding.LLInWork.setVisibility( View.GONE );

        Map <String, Object> updates = new HashMap <>();
        updates.put("title", "done");
        documentReference.update(updates).addOnSuccessListener( aVoid -> {
            // Handle the case when the update is successful
        } ).addOnFailureListener( e -> {
            // Handle the case when the update fails
        } );

    }
    void closeProject(){
        binding.tvProjectstate.setText( "مغلق" );
        binding.tvProjectstate.setBackgroundResource(R.color.red);

        Map <String, Object> updates = new HashMap <>();
        updates.put("title", "close");
        documentReference.update(updates).addOnSuccessListener( aVoid -> {
            // Handle the case when the update is successful
        } ).addOnFailureListener( e -> {
            // Handle the case when the update fails
        } );
    }


}