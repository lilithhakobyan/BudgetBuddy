package com.example.budgetbuddy.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budgetbuddy.R;
import com.example.budgetbuddy.expense.Expense;

import java.util.List;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {

    private List<Expense> expenseList;
    private OnDeleteClickListener deleteClickListener;

    public ExpenseAdapter(List<Expense> expenseList) {
        this.expenseList = expenseList;
    }

    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.deleteClickListener = listener;
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_expense, parent, false);
        return new ExpenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        Expense expense = expenseList.get(position);
        holder.bind(expense);
    }

    @Override
    public int getItemCount() {
        return expenseList.size();
    }

    public class ExpenseViewHolder extends RecyclerView.ViewHolder {
        private TextView categoryTextView;
        private TextView amountTextView;
        private TextView descriptionTextView;
        private ImageView delete;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryTextView = itemView.findViewById(R.id.category_text_view);
            amountTextView = itemView.findViewById(R.id.amount_text_view);
            descriptionTextView = itemView.findViewById(R.id.description_text_view);
            delete = itemView.findViewById(R.id.delete_expense);

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (deleteClickListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            deleteClickListener.onDeleteClick(position);
                        }
                    }
                }
            });
        }

        public void bind(Expense expense) {
            categoryTextView.setText(expense.getCategory());
            amountTextView.setText(String.valueOf(expense.getAmount()));
            descriptionTextView.setText(expense.getDescription());
        }
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(int position);
    }
}
