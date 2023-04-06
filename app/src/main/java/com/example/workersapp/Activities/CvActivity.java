package com.example.workersapp.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.workersapp.R;
import com.example.workersapp.databinding.ActivityCvBinding;
import com.example.workersapp.databinding.ActivityMapsBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CvActivity extends AppCompatActivity {
    ActivityCvBinding binding;
    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    ArrayAdapter<String> adapter;
    ArrayList<String> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCvBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        list = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, list);
        binding.CvWork.setAdapter(adapter);

        binding.CvNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                insertData();

                String work = binding.CvWork.getText().toString();
                String cv = binding.Cv.getText().toString();

                if (!work.isEmpty()) {
                    Map<String, Object> data = new HashMap<>();
                    data.put("work", work);
                    data.put("cv", cv);

                    db.collection("user").document("worker").collection(firebaseUser.getUid())
                            .add(data)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Toast.makeText(CvActivity.this, "success add city and title", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });
                } else {
                    binding.CvWork.setError("يرجى تعبئة هذا الحقل");
                }

            }
        });
    }
    
    public void insertData(){

        Map<String, Object> category = new HashMap<>();
        category.put("category1", "نجار");
        category.put("category2", "دهان");
        category.put("category3", "بناء");
        category.put("category4", "حداد");
        category.put("category6", "بليط");
        category.put("category7", "قصارة");
        category.put("category8", "سباك");
        category.put("category9", "كهربائي");

        // Save the cities to Firestore
        db.collection("categories")
                .document()
                .collection(firebaseUser.getUid())
                .add(category)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
        list.clear();
        fetchData();
        adapter.notifyDataSetChanged();
        Toast.makeText(this, "Inserted successfully", Toast.LENGTH_SHORT).show();
    }

    public void fetchData() {
        db.collection("categories")
                .document()
                .collection(firebaseUser.getUid())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        for (DocumentChange dc : value.getDocumentChanges()) {
                            if (dc.getType() == DocumentChange.Type.ADDED) {
                                list.add(dc.getDocument().toString());
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
    }
}
