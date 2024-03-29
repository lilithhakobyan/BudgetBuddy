package com.example.budgetbuddy.expense;

import com.example.budgetbuddy.R;

public enum ExpenseCategory {
    RENT("Rent", R.drawable.rent),
    HEALTH("Healthcare", R.drawable.medical_services),
    FOOD("Food", R.drawable.food),
    TRANSPORTATION("Transportation", R.drawable.transport),
    UTILITIES("Utilities", R.drawable.utility),
    ENTERTAINMENT("Entertainment", R.drawable.theater),
    OTHER("Other", R.drawable.select_icon);

    private final String categoryName;
    private final int categoryIcon;

    ExpenseCategory(String categoryName, int categoryIcon) {
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
