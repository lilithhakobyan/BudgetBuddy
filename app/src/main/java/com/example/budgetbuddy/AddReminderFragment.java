package com.example.budgetbuddy;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AddReminderFragment extends Fragment {
    private static final String TAG = "AddReminderFragment";

    private EditText describeReminderEditText, amountEditText;
    private TextView timeRemText;
    private Spinner choiceSpinner;
    private FirebaseFirestore db;
    private Button DateButton;
    private Button TimeButton;
    private TextView dateRemText;
    private FirebaseAuth auth;
    private long dateTime;

    private static final int ALARM_REQUEST_CODE = 123;
    private Context mContext;

    private static final String DATE_TIME_FORMAT = "dd/MM/yyyy HH:mm";
    private TimePicker timePicker;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_reminder, container, false);

        mContext = getContext();


        describeReminderEditText = view.findViewById(R.id.describeReminderEditText);
        timeRemText = view.findViewById(R.id.timeRemText);
        amountEditText = view.findViewById(R.id.AmountEditText);
        choiceSpinner = view.findViewById(R.id.choiceSpinner);
        DateButton = view.findViewById(R.id.date_button);
        dateRemText = view.findViewById(R.id.dateRemText);
        TimeButton = view.findViewById(R.id.time_button);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Set up spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.choices_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        choiceSpinner.setAdapter(adapter);

        // Set onClickListeners
        DateButton.setOnClickListener(v -> openDatePicker());
        TimeButton.setOnClickListener(v -> openTimePicker());
        choiceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Handle item selection if needed
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle case when nothing is selected if needed
            }
        });

        Button submitButton = view.findViewById(R.id.submit_btn);
        submitButton.setOnClickListener(v -> {
            long timestamp = saveReminderToFirestore(); // Save reminder and get timestamp
            if (timestamp != 0) {
                scheduleReminder(timestamp); // Schedule the reminder
                openReminderFragment();
            } else {
                // Handle error case where timestamp couldn't be generated
                Toast.makeText(requireContext(), "Error saving reminder", Toast.LENGTH_SHORT).show();
            }
        });

        ImageView imageView = view.findViewById(R.id.close_addrem);
        imageView.setOnClickListener(v -> showAlertDialog());


        return view;
    }

    private long saveReminderToFirestore() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            Toast.makeText(requireContext(), "User not authenticated. Please log in.", Toast.LENGTH_SHORT).show();
            return 0;
        }

        String reminderTitle = describeReminderEditText.getText().toString();
        if (reminderTitle.trim().isEmpty()) {
            Toast.makeText(requireContext(), "Reminder title is required", Toast.LENGTH_SHORT).show();
            return 0;
        }

        String dateText = dateRemText.getText().toString();
        String timeText = timeRemText.getText().toString();
        String amountText = amountEditText.getText().toString();
        String selectedChoice = choiceSpinner.getSelectedItem().toString();

        String dateTimeString = dateText + " " + timeText;
        long dateTime = convertStringToTimestamp(dateTimeString);

        boolean settled = false;

        // Create ReminderClass object
        ReminderClass newReminder = new ReminderClass(reminderTitle, dateTime, amountText, selectedChoice, settled);

        // Save to Firestore
        Map<String, Object> reminderData = new HashMap<>();
        reminderData.put("title", reminderTitle);
        reminderData.put("date", dateText);
        reminderData.put("time", timeText);
        reminderData.put("amount", amountText);
        reminderData.put("choice", selectedChoice);
        reminderData.put("dateTime", dateTime);
        reminderData.put("settled", settled);
        reminderData.put("userId", user.getUid());

        db.collection("reminders")
                .add(reminderData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Reminder added with ID: " + task.getResult().getId());

                        clearInputFields();
                    } else {
                        Log.w(TAG, "Error adding reminder", task.getException());
                    }
                });
        return dateTime;
    }

    // Method to clear input fields after submission
    private void clearInputFields() {
        describeReminderEditText.getText().clear();
        amountEditText.getText().clear();
        timeRemText.setText("");
        dateRemText.setText("");
    }

    // Method to show alert dialog before closing fragment
    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Cancel Reminder Confirmation")
                .setMessage("Are you sure you want to cancel?")
                .setPositiveButton("Yes, cancel", (dialog, which) -> openReminderFragment())
                .setNegativeButton("Continue", (dialog, which) -> dialog.dismiss());

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    // Method to navigate back to ReminderFragment
    private void openReminderFragment() {
        ReminderFragment fragmentB = new ReminderFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        Bundle args = new Bundle();
        args.putString("userId", auth.getCurrentUser().getUid());
        fragmentB.setArguments(args);

        transaction.replace(R.id.fragment_container, fragmentB);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    // Method to convert string date/time to timestamp
    private long convertStringToTimestamp(String dateTimeString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.UK);
        try {
            Date date = dateFormat.parse(dateTimeString);
            if (date != null) {
                return date.getTime();
            } else {
                // Handle case where parsed date is null
                Log.e(TAG, "Parsed date is null");
                return 0;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            Log.e(TAG, "Error parsing date/time: " + e.getMessage());
            Toast.makeText(requireContext(), "Error parsing date/time: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            return 0;
        }
    }

    private void openDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), R.style.DialogTheme, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;

                String formattedDay = (day < 10) ? "0" + day : String.valueOf(day);
                String formattedMonth = (month < 10) ? "0" + month : String.valueOf(month);

                dateRemText.setText(formattedDay + "/" + formattedMonth + "/" + year);
            }
        }, currentYear, currentMonth, currentDay);

        datePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int year = datePickerDialog.getDatePicker().getYear();
                int month = datePickerDialog.getDatePicker().getMonth() + 1;  // Month is 0-based
                int day = datePickerDialog.getDatePicker().getDayOfMonth();

                String formattedDay = (day < 10) ? "0" + day : String.valueOf(day);
                String formattedMonth = (month < 10) ? "0" + month : String.valueOf(month);
                String selectedDate = formattedDay + "/" + formattedMonth + "/" + year;

                dateRemText.setText(selectedDate);
            }
        });

        datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        datePickerDialog.show();

        Button positiveButton = datePickerDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        if (positiveButton != null) {
            positiveButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.purple));
        }

        Button negativeButton = datePickerDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        if (negativeButton != null) {
            negativeButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.purple));
        }
    }

    private void openTimePicker() {
        timePicker = new TimePicker(requireContext());

        Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int currentMinute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(), R.style.DialogTheme, (view, hourOfDay, minute) -> {
            String selectedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
            timeRemText.setText(selectedTime);
        }, currentHour, currentMinute, DateFormat.is24HourFormat(requireContext()));

        timePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.ok), (dialog, which) -> {

        });

        timePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel), (dialog, which) -> dialog.dismiss());

        timePickerDialog.show();

        Button positiveButton = timePickerDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        if (positiveButton != null) {
            positiveButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.purple));
        }

        Button negativeButton = timePickerDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        if (negativeButton != null) {
            negativeButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.purple));
        }



    }

    public interface OnReminderAddedListener {
        void onReminderAdded(ReminderClass newReminder);
    }


    @SuppressLint("ScheduleExactAlarm")
    private void scheduleReminder(long timestamp) {
        AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) {
            Log.e(TAG, "AlarmManager is null");
            return;
        }

        // Create an intent for the ReminderReceiver and add the reminder message as an extra
        Intent intent = new Intent(requireContext(), ReminderReceiver.class);
        intent.putExtra("REMINDER_MESSAGE", describeReminderEditText.getText().toString()); // Add the reminder message as an extra

        // Create a PendingIntent to be broadcasted when the alarm triggers
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                requireContext(),
                ALARM_REQUEST_CODE,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Schedule the alarm to trigger at the specified timestamp
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timestamp, pendingIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, timestamp, pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, timestamp, pendingIntent);
        }

        Log.d(TAG, "Reminder scheduled at timestamp: " + timestamp);
    }


}
