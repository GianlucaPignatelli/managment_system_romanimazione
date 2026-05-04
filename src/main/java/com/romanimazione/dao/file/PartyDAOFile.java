package com.romanimazione.dao.file;

import com.fasterxml.jackson.core.type.TypeReference;
import com.romanimazione.dao.PartyDAO;
import com.romanimazione.entity.Party;
import com.romanimazione.exception.DAOException;
import java.util.List;

public class PartyDAOFile extends GenericFileDAO<Party> implements PartyDAO {

    private static final String PARTY_NOT_FOUND_MSG = "Party not found";

    public PartyDAOFile() {
        super("parties.json");
    }

    @Override
    public void saveParty(Party party) throws DAOException {
        List<Party> list = load(new TypeReference<List<Party>>(){});

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
    public Party getPartyById(int id) throws DAOException {
        List<Party> list = load(new TypeReference<List<Party>>(){});
        return list.stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .orElse(null);
    }
    
    @Override
    public void update(Party party) throws DAOException {
        List<Party> list = load(new TypeReference<List<Party>>(){});
        boolean updated = false;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId() == party.getId()) {
                list.set(i, party);
                updated = true;
                break;
            }
        }
        if (!updated) throw new DAOException(PARTY_NOT_FOUND_MSG);
        save(list);
    }
    
    @Override
    public void deleteParty(int id) throws DAOException {
        List<Party> list = load(new TypeReference<List<Party>>(){});
        if (list.removeIf(p -> p.getId() == id)) {
            save(list);
        } else {
            throw new DAOException(PARTY_NOT_FOUND_MSG);
        }
    }
}
