package com.romanimazione.dao;

import com.romanimazione.entity.Party;
import com.romanimazione.exception.DAOException;
import java.util.List;

public interface PartyDAO {
    void saveParty(Party party) throws DAOException;
    List<Party> findAllParties() throws DAOException;
    
    // Assignment Methods
    void assignAnimator(int partyId, String animatorUsername) throws DAOException;
    List<String> getAssignedAnimators(int partyId) throws DAOException;
    
    // Job Offers & Status Methods
    List<Party> findJobOffers(String animatorUsername) throws DAOException;
    List<Party> findAcceptedJobs(String animatorUsername, java.time.LocalDate startDate, java.time.LocalDate endDate) throws DAOException;
    void updateAssignmentStatus(int partyId, String animatorUsername, com.romanimazione.entity.AssignmentStatus status) throws DAOException;
    com.romanimazione.entity.AssignmentStatus getAssignmentStatus(int partyId, String animatorUsername) throws DAOException;
    java.time.LocalDateTime getAssignmentTimestamp(int partyId, String animatorUsername) throws DAOException;
    int getProposalCount(int partyId) throws DAOException;
    
    void removeAssignment(int partyId, String animatorUsername) throws DAOException; // For cleaning up/replacing
    
    void updateStatus(int partyId, com.romanimazione.entity.PartyStatus status) throws DAOException;
    
    // Evaluate and update any TIMEOUTs
    void checkTimeouts() throws DAOException;
}
