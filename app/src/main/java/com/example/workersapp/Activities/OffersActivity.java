package com.example.workersapp.Activities;

import static androidx.constraintlayout.widget.ConstraintLayoutStates.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.workersapp.Adapters.OffersAdapter;
import com.example.workersapp.Listeneres.OfferListener;
import com.example.workersapp.R;
import com.example.workersapp.Utilities.Offer;
import com.example.workersapp.databinding.ActivityOffersBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class OffersActivity extends AppCompatActivity {
ActivityOffersBinding binding;
    FirebaseFirestore firestore;
    List< Offer > offerList;
    private AlertDialog HireDialog;
    AlertDialog.Builder HireDialog_builder;
    View hireDialogView;

    OffersAdapter offersAdapter;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;

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
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        //TODO INTENT
        String clientId = user.getPhoneNumber();
        String postId = getIntent().getStringExtra( "postID" );

        Log.e( "task1postId",postId );

        CollectionReference workerOffersRef = firestore.collection("offers").document(postId).collection("workerOffers");

        workerOffersRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Offer offer;

                QuerySnapshot querySnapshot = task.getResult();
                for (QueryDocumentSnapshot documentSnapshot : querySnapshot) {
                    // get the document data
                    String documentId = documentSnapshot.getId();

                    String workerID = String.valueOf(documentSnapshot.get("workerID"));
                    String offerBudget = String.valueOf(documentSnapshot.get("offerBudget"));
                    String offerDuration = String.valueOf(documentSnapshot.get("offerDuration"));
                    String offerDescription = String.valueOf(documentSnapshot.get("offerDescription"));
                    String offerState = String.valueOf(documentSnapshot.get("OfferState"));
                    offer = new Offer(offerBudget, offerDuration, offerDescription, workerID,clientId,postId);
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

            } else {
                Log.d(TAG, "Error getting documents: ", task.getException());
            }
        });
        offersAdapter = new OffersAdapter(offerList, new OfferListener() {
            @Override
            public void onDelete(int pos) {
                Offer offer = offerList.get(pos);
                String offerId = offer.workerID; // assuming that you have an offer ID field in your Offer class
                new AlertDialog.Builder(OffersActivity.this)
                        .setMessage("هل أنت متأكد أنك تريد حذف العرض؟")
                        .setNegativeButton("لا, الغاء", (dialogInterface, i) -> {
                        })
                        .setPositiveButton("نعم ، حذف", (dialogInterface, i) -> {
                            Map<String, Object> updates = new HashMap<>();
                            updates.put("OfferState", "hide");
                            workerOffersRef.document(offerId)
                                    .update(updates)
                                    .addOnSuccessListener(aVoid -> {
                                        // Handle the case when the update is successful
                                        offerList.remove(pos);
                                        offersAdapter.notifyItemRemoved(pos);
                                        offersAdapter.notifyItemRangeChanged(pos, offerList.size());
                                        if (offerList.isEmpty()) {
                                            binding.ProgressBar.setVisibility(View.GONE);
                                            binding.RV.setVisibility(View.GONE);
                                            binding.LLEmpty.setVisibility(View.VISIBLE);
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










    }
    void Hiring(int pos){
        CollectionReference reference=   firestore.collection( "posts" ).document( user.getPhoneNumber() )
                .collection( "userPost" );
        new AlertDialog.Builder(OffersActivity.this)
                .setMessage("هل أنت متأكد أنك تريد قبول العرض؟")
                .setNegativeButton("لا, الغاء", (dialogInterface, i) -> {
                })
                .setPositiveButton("نعم ، قبول", (dialogInterface, i) -> {
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("workerId", offerList.get( pos ).getWorkerID());
                    updates.put( "jobState","inWork" );

// For devices with Android Nougat (API level 24) or higher
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", new Locale("ar"));
                        String currentDate = dateFormat.format(new Date());
                        updates.put("jobStartDate", currentDate);
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
                        updates.put("jobStartDate", date);
                    }


                    reference.document(offerList.get( pos ).getPostID())
                            .update(updates)
                            .addOnSuccessListener(aVoid -> {
                                // Handle the case when the update is successful
                                offerList.remove(pos);
                                offersAdapter.notifyItemRemoved(pos);
                                offersAdapter.notifyItemRangeChanged(pos, offerList.size());
                                if (offerList.isEmpty()) {
                                    binding.ProgressBar.setVisibility(View.GONE);
                                    binding.RV.setVisibility(View.GONE);
                                    binding.LLEmpty.setVisibility(View.VISIBLE);
                                }
                            })
                            .addOnFailureListener(e -> {
                                // Handle the case when the update fails
                            });
                }).create().show();

        }


}