package com.example.budgetbuddy.income;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.budgetbuddy.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class IncomeFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_income, container, false);

        FloatingActionButton fab = view.findViewById(R.id.add_income_floatingActionButton);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new AddIncomeFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });


        return view;

    }
}