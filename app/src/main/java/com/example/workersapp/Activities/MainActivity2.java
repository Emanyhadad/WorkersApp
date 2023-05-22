package com.example.workersapp.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.workersapp.R;
import com.example.workersapp.databinding.ActivityMain2Binding;

public class MainActivity2 extends AppCompatActivity {
ActivityMain2Binding binding;
    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        binding=ActivityMain2Binding.inflate( getLayoutInflater() );
        setContentView( binding.getRoot() );
    }
}