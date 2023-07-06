package com.example.workersapp.Activities;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.workersapp.Utilities.MyJobService;
import com.example.workersapp.databinding.ActivitySplashBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {
    private static final int SPLASH_TIMEOUT = 3000;
    public static SharedPreferences sp;
    FirebaseUser currentUser;
    ActivitySplashBinding binding;
    public static JobScheduler jobScheduler;
    FirebaseAuth auth;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        binding = ActivitySplashBinding.inflate( getLayoutInflater( ) );
        setContentView( binding.getRoot( ) );
        jobService();

        sp = getSharedPreferences( "MyPreferencesBoarding" , MODE_PRIVATE );

        auth = FirebaseAuth.getInstance( );
        currentUser = auth.getCurrentUser( );


        new Handler( ).postDelayed( ( ) -> {
            Intent intent;
            boolean app = sp.getBoolean( "appUp-lode" , false );
            if ( app == false ) {
                intent = new Intent( SplashActivity.this , OnboardingActivity.class );
                startActivity( intent );

            } else if ( currentUser != null ) {
                intent = new Intent( SplashActivity.this , LoginActivity.class );
                startActivity( intent );
            } else {
                intent = new Intent( SplashActivity.this , GuestActivity.class );
                startActivity( intent );
            }

            finish( );
        } , SPLASH_TIMEOUT );
    }
    public void jobService() {
        jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        ComponentName componentName = new ComponentName(getBaseContext(), MyJobService.class);
        JobInfo jobInfo = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            jobInfo = new JobInfo.Builder(1, componentName)
                    .setPeriodic(24 * 60 * 60 * 1000, JobInfo.getMinFlexMillis())
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .build();
        }
        jobScheduler.schedule(jobInfo);
    }
}
