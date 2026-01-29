package com.romanimazione.dao;

import com.romanimazione.entity.Party;
import com.romanimazione.exception.DAOException;
import java.util.List;

public interface PartyDAO {
    void saveParty(Party party) throws DAOException;
    List<Party> findAllParties() throws DAOException;
}
