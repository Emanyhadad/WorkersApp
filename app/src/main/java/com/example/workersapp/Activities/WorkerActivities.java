package com.example.workersapp.Activities;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.workersapp.Fragments.BlankFragment2;
import com.example.workersapp.Fragments.BlankFragment3;
import com.example.workersapp.Fragments.BlankFragment4;
import com.example.workersapp.Fragments.WorkerProfileFragment;
import com.example.workersapp.R;
import com.example.workersapp.databinding.ActivityWorkerActivitiesBinding;
import com.google.android.material.navigation.NavigationBarView;

public class WorkerActivities extends AppCompatActivity {

    ActivityWorkerActivitiesBinding binding ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWorkerActivitiesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportFragmentManager().beginTransaction().replace(R.id.frame , new WorkerProfileFragment()).commit();
        int selectedColor = getResources().getColor(R.color.blue);

        binding.bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.profile:
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame , new WorkerProfileFragment()).commit();
                        break;
                    case R.id.myJobs:
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame , new BlankFragment2()).commit();
                        break;
                    case R.id.Presenter:
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame , new BlankFragment3()).commit();
                        break;
                    case R.id.jobs:
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame , new BlankFragment4()).commit();
                        break;
                }
                return true;
            }
        });

    }
}