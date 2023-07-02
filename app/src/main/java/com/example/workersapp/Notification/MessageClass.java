package com.example.workersapp.Notification;

import android.Manifest;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.workersapp.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MessageClass extends FirebaseMessagingService {

    private static final String CHANNEL_ID = "channel_id";
    private static final int NOTIFICATION_ID = 1;

    private static final String TAG = "MessageClass";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // استخراج المعلومات من الإشعار المستلم
        String comment = remoteMessage.getData().get("comment");

        Log.d("commentMessage",comment);

        // استخدام المعلومات لعرض إشعار للعامل
        showNotification(comment);
    }

    private void showNotification(String comment) {
        // قم ببناء الإشعار باستخدام NotificationCompat.Builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.delete_image)
                .setContentTitle("تم تقييمك")
                .setContentText(comment)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        // عرض الإشعار
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.VIBRATE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.WAKE_LOCK) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.FOREGROUND_SERVICE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Request the missing permissions, or handle the case where the permissions are not granted.
            return;
        }
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}



