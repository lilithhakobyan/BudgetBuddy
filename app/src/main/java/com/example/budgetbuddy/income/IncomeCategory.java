package com.example.budgetbuddy.income;

import com.example.budgetbuddy.R;

public enum IncomeCategory {
    SALARY("Salary", R.drawable.salary),
    FREELANCE("Freelance", R.drawable.baseline_computer_24),
    OTHER("Other", R.drawable.select_icon);

    private final String categoryName;
    private final int categoryIcon;

    IncomeCategory(String categoryName, int categoryIcon) {
        this.categoryName = categoryName;
        this.categoryIcon = categoryIcon;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public int getCategoryIcon() {
        return categoryIcon;
    }
}

