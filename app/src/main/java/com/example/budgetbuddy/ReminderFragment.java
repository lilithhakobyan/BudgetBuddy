package com.example.budgetbuddy;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.budgetbuddy.AddReminderFragment;
import com.example.budgetbuddy.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;


public class ReminderFragment extends Fragment {


    private EditText editTextTask;
    private FloatingActionButton buttonAdd;
    private ListView listViewTasks;
    private ArrayList<String> taskList;
    private ArrayAdapter<String> adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reminder, container, false);

        buttonAdd = view.findViewById(R.id.addReminder_button);

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addReminderFragment();
            }
        });

        return view;
    }

    private void addReminderFragment() {
        AddReminderFragment addReminderFragment = new AddReminderFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, addReminderFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

        Log.d("ReminderFragment", "addReminderFragment() called");
    }




}