package com.example.workersapp.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.interfaces.ItemChangeListener;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.workersapp.Adapters.categoryAdapter;
import com.example.workersapp.R;
import com.example.workersapp.databinding.ActivityDetailsModelsBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

    categoryAdapter categoryAdapter;

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

        firebaseFirestore.collection("users").document(Objects.requireNonNull(firebaseUser.getPhoneNumber()))
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String fullName = documentSnapshot.getString("fullName");
                            String image = documentSnapshot.getString("image");
                            binding.businessUserName.setText(fullName);
                            Glide.with(getBaseContext())
                                    .load(image)
                                    .circleCrop()
                                    .error(R.drawable.worker)
                                    .into(binding.businessImgUser);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(DetailsModelsActivity.this, "Error retrieving data", Toast.LENGTH_SHORT).show();
                    }
                });

        binding.businessImgEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(getBaseContext(),EditModelActivity.class);
                intent1.putExtra("document",doc);
                startActivity(intent1);
            }
        });

//
//                .get()
//                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                    @Override
//                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                        ArrayList<SlideModel> slideModels = new ArrayList<>();
//                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
//                            List<String> images = (List<String>) document.get("images");
//                            if (!images.isEmpty()) {
//                                for (String imageUrl : images) {
//                                    imagesList.add(imageUrl);
//                                    // imageUrl : بتجيب كل الصور الموجودة في كل الديكيومنت
//                                    Toast.makeText(DetailsModelsActivity.this, "imageUrl: "+imageUrl, Toast.LENGTH_SHORT).show();
//                                    slideModels.add(new SlideModel(imageUrl,null));
////                                    adapter = new SliderImgAdapter(imagesList,getBaseContext());
////                                    binding.businessRvImg.setAdapter(adapter);
////                                    binding.businessRvImg.setLayoutManager(new LinearLayoutManager(getBaseContext(),
////                                            RecyclerView.HORIZONTAL, false));
////                                    binding.businessRvImg.setAdapter(adapter);
////                                    adapter.notifyDataSetChanged();
//                                }
//                            }
//                            else {
//                                Toast.makeText(getBaseContext(), "wait", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                        binding.imageSlider.setImageList(slideModels);
//
//                    }
//                });

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
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        ArrayList<SlideModel> slideModels = new ArrayList<>();
                        List<String> images = (List<String>) documentSnapshot.get("images");
                        for (String imageUrl : images) {
                            imagesList.add(imageUrl);
                            slideModels.add(new SlideModel(imageUrl, null));
                        }
                        binding.imageSlider.setImageList(slideModels, ScaleTypes.CENTER_CROP);
                        binding.businessNumImg.setText((1) + "/" + (imagesList.size()));

                        binding.imageSlider.setItemChangeListener(new ItemChangeListener() {
                            @Override
                            public void onItemChanged(int i) {
                                binding.businessNumImg.setText((i + 1) + "/" + (imagesList.size()));
                            }
                        });

                        List<String> categories = (List<String>) documentSnapshot.get("categoriesList");

                        for (String categoryList:categories){
                            categoryArrayList.add(categoryList);
                            categoryAdapter = new categoryAdapter(categoryArrayList);
                            binding.businessRv.setAdapter(categoryAdapter);
                            binding.businessRv.setLayoutManager(new LinearLayoutManager(getBaseContext(),
                                    RecyclerView.HORIZONTAL, false));
                        }

                        String description = (String) documentSnapshot.get("description");
                        binding.businessDetails.setText(description);

                        String date = (String) documentSnapshot.get("date");
                        binding.businessDate.setText(date);
                    }
                });

    }
}