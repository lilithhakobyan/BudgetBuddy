package com.example.budgetbuddy;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import androidx.core.app.NotificationCompat;

public class NotificationHelper {
    private static final String CHANNEL_ID = "Your_Channel_ID";
    private static final String CHANNEL_NAME = "Your_Channel_Name";
    private static final String CHANNEL_DESCRIPTION = "Your_Channel_Description";
    private static final int NOTIFICATION_ID = 101;

    public static void showNotification(Context context, String title, String message) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(CHANNEL_DESCRIPTION);
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.enableVibration(true);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.notifications_icon)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}

