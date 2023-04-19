package com.example.workersapp.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.workersapp.R;
import com.example.workersapp.databinding.ActivityProfileWorkerBinding;

public class ProfileWorker extends AppCompatActivity {
    ActivityProfileWorkerBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileWorkerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}