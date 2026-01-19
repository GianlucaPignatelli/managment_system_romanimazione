package com.romanimazione.dao.file;

import com.romanimazione.dao.DAOFactory;
import com.romanimazione.dao.UserDAO;
import com.romanimazione.dao.AvailabilityDAO;

public class FileDAOFactory extends DAOFactory {
    @Override
    public UserDAO getUserDAO() {
        return new UserDAOFile();
    }

    @Override
    public AvailabilityDAO getAvailabilityDAO() {
        return new AvailabilityDAOFile();
    }
}
