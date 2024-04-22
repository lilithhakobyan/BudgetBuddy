package com.example.budgetbuddy;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budgetbuddy.adapter.CombinedAdapter;
import com.example.budgetbuddy.adapter.ExpenseAdapter;
import com.example.budgetbuddy.adapter.IncomeAdapter;
import com.example.budgetbuddy.expense.Expense;
import com.example.budgetbuddy.expense.ExpenseFragment;
import com.example.budgetbuddy.income.Income;
import com.example.budgetbuddy.income.IncomeFragment;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private SharedViewModel sharedViewModel;
    private RecyclerView recyclerView;
    private CombinedAdapter combinedAdapter;
    private IncomeAdapter incomeAdapter;
    private ExpenseAdapter expenseAdapter;
    private TextView balanceTextView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        combinedAdapter = new CombinedAdapter(new ArrayList<>());
        recyclerView.setAdapter(combinedAdapter);
        balanceTextView = view.findViewById(R.id.balanceTextView);

        Button incomeButton = view.findViewById(R.id.income);
        Button expenseButton = view.findViewById(R.id.expense);

        incomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity activity = (MainActivity) getActivity();
                if (activity != null) {
                    activity.loadFragment(new IncomeFragment(), false);
                }
            }
        });

        expenseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity activity = (MainActivity) getActivity();
                if (activity != null) {
                    activity.loadFragment(new ExpenseFragment(), false);
                }
            }
        });

        IncomeFragment incomeFragment = new IncomeFragment();
        incomeFragment.fetchIncomeData(); // Call fetchIncomeData() from IncomeFragment
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Observe changes in income and expense lists and update the adapter accordingly
        sharedViewModel.getIncomeList().observe(getViewLifecycleOwner(), incomes -> {
            updateCombinedList();
            updateBalance();
        });

        sharedViewModel.getExpenseList().observe(getViewLifecycleOwner(), expenses -> {
            updateCombinedList();
            updateBalance();
        });
    }

    private void updateCombinedList() {
        List<Object> combinedList = new ArrayList<>();
        List<Income> incomes = sharedViewModel.getIncomeList().getValue();
        List<Expense> expenses = sharedViewModel.getExpenseList().getValue();

        if (incomes != null) {
            combinedList.addAll(incomes);
        }
        if (expenses != null) {
            combinedList.addAll(expenses);
        }

        combinedAdapter.updateItems(combinedList);
    }

    private void updateBalance() {
        double totalIncome = calculateTotalIncome();
        double totalExpense = calculateTotalExpense();
        double balance = totalIncome - totalExpense;
        // Format balance to two decimal places
        String formattedBalance = String.format("%.2f", balance);

        // Log the calculated balance
        Log.d("Balance", "Total Income: " + totalIncome + ", Total Expense: " + totalExpense + ", Balance: " + formattedBalance);

        balanceTextView.setText("Balance: " + formattedBalance);
    }


    private double calculateTotalIncome() {
        double totalIncome = 0.0;
        List<Income> incomes = sharedViewModel.getIncomeList().getValue();
        if (incomes != null) {
            for (Income income : incomes) {
                totalIncome += income.getAmount();
            }
        }
        return totalIncome;
    }

    private double calculateTotalExpense() {
        double totalExpense = 0.0;
        List<Expense> expenses = sharedViewModel.getExpenseList().getValue();
        if (expenses != null) {
            for (Expense expense : expenses) {
                totalExpense += expense.getAmount();
            }
        }
        return totalExpense;
    }
}