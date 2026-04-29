package com.romanimazione.dao.demo;

import com.romanimazione.dao.PartyDAO;
import com.romanimazione.entity.Party;
import java.util.ArrayList;
import java.util.List;

public class PartyDAODemo implements PartyDAO {

    private static final String PARTY_NOT_FOUND_MSG = "Party not found";

    private static final List<Party> parties = new ArrayList<>();

    @Override
    public void saveParty(Party party) {
         int maxId = parties.stream().mapToInt(Party::getId).max().orElse(0);
         party.setId(maxId + 1);
         parties.add(party);
    }

    @Override
    public List<Party> findAllParties() {
        return new ArrayList<>(parties);
    }

    @Override
    public Party getPartyById(int id) throws com.romanimazione.exception.DAOException {
        return parties.stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .orElse(null);
    }
    
    @Override
    public void update(Party party) throws com.romanimazione.exception.DAOException {
        boolean updated = false;
        for (int i = 0; i < parties.size(); i++) {
            if (parties.get(i).getId() == party.getId()) {
                parties.set(i, party);
                updated = true;
                break;
            }
        }
        if (!updated) throw new com.romanimazione.exception.DAOException(PARTY_NOT_FOUND_MSG);
    }
    
    @Override
    public void deleteParty(int id) throws com.romanimazione.exception.DAOException {
        if (!parties.removeIf(p -> p.getId() == id)) {
            throw new com.romanimazione.exception.DAOException(PARTY_NOT_FOUND_MSG);
        }
    }
}
