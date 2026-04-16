package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    // Database connection constants
    private static final String DB_HOST = "127.0.0.1";
    private static final String DB_PORT = "3306";
    private static final String DB_NAME = "codyquest";
    private static final String DB_USER = "root"; // TODO: Fill in your DB_USER
    private static final String DB_PASSWORD = ""; // TODO: Fill in your DB_PASSWORD
    private static final String JDBC_URL = "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME
            + "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";

    private static DatabaseConnection instance;
    private Connection connection;

    // Private constructor for singleton pattern
    private DatabaseConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD);
            System.out.println("[DatabaseConnection] ✓ Connected to " + DB_HOST + ":" + DB_PORT + "/" + DB_NAME);
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("[DatabaseConnection] ✗ Connection Failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Get singleton instance
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    // Get database connection
    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                System.out.println("[DatabaseConnection] Connection is null/closed, attempting to reconnect...");
                connection = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD);
                System.out.println("[DatabaseConnection] ✓ Reconnected successfully");
            }
            return connection;
        } catch (SQLException e) {
            System.err.println("[DatabaseConnection] ✗ Connection Error: " + e.getMessage());
            System.err.println("[DatabaseConnection] ✗ JDBC URL: " + JDBC_URL);
            System.err.println("[DatabaseConnection] ✗ DB_USER: " + DB_USER);
            System.err.println("[DatabaseConnection] ✗ DB_PASSWORD: " + (DB_PASSWORD.isEmpty() ? "(empty)" : "***"));
            System.err.println("[DatabaseConnection] ✗ Make sure:");
            System.err.println("    1. MySQL is RUNNING");
            System.err.println("    2. Database 'codyquest' EXISTS");
            System.err.println("    3. DB_USER and DB_PASSWORD are CORRECT");
            e.printStackTrace();
            return null;
        }
    }

    // Close database connection
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
