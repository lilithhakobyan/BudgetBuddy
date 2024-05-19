package com.example.budgetbuddy.demo;

import static com.example.budgetbuddy.currency.CurrencyUtils2.fetchCurrencies;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.budgetbuddy.R;
import com.example.budgetbuddy.income.IncomeCategory;

import java.util.List;

public class DemoAddIncomeFragment extends Fragment {

    private ImageView closeD;
    private String selectedCurrency;
    private IncomeCategory selectedCategoryDemo;
    private Button save_d;
    ConstraintLayout incomeCategoryLayoutDemo;


    public DemoAddIncomeFragment() {

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_demo_add_income, container, false);

        closeD = view.findViewById(R.id.close_income_demo);
        Spinner spinner = view.findViewById(R.id.currency_spinner_d);

        List<String> currencies = fetchCurrencies();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), R.layout.spinner_item, currencies);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCurrency = currencies.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedCurrency = null;
            }
        });


        closeD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        save_d = view.findViewById(R.id.button_d);
        incomeCategoryLayoutDemo = view.findViewById(R.id.income_category_d);

        save_d.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(requireContext(), "Please log in to save income", Toast.LENGTH_SHORT).show();
                getParentFragmentManager().popBackStack();
            }
        });

        incomeCategoryLayoutDemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(requireContext(), "Please log in to select category", Toast.LENGTH_SHORT).show();
            }
        });
    }
}