package com.romanimazione.controller.application;

import com.romanimazione.bean.PartyBean;
import com.romanimazione.bean.UserBean;
import com.romanimazione.dao.AvailabilityDAO;
import com.romanimazione.dao.DAOFactory;
import com.romanimazione.dao.PartyDAO;
import com.romanimazione.dao.UserDAO;
import com.romanimazione.entity.Party;
import com.romanimazione.entity.User;
import com.romanimazione.exception.DAOException;
import com.romanimazione.exception.InvalidPartyException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PartyController extends Subject {

    private static final List<String> ALLOWED_TYPES = Arrays.asList(
        "Full Party", "Smart Party", "Consegna", "Ritiro in sede", "Servizio carretti", "Evento in piazza"
    );

    public void createParty(PartyBean bean) throws InvalidPartyException, DAOException {
        validateMandatoryFields(bean);
        validateType(bean);
        validateDate(bean);
        validateTime(bean);
        validateNumbers(bean);

        // Persistence
        PartyDAO dao = DAOFactory.getDAOFactory().getPartyDAO();
        dao.saveParty(bean.toEntity());
        
        // Notify View
        notifyObservers("Party Created Successfully");
    }

    private void validateMandatoryFields(PartyBean bean) throws InvalidPartyException {
        if (bean.getName() == null || bean.getName().trim().isEmpty()) throw new InvalidPartyException("Party name is required.");
        if (bean.getAddress() == null || bean.getAddress().trim().isEmpty()) throw new InvalidPartyException("Address is required.");
        if (bean.getClientName() == null || bean.getClientName().trim().isEmpty()) throw new InvalidPartyException("Client Name is required.");
        if (bean.getClientPhone() == null || !bean.getClientPhone().matches("^\\d{10}$")) {
            throw new InvalidPartyException("Client Phone must be exactly 10 digits.");
        }
    }

    private void validateType(PartyBean bean) throws InvalidPartyException {
        if (bean.getType() == null || !ALLOWED_TYPES.contains(bean.getType())) {
            throw new InvalidPartyException("Invalid Party Type. Allowed: " + ALLOWED_TYPES);
        }
    }

    private void validateDate(PartyBean bean) throws InvalidPartyException {
        if (bean.getDate() == null || bean.getDate().isBefore(LocalDate.now())) {
            throw new InvalidPartyException("Date must be today or in the future.");
        }
    }

    private void validateTime(PartyBean bean) throws InvalidPartyException {
        if (bean.getStartTime() == null || bean.getEndTime() == null) {
            throw new InvalidPartyException("Start and End times are required.");
        }
        if (!bean.getEndTime().isAfter(bean.getStartTime())) {
            throw new InvalidPartyException("End Time must be after Start Time.");
        }
        
        // Strict Rule: If date is today, start time must be in the future
        // Strict Rule: If date is today, start time must be in the future
        if (bean.getDate() != null && bean.getDate().isEqual(LocalDate.now()) && 
            bean.getStartTime().isBefore(java.time.LocalTime.now())) {
            throw new InvalidPartyException("Cannot schedule a party in the past on the current day.");
        }
    }

    private void validateNumbers(PartyBean bean) throws InvalidPartyException {
        if (bean.getAnimatorsRequired() < 1) throw new InvalidPartyException("At least 1 animator is required.");
        if (bean.getCost() < 0) throw new InvalidPartyException("Cost cannot be negative.");
    }

    public List<PartyBean> getAllParties() throws DAOException {
        PartyDAO dao = DAOFactory.getDAOFactory().getPartyDAO();
        List<Party> entities = dao.findAllParties();
        List<PartyBean> beans = new ArrayList<>();
        
        for (Party p : entities) {
            beans.add(PartyBean.fromEntity(p));
        }
        
        // Sort by Date (Ascending) -> Time (Ascending)
        // Sort by Date (Ascending) -> Time (Ascending)
        beans.sort((p1, p2) -> {
            if (p1.getDate() == null && p2.getDate() == null) return 0;
            if (p1.getDate() == null) return 1;
            if (p2.getDate() == null) return -1;
            
            int dateComp = p1.getDate().compareTo(p2.getDate());
            if (dateComp != 0) return dateComp;
            
            if (p1.getStartTime() == null && p2.getStartTime() == null) return 0;
            if (p1.getStartTime() == null) return 1;
            if (p2.getStartTime() == null) return -1;
            
            return p1.getStartTime().compareTo(p2.getStartTime());
        });
        
        return beans;
    }
    
    public List<String> getPartyTypes() {
        return ALLOWED_TYPES;
    }

    public List<UserBean> findEligibleAnimators(PartyBean party) throws DAOException {
        AvailabilityDAO availabilityDAO = DAOFactory.getDAOFactory().getAvailabilityDAO();
        UserDAO userDAO = DAOFactory.getDAOFactory().getUserDAO();
        
        List<String> usernames = availabilityDAO.findAvailableAnimators(party.getDate(), party.getStartTime(), party.getEndTime());
        List<UserBean> fullAnimators = new ArrayList<>();
        
        for (String username : usernames) {
            User user = userDAO.findUserByIdentifier(username);
            
            // Exclude already assigned animators (Optional check, good for UI filtering)
            List<String> assigned = DAOFactory.getDAOFactory().getPartyDAO().getAssignedAnimators(party.getId());
            if (user != null && !assigned.contains(username)) {
                UserBean animatorBean = new UserBean();
                animatorBean.setUsername(user.getUsername());
                animatorBean.setNome(user.getNome());
                animatorBean.setCognome(user.getCognome());
                animatorBean.setEmail(user.getEmail());
                animatorBean.setRole(user.getRole());
                fullAnimators.add(animatorBean);
            }
        }
        return fullAnimators;
    }

    public void assignAnimator(PartyBean party, UserBean animator) throws DAOException {
        if (party == null || animator == null) {
            throw new IllegalArgumentException("Party and Animator cannot be null");
        }
        
        PartyDAO dao = DAOFactory.getDAOFactory().getPartyDAO();
        List<String> assigned = dao.getAssignedAnimators(party.getId());
        
        if (assigned.size() >= party.getAnimatorsRequired()) {
            throw new IllegalArgumentException("Cannot assign more animators. Limit reached (" + party.getAnimatorsRequired() + ").");
        }
        
        dao.assignAnimator(party.getId(), animator.getUsername());
        notifyObservers("Animator " + animator.getUsername() + " assigned to party " + party.getId());
    }

    public void cancelParty(PartyBean party) throws DAOException {
        if (party == null) throw new IllegalArgumentException("Party cannot be null");
        
        PartyDAO dao = DAOFactory.getDAOFactory().getPartyDAO();
        dao.updateStatus(party.getId(), com.romanimazione.entity.PartyStatus.CANCELLED);
        
        // Logic Requirement: Notify assigned animators (Mock notification for now)
        List<String> assigned = dao.getAssignedAnimators(party.getId());
        if (!assigned.isEmpty()) {
            System.out.println("SYSTEM NOTIFICATION: Sending cancellation email to " + assigned);
        }
        
        notifyObservers("Party " + party.getId() + " has been CANCELLED.");
    }
}
