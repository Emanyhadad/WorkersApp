package com.example.workersapp.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.example.workersapp.Utilities.Offer;
import com.example.workersapp.databinding.ActivityOffersBinding;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class OffersActivity extends AppCompatActivity {
ActivityOffersBinding binding;
    FirebaseFirestore firestore;
    List< Offer > offerList;
    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        binding = ActivityOffersBinding.inflate( getLayoutInflater() );
        setContentView( binding.getRoot() );
        offerList=new ArrayList <>();
         firestore = FirebaseFirestore.getInstance();
         //TODO INTENT
        String clientId = "+970595964511";
        String postId = "b2nYHmf9PCV4NhRWd0la2Wf9lqk11683192375017";

        CollectionReference offersRef = firestore.collection("posts").document(clientId)
                .collection("userPost").document(postId)
                .collection("offers");

        offersRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Offer offer;
                List < DocumentSnapshot > offersList = task.getResult().getDocuments();
                for (DocumentSnapshot document : task.getResult()) {
                    String WorkerID = String.valueOf( document.get( "WorkerID" ) );
                    String offerBudget = String.valueOf( document.get( "offerBudget" ) );
                    String offerDuration = String.valueOf( document.get( "offerDuration" ) );
                    String offerDescription = String.valueOf( document.get( "offerDescription" ) );
                    offer=new Offer( offerBudget,offerDuration,offerDescription,WorkerID );
                    offerList.add( offer );

                }
                // Use the offersList as needed
            } else {
                Log.d("TAG", "Error getting offers: ", task.getException());
            }
        });



    }
}