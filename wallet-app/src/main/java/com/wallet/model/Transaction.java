package com.wallet.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Transaction {
    private int transactionId;
    private int walletId;
    private TransactionType transactionType;
    private BigDecimal amount;
    private String description;
    private String referenceNumber;
    private TransactionStatus status;
    private Timestamp createdAt;
    
    // Enums for transaction type and status
    public enum TransactionType {
        CREDIT, DEBIT, TRANSFER_IN, TRANSFER_OUT
    }
    
    public enum TransactionStatus {
        PENDING, COMPLETED, FAILED
    }
    
    // Default constructor
    public Transaction() {}
    
    // Constructor with parameters
    public Transaction(int walletId, TransactionType transactionType, BigDecimal amount, 
                      String description, String referenceNumber) {
        this.walletId = walletId;
        this.transactionType = transactionType;
        this.amount = amount;
        this.description = description;
        this.referenceNumber = referenceNumber;
        this.status = TransactionStatus.COMPLETED;
    }
    
    // Getters and Setters
    public int getTransactionId() {
        return transactionId;
    }
    
    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }
    
    public int getWalletId() {
        return walletId;
    }
    
    public void setWalletId(int walletId) {
        this.walletId = walletId;
    }
    
    public TransactionType getTransactionType() {
        return transactionType;
    }
    
    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getReferenceNumber() {
        return referenceNumber;
    }
    
    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }
    
    public TransactionStatus getStatus() {
        return status;
    }
    
    public void setStatus(TransactionStatus status) {
        this.status = status;
    }
    
    public Timestamp getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
    
    @Override
    public String toString() {
        return "Transaction{" +
                "transactionId=" + transactionId +
                ", walletId=" + walletId +
                ", transactionType=" + transactionType +
                ", amount=" + amount +
                ", description='" + description + '\'' +
                ", referenceNumber='" + referenceNumber + '\'' +
                ", status=" + status +
                ", createdAt=" + createdAt +
                '}';
    }
}