package com.example.budgetbuddy;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.budgetbuddy.R;
import com.example.budgetbuddy.SharedViewModel;
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
    private IncomeAdapter incomeAdapter;
    private ExpenseAdapter expenseAdapter;

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

        incomeAdapter = new IncomeAdapter(new ArrayList<>());
        recyclerView.setAdapter(incomeAdapter);


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

        sharedViewModel.getIncomeList().observe(getViewLifecycleOwner(), new Observer<List<Income>>() {
            @Override
            public void onChanged(List<Income> incomes) {
                incomeAdapter.setIncomes(incomes);
                incomeAdapter.notifyDataSetChanged();
            }
        });

        sharedViewModel.getExpenseList().observe(getViewLifecycleOwner(), new Observer<List<Expense>>() {
            @Override
            public void onChanged(List<Expense> expenses) {
                expenseAdapter.setExpenses(expenses); // Update expense list in the adapter
            }
        });

        return view;
    }
}
