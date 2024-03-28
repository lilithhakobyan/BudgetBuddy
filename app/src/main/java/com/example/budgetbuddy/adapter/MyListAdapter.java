package com.example.budgetbuddy.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.budgetbuddy.ListItem;
import com.example.budgetbuddy.R;

import java.util.List;

public class MyListAdapter extends ArrayAdapter<ListItem> {

    public MyListAdapter(Context context, List<ListItem> items) {
        super(context, 0, items);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        ListItem item = getItem(position);
        TextView titleTextView = convertView.findViewById(R.id.titleTextView);
        TextView descriptionTextView = convertView.findViewById(R.id.descriptionTextView);
        TextView amountTextView = convertView.findViewById(R.id.amount_d);
        ImageView iconImageView = convertView.findViewById(R.id.iconImageView);

        if (item != null) {
            titleTextView.setText(item.getTitle());
            descriptionTextView.setText(item.getDescription());
            amountTextView.setText(item.getAmount()); // Set the amount text
            iconImageView.setImageResource(item.getIcon());
        }

        return convertView;
    }
}
