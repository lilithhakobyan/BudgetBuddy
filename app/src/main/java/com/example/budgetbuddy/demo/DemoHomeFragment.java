package com.example.budgetbuddy.demo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budgetbuddy.ListItem;
import com.example.budgetbuddy.R;
import com.example.budgetbuddy.adapter.MyListAdapter;

import java.util.ArrayList;
import java.util.List;

public class DemoHomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private MyListAdapter adapter;
    private List<ListItem> itemList;
    Button incomeDemoButton;
    Button expenseDemoButton;

    public DemoHomeFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_demo_home, container, false);

        incomeDemoButton = view.findViewById(R.id.income_demo);
        expenseDemoButton = view.findViewById(R.id.expense_demo);
        recyclerView = view.findViewById(R.id.recycler_view_demo);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        incomeDemoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DemoActivity activity = (DemoActivity) getActivity();
                if (activity != null) {
                    activity.loadFragment(new DemoIncomeFragment(), false);
                }
            }
        });

        expenseDemoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DemoActivity activity = (DemoActivity) getActivity();
                if (activity != null) {
                    activity.loadFragment(new DemoExpenseFragment(), false);
                }
            }
        });


        itemList = new ArrayList<>();


        itemList.add(new ListItem("Salary", "Monthly income", R.drawable.i, "+ $4000", "Income"));
        itemList.add(new ListItem("Rent", "Monthly rent payment", R.drawable.e, "- $1000", "Expense"));
        itemList.add(new ListItem("Utilities", "Monthly utilities payment", R.drawable.e, "- $300", "Expense"));


        adapter = new MyListAdapter(getContext(), itemList);

        recyclerView.setAdapter(adapter);

        return view;
    }
}
