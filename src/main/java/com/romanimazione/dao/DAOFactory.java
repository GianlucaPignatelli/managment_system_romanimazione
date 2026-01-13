package com.romanimazione.dao;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public abstract class DAOFactory {

    public static final int MYSQL = 1;
    public static final int FILESYSTEM = 2;
    public static final int DEMO = 3;

    public abstract UserDAO getUserDAO();

    public static DAOFactory getDAOFactory(int whichFactory) {
        switch (whichFactory) {
            case MYSQL:
                return new MySQLDAOFactory();
            case FILESYSTEM:
                return new FileDAOFactory();
            case DEMO:
                return new DemoDAOFactory();
            default:
                return null;
        }
    }
    
    // Helper to get from config
    public static DAOFactory getDAOFactory() {
        Properties prop = new Properties();
        try (InputStream input = DAOFactory.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return new DemoDAOFactory(); // Default fallback
            }
            prop.load(input);
            String type = prop.getProperty("persistence_type");
            if ("MYSQL".equalsIgnoreCase(type)) return getDAOFactory(MYSQL);
            if ("FILESYSTEM".equalsIgnoreCase(type)) return getDAOFactory(FILESYSTEM);
            return getDAOFactory(DEMO);
        } catch (IOException ex) {
            ex.printStackTrace();
            return new DemoDAOFactory();
        }
    }
}
