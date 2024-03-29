package com.example.budgetbuddy.expense;

public class Expense {
    private String id;
    private double amount;
    private String category;
    private String description;
    private String currency;
    public Expense() {

    }


    public Expense(double amount, String category, String description, String currency) {
        this.amount = amount;
        this.category = category;
        this.description = description;
        this.currency = currency;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}