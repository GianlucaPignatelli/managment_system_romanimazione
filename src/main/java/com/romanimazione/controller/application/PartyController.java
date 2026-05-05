package com.romanimazione.controller.application;

import com.romanimazione.bean.PartyBean;
import com.romanimazione.bean.UserBean;
import com.romanimazione.dao.DAOFactory;
import com.romanimazione.dao.PartyDAO;
import com.romanimazione.dao.UserDAO;
import com.romanimazione.entity.Party;
import com.romanimazione.entity.User;
import com.romanimazione.exception.DAOException;
import com.romanimazione.exception.InvalidPartyException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PartyController extends Subject {

    private static final List<String> ALLOWED_TYPES = Arrays.asList(
        "Full Party", "Smart Party", "Consegna", "Ritiro in sede", "Servizio carretti", "Evento in piazza"
    );

    public void createParty(PartyBean bean) throws InvalidPartyException, DAOException {
        bean.validateSyntax();

        // Persistence
        PartyDAO dao = DAOFactory.getDAOFactory().getPartyDAO();
        Party entity = mapToEntity(bean);
        dao.saveParty(entity);
        
        // Notify View
        notifyObservers("Party Created Successfully");
    }



    public List<PartyBean> getAllParties() throws DAOException {
        PartyDAO dao = DAOFactory.getDAOFactory().getPartyDAO();
        // The DAO will now return fully populated entities
        List<Party> entities = dao.findAllParties();
        List<PartyBean> beans = new ArrayList<>();
        
        for (Party p : entities) {
            // timeout logic applied purely in memory over the loaded map before exposing!
            applyTimeoutsInSystem(p);
            // push to DAO if there are changes!
            dao.update(p); 
            beans.add(mapToBean(p));
        }
        
        beans.sort(this::compareParties);
        return beans;
    }

    private void applyTimeoutsInSystem(Party p) {
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        for (java.util.Map.Entry<String, com.romanimazione.entity.AssignmentStatus> entry : p.getAssignmentStatuses().entrySet()) {
            if (entry.getValue() == com.romanimazione.entity.AssignmentStatus.PENDING) {
                java.time.LocalDateTime assignedAt = p.getAssignmentTimestamps().get(entry.getKey());
                if (assignedAt != null && assignedAt.isBefore(now.minusHours(24))) {
                    p.getAssignmentStatuses().put(entry.getKey(), com.romanimazione.entity.AssignmentStatus.TIMEOUT);
                }
            }
        }
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
        
        return LocalDate.parse(p1.getDate()).compareTo(LocalDate.parse(p2.getDate()));
    }
    
    public static Party mapToEntity(PartyBean bean) {
        Party entity = new Party();
        if (bean.getId() != null) entity.setId(Integer.parseInt(bean.getId()));
        entity.setName(bean.getName());
        entity.setType(bean.getType());
        entity.setAddress(bean.getAddress());
        if (bean.getDate() != null) entity.setDate(LocalDate.parse(bean.getDate()));
        entity.setClientName(bean.getClientName());
        entity.setClientPhone(bean.getClientPhone());
        if (bean.getStartTime() != null) entity.setStartTime(LocalTime.parse(bean.getStartTime()));
        if (bean.getEndTime() != null) entity.setEndTime(LocalTime.parse(bean.getEndTime()));
        if (bean.getChildrenCount() != null && !bean.getChildrenCount().trim().isEmpty()) 
            entity.setChildrenCount(Integer.parseInt(bean.getChildrenCount()));
        if (bean.getAnimatorsRequired() != null)
            entity.setAnimatorsRequired(Integer.parseInt(bean.getAnimatorsRequired()));
        entity.setDescription(bean.getDescription());
        if (bean.getCost() != null) entity.setCost(Double.parseDouble(bean.getCost()));
        if (bean.getStatus() != null) entity.setStatus(com.romanimazione.entity.PartyStatus.valueOf(bean.getStatus()));
        entity.setEquipmentCategory(bean.getEquipmentCategory());
        
        java.util.Map<String, com.romanimazione.entity.AssignmentStatus> sMap = new java.util.HashMap<>();
        for (java.util.Map.Entry<String, String> entry : bean.getAssignmentStatuses().entrySet()) {
            sMap.put(entry.getKey(), com.romanimazione.entity.AssignmentStatus.valueOf(entry.getValue()));
        }
        entity.setAssignmentStatuses(sMap);
        
        java.util.Map<String, java.time.LocalDateTime> tMap = new java.util.HashMap<>();
        for (java.util.Map.Entry<String, String> entry : bean.getAssignmentTimestamps().entrySet()) {
            tMap.put(entry.getKey(), java.time.LocalDateTime.parse(entry.getValue()));
        }
        entity.setAssignmentTimestamps(tMap);
        
        return entity;
    }
    
    public static PartyBean mapToBean(Party entity) {
        PartyBean bean = new PartyBean();
        bean.setId(String.valueOf(entity.getId()));
        bean.setName(entity.getName());
        bean.setType(entity.getType());
        bean.setAddress(entity.getAddress());
        bean.setDate(entity.getDate() != null ? entity.getDate().toString() : null);
        bean.setClientName(entity.getClientName());
        bean.setClientPhone(entity.getClientPhone());
        bean.setStartTime(entity.getStartTime() != null ? entity.getStartTime().toString() : null);
        bean.setEndTime(entity.getEndTime() != null ? entity.getEndTime().toString() : null);
        bean.setChildrenCount(entity.getChildrenCount() != null ? entity.getChildrenCount().toString() : "");
        bean.setAnimatorsRequired(String.valueOf(entity.getAnimatorsRequired()));
        bean.setDescription(entity.getDescription());
        bean.setCost(String.valueOf(entity.getCost()));
        bean.setStatus(entity.getStatus() != null ? entity.getStatus().name() : null);
        bean.setEquipmentCategory(entity.getEquipmentCategory());
        
        java.util.Map<String, String> sMap = new java.util.HashMap<>();
        for (java.util.Map.Entry<String, com.romanimazione.entity.AssignmentStatus> entry : entity.getAssignmentStatuses().entrySet()) {
            sMap.put(entry.getKey(), entry.getValue().name());
        }
        bean.setAssignmentStatuses(sMap);
        
        java.util.Map<String, String> tMap = new java.util.HashMap<>();
        for (java.util.Map.Entry<String, java.time.LocalDateTime> entry : entity.getAssignmentTimestamps().entrySet()) {
            tMap.put(entry.getKey(), entry.getValue().toString());
        }
        bean.setAssignmentTimestamps(tMap);
        
        return bean;
    }
    
    public List<String> getPartyTypes() {
        return ALLOWED_TYPES;
    }

    public List<UserBean> findEligibleAnimators(PartyBean party) throws DAOException {
        UserDAO userDAO = DAOFactory.getDAOFactory().getUserDAO();
        List<User> allUsers = userDAO.findAllUsers();
        List<UserBean> result = new ArrayList<>();
        
        // Applicative logic: Loop users, filter Animators, use Domain Logic to check availability
        for (User u : allUsers) {
            if (u instanceof com.romanimazione.entity.Animatore animatore) {
                
                // Skip if already assigned or proposed
                if (party.getAssignmentStatuses().containsKey(animatore.getUsername())) {
                    continue;
                }
                
                if (animatore.isAvailableFor(LocalDate.parse(party.getDate()), LocalTime.parse(party.getStartTime()), LocalTime.parse(party.getEndTime()))) {
                    UserBean animatorBean = new UserBean();
                    animatorBean.setUsername(animatore.getUsername());
                    animatorBean.setNome(animatore.getNome());
                    animatorBean.setCognome(animatore.getCognome());
                    animatorBean.setEmail(animatore.getEmail());
                    animatorBean.setRole(animatore.getRole());
                    animatorBean.setIsTimeCompatible("true");
                    result.add(animatorBean);
                }
            }
        }
        return result;
    }

    public List<UserBean> findAllAnimatorsForForce(PartyBean party) throws DAOException {
        UserDAO userDAO = DAOFactory.getDAOFactory().getUserDAO();
        List<User> allUsers = userDAO.findAllUsers();
        List<UserBean> result = new ArrayList<>();

        for (User u : allUsers) {
            if (u instanceof com.romanimazione.entity.Animatore && !party.getAssignmentStatuses().containsKey(u.getUsername())) {
                UserBean animatorBean = new UserBean();
                animatorBean.setUsername(u.getUsername());
                animatorBean.setNome(u.getNome());
                animatorBean.setCognome(u.getCognome());
                animatorBean.setEmail(u.getEmail());
                animatorBean.setRole(u.getRole());
                animatorBean.setIsTimeCompatible("false"); 
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
        Party p = dao.getPartyById(Integer.parseInt(party.getId()));
        
        if (p.getAssignmentStatuses().size() >= p.getAnimatorsRequired()) {
            throw new IllegalArgumentException("Cannot assign more animators. Limit reached.");
        }
        
        p.getAssignmentStatuses().put(animator.getUsername(), com.romanimazione.entity.AssignmentStatus.PENDING);
        p.getAssignmentTimestamps().put(animator.getUsername(), java.time.LocalDateTime.now());
        
        dao.update(p);
        notifyObservers("Animator " + animator.getUsername() + " assigned to party " + party.getId());
    }

    public void cancelParty(PartyBean party) throws DAOException {
        if (party == null) throw new IllegalArgumentException("Party cannot be null");
        
        PartyDAO dao = DAOFactory.getDAOFactory().getPartyDAO();
        Party p = dao.getPartyById(Integer.parseInt(party.getId()));
        
        // Logica applicativa: se la festa è già stata annullata in precedenza, l'ulteriore annullamento la elimina definitivamente (Hard Delete)
        if (p.getStatus() == com.romanimazione.entity.PartyStatus.CANCELLED) {
            dao.deleteParty(p.getId());
            notifyObservers("Party " + party.getId() + " has been permanently DELETED from the system.");
            return;
        }
        
        p.setStatus(com.romanimazione.entity.PartyStatus.CANCELLED);
        dao.update(p);
        
        notifyObservers("Party " + party.getId() + " has been CANCELLED.");
    }

    public int getAssignmentFeedback(int partyId) throws DAOException {
        Party p = DAOFactory.getDAOFactory().getPartyDAO().getPartyById(partyId);
        if (p.getAssignmentStatuses().isEmpty()) return 2;
        
        boolean allAccepted = true;
        for (com.romanimazione.entity.AssignmentStatus status : p.getAssignmentStatuses().values()) {
            if (status == com.romanimazione.entity.AssignmentStatus.REJECTED) return -1;
            if (status != com.romanimazione.entity.AssignmentStatus.ACCEPTED) allAccepted = false;
        }
        return allAccepted ? 1 : 0;
    }
    
    public int getProposalCount(int partyId) throws DAOException {
        Party p = DAOFactory.getDAOFactory().getPartyDAO().getPartyById(partyId);
        return p.getAssignmentStatuses().size();
    }

    public void removeAssignment(PartyBean party, String username) throws DAOException {
        PartyDAO dao = DAOFactory.getDAOFactory().getPartyDAO();
        Party p = dao.getPartyById(Integer.parseInt(party.getId()));
        p.getAssignmentStatuses().remove(username);
        p.getAssignmentTimestamps().remove(username);
        dao.update(p);
        notifyObservers("Removed assignment for " + username + " from party " + party.getId());
    }

    public java.util.Map<String, com.romanimazione.entity.AssignmentStatus> getAssignmentStatuses(int partyId) throws DAOException {
        List<PartyBean> all = getAllParties();
        for(PartyBean p : all) {
            if(Integer.parseInt(p.getId()) == partyId) {
                java.util.Map<String, com.romanimazione.entity.AssignmentStatus> m = new java.util.HashMap<>();
                for(var entry : p.getAssignmentStatuses().entrySet()) {
                    m.put(entry.getKey(), com.romanimazione.entity.AssignmentStatus.valueOf(entry.getValue()));
                }
                return m;
            }
        }
        return new java.util.HashMap<>();
    }

    public java.util.Map<String, java.time.LocalDateTime> getAssignmentTimestamps(int partyId) throws DAOException {
        List<PartyBean> all = getAllParties();
        for(PartyBean p : all) {
            if(Integer.parseInt(p.getId()) == partyId) {
                java.util.Map<String, java.time.LocalDateTime> m = new java.util.HashMap<>();
                for(var entry : p.getAssignmentTimestamps().entrySet()) {
                    m.put(entry.getKey(), java.time.LocalDateTime.parse(entry.getValue()));
                }
                return m;
            }
        }
        return new java.util.HashMap<>();
    }
}
