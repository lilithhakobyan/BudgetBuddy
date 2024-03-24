package com.example.budgetbuddy.demo;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.budgetbuddy.DemoBottomSheetDialog;
import com.example.budgetbuddy.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class DemoReminderFragment extends Fragment {

    public DemoReminderFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reminder_demo, container, false);

        FloatingActionButton fab = view.findViewById(R.id.addReminder_button_demo);
        RelativeLayout relativeLayout = view.findViewById(R.id.demo_reminder_item);

        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DemoBottomSheetDialog bottomSheetDialog = new DemoBottomSheetDialog();
                bottomSheetDialog.show(getChildFragmentManager(), bottomSheetDialog.getTag());
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                demoAddReminderFragment();
                openDemoBottomSheetDialog(); // Call to open the bottom sheet dialog
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

        Log.d("DemoReminderFragment", "demoAddReminderFragment() called");
    }

    private void openDemoBottomSheetDialog() {
        DemoBottomSheetDialog bottomSheetDialog = new DemoBottomSheetDialog();
        bottomSheetDialog.show(getChildFragmentManager(), bottomSheetDialog.getTag());
    }
}
