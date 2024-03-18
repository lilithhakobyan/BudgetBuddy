package com.example.budgetbuddy.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.budgetbuddy.R;
import com.example.budgetbuddy.ReminderClass;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ReminderAdapter extends ArrayAdapter<ReminderClass> {

    private Context context;
    private int resource;

    public ReminderAdapter(Context context, int resource, List<ReminderClass> reminders) {
        super(context, resource, reminders);
        this.context = context;
        this.resource = resource;
    }

    private static class ViewHolder {
        TextView titleTextView;
        TextView dateTimeTextView;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(resource, parent, false);

            holder = new ViewHolder();
            holder.titleTextView = convertView.findViewById(R.id.describeReminderTextView);
            holder.dateTimeTextView = convertView.findViewById(R.id.dateRemText);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ReminderClass currentReminder = getItem(position);

        if (currentReminder != null) {
            holder.titleTextView.setText(currentReminder.getTitle());

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            String formattedDateTime = sdf.format(currentReminder.getDateTime());
            holder.dateTimeTextView.setText(formattedDateTime);
        }

        return convertView;
    }
}