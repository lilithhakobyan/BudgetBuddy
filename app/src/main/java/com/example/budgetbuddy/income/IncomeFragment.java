package com.example.budgetbuddy.income;

import android.content.Context;
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
import com.example.budgetbuddy.adapter.IncomeAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class IncomeFragment extends Fragment implements IncomeAdapter.OnDeleteClickListener, OnSwipeToDeleteListener {

    private Context mContext;

    private FirebaseFirestore db;
    private List<Income> incomeList;
    private RecyclerView recyclerView;
    private IncomeAdapter incomeAdapter;
    private SharedViewModel sharedViewModel;

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
        incomeList = new ArrayList<>();
        incomeAdapter = new IncomeAdapter(incomeList);
        incomeAdapter.setOnDeleteClickListener(this);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_income, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(incomeAdapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(this));
        itemTouchHelper.attachToRecyclerView(recyclerView);

        FloatingActionButton fab = view.findViewById(R.id.add_income_floatingActionButton);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new AddIncomeFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        fetchIncomeData();
        sharedViewModel.setIncomeList(incomeList);

        return view;
    }

    public void fetchIncomeData() {
        FirebaseFirestore.getInstance().collection("income")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Check if mContext is not null before using it
                        if (mContext != null) {

                        }
                        QuerySnapshot queryDocumentSnapshots = task.getResult();
                        if (queryDocumentSnapshots != null && incomeList != null) {
                            incomeList.clear(); // Clear the existing list
                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                Income income = documentSnapshot.toObject(Income.class);
                                income.setId(documentSnapshot.getId());
                                incomeList.add(income);
                            }
                            incomeAdapter.notifyDataSetChanged();
                            sharedViewModel.setIncomeList(incomeList);
                        } else {
                            if (mContext != null) {
                                Toast.makeText(mContext, "No income data found", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        if (mContext != null) {
                            Toast.makeText(mContext, "Failed to fetch income data: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onDeleteClick(int position) {
        Income income = incomeList.get(position);
        String incomeId = income.getId();

        if (incomeId != null && !incomeId.isEmpty()) {
            db.collection("income")
                    .document(incomeId)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        incomeList.remove(position);
                        incomeAdapter.notifyItemRemoved(position);
                        sharedViewModel.removeIncome(position);
                    })
                    .addOnFailureListener(e -> {
                        if (mContext != null) {
                            Toast.makeText(mContext, "Failed to delete income: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            if (mContext != null) {
                Toast.makeText(mContext, "Income ID is null or empty", Toast.LENGTH_SHORT).show();
            }
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