package com.example.budgetbuddy.reminder;

import java.io.Serializable;

public class ReminderClass implements Serializable {
    private String title;
    private String message;
    private long dateTime;
    private String amount;
    private String choice;
    private boolean settled;

    private String uniqueId;
    private String documentId;

    public ReminderClass() {
    }

    public ReminderClass(String title, long dateTime, String amount, String choice, boolean settled) {
        this.title = title;
        this.dateTime = dateTime;
        this.amount = amount;
        this.choice = choice;
        this.settled = settled;

        // Generate unique ID based on title and timestamp
        this.uniqueId = generateUniqueId(title, dateTime);
        this.documentId = "";
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }


    private String generateUniqueId(String title, long dateTime) {
        return title + "_" + dateTime;
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

    public String getUniqueId() {
        return uniqueId;
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

    public String getId() {
        return uniqueId;
    }

    public String getMessage() {
        return message;
    }
}
