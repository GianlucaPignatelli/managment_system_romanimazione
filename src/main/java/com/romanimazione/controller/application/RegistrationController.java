package com.romanimazione.controller.application;

import com.romanimazione.bean.UserBean;
import com.romanimazione.dao.DAOFactory;
import com.romanimazione.dao.UserDAO;
import com.romanimazione.entity.Amministratore;
import com.romanimazione.entity.Animatore;
import com.romanimazione.entity.User;
import com.romanimazione.exception.DAOException;
import com.romanimazione.exception.DuplicateUserException;

public class RegistrationController extends Subject {

    public long countAdmins() throws DAOException {
        return DAOFactory.getDAOFactory().getUserDAO().countAdmins();
    }

    public void register(UserBean userBean) throws DAOException, DuplicateUserException, IllegalArgumentException {
        DAOFactory daoFactory = DAOFactory.getDAOFactory();
        UserDAO userDAO = daoFactory.getUserDAO();

        checkAvailability(userDAO, userBean);
        validateEmail(userBean);

        User user;
        if ("AMMINISTRATORE".equalsIgnoreCase(userBean.getRole())) {
            user = createAdminUser(userBean, userDAO);
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

    private void checkAvailability(UserDAO userDAO, UserBean userBean) throws DAOException, DuplicateUserException {
        if (userDAO.findUserByIdentifier(userBean.getUsername()) != null) {
            throw new DuplicateUserException("Username already exists");
        }
    }

    private void validateEmail(UserBean userBean) {
        if (userBean.getEmail() == null || !userBean.getEmail().endsWith("@gmail.com")) {
            throw new IllegalArgumentException("Email must be a valid @gmail.com address");
        }
    }

    private User createAdminUser(UserBean userBean, UserDAO userDAO) throws DAOException {
        User user = new Amministratore();
        long adminCount = userDAO.countAdmins();
        com.romanimazione.bean.SecurityManager secManager = com.romanimazione.bean.SecurityManager.getInstance();

        if (!secManager.isMasterCodeSet() || adminCount == 0) {
            if (userBean.getSecurityCode() == null || userBean.getSecurityCode().length() < 64) {
                throw new IllegalArgumentException("You must create a Master Code (min 64 chars) to initialize the system security.");
            }
            secManager.setMasterCode(userBean.getSecurityCode());
            user.setSuperAdmin(true);
        } else {
            if (!secManager.verifyMasterCode(userBean.getSecurityCode())) {
                throw new IllegalArgumentException("Invalid Master Code. Registration denied.");
            }
            // Simplified boolean assignment logic
            user.setSuperAdmin(adminCount == 0);
        }
        return user;
    }
}
