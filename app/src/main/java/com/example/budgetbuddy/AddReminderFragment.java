package com.example.budgetbuddy;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AddReminderFragment extends Fragment {

    private EditText describeReminderEditText, amountEditText;
    private TextView timeRemText;
    private Spinner choiceSpinner;
    private FirebaseFirestore db;
    private Button DateButton;
    private Button TimeButton;
    private TextView dateRemText;
    private FirebaseAuth auth;

    private List<ReminderClass> reminderList = new ArrayList<>();

    private static final int ALARM_REQUEST_CODE = 123;
    private Context mContext;

    private static final String DATE_TIME_FORMAT = "MM/dd/yyyy HH:mm";
    private TimePicker timePicker;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_reminder, container, false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            if (ContextCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS) !=
                    PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(requireActivity(),
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},101);
            }
        }

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

        FirebaseUser user = auth.getCurrentUser();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.choices_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        choiceSpinner.setAdapter(adapter);


        DateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePicker();
            }

        });

        TimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTimePicker();
            }
        });
        choiceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }
        });

        Button submitButton = view.findViewById(R.id.submit_btn);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveReminderToFirestore();
                makeNotification();
                openReminderFragment();
            }
        });


        ImageView imageView = view.findViewById(R.id.close_addrem);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog();
            }
        });


        return view;
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
        TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(), R.style.DialogTheme, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                String selectedTime = String.format(Locale.getDefault(), "%02d:%02d", hour, minute);
                timeRemText.setText(selectedTime);
            }
        }, 15, 30, false);

        timePickerDialog.show();

        timePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }

        });
        timePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        Button positiveButton = timePickerDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        if (positiveButton != null) {
            positiveButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.purple));
        }

        Button negativeButton = timePickerDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        if (negativeButton != null) {
            negativeButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.purple));
        }
    }


    private void saveReminderToFirestore() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            Toast.makeText(requireContext(), "User not authenticated. Please log in.", Toast.LENGTH_SHORT).show();
            return;
        }

        String reminderTitle = describeReminderEditText.getText().toString();
        if (reminderTitle.trim().isEmpty()) {
            Toast.makeText(requireContext(), "Reminder title is required", Toast.LENGTH_SHORT).show();
            return;
        }

        String dateText = dateRemText.getText().toString();
        String timeText = timeRemText.getText().toString();
        String amountText = amountEditText.getText().toString();
        String selectedChoice = choiceSpinner.getSelectedItem().toString();

        String dateTimeString = dateText + " " + timeText;
        long dateTime = convertStringToTimestamp(dateTimeString);

        boolean settled = false;

        ReminderClass newReminder = new ReminderClass(reminderTitle, dateTime, amountText, selectedChoice, settled);

        reminderList.add(newReminder);

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


        long alarmTimeMillis = convertStringToTimestamp(dateTimeString);

        scheduleAlarm(alarmTimeMillis);
    }

    private void clearInputFields() {
        describeReminderEditText.getText().clear();
        amountEditText.getText().clear();
        timeRemText.setText("");
        dateRemText.setText("");
    }


    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Cancel Reminder Confirmation")
                .setMessage(" Are you sure you want to cancel?")
                .setPositiveButton("Yes, cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        openReminderFragment();
                    }
                })
                .setNegativeButton("Continue", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void openReminderFragment() {
        ReminderFragment fragmentB = new ReminderFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        Bundle args = new Bundle();
        args.putString("userId", auth.getCurrentUser().getUid());
        fragmentB.setArguments(args);

        transaction.replace(R.id.fragment_container, fragmentB);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private long convertStringToTimestamp(String dateTimeString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_TIME_FORMAT, Locale.UK);
        try {
            Date date = dateFormat.parse(dateTimeString);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Error parsing date/time", Toast.LENGTH_SHORT).show();
            return 0;
        }

    }

    public interface OnReminderAddedListener {
        void onReminderAdded(ReminderClass newReminder);
    }

    private void scheduleAlarm(long alarmTimeMillis) {
        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(mContext, ReminderReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, ALARM_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE); // Add FLAG_IMMUTABLE

        alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTimeMillis, pendingIntent);
    }

   
    public void makeNotification(){
        String chanelID = "CHANNEL_ID_NOTIFICATION";
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity().getApplicationContext(),chanelID);
        builder.setSmallIcon(R.drawable.notifications_icon);
        builder.setContentTitle("Notification Title");
        builder.setContentText("AAAAAAAAAA");
        builder.setAutoCancel(true).setPriority(NotificationCompat.PRIORITY_DEFAULT);

        Intent intent = new Intent(getActivity().getApplicationContext(), NotificationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("data","Some value to be passed here");

        PendingIntent pendingIntent = PendingIntent.getActivity(getActivity().getApplicationContext(),0,intent,PendingIntent.FLAG_MUTABLE);
        builder.setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) requireActivity().getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = notificationManager.getNotificationChannel(chanelID);
            if (notificationChannel == null){
                int importance = NotificationManager.IMPORTANCE_HIGH;
                notificationChannel = new NotificationChannel(chanelID,"Some description",importance);
                notificationChannel.setLightColor(Color.GREEN);
                notificationChannel.enableVibration(true);
                notificationManager.createNotificationChannel(notificationChannel);

            }
        }
        notificationManager.notify(0,builder.build());
    }
}
