<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.wallet.model.*" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.NumberFormat" %>
<%@ page import="java.text.SimpleDateFormat" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Wallet App - Dashboard</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
    <nav class="navbar">
        <div class="nav-container">
            <h1 class="nav-brand">Wallet App</h1>
            <div class="nav-menu">
                <span class="nav-user">Welcome, <%= ((User)session.getAttribute("user")).getFullName() %></span>
                <a href="transaction?action=deposit" class="nav-link">Deposit</a>
                <a href="transaction?action=withdraw" class="nav-link">Withdraw</a>
                <a href="transaction?action=transfer" class="nav-link">Transfer</a>
                <a href="logout" class="nav-link">Logout</a>
            </div>
        </div>
    </nav>

    <div class="container">
        <% 
            Wallet wallet = (Wallet) request.getAttribute("wallet");
            List<Transaction> recentTransactions = (List<Transaction>) request.getAttribute("recentTransactions");
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm");
        %>
        
        <!-- Wallet Balance Card -->
        <div class="balance-card">
            <h2>Current Balance</h2>
            <div class="balance-amount">
                <%= currencyFormat.format(wallet.getBalance()) %>
            </div>
            <p class="balance-currency"><%= wallet.getCurrency() %></p>
        </div>

        <!-- Quick Actions -->
        <div class="quick-actions">
            <h3>Quick Actions</h3>
            <div class="action-buttons">
                <a href="transaction?action=deposit" class="btn btn-success">
                    <span class="btn-icon">💰</span>
                    Deposit Money
                </a>
                <a href="transaction?action=withdraw" class="btn btn-warning">
                    <span class="btn-icon">💸</span>
                    Withdraw Money
                </a>
                <a href="transaction?action=transfer" class="btn btn-info">
                    <span class="btn-icon">🔄</span>
                    Transfer Money
                </a>
            </div>
        </div>

        <!-- Recent Transactions -->
        <div class="transactions-section">
            <h3>Recent Transactions</h3>
            <% if (recentTransactions != null && !recentTransactions.isEmpty()) { %>
                <div class="transactions-list">
                    <% for (Transaction transaction : recentTransactions) { %>
                        <div class="transaction-item <%= transaction.getTransactionType().name().toLowerCase() %>">
                            <div class="transaction-info">
                                <div class="transaction-type">
                                    <% 
                                        String icon = "";
                                        String typeText = "";
                                        switch (transaction.getTransactionType()) {
                                            case CREDIT:
                                                icon = "⬇️";
                                                typeText = "Deposit";
                                                break;
                                            case DEBIT:
                                                icon = "⬆️";
                                                typeText = "Withdrawal";
                                                break;
                                            case TRANSFER_IN:
                                                icon = "⬅️";
                                                typeText = "Transfer In";
                                                break;
                                            case TRANSFER_OUT:
                                                icon = "➡️";
                                                typeText = "Transfer Out";
                                                break;
                                        }
                                    %>
                                    <span class="transaction-icon"><%= icon %></span>
                                    <span class="transaction-label"><%= typeText %></span>
                                </div>
                                <div class="transaction-description">
                                    <%= transaction.getDescription() %>
                                </div>
                                <div class="transaction-date">
                                    <%= dateFormat.format(transaction.getCreatedAt()) %>
                                </div>
                            </div>
                            <div class="transaction-amount">
                                <% if (transaction.getTransactionType() == Transaction.TransactionType.CREDIT || 
                                       transaction.getTransactionType() == Transaction.TransactionType.TRANSFER_IN) { %>
                                    <span class="amount-positive">+<%= currencyFormat.format(transaction.getAmount()) %></span>
                                <% } else { %>
                                    <span class="amount-negative">-<%= currencyFormat.format(transaction.getAmount()) %></span>
                                <% } %>
                            </div>
                        </div>
                    <% } %>
                </div>
            <% } else { %>
                <div class="empty-state">
                    <p>No transactions yet. Start by making a deposit!</p>
                    <a href="transaction?action=deposit" class="btn btn-primary">Make First Deposit</a>
                </div>
            <% } %>
        </div>
    </div>
</body>
</html>