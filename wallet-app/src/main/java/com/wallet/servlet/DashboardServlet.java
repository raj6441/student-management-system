package com.wallet.servlet;

import com.wallet.dao.WalletDAO;
import com.wallet.dao.TransactionDAO;
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
import java.util.List;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {
    private WalletDAO walletDAO;
    private TransactionDAO transactionDAO;
    
    @Override
    public void init() throws ServletException {
        walletDAO = new WalletDAO();
        transactionDAO = new TransactionDAO();
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
        
        // Get user's wallet
        Wallet wallet = walletDAO.getWalletByUserId(user.getUserId());
        if (wallet == null) {
            request.setAttribute("errorMessage", "Wallet not found");
            request.getRequestDispatcher("/error.jsp").forward(request, response);
            return;
        }
        
        // Get recent transactions (last 10)
        List<Transaction> recentTransactions = transactionDAO.getTransactionsByWalletId(wallet.getWalletId(), 10);
        
        // Set attributes for JSP
        request.setAttribute("wallet", wallet);
        request.setAttribute("recentTransactions", recentTransactions);
        
        // Forward to dashboard page
        request.getRequestDispatcher("/dashboard.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doGet(request, response);
    }
}