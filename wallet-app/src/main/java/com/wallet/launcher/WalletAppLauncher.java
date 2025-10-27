package com.wallet.launcher;

import org.h2.tools.Server;
import com.wallet.util.DatabaseConnection;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class WalletAppLauncher {
    private static Server webServer;
    
    public static void main(String[] args) {
        try {
            System.out.println("🚀 Starting Wallet Application...");
            
            // Initialize database
            System.out.println("📊 Initializing database...");
            DatabaseConnection.getInstance();
            System.out.println("✅ Database initialized successfully!");
            
            // Start H2 web server on port 8080
            System.out.println("🌐 Starting web server...");
            webServer = Server.createWebServer(
                "-web", 
                "-webAllowOthers", 
                "-webPort", "8080",
                "-baseDir", new File("src/main/webapp").getAbsolutePath()
            );
            webServer.start();
            
            System.out.println("🎉 Wallet Application is running!");
            System.out.println("🔗 Open your browser and go to: http://localhost:8080");
            System.out.println("📊 H2 Database Console: http://localhost:8080/h2-console");
            System.out.println("🔑 Database URL: jdbc:h2:mem:wallet_db");
            System.out.println("👤 Username: sa");
            System.out.println("🔒 Password: (empty)");
            System.out.println();
            System.out.println("Default login credentials:");
            System.out.println("Username: admin, Password: admin123");
            System.out.println("Username: john_doe, Password: password123");
            System.out.println();
            System.out.println("Press Ctrl+C to stop the server");
            
            // Keep the server running
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("\n🛑 Shutting down server...");
                if (webServer != null) {
                    webServer.stop();
                }
                System.out.println("✅ Server stopped successfully!");
            }));
            
            // Keep main thread alive
            while (webServer.isRunning(false)) {
                Thread.sleep(1000);
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error starting wallet application: " + e.getMessage());
            e.printStackTrace();
        }
    }
}