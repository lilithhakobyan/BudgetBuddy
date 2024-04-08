package com.example.budgetbuddy;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.budgetbuddy.income.Income;
import com.example.budgetbuddy.expense.Expense;

import java.util.List;

public class SharedViewModel extends ViewModel {
    private MutableLiveData<List<Income>> incomeList = new MutableLiveData<>();
    private MutableLiveData<List<Expense>> expenseList = new MutableLiveData<>();


    public void setIncomeList(List<Income> incomes) {
        incomeList.setValue(incomes);
    }

    public LiveData<List<Income>> getIncomeList() {
        return incomeList;
    }

    public void removeIncome(int position) {
        List<Income> currentList = incomeList.getValue();
        if (currentList != null && position >= 0 && position < currentList.size()) {
            currentList.remove(position);
            incomeList.setValue(currentList);
        }
    }

    public void setExpenseList(List<Expense> expenses) {
        expenseList.setValue(expenses);
    }

    public LiveData<List<Expense>> getExpenseList() {
        return expenseList;
    }

    public void removeExpense(int position) {
        List<Expense> currentList = expenseList.getValue();
        if (currentList != null && position >= 0 && position < currentList.size()) {
            currentList.remove(position);
            expenseList.setValue(currentList);
        }
    }
}
