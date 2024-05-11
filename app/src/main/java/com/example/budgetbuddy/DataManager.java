package com.example.budgetbuddy;

import com.example.budgetbuddy.expense.Expense;
import com.example.budgetbuddy.income.Income;

import java.util.ArrayList;
import java.util.List;

public class DataManager {
    private static DataManager instance;
    private List<Income> incomeList;
    private List<Expense> expenseList;

    private DataManager() {
        incomeList = new ArrayList<>();
        expenseList = new ArrayList<>();
    }

    public static DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
        }
        return instance;
    }

    public List<Income> getIncomeList() {
        return incomeList;
    }

    public List<Expense> getExpenseList() {
        return expenseList;
    }

    public void clearIncomeList() {
        incomeList.clear();
    }

    public void clearExpenseList() {
        expenseList.clear();
    }
}
