package com.romanimazione.controller.application;

import com.romanimazione.bean.UserBean;
import com.romanimazione.dao.DAOFactory;
import com.romanimazione.dao.PartyDAO;
import com.romanimazione.dao.UserDAO;
import com.romanimazione.entity.Party;
import com.romanimazione.entity.User;
import com.romanimazione.exception.DAOException;
import java.util.ArrayList;
import java.util.List;

public class AdminUserController extends Subject {

    public List<UserBean> getAllUsers() throws DAOException {
        UserDAO userDAO = DAOFactory.getDAOFactory().getUserDAO();
        List<User> entities = userDAO.findAllUsers();
        
        List<UserBean> beans = new ArrayList<>();
        for (User u : entities) {
            UserBean b = new UserBean();
            b.setId(u.getId());
            b.setUsername(u.getUsername());
            b.setNome(u.getNome());
            b.setCognome(u.getCognome());
            b.setRole(u.getRole());
            b.setEmail(u.getEmail());
            b.setSuperAdmin(u.isSuperAdmin());
            beans.add(b);
        }
        return beans;
    }

    public void deleteUser(UserBean userToDelete) throws DAOException, IllegalArgumentException {
        if (userToDelete == null || userToDelete.getUsername() == null) {
            throw new IllegalArgumentException("Invalid user to delete");
        }
        
        if (userToDelete.isSuperAdmin()) {
            throw new IllegalArgumentException("Cannot delete a Super Admin.");
        }

        UserDAO userDAO = DAOFactory.getDAOFactory().getUserDAO();
        PartyDAO partyDAO = DAOFactory.getDAOFactory().getPartyDAO();

        // Check if Animator and has assignments
        if ("ANIMATORE".equalsIgnoreCase(userToDelete.getRole())) {
            List<Party> allParties = partyDAO.findAllParties();
            for (Party p : allParties) {
                if (p.getAssignmentStatuses().containsKey(userToDelete.getUsername())) {
                     throw new IllegalArgumentException("Cannot delete Animator " + userToDelete.getUsername() + 
                        " because they have active party assignments. Please remove them from parties first.");
                }
            }
        }
        
        userDAO.deleteUser(userToDelete.getUsername());
        notifyObservers("User " + userToDelete.getUsername() + " deleted successfully.");
    }
}
