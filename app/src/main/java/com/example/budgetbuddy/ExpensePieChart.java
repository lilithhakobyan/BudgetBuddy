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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.budgetbuddy.expense.Expense;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExpensePieChart extends Fragment {

    private PieChart pieChart;
    private SharedViewModel sharedViewModel;
    private Map<String, Float> conversionRates;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_expense_pie_chart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        pieChart = view.findViewById(R.id.expensePieChart);

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        // Initialize conversion rates (example rates, replace with actual data or API calls)
        conversionRates = new HashMap<>();
        conversionRates.put("USD_TO_AMD", 470.0f);
        conversionRates.put("EUR_TO_AMD", 520.0f);
        conversionRates.put("AMD_TO_AMD", 1.0f);  // Base currency
        // Add more rates as needed

        sharedViewModel.getExpenseList().observe(getViewLifecycleOwner(), new Observer<List<Expense>>() {
            @Override
            public void onChanged(List<Expense> expenses) {
                populatePieChart(expenses);
            }
        });
    }

    private void populatePieChart(List<Expense> expenses) {
        Map<String, Float> categoryTotals = new HashMap<>();

        for (Expense expense : expenses) {
            float amount = (float) expense.getAmount();
            String category = expense.getCategory();
            float convertedAmount = convertCurrency(amount, expense.getCurrency(), "AMD");
            categoryTotals.put(category, categoryTotals.getOrDefault(category, 0f) + convertedAmount);
        }


        List<PieEntry> entries = new ArrayList<>();
        for (Map.Entry<String, Float> entry : categoryTotals.entrySet()) {
            entries.add(new PieEntry(entry.getValue(), entry.getKey()));
        }

        // Define pastel colors
        int[] pastelColors = new int[]{
                Color.rgb(215, 153, 194), // light pink
                Color.rgb(255, 209, 128), // light orange
                Color.rgb(168, 206, 255), // light blue
                Color.rgb(255, 159, 128), // light red
                Color.rgb(179, 224, 196), // light green
                Color.rgb(255, 204, 188), // light yellow
                Color.rgb(204, 204, 255)  // light purple
        };

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(pastelColors);
        PieData data = new PieData(dataSet);

        pieChart.setData(data);
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText("Expense Categories");
        pieChart.animateY(1000);
        pieChart.invalidate();

        dataSet.setDrawValues(false);

        Legend legend = pieChart.getLegend();
        legend.setTextSize(40f);
        legend.setXEntrySpace(20f);

        pieChart.setNoDataTextColor(Color.RED);
        pieChart.setNoDataTextTypeface(Typeface.DEFAULT_BOLD);
    }

    private float convertCurrency(float amount, String fromCurrency, String toCurrency) {
        String conversionKey = fromCurrency + "_TO_" + toCurrency;
        Float conversionRate = conversionRates.get(conversionKey);

        if (conversionRate != null) {
            return amount * conversionRate;
        } else {
            // Handle missing conversion rate (e.g., log an error, throw an exception, or return a default value)
            // For simplicity, let's assume the conversion rate is 1 if not found
            return amount;
        }
    }
}
