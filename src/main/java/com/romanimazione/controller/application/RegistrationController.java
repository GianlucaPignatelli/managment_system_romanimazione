package com.romanimazione.controller.application;

import com.romanimazione.bean.UserBean;
import com.romanimazione.dao.DAOFactory;
import com.romanimazione.dao.UserDAO;
import com.romanimazione.entity.Amministratore;
import com.romanimazione.entity.Animatore;
import com.romanimazione.entity.User;

public class RegistrationController extends Subject {

    public void register(UserBean userBean) throws Exception {
        DAOFactory daoFactory = DAOFactory.getDAOFactory();
        UserDAO userDAO = daoFactory.getUserDAO();

        // Check availability
        if (userDAO.findUserByIdentifier(userBean.getUsername()) != null) {
            throw new Exception("Username already exists");
        }

        // Validate Email
        if (userBean.getEmail() == null || !userBean.getEmail().endsWith("@gmail.com")) {
            throw new Exception("Email must be a valid @gmail.com address");
        }

        User user;
        if ("AMMINISTRATORE".equalsIgnoreCase(userBean.getRole())) {
            user = new Amministratore();
        } else {
            user = new Animatore();
        }

        user.setUsername(userBean.getUsername());
        user.setPassword(userBean.getPassword());
        user.setNome(userBean.getNome());
        user.setCognome(userBean.getCognome());
        user.setEmail(userBean.getEmail());

        userDAO.saveUser(user);
        
        notifyObservers("Registration Successful for " + user.getUsername());
    }
}
