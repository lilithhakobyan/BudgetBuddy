package com.example.budgetbuddy;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.example.budgetbuddy.reminder.AddReminderFragment;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {

        Log.d(TAG, "FCM message received");

        // Log notification title and body if available
        if (remoteMessage.getNotification() != null) {
            String notificationTitle = remoteMessage.getNotification().getTitle();
            String notificationBody = remoteMessage.getNotification().getBody();
            Log.d(TAG, "Notification Title: " + notificationTitle);
            Log.d(TAG, "Notification Body: " + notificationBody);
        }

        // Log data payload if available
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Data Payload:");
            for (Map.Entry<String, String> entry : remoteMessage.getData().entrySet()) {
                Log.d(TAG, "Key: " + entry.getKey() + ", Value: " + entry.getValue());
            }
        }

        if (remoteMessage.getNotification() != null) {
            String notificationTitle = remoteMessage.getNotification().getTitle();
            String notificationBody = remoteMessage.getNotification().getBody();
            long timeInMillis = parseTimeFromNotification(notificationTitle, notificationBody);
            scheduleLocalNotification(timeInMillis);
        }
    }

    private long parseTimeFromNotification(String title, String body) {
        // Sample format: "dd/MM/yyyy HH:mm"
        String dateTimeFormat = "dd/MM/yyyy HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(dateTimeFormat, Locale.getDefault());
        try {
            Date date = sdf.parse(body); // Assuming the time is in the body of the notification
            if (date != null) {
                return date.getTime();
            } else {
                Log.e(TAG, "Failed to parse date");
                return 0;
            }
        } catch (ParseException e) {
            Log.e(TAG, "Error parsing date: " + e.getMessage());
            return 0;
        }
    }


    private void scheduleLocalNotification(long timeInMillis) {
        Activity currentActivity = getCurrentActivity();

        if (currentActivity instanceof FragmentActivity) {
            FragmentManager fragmentManager = ((FragmentActivity) currentActivity).getSupportFragmentManager();
            Fragment fragment = fragmentManager.findFragmentByTag("ADD_REMINDER_FRAGMENT_TAG");

            if (fragment instanceof AddReminderFragment) {
                ((AddReminderFragment) fragment).scheduleLocalNotification(timeInMillis);
            } else {
                Log.e(TAG, "AddReminderFragment not found");
            }
        } else {
            Log.e(TAG, "Current activity is not an instance of FragmentActivity");
        }
    }


    private Activity getCurrentActivity() {
        MyApplication application = (MyApplication) getApplicationContext();
        return application.getCurrentActivity();
    }

}
