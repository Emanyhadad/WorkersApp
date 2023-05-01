package com.example.workersapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.example.workersapp.R;
import com.example.workersapp.databinding.ActivityProfileWorkerBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

public class ProfileWorker extends AppCompatActivity {
    ActivityProfileWorkerBinding binding;
    FirebaseFirestore db;
    FirebaseStorage storage;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    String workerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileWorkerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        if (LoginActivity.sp.getString("accountType", "").equals("worker")) {
            workerId = getIntent().getStringExtra("workerId");

            Toast.makeText(this, "workerId: " + workerId, Toast.LENGTH_SHORT).show();

            db.collection("user").document("worker").collection(firebaseUser.getUid())
                    .document(workerId)
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
                                binding.pWorkerUserName.setText(fullName);
                                binding.pWorkerNickName.setText("( "+nickName+" )");
                                binding.pWorkerJobName.setText(work);
                                binding.pWorkerCv.setText(cv);
                                binding.pWorkerLocation.setText(city);
//                            assert name != null;
//                                Toast.makeText(ProfileWorker.this, "name is : "+name, Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ProfileWorker.this, "Error retrieving data", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "not worker", Toast.LENGTH_SHORT).show();
        }
    }
}