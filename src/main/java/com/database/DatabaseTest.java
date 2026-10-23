package com.database;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseTest {

    public static void main(String[] args) {

        try (Connection connection = DatabaseManager.getConnection()) {

            if (connection != null && !connection.isClosed()) {
                System.out.println("================================");
                System.out.println("   SpendSphere Database Test");
                System.out.println("================================");
                System.out.println("SQLite connection successful!");
                System.out.println("Database is ready.");
            }

        } catch (SQLException e) {
            System.out.println("Database connection failed!");
            e.printStackTrace();
        }
    }
}
