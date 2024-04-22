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

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.List;

public class ExpensePieChart extends Fragment {

    private PieChart pieChart;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_expense_pie_chart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        pieChart = view.findViewById(R.id.expensePieChart);

        populatePieChart();
    }

    private void populatePieChart() {
        // Sample data for expense categories
        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(200f, "Rent"));
        entries.add(new PieEntry(150f, "Healthcare"));
        entries.add(new PieEntry(100f, "Food"));
        entries.add(new PieEntry(80f, "Transportation"));
        entries.add(new PieEntry(50f, "Utilities"));
        entries.add(new PieEntry(30f, "Entertainment"));
        entries.add(new PieEntry(20f, "Other"));

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

        // Set legend text size
        Legend legend = pieChart.getLegend();
        legend.setTextSize(13f);
        legend.setXEntrySpace(20f);

        pieChart.setNoDataTextColor(Color.RED);
        pieChart.setNoDataTextTypeface(Typeface.DEFAULT_BOLD);
    }

}
