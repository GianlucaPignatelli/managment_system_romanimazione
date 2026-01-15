package com.romanimazione.dao;

import com.romanimazione.entity.User;
import com.romanimazione.exception.DAOException;

public class UserDAOFile implements UserDAO {
    @Override
    public User findUserByIdentifier(String identifier) throws DAOException {
        // Not implemented yet - File persistence planned for future release
        return null;
    }

    @Override
    public void saveUser(User user) throws DAOException {
        // Not implemented yet - File persistence planned for future release
    }
}
