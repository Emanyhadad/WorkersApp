package com.example.workersapp.Activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.workersapp.Adapters.ShowCategoryAdapter;
import com.example.workersapp.R;
import com.example.workersapp.databinding.ActivityDetailsModelsBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DetailsModelsActivity extends AppCompatActivity {
    ActivityDetailsModelsBinding binding;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    FirebaseStorage firebaseStorage;

    ArrayList<String> categoryArrayList;

    ShowCategoryAdapter categoryAdapter;

    List<String> imagesList;
    String doc,posWorker;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailsModelsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseFirestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        firebaseStorage = FirebaseStorage.getInstance();
        categoryArrayList = new ArrayList<>();
        imagesList = new ArrayList<>();

        Intent intent = getIntent();
        doc = intent.getStringExtra("documentId");
        posWorker = intent.getStringExtra("posWorker");

        getDetailsWorker();

        ActivityResultLauncher<Intent> arl1 = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Snackbar.make(binding.businessDate, "تم التعديل بنجاح", Snackbar.LENGTH_SHORT).show();
                    }
                });

        binding.inculd.editIcon.setOnClickListener( view -> {
            Intent intent1 = new Intent(getBaseContext(),EditModelActivity.class);
            intent1.putExtra("document",doc);
            arl1.launch(intent1);
        } );
        

    }

    private void getDetailsWorker(){
        if (posWorker != null && !posWorker.equals(firebaseUser.getPhoneNumber())){
            Toast.makeText(this, "worker", Toast.LENGTH_SHORT).show();
            binding.inculd.editIcon.setVisibility( View.GONE );
            binding.inculd.tvPageTitle.setText( "نماذج الأعمال" );
            Log.d("posWorker",Objects.requireNonNull(posWorker));
            firebaseFirestore.collection("users")
                    .document(Objects.requireNonNull(posWorker))
                    .get()
                    .addOnSuccessListener( documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String fullName = documentSnapshot.getString("fullName");
                            String image = documentSnapshot.getString("image");
                            binding.businessUserName.setText(fullName);
                            Glide.with(getBaseContext())
                                    .load(image)
                                    .circleCrop()
                                    .error(R.drawable.worker)
                                    .into(binding.businessImgUser);
                            binding.LLNoWifi.setVisibility( View.GONE );
                            binding.PB.setVisibility( View.GONE );
                            binding.LLData.setVisibility( View.VISIBLE );
                        }
                    } )
                    .addOnFailureListener( e -> {
                        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService( Context.CONNECTIVITY_SERVICE);
                        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
                        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

                        if (!isConnected) {
                            binding.LLNoWifi.setVisibility(View.VISIBLE);
                            binding.PB.setVisibility(View.GONE);
                            binding.LLData.setVisibility(View.GONE);
                        }

                    });

            imagesList.clear();
            categoryArrayList.clear();

            firebaseFirestore.collection("forms")
                    .document(posWorker)
                    .collection("userForm")
                    .document(doc)
                    .get()
                    .addOnSuccessListener( documentSnapshot -> {
                        ArrayList<SlideModel> slideModels = new ArrayList<>();
                        List<String> images = (List<String>) documentSnapshot.get("images");
                        for (String imageUrl : images) {
                            imagesList.add(imageUrl);
                            slideModels.add(new SlideModel(imageUrl, null));
                        }
                        binding.imageSlider.setImageList(slideModels, ScaleTypes.CENTER_CROP);
                        binding.businessNumImg.setText((1) + "/" + (imagesList.size()));

                        binding.imageSlider.setItemChangeListener( i -> binding.businessNumImg.setText((i + 1) + "/" + (imagesList.size())) );

                        List<String> categories = (List<String>) documentSnapshot.get("categoriesList");

                        for (String categoryList:categories){
                            categoryArrayList.add(categoryList);
                            categoryAdapter = new ShowCategoryAdapter(categoryArrayList);
                            binding.businessRv.setAdapter(categoryAdapter);
                            binding.businessRv.setLayoutManager(new LinearLayoutManager(getBaseContext(),
                                    RecyclerView.HORIZONTAL, false));
                        }

                        String description = (String) documentSnapshot.get("description");
                        binding.businessDetails.setText(description);

                        String date = (String) documentSnapshot.get("date");
                        binding.businessDate.setText(date);

                        binding.LLNoWifi.setVisibility( View.GONE );
                        binding.PB.setVisibility( View.GONE );
                        binding.LLData.setVisibility( View.VISIBLE );
                    } ).addOnFailureListener( runnable -> {binding.LLNoWifi.setVisibility( View.VISIBLE );
                        binding.PB.setVisibility( View.GONE );
                        binding.LLData.setVisibility( View.GONE );} );

            List<Long> RatingWorkerList = new ArrayList<>();
            firebaseFirestore.collection("users").get().addOnSuccessListener(queryDocumentSnapshots -> {
                for (DocumentSnapshot documentSnapshot1 : queryDocumentSnapshots) {
                    firebaseFirestore.collection("posts").document(documentSnapshot1.getId())
                            .collection("userPost").whereEqualTo("jobState", "done")
                            .whereEqualTo("workerId", firebaseUser.getPhoneNumber()).get()
                            .addOnCompleteListener(task -> {
                                for (DocumentSnapshot document : task.getResult()) {
                                    RatingWorkerList.add(document.getLong("Rating-worker"));

                                    Log.d("tag", document.getId());

                                    long sum = 0;
                                    for (Long value : RatingWorkerList) {
                                        sum += value;
                                    }
                                    if (RatingWorkerList.size() != 0) {
                                        int x = (int) (sum / RatingWorkerList.size());
                                        binding.businessRate.setText(x + "");
                                        Log.d("tag", String.valueOf(x));
                                    } else {
                                        binding.businessRate.setText("0");
                                    }
                                }
                            });
                }
            });

        }
        else if (posWorker == null ) {
            binding.inculd.editIcon.setVisibility( View.VISIBLE );
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (posWorker == null ) {
            firebaseFirestore.collection("users")
                    .document(firebaseUser.getPhoneNumber())
                    .get()
                    .addOnSuccessListener( documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String fullName = documentSnapshot.getString("fullName");
                            String image = documentSnapshot.getString("image");
                            binding.businessUserName.setText(fullName);
                            Glide.with(getBaseContext())
                                    .load(image)
                                    .circleCrop()
                                    .error(R.drawable.worker)
                                    .into(binding.businessImgUser);
                            binding.LLNoWifi.setVisibility( View.GONE );
                            binding.PB.setVisibility( View.GONE );
                            binding.LLData.setVisibility( View.VISIBLE );
                        }
                    } )
                    .addOnFailureListener( e -> {
                        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService( Context.CONNECTIVITY_SERVICE);
                        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
                        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

                        if (!isConnected) {
                            binding.LLNoWifi.setVisibility(View.VISIBLE);
                            binding.PB.setVisibility(View.GONE);
                            binding.LLData.setVisibility(View.GONE);
                        }

                    });

            imagesList.clear();
            categoryArrayList.clear();

            firebaseFirestore.collection("forms")
                    .document(firebaseUser.getPhoneNumber())
                    .collection("userForm")
                    .document(doc)
                    .get()
                    .addOnSuccessListener( documentSnapshot -> {
                        ArrayList<SlideModel> slideModels = new ArrayList<>();
                        List<String> images = (List<String>) documentSnapshot.get("images");
                        for (String imageUrl : images) {
                            imagesList.add(imageUrl);
                            slideModels.add(new SlideModel(imageUrl, null));
                        }
                        binding.imageSlider.setImageList(slideModels, ScaleTypes.CENTER_CROP);
                        binding.businessNumImg.setText((1) + "/" + (imagesList.size()));

                        binding.imageSlider.setItemChangeListener( i -> binding.businessNumImg.setText((i + 1) + "/" + (imagesList.size())) );

                        List<String> categories = (List<String>) documentSnapshot.get("categoriesList");

                        for (String categoryList:categories){
                            categoryArrayList.add(categoryList);
                            categoryAdapter = new ShowCategoryAdapter(categoryArrayList);
                            binding.businessRv.setAdapter(categoryAdapter);
                            binding.businessRv.setLayoutManager(new LinearLayoutManager(getBaseContext(),
                                    RecyclerView.HORIZONTAL, false));
                        }

                        String description = (String) documentSnapshot.get("description");
                        binding.businessDetails.setText(description);

                        String date = (String) documentSnapshot.get("date");
                        binding.businessDate.setText(date);

                        binding.LLNoWifi.setVisibility( View.GONE );
                        binding.PB.setVisibility( View.GONE );
                        binding.LLData.setVisibility( View.VISIBLE );
                    } ).addOnFailureListener( runnable -> {binding.LLNoWifi.setVisibility( View.VISIBLE );
                        binding.PB.setVisibility( View.GONE );
                        binding.LLData.setVisibility( View.GONE );} );
        }
        else if (posWorker != null && !posWorker.equals(firebaseUser.getPhoneNumber())){
            Toast.makeText(this, "not", Toast.LENGTH_SHORT).show();
        }
    }
}