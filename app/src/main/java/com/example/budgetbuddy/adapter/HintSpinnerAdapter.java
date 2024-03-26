package com.example.budgetbuddy.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class HintSpinnerAdapter<T> extends ArrayAdapter<T> {

    private final LayoutInflater inflater;
    private final List<T> items;
    private final int hintResId;

    public HintSpinnerAdapter(@NonNull Context context, @NonNull List<T> items, int hintResId) {
        super(context, android.R.layout.simple_spinner_item, items);
        this.inflater = LayoutInflater.from(context);
        this.items = items;
        this.hintResId = hintResId;
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (position == 0) {
            return inflater.inflate(hintResId, parent, false);
        }
        return super.getView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (position == 0) {
            return new View(getContext()); // Hide the hint in dropdown
        }
        return super.getDropDownView(position, convertView, parent);
    }

    @Override
    public int getCount() {
        return items.size() + 1; // Include hint item
    }

    @Override
    public T getItem(int position) {
        if (position == 0) {
            return null; // Return null for the hint item
        }
        return items.get(position - 1); // Adjust index for the hint item
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}

