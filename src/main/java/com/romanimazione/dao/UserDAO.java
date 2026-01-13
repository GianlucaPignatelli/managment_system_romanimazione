package com.romanimazione.dao;

import com.romanimazione.entity.User;
import com.romanimazione.exception.DAOException; // I'll create this exception later, or assume generic Exception for now.

public interface UserDAO {
    User findUserByIdentifier(String identifier) throws Exception;
    void saveUser(User user) throws Exception;
}
