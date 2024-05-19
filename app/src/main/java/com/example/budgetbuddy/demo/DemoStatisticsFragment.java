package com.example.budgetbuddy.demo;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.budgetbuddy.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.List;

public class DemoStatisticsFragment extends Fragment {

    private PieChart pieChart;

    public DemoStatisticsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_statistics, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        pieChart = view.findViewById(R.id.pieChart);

        float expenseAmount = 1000f;
        float incomeAmount = 4000f;

        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(expenseAmount, "Expenses"));
        entries.add(new PieEntry(incomeAmount, "Incomes"));

        int[] colors = {getResources().getColor(R.color.main), getResources().getColor(R.color.dark_blue)};
        PieDataSet dataSet = new PieDataSet(entries, "Incomes and Expenses");
        dataSet.setColors(colors);


        dataSet.setDrawValues(false);

        PieData data = new PieData(dataSet);
        pieChart.setData(data);

        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText("Expenses and Incomes");
        pieChart.animateY(1000);

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
    }

    private void openIncomeCategoriesPieChart() {
        float salaryAmount = 4000f;
        List<PieEntry> entries = new ArrayList<>();

        entries.add(new PieEntry(salaryAmount, "Salary"));

        int[] colors = {Color.rgb(168, 206, 255)};

        PieDataSet dataSet = new PieDataSet(entries, "Incomes");
        dataSet.setColors(colors);
        PieData data = new PieData(dataSet);
        pieChart.setData(data);
        pieChart.invalidate();
    }


    private void openExpenseCategoriesPieChart() {
        float rentAmount = 1000f;
        float utilitiesAmount = 300f;

        List<PieEntry> entries = new ArrayList<>();

        entries.add(new PieEntry(rentAmount, "Rent"));
        entries.add(new PieEntry(utilitiesAmount, "Utilities"));

        int[] expenseColors = {
                Color.rgb(255, 204, 102),
                Color.rgb(255, 255, 102)
        };

        PieDataSet dataSet = new PieDataSet(entries, "Expenses");
        dataSet.setColors(expenseColors);
        PieData data = new PieData(dataSet);
        pieChart.setData(data);

        pieChart.invalidate();
    }

    @Override
    public void onResume() {
        super.onResume();
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                getParentFragmentManager().popBackStack();
            }
        });

    }
}
