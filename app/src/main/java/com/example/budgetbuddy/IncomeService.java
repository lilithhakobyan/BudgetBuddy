package com.example.budgetbuddy;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class IncomeService extends Service {
    public IncomeService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // This method is called when the service is started
        // Handle the task of saving income here

        // Stop the service after the task is complete
        stopSelf();

        return START_NOT_STICKY;
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
