package com.example.workersapp.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.example.workersapp.R;
import com.example.workersapp.databinding.ActivitySplashBinding;

public class SplashActivity extends AppCompatActivity {
    private static final int SPLASH_TIMEOUT = 3000;
    public static SharedPreferences sp;

    ActivitySplashBinding binding;
    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        binding=ActivitySplashBinding.inflate( getLayoutInflater() );
        setContentView( binding.getRoot() );
        sp = getSharedPreferences("shared", MODE_PRIVATE);


        new Handler().postDelayed( ( ) -> {
            Intent intent;
            boolean app=sp.getBoolean( "appUp-lode",false );
            if ( app==false ){ intent = new Intent(SplashActivity.this, OnboardingActivity.class); }

            else  {intent = new Intent(SplashActivity.this, LoginActivity.class);}

            startActivity(intent);
            finish();
        } , SPLASH_TIMEOUT);
    }
    }
