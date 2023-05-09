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



        binding.PAtvPostTitle.setText( title );
        binding.PAtvPostDec.setText( description );
        //TODO GET Timestamp
        binding.PAtvPostPrice.setText( projectedBudget );
        binding.PAtvPostLoc.setText( jobLocation );
        binding.PAtvJobTime.setText( expectedWorkDuration );

        categoriesList=new ArrayList <>(  );
        showCategoryAdapter = new ShowCategoryAdapter( ( ArrayList < String > ) categoriesList );
        binding.PACategoryRecycle.setAdapter( showCategoryAdapter );
        binding.PACategoryRecycle.setLayoutManager( new LinearLayoutManager(getBaseContext(),
                LinearLayoutManager.HORIZONTAL, false));

        imageList=new ArrayList<>();
        //TODO: GET IMAGE LIST FROM FIREBASE
        if ( imageList.size() == 0 ){binding.PAImageRecycle.setVisibility( View.GONE );}
        showImagesAdapter = new ShowImagesAdapter(images, this);
        binding.PAImageRecycle.setAdapter(showImagesAdapter);
        binding.PAImageRecycle.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false));



        String projectState = "open";

        switch ( projectState ){

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


    void inWorkProject(){
        binding.PAtvProjectstate.setText( "قيد العمل" );
        binding.PAtvProjectstate.setBackgroundResource(R.color.yellow);
//        binding.LLInWork.setVisibility( View.VISIBLE );

        Map <String, Object> updates = new HashMap <>();
        updates.put("title", "inWork");
        documentReference.update(updates).addOnSuccessListener( aVoid -> {
            // Handle the case when the update is successful
        } ).addOnFailureListener( e -> {
            // Handle the case when the update fails
        } );

        Rating();

//        binding.btnFinishProject.setOnClickListener( view -> {
//            // Show the evaluation dialog
//            evaluationDialog.show();
//            doneProject();
//        } );

    }
    void doneProject(){
        binding.PAtvProjectstate.setText( "منتهي" );
        binding.PAtvProjectstate.setBackgroundResource(R.color.blue);
//        binding.PAtvDjData.setVisibility( View.VISIBLE );
//        binding.PACLDoneWork.setVisibility( View.VISIBLE );
//        binding.LLInWork.setVisibility( View.GONE );

        Map <String, Object> updates = new HashMap <>();
        updates.put("title", "done");
        documentReference.update(updates).addOnSuccessListener( aVoid -> {
            // Handle the case when the update is successful
        } ).addOnFailureListener( e -> {
            // Handle the case when the update fails
        } );

    }
    void closeProject(){
        binding.PAtvProjectstate.setText( "مغلق" );
        binding.PAtvProjectstate.setBackgroundResource(R.color.red);

        Map <String, Object> updates = new HashMap <>();
        updates.put("title", "close");
        documentReference.update(updates).addOnSuccessListener( aVoid -> {
            // Handle the case when the update is successful
        } ).addOnFailureListener( e -> {
            // Handle the case when the update fails
        } );
    }


}