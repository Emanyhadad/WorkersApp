package com.example.workersapp.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.workersapp.Adapters.categoryAdapter;
import com.example.workersapp.databinding.ActivityDetailsModelsBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

public class DetailsModelsActivity extends AppCompatActivity{
    ActivityDetailsModelsBinding binding;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    FirebaseStorage firebaseStorage;

    ArrayList<String> categoryArrayList;

    categoryAdapter categoryAdapter;

    List<String> imageArrayList;
    List<String> imagesList;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailsModelsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

//        sp = getSharedPreferences("details", MODE_PRIVATE);
//        editor = sp.edit();

        firebaseFirestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        firebaseStorage = FirebaseStorage.getInstance();
        categoryArrayList = new ArrayList<>();
        imageArrayList = new ArrayList<>();
        imagesList = new ArrayList<>();

        //TODO : تخزين مصفوفة الفئات
        categoryArrayList.add("أثاث خشبي");
        categoryArrayList.add(" خشبي");
        categoryArrayList.add("أثاث خشبي");
        categoryArrayList.add("أثاث خشبي");
        categoryArrayList.add("أثاث خشبي");
        categoryArrayList.add("أثاث خشبي");
        categoryArrayList.add("أثاث خشبي");
        categoryArrayList.add("أثاث ");
        categoryArrayList.add(" خشبي");
        categoryArrayList.add("أثاث خشبي");
        categoryArrayList.add("أثاث خشبي");
        categoryArrayList.add("أثاث خشبي");
        categoryArrayList.add("أثاث خشبي");
        categoryArrayList.add("أثاث ");
        categoryArrayList.add("أثاث خشبي");

        categoryAdapter = new categoryAdapter(categoryArrayList);
        binding.businessRv.setAdapter(categoryAdapter);
        binding.businessRv.setLayoutManager(new LinearLayoutManager(getBaseContext(),
                RecyclerView.HORIZONTAL, false));


        Intent intent = getIntent();
        int position = intent.getIntExtra("pos", 0);
        String documentId = intent.getStringExtra("documentId");

        firebaseFirestore.collection("forms").document(firebaseUser.getPhoneNumber())
                .collection("userForm")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            List<String> images = (List<String>) document.get("images");
//                            Toast.makeText(DetailsModelsActivity.this, ""+document.getId(), Toast.LENGTH_SHORT).show();
                            if (!images.isEmpty()) {
                                for (String imageUrl : images) {
                                    imagesList.add(imageUrl);
                                }
                            }
                            else {
                                Toast.makeText(getBaseContext(), "wait", Toast.LENGTH_SHORT).show();
                            }
//                            for (String imageUrl : images) {
////                                Picasso.get().load(imageUrl).into(imageView);
//                                Toast.makeText(getContext(), "imageUrl: "+imageUrl, Toast.LENGTH_SHORT).show();
//                                Log.d("imageUrlBusniessModels:",imageUrl);
////                                Toast.makeText(getContext(), "imageDone", Toast.LENGTH_SHORT).show();
//                            }
                        }
                    }
                });

    }

}