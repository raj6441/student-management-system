package com.wallet.dao;

import com.wallet.model.Wallet;
import com.wallet.util.DatabaseConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WalletDAO {
    private Connection connection;
    
    public WalletDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }
    
    // Create a new wallet
    public boolean createWallet(Wallet wallet) {
        String sql = "INSERT INTO wallets (user_id, balance, currency) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, wallet.getUserId());
            stmt.setBigDecimal(2, wallet.getBalance());
            stmt.setString(3, wallet.getCurrency());
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    wallet.setWalletId(generatedKeys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // Get wallet by user ID
    public Wallet getWalletByUserId(int userId) {
        String sql = "SELECT * FROM wallets WHERE user_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToWallet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // Get wallet by wallet ID
    public Wallet getWalletById(int walletId) {
        String sql = "SELECT * FROM wallets WHERE wallet_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, walletId);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToWallet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // Update wallet balance
    public boolean updateWalletBalance(int walletId, BigDecimal newBalance) {
        String sql = "UPDATE wallets SET balance = ? WHERE wallet_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setBigDecimal(1, newBalance);
            stmt.setInt(2, walletId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // Add amount to wallet balance
    public boolean addToWalletBalance(int walletId, BigDecimal amount) {
        String sql = "UPDATE wallets SET balance = balance + ? WHERE wallet_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setBigDecimal(1, amount);
            stmt.setInt(2, walletId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // Subtract amount from wallet balance
    public boolean subtractFromWalletBalance(int walletId, BigDecimal amount) {
        String sql = "UPDATE wallets SET balance = balance - ? WHERE wallet_id = ? AND balance >= ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setBigDecimal(1, amount);
            stmt.setInt(2, walletId);
            stmt.setBigDecimal(3, amount);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // Check if wallet has sufficient balance
    public boolean hasSufficientBalance(int walletId, BigDecimal amount) {
        String sql = "SELECT balance FROM wallets WHERE wallet_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, walletId);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                BigDecimal currentBalance = rs.getBigDecimal("balance");
                return currentBalance.compareTo(amount) >= 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // Get all wallets
    public List<Wallet> getAllWallets() {
        List<Wallet> wallets = new ArrayList<>();
        String sql = "SELECT * FROM wallets ORDER BY created_at DESC";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                wallets.add(mapResultSetToWallet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return wallets;
    }
    
    // Delete wallet
    public boolean deleteWallet(int walletId) {
        String sql = "DELETE FROM wallets WHERE wallet_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, walletId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // Transfer money between wallets (atomic operation)
    public boolean transferMoney(int fromWalletId, int toWalletId, BigDecimal amount) {
        try {
            connection.setAutoCommit(false);
            
            // Check if source wallet has sufficient balance
            if (!hasSufficientBalance(fromWalletId, amount)) {
                connection.rollback();
                return false;
            }
            
            // Subtract from source wallet
            boolean subtracted = subtractFromWalletBalance(fromWalletId, amount);
            if (!subtracted) {
                connection.rollback();
                return false;
            }
            
            // Add to destination wallet
            boolean added = addToWalletBalance(toWalletId, amount);
            if (!added) {
                connection.rollback();
                return false;
            }
            
            connection.commit();
            return true;
            
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    
    // Helper method to map ResultSet to Wallet object
    private Wallet mapResultSetToWallet(ResultSet rs) throws SQLException {
        Wallet wallet = new Wallet();
        wallet.setWalletId(rs.getInt("wallet_id"));
        wallet.setUserId(rs.getInt("user_id"));
        wallet.setBalance(rs.getBigDecimal("balance"));
        wallet.setCurrency(rs.getString("currency"));
        wallet.setCreatedAt(rs.getTimestamp("created_at"));
        wallet.setUpdatedAt(rs.getTimestamp("updated_at"));
        return wallet;
    }
}