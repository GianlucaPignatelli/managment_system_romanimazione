package com.romanimazione.dao;

import com.romanimazione.entity.User;
import com.romanimazione.entity.Animatore;
import com.romanimazione.exception.DAOException;

public class UserDAODemo implements UserDAO {
    
    private static final java.util.List<User> MOCK_USERS = new java.util.ArrayList<>();

    // Static init block to add default demo user
    static {
        MOCK_USERS.add(new Animatore("demo", "pass", "Demo", "User", "demo@mail.com"));
    }

    @Override
    public User findUserByIdentifier(String identifier) throws DAOException {
        for (User u : MOCK_USERS) {
            if (u.getUsername().equals(identifier) || u.getEmail().equals(identifier)) {
                return u;
            }
        }
        return null;
    }

    @Override
    public void saveUser(User user) throws DAOException {
        // Check duplicate
        if (findUserByIdentifier(user.getUsername()) != null) {
            throw new DAOException("User already exists in Demo DB");
        }
        MOCK_USERS.add(user);
        System.out.println("Demo: Resistered user " + user.getUsername() + " to in-memory list.");
    }
}
