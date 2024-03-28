package com.example.budgetbuddy.income;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budgetbuddy.R;
import com.example.budgetbuddy.adapter.IncomeAdapter;
import com.example.budgetbuddy.income.Income;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class IncomeFragment extends Fragment implements IncomeAdapter.OnDeleteClickListener {

    private FirebaseFirestore db;
    private List<Income> incomeList;
    private RecyclerView recyclerView;
    private IncomeAdapter incomeAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        incomeList = new ArrayList<>();
        incomeAdapter = new IncomeAdapter(incomeList);
        incomeAdapter.setOnDeleteClickListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_income, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(incomeAdapter);

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

        return view;
    }

    private void fetchIncomeData() {
        db.collection("income")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    incomeList.clear();
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        Income income = documentSnapshot.toObject(Income.class);
                        income.setId(documentSnapshot.getId());
                        incomeList.add(income);
                    }
                    incomeAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Failed to fetch income data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


    @Override
    public void onDeleteClick(int position) {
        Income income = incomeList.get(position);
        String incomeId = income.getId();
        db.collection("income")
                .document(incomeId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        incomeList.remove(position);
                        incomeAdapter.notifyItemRemoved(position);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(requireContext(), "Failed to delete income: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
