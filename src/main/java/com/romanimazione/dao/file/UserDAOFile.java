package com.romanimazione.dao.file;

import com.fasterxml.jackson.core.type.TypeReference;
import com.romanimazione.dao.UserDAO;
import com.romanimazione.entity.User;
import com.romanimazione.exception.DAOException;
import java.util.List;

public class UserDAOFile extends GenericFileDAO<User> implements UserDAO {

    public UserDAOFile() {
        super("users.json");
        try {
            List<User> users = load(new TypeReference<List<User>>(){});
            boolean dirty = false;

            if (users.isEmpty()) {
                System.out.println("File: No users found. Starting fresh.");
            } else {
                // Sanitize existing users with ID 0
                int maxId = users.stream().mapToInt(User::getId).max().orElse(0);
                for (User u : users) {
                    if (u.getId() == 0) {
                        maxId++;
                        u.setId(maxId);
                        dirty = true;
                    }
                }
            }

            if (dirty) save(users);
        } catch (DAOException e) {
            System.err.println("Error initializing UserDAOFile: " + e.getMessage());
        }
    }

    @Override
    public void saveUser(User user) throws DAOException {
        List<User> users = load(new TypeReference<List<User>>(){});
        for (User u : users) {
             if (u.getUsername().equals(user.getUsername())) throw new DAOException("User already exists");
        }
        int maxId = users.stream().mapToInt(User::getId).max().orElse(0);
        user.setId(maxId + 1);
        
        users.add(user);
        save(users);
    }

    @Override
    public void updateUser(User user) throws DAOException {
        List<User> users = load(new TypeReference<List<User>>(){});
        boolean found = false;
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUsername().equals(user.getUsername())) {
                user.setId(users.get(i).getId());
                users.set(i, user);
                found = true;
                break;
            }
        }
        if (!found) throw new DAOException("User not found");
        save(users);
    }

    @Override
    public void deleteUser(String username) throws DAOException {
        List<User> users = load(new TypeReference<List<User>>(){});
        boolean removed = users.removeIf(u -> u.getUsername().equals(username));
        if (removed) save(users);
        else throw new DAOException("User not found");
    }

    @Override
    public List<User> findAllUsers() throws DAOException {
        return load(new TypeReference<List<User>>(){});
    }
}
