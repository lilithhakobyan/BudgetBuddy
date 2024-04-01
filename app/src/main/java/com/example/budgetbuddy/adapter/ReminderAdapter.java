package com.example.budgetbuddy.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budgetbuddy.R;
import com.example.budgetbuddy.reminder.ReminderClass;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder> {

    private List<ReminderClass> reminderList;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public ReminderAdapter(Context context, List<ReminderClass> objects) {
        this.context = context;
        this.reminderList = objects;
    }

    public interface OnItemClickListener {
        void onItemClick(ReminderClass reminder);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public ReminderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_layout, parent, false);
        return new ReminderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReminderViewHolder holder, int position) {
        ReminderClass reminder = reminderList.get(position);
        holder.textTitle.setText(reminder.getTitle());
        holder.textDate.setText(formatDate(reminder.getDateTime()));
        holder.textTime.setText(formatTime(reminder.getDateTime()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(reminder);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return reminderList.size();
    }

    private String formatDate(long dateTime) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());
        return dateFormat.format(new Date(dateTime));
    }

    private String formatTime(long dateTime) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        return timeFormat.format(new Date(dateTime));
    }

    public class ReminderViewHolder extends RecyclerView.ViewHolder {
        public TextView textTitle;
        public TextView textDate;
        public TextView textTime;

        public ReminderViewHolder(View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.textTitle);
            textDate = itemView.findViewById(R.id.textDate);
            textTime = itemView.findViewById(R.id.textTime);
        }
    }
}
