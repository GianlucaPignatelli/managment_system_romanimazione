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
        dao.checkTimeouts();
        
        List<Party> entities = dao.findAllParties();
        List<PartyBean> beans = new ArrayList<>();
        
        for (Party p : entities) {
            PartyBean pb = PartyBean.fromEntity(p);
            // Fetch assignments to populate the status map used by CLI and details
            List<String> assignees = dao.getAssignedAnimators(p.getId());
            for (String user : assignees) {
                com.romanimazione.entity.AssignmentStatus status = dao.getAssignmentStatus(p.getId(), user);
                if (status != null) {
                    pb.getAssignmentStatuses().put(user, status);
                }
                java.time.LocalDateTime assignedAt = dao.getAssignmentTimestamp(p.getId(), user);
                if (assignedAt != null) {
                    pb.getAssignmentTimestamps().put(user, assignedAt);
                }
            }
            beans.add(pb);
        }
        
        beans.sort(this::compareParties);
        
        return beans;
    }

    private int compareParties(PartyBean p1, PartyBean p2) {
        if (p1.getDate() == null && p2.getDate() == null) return 0;
        if (p1.getDate() == null) return 1;
        if (p2.getDate() == null) return -1;
        
        int dateComp = p1.getDate().compareTo(p2.getDate());
        if (dateComp != 0) return dateComp;
        
        if (p1.getStartTime() == null && p2.getStartTime() == null) return 0;
        if (p1.getStartTime() == null) return 1;
        if (p2.getStartTime() == null) return -1;
        
        return p1.getStartTime().compareTo(p2.getStartTime());
    }
    
    public List<String> getPartyTypes() {
        return ALLOWED_TYPES;
    }

    public List<UserBean> findEligibleAnimators(PartyBean party) throws DAOException {
        AvailabilityDAO availabilityDAO = DAOFactory.getDAOFactory().getAvailabilityDAO();
        UserDAO userDAO = DAOFactory.getDAOFactory().getUserDAO();
        PartyDAO partyDAO = DAOFactory.getDAOFactory().getPartyDAO();

        // 1. Get ALL availabilities for the date
        List<com.romanimazione.entity.Availability> allAvailabilities = availabilityDAO.findByDate(party.getDate());
        
        // 2. Group by username (Animator might have multiple slots)
        java.util.Map<String, List<com.romanimazione.entity.Availability>> grouped = allAvailabilities.stream()
                .collect(java.util.stream.Collectors.groupingBy(com.romanimazione.entity.Availability::getUsername));
        
        // 3. Get Excluded (Already Assigned)
        List<String> assigned = partyDAO.getAssignedAnimators(party.getId());
        
        List<UserBean> result = new ArrayList<>();
        
        for (java.util.Map.Entry<String, List<com.romanimazione.entity.Availability>> entry : grouped.entrySet()) {
            String username = entry.getKey();
            
            // Skip if already assigned
            if (assigned.contains(username)) {
                continue;
            }
            
            User user = userDAO.findUserByIdentifier(username);
            
            if (user != null) {
                UserBean animatorBean = new UserBean();
                animatorBean.setUsername(user.getUsername());
                animatorBean.setNome(user.getNome());
                animatorBean.setCognome(user.getCognome());
                animatorBean.setEmail(user.getEmail());
                animatorBean.setRole(user.getRole());
                
                // Check Compatibility Logic
                boolean isTimeCompatible = false;
                List<com.romanimazione.entity.Availability> slots = entry.getValue();
                
                for (com.romanimazione.entity.Availability slot : slots) {
                    if (!party.getStartTime().isBefore(slot.getStartTime()) && 
                        !party.getEndTime().isAfter(slot.getEndTime())) {
                        isTimeCompatible = true;
                        break;
                    }
                }
                
                animatorBean.setTimeCompatible(isTimeCompatible);
                result.add(animatorBean);
            }
        }
        return result;
    }

    public List<UserBean> findAllAnimatorsForForce(PartyBean party) throws DAOException {
        UserDAO userDAO = DAOFactory.getDAOFactory().getUserDAO();
        PartyDAO partyDAO = DAOFactory.getDAOFactory().getPartyDAO();

        List<User> allUsers = userDAO.findAllUsers();
        List<String> assigned = partyDAO.getAssignedAnimators(party.getId());
        List<UserBean> result = new ArrayList<>();

        for (User user : allUsers) {
            if ("ANIMATORE".equalsIgnoreCase(user.getRole()) && !assigned.contains(user.getUsername())) {
                UserBean animatorBean = new UserBean();
                animatorBean.setUsername(user.getUsername());
                animatorBean.setNome(user.getNome());
                animatorBean.setCognome(user.getCognome());
                animatorBean.setEmail(user.getEmail());
                animatorBean.setRole(user.getRole());
                
                // Force time compatible to false so it shows up formatted in the UI as a forced option
                animatorBean.setTimeCompatible(false); 
                result.add(animatorBean);
            }
        }
        return result;
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

    public int getAssignmentFeedback(int partyId) throws DAOException {
        PartyDAO dao = DAOFactory.getDAOFactory().getPartyDAO();
        List<String> animators = dao.getAssignedAnimators(partyId); // This returns ALL assigned usernames
        
        if (animators.isEmpty()) return 2;
        
        boolean allAccepted = true;
        for (String user : animators) {
            com.romanimazione.entity.AssignmentStatus status = dao.getAssignmentStatus(partyId, user);
            if (status == com.romanimazione.entity.AssignmentStatus.REJECTED) {
                return -1; // Specific rejection found
            }
            if (status != com.romanimazione.entity.AssignmentStatus.ACCEPTED) {
                allAccepted = false;
            }
        }
        
        return allAccepted ? 1 : 0;
    }
    
    public int getProposalCount(int partyId) throws DAOException {
        return DAOFactory.getDAOFactory().getPartyDAO().getProposalCount(partyId);
    }

    public void removeAssignment(PartyBean party, String username) throws DAOException {
        PartyDAO dao = DAOFactory.getDAOFactory().getPartyDAO();
        dao.removeAssignment(party.getId(), username);
        notifyObservers("Removed assignment for " + username + " from party " + party.getId());
    }

    public java.util.Map<String, com.romanimazione.entity.AssignmentStatus> getAssignmentStatuses(int partyId) throws DAOException {
        List<PartyBean> all = getAllParties();
        for(PartyBean p : all) {
            if(p.getId() == partyId) return p.getAssignmentStatuses();
        }
        return new java.util.HashMap<>();
    }

    public java.util.Map<String, java.time.LocalDateTime> getAssignmentTimestamps(int partyId) throws DAOException {
        List<PartyBean> all = getAllParties();
        for(PartyBean p : all) {
            if(p.getId() == partyId) return p.getAssignmentTimestamps();
        }
        return new java.util.HashMap<>();
    }
}
