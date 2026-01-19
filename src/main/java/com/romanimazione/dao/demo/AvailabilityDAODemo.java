package com.romanimazione.dao.demo;

import com.romanimazione.dao.AvailabilityDAO;

import com.romanimazione.entity.Availability;
import com.romanimazione.exception.DAOException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
}