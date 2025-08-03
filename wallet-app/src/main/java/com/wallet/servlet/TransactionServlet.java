package com.wallet.servlet;

import com.wallet.dao.WalletDAO;
import com.wallet.dao.TransactionDAO;
import com.wallet.dao.UserDAO;
import com.wallet.model.User;
import com.wallet.model.Wallet;
import com.wallet.model.Transaction;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;

@WebServlet("/transaction")
public class TransactionServlet extends HttpServlet {
    private WalletDAO walletDAO;
    private TransactionDAO transactionDAO;
    private UserDAO userDAO;
    
    @Override
    public void init() throws ServletException {
        walletDAO = new WalletDAO();
        transactionDAO = new TransactionDAO();
        userDAO = new UserDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        
        // Check if user is logged in
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        String action = request.getParameter("action");
        if (action == null) {
            action = "deposit"; // default action
        }
        
        request.setAttribute("action", action);
        request.getRequestDispatcher("/transaction.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        
        // Check if user is logged in
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        String action = request.getParameter("action");
        String amountStr = request.getParameter("amount");
        String description = request.getParameter("description");
        
        // Validate input
        if (amountStr == null || amountStr.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Amount is required");
            request.setAttribute("action", action);
            request.getRequestDispatcher("/transaction.jsp").forward(request, response);
            return;
        }
        
        BigDecimal amount;
        try {
            amount = new BigDecimal(amountStr);
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new NumberFormatException("Amount must be positive");
            }
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Invalid amount format");
            request.setAttribute("action", action);
            request.getRequestDispatcher("/transaction.jsp").forward(request, response);
            return;
        }
        
        // Get user's wallet
        Wallet wallet = walletDAO.getWalletByUserId(user.getUserId());
        if (wallet == null) {
            request.setAttribute("errorMessage", "Wallet not found");
            request.getRequestDispatcher("/error.jsp").forward(request, response);
            return;
        }
        
        boolean success = false;
        String successMessage = "";
        String errorMessage = "";
        
        switch (action) {
            case "deposit":
                success = handleDeposit(wallet, amount, description);
                successMessage = "Deposit successful! Amount: $" + amount;
                errorMessage = "Deposit failed. Please try again.";
                break;
                
            case "withdraw":
                success = handleWithdraw(wallet, amount, description);
                successMessage = "Withdrawal successful! Amount: $" + amount;
                errorMessage = "Insufficient balance or withdrawal failed.";
                break;
                
            case "transfer":
                String recipientUsername = request.getParameter("recipientUsername");
                success = handleTransfer(wallet, amount, description, recipientUsername);
                successMessage = "Transfer successful! Amount: $" + amount + " to " + recipientUsername;
                errorMessage = "Transfer failed. Check recipient username and balance.";
                break;
                
            default:
                request.setAttribute("errorMessage", "Invalid action");
                request.setAttribute("action", action);
                request.getRequestDispatcher("/transaction.jsp").forward(request, response);
                return;
        }
        
        if (success) {
            request.setAttribute("successMessage", successMessage);
        } else {
            request.setAttribute("errorMessage", errorMessage);
        }
        
        request.setAttribute("action", action);
        request.getRequestDispatcher("/transaction.jsp").forward(request, response);
    }
    
    private boolean handleDeposit(Wallet wallet, BigDecimal amount, String description) {
        // Add amount to wallet
        if (walletDAO.addToWalletBalance(wallet.getWalletId(), amount)) {
            // Create transaction record
            Transaction transaction = new Transaction(
                wallet.getWalletId(),
                Transaction.TransactionType.CREDIT,
                amount,
                description != null ? description : "Deposit",
                transactionDAO.generateReferenceNumber()
            );
            return transactionDAO.createTransaction(transaction);
        }
        return false;
    }
    
    private boolean handleWithdraw(Wallet wallet, BigDecimal amount, String description) {
        // Check if wallet has sufficient balance
        if (!walletDAO.hasSufficientBalance(wallet.getWalletId(), amount)) {
            return false;
        }
        
        // Subtract amount from wallet
        if (walletDAO.subtractFromWalletBalance(wallet.getWalletId(), amount)) {
            // Create transaction record
            Transaction transaction = new Transaction(
                wallet.getWalletId(),
                Transaction.TransactionType.DEBIT,
                amount,
                description != null ? description : "Withdrawal",
                transactionDAO.generateReferenceNumber()
            );
            return transactionDAO.createTransaction(transaction);
        }
        return false;
    }
    
    private boolean handleTransfer(Wallet senderWallet, BigDecimal amount, String description, String recipientUsername) {
        if (recipientUsername == null || recipientUsername.trim().isEmpty()) {
            return false;
        }
        
        // Find recipient user
        User recipient = userDAO.getUserByUsername(recipientUsername.trim());
        if (recipient == null) {
            return false;
        }
        
        // Get recipient's wallet
        Wallet recipientWallet = walletDAO.getWalletByUserId(recipient.getUserId());
        if (recipientWallet == null) {
            return false;
        }
        
        // Don't allow transfer to self
        if (senderWallet.getUserId() == recipientWallet.getUserId()) {
            return false;
        }
        
        // Perform transfer
        if (walletDAO.transferMoney(senderWallet.getWalletId(), recipientWallet.getWalletId(), amount)) {
            // Create transaction records for both wallets
            String refNumber = transactionDAO.generateReferenceNumber();
            
            // Sender transaction (outgoing)
            Transaction senderTransaction = new Transaction(
                senderWallet.getWalletId(),
                Transaction.TransactionType.TRANSFER_OUT,
                amount,
                description != null ? description : "Transfer to " + recipientUsername,
                refNumber
            );
            
            // Recipient transaction (incoming)
            Transaction recipientTransaction = new Transaction(
                recipientWallet.getWalletId(),
                Transaction.TransactionType.TRANSFER_IN,
                amount,
                description != null ? description : "Transfer from " + senderWallet.getUserId(),
                refNumber
            );
            
            return transactionDAO.createTransaction(senderTransaction) && 
                   transactionDAO.createTransaction(recipientTransaction);
        }
        
        return false;
    }
}