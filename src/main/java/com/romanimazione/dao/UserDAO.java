package com.romanimazione.dao;

import com.romanimazione.entity.User;
import com.romanimazione.exception.DAOException;

public interface UserDAO {
    void saveUser(User user) throws DAOException;
    void updateUser(User user) throws DAOException;
    void deleteUser(String username) throws DAOException;
    java.util.List<User> findAllUsers() throws DAOException;
}
