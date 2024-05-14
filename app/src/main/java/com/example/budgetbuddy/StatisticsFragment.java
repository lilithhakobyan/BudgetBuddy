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
import java.util.List;

public class StatisticsFragment extends Fragment {

    private PieChart pieChart;
    private SharedViewModel sharedViewModel;

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

        sharedViewModel.getIncomeList().observe(getViewLifecycleOwner(), new Observer<List<Income>>() {
            @Override
            public void onChanged(List<Income> incomes) {
                float totalIncome = calculateTotalIncome(incomes);
                float totalExpenses = calculateTotalExpenses(sharedViewModel.getExpenseList().getValue());
                populatePieChart(totalIncome, totalExpenses);
            }
        });

        sharedViewModel.getExpenseList().observe(getViewLifecycleOwner(), new Observer<List<Expense>>() {
            @Override
            public void onChanged(List<Expense> expenses) {
                float totalIncome = calculateTotalIncome(sharedViewModel.getIncomeList().getValue());
                float totalExpenses = calculateTotalExpenses(expenses);
                populatePieChart(totalIncome, totalExpenses);
            }
        });
    }

    private void setupPieChart() {
        Legend legend = pieChart.getLegend();
        legend.setTextSize(20f);
    }

    @Override
    public void onResume() {
        super.onResume();
        setupPieChart();
    }


    private float calculateTotalIncome(List<Income> incomes) {
        float totalIncome = 0;
        if (incomes != null) {
            for (Income income : incomes) {
                totalIncome += income.getAmount();
            }
        }
        return totalIncome;
    }

    private float calculateTotalExpenses(List<Expense> expenses) {
        float totalExpenses = 0;
        if (expenses != null) {
            for (Expense expense : expenses) {
                totalExpenses += expense.getAmount();
            }
        }
        return totalExpenses;
    }

    private void populatePieChart(float income, float expenses) {
        List<PieEntry> entries = new ArrayList<>();
        if (income > 0) {
            entries.add(new PieEntry(income, "Incomes"));
        }
        if (expenses > 0) {
            entries.add(new PieEntry(expenses, "Expenses"));
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(new int[]{getResources().getColor(R.color.dark_blue),
                getResources().getColor(R.color.main)});
        PieData data = new PieData(dataSet);

        dataSet.setDrawValues(false);

        pieChart.setData(data);
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText("Incomes and Expenses");
        pieChart.animateY(1000);
        pieChart.invalidate();

        Legend legend = pieChart.getLegend();
        legend.setTextSize(13f);
        legend.setXEntrySpace(20f);

        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                if (e instanceof PieEntry) {
                    PieEntry pieEntry = (PieEntry) e;
                    if (pieEntry.getLabel().equals("Incomes")) {
                        openIncomeCategoriesPieChart();
                    }else if (pieEntry.getLabel().equals("Expenses")) {
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
