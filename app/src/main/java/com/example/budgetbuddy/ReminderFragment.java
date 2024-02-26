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
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class ReminderFragment extends Fragment implements AddReminderFragment.OnReminderAddedListener {

    private FloatingActionButton buttonAdd;
    private ListView listViewTasks;
    private ArrayList<String> taskList;
    private ArrayAdapter<String> adapter;

    private static final String TAG = "ReminderFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reminder, container, false);

        listViewTasks = view.findViewById(R.id.listViewTasks);
        taskList = new ArrayList<>();
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, taskList);
        listViewTasks.setAdapter(adapter);

        listViewTasks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedReminder = taskList.get(position);
                BottomSheetDialog bottomSheetFragment = new BottomSheetDialog();
                bottomSheetFragment.show(getParentFragmentManager(), bottomSheetFragment.getTag());
            }
        });

        buttonAdd = view.findViewById(R.id.addReminder_button);

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addReminderFragment();
            }
        });

        fetchRemindersFromFirestore();

        return view;
    }

    public void onReminderAdded(ReminderClass newReminder) {
        taskList.add(newReminder.getTitle());
        adapter.notifyDataSetChanged();
    }

    public void fetchRemindersFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("reminders")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        taskList.clear();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String reminderTitle = document.getString("title");
                            taskList.add(reminderTitle);
                        }

                        adapter.notifyDataSetChanged();
                    } else {
                        Log.w(TAG, "Error getting reminders", task.getException());
                    }
                });
    }

    private void addReminderFragment() {
        AddReminderFragment addReminderFragment = new AddReminderFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, addReminderFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

        Log.d(TAG, "addReminderFragment() called");
    }

    private void updateReminder(ReminderClass updatedReminder) {
        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getContext(), ReminderReceiver.class);
        intent.putExtra("reminder", updatedReminder);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Set the alarm for the desired time
        if (alarmManager != null) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, updatedReminder.getDateTime(), pendingIntent);
        }
    }

}
