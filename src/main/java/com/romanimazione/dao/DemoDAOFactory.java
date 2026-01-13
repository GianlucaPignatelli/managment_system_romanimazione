package com.romanimazione.dao;

public class DemoDAOFactory extends DAOFactory {
    @Override
    public UserDAO getUserDAO() {
        return new UserDAODemo(); // Need to create this
    }
}
