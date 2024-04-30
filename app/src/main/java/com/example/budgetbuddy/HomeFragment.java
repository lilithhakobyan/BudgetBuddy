package com.example.budgetbuddy;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
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

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HomeFragment extends Fragment implements CurrencyUtils.CurrencyFetchListener {
    private SharedViewModel sharedViewModel;
    private RecyclerView recyclerView;
    private CombinedAdapter combinedAdapter;
    private IncomeAdapter incomeAdapter;
    private ExpenseAdapter expenseAdapter;
    private TextView balanceTextView;
    private Spinner currencySpinner;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        // Fetch currencies when fragment is created
        CurrencyUtils.fetchCurrencies(requireContext(), this);
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
        currencySpinner = view.findViewById(R.id.balanceSpinner);

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
        incomeFragment.fetchIncomeData();



        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        currencySpinner = view.findViewById(R.id.balanceSpinner);

        CurrencyUtils.fetchCurrencies(requireContext(), this);

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



    @Override
    public void onCurrencyFetchSuccess(List<String> currencies) {
        ArrayAdapter<String> currencyAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, currencies);
        currencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        currencySpinner.setAdapter(currencyAdapter);

        if (currencySpinner.getSelectedItem() == null && currencies.size() > 0) {
            currencySpinner.setSelection(0);
        }

        updateBalance();
    }

}
