package com.example.budgetbuddy.demo;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.budgetbuddy.ListItem;
import com.example.budgetbuddy.R;
import com.example.budgetbuddy.adapter.MyListAdapter;

import java.util.ArrayList;
import java.util.List;

public class DemoHomeFragment extends Fragment {
    private ListView listView;

    public DemoHomeFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_demo_home, container, false);

        listView = view.findViewById(R.id.list_view);

        String[] items = {"Car", "Salary", "Travelling"};
        String[] descriptions = {"Expense", "Income", "Expense"};
        int[] icons = {R.drawable.baseline_account_balance_wallet_24, R.drawable.money, R.drawable.baseline_account_balance_wallet_24};
        String[] amount = {"- 50,000 USD", "+ 4000 USD" , "- 1000 USD"};

        List<ListItem> itemList = new ArrayList<>();
        for (int i = 0; i < items.length; i++) {
            itemList.add(new ListItem(items[i], descriptions[i], icons[i], amount[i]));
        }

        MyListAdapter adapter = new MyListAdapter(getContext(), itemList);
        listView.setAdapter(adapter);

        return view;
    }
}
