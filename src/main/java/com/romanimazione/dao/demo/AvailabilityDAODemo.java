package com.romanimazione.dao.demo;

import com.romanimazione.dao.AvailabilityDAO;

import com.romanimazione.entity.Availability;
import com.romanimazione.exception.DAOException;

import java.util.ArrayList;
import java.util.List;


public class AvailabilityDAODemo implements AvailabilityDAO {

    // Static list to simulate DB across controller instances if needed
    private static final List<Availability> MOCK_DB = new ArrayList<>();

    @Override
    public void saveAvailability(Availability availability) throws DAOException {
        availability.setId(MOCK_DB.size() + 1); // Mock ID
        MOCK_DB.add(availability);
        System.out.println("Demo: Availability saved for " + availability.getUsername());
    }

    @Override
    public List<Availability> findByUsername(String username) throws DAOException {
        return MOCK_DB.stream()
                .filter(a -> a.getUsername().equals(username))
                .toList();
    }

    @Override
    public void updateAvailability(Availability availability) throws DAOException {
        for (int i = 0; i < MOCK_DB.size(); i++) {
            Availability a = MOCK_DB.get(i);
            // Match by ID if present, otherwise approximate match
            if (a.getId() == availability.getId()) {
                MOCK_DB.set(i, availability);
                return;
            }
        }
        throw new DAOException("Item to update not found in Demo DB");
    }

    @Override
    public void deleteAvailability(Availability availability) throws DAOException {
        MOCK_DB.removeIf(a -> a.getId() == availability.getId() ||
                (a.getUsername().equals(availability.getUsername()) && a.getDate().equals(availability.getDate())));
    }

    @Override
    public List<String> findAvailableAnimators(java.time.LocalDate date, java.time.LocalTime startTime, java.time.LocalTime endTime) throws DAOException {
        return MOCK_DB.stream()
                .filter(a -> a.getDate().equals(date))
                .filter(a -> a.isFullDay() || (
                        (a.getStartTime().isBefore(startTime) || a.getStartTime().equals(startTime)) &&
                        (a.getEndTime().isAfter(endTime) || a.getEndTime().equals(endTime))
                ))
                .map(Availability::getUsername)
                .distinct()
                .filter(username -> {
                    // Check if they have an ACCEPTED assignment on this date
                    com.romanimazione.dao.demo.PartyDAODemo partyDao = new com.romanimazione.dao.demo.PartyDAODemo(); 
                    // Since PartyDAODemo uses a static list, new instance accesses same data
                    return partyDao.findAllParties().stream()
                        .noneMatch(p -> p.getDate().equals(date) && 
                            p.getAssignmentStatuses().containsKey(username) && 
                            p.getAssignmentStatuses().get(username) == com.romanimazione.entity.AssignmentStatus.ACCEPTED &&
                            p.getStatus() != com.romanimazione.entity.PartyStatus.CANCELLED);
                })
                .toList();
    }
    @Override
    public List<Availability> findByDate(java.time.LocalDate date) {
        return MOCK_DB.stream()
                .filter(a -> a.getDate().equals(date))
                .toList();
    }
}