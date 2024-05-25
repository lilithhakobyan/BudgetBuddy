package com.example.budgetbuddy;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.budgetbuddy.expense.Expense;
import com.example.budgetbuddy.income.Income;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatisticsFragment extends Fragment {

    private PieChart pieChart;
    private SharedViewModel sharedViewModel;
    private Map<String, Float> conversionRates;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_statistics, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        pieChart = view.findViewById(R.id.pieChart);

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        // Initialize conversion rates (example rates, replace with actual data or API calls)
        conversionRates = new HashMap<>();
        conversionRates.put("USD_TO_AMD", 470.0f);
        conversionRates.put("EUR_TO_AMD", 520.0f);
        conversionRates.put("AMD_TO_AMD", 1.0f);  // Base currency
        // Add more rates as needed

        sharedViewModel.getIncomeList().observe(getViewLifecycleOwner(), new Observer<List<Income>>() {
            @Override
            public void onChanged(List<Income> incomes) {
                float totalIncome = calculateTotalIncome(incomes, "AMD");
                float totalExpenses = calculateTotalExpenses(sharedViewModel.getExpenseList().getValue(), "AMD");
                populatePieChart(totalIncome, totalExpenses);
            }
        });

        sharedViewModel.getExpenseList().observe(getViewLifecycleOwner(), new Observer<List<Expense>>() {
            @Override
            public void onChanged(List<Expense> expenses) {
                float totalIncome = calculateTotalIncome(sharedViewModel.getIncomeList().getValue(), "AMD");
                float totalExpenses = calculateTotalExpenses(expenses, "AMD");
                populatePieChart(totalIncome, totalExpenses);
            }
        });
    }

    private void setupPieChart() {
        Legend legend = pieChart.getLegend();
        legend.setTextSize(40f);
    }

    @Override
    public void onResume() {
        super.onResume();
        setupPieChart();
    }

    private float calculateTotalIncome(List<Income> incomes, String targetCurrency) {
        float totalIncome = 0;
        if (incomes != null) {
            for (Income income : incomes) {
                float convertedAmount = convertCurrency((double) income.getAmount(), income.getCurrency(), targetCurrency);
                totalIncome += convertedAmount;
            }
        }
        return totalIncome;
    }

    private float calculateTotalExpenses(List<Expense> expenses, String targetCurrency) {
        float totalExpenses = 0;
        if (expenses != null) {
            for (Expense expense : expenses) {
                float convertedAmount = convertCurrency((double) expense.getAmount(), expense.getCurrency(), targetCurrency);
                totalExpenses += convertedAmount;
            }
        }
        return totalExpenses;
    }

    private float convertCurrency(double amount, String fromCurrency, String toCurrency) {
        String conversionKey = fromCurrency + "_TO_" + toCurrency;
        Float conversionRate = conversionRates.get(conversionKey);

        if (conversionRate != null) {
            return (float) (amount * conversionRate);
        } else {
            // Handle missing conversion rate (e.g., log an error, throw an exception, or return a default value)
            // For simplicity, let's assume the conversion rate is 1 if not found
            return (float) amount;
        }
    }

    private void populatePieChart(float income, float expenses) {
        List<PieEntry> entries = new ArrayList<>();
        float total = income + expenses;

        if (total > 0) {
            if (income > 0) {
                entries.add(new PieEntry(income / total * 100, "Incomes"));
            }
            if (expenses > 0) {
                entries.add(new PieEntry(expenses / total * 100, "Expenses"));
            }
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(new int[]{getResources().getColor(R.color.dark_blue),
                getResources().getColor(R.color.main)});
        PieData data = new PieData(dataSet);

        dataSet.setDrawValues(true);
        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(Color.WHITE);

        pieChart.setData(data);
        dataSet.setDrawValues(false);
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText("Incomes and Expenses");
        pieChart.animateY(1000);
        pieChart.invalidate();

        Legend legend = pieChart.getLegend();
        legend.setTextSize(12f);
        legend.setXEntrySpace(20f);

        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                if (e instanceof PieEntry) {
                    PieEntry pieEntry = (PieEntry) e;
                    if (pieEntry.getLabel().equals("Incomes")) {
                        openIncomeCategoriesPieChart();
                    } else if (pieEntry.getLabel().equals("Expenses")) {
                        openExpenseCategoriesPieChart();
                    }
                }
            }

            @Override
            public void onNothingSelected() {
            }
        });

        pieChart.setNoDataTextColor(Color.RED);
        pieChart.setNoDataTextTypeface(Typeface.DEFAULT_BOLD);
    }

    private void openIncomeCategoriesPieChart() {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        IncomePieChart fragment = new IncomePieChart();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void openExpenseCategoriesPieChart() {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        ExpensePieChart fragment = new ExpensePieChart();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
