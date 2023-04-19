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

        insertData();
        binding.CvNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String work = binding.CvWork.getText().toString();
                String cv = binding.Cv.getText().toString();

                if (!work.isEmpty()) {
                    Map<String, Object> data = new HashMap<>();
                    data.put("work", work);
                    data.put("cv", cv);

                    String workerId = getIntent().getStringExtra("workerId");

                    db.collection("user").document("worker").collection(firebaseUser.getUid())
                            .document(workerId)
                            .update(data)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(CvActivity.this, "success add Cv and work", Toast.LENGTH_SHORT).show();

                                }
                            });
                } else {
                    binding.CvWork.setError("يرجى تعبئة هذا الحقل");
                }

            }
        });
    }
    
    public void insertData(){
        db.collection("category")
                .document("dipdTXxKmbylQIJJhC0v")
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // تحميل البيانات وإضافتها إلى ArrayList
                            ArrayList<String> category = new ArrayList<>();
                            category.add(documentSnapshot.getString("category1"));
                            category.add(documentSnapshot.getString("category2"));
                            category.add(documentSnapshot.getString("category3"));
                            category.add(documentSnapshot.getString("category4"));
                            category.add(documentSnapshot.getString("category5"));
                            category.add(documentSnapshot.getString("category6"));
                            category.add(documentSnapshot.getString("category7"));
                            category.add(documentSnapshot.getString("category8"));
                            // وهكذا للحصول على بقية البيانات

                            // إنشاء ArrayAdapter وتعيينه كبيانات مصدر لـ Spinner
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.drop_down_item, category);
//                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            binding.CvWork.setAdapter(adapter);
                        } else {
                            Toast.makeText(CvActivity.this, "No such document", Toast.LENGTH_SHORT).show();
//                            Log.d(TAG, "No such document");
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CvActivity.this, "get failed with", Toast.LENGTH_SHORT).show();
//                        Log.d(TAG, "get failed with ", e);
                    }
                });
    }
}
