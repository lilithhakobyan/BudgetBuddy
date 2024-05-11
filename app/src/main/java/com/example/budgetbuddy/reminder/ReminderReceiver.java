package com.example.budgetbuddy.reminder;

import android.app.AlarmManager;
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

import com.example.budgetbuddy.NotificationActivity;

public class ReminderReceiver extends BroadcastReceiver {
    private static final String TAG = "ReminderReceiver";
    private static final int NOTIFICATION_ID = 123;
    private static final String CHANNEL_ID = "reminder_channel";
    private static final CharSequence CHANNEL_NAME = "Reminder Channel";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Reminder received.");

        // Check if the intent action is for a reminder
        if (intent.getAction() != null && intent.getAction().equals("com.example.budgetbuddy.ACTION_DELETE_REMINDER")) {
            Log.d(TAG, "Reminder deleted, skipping notification.");
            return; // Skip notification if reminder is deleted
        }

        String message = intent.getStringExtra("REMINDER_MESSAGE");
        long triggerTime = intent.getLongExtra("REMINDER_TIME", 0); // Get the trigger time

        if (message != null && triggerTime != 0) {
            Log.d(TAG, "Received message: " + message);

            // Create an explicit intent for the notification activity
            Intent notificationIntent = new Intent(context, NotificationActivity.class);
            notificationIntent.putExtra("data", message);

            PendingIntent pendingIntent = PendingIntent.getActivity(
                    context,
                    NOTIFICATION_ID,
                    notificationIntent,
                    PendingIntent.FLAG_IMMUTABLE
            );

            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

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

            // Schedule the notification using AlarmManager
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null) {
                alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
            } else {
                Log.e(TAG, "AlarmManager is null.");
            }
        } else {
            Log.e(TAG, "Message or trigger time is null.");
        }
    }
}
