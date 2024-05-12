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

import androidx.core.app.NotificationCompat;

import com.example.budgetbuddy.R;
import com.example.budgetbuddy.NotificationActivity;

public class ReminderReceiver extends BroadcastReceiver {
    private static final String TAG = "ReminderReceiver";
    private static final int NOTIFICATION_ID = 123;
    private static final String CHANNEL_ID = "reminder_channel";
    private static final CharSequence CHANNEL_NAME = "Reminder Channel";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Reminder received.");

        if (intent.getAction() != null && intent.getAction().equals("com.example.budgetbuddy.ACTION_DELETE_REMINDER")) {
            Log.d(TAG, "Reminder deleted, skipping notification.");
            return;
        }

        String reminderMessage = intent.getStringExtra("REMINDER_MESSAGE");
        if (reminderMessage == null) {
            Log.e(TAG, "Reminder message is null.");
            return;
        }

        // Check if the trigger time is null
        long triggerTime = intent.getLongExtra("REMINDER_TRIGGER_TIME", 0);
        if (triggerTime == 0) {
            Log.e(TAG, "Reminder trigger time is null.");
            return;
        }

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.notifications_icon)
                .setContentTitle("Reminder")
                .setContentText(reminderMessage)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        Intent notificationIntent = new Intent(context, NotificationActivity.class);
        notificationIntent.putExtra("data", reminderMessage);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                NOTIFICATION_ID,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        builder.setContentIntent(pendingIntent);

        // Set sound for the notification
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(defaultSoundUri);

        // Create the notification channel (required for Android Oreo and above)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            } else {
                Log.e(TAG, "NotificationManager is null.");
            }
        }

        // Display the notification
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        } else {
            Log.e(TAG, "NotificationManager is null.");
        }
    }
}