package com.example.workersapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.workersapp.Adapters.ShowCategoryAdapter;
import com.example.workersapp.Adapters.ShowImagesAdapter;
import com.example.workersapp.R;
import com.example.workersapp.Utilities.Offer;
import com.example.workersapp.databinding.ActivityPostForWorkerBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@SuppressLint( "Registered" )
public class PostActivity_forWorker extends AppCompatActivity {
    String offerDes,offerPrice,offerDuration;
    ShowCategoryAdapter showCategoryAdapter;
    ShowImagesAdapter showImagesAdapter;
    String jobState,title,description,expectedWorkDuration,projectedBudget,jobLocation,projectState;

    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    String clintID,postId,path,duration,budget;
    int formsCount;
    private Dialog evaluationDialog;
    float rating;
    String comment;
    DocumentReference documentReference;


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


        binding.inculd.editIcon.setVisibility( View.GONE );
        binding.inculd.tvPageTitle.setText( "بيانات الوظيفة" );
        //GetPost
        clintID = getIntent().getStringExtra("OwnerId").trim();
        postId = getIntent().getStringExtra("PostId").trim();

        //Get userForms Count
        firestore.collection("forms").document(user.getPhoneNumber()).collection("userForm").get()
                .addOnSuccessListener(runnable -> formsCount = runnable.size());

        path = "posts/" + clintID + "/userPost/" + postId;
        String offerId = user.getPhoneNumber()+">"+postId;

        //Get Post
        getPostData();

        //store Offer in Worker
        firestore.collection( "offers" ).document( postId ).collection( "workerOffers" ).document(user.getPhoneNumber()  ).get().addOnSuccessListener(
                documentSnapshot -> {
                    if ( documentSnapshot.exists() ){
                        binding.PB2.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), "You have already applied for a job", Toast.LENGTH_SHORT).show();
                        binding.tvWriteOffer.setText("العرض الخاص بك");

                        //Get Worker Data
                        GetUserData();
                        binding.tvSendOfferDuration.setText( documentSnapshot.getString( "offerDuration" ) );
                        binding.tvSendOfferDec.setText( documentSnapshot.getString( "offerDescription" ) );
                        binding.tvSendOfferPrice.setText( documentSnapshot.getString( "offerBudget" ) );

                    }                    else {
                        binding.LLWriteOffer.setVisibility(View.VISIBLE);

                        binding.btnSendOffer.setOnClickListener(view -> {
                            offerDes = binding.etOfferDes.getText().toString().trim();
                            offerPrice = binding.PASpOfferPrice.getText().toString().trim();
                            offerDuration = binding.PAspOfferDuration.getText().toString().trim();
                            Offer offer = new Offer(offerPrice, offerDuration, offerDes, user.getPhoneNumber(),clintID,postId);

                            new AlertDialog.Builder(PostActivity_forWorker.this)
                                    .setMessage("هل أنت متأكد أنك تريد تقديم عرضك؟")
                                    .setNegativeButton("لا، تعديل", (dialogInterface, i) -> {})
                                    .setPositiveButton("نعم، أرسل", (dialogInterface, i) -> {
                                        binding.PB2.setVisibility(View.VISIBLE);


                                        firestore.collection( "offers" ).document( postId )
                                                .collection( "workerOffers" ).document(user.getPhoneNumber()  ).set( offer );

                                        binding.LLWriteOffer.setVisibility(View.GONE);
                                        binding.tvWriteOffer.setText("العرض الخاص بك");
                                        binding.tvSendOfferDuration.setText(offerDuration);
                                        binding.tvSendOfferDec.setText(offerDes);
                                        binding.tvSendOfferPrice.setText(offerPrice);
                                        binding.tvCountWorks.setText(formsCount + "");

                                        GetUserData();

                                    })
                                    .create()
                                    .show();
                        } );
                    }

                }
        ).addOnFailureListener( e -> {} );}

        void GetUserData(){
        // Get user details and set them in the view
        firestore.collection("users").document(Objects.requireNonNull(user.getPhoneNumber()))
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

    }
    void getPostData(){
        //Get Post
        firestore.document(path).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        binding.progressBar.setVisibility(View.GONE);
                        binding.ScrollView.setVisibility(View.VISIBLE);
                        jobState = documentSnapshot.getString("jobState");
                        projectState = jobState;

                        title = documentSnapshot.getString("title");
                        binding.tvPostTitle.setText(title);

                        description = documentSnapshot.getString("description");
                        binding.tvPostDec.setText(description);

                        expectedWorkDuration = documentSnapshot.getString("expectedWorkDuration");
                        binding.tvJobTime.setText(expectedWorkDuration);

                        projectedBudget = documentSnapshot.getString("projectedBudget");
                        binding.tvPostPrice.setText(projectedBudget);

                        jobLocation = documentSnapshot.getString("jobLocation");
                        binding.tvPostLoc.setText(jobLocation);

                        //TODO GET Timestamp

                        List<String> categoriesList = (List<String>) documentSnapshot.get("categoriesList");
                        showCategoryAdapter = new ShowCategoryAdapter( ( ArrayList < String > ) categoriesList );
                        binding.CategoryRecycle.setAdapter( showCategoryAdapter );
                        binding.CategoryRecycle.setLayoutManager( new LinearLayoutManager(getBaseContext(),
                                LinearLayoutManager.HORIZONTAL, false));
                        List<String> images = (List<String>) documentSnapshot.get("images");

                        if (images == null || images.isEmpty()) {
                            binding.ImageRecycle.setVisibility(View.GONE);
                        } else {
                            showImagesAdapter = new ShowImagesAdapter(images, this);
                            binding.ImageRecycle.setAdapter(showImagesAdapter);
                            binding.ImageRecycle.setLayoutManager(new LinearLayoutManager(this,
                                    LinearLayoutManager.HORIZONTAL, false));
                        }}
                })
                .addOnFailureListener(e -> Log.e("getPot", "Error getting documents: ", e) );

    }
    void Rating(){
        documentReference = firestore.collection("posts").document( clintID).
                collection("userPost").document(postId);
        RatingBar ratingBar = evaluationDialog.findViewById(R.id.ratingBar);
        EditText commentEditText = evaluationDialog.findViewById(R.id.et_comment);
        Button sendButton = evaluationDialog.findViewById(R.id.sendButton);
        TextView title = evaluationDialog.findViewById( R.id.tv_title );
        title.setText( "تقيم العميل" );

        sendButton.setOnClickListener(v -> {
            rating = ratingBar.getRating();
            comment = commentEditText.getText().toString();

            Map <String, Object> updates1 = new HashMap <>();
            updates1.put("Comment-clint", comment);
            updates1.put("Rating-clint", (int) rating);
            updates1.put("jobState", "done");

            documentReference.update(updates1).addOnSuccessListener(aVoid -> {
                // Handle the case when the update is successful
            }).addOnFailureListener(e -> {
                // Handle the case when the update fails
            });

            evaluationDialog.dismiss();
        });

    }

}

