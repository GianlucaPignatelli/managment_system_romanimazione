package com.romanimazione.dao;

import com.romanimazione.entity.User;
import com.romanimazione.exception.DAOException;

public class UserDAOFile implements UserDAO {
    @Override
    public User findUserByIdentifier(String identifier) throws DAOException {
        // TODO: Implement JSON/CSV reading
        return null;
    }

    @Override
    public void saveUser(User user) throws DAOException {
        // TODO: Implement JSON/CSV writing
    }
}
