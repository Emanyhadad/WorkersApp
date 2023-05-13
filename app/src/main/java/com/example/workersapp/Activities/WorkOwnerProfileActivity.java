package com.example.workersapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.workersapp.Fragments.OwnerInProgressFragment1;
import com.example.workersapp.Fragments.NewJobFragment;
import com.example.workersapp.Fragments.OwnerProfileFragment;
import com.example.workersapp.Fragments.PostsFragment;
import com.example.workersapp.R;
import com.example.workersapp.databinding.ActivityWorkOwnerProfileBinding;
import com.google.android.material.navigation.NavigationBarView;

public class WorkOwnerProfileActivity extends AppCompatActivity {
    ActivityWorkOwnerProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWorkOwnerProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportFragmentManager().beginTransaction().replace(R.id.container, new OwnerProfileFragment()).commit();
        NavigationBarView.OnItemSelectedListener listener = new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.itProfile:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, new OwnerProfileFragment()).commit();
                        return true;
                    case R.id.itInProgress:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, new OwnerInProgressFragment1()).commit();
                        return true;
                    case R.id.itOpenJobs:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, new PostsFragment()).commit();
                        return true;
                    case R.id.itNewJob:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, new NewJobFragment()).commit();
                        return true;
                    default:
                        return false;
                }
            }
        };
        // تنشيط itProfile
        binding.workOwnerNav.getMenu().findItem(R.id.itProfile).setChecked(true);
        binding.workOwnerNav.getMenu().performIdentifierAction(R.id.itProfile, 0);
        binding.workOwnerNav.setOnItemSelectedListener(listener);
    }
}