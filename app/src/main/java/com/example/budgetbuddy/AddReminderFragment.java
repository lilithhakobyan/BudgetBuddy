package com.example.budgetbuddy;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AddReminderFragment extends Fragment {

    private EditText describeReminderEditText, dateRemEditText, timeRemEditText, amountEditText;
    private Spinner choiceSpinner;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_reminder, container, false);

        // Initialize UI components
        describeReminderEditText = view.findViewById(R.id.describeReminderEditText);
        dateRemEditText = view.findViewById(R.id.dateRemEditText);
        timeRemEditText = view.findViewById(R.id.timeRemEditText);
        amountEditText = view.findViewById(R.id.AmountEditText);
        choiceSpinner = view.findViewById(R.id.choiceSpinner);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Set up the Spinner with choices
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.choices_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        choiceSpinner.setAdapter(adapter);

        // Spinner item selection listener
        choiceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // You can add any specific logic here if needed
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

        EditText dateEditText = view.findViewById(R.id.dateRemEditText);
        dateEditText.addTextChangedListener(new TextWatcher() {
            private static final int MAX_LENGTH = 8;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0 && (editable.length() % 3) == 0) {
                    final char c = editable.charAt(editable.length() - 1);
                    if ('/' == c) {
                        editable.delete(editable.length() - 1, editable.length());
                    } else {
                        editable.insert(editable.length() - 1, "/");
                    }
                }

                if (editable.length() > MAX_LENGTH) {
                    editable.delete(MAX_LENGTH, editable.length());
                }
            }
        });

        return view;
    }

    private void saveReminderToFirestore() {
        // Access the values entered by the user
        String reminderTitle = describeReminderEditText.getText().toString();
        String dateText = dateRemEditText.getText().toString();
        String timeText = timeRemEditText.getText().toString();
        String amountText = amountEditText.getText().toString();
        String selectedChoice = choiceSpinner.getSelectedItem().toString();

        String dateTimeString = dateText + " " + timeText;
        long dateTime = convertStringToTimestamp(dateTimeString);


        Map<String, Object> reminderData = new HashMap<>();
        reminderData.put("title", reminderTitle);
        reminderData.put("date", dateText);
        reminderData.put("time", timeText);
        reminderData.put("amount", amountText);
        reminderData.put("choice", selectedChoice);
        reminderData.put("dateTime", dateTime);

        db.collection("reminders")
                .add(reminderData)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "Reminder added with ID: " + documentReference.getId());

                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error adding reminder", e);
                });
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
        transaction.replace(R.id.fragment_container, fragmentB);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private long convertStringToTimestamp(String dateTimeString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.UK);
        try {
            Date date = dateFormat.parse(dateTimeString);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Error parsing date/time", Toast.LENGTH_SHORT).show();
            return 0;
        }
    }
}
