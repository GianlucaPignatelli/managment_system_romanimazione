package com.romanimazione.dao;

import com.romanimazione.entity.User;
import com.romanimazione.exception.DAOException;

public interface UserDAO {
    User findUserByIdentifier(String identifier) throws DAOException;
    void saveUser(User user) throws DAOException;
    
    // Security & Management
    long countAdmins() throws DAOException;
    void deleteUser(String username) throws DAOException;
    java.util.List<User> findAllUsers() throws DAOException;
}
