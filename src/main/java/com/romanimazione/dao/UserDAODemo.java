package com.romanimazione.dao;

import com.romanimazione.entity.User;
import com.romanimazione.entity.Animatore;
import com.romanimazione.exception.DAOException;

public class UserDAODemo implements UserDAO {
    @Override
    public User findUserByIdentifier(String identifier) throws DAOException {
        // Return a mock user
        if ("demo".equals(identifier) || "demo@gmail.com".equals(identifier)) {
            return new Animatore("demo", "pass", "Demo", "User", "demo@mail.com");
        }
        return null;
    }

    @Override
    public void saveUser(User user) throws DAOException {
        System.out.println("Demo: User saved " + user.getUsername());
    }
}
