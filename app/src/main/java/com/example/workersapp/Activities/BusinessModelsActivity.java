package com.example.workersapp.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.example.workersapp.R;
import com.example.workersapp.Utilities.categoryAdapter;
import com.example.workersapp.databinding.ActivityBusinessModelsBinding;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class BusinessModelsActivity extends AppCompatActivity {
    ActivityBusinessModelsBinding binding;

    ArrayList<String> categoryArrayList;

    categoryAdapter categoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBusinessModelsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        categoryArrayList= new ArrayList<>();

        categoryArrayList.add("أثاث خشبي");
        categoryArrayList.add(" خشبي");
        categoryArrayList.add("أثاث خشبي");
        categoryArrayList.add("أثاث خشبي");
        categoryArrayList.add("أثاث خشبي");
        categoryArrayList.add("أثاث خشبي");
        categoryArrayList.add("أثاث خشبي");
        categoryArrayList.add("أثاث ");
        categoryArrayList.add(" خشبي");
        categoryArrayList.add("أثاث خشبي");
        categoryArrayList.add("أثاث خشبي");
        categoryArrayList.add("أثاث خشبي");
        categoryArrayList.add("أثاث خشبي");
        categoryArrayList.add("أثاث ");
        categoryArrayList.add("أثاث خشبي");

        categoryAdapter = new categoryAdapter(categoryArrayList);
        binding.businessRv.setAdapter(categoryAdapter);
        binding.businessRv.setLayoutManager( new LinearLayoutManager(getBaseContext(),
                RecyclerView.HORIZONTAL, false));
    }
}