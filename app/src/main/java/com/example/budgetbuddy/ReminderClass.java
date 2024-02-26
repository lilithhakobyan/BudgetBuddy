package com.example.budgetbuddy;

import java.io.Serializable;
import java.security.Timestamp;

public class ReminderClass implements Serializable {
    private String title;
    private long dateTime;
    private String amount;
    private String choice;
    private boolean settled;

    public ReminderClass() {
    }

    public ReminderClass(String title, long dateTime, String amount, String choice, boolean settled ) {
        this.title = title;
        this.dateTime = dateTime;
        this.amount = amount;
        this.choice = choice;
        this.settled = settled;
    }


    public String getAmount() {
        return amount;
    }

    public String getChoice() {
        return choice;
    }

    public boolean isSettled() {
        return settled;
    }


    public String getTitle() {

        return title;
    }

    public long getDateTime() {
        return dateTime;
    }

    @Override
    public String toString() {
        return "Reminder{" +
                "title='" + title + '\'' +
                ", dateTime=" + dateTime +
                '}';
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDateTime(long dateTime) {
        this.dateTime = dateTime;
    }

    public void setSettled(boolean settled) {
        this.settled = settled;
    }
}
