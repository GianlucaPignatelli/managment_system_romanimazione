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
                .orElseThrow(() -> new DAOException(PARTY_NOT_FOUND_MSG));

        if (!party.getAssignmentStatuses().containsKey(animatorUsername)) {
            party.getAssignmentStatuses().put(animatorUsername, com.romanimazione.entity.AssignmentStatus.PENDING);
            party.getAssignmentTimestamps().put(animatorUsername, java.time.LocalDateTime.now());
            save(list);
        } else {
             // Overwrite on re-assign logic
             com.romanimazione.entity.AssignmentStatus curr = party.getAssignmentStatuses().get(animatorUsername);
             if (curr == com.romanimazione.entity.AssignmentStatus.TIMEOUT || curr == com.romanimazione.entity.AssignmentStatus.REJECTED) {
                 party.getAssignmentStatuses().put(animatorUsername, com.romanimazione.entity.AssignmentStatus.PENDING);
                 party.getAssignmentTimestamps().put(animatorUsername, java.time.LocalDateTime.now());
                 save(list);
             } else {
                 throw new DAOException("Animator already assigned");
             }
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
                .orElseThrow(() -> new DAOException(PARTY_NOT_FOUND_MSG));
        
        party.setStatus(status);
        save(list);
    }
    public List<Party> findJobOffers(String animatorUsername) throws DAOException {
        List<Party> list = load(new TypeReference<List<Party>>(){});
        return list.stream()
                .filter(p -> p.getAssignmentStatuses().containsKey(animatorUsername))
                .filter(p -> {
                    com.romanimazione.entity.AssignmentStatus status = p.getAssignmentStatuses().get(animatorUsername);
                    return status == com.romanimazione.entity.AssignmentStatus.PENDING &&
                           p.getStatus() != com.romanimazione.entity.PartyStatus.CANCELLED;
                })
                .toList();
    }

    @Override
    public List<Party> findAcceptedJobs(String animatorUsername, java.time.LocalDate startDate, java.time.LocalDate endDate) throws DAOException {
        List<Party> list = load(new TypeReference<List<Party>>(){});
        return list.stream()
                .filter(p -> p.getAssignmentStatuses().containsKey(animatorUsername))
                .filter(p -> {
                    com.romanimazione.entity.AssignmentStatus status = p.getAssignmentStatuses().get(animatorUsername);
                    boolean isAccepted = (status == com.romanimazione.entity.AssignmentStatus.ACCEPTED);
                    boolean afterStart = (startDate == null) || !p.getDate().isBefore(startDate);
                    boolean beforeEnd = (endDate == null) || !p.getDate().isAfter(endDate);
                    return isAccepted && afterStart && beforeEnd;
                })
                .sorted(java.util.Comparator.comparing(Party::getDate).thenComparing(Party::getStartTime))
                .toList();
    }

    @Override
    public void updateAssignmentStatus(int partyId, String animatorUsername, com.romanimazione.entity.AssignmentStatus status) throws DAOException {
        List<Party> list = load(new TypeReference<List<Party>>(){});
        Party party = list.stream()
                .filter(p -> p.getId() == partyId)
                .findFirst()
                .orElseThrow(() -> new DAOException(PARTY_NOT_FOUND_MSG));
        
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
             throw new DAOException(PARTY_NOT_FOUND_MSG);
        }
    }

    @Override
    public java.time.LocalDateTime getAssignmentTimestamp(int partyId, String animatorUsername) throws DAOException {
        List<Party> list = load(new TypeReference<List<Party>>(){});
        return list.stream()
                .filter(p -> p.getId() == partyId)
                .findFirst()
                .map(p -> p.getAssignmentTimestamps().get(animatorUsername))
                .orElse(null);
    }
    
    @Override
    public void checkTimeouts() throws DAOException {
        List<Party> parties = load(new TypeReference<List<Party>>(){});
        boolean changed = false;
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        
        for (Party p : parties) {
            java.util.Map<String, com.romanimazione.entity.AssignmentStatus> statuses = p.getAssignmentStatuses();
            java.util.Map<String, java.time.LocalDateTime> timestamps = p.getAssignmentTimestamps();
            
            for (java.util.Map.Entry<String, com.romanimazione.entity.AssignmentStatus> entry : statuses.entrySet()) {
                if (entry.getValue() == com.romanimazione.entity.AssignmentStatus.PENDING) {
                    java.time.LocalDateTime assignedAt = timestamps.get(entry.getKey());
                    if (assignedAt != null && assignedAt.plusHours(24).isBefore(now)) {
                        statuses.put(entry.getKey(), com.romanimazione.entity.AssignmentStatus.TIMEOUT);
                        changed = true;
                    }
                }
            }
        }
        
        if (changed) {
            save(parties);
        }
    }
}
