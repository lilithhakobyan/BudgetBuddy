package com.example.budgetbuddy.reminder;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budgetbuddy.R;
import com.example.budgetbuddy.adapter.ReminderAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class ReminderFragment extends Fragment implements ReminderAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private ArrayList<ReminderClass> reminderList;
    private ReminderAdapter adapter;
    private SwipeToDeleteCallback swipeToDeleteCallback;

    private static final String TAG = "ReminderFragment";

    private boolean isNoRemindersViewShown = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reminder, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewTasks);
        reminderList = new ArrayList<>();
        adapter = new ReminderAdapter(getContext(), reminderList);
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        view.findViewById(R.id.addReminder_button).setOnClickListener(v -> addReminderFragment());

        fetchRemindersFromFirestore();

        swipeToDeleteCallback = new SwipeToDeleteCallback();
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        checkNoRemindersViewVisibility(view);
    }


    private void checkNoRemindersViewVisibility(View parentView) {
        ViewGroup parent = (ViewGroup) parentView.getParent();
        @SuppressLint("ResourceType") View noReminderView = parent.findViewById(R.layout.layout_no_reminders);
        if (noReminderView != null) {
            isNoRemindersViewShown = true;
        } else {
            isNoRemindersViewShown = false;
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isNoRemindersViewShown = false;
    }

    private void fetchRemindersFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (db == null) {
            Log.e(TAG, "Firestore instance is null. Cannot fetch income data.");
            return;
        }

        if (user != null) {
            db.collection("reminders")
                    .whereEqualTo("userId", user.getUid())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            reminderList.clear();

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ReminderClass reminder = document.toObject(ReminderClass.class);
                                reminder.setDocumentId(document.getId());
                                reminderList.add(reminder);
                                Log.d(TAG, "Reminder retrieved: " + reminder.getTitle());
                            }

                            if (reminderList.isEmpty()) {
                                View noReminderView = LayoutInflater.from(requireContext()).inflate(R.layout.layout_no_reminders, recyclerView, false);
                                ViewGroup parent = (ViewGroup) recyclerView.getParent();
                                int index = parent.indexOfChild(recyclerView);
                                parent.removeView(recyclerView);
                                parent.addView(noReminderView, index);
                            }

                            adapter.notifyDataSetChanged();
                            Log.d(TAG, "Reminder list size: " + reminderList.size());

                        } else {
                            Log.w(TAG, "Error getting reminders", task.getException());
                        }
                    });
        } else {
            Log.e(TAG, "User is null");
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


    @Override
    public void onItemClick(ReminderClass reminder) {

    }

    private class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {

        private Drawable deleteIcon;
        private int iconMargin;
        private boolean initiated;

        SwipeToDeleteCallback() {
            super(0, ItemTouchHelper.LEFT);
        }

        private void init() {
            deleteIcon = ContextCompat.getDrawable(getContext(), R.drawable.baseline_delete_white);
            iconMargin = (int) getResources().getDimension(R.dimen.icon_margin);
            initiated = true;
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            // Not needed for swipe-to-delete
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            ReminderClass deletedReminder = reminderList.get(position);

            reminderList.remove(position);
            adapter.notifyItemRemoved(position);

            deleteReminderDocument(deletedReminder);
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            if (!initiated) {
                init();
            }
            View itemView = viewHolder.itemView;
            int itemHeight = itemView.getHeight();

            // Draw red background when swiping left
            if (dX < 0) {
                Paint paint = new Paint();
                paint.setColor(Color.rgb(139, 0, 0));
                RectF background = new RectF(
                        (float) itemView.getRight() + dX,
                        (float) itemView.getTop(),
                        (float) itemView.getRight(),
                        (float) itemView.getBottom()
                );
                c.drawRect(background, paint);

                int iconTop = itemView.getTop() + (itemHeight - deleteIcon.getIntrinsicHeight()) / 2;
                int iconMargin = (itemHeight - deleteIcon.getIntrinsicHeight()) / 2;
                int iconLeft = itemView.getRight() - iconMargin - deleteIcon.getIntrinsicWidth();
                int iconRight = itemView.getRight() - iconMargin;
                int iconBottom = iconTop + deleteIcon.getIntrinsicHeight();

                int iconWidth = deleteIcon.getIntrinsicWidth();
                int iconMarginEnd = iconMargin + iconWidth;
                int iconLeftOnRed = iconLeft - (int) Math.abs(dX);
                int iconRightOnRed = iconRight - (int) Math.abs(dX);
                if (iconLeftOnRed < itemView.getRight() - iconMarginEnd) {
                    iconLeftOnRed = itemView.getRight() - iconMarginEnd;
                    iconRightOnRed = itemView.getRight() - iconMargin;
                }

                deleteIcon.setBounds(iconLeftOnRed, iconTop, iconRightOnRed, iconBottom);
                deleteIcon.draw(c);
            }

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    }

    private void deleteReminderDocument(ReminderClass reminder) {
        if (reminder != null) {
            String documentId = reminder.getDocumentId();
            if (documentId  != null) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                CollectionReference remindersRef = db.collection("reminders");

                remindersRef.document(documentId)
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot successfully deleted: " + documentId);
                                reminderList.remove(reminder);

                                adapter.notifyDataSetChanged();

                                if (reminderList.isEmpty()) {
                                    View noReminderView = LayoutInflater.from(getContext()).inflate(R.layout.layout_no_reminders, recyclerView, false);
                                    ViewGroup parent = (ViewGroup) recyclerView.getParent();
                                    int index = parent.indexOfChild(recyclerView);
                                    parent.removeView(recyclerView);
                                    parent.addView(noReminderView, index);
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error deleting document " + documentId, e);
                            }
                        });
            } else {
                Log.w(TAG, "Document ID is null. Cannot delete document.");
            }
        } else {
            Log.w(TAG, "Reminder object is null. Cannot delete document.");
        }
    }


}
