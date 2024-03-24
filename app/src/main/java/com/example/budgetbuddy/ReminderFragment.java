package com.example.budgetbuddy;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.budgetbuddy.adapter.ReminderAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;

public class ReminderFragment extends Fragment implements AddReminderFragment.OnReminderAddedListener {

    private ListView listViewTasks;
    private ArrayList<ReminderClass> reminderList;
    private ReminderAdapter adapter;

    private static final String TAG = "ReminderFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reminder, container, false);

        listViewTasks = view.findViewById(R.id.listViewTasks);
        reminderList = new ArrayList<>();
        adapter = new ReminderAdapter(getContext(), R.layout.list_item_layout, reminderList);
        listViewTasks.setAdapter(adapter);

        listViewTasks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ReminderClass selectedReminder = reminderList.get(position);
                openBottomSheetDialog(selectedReminder.getTitle());
            }
        });

        view.findViewById(R.id.addReminder_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addReminderFragment();
            }
        });

        fetchRemindersFromFirestore();
        retrieveFCMToken();

        return view;
    }

    private void openBottomSheetDialog(String selectedReminder) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog();
        Bundle bundle = new Bundle();
        bundle.putString("selectedReminder", selectedReminder);
        bottomSheetDialog.setArguments(bundle);
        bottomSheetDialog.show(getParentFragmentManager(), bottomSheetDialog.getTag());
    }

    public void onReminderAdded(ReminderClass newReminder) {
        reminderList.add(newReminder);
        adapter.notifyDataSetChanged();
        updateReminder(newReminder);
    }

    private void fetchRemindersFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            db.collection("reminders")
                    .whereEqualTo("userId", user.getUid())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            reminderList.clear();

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ReminderClass reminder = document.toObject(ReminderClass.class);
                                reminderList.add(reminder);
                            }

                            adapter.notifyDataSetChanged();
                        } else {
                            Log.w(TAG, "Error getting reminders", task.getException());
                        }
                    });
        }
    }

    private void addReminderFragment() {
        AddReminderFragment addReminderFragment = new AddReminderFragment();
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, addReminderFragment)
                .addToBackStack(null)
                .commit();

        Log.d(TAG, "addReminderFragment() called");
    }

    private void updateReminder(ReminderClass updatedReminder) {
        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getContext(), ReminderReceiver.class);
        intent.putExtra("reminder", updatedReminder);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (alarmManager != null) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, updatedReminder.getDateTime(), pendingIntent);
            Log.d(TAG, "Reminder alarm set for: " + updatedReminder.getDateTime());
        } else {
            Log.e(TAG, "Failed to get AlarmManager");
        }
    }

    private void retrieveFCMToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        String token = task.getResult();
                        Log.d(TAG, "FCM Token: " + token);
                        // Save the token or use it as needed
                    } else {
                        Log.w(TAG, "Fetching FCM token failed", task.getException());
                    }
                });
    }
}
