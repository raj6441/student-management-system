package com.wallet.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private static final String DB_URL = "jdbc:h2:mem:wallet_db;DB_CLOSE_DELAY=-1;INIT=CREATE SCHEMA IF NOT EXISTS PUBLIC";
    private static final String DB_USERNAME = "sa";
    private static final String DB_PASSWORD = "";
    private static final String DB_DRIVER = "org.h2.Driver";
    
    private static DatabaseConnection instance;
    private Connection connection;
    
    private DatabaseConnection() {
        try {
            Class.forName(DB_DRIVER);
            this.connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            initializeDatabase();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to create database connection", e);
        }
    }
    
    private void initializeDatabase() {
        try (Statement stmt = connection.createStatement()) {
            // Create users table
            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "user_id INT PRIMARY KEY AUTO_INCREMENT," +
                    "username VARCHAR(50) UNIQUE NOT NULL," +
                    "email VARCHAR(100) UNIQUE NOT NULL," +
                    "password VARCHAR(255) NOT NULL," +
                    "full_name VARCHAR(100) NOT NULL," +
                    "phone VARCHAR(15)," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ")");
            
            // Create wallets table
            stmt.execute("CREATE TABLE IF NOT EXISTS wallets (" +
                    "wallet_id INT PRIMARY KEY AUTO_INCREMENT," +
                    "user_id INT NOT NULL," +
                    "balance DECIMAL(15,2) DEFAULT 0.00," +
                    "currency VARCHAR(3) DEFAULT 'USD'," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE" +
                    ")");
            
            // Create transactions table
            stmt.execute("CREATE TABLE IF NOT EXISTS transactions (" +
                    "transaction_id INT PRIMARY KEY AUTO_INCREMENT," +
                    "wallet_id INT NOT NULL," +
                    "transaction_type VARCHAR(20) NOT NULL," +
                    "amount DECIMAL(15,2) NOT NULL," +
                    "description VARCHAR(255)," +
                    "reference_number VARCHAR(50) UNIQUE," +
                    "status VARCHAR(20) DEFAULT 'COMPLETED'," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "FOREIGN KEY (wallet_id) REFERENCES wallets(wallet_id) ON DELETE CASCADE" +
                    ")");
            
            // Insert sample data (H2 compatible)
            stmt.execute("MERGE INTO users (username, email, password, full_name, phone) VALUES " +
                    "('admin', 'admin@wallet.com', 'admin123', 'Administrator', '+1234567890')");
            stmt.execute("MERGE INTO users (username, email, password, full_name, phone) VALUES " +
                    "('john_doe', 'john@example.com', 'password123', 'John Doe', '+1234567891')");
            
            stmt.execute("MERGE INTO wallets (user_id, balance) VALUES (1, 1000.00)");
            stmt.execute("MERGE INTO wallets (user_id, balance) VALUES (2, 500.00)");
            
            stmt.execute("MERGE INTO transactions (wallet_id, transaction_type, amount, description, reference_number) VALUES " +
                    "(1, 'CREDIT', 1000.00, 'Initial deposit', 'TXN001')");
            stmt.execute("MERGE INTO transactions (wallet_id, transaction_type, amount, description, reference_number) VALUES " +
                    "(2, 'CREDIT', 500.00, 'Welcome bonus', 'TXN002')");
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public static DatabaseConnection getInstance() {
        if (instance == null) {
            synchronized (DatabaseConnection.class) {
                if (instance == null) {
                    instance = new DatabaseConnection();
                }
            }
        }
        return instance;
    }
    
    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to get database connection", e);
        }
        return connection;
    }
    
    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}