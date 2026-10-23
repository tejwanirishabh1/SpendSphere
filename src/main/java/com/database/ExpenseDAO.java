package com.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

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
}
