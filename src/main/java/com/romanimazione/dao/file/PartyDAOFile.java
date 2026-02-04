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

        if (!party.getAssignmentStatuses().containsKey(animatorUsername)) {
            party.getAssignmentStatuses().put(animatorUsername, com.romanimazione.entity.AssignmentStatus.PENDING);
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
                .map(p -> new java.util.ArrayList<>(p.getAssignmentStatuses().keySet()))
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
        @Override
    public List<Party> findJobOffers(String animatorUsername) throws DAOException {
        List<Party> list = load(new TypeReference<List<Party>>(){});
        return list.stream()
                .filter(p -> p.getAssignmentStatuses().containsKey(animatorUsername))
                .filter(p -> {
                    com.romanimazione.entity.AssignmentStatus status = p.getAssignmentStatuses().get(animatorUsername);
                    return (status == com.romanimazione.entity.AssignmentStatus.PENDING || 
                           status == com.romanimazione.entity.AssignmentStatus.ACCEPTED) &&
                           p.getStatus() != com.romanimazione.entity.PartyStatus.CANCELLED;
                })
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public void updateAssignmentStatus(int partyId, String animatorUsername, com.romanimazione.entity.AssignmentStatus status) throws DAOException {
        List<Party> list = load(new TypeReference<List<Party>>(){});
        Party party = list.stream()
                .filter(p -> p.getId() == partyId)
                .findFirst()
                .orElseThrow(() -> new DAOException("Party not found"));
        
        if (party.getAssignmentStatuses().containsKey(animatorUsername)) {
            party.getAssignmentStatuses().put(animatorUsername, status);
            save(list);
        } else {
             throw new DAOException("Assignment not found");
        }
    }

    @Override
    public com.romanimazione.entity.AssignmentStatus getAssignmentStatus(int partyId, String animatorUsername) throws DAOException {
        List<Party> list = load(new TypeReference<List<Party>>(){});
        return list.stream()
                .filter(p -> p.getId() == partyId)
                .findFirst()
                .map(p -> p.getAssignmentStatuses().get(animatorUsername))
                .orElse(null);
    }
    
    @Override
    public int getProposalCount(int partyId) throws DAOException {
        List<Party> list = load(new TypeReference<List<Party>>(){});
        return list.stream()
                .filter(p -> p.getId() == partyId)
                .findFirst()
                .map(p -> p.getAssignmentStatuses().size())
                .orElse(0);
    }

    @Override
    public void removeAssignment(int partyId, String animatorUsername) throws DAOException {
        List<Party> parties = load(new TypeReference<List<Party>>(){});
        boolean found = false;
        for (Party p : parties) {
            if (p.getId() == partyId) {
                p.getAssignmentStatuses().remove(animatorUsername);
                found = true;
                break;
            }
        }
        if (found) {
            save(parties);
        } else {
             throw new DAOException("Party not found");
        }
    }
}
