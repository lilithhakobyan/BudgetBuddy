package com.example.budgetbuddy.expense;

import android.annotation.SuppressLint;
import android.os.Bundle;
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
import com.example.budgetbuddy.income.Income;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ExpenseFragment extends Fragment implements ExpenseAdapter.OnDeleteClickListener, OnSwipeToDeleteListener {

    private FirebaseFirestore db;
    private List<Expense> expenseList;
    private RecyclerView recyclerView;
    private ExpenseAdapter expenseAdapter;
    private SharedViewModel sharedViewModel;

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

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new AddExpenseFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        fetchExpenseData();
        sharedViewModel.setExpenseList(expenseList);

        return view;
    }

    private void fetchExpenseData() {
        db.collection("expense")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    expenseList.clear();
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        Expense expense = documentSnapshot.toObject(Expense.class);
                        expense.setId(documentSnapshot.getId());
                        expenseList.add(expense);
                    }
                    expenseAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Failed to fetch expense data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onDeleteClick(int position) {
         Expense expense = expenseList.get(position);
        String incomeId = expense.getId();

        // Ensure incomeId is not null before attempting to delete
        if (incomeId != null && !incomeId.isEmpty()) {
            db.collection("expense")
                    .document(incomeId)
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Remove item from local list
                            expenseList.remove(position);

                            // Notify adapter of item removal
                            expenseAdapter.notifyItemRemoved(position);

                            // Notify SharedViewModel to remove income
                            sharedViewModel.removeIncome(position);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(requireContext(), "Failed to delete expense: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(requireContext(), "Expense ID is null or empty", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onSwipedLeft(int position) {
        onDeleteClick(position);
    }

    @Override
    public void onSwipedRight(int position) {

    }
}
