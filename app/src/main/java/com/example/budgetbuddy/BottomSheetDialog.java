package com.example.budgetbuddy;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.firestore.FirebaseFirestore;

public class BottomSheetDialog extends BottomSheetDialogFragment {

    private static final String TAG = "BottomSheetDialog";
    private FirebaseFirestore firestore;
    private String selectedReminderId;

    public BottomSheetDialog() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            selectedReminderId = getArguments().getString("selectedReminderId");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_dialog, container, false);

        firestore = FirebaseFirestore.getInstance();

        LinearLayout deleteReminderLayout = view.findViewById(R.id.delete_reminder_layout);

        ImageView close = view.findViewById(R.id.close_bottom_sheet_dialog);

        deleteReminderLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteReminder(selectedReminderId);
                dismiss();
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return view;
    }

    private void deleteReminder(String reminderId) {
        if (firestore == null) {
            firestore = FirebaseFirestore.getInstance();
        }

        if (!TextUtils.isEmpty(reminderId)) {
            firestore.collection("reminders").document(reminderId)
                    .delete()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Reminder Deleted Successfully", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getContext(), "Failed to Delete Reminder!!", Toast.LENGTH_LONG).show();
                            Log.e(TAG, "Error deleting reminder", task.getException());
                        }
                    });
        } else {
            Log.e(TAG, "Invalid reminder ID");
        }
    }
}
