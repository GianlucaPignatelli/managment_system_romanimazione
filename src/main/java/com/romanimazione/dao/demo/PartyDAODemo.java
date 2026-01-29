package com.romanimazione.dao.demo;

import com.romanimazione.dao.PartyDAO;
import com.romanimazione.entity.Party;
import java.util.ArrayList;
import java.util.List;

public class PartyDAODemo implements PartyDAO {

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
}
