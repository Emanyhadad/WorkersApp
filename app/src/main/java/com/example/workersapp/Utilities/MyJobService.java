package com.example.workersapp.Utilities;

import android.app.job.JobParameters;
import android.app.job.JobService;

import com.example.workersapp.Activities.SplashActivity;

public class MyJobService extends JobService {

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        NotificationUtils.displayNotification(this);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        SplashActivity.jobScheduler.cancel(1);
        return true;
    }
}
