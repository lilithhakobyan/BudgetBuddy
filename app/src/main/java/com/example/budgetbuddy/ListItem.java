package com.example.budgetbuddy;

public class ListItem {
    private String title;
    private String description;
    private int icon;
    private String amount;

    public ListItem(String title, String description, int icon, String amount) {
        this.title = title;
        this.description = description;
        this.icon = icon;
        this.amount = amount;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getIcon() {
        return icon;
    }

    public String getAmount() {
        return amount;
    }
}
