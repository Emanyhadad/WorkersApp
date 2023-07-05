package com.example.workersapp.Activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.interfaces.ItemChangeListener;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.workersapp.Adapters.ShowCategoryAdapter;
import com.example.workersapp.R;
import com.example.workersapp.databinding.ActivityDetailsModelsBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
    String doc;

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

        firebaseFirestore.collection("users")
                .document(Objects.requireNonNull(firebaseUser.getPhoneNumber()))
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

                } );

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
        binding.inculd.editIcon.setVisibility( View.VISIBLE );
       binding.inculd.tvPageTitle.setText( "نماذج الأعمال" );
        

    }

    @Override
    protected void onResume() {
        super.onResume();

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
}