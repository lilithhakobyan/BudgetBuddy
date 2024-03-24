package com.example.budgetbuddy;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class DemoBottomSheetDialog extends BottomSheetDialogFragment {

    public DemoBottomSheetDialog() {

    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_demo_bottom_sheet_dialog, container, false);


        LinearLayout deleteReminderLayout = view.findViewById(R.id.delete_reminder_layout);
        LinearLayout makeChangesReminderLayout = view.findViewById(R.id.make_changes_layout);
        ImageView Close = view.findViewById(R.id.close_bottom_sheet_dialog);

        makeChangesReminderLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Log in for getting access to all  features.", Toast.LENGTH_SHORT).show();
            }
        });
        deleteReminderLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Log in for getting access to all  features.", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }
}