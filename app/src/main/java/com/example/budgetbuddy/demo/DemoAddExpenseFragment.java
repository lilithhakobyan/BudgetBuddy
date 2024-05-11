package com.example.budgetbuddy.demo;

import static com.example.budgetbuddy.currency.CurrencyUtils2.fetchCurrencies;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.budgetbuddy.R;

import java.util.List;

public class DemoAddExpenseFragment extends Fragment {

    private String selectedCurrency;
    private ImageView closeD;
    LinearLayout expenseCategoryLayoutDemo;

    public DemoAddExpenseFragment() {

    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_demo_add_expense, container, false);

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

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        closeD = view.findViewById(R.id.close_expense_d);
        expenseCategoryLayoutDemo = view.findViewById(R.id.expense_category_d);

        closeD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();
            }
        });

        expenseCategoryLayoutDemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(requireContext(), "Please log in to select category", Toast.LENGTH_SHORT).show();
            }
        });

        
    }
}