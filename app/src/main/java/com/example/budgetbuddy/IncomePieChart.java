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

import com.example.budgetbuddy.income.Income;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

public class IncomePieChart extends Fragment {

    private PieChart pieChart;
    private SharedViewModel sharedViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_income_pie_chart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        pieChart = view.findViewById(R.id.incomePieChart);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        sharedViewModel.getIncomeList().observe(getViewLifecycleOwner(), new Observer<List<Income>>() {
            @Override
            public void onChanged(List<Income> incomes) {
                populatePieChart(incomes);
            }
        });
    }

    private void populatePieChart(List<Income> incomes) {
        List<PieEntry> entries = new ArrayList<>();

        // Calculate total income
        float totalIncome = 0;
        for (Income income : incomes) {
            totalIncome += income.getAmount();
        }

        for (Income income : incomes) {
            // Cast double value to float here
            float percentage = (float) ((income.getAmount() / totalIncome) * 100);
            // Assuming getCategory() retrieves the category of income
            entries.add(new PieEntry(percentage, income.getCategory()));
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(ColorTemplate.PASTEL_COLORS);
        PieData data = new PieData(dataSet);

        pieChart.setData(data);
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText("Income Categories");
        pieChart.animateY(1000);
        pieChart.invalidate();

        dataSet.setDrawValues(false);

        Legend legend = pieChart.getLegend();
        legend.setTextSize(40f);
        legend.setXEntrySpace(20f);

        pieChart.setNoDataTextColor(Color.RED);
        pieChart.setNoDataTextTypeface(Typeface.DEFAULT_BOLD);
    }
}
