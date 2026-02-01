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
    
    void updateStatus(int partyId, com.romanimazione.entity.PartyStatus status) throws DAOException;
}
