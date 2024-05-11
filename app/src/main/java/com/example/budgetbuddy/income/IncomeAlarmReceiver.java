package com.example.budgetbuddy.income;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.budgetbuddy.IncomeService;

import java.util.Calendar;

public class IncomeAlarmReceiver extends BroadcastReceiver {

    public static final int NOTIFICATION_ID = 124;
    public static final String CHANNEL_ID = "income_notifications";


    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if ("continue_action".equals(action)) {
            // Continue with adding income
            // You can start a Service to perform this operation
            Intent serviceIntent = new Intent(context, IncomeService.class);
            context.startService(serviceIntent);
        } else if ("cancel_action".equals(action)) {
            // Cancel adding income
            // You can handle this action as needed
        }

        // Get current time
        Calendar currentTime = Calendar.getInstance();

        // Get next month
        Calendar nextMonth = Calendar.getInstance();
        nextMonth.add(Calendar.MONTH, 1);

        // Preserve the day of the month and time of the day
        nextMonth.set(Calendar.DAY_OF_MONTH, currentTime.get(Calendar.DAY_OF_MONTH));
        nextMonth.set(Calendar.HOUR_OF_DAY, currentTime.get(Calendar.HOUR_OF_DAY));
        nextMonth.set(Calendar.MINUTE, currentTime.get(Calendar.MINUTE));
        nextMonth.set(Calendar.SECOND, currentTime.get(Calendar.SECOND));
        nextMonth.set(Calendar.MILLISECOND, currentTime.get(Calendar.MILLISECOND));

        // Here you can trigger any action you want for the income alarm
        // For example, you can send a notification or start a service to handle income addition
    }
}