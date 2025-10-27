<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Wallet App - Error</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body class="error-page">
    <div class="error-container">
        <div class="error-card">
            <h1 class="error-title">⚠️ Something went wrong</h1>
            
            <% if (request.getAttribute("errorMessage") != null) { %>
                <p class="error-message"><%= request.getAttribute("errorMessage") %></p>
            <% } else { %>
                <p class="error-message">An unexpected error occurred. Please try again later.</p>
            <% } %>
            
            <div class="error-actions">
                <a href="dashboard" class="btn btn-primary">Go to Dashboard</a>
                <a href="login" class="btn btn-secondary">Back to Login</a>
            </div>
        </div>
    </div>
</body>
</html>