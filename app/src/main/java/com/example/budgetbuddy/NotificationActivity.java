package com.example.budgetbuddy;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class NotificationActivity extends AppCompatActivity {
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        textView = findViewById(R.id.textViewData);

        // Retrieve the data using the correct key
        String data = getIntent().getStringExtra("REMINDER_MESSAGE");
        textView.setText(data);


    }
}
