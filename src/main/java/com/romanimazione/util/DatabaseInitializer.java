package com.romanimazione.util;

import com.romanimazione.dao.mysql.MySQLDAOFactory;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Statement;
import java.util.stream.Collectors;

public class DatabaseInitializer {

    public static void main(String[] args) {
        System.out.println("Initializing Database...");
        try {
            initializePartyTable();
            System.out.println("Database initialization completed successfully.");
        } catch (Exception e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void initializePartyTable() throws Exception {
        System.out.println("Creating 'party' table...");
        String sql = loadSqlScript("sql/party_schema.sql");
        
        try (Connection conn = MySQLDAOFactory.createConnection();
             Statement stmt = conn.createStatement()) {
            
            // Basic splitter for simplicity since we have DROP and CREATE
            String[] statements = sql.split(";");
            for (String s : statements) {
                if (!s.trim().isEmpty()) {
                   stmt.executeUpdate(s);
                }
            }
            System.out.println("Table 'party' reset successfully.");
        }
    }

    private static String loadSqlScript(String path) throws Exception {
        try (InputStream is = DatabaseInitializer.class.getClassLoader().getResourceAsStream(path)) {
            if (is == null) {
                throw new RuntimeException("SQL script not found: " + path);
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                return reader.lines().collect(Collectors.joining("\n"));
            }
        }
    }
}
