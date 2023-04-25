package com.example.workersapp.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.workersapp.Fragments.NewJobFragment;
import com.example.workersapp.R;
import com.example.workersapp.databinding.ActivityWorkOwnerProfileBinding;

public class WorkOwnerProfileActivity extends AppCompatActivity {
ActivityWorkOwnerProfileBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWorkOwnerProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportFragmentManager().beginTransaction().add(R.id.container,new NewJobFragment()).commit();

    }
}