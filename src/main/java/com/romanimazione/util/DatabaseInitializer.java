package com.romanimazione.util;

import com.romanimazione.dao.mysql.MySQLDAOFactory;
import java.sql.Connection;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void main(String[] args) {
        System.out.println("Initializing Database...");
        try {
            initializeUsersTable(); // Reset Users First
            initializePartyTable();
            initializeAssignmentsTable();
            System.out.println("Database initialization completed successfully.");
        } catch (java.sql.SQLException e) {
            java.util.logging.Logger.getLogger(DatabaseInitializer.class.getName()).log(java.util.logging.Level.SEVERE, "Error initializing database", e);
        } catch (Exception e) {
             // Fallback for unexpected errors
             java.util.logging.Logger.getLogger(DatabaseInitializer.class.getName()).log(java.util.logging.Level.SEVERE, "Unexpected error", e);
        }
    }

    private static void initializeUsersTable() throws java.sql.SQLException {
        System.out.println("Creating 'users' table...");
        String sql = "DROP TABLE IF EXISTS party_assignments;" + 
                     "DROP TABLE IF EXISTS availability; " + // Dependent table
                     "DROP TABLE IF EXISTS users; " +
                     "CREATE TABLE users (" +
                     "    id INT AUTO_INCREMENT PRIMARY KEY," +
                     "    username VARCHAR(50) NOT NULL UNIQUE," +
                     "    password VARCHAR(50) NOT NULL," +
                     "    role VARCHAR(20) NOT NULL," +
                     "    nome VARCHAR(50) NOT NULL," +
                     "    cognome VARCHAR(50) NOT NULL," +
                     "    email VARCHAR(100) NOT NULL," +
                     "    is_super_admin BOOLEAN DEFAULT FALSE" +
                     ");";
        
        try (Connection conn = MySQLDAOFactory.createConnection();
             Statement stmt = conn.createStatement()) {
            
            String[] statements = sql.split(";");
            for (String s : statements) {
                if (!s.trim().isEmpty()) {
                   stmt.executeUpdate(s);
                }
            }
            System.out.println("Table 'users' reset successfully.");
        }
    }

    private static void initializePartyTable() throws java.sql.SQLException {
        System.out.println("Creating 'party' table...");
        String sql = "DROP TABLE IF EXISTS party_assignments; " +
                     "DROP TABLE IF EXISTS party; " +
                     "CREATE TABLE party (" +
                     "    id INT AUTO_INCREMENT PRIMARY KEY," +
                     "    name VARCHAR(100) NOT NULL," +
                     "    type VARCHAR(50) NOT NULL," +
                     "    address VARCHAR(255) NOT NULL," +
                     "    party_date DATE NOT NULL," +
                     "    client_name VARCHAR(100) NOT NULL," +
                     "    client_phone VARCHAR(20) NOT NULL," +
                     "    start_time TIME NOT NULL," +
                     "    end_time TIME NOT NULL," +
                     "    children_count INT," +
                     "    animators_required INT NOT NULL," +
                     "    description TEXT," +
                     "    cost DECIMAL(10,2) NOT NULL," +
                     "    status VARCHAR(20) DEFAULT 'SCHEDULED'" +
                     ");";
        
        try (Connection conn = MySQLDAOFactory.createConnection();
             Statement stmt = conn.createStatement()) {
            
            String[] statements = sql.split(";");
            for (String s : statements) {
                if (!s.trim().isEmpty()) {
                   stmt.executeUpdate(s);
                }
            }
            System.out.println("Table 'party' reset successfully.");
        }
    }

    private static void initializeAssignmentsTable() throws java.sql.SQLException {
        System.out.println("Creating 'party_assignments' table...");
        
        // HARDCODED SQL from create_assignments_table.sql
        String sql = "CREATE TABLE IF NOT EXISTS party_assignments (" +
                     "    assignment_id INT AUTO_INCREMENT PRIMARY KEY," +
                     "    party_id INT NOT NULL," +
                     "    animator_username VARCHAR(50) NOT NULL," +
                     "    status VARCHAR(20) DEFAULT 'PENDING'," +
                     "    FOREIGN KEY (party_id) REFERENCES party(id) ON DELETE CASCADE," +
                     "    FOREIGN KEY (animator_username) REFERENCES users(username) ON DELETE CASCADE," +
                     "    UNIQUE(party_id, animator_username)" +
                     ");";
        
        try (Connection conn = MySQLDAOFactory.createConnection();
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate(sql);
            System.out.println("Table 'party_assignments' check/create completed.");
        }
    }
}
