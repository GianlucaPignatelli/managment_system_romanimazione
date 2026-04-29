package com.romanimazione.dao.demo;

import com.romanimazione.dao.UserDAO;
import com.romanimazione.entity.User;
import com.romanimazione.exception.DAOException;

public class UserDAODemo implements UserDAO {
    
    private static final java.util.List<User> MOCK_USERS = new java.util.ArrayList<>();

    // Static init block - Default users removed as per user request for consistency
    static {
        // Empty to start clean
    }

    @Override
    public void saveUser(User user) throws DAOException {
        // Check duplicate
        boolean duplicate = MOCK_USERS.stream()
                .anyMatch(u -> u.getUsername().equals(user.getUsername()));
        if (duplicate) {
            throw new DAOException("User already exists in Demo DB");
        }
        MOCK_USERS.add(user);
        System.out.println("Demo: Registered user " + user.getUsername());
    }

    @Override
    public void updateUser(User user) throws DAOException {
        boolean found = false;
        for (int i = 0; i < MOCK_USERS.size(); i++) {
            if (MOCK_USERS.get(i).getUsername().equals(user.getUsername())) {
                user.setId(MOCK_USERS.get(i).getId());
                MOCK_USERS.set(i, user);
                found = true;
                break;
            }
        }
        if (!found) throw new DAOException("User not found");
    }

    @Override
    public void deleteUser(String username) throws DAOException {
        MOCK_USERS.removeIf(u -> u.getUsername().equals(username));
    }

    @Override
    public java.util.List<User> findAllUsers() {
        return new java.util.ArrayList<>(MOCK_USERS);
    }
}
