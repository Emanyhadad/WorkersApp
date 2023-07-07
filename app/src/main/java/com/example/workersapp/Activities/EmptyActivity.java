package com.example.workersapp.Activities;

import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;

import com.example.workersapp.R;
import com.example.workersapp.databinding.ActivityEmptyBinding;

public class EmptyActivity extends AppCompatActivity {
    ActivityEmptyBinding binding;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEmptyBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_empty);


    }
}