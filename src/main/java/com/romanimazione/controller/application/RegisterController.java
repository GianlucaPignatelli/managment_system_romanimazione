package com.romanimazione.controller.application;

import com.romanimazione.bean.UserBean;
import com.romanimazione.dao.DAOFactory;
import com.romanimazione.dao.UserDAO;
import com.romanimazione.entity.Amministratore;
import com.romanimazione.entity.Animatore;
import com.romanimazione.entity.User;
import com.romanimazione.exception.DAOException;

public class RegisterController {

    public void register(UserBean userBean) throws DAOException {
        UserDAO dao = DAOFactory.getDAOFactory().getUserDAO();
        
        // Basic Validation (in real app, more strict)
        if (userBean.getUsername() == null || userBean.getUsername().isEmpty()) {
            throw new IllegalArgumentException("Username is required");
        }
        
        // Check duplicate
        if (dao.findUserByIdentifier(userBean.getUsername()) != null) {
            throw new IllegalArgumentException("Username already exists");
        }

        User user;
        if ("ANIMATORE".equalsIgnoreCase(userBean.getRole())) {
            user = new Animatore();
        } else if ("AMMINISTRATORE".equalsIgnoreCase(userBean.getRole())) {
            user = new Amministratore();
        } else {
            user = new User();
        }
        
        user.setUsername(userBean.getUsername());
        user.setPassword(userBean.getPassword());
        user.setNome(userBean.getNome());
        user.setCognome(userBean.getCognome());
        user.setEmail(userBean.getEmail());
        user.setRole(userBean.getRole());
        
        dao.saveUser(user);
    }
}
