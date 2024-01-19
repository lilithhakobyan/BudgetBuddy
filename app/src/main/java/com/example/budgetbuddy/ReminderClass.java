package com.example.budgetbuddy;

import java.security.Timestamp;

public class ReminderClass {
    private String title;
    private Timestamp dateTime;

    public ReminderClass() {
    }

    public ReminderClass(String title, Timestamp dateTime) {
        this.title = title;
        this.dateTime = dateTime;
    }

    public String getTitle() {
        return title;
    }

    public Timestamp getDateTime() {
        return dateTime;
    }

    @Override
    public String toString() {
        return "Reminder{" +
                "title='" + title + '\'' +
                ", dateTime=" + dateTime +
                '}';
    }
}
