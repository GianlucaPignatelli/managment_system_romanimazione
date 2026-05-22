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
        List<Party> allParties = dao.findAllParties();
        List<PartyBean> beans = new ArrayList<>();
        
        for (Party p : allParties) {
            AssignmentStatus status = p.getAssignmentStatuses().get(animator.getUsername());
            if (status == AssignmentStatus.PENDING) {
                java.time.LocalDateTime ts = p.getAssignmentTimestamps().get(animator.getUsername());
                if (ts != null && ts.isBefore(java.time.LocalDateTime.now().minusHours(24))) {
                    p.getAssignmentStatuses().put(animator.getUsername(), AssignmentStatus.TIMEOUT);
                    dao.update(p);
                    continue;
                }
                
                PartyBean pb = PartyController.mapToBean(p);
                beans.add(pb);
            }
        }
        return beans;
    }
    
    public void acceptOffer(PartyBean party, UserBean animator) throws DAOException {
        if (party == null || animator == null) throw new IllegalArgumentException("Null arguments");
        
        PartyDAO dao = DAOFactory.getDAOFactory().getPartyDAO();
        
        Party targetParty = dao.getPartyById(Integer.parseInt(party.getId()));
        
        com.romanimazione.entity.Animator animatorEntity = new com.romanimazione.entity.Animator();
        animatorEntity.setUsername(animator.getUsername());
        animatorEntity.addAcceptedJob(targetParty);
        
        targetParty.getAssignmentStatuses().put(animator.getUsername(), AssignmentStatus.ACCEPTED);
        dao.update(targetParty);
        
        // 2. Auto-reject other pending offers on the SAME DAY (Applicative Logic applied iteratively)
        List<Party> allOffers = dao.findAllParties();
        for (Party other : allOffers) {
            if (String.valueOf(other.getId()).equals(party.getId())) continue;
            
            if (other.getDate().toString().equals(party.getDate())) {
                AssignmentStatus status = other.getAssignmentStatuses().get(animator.getUsername());
                if (status == AssignmentStatus.PENDING) {
                    other.getAssignmentStatuses().put(animator.getUsername(), AssignmentStatus.REJECTED);
                    dao.update(other);
                    System.out.println("System: Auto-rejected conflicting offer for party " + other.getId());
                }
            }
        }
        
        notifyObservers("Offer accepted for party: " + party.getName());
    }
    
    public void rejectOffer(PartyBean party, UserBean animator) throws DAOException {
        if (party == null || animator == null) throw new IllegalArgumentException("Null arguments");
        
        PartyDAO dao = DAOFactory.getDAOFactory().getPartyDAO();
        Party targetParty = dao.getPartyById(Integer.parseInt(party.getId()));
        targetParty.getAssignmentStatuses().put(animator.getUsername(), AssignmentStatus.REJECTED);
        dao.update(targetParty);
        
        notifyObservers("Offer rejected for party: " + party.getName());
    }
}
