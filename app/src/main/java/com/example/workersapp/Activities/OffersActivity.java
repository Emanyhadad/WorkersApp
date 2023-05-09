package com.example.workersapp.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.bumptech.glide.Glide;
import com.example.workersapp.Adapters.OffersAdapter;
import com.example.workersapp.Listeneres.OfferListener;
import com.example.workersapp.R;
import com.example.workersapp.Utilities.Offer;
import com.example.workersapp.databinding.ActivityOffersBinding;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class OffersActivity extends AppCompatActivity {
ActivityOffersBinding binding;
    FirebaseFirestore firestore;
    List< Offer > offerList;
    private AlertDialog HireDialog;
    AlertDialog.Builder HireDialog_builder;
    View hireDialogView;

    OffersAdapter offersAdapter;


    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        binding = ActivityOffersBinding.inflate( getLayoutInflater() );
        setContentView( binding.getRoot() );
        binding.ProgressBar.setVisibility( View.VISIBLE );
        offerList=new ArrayList <>();
        //Hire Dialog
        HireDialog_builder = new AlertDialog.Builder(this);
        hireDialogView = getLayoutInflater().inflate(R.layout.deluge_hireing, null);
        HireDialog_builder.setView(hireDialogView);
        HireDialog = HireDialog_builder.create();




         firestore = FirebaseFirestore.getInstance();
         //TODO INTENT
        String clientId = "+970595964511";
        String postId = "b2nYHmf9PCV4NhRWd0la2Wf9lqk11683485597267";

        CollectionReference offersRef = firestore.collection("posts").document(clientId)
                .collection("userPost").document(postId)
                .collection("Offers");

        offersRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                Offer offer;
                List<DocumentSnapshot> offersList = task.getResult().getDocuments();
                for (DocumentSnapshot document : task.getResult()) {
                    String workerID = String.valueOf(document.get("WorkerID"));
                    String offerBudget = String.valueOf(document.get("offerBudget"));
                    String offerDuration = String.valueOf(document.get("offerDuration"));
                    String offerDescription = String.valueOf(document.get("offerDescription"));
                    String offerState = String.valueOf(document.get("OfferState"));
                    offer = new Offer(offerBudget, offerDuration, offerDescription, workerID);
                    if (!offerState.equals("hide") || offerState.equals(null)) {
                        offerList.add(offer);
                    }
                    else {
                        binding.ProgressBar.setVisibility( View.GONE );
                        binding.LLEmpty.setVisibility( View.VISIBLE );
                    }
                }

                if ( task.getResult().getDocuments().isEmpty() ){
                    binding.ProgressBar.setVisibility( View.GONE );
                    binding.LLEmpty.setVisibility( View.VISIBLE );
                } else if ( !offerList.isEmpty() ) {
                    binding.ProgressBar.setVisibility( View.GONE );
                    binding.RV.setVisibility( View.VISIBLE );
                }
                offersAdapter = new OffersAdapter(offerList, new OfferListener() {
                    @Override
                    public void onDelete(int pos) {
                        new AlertDialog.Builder(OffersActivity.this)
                                .setMessage("هل أنت متأكد أنك تريد حذف العرض؟")
                                .setNegativeButton("لا, الغاء", (dialogInterface, i) -> {
                                })
                                .setPositiveButton("نعم ، حذف", (dialogInterface, i) -> {
                                    Map<String, Object> updates = new HashMap<>();
                                    updates.put("OfferState", "hide");
                                    offersRef.document(offerList.get( pos ).workerID)
                                            .update(updates)
                                            .addOnSuccessListener(aVoid -> {
                                                // Handle the case when the update is successful
                                                offerList.remove(pos);
                                                offersAdapter.notifyItemRemoved(pos);
                                                offersAdapter.notifyItemRangeChanged(pos, offerList.size());
                                                if ( offerList.isEmpty() ) {
                                                    binding.ProgressBar.setVisibility( View.GONE );
                                                    binding.RV.setVisibility( View.GONE );

                                                    binding.LLEmpty.setVisibility( View.VISIBLE );
                                                }
                                            })
                                            .addOnFailureListener(e -> {
                                                // Handle the case when the update fails
                                            });
                                }).create().show();
                    }

                    @Override
                    public void onHire(int pos) {
                        Hiring(pos);
                    }
                }, getApplicationContext());
                binding.RV.setAdapter(offersAdapter);
                binding.RV.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false));

            } else {
                Log.d("TAG", "Error getting offers: ", task.getException());
            }
        });



    }
    void Hiring(int pos){

            // Set the click listeners for the delete and cancel buttons
            Button deleteButton = hireDialogView.findViewById( R.id.deleteButton);

            deleteButton.setOnClickListener( v -> {
                // TODO: delete the work and change its state to closed & delete job


                HireDialog.dismiss();
            } );

            Button cancelButton = hireDialogView.findViewById(R.id.cancelButton);
            cancelButton.setOnClickListener( v -> HireDialog.dismiss() );
        }


}