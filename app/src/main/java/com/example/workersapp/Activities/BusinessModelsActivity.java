package com.example.workersapp.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.workersapp.R;
import com.example.workersapp.databinding.ActivityBusinessModelsBinding;

public class BusinessModelsActivity extends AppCompatActivity {
    ActivityBusinessModelsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBusinessModelsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}