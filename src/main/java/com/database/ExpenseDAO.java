package com.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import com.expense.Expense;

public class ExpenseDAO {

    public void createTable() throws SQLException {

        String sql = """
                CREATE TABLE IF NOT EXISTS expenses (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    title TEXT NOT NULL,
                    amount REAL NOT NULL,
                    category TEXT NOT NULL,
                    date TEXT NOT NULL
                )
                """;

        try (Connection connection = DatabaseManager.getConnection();
             Statement statement = connection.createStatement()) {

            statement.execute(sql);
            System.out.println("Expenses table is ready!");
        }
    }
    
    public void addExpense(Expense expense) throws SQLException {

        String sql = """
                INSERT INTO expenses (title, amount, category, date)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, expense.getTitle());
            statement.setDouble(2, expense.getAmount());
            statement.setString(3, expense.getCategory());
            statement.setString(4, expense.getDate().toString());

            statement.executeUpdate();

            System.out.println("Expense saved successfully!");
        }
    }

}
