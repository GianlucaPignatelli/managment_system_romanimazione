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
}
