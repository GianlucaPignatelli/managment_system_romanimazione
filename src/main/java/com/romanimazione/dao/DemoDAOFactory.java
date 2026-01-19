package com.romanimazione.dao;

public class DemoDAOFactory extends DAOFactory {
    @Override
    public UserDAO getUserDAO() {
        return new UserDAODemo();
    }

    @Override
    public AvailabilityDAO getAvailabilityDAO() {
        return new AvailabilityDAODemo();
    }
}
