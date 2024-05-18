package com.example.budgetbuddy;

import android.os.Bundle;
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
import com.example.budgetbuddy.expense.Expense;
import com.example.budgetbuddy.expense.ExpenseFragment;
import com.example.budgetbuddy.income.Income;
import com.example.budgetbuddy.income.IncomeFragment;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HomeFragment extends Fragment  {
    private SharedViewModel sharedViewModel;
    private RecyclerView recyclerView;
    private CombinedAdapter combinedAdapter;
    private TextView balanceTextView;




    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        fetchIncomeData();
        fetchExpenseData();
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
                    activity.loadFragment(new IncomeFragment());
                }
            }
        });

        expenseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity activity = (MainActivity) getActivity();
                if (activity != null) {
                    activity.loadFragment(new ExpenseFragment());
                }
            }
        });

        IncomeFragment incomeFragment = new IncomeFragment();
        incomeFragment.fetchIncomeData();



        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        combinedAdapter.setOnIncomeItemClickListener(new CombinedAdapter.OnIncomeItemClickListener() {
            @Override
            public void onIncomeItemClick(Income income) {
                MainActivity activity = (MainActivity) getActivity();
                if (activity != null) {
                    activity.loadFragment(new IncomeFragment());
                }
            }
        });

        combinedAdapter.setOnExpenseItemClickListener(new CombinedAdapter.OnExpenseItemClickListener() {
            @Override
            public void onExpenseItemClick(Expense expense) {
                MainActivity activity = (MainActivity) getActivity();
                if (activity != null) {
                    activity.loadFragment(new ExpenseFragment());
                }
            }
        });
        

        sharedViewModel.getIncomeList().observe(getViewLifecycleOwner(), incomes -> {
            updateCombinedList();
            updateBalance();
            calculateTotalIncomeByCurrency();
        });

        sharedViewModel.getExpenseList().observe(getViewLifecycleOwner(), expenses -> {
            updateCombinedList();
            updateBalance();
            calculateTotalExpenseByCurrency();
        });
    }

    private void fetchIncomeData() {
        FirebaseFirestore.getInstance().collection("income")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Income> incomes = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Income income = document.toObject(Income.class);
                            incomes.add(income);
                        }
                        sharedViewModel.setIncomeList(incomes);
                    } else {
                        // Handle error
                    }
                });
    }

    private void fetchExpenseData() {
        FirebaseFirestore.getInstance().collection("expense")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Expense> expenses = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Expense expense = document.toObject(Expense.class);
                            expenses.add(expense);
                        }
                        sharedViewModel.setExpenseList(expenses); // Update ViewModel
                    } else {
                        // Handle error
                    }
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

    private Map<String, Double> calculateTotalIncomeByCurrency() {
        Map<String, Double> totalIncomeByCurrency = new HashMap<>();
        List<Income> incomes = sharedViewModel.getIncomeList().getValue();
        if (incomes != null) {
            for (Income income : incomes) {
                String currency = income.getCurrency();
                double amount = income.getAmount();

                if (totalIncomeByCurrency.containsKey(currency)) {
                    double currentTotal = totalIncomeByCurrency.get(currency);
                    totalIncomeByCurrency.put(currency, currentTotal + amount);
                } else {
                    totalIncomeByCurrency.put(currency, amount);
                }
            }
        }
        return totalIncomeByCurrency;
    }

    private Map<String, Double> calculateTotalExpenseByCurrency() {
        Map<String, Double> totalExpenseByCurrency = new HashMap<>();
        List<Expense> expenses = sharedViewModel.getExpenseList().getValue();
        if (expenses != null) {
            for (Expense expense : expenses) {
                String currency = expense.getCurrency();
                double amount = expense.getAmount();

                if (totalExpenseByCurrency.containsKey(currency)) {
                    double currentTotal = totalExpenseByCurrency.get(currency);
                    totalExpenseByCurrency.put(currency, currentTotal + amount);
                } else {
                    totalExpenseByCurrency.put(currency, amount);
                }
            }
        }
        return totalExpenseByCurrency;
    }

    private void updateBalance() {
        Map<String, Double> totalIncomeByCurrency = calculateTotalIncomeByCurrency();
        Map<String, Double> totalExpenseByCurrency = calculateTotalExpenseByCurrency();

        StringBuilder balanceText = new StringBuilder();

        for (Map.Entry<String, Double> entry : totalIncomeByCurrency.entrySet()) {
            String currency = entry.getKey();
            double totalIncome = entry.getValue();
            double totalExpense = totalExpenseByCurrency.getOrDefault(currency, 0.0);
            double balance = totalIncome - totalExpense;

            NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.getDefault());
            currencyFormatter.setCurrency(Currency.getInstance(currency));
            String formattedBalance = currencyFormatter.format(balance);

            balanceText.append(currency).append(": ").append(formattedBalance).append("\n");
        }

        balanceTextView.setText(balanceText.toString());
    }

}
