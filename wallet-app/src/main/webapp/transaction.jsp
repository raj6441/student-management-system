<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.wallet.model.User" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Wallet App - Transaction</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
    <nav class="navbar">
        <div class="nav-container">
            <h1 class="nav-brand">Wallet App</h1>
            <div class="nav-menu">
                <span class="nav-user">Welcome, <%= ((User)session.getAttribute("user")).getFullName() %></span>
                <a href="dashboard" class="nav-link">Dashboard</a>
                <a href="logout" class="nav-link">Logout</a>
            </div>
        </div>
    </nav>

    <div class="container">
        <% 
            String action = (String) request.getAttribute("action");
            if (action == null) action = "deposit";
        %>
        
        <div class="transaction-container">
            <!-- Tab Navigation -->
            <div class="tab-navigation">
                <a href="transaction?action=deposit" class="tab-link <%= "deposit".equals(action) ? "active" : "" %>">
                    <span class="tab-icon">💰</span>
                    Deposit
                </a>
                <a href="transaction?action=withdraw" class="tab-link <%= "withdraw".equals(action) ? "active" : "" %>">
                    <span class="tab-icon">💸</span>
                    Withdraw
                </a>
                <a href="transaction?action=transfer" class="tab-link <%= "transfer".equals(action) ? "active" : "" %>">
                    <span class="tab-icon">🔄</span>
                    Transfer
                </a>
            </div>

            <!-- Transaction Form -->
            <div class="transaction-form-container">
                <% if (request.getAttribute("errorMessage") != null) { %>
                    <div class="alert alert-error">
                        <%= request.getAttribute("errorMessage") %>
                    </div>
                <% } %>
                
                <% if (request.getAttribute("successMessage") != null) { %>
                    <div class="alert alert-success">
                        <%= request.getAttribute("successMessage") %>
                    </div>
                <% } %>

                <form method="post" action="transaction" class="transaction-form">
                    <input type="hidden" name="action" value="<%= action %>">
                    
                    <% if ("deposit".equals(action)) { %>
                        <h2 class="form-title">💰 Deposit Money</h2>
                        <p class="form-description">Add money to your wallet</p>
                        
                        <div class="form-group">
                            <label for="amount">Amount</label>
                            <input type="number" id="amount" name="amount" step="0.01" min="0.01" required 
                                   placeholder="Enter amount to deposit">
                        </div>
                        
                        <div class="form-group">
                            <label for="description">Description (Optional)</label>
                            <input type="text" id="description" name="description" 
                                   placeholder="e.g., Salary deposit">
                        </div>
                        
                        <button type="submit" class="btn btn-success btn-full">Deposit Money</button>
                        
                    <% } else if ("withdraw".equals(action)) { %>
                        <h2 class="form-title">💸 Withdraw Money</h2>
                        <p class="form-description">Withdraw money from your wallet</p>
                        
                        <div class="form-group">
                            <label for="amount">Amount</label>
                            <input type="number" id="amount" name="amount" step="0.01" min="0.01" required 
                                   placeholder="Enter amount to withdraw">
                        </div>
                        
                        <div class="form-group">
                            <label for="description">Description (Optional)</label>
                            <input type="text" id="description" name="description" 
                                   placeholder="e.g., ATM withdrawal">
                        </div>
                        
                        <button type="submit" class="btn btn-warning btn-full">Withdraw Money</button>
                        
                    <% } else if ("transfer".equals(action)) { %>
                        <h2 class="form-title">🔄 Transfer Money</h2>
                        <p class="form-description">Send money to another user</p>
                        
                        <div class="form-group">
                            <label for="recipientUsername">Recipient Username</label>
                            <input type="text" id="recipientUsername" name="recipientUsername" required 
                                   placeholder="Enter recipient's username">
                        </div>
                        
                        <div class="form-group">
                            <label for="amount">Amount</label>
                            <input type="number" id="amount" name="amount" step="0.01" min="0.01" required 
                                   placeholder="Enter amount to transfer">
                        </div>
                        
                        <div class="form-group">
                            <label for="description">Description (Optional)</label>
                            <input type="text" id="description" name="description" 
                                   placeholder="e.g., Payment for services">
                        </div>
                        
                        <button type="submit" class="btn btn-info btn-full">Transfer Money</button>
                    <% } %>
                </form>
                
                <div class="form-footer">
                    <a href="dashboard" class="btn btn-secondary">Back to Dashboard</a>
                </div>
            </div>
        </div>
    </div>

    <script>
        // Add some basic form validation
        document.addEventListener('DOMContentLoaded', function() {
            const form = document.querySelector('.transaction-form');
            const amountInput = document.getElementById('amount');
            
            form.addEventListener('submit', function(e) {
                const amount = parseFloat(amountInput.value);
                if (amount <= 0) {
                    e.preventDefault();
                    alert('Please enter a valid amount greater than 0');
                    return false;
                }
                
                if (amount > 1000000) {
                    e.preventDefault();
                    alert('Amount cannot exceed $1,000,000');
                    return false;
                }
                
                return confirm('Are you sure you want to proceed with this transaction?');
            });
        });
    </script>
</body>
</html>