import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class SimpleWebServer {
    private static final int PORT = 8080;
    
    public static void main(String[] args) throws Exception {
        // Initialize database
        System.out.println("🚀 Starting Wallet Application...");
        System.out.println("📊 Initializing database...");
        
        // Initialize H2 database
        Class.forName("org.h2.Driver");
        java.sql.Connection conn = java.sql.DriverManager.getConnection(
            "jdbc:h2:mem:wallet_db;DB_CLOSE_DELAY=-1", "sa", "");
        
        // Create tables and insert sample data
        java.sql.Statement stmt = conn.createStatement();
        
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
        
        stmt.execute("CREATE TABLE IF NOT EXISTS wallets (" +
                "wallet_id INT PRIMARY KEY AUTO_INCREMENT," +
                "user_id INT NOT NULL," +
                "balance DECIMAL(15,2) DEFAULT 0.00," +
                "currency VARCHAR(3) DEFAULT 'USD'," +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE" +
                ")");
        
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
        
        // Insert sample data
        stmt.execute("INSERT INTO users (username, email, password, full_name, phone) VALUES " +
                "('admin', 'admin@wallet.com', 'admin123', 'Administrator', '+1234567890')");
        stmt.execute("INSERT INTO users (username, email, password, full_name, phone) VALUES " +
                "('john_doe', 'john@example.com', 'password123', 'John Doe', '+1234567891')");
        
        stmt.execute("INSERT INTO wallets (user_id, balance) VALUES (1, 1000.00)");
        stmt.execute("INSERT INTO wallets (user_id, balance) VALUES (2, 500.00)");
        
        stmt.execute("INSERT INTO transactions (wallet_id, transaction_type, amount, description, reference_number) VALUES " +
                "(1, 'CREDIT', 1000.00, 'Initial deposit', 'TXN001')");
        stmt.execute("INSERT INTO transactions (wallet_id, transaction_type, amount, description, reference_number) VALUES " +
                "(2, 'CREDIT', 500.00, 'Welcome bonus', 'TXN002')");
        
        System.out.println("✅ Database initialized successfully!");
        
        // Create HTTP server
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        
        // Serve static files
        server.createContext("/", new StaticFileHandler());
        server.createContext("/css/", new StaticFileHandler());
        
        server.setExecutor(null);
        server.start();
        
        System.out.println("🎉 Wallet Application is running!");
        System.out.println("🔗 Open your browser and go to: http://localhost:" + PORT);
        System.out.println();
        System.out.println("Default login credentials:");
        System.out.println("👤 Username: admin, Password: admin123");
        System.out.println("👤 Username: john_doe, Password: password123");
        System.out.println();
        System.out.println("Press Ctrl+C to stop the server");
        
        // Keep server running
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n🛑 Shutting down server...");
            server.stop(0);
            try {
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("✅ Server stopped successfully!");
        }));
    }
    
    static class StaticFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            
            // Default to login page
            if (path.equals("/") || path.equals("/index.jsp")) {
                path = "/login.jsp";
            }
            
            // Remove leading slash and add webapp directory
            String filePath = "src/main/webapp" + path;
            File file = new File(filePath);
            
            if (file.exists() && file.isFile()) {
                String contentType = getContentType(filePath);
                exchange.getResponseHeaders().set("Content-Type", contentType);
                exchange.sendResponseHeaders(200, file.length());
                
                try (FileInputStream fis = new FileInputStream(file);
                     OutputStream os = exchange.getResponseBody()) {
                    byte[] buffer = new byte[8192];
                    int bytesRead;
                    while ((bytesRead = fis.read(buffer)) != -1) {
                        os.write(buffer, 0, bytesRead);
                    }
                }
            } else {
                // Return 404
                String response = "<!DOCTYPE html><html><head><title>404 Not Found</title></head>" +
                                "<body><h1>404 - File Not Found</h1><p>The requested file was not found.</p>" +
                                "<a href='/'>Go to Home</a></body></html>";
                exchange.sendResponseHeaders(404, response.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            }
        }
        
        private String getContentType(String filePath) {
            if (filePath.endsWith(".html") || filePath.endsWith(".jsp")) {
                return "text/html";
            } else if (filePath.endsWith(".css")) {
                return "text/css";
            } else if (filePath.endsWith(".js")) {
                return "application/javascript";
            } else if (filePath.endsWith(".png")) {
                return "image/png";
            } else if (filePath.endsWith(".jpg") || filePath.endsWith(".jpeg")) {
                return "image/jpeg";
            }
            return "text/plain";
        }
    }
}