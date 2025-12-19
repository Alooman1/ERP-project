package edu.univ.erp.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// Central utility for managing database connections.
// Handles connecting to both the Authentication DB and the ERP DB.
public class DBConnectionManager {

    // --- Database Configuration Constants ---
    
    // Auth DB Settings
    private static final String AUTH_DB_URL = "jdbc:mysql://localhost:3306/auth_db";
    private static final String AUTH_DB_USER = "root";
    private static final String AUTH_DB_PASSWORD = "Naveen23@"; // Update if your local DB password differs

    // ERP DB Settings
    private static final String ERP_DB_URL = "jdbc:mysql://localhost:3306/erp_db";
    private static final String ERP_DB_USER = "root";
    private static final String ERP_DB_PASSWORD = "Naveen23@";

    // Returns a connection to the Authentication Database
    public static Connection getAuthConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // Load MySQL Driver
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found!", e);
        }
        return DriverManager.getConnection(AUTH_DB_URL, AUTH_DB_USER, AUTH_DB_PASSWORD);
    }

    // Getters for DB credentials (useful for Backup/Restore utilities)
    public static String getAuthDbUser() {
        return AUTH_DB_USER;
    }

    public static String getAuthDbPassword() {
        return AUTH_DB_PASSWORD;
    }

    public static String getErpDbUser() {
        return ERP_DB_USER;
    }

    public static String getErpDbPassword() {
        return ERP_DB_PASSWORD;
    }
    
    // Returns a connection to the ERP Database (Course/Student data)
    public static Connection getErpConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found!", e);
        }
        return DriverManager.getConnection(ERP_DB_URL, ERP_DB_USER, ERP_DB_PASSWORD);
    }

    // Main method for quick connection testing
    public static void main(String[] args) {
        try (Connection authConn = getAuthConnection();
             Connection erpConn = getErpConnection()) {
            
            if (authConn != null && !authConn.isClosed()) {
                System.out.println("SUCCESS: Connected to auth_db!");
            }
            
            if (erpConn != null && !erpConn.isClosed()) {
                System.out.println("SUCCESS: Connected to erp_db!");
            }

        } catch (SQLException e) {
            System.err.println("FAILED: Connection error.");
            e.printStackTrace();
        }
    }
}