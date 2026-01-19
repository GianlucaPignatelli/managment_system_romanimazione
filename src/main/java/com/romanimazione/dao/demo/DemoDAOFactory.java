package com.romanimazione.dao.demo;

import com.romanimazione.dao.DAOFactory;
import com.romanimazione.dao.UserDAO;
import com.romanimazione.dao.AvailabilityDAO;

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
