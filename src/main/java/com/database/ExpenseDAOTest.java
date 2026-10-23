
package com.database;

import java.sql.SQLException;

public class ExpenseDAOTest {

    public static void main(String[] args) {

        ExpenseDAO expenseDAO = new ExpenseDAO();

        try {
            expenseDAO.createTable();

            System.out.println("================================");
            System.out.println("   SpendSphere Database Test");
            System.out.println("================================");
            System.out.println("Expenses table created successfully!");
            System.out.println("Database setup is complete.");

        } catch (SQLException e) {
            System.out.println("Failed to create expenses table!");
            e.printStackTrace();
        }
    }
}
