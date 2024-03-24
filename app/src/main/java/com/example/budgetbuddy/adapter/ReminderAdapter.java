package com.example.budgetbuddy.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.budgetbuddy.R;
import com.example.budgetbuddy.ReminderClass;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReminderAdapter extends ArrayAdapter<ReminderClass> {

    private List<ReminderClass> reminderList;
    private Context context;

    public ReminderAdapter(@NonNull Context context, int resource, @NonNull List<ReminderClass> objects) {
        super(context, resource, objects);
        this.context = context;
        this.reminderList = objects;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_layout, parent, false);
        }

        final ReminderClass reminder = getItem(position);

        TextView textTitle = convertView.findViewById(R.id.textTitle);
        TextView textDate = convertView.findViewById(R.id.textDate);
        TextView textTime = convertView.findViewById(R.id.textTime);

        if (reminder != null) {
            textTitle.setText(reminder.getTitle());
            textDate.setText(formatDate(reminder.getDateTime()));
            textTime.setText(formatTime(reminder.getDateTime()));
        }

        return convertView;
    }

    private String formatDate(long dateTime) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());
        return dateFormat.format(new Date(dateTime));
    }

    private String formatTime(long dateTime) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        return timeFormat.format(new Date(dateTime));
    }
}
