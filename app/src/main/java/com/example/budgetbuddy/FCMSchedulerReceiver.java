package com.example.budgetbuddy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class FCMSchedulerReceiver extends BroadcastReceiver {
    private static final String TAG = "FCMSchedulerReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String reminderMessage = intent.getStringExtra("REMINDER_MESSAGE");
        // Handle the FCM message here
        Log.d(TAG, "Received FCM message: " + reminderMessage);
    }
}
