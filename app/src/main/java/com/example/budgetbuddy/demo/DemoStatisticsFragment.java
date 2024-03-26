package com.example.budgetbuddy.demo;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.budgetbuddy.PieChartView;
import com.example.budgetbuddy.R;

public class DemoStatisticsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistics, container, false);


        PieChartView pieChartView = view.findViewById(R.id.pieChartView);

        // Set the data for the pie chart
        float income = 500; // Example income value
        float expense = 300; // Example expense value
        pieChartView.setData(income, expense);

        return view;
    }
}
