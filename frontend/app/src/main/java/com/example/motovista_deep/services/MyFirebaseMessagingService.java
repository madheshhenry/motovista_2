package com.example.motovista_deep.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.motovista_deep.CustomerHomeActivity;
import com.example.motovista_deep.R;
import com.example.motovista_deep.utils.FcmTokenManager;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    private static final String CHANNEL_ID = "high_priority_notifications";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        // Check if notifications are enabled by user
        if (!com.example.motovista_deep.helpers.SharedPrefManager.getInstance(getApplicationContext()).areNotificationsEnabled()) {
            Log.d(TAG, "Notifications disabled by user. Suppressing.");
            return;
        }

        // Handle data payload
        if (remoteMessage.getData().size() > 0) {
            String title = remoteMessage.getData().get("title");
            String message = remoteMessage.getData().get("message");
            String screen = remoteMessage.getData().get("screen");
            String id = remoteMessage.getData().get("id");
            sendNotification(title, message, screen, id);
        }

        // Handle notification payload
        if (remoteMessage.getNotification() != null) {
            sendNotification(remoteMessage.getNotification().getTitle(), 
                             remoteMessage.getNotification().getBody(), 
                             null, null);
        }
    }

    @Override
    public void onNewToken(@NonNull String token) {
        Log.d(TAG, "Refreshed token: " + token);
        // Send token to server
        FcmTokenManager.sendTokenToServer(getApplicationContext(), token);
    }

    private void sendNotification(String title, String messageBody, String screen, String id) {
        Intent intent = new Intent(this, com.example.motovista_deep.SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        
        if (screen != null) {
            intent.putExtra("screen", screen);
        }
        if (id != null) {
            intent.putExtra("id", id);
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_notifications)
                        .setContentTitle(title)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "High Priority Notifications",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0, notificationBuilder.build());
    }
}
