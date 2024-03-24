package com.example.budgetbuddy;

import android.app.Notification;
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

public class ReminderReceiver extends BroadcastReceiver {
    private static final String TAG = "ReminderReceiver";
    private static final int NOTIFICATION_ID = 123; // Arbitrary notification ID
    private static final String CHANNEL_ID = "reminder_channel"; // Notification channel ID
    private static final CharSequence CHANNEL_NAME = "Reminder Channel"; // Notification channel name

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Reminder received.");

        String message = intent.getStringExtra("REMINDER_MESSAGE"); // Corrected key
        if (message != null) {
            Log.d(TAG, "Received message: " + message);

            // Show a toast with the received message
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

            // Create an explicit intent for the NotificationActivity
            Intent notificationIntent = new Intent(context, NotificationActivity.class);
            notificationIntent.putExtra("data", message);

            // Create a PendingIntent with mutability flag
            PendingIntent pendingIntent = PendingIntent.getActivity(
                    context,
                    NOTIFICATION_ID,
                    notificationIntent,
                    PendingIntent.FLAG_IMMUTABLE // or PendingIntent.FLAG_MUTABLE
            );

            // Set sound for the notification
            // Specify the URI of the custom sound file
            // Get the URI of the default notification sound
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.notifications_icon)
                    .setContentTitle("Reminder")
                    .setContentText(message)
                    .setContentIntent(pendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri);



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
