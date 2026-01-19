package com.romanimazione.controller.application;

import com.romanimazione.bean.AvailabilityBean;
import com.romanimazione.dao.AvailabilityDAO;
import com.romanimazione.dao.DAOFactory;
import com.romanimazione.entity.Availability;
import com.romanimazione.exception.DAOException;
import com.romanimazione.exception.InvalidAvailabilityException;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class AvailabilityController {

    private void checkOverlaps(AvailabilityBean bean) throws InvalidAvailabilityException, DAOException {
        AvailabilityDAO dao = DAOFactory.getDAOFactory().getAvailabilityDAO();
        List<Availability> existingList = dao.findByUsername(bean.getUsername());
        
        for (Availability existing : existingList) {
            // Skip self (for update)
            if (bean.getId() > 0 && existing.getId() == bean.getId()) continue;
            
            // Check Date
            if (!existing.getDate().equals(bean.getDate())) continue;
            
            // Logic:
            // 1. If either is Full Day -> Overlap!
            if (existing.isFullDay() || bean.isFullDay()) {
                throw new InvalidAvailabilityException("Clash with existing availability (Full Day constraint).");
            }
            
            // 2. Partial Overlap
            // Overlap if (StartA < EndB) and (EndA > StartB)
            if (bean.getStartTime().isBefore(existing.getEndTime()) && 
                bean.getEndTime().isAfter(existing.getStartTime())) {
                throw new InvalidAvailabilityException("Time slot overlaps with an existing availability (" + existing.getStartTime() + "-" + existing.getEndTime() + ").");
            }
        }
    }

    public void addAvailability(AvailabilityBean bean) throws InvalidAvailabilityException, DAOException {
        validate(bean);
        checkOverlaps(bean);

        // Conversion Bean -> Entity
        Availability availability = new Availability();
        availability.setUsername(bean.getUsername());
        availability.setDate(bean.getDate());
        availability.setStartTime(bean.getStartTime());
        availability.setEndTime(bean.getEndTime());
        availability.setFullDay(bean.isFullDay());

        // Persistence
        AvailabilityDAO dao = DAOFactory.getDAOFactory().getAvailabilityDAO();
        dao.saveAvailability(availability);
    }
    
    // ... delete ...

    public void updateAvailability(AvailabilityBean bean) throws InvalidAvailabilityException, DAOException {
        validate(bean);
        checkOverlaps(bean);

        Availability entity = new Availability();
        entity.setId(bean.getId());
        entity.setUsername(bean.getUsername());
        entity.setDate(bean.getDate());
        entity.setStartTime(bean.getStartTime());
        entity.setEndTime(bean.getEndTime());
        entity.setFullDay(bean.isFullDay());
        
        AvailabilityDAO dao = DAOFactory.getDAOFactory().getAvailabilityDAO();
        dao.updateAvailability(entity);
    }
    
    private void validate(AvailabilityBean bean) throws InvalidAvailabilityException {
        if (bean.getDate() == null) {
            throw new InvalidAvailabilityException("Date is required.");
        }
        if (bean.getDate().isBefore(java.time.LocalDate.now())) {
            throw new InvalidAvailabilityException("Cannot set availability for a past date.");
        }

        if (bean.isFullDay()) {
             bean.setStartTime(LocalTime.MIN);
             bean.setEndTime(LocalTime.of(23, 59));
             
             if (bean.getDate().equals(java.time.LocalDate.now())) {
                 throw new InvalidAvailabilityException("Cannot select 'Full Day' for today as start time (00:00) is past.");
             }
        } else {
             if (bean.getStartTime() == null || bean.getEndTime() == null) throw new InvalidAvailabilityException("Start/End time required.");
             if (!bean.getEndTime().isAfter(bean.getStartTime())) throw new InvalidAvailabilityException("End must be after Start.");
             if (bean.getStartTime().equals(bean.getEndTime())) throw new InvalidAvailabilityException("Start and End time cannot be the same.");
             
             if (bean.getDate().equals(java.time.LocalDate.now()) && bean.getStartTime().isBefore(LocalTime.now())) {
                throw new InvalidAvailabilityException("Cannot set start time in the past.");
             }
        }
    }

    public List<AvailabilityBean> getAvailabilities(String username) throws DAOException {
        AvailabilityDAO dao = DAOFactory.getDAOFactory().getAvailabilityDAO();
        List<Availability> entities = dao.findByUsername(username);
        List<AvailabilityBean> beans = new ArrayList<>();

        for (Availability entity : entities) {
            AvailabilityBean bean = new AvailabilityBean();
            bean.setId(entity.getId());
            bean.setUsername(entity.getUsername());
            bean.setDate(entity.getDate());
            bean.setStartTime(entity.getStartTime());
            bean.setEndTime(entity.getEndTime());
            bean.setFullDay(entity.isFullDay());
            beans.add(bean);
        }

        
        // Sort by Date, then Start Time
        beans.sort(java.util.Comparator.comparing(AvailabilityBean::getDate)
                .thenComparing(AvailabilityBean::getStartTime));
                
        return beans;
    }

    public void deleteAvailability(AvailabilityBean bean) throws DAOException {
        AvailabilityDAO dao = DAOFactory.getDAOFactory().getAvailabilityDAO();
        Availability entity = new Availability();
        entity.setId(bean.getId());
        entity.setUsername(bean.getUsername());
        entity.setDate(bean.getDate());
        
        dao.deleteAvailability(entity);
    }


}
