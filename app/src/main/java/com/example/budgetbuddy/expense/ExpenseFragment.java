package com.example.budgetbuddy.expense;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budgetbuddy.OnSwipeToDeleteListener;
import com.example.budgetbuddy.R;
import com.example.budgetbuddy.SharedViewModel;
import com.example.budgetbuddy.SwipeToDeleteCallback;
import com.example.budgetbuddy.adapter.ExpenseAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ExpenseFragment extends Fragment implements ExpenseAdapter.OnDeleteClickListener, OnSwipeToDeleteListener {
    private Context mContext;
    private FirebaseFirestore db;
    private List<Expense> expenseList;
    private RecyclerView recyclerView;
    private ExpenseAdapter expenseAdapter;
    private SharedViewModel sharedViewModel;

    private static final String TAG = "ExpenseFragment";

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        expenseList = new ArrayList<>();
        expenseAdapter = new ExpenseAdapter(expenseList);
        expenseAdapter.setOnDeleteClickListener(this);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_expense, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_expense);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(expenseAdapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(this));
        itemTouchHelper.attachToRecyclerView(recyclerView);

        FloatingActionButton fab = view.findViewById(R.id.add_expense_floatingActionButton);
        fab.setOnClickListener(v -> getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new AddExpenseFragment())
                .addToBackStack(null)
                .commit());

        fetchExpenseData();
        sharedViewModel.setExpenseList(expenseList);

        return view;
    }

    public void fetchExpenseData() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.d(TAG, "Fetching expense data for user ID: " + userId);

        db.collection("expense")
                .whereEqualTo("id", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot queryDocumentSnapshots = task.getResult();
                        if (queryDocumentSnapshots != null) {
                            expenseList.clear();
                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                Expense expense = documentSnapshot.toObject(Expense.class);
                                expense.setId(documentSnapshot.getId()); // Set the document ID if needed
                                expenseList.add(expense);
                                Log.d(TAG, "Expense added: " + expense.getId());
                            }
                            expenseAdapter.notifyDataSetChanged();
                            sharedViewModel.setExpenseList(expenseList);
                        } else {
                            showToast("No expense data found");
                            Log.d(TAG, "No expense data found");
                        }
                    } else {
                        showToast("Failed to fetch expense data: " + task.getException().getMessage());
                        Log.e(TAG, "Failed to fetch expense data", task.getException());
                    }
                });
    }
    private void showToast(String message) {
        if (mContext != null) {
            Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDeleteClick(int position) {
        Expense expense = expenseList.get(position);
        String expenseId = expense.getId();

        if (expenseId != null && !expenseId.isEmpty()) {
            db.collection("expense")
                    .document(expenseId)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        expenseList.remove(position);
                        expenseAdapter.notifyItemRemoved(position);
                        sharedViewModel.removeExpense(position);
                        Log.d(TAG, "Expense deleted successfully: " + expenseId);
                    })
                    .addOnFailureListener(e -> {
                        showToast("Failed to delete expense: " + e.getMessage());
                        Log.e(TAG, "Failed to delete expense: " + e.getMessage(), e);
                    });
        } else {
            showToast("Expense ID is null or empty");
            Log.e(TAG, "Expense ID is null or empty");
        }
    }

    @Override
    public void onSwipedLeft(int position) {
        onDeleteClick(position);
    }

    @Override
    public void onSwipedRight(int position) {
        // Implement if needed
    }
}