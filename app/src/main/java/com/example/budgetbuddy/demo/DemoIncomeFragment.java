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
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class DemoIncomeFragment extends Fragment {
    private RecyclerView recyclerView;
    private MyListAdapter adapter;
    private List<ListItem> itemList;
    Button incomeDemoButton;

    FloatingActionButton floatingActionButton;

    public DemoIncomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_demo_income, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_d);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        floatingActionButton = view.findViewById(R.id.add_income_floatingActionButton_d);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container_demo, new DemoAddIncomeFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        itemList = new ArrayList<>();


        itemList.add(new ListItem("Salary", "Monthly income", R.drawable.i, "+ $4000", "Income"));


        adapter = new MyListAdapter(getContext(), itemList);

        recyclerView.setAdapter(adapter);
        return view;
    }
}