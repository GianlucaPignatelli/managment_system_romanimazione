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

    @Override
    public void assignAnimator(int partyId, String animatorUsername) throws com.romanimazione.exception.DAOException {
        Party party = parties.stream()
                .filter(p -> p.getId() == partyId)
                .findFirst()
                .orElseThrow(() -> new com.romanimazione.exception.DAOException("Party not found"));
        
        if (!party.getAssignedAnimators().contains(animatorUsername)) {
            party.getAssignedAnimators().add(animatorUsername);
            System.out.println("Demo: Assigned " + animatorUsername + " to party " + partyId);
        } else {
            throw new com.romanimazione.exception.DAOException("Animator already assigned");
        }
    }

    @Override
    public List<String> getAssignedAnimators(int partyId) throws com.romanimazione.exception.DAOException {
         Party party = parties.stream()
                .filter(p -> p.getId() == partyId)
                .findFirst()
                .orElse(null);
         
         if (party != null) {
             return new ArrayList<>(party.getAssignedAnimators());
         }
         return new ArrayList<>();
    }

    @Override
    public void updateStatus(int partyId, com.romanimazione.entity.PartyStatus status) throws com.romanimazione.exception.DAOException {
        Party party = parties.stream()
                .filter(p -> p.getId() == partyId)
                .findFirst()
                .orElseThrow(() -> new com.romanimazione.exception.DAOException("Party not found"));
        
        party.setStatus(status);
        System.out.println("Demo: Party " + partyId + " status updated to " + status);
    }
}
