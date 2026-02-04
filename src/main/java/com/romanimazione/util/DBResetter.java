package com.romanimazione.util;

import com.romanimazione.dao.mysql.MySQLDAOFactory;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

public class DBResetter {

    public static void main(String[] args) {
        System.out.println("Beginning Full Database Reset...");
        resetDatabase();
    }

    public static void resetDatabase() {
        try (Connection conn = MySQLDAOFactory.createConnection();
             Statement stmt = conn.createStatement()) {

            // 1. DROP Tables (Order matters due to Foreign Keys)
            System.out.println("Dropping existing tables...");
            stmt.executeUpdate("SET FOREIGN_KEY_CHECKS = 0"); // Disable checks to be safe
            stmt.executeUpdate("DROP TABLE IF EXISTS party_assignments");
            stmt.executeUpdate("DROP TABLE IF EXISTS availability"); // Note: availability vs availabilities. DAO uses 'availability'
            stmt.executeUpdate("DROP TABLE IF EXISTS party");
            stmt.executeUpdate("DROP TABLE IF EXISTS users");
            stmt.executeUpdate("SET FOREIGN_KEY_CHECKS = 1");

            // 2. CREATE 'users'
            System.out.println("Creating table 'users'...");
            String createUsers = "CREATE TABLE users (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "username VARCHAR(50) NOT NULL UNIQUE, " +
                    "password VARCHAR(50) NOT NULL, " +
                    "role ENUM('ANIMATORE', 'AMMINISTRATORE') NOT NULL, " +
                    "nome VARCHAR(50), " +
                    "cognome VARCHAR(50), " +
                    "email VARCHAR(100), " +
                    "is_super_admin BOOLEAN DEFAULT FALSE" +
                    ")";
            stmt.executeUpdate(createUsers);

            // 3. CREATE 'party'
            System.out.println("Creating table 'party'...");
            String createParty = "CREATE TABLE party (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "name VARCHAR(100) NOT NULL, " +
                    "type VARCHAR(50) NOT NULL, " +
                    "address VARCHAR(255) NOT NULL, " +
                    "party_date DATE NOT NULL, " +
                    "client_name VARCHAR(100) NOT NULL, " +
                    "client_phone VARCHAR(20) NOT NULL, " +
                    "start_time TIME NOT NULL, " +
                    "end_time TIME NOT NULL, " +
                    "children_count INT, " +
                    "animators_required INT NOT NULL, " +
                    "description TEXT, " +
                    "cost DECIMAL(10,2) NOT NULL, " +
                    "status VARCHAR(20) DEFAULT 'SCHEDULED'" +
                    ")";
            stmt.executeUpdate(createParty);

            // 4. CREATE 'availability'
            System.out.println("Creating table 'availability'...");
            String createAvail = "CREATE TABLE availability (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "username VARCHAR(50) NOT NULL, " +
                    "availability_date DATE NOT NULL, " +
                    "start_time TIME NOT NULL, " +
                    "end_time TIME NOT NULL, " +
                    "is_full_day BOOLEAN NOT NULL, " +
                    "FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE" +
                    ")";
            stmt.executeUpdate(createAvail);

            // 5. CREATE 'party_assignments'
            System.out.println("Creating table 'party_assignments'...");
            String createAssign = "CREATE TABLE party_assignments (" +
                    "assignment_id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "party_id INT NOT NULL, " +
                    "animator_username VARCHAR(50) NOT NULL, " +
                    "status VARCHAR(20) DEFAULT 'PENDING', " +
                    "FOREIGN KEY (party_id) REFERENCES party(id) ON DELETE CASCADE, " +
                    "FOREIGN KEY (animator_username) REFERENCES users(username) ON DELETE CASCADE, " +
                    "UNIQUE(party_id, animator_username)" +
                    ")";
            stmt.executeUpdate(createAssign);

            System.out.println("Database Reset Completed Successfully!");

        } catch (SQLException e) {
            java.util.logging.Logger.getLogger(DBResetter.class.getName()).log(java.util.logging.Level.SEVERE, e, () -> "Error resetting database: " + e.getMessage());
        }
    }
}
