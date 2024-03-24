package com.example.budgetbuddy;

import android.util.Log;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Check if the message contains data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            // Handle data message
            // Here you can extract data from the message and take appropriate actions
            // For example, you can update UI, show notification, or perform other tasks based on the message content
        }

        // Check if the message contains notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message notification body: " + remoteMessage.getNotification().getBody());

            // Handle notification message
            // Here you can display the notification to the user, launch an activity, etc.
        }
    }
}
