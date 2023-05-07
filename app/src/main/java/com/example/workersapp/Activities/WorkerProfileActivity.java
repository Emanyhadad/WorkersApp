package com.example.workersapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.workersapp.Adapters.ImageModelFragAdapter;
import com.example.workersapp.Fragments.BlankFragment;
import com.example.workersapp.Fragments.BusinessModelsFragment;
import com.example.workersapp.R;
import com.example.workersapp.databinding.ActivityWorkerProfileBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class WorkerProfileActivity extends AppCompatActivity {
    ActivityWorkerProfileBinding binding;
    FirebaseFirestore db;
    FirebaseStorage storage;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    
    List<String> imagesList;

    ImageModelFragAdapter adapter;
    String documentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWorkerProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        db.collection("users").document(Objects.requireNonNull(firebaseUser.getPhoneNumber()))
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String fullName = documentSnapshot.getString("fullName");
                            String nickName = documentSnapshot.getString("nickName");
                            String work = documentSnapshot.getString("work");
                            String cv = documentSnapshot.getString("cv");
                            String city = documentSnapshot.getString("city");
                            String image = documentSnapshot.getString("image");
                            binding.pWorkerUserName.setText(fullName);
                            binding.pWorkerNickName.setText("( " + nickName + " )");
                            binding.pWorkerJobName.setText(work);
                            binding.pWorkerCv.setText(cv);
                            binding.pWorkerLocation.setText(city);
                            binding.pWorkerPhone.setText(firebaseUser.getPhoneNumber());
                            Glide.with(getBaseContext())
                                    .load(image)
                                    .circleCrop()
                                    .error(R.drawable.worker)
                                    .into(binding.pWorkerImg);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(WorkerProfileActivity.this, "Error retrieving data", Toast.LENGTH_SHORT).show();
                    }
                });
        imagesList = new ArrayList<>();
        ArrayList<Fragment> fragments = new ArrayList<>();
        ArrayList<String> tabs = new ArrayList<>();
        tabs.add("آراء العملاء");
        tabs.add("نماذج الأعمال");

        fragments.add(BlankFragment.newInstance("Audi"));
        fragments.add(new BusinessModelsFragment());

        adapter = new ImageModelFragAdapter(this, fragments);
        binding.FragPager.setAdapter(adapter);

        new TabLayoutMediator(binding.FragTab, binding.FragPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                if (position < tabs.size()) {
                    tab.setText(tabs.get(position));
                }
            }
        }).attach();
        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), NewModelActivity.class);
                startActivity(intent);
            }
        });
    }
}