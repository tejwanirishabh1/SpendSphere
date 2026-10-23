package com.expense;

import java.time.LocalDate;

public class Expense {

    private int id;
    private String title;
    private double amount;
    private String category;
    private LocalDate date;

    public Expense(int id, String title, double amount, String category, LocalDate date) {
        this.id = id;
        this.title = title;
        this.amount = amount;
        this.category = category;
        this.date = date;
    }

    public Expense(String title, double amount, String category, LocalDate date) {
        this(0, title, amount, category, date);
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public double getAmount() {
        return amount;
    }

    public String getCategory() {
        return category;
    }

    public LocalDate getDate() {
        return date;
    }
}
