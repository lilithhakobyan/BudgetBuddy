package com.example.budgetbuddy.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budgetbuddy.R;
import com.example.budgetbuddy.expense.Expense;
import com.example.budgetbuddy.income.Income;

import java.util.List;

public class CombinedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Object> items;

    private static final int VIEW_TYPE_INCOME = 1;
    private static final int VIEW_TYPE_EXPENSE = 2;

    public CombinedAdapter(List<Object> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_TYPE_INCOME) {
            View itemView = inflater.inflate(R.layout.item_income, parent, false);
            return new IncomeViewHolder(itemView);
        } else {
            View itemView = inflater.inflate(R.layout.item_expense, parent, false);
            return new ExpenseViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Object item = items.get(position);
        if (item instanceof Income) {
            ((IncomeViewHolder) holder).bind((Income) item);
        } else if (item instanceof Expense) {
            ((ExpenseViewHolder) holder).bind((Expense) item);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        Object item = items.get(position);
        if (item instanceof Income) {
            return VIEW_TYPE_INCOME;
        } else if (item instanceof Expense) {
            return VIEW_TYPE_EXPENSE;
        }
        return super.getItemViewType(position);
    }

    public void updateItems(List<Object> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    private static class IncomeViewHolder extends RecyclerView.ViewHolder {

        private TextView amountTextView;
        private TextView categoryTextView;
        private TextView descriptionTextView;

        public IncomeViewHolder(@NonNull View itemView) {
            super(itemView);
            amountTextView = itemView.findViewById(R.id.amount_text_view);
            categoryTextView = itemView.findViewById(R.id.category_text_view);
            descriptionTextView = itemView.findViewById(R.id.description_text_view);
        }

        public void bind(Income income) {
            amountTextView.setText("Amount: " + income.getAmount());
            categoryTextView.setText("Category: " + income.getCategory());
            descriptionTextView.setText("Description: " + income.getDescription());
        }
    }


    private static class ExpenseViewHolder extends RecyclerView.ViewHolder {
        private TextView amountTextView;
        private TextView categoryTextView;
        private TextView descriptionTextView;
        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            amountTextView = itemView.findViewById(R.id.amount_text_view);
            categoryTextView = itemView.findViewById(R.id.category_text_view);
            descriptionTextView = itemView.findViewById(R.id.description_text_view);
        }

        public void bind(Expense expense) {
            amountTextView.setText("Amount: " + expense.getAmount());
            categoryTextView.setText("Category: " + expense.getCategory());
            descriptionTextView.setText("Description: " + expense.getDescription());
        }
    }
}
