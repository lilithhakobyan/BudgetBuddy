// IncomePieChartFragment.java
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

public class IncomePieChart extends Fragment {

    private PieChart pieChart;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_income_pie_chart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        pieChart = view.findViewById(R.id.incomePieChart);

        populatePieChart();
    }

    private void populatePieChart() {
        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(3000f, "Salary"));
        entries.add(new PieEntry(1500f, "Freelance"));
        entries.add(new PieEntry(1000f, "Other"));

        int[] pastelColors = new int[]{
                Color.rgb(215, 153, 194), // light pink
                Color.rgb(255, 209, 128), // light orange
                Color.rgb(168, 206, 255)  // light blue
        };

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(pastelColors);
        PieData data = new PieData(dataSet);

        pieChart.setData(data);
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText("Income Categories");
        pieChart.animateY(1000);
        pieChart.invalidate();

        // Set legend text size
        Legend legend = pieChart.getLegend();
        legend.setTextSize(13f);
        legend.setXEntrySpace(20f);

        pieChart.setNoDataTextColor(Color.RED); // Set the desired color for the text
        pieChart.setNoDataTextTypeface(Typeface.DEFAULT_BOLD); // Optionally set the text typeface
    }

}
