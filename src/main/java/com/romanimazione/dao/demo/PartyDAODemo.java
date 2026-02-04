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
    public void assignAnimator(int partyId, String animatorUsername) throws com.romanimazione.exception.DAOException {
        Party party = parties.stream()
                .filter(p -> p.getId() == partyId)
                .findFirst()
                .orElseThrow(() -> new com.romanimazione.exception.DAOException(PARTY_NOT_FOUND_MSG));
        
        if (!party.getAssignmentStatuses().containsKey(animatorUsername)) {
            party.getAssignmentStatuses().put(animatorUsername, com.romanimazione.entity.AssignmentStatus.PENDING);
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
             // Return keyset as list
             return new ArrayList<>(party.getAssignmentStatuses().keySet());
         }
         return new ArrayList<>();
    }

    @Override
    public void updateStatus(int partyId, com.romanimazione.entity.PartyStatus status) throws com.romanimazione.exception.DAOException {
        Party party = parties.stream()
                .filter(p -> p.getId() == partyId)
                .findFirst()
                .orElseThrow(() -> new com.romanimazione.exception.DAOException(PARTY_NOT_FOUND_MSG));
        
        party.setStatus(status);
        System.out.println("Demo: Party " + partyId + " status updated to " + status);
    }
    
    @Override
    public java.util.List<Party> findJobOffers(String animatorUsername) {
        return parties.stream()
                .filter(p -> p.getAssignmentStatuses().containsKey(animatorUsername))
                .filter(p -> {
                    com.romanimazione.entity.AssignmentStatus status = p.getAssignmentStatuses().get(animatorUsername);
                    return (status == com.romanimazione.entity.AssignmentStatus.PENDING || 
                           status == com.romanimazione.entity.AssignmentStatus.ACCEPTED) &&
                           p.getStatus() != com.romanimazione.entity.PartyStatus.CANCELLED;
                })
                .toList();
    }

    @Override
    public void updateAssignmentStatus(int partyId, String animatorUsername, com.romanimazione.entity.AssignmentStatus status) throws com.romanimazione.exception.DAOException {
        Party party = parties.stream()
                .filter(p -> p.getId() == partyId)
                .findFirst()
                .orElseThrow(() -> new com.romanimazione.exception.DAOException(PARTY_NOT_FOUND_MSG));
        
        if (party.getAssignmentStatuses().containsKey(animatorUsername)) {
            party.getAssignmentStatuses().put(animatorUsername, status);
        } else {
             throw new com.romanimazione.exception.DAOException("Assignment not found");
        }
    }

    @Override
    public com.romanimazione.entity.AssignmentStatus getAssignmentStatus(int partyId, String animatorUsername) {
         Party party = parties.stream()
                .filter(p -> p.getId() == partyId)
                .findFirst()
                .orElse(null);
         
         if (party != null) {
             return party.getAssignmentStatuses().get(animatorUsername);
         }
         return null;
    }
    
    @Override
    public int getProposalCount(int partyId) {
        // In demo, we just count all entries in the map (Pending + Accepted + Rejected)
        Party party = parties.stream()
                .filter(p -> p.getId() == partyId)
                .findFirst()
                .orElse(null);
        return party != null ? party.getAssignmentStatuses().size() : 0;
    }
    
    @Override
    public void removeAssignment(int partyId, String animatorUsername) throws com.romanimazione.exception.DAOException {
        Party party = parties.stream()
                .filter(p -> p.getId() == partyId)
                .findFirst()
                .orElseThrow(() -> new com.romanimazione.exception.DAOException(PARTY_NOT_FOUND_MSG));
        party.getAssignmentStatuses().remove(animatorUsername);
    }
}
