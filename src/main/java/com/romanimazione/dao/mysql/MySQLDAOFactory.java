package com.romanimazione.dao.mysql;

import com.romanimazione.dao.DAOFactory;
import com.romanimazione.dao.UserDAO;
import com.romanimazione.dao.AvailabilityDAO;
import com.romanimazione.exception.DAOException; // If needed, or check imports
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.InputStream;
import java.util.Properties;

public class MySQLDAOFactory extends DAOFactory {

    private static String connectionUrl;
    private static String loginUser;
    private static String loginPass;

    static {
        Properties prop = new Properties();
        try (InputStream input = MySQLDAOFactory.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (input != null) {
                prop.load(input);
                connectionUrl = prop.getProperty("CONNECTION_URL");
                loginUser = prop.getProperty("LOGIN_USER");
                loginPass = prop.getProperty("LOGIN_PASS");
            }
        } catch (Exception e) {
            System.err.println("Database Configuration Error: " + e.getMessage());
        }
    }

    public static Connection createConnection() throws SQLException {
        return DriverManager.getConnection(connectionUrl, loginUser, loginPass);
    }

    @Override
    public UserDAO getUserDAO() {
        return new UserDAOMySQL();
    }

    @Override
    public AvailabilityDAO getAvailabilityDAO() {
        return new AvailabilityDAOMySQL();
    }
}
