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
            party.getAssignmentTimestamps().put(animatorUsername, java.time.LocalDateTime.now());
            System.out.println("Demo: Assigned " + animatorUsername + " to party " + partyId);
        } else {
            com.romanimazione.entity.AssignmentStatus curr = party.getAssignmentStatuses().get(animatorUsername);
            if (curr == com.romanimazione.entity.AssignmentStatus.TIMEOUT || curr == com.romanimazione.entity.AssignmentStatus.REJECTED) {
                party.getAssignmentStatuses().put(animatorUsername, com.romanimazione.entity.AssignmentStatus.PENDING);
                party.getAssignmentTimestamps().put(animatorUsername, java.time.LocalDateTime.now());
                System.out.println("Demo: Re-assigned " + animatorUsername + " to party " + partyId);
            } else {
                throw new com.romanimazione.exception.DAOException("Animator already assigned");
            }
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
                    return status == com.romanimazione.entity.AssignmentStatus.PENDING &&
                           p.getStatus() != com.romanimazione.entity.PartyStatus.CANCELLED;
                })
                .toList();
    }

    @Override
    public List<Party> findAcceptedJobs(String animatorUsername, java.time.LocalDate startDate, java.time.LocalDate endDate) throws com.romanimazione.exception.DAOException {
        return parties.stream()
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

    @Override
    public java.time.LocalDateTime getAssignmentTimestamp(int partyId, String animatorUsername) throws com.romanimazione.exception.DAOException {
        Party party = parties.stream()
                .filter(p -> p.getId() == partyId)
                .findFirst()
                .orElse(null);
        if (party != null) {
            return party.getAssignmentTimestamps().get(animatorUsername);
        }
        return null;
    }

    @Override
    public void checkTimeouts() throws com.romanimazione.exception.DAOException {
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        for (Party p : parties) {
            java.util.Map<String, com.romanimazione.entity.AssignmentStatus> statuses = p.getAssignmentStatuses();
            java.util.Map<String, java.time.LocalDateTime> timestamps = p.getAssignmentTimestamps();
            
            for (java.util.Map.Entry<String, com.romanimazione.entity.AssignmentStatus> entry : statuses.entrySet()) {
                if (entry.getValue() == com.romanimazione.entity.AssignmentStatus.PENDING) {
                    java.time.LocalDateTime assignedAt = timestamps.get(entry.getKey());
                    if (assignedAt != null && assignedAt.plusHours(24).isBefore(now)) {
                        statuses.put(entry.getKey(), com.romanimazione.entity.AssignmentStatus.TIMEOUT);
                        System.out.println("Demo: Timeout triggered for user " + entry.getKey() + " on party " + p.getId());
                    }
                }
            }
        }
    }
}
