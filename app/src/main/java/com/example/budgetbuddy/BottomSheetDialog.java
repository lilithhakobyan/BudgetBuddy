package com.example.budgetbuddy;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.Objects;

public class BottomSheetDialog extends BottomSheetDialogFragment  {

    private static final String TAG = "BottomSheetDialog";
    private FirebaseFirestore firestore;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;


    public BottomSheetDialog() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_dialog, container, false);

        LinearLayout makeChangesLayout = view.findViewById(R.id.make_changes_layout);
        LinearLayout deleteReminderLayout = view.findViewById(R.id.delete_reminder_layout);
        ImageView Close = view.findViewById(R.id.close_bottom_sheet_dialog);

        deleteReminderLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteReminder();
                Toast.makeText(getContext(), "aaaaa", Toast.LENGTH_SHORT).show();

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
        DocumentReference userDocRef = db.collection("users")
                .document(Objects.requireNonNull(Objects.requireNonNull(mAuth.getCurrentUser()).getEmail()));

        CollectionReference remindersCollection = userDocRef.collection("reminders");

        String reminderIdToDelete = "your_actual_reminder_id";

        remindersCollection.document(reminderIdToDelete).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "Reminder Deleted Successfully", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getContext(), "Failed to Delete Reminder!!", Toast.LENGTH_LONG).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Failed to Delete Reminder!!", Toast.LENGTH_LONG).show();
            }
        });
    }
}
