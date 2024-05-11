package com.example.budgetbuddy.demo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budgetbuddy.ListItem;
import com.example.budgetbuddy.R;
import com.example.budgetbuddy.adapter.MyListAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class DemoExpenseFragment extends Fragment {
    private RecyclerView recyclerView;
    private MyListAdapter adapter;
    private List<ListItem> itemList;

    public DemoExpenseFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_demo_expense, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_expense_d);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        FloatingActionButton fab = view.findViewById(R.id.add_expense_floatingActionButton_d);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container_demo, new DemoAddExpenseFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });


        itemList = new ArrayList<>();

        itemList.add(new ListItem("Rent", "Monthly rent payment", R.drawable.e, "- $1000", "Expense"));
        itemList.add(new ListItem("Utilities", "Monthly utilities payment", R.drawable.e, "- $300", "Expense"));


        adapter = new MyListAdapter(getContext(), itemList);

        recyclerView.setAdapter(adapter);

        return view;
    }
}