package com.example.budgetbuddy;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

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

import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddReminderFragment extends Fragment {

    private EditText describeReminderEditText, dateRemEditText, timeRemEditText, amountEditText;
    private Spinner choiceSpinner;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_reminder, container, false);

        describeReminderEditText = view.findViewById(R.id.describeReminderEditText);
        dateRemEditText = view.findViewById(R.id.dateRemEditText);
        timeRemEditText = view.findViewById(R.id.timeRemEditText);
        amountEditText = view.findViewById(R.id.AmountEditText);
        choiceSpinner = view.findViewById(R.id.choiceSpinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.choices_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        choiceSpinner.setAdapter(adapter);

        choiceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedChoice = parentView.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }
        });

        Button submitButton = view.findViewById(R.id.submit_btn);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String reminderTitle = describeReminderEditText.getText().toString();
                String dateText = dateRemEditText.getText().toString();
                String timeText = timeRemEditText.getText().toString();
                String amountText = amountEditText.getText().toString();

                String selectedChoice = choiceSpinner.getSelectedItem().toString();

                String dateTimeString = dateText + " " + timeText;
                long dateTime = convertStringToTimestamp(dateTimeString);
                boolean isSettled = false  ;

                saveReminderToFirestore(reminderTitle, dateTime, amountText, selectedChoice, isSettled);
                Log.d("AddReminderFragment", "Reminder saved to Firestore");

                openReminderFragment();

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


    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Cancel Reminder Confirmation")
                .setMessage(" Are you sure you want to cancel?")
                .setPositiveButton("Yes,cancel", new DialogInterface.OnClickListener() {
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

    private void openReminderFragment() {
        ReminderFragment fragmentB = new ReminderFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragmentB);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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

    }


    public void saveReminderToFirestore(String title, long dateTime, String amountText, String selectedChoice,boolean isSettled) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        ReminderClass newReminder = new ReminderClass();
        newReminder.setTitle(title);
        newReminder.setDateTime(dateTime);
        newReminder.setSettled(isSettled);


        db.collection("reminders")
                .add(newReminder)
                .addOnSuccessListener(documentReference -> {

                })
                .addOnFailureListener(e -> {
                    
                });
    }

}


