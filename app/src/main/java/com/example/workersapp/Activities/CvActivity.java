package com.example.workersapp.Activities;

import android.content.Intent;
import android.os.Bundle;

import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;

import com.example.workersapp.R;
import com.example.workersapp.databinding.ActivityCvBinding;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CvActivity extends AppCompatActivity {
    ActivityCvBinding binding;
    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    ArrayList<String> list;
    List<String> categoriesListF = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCvBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        list = new ArrayList<>();

        binding.inculd.tvPageTitle.setText(getString( R.string.ProfileToolBar ));

        fetchData();
        binding.CvNext.setOnClickListener( view -> {
            String work = binding.CvWork.getText().toString();
            String cv = binding.Cv.getText().toString();

            if (!work.isEmpty() && !cv.isEmpty()) {
                Map<String, Object> data = new HashMap<>();
                data.put("work", work);
                data.put("cv", cv);

                Task < Void > voidTask = db.collection( "users" ).document( Objects.requireNonNull( firebaseUser.getPhoneNumber( ) ) )
                        .update( data )
                        .addOnSuccessListener(unused -> {
                        });
                Intent intent = new Intent(getBaseContext(),WorkerActivities.class);
                startActivity(intent);
                finish();
            } else {
                binding.CvWork.setError("يرجى تعبئة هذا الحقل");
            }

        } );
    }

    public void fetchData() {
        db.collection("category").document("category")
                .get()
                .addOnCompleteListener( task -> {
                    if (task.isSuccessful()) {
                        categoriesListF = (List<String>) task.getResult().get("categories");
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, categoriesListF);
                        binding.CvWork.setAdapter(adapter);
                    } else {}
                } );
    }
}
