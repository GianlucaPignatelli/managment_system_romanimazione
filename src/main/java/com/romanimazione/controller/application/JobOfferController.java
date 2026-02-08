package com.romanimazione.controller.application;

import com.romanimazione.bean.PartyBean;
import com.romanimazione.bean.UserBean;
import com.romanimazione.dao.DAOFactory;
import com.romanimazione.dao.PartyDAO;
import com.romanimazione.entity.AssignmentStatus;
import com.romanimazione.entity.Party;
import com.romanimazione.exception.DAOException;
import java.util.ArrayList;
import java.util.List;

public class JobOfferController extends Subject {

    public List<PartyBean> getPendingOffers(UserBean animator) throws DAOException {
        if (animator == null || animator.getUsername() == null) {
             throw new IllegalArgumentException("Invalid animator");
        }
        
        PartyDAO dao = DAOFactory.getDAOFactory().getPartyDAO();
        List<Party> proposed = dao.findJobOffers(animator.getUsername());
        List<PartyBean> beans = new ArrayList<>();
        
        for (Party p : proposed) {
            beans.add(PartyBean.fromEntity(p));
        }
        return beans;
    }
    
    public void acceptOffer(PartyBean party, UserBean animator) throws DAOException {
        if (party == null || animator == null) throw new IllegalArgumentException("Null arguments");
        
        PartyDAO dao = DAOFactory.getDAOFactory().getPartyDAO();
        
        // 1. Accept the current offer
        dao.updateAssignmentStatus(party.getId(), animator.getUsername(), AssignmentStatus.ACCEPTED);
        
        // 2. Auto-reject other pending offers on the SAME DAY
        List<Party> allOffers = dao.findJobOffers(animator.getUsername());
        
        for (Party other : allOffers) {
            if (other.getId() == party.getId()) continue; // Skip current
            
            // Check Date Conflict
            if (other.getDate().equals(party.getDate())) {
                AssignmentStatus status = other.getAssignmentStatuses().get(animator.getUsername());
                // Only reject if it is currently PENDING
                if (status == AssignmentStatus.PENDING) {
                    dao.updateAssignmentStatus(other.getId(), animator.getUsername(), AssignmentStatus.REJECTED);
                    System.out.println("System: Auto-rejected conflicting offer for party " + other.getId());
                }
            }
        }
        
        notifyObservers("Offer accepted for party: " + party.getName());
    }
    
    public void rejectOffer(PartyBean party, UserBean animator) throws DAOException {
        if (party == null || animator == null) throw new IllegalArgumentException("Null arguments");
        
        // Update Status
        DAOFactory.getDAOFactory().getPartyDAO().updateAssignmentStatus(party.getId(), animator.getUsername(), AssignmentStatus.REJECTED);
        
        notifyObservers("Offer rejected for party: " + party.getName());
    }
}
