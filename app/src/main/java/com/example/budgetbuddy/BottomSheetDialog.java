package com.example.budgetbuddy;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.os.Bundle;
import com.google.firebase.firestore.QuerySnapshot;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BottomSheetDialog extends BottomSheetDialogFragment {

    private static final String TAG = "BottomSheetDialog";
    private FirebaseFirestore firestore;
    private FirebaseAuth mAuth;
    private ArrayAdapter<String> reminderAdapter;
    private List<String> reminderList = new ArrayList<>();

    public BottomSheetDialog() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_dialog, container, false);

        firestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        LinearLayout makeChangesLayout = view.findViewById(R.id.make_changes_layout);
        LinearLayout deleteReminderLayout = view.findViewById(R.id.delete_reminder_layout);
        ImageView Close = view.findViewById(R.id.close_bottom_sheet_dialog);

        deleteReminderLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteReminder();
            }
        });

        Close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return view;
    }

    private void deleteReminder() {
        if (firestore == null) {
            firestore = FirebaseFirestore.getInstance();
        }

        CollectionReference remindersCollection = firestore.collection("reminders");

        remindersCollection.limit(1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                document.getReference().delete()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> deleteTask) {
                                                if (deleteTask.isSuccessful()) {
                                                    Toast.makeText(getContext(), "Reminder Deleted Successfully", Toast.LENGTH_LONG).show();


                                                    reminderList.clear();
                                                    reminderAdapter.notifyDataSetChanged();
                                                } else {
                                                    Toast.makeText(getContext(), "Failed to Delete Reminder!!", Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });

                                break;
                            }
                        } else {
                            Toast.makeText(getContext(), "Error getting reminders", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}