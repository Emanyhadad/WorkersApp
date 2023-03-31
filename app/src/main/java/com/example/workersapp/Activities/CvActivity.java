package com.example.workersapp.Activities;

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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CvActivity extends AppCompatActivity {
    ActivityCvBinding binding;
    FirebaseFirestore db;
    ArrayAdapter<CharSequence> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCvBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();

        adapter = ArrayAdapter.createFromResource(
                getBaseContext(),
                R.array.work,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.CvWork.setAdapter(adapter);

        binding.CvNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
                String work = binding.CvWork.getText().toString();
                String cv = binding.Cv.getText().toString();


                if (!work.isEmpty()){
                    Map<String, Object> data = new HashMap<>();
                    data.put("work", work);
                    data.put("cv", cv);

                    db.collection("person")
                            .document()
                            .set(data)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(CvActivity.this, "success add city and title", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(Exception e) {

                                }
                            });
                }else {
                    binding.CvWork.setError("يرجى تعبئة هذا الحقل");
                }


            }
        });
    }
}