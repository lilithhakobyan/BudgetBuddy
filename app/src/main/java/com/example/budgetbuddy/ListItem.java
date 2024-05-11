package com.example.budgetbuddy;

public class ListItem {
    private String title;
    private String description;
    private int icon;
    private String amount;
    private String category;

    public ListItem(String title, String description, int icon, String amount, String category) {
        this.title = title;
        this.description = description;
        this.icon = icon;
        this.amount = amount;
        this.category = category;
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
    public String getCategory() {
        return category;
    }
}
