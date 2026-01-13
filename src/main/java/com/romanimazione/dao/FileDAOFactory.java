package com.romanimazione.dao;

public class FileDAOFactory extends DAOFactory {
    @Override
    public UserDAO getUserDAO() {
        return new UserDAOFile(); // Need to create this
    }
}
