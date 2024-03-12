package com.example.budgetbuddy.demo;

import static androidx.fragment.app.FragmentManager.TAG;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.budgetbuddy.AddReminderFragment;
import com.example.budgetbuddy.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class DemoReminderFragment extends Fragment {

    public DemoReminderFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_reminder_demo, container, false);


        FloatingActionButton fab = view.findViewById(R.id.addReminder_button_demo);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                demoAddReminderFragment();
            }
        });

        return view;
    }

    @SuppressLint("RestrictedApi")
    private void demoAddReminderFragment() {
        DemoAddReminderFragment demoAddReminderFragment = new DemoAddReminderFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container_d, demoAddReminderFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

        Log.d(TAG, "demoAddReminderFragment() called");
    }


}
