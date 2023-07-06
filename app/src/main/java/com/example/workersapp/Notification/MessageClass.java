package com.example.workersapp.Notification;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MessageClass extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
    }

    //    private static final String CHANNEL_ID = "your_notification_channel_id";
//
//    @Override
//    public void onMessageReceived(RemoteMessage remoteMessage) {
//        if (remoteMessage.getNotification() != null) {
//            String title = remoteMessage.getNotification().getTitle();
//            String body = remoteMessage.getNotification().getBody();
//
//            showNotification(title, body);
//        }
//    }
//
//    private void showNotification(String title, String body) {
//        Intent destinationIntent = new Intent(this, PostActivity_forWorker.class);
//        destinationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, destinationIntent, 0);
//
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
//                .setSmallIcon(R.drawable.ic_calendar)
//                .setContentTitle(title)
//                .setContentText(body)
//                .setContentIntent(pendingIntent)
//                .setAutoCancel(true);
//
//        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
//        notificationManager.notify(0, builder.build());
//    }
//
//
//    @Override
//    public void onNewToken(String token) {
//        // يتم استدعاء هذه الدالة عند توليد أو تحديث رمز الجهاز (توكن)
//        // قم بتنفيذ الإجراءات المطلوبة مثل تحديث رمز الجهاز في قاعدة البيانات
//        // أو إرساله إلى الخادم الخاص بك للاستخدام المستقبلي
//
//        sendDeviceTokenToServer(token);
//    }
//
//
//    private void sendDeviceTokenToServer(String token) {
//        // قم بإنشاء Map لإرسال بيانات رمز الجهاز إلى دالة Firebase Cloud Function
//        Map<String, Object> data = new HashMap<>();
//        data.put("token", token);
//
//        // استدعاء دالة Firebase Cloud Function باستخدام Cloud Functions API
//        FirebaseFunctions.getInstance()
//                .getHttpsCallable("updateDeviceToken")
//                .call(data)
//                .addOnSuccessListener(new OnSuccessListener<HttpsCallableResult>() {
//                    @Override
//                    public void onSuccess(HttpsCallableResult httpsCallableResult) {
//                        // تم تحديث رمز الجهاز بنجاح
//                        Toast.makeText(MessageClass.this, "success notification", Toast.LENGTH_SHORT).show();
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        // حدث خطأ في تحديث رمز الجهاز
//                    }
//                });
//    }

}



