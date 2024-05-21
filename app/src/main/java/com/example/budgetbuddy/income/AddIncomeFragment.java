package com.example.budgetbuddy.income;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;
import static com.example.budgetbuddy.currency.CurrencyUtils2.fetchCurrencies;
import static com.example.budgetbuddy.income.IncomeAlarmReceiver.CHANNEL_ID;
import static com.example.budgetbuddy.income.IncomeAlarmReceiver.NOTIFICATION_ID;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.budgetbuddy.R;
import com.example.budgetbuddy.SharedViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddIncomeFragment extends Fragment {

    public interface CategorySelectionListener {
        void onCategorySelected(String categoryName, int categoryIcon);

    }

    private TextView selectCategoryTextView;
    private ImageView selectIconButton;
    private IncomeCategory selectedCategory;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private EditText descEditText;
    private EditText amountEditText;
    private String selectedCurrency;
    private Button save;
    private ImageView close;
    private SharedViewModel sharedViewModel;
    private RadioGroup repeatFrequencyGroup;
    private AlarmManager alarmManager;
    private PendingIntent alarmIntent;


    public AddIncomeFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_income, container, false);

        Spinner spinner = view.findViewById(R.id.currency_spinner);

        List<String> currencies = fetchCurrencies();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), R.layout.spinner_item, currencies);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCurrency = currencies.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedCurrency = null;
            }
        });

        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ConstraintLayout incomeCategoryLayout = view.findViewById(R.id.income_category);
        selectCategoryTextView = view.findViewById(R.id.select_category);
        selectIconButton = view.findViewById(R.id.select_icon);
        save = view.findViewById(R.id.button);
        descEditText = view.findViewById(R.id.desc_edit_text);
        amountEditText = view.findViewById(R.id.amount_edit_text);
        TextView amountTextView = view.findViewById(R.id.amount_text_view);
        close = view.findViewById(R.id.close_income);
        repeatFrequencyGroup = view.findViewById(R.id.repeat_frequency_group);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setMessage("Are you sure you want to discard the draft?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (isAdded()) {
                                    getParentFragmentManager().popBackStack();
                                }
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        incomeCategoryLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCategoryDialog();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveIncome();
                getParentFragmentManager().popBackStack();
            }
        });
        alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);

        Intent alarmIntent = new Intent(requireContext(), IncomeAlarmReceiver.class);
        alarmIntent.setAction("com.example.budgetbuddy.income.INCOME_ALARM");
        this.alarmIntent = PendingIntent.getBroadcast(requireContext(), 0, alarmIntent, PendingIntent.FLAG_MUTABLE);

        askUserToContinue();
    }

    private void showCategoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Select Category")
                .setItems(getCategoryNamesArray(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectedCategory = IncomeCategory.values()[which];
                        updateSelectedCategory(selectedCategory.getCategoryName(), selectedCategory.getCategoryIcon());
                    }
                })
                .setNegativeButton("Cancel", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private String[] getCategoryNamesArray() {
        String[] categoryNames = new String[IncomeCategory.values().length];
        for (int i = 0; i < IncomeCategory.values().length; i++) {
            categoryNames[i] = IncomeCategory.values()[i].getCategoryName();
        }
        return categoryNames;
    }

    public void updateSelectedCategory(String categoryName, int categoryIcon) {
        selectCategoryTextView.setText(categoryName);
        selectIconButton.setImageResource(categoryIcon);
    }

    private void saveIncome() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Log.e(TAG, "User is not authenticated");
            return;
        }
        String amountStr = amountEditText.getText().toString().trim();
        String description = descEditText.getText().toString().trim();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (auth.getCurrentUser() == null) {
            return;
        }

        if (userId == null) {
            return;
        }

        if (amountStr.isEmpty()) {
            amountEditText.setError("Amount is required");
            amountEditText.requestFocus();
            return;
        }

        if (description.isEmpty()) {
            descEditText.setError("Description is required");
            descEditText.requestFocus();
            return;
        }

        if (selectedCategory == null) {
            Toast.makeText(requireContext(), "Please select a category", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedCurrency == null || selectedCurrency.equals("Select currency")) {
            Toast.makeText(requireContext(), "Please select a currency", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountStr);

        Income income = new Income(amount, selectedCategory.getCategoryName(), description, selectedCurrency, userId);
        Map<String, Object> incomeData = new HashMap<>();
        incomeData.put("id", userId);
        incomeData.put("amount", amount);
        incomeData.put("category", selectedCategory.getCategoryName());
        incomeData.put("currency", selectedCurrency);
        incomeData.put("description", description);

        db.collection("income")
                .add(incomeData) // Pass the income data here
                .addOnSuccessListener(documentReference -> {
                    String incomeId = documentReference.getId(); // Get the generated document ID
                    income.setId(incomeId); // Set the ID in the Income object
                    List<Income> updatedIncomeList = sharedViewModel.getIncomeList().getValue();
                    if (updatedIncomeList != null) {
                        updatedIncomeList.add(income);
                        sharedViewModel.setIncomeList(updatedIncomeList);
                    }

                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), "Income saved successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), "Failed to save income: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        int selectedFrequencyId = repeatFrequencyGroup.getCheckedRadioButtonId();
        String selectedFrequency = "";

        if (selectedFrequencyId == R.id.every_day_radio_button) {
            selectedFrequency = "Every day";
        } else if (selectedFrequencyId == R.id.weekly_radio_button) {
            selectedFrequency = "Weekly";
        } else if (selectedFrequencyId == R.id.monthly_radio_button) {
            selectedFrequency = "Monthly";
        } else if (selectedFrequencyId == R.id.annually_radio_button) {
            selectedFrequency = "Annually";
        }

        if (!selectedFrequency.isEmpty()) {
            scheduleIncomeAddition(selectedFrequency);
        }
    }



    private void scheduleIncomeAddition(String frequency) {
        // Get the interval based on the selected frequency
        long intervalMillis = getIntervalMillisFromFrequency(frequency);

        // Schedule the periodic task using AlarmManager
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), intervalMillis, alarmIntent);
    }

    private long getIntervalMillisFromFrequency(String frequency) {
        switch (frequency) {
            case "Every day":
                return AlarmManager.INTERVAL_DAY;
            case "Weekly":
                return AlarmManager.INTERVAL_DAY * 7; // 1 week
            case "Monthly":
                // Calculate interval until the next month
                Calendar nextMonth = Calendar.getInstance();
                nextMonth.add(Calendar.MONTH, 1);
                nextMonth.set(Calendar.DAY_OF_MONTH, nextMonth.get(Calendar.DAY_OF_MONTH)); // Same day of the month
                nextMonth.set(Calendar.HOUR_OF_DAY, nextMonth.get(Calendar.HOUR_OF_DAY)); // Same hour
                nextMonth.set(Calendar.MINUTE, nextMonth.get(Calendar.MINUTE)); // Same minute
                nextMonth.set(Calendar.SECOND, nextMonth.get(Calendar.SECOND)); // Same second
                nextMonth.set(Calendar.MILLISECOND, nextMonth.get(Calendar.MILLISECOND)); // Same millisecond
                return nextMonth.getTimeInMillis() - System.currentTimeMillis();
            case "Annually":
                // Calculate interval until the next year
                Calendar nextYear = Calendar.getInstance();
                nextYear.add(Calendar.YEAR, 1);
                nextYear.set(Calendar.MONTH, nextYear.get(Calendar.MONTH)); // Same month
                nextYear.set(Calendar.DAY_OF_MONTH, nextYear.get(Calendar.DAY_OF_MONTH)); // Same day of the month
                nextYear.set(Calendar.HOUR_OF_DAY, nextYear.get(Calendar.HOUR_OF_DAY)); // Same hour
                nextYear.set(Calendar.MINUTE, nextYear.get(Calendar.MINUTE)); // Same minute
                nextYear.set(Calendar.SECOND, nextYear.get(Calendar.SECOND)); // Same second
                nextYear.set(Calendar.MILLISECOND, nextYear.get(Calendar.MILLISECOND)); // Same millisecond
                return nextYear.getTimeInMillis() - System.currentTimeMillis();
            default:
                return AlarmManager.INTERVAL_DAY; // Default to daily
        }
    }

    private void askUserToContinue() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(requireContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Continue adding income?")
                .setContentText("Do you want to continue adding income with the same details?")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .addAction(R.drawable.baseline_check_24, "Continue", createContinueIntent())
                .addAction(R.drawable.close, "Cancel", createCancelIntent());

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(requireContext());

        // Check if the app has the necessary permission
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // Handle the case where the permission is not granted
            // You can request the permission here or handle the absence of permission in another way
            // For now, I'll just log a message
            Log.e("Notification", "Permission not granted");
            return;
        }

        // If permission is granted, proceed to send the notification
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }




    private PendingIntent createContinueIntent() {
        Intent continueIntent = new Intent(requireContext(), IncomeAlarmReceiver.class);
        continueIntent.setAction("continue_action");
        return PendingIntent.getBroadcast(requireContext(), 0, continueIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
    }

    private PendingIntent createCancelIntent() {
        Intent cancelIntent = new Intent(requireContext(), IncomeAlarmReceiver.class);
        cancelIntent.setAction("cancel_action");
        return PendingIntent.getBroadcast(requireContext(), 0, cancelIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
    }


}