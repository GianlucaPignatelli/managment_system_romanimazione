package com.romanimazione.dao;

import com.romanimazione.entity.User;
import com.romanimazione.exception.DAOException;
import java.util.List;

public class UserDAOFile implements UserDAO {
    
    private final java.io.File file;
    private final com.fasterxml.jackson.databind.ObjectMapper mapper;

    public UserDAOFile() {
        this.file = new java.io.File("users.json");
        this.mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        // Activate default typing to handle Polymorphism (User/Animatore/Amministratore)
        // This adds type info to JSON.
        mapper.activateDefaultTyping(
            mapper.getPolymorphicTypeValidator(), 
            com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping.NON_FINAL
        );
        mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        mapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    public User findUserByIdentifier(String identifier) throws DAOException {
        List<User> users = loadUsers();
        for (User u : users) {
            if (u.getUsername().equals(identifier) || u.getEmail().equals(identifier)) {
                return u;
            }
        }
        return null;
    }

    @Override
    public void saveUser(User user) throws DAOException {
        List<User> users = loadUsers();
        // Check duplicate
        for (User u : users) {
             if (u.getUsername().equals(user.getUsername())) throw new DAOException("User already exists");
        }
        users.add(user);
        saveUsers(users);
    }

    private List<User> loadUsers() throws DAOException {
        if (!file.exists()) return new java.util.ArrayList<>();
        try {
            // Deserialize list of Users
            return mapper.readValue(file, new com.fasterxml.jackson.core.type.TypeReference<List<User>>(){});
        } catch (java.io.IOException e) {
            throw new DAOException("Error reading users file: " + e.getMessage(), e);
        }
    }

    private void saveUsers(List<User> users) throws DAOException {
        try {
            mapper.writeValue(file, users);
        } catch (java.io.IOException e) {
            throw new DAOException("Error writing users file: " + e.getMessage(), e);
        }
    }
}
