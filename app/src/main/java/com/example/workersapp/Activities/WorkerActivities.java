package com.example.workersapp.Activities;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.workersapp.Fragments.WorkerInProgressFragment;
import com.example.workersapp.Fragments.WorkerSubmittedJobFragment;
import com.example.workersapp.Fragments.PostFragment_inWorker;
import com.example.workersapp.Fragments.WorkerProfileFragment;
import com.example.workersapp.R;
import com.example.workersapp.databinding.ActivityWorkerActivitiesBinding;

public class WorkerActivities extends AppCompatActivity {

    ActivityWorkerActivitiesBinding binding ;

    @SuppressLint( "NonConstantResourceId" )
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWorkerActivitiesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportFragmentManager().beginTransaction().replace(R.id.frame , new WorkerProfileFragment()).commit();
        int selectedColor = getResources().getColor(R.color.blue);

        binding.bottomNav.setOnItemSelectedListener( item -> {
            switch(item.getItemId()){
                case R.id.jobs:
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame , PostFragment_inWorker.newInstance( ) ).commit();
                    break;
                case R.id.Presenter:
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame , new WorkerSubmittedJobFragment()).commit();
                    break;
                case R.id.myJobs:
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame , new WorkerInProgressFragment()).commit();
                    break;
                case R.id.profile:
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame , new WorkerProfileFragment()).commit();
                    break;
            }
            return true;
        } );
    }
}