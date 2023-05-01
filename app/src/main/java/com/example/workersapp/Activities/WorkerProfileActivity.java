package com.example.workersapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.workersapp.R;
import com.example.workersapp.databinding.ActivityWorkerProfileBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.Objects;

public class WorkerProfileActivity extends AppCompatActivity {
    ActivityWorkerProfileBinding binding;
    FirebaseFirestore db;
    FirebaseStorage storage;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

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
                            Toast.makeText(WorkerProfileActivity.this, "phone: " + firebaseUser.getPhoneNumber(), Toast.LENGTH_SHORT).show();
                            Toast.makeText(WorkerProfileActivity.this, "image: " + image, Toast.LENGTH_SHORT).show();
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

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getBaseContext(),NewFormActivity.class));
            }
        });
    }
}