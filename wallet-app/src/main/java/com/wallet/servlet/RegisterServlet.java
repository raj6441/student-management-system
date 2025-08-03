package com.wallet.servlet;

import com.wallet.dao.UserDAO;
import com.wallet.dao.WalletDAO;
import com.wallet.model.User;
import com.wallet.model.Wallet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    private UserDAO userDAO;
    private WalletDAO walletDAO;
    
    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
        walletDAO = new WalletDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Forward to registration page
        request.getRequestDispatcher("/register.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String fullName = request.getParameter("fullName");
        String phone = request.getParameter("phone");
        
        // Validate input
        StringBuilder errors = new StringBuilder();
        
        if (username == null || username.trim().isEmpty()) {
            errors.append("Username is required. ");
        }
        if (email == null || email.trim().isEmpty()) {
            errors.append("Email is required. ");
        }
        if (password == null || password.trim().isEmpty()) {
            errors.append("Password is required. ");
        }
        if (confirmPassword == null || !password.equals(confirmPassword)) {
            errors.append("Passwords do not match. ");
        }
        if (fullName == null || fullName.trim().isEmpty()) {
            errors.append("Full name is required. ");
        }
        
        if (errors.length() > 0) {
            request.setAttribute("errorMessage", errors.toString());
            request.getRequestDispatcher("/register.jsp").forward(request, response);
            return;
        }
        
        // Check if username or email already exists
        if (userDAO.getUserByUsername(username.trim()) != null) {
            request.setAttribute("errorMessage", "Username already exists");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
            return;
        }
        
        if (userDAO.getUserByEmail(email.trim()) != null) {
            request.setAttribute("errorMessage", "Email already exists");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
            return;
        }
        
        // Create new user
        User user = new User(username.trim(), email.trim(), password, fullName.trim(), phone);
        
        if (userDAO.createUser(user)) {
            // Create wallet for the new user with initial balance of 0
            Wallet wallet = new Wallet(user.getUserId(), BigDecimal.ZERO, "USD");
            walletDAO.createWallet(wallet);
            
            request.setAttribute("successMessage", "Registration successful! Please login.");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        } else {
            request.setAttribute("errorMessage", "Registration failed. Please try again.");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
        }
    }
}