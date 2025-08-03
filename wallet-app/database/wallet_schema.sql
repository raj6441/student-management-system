-- Wallet Application Database Schema
CREATE DATABASE IF NOT EXISTS wallet_db;
USE wallet_db;

-- Users table
CREATE TABLE users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    phone VARCHAR(15),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Wallets table
CREATE TABLE wallets (
    wallet_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    balance DECIMAL(15,2) DEFAULT 0.00,
    currency VARCHAR(3) DEFAULT 'USD',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Transactions table
CREATE TABLE transactions (
    transaction_id INT PRIMARY KEY AUTO_INCREMENT,
    wallet_id INT NOT NULL,
    transaction_type ENUM('CREDIT', 'DEBIT', 'TRANSFER_IN', 'TRANSFER_OUT') NOT NULL,
    amount DECIMAL(15,2) NOT NULL,
    description VARCHAR(255),
    reference_number VARCHAR(50) UNIQUE,
    status ENUM('PENDING', 'COMPLETED', 'FAILED') DEFAULT 'COMPLETED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (wallet_id) REFERENCES wallets(wallet_id) ON DELETE CASCADE
);

-- Indexes for better performance
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_wallets_user_id ON wallets(user_id);
CREATE INDEX idx_transactions_wallet_id ON transactions(wallet_id);
CREATE INDEX idx_transactions_created_at ON transactions(created_at);

-- Insert sample data
INSERT INTO users (username, email, password, full_name, phone) VALUES
('admin', 'admin@wallet.com', 'admin123', 'Administrator', '+1234567890'),
('john_doe', 'john@example.com', 'password123', 'John Doe', '+1234567891');

INSERT INTO wallets (user_id, balance) VALUES
(1, 1000.00),
(2, 500.00);

INSERT INTO transactions (wallet_id, transaction_type, amount, description, reference_number) VALUES
(1, 'CREDIT', 1000.00, 'Initial deposit', 'TXN001'),
(2, 'CREDIT', 500.00, 'Welcome bonus', 'TXN002');