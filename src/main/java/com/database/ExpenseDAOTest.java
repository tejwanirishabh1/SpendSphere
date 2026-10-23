
package com.database;

import java.sql.SQLException;
import java.time.LocalDate;
import com.expense.Expense;

public class ExpenseDAOTest {

    public static void main(String[] args) {

        ExpenseDAO expenseDAO = new ExpenseDAO();

        try {
            expenseDAO.createTable();

            Expense expense = new Expense(
                    "Lunch",
                    250.00,
                    "Food",
                    LocalDate.now()
            );

            expenseDAO.addExpense(expense);
            
            System.out.println("\nSaved expenses:");

            for (Expense savedExpense : expenseDAO.getAllExpenses()) {
                System.out.println(
                        savedExpense.getTitle()
                        + " | Rs. " + savedExpense.getAmount()
                        + " | " + savedExpense.getCategory()
                        + " | " + savedExpense.getDate()
                );
            }


            System.out.println("================================");
            System.out.println("   SpendSphere Expense Test");
            System.out.println("================================");
            System.out.println("Title: Lunch");
            System.out.println("Amount: Rs. 250.00");
            System.out.println("Category: Food");
            System.out.println("Expense saved to SQLite!");

        } catch (SQLException e) {
            System.out.println("Expense test failed!");
            e.printStackTrace();
        }
    }
}
