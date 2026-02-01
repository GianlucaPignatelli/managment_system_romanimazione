package com.romanimazione.dao;

import com.romanimazione.entity.Availability;
import com.romanimazione.exception.DAOException;
import java.util.List;

public interface AvailabilityDAO {
    void saveAvailability(Availability availability) throws DAOException;
    List<Availability> findByUsername(String username) throws DAOException;
    void updateAvailability(Availability availability) throws DAOException;
    void deleteAvailability(Availability availability) throws DAOException;
    
    // Matching Logic: Find animators available for a specific slot
    List<String> findAvailableAnimators(java.time.LocalDate date, java.time.LocalTime startTime, java.time.LocalTime endTime) throws DAOException;
}
