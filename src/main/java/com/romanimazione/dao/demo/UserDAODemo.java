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
        System.out.println("Demo: Registered user " + user.getUsername());
    }

    @Override
    public long countAdmins() {
        return MOCK_USERS.stream()
            .filter(u -> "AMMINISTRATORE".equalsIgnoreCase(u.getRole()))
            .count();
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
