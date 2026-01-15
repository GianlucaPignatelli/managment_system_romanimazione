package com.romanimazione.controller.application;

import com.romanimazione.bean.CredentialsBean;
import com.romanimazione.bean.UserBean;
import com.romanimazione.dao.DAOFactory;
import com.romanimazione.dao.UserDAO;
import com.romanimazione.entity.User;
import com.romanimazione.exception.DAOException;
import com.romanimazione.exception.UserNotFoundException;

public class LoginController extends Subject {

    public UserBean login(CredentialsBean credentials) throws DAOException, UserNotFoundException {
        DAOFactory daoFactory = DAOFactory.getDAOFactory();
        UserDAO userDAO = daoFactory.getUserDAO();
        User user = userDAO.findUserByIdentifier(credentials.getUsername());

        if (user == null || !user.getPassword().equals(credentials.getPassword())) {
            throw new UserNotFoundException("Invalid username/email or password");
        }

        // Strict Case Sensitivity Check
        String inputId = credentials.getUsername();
        boolean matchesUsername = user.getUsername().equals(inputId); // Exact case match
        boolean matchesEmail = user.getEmail() != null && user.getEmail().equals(inputId); // Exact case match

        if (!matchesUsername && !matchesEmail) {
             throw new UserNotFoundException("Invalid identifier (Case Sensitive mismatch)");
        }

        UserBean userBean = new UserBean();
        userBean.setUsername(user.getUsername());
        userBean.setRole(user.getRole());
        userBean.setNome(user.getNome());
        userBean.setCognome(user.getCognome());
        userBean.setEmail(user.getEmail());

        // Notify observers? e.g. "User Logged In"
        notifyObservers("Login Successful for " + user.getUsername());
        
        return userBean;
    }
}
