package com.example.budgetbuddy.reminder;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.example.budgetbuddy.NotificationActivity;
import com.example.budgetbuddy.R;

public class ReminderReceiver extends BroadcastReceiver {
    private static final String TAG = "ReminderReceiver";
    private static final int NOTIFICATION_ID = 123;
    private static final String CHANNEL_ID = "reminder_channel";
    private static final CharSequence CHANNEL_NAME = "Reminder Channel";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Reminder received.");

        String message = intent.getStringExtra("REMINDER_MESSAGE");
        if (message != null) {
            Log.d(TAG, "Received message: " + message);

            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

            Intent notificationIntent = new Intent(context, NotificationActivity.class);
            notificationIntent.putExtra("data", message);

            PendingIntent pendingIntent = PendingIntent.getActivity(
                    context,
                    NOTIFICATION_ID,
                    notificationIntent,
                    PendingIntent.FLAG_IMMUTABLE
            );

            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.notifications_icon)
                    .setContentTitle("Reminder")
                    .setContentText(message)
                    .setContentIntent(pendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri);

            // Create the notification channel for Android Oreo and higher
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(
                        CHANNEL_ID,
                        CHANNEL_NAME,
                        NotificationManager.IMPORTANCE_DEFAULT
                );
                NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
                if (notificationManager != null) {
                    notificationManager.createNotificationChannel(channel);
                } else {
                    Log.e(TAG, "NotificationManager is null.");
                }
            }

            // Show the notification
            NotificationManager notificationManager = ContextCompat.getSystemService(
                    context,
                    NotificationManager.class
            );
            if (notificationManager != null) {
                notificationManager.notify(NOTIFICATION_ID, builder.build());
            } else {
                Log.e(TAG, "NotificationManager is null.");
            }
        } else {
            Log.e(TAG, "Message is null.");
        }
    }
}