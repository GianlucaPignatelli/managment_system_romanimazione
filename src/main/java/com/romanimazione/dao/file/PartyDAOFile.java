package com.romanimazione.dao.file;

import com.fasterxml.jackson.core.type.TypeReference;
import com.romanimazione.dao.PartyDAO;
import com.romanimazione.entity.Party;
import com.romanimazione.exception.DAOException;
import java.util.List;

public class PartyDAOFile extends GenericFileDAO<Party> implements PartyDAO {

    public PartyDAOFile() {
        super("parties.json");
    }

    @Override
    public void saveParty(Party party) throws DAOException {
        List<Party> list = load(new TypeReference<List<Party>>(){});
        // Generate ID
        int maxId = list.stream().mapToInt(Party::getId).max().orElse(0);
        party.setId(maxId + 1);
        list.add(party);
        save(list);
    }

    @Override
    public List<Party> findAllParties() throws DAOException {
        return load(new TypeReference<List<Party>>(){});
    }

    @Override
    public void assignAnimator(int partyId, String animatorUsername) throws DAOException {
        List<Party> list = load(new TypeReference<List<Party>>(){});
        Party party = list.stream()
                .filter(p -> p.getId() == partyId)
                .findFirst()
                .orElseThrow(() -> new DAOException("Party not found"));

        if (!party.getAssignedAnimators().contains(animatorUsername)) {
            party.getAssignedAnimators().add(animatorUsername);
            save(list);
        } else {
             throw new DAOException("Animator already assigned");
        }
    }

    @Override
    public List<String> getAssignedAnimators(int partyId) throws DAOException {
        List<Party> list = load(new TypeReference<List<Party>>(){});
        return list.stream()
                .filter(p -> p.getId() == partyId)
                .findFirst()
                .map(Party::getAssignedAnimators)
                .orElse(new java.util.ArrayList<>());
    }

    @Override
    public void updateStatus(int partyId, com.romanimazione.entity.PartyStatus status) throws DAOException {
        List<Party> list = load(new TypeReference<List<Party>>(){});
        Party party = list.stream()
                .filter(p -> p.getId() == partyId)
                .findFirst()
                .orElseThrow(() -> new DAOException("Party not found"));
        
        party.setStatus(status);
        save(list);
    }
}
