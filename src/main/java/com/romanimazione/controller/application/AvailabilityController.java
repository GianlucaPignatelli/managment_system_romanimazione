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
        List<Availability> existingList = dao.findAllAvailabilities().stream()
                .filter(a -> a.getUsername().equals(bean.getUsername()))
                .toList();
        
        for (Availability existing : existingList) {
            // Check if it's NOT self AND dates match
            boolean notSelf = (bean.getId() == null || bean.getId().isEmpty() || existing.getId() != Integer.parseInt(bean.getId()));
            boolean sameDate = existing.getDate().toString().equals(bean.getDate());

            if (notSelf && sameDate) {
                // Logic:
                if (existing.isFullDay() || Boolean.parseBoolean(bean.getIsFullDay())) {
                    throw new InvalidAvailabilityException("Clash with existing availability (Full Day constraint).");
                }
                
                // 2. Partial Overlap
                if (java.time.LocalTime.parse(bean.getStartTime()).isBefore(existing.getEndTime()) && 
                    java.time.LocalTime.parse(bean.getEndTime()).isAfter(existing.getStartTime())) {
                    throw new InvalidAvailabilityException("Time slot overlaps with an existing availability (" + existing.getStartTime() + "-" + existing.getEndTime() + ").");
                }
            }
        }
    }

    public void addAvailability(AvailabilityBean bean) throws InvalidAvailabilityException, DAOException {
        validate(bean);
        checkOverlaps(bean);

        Availability availability = mapToEntity(bean);
        
        com.romanimazione.entity.Animator animatorEntity = new com.romanimazione.entity.Animator();
        animatorEntity.setUsername(bean.getUsername());
        animatorEntity.addAvailability(availability);

        AvailabilityDAO dao = DAOFactory.getDAOFactory().getAvailabilityDAO();
        dao.saveAvailability(availability);
    }

    public void updateAvailability(AvailabilityBean bean) throws InvalidAvailabilityException, DAOException {
        validate(bean);
        checkOverlaps(bean);

        Availability entity = mapToEntity(bean);
        
        AvailabilityDAO dao = DAOFactory.getDAOFactory().getAvailabilityDAO();
        dao.updateAvailability(entity);
    }
    
    private void validate(AvailabilityBean bean) throws InvalidAvailabilityException {
        bean.validateSyntax();
        
        if (bean.getDate() == null) throw new InvalidAvailabilityException("Date is required.");
        if (java.time.LocalDate.parse(bean.getDate()).isBefore(java.time.LocalDate.now())) throw new InvalidAvailabilityException("Cannot set availability for a past date.");

        if (Boolean.parseBoolean(bean.getIsFullDay())) {
            validateFullDay(bean);
        } else {
            validatePartialDay(bean);
        }
    }

    private void validateFullDay(AvailabilityBean bean) throws InvalidAvailabilityException {
        bean.setStartTime(LocalTime.MIN.toString());
        bean.setEndTime(LocalTime.of(23, 59).toString());
        
        if (java.time.LocalDate.parse(bean.getDate()).equals(java.time.LocalDate.now())) {
            throw new InvalidAvailabilityException("Cannot select 'Full Day' for today as start time (00:00) is past.");
        }
    }

    private void validatePartialDay(AvailabilityBean bean) throws InvalidAvailabilityException {
        if (bean.getStartTime() == null || bean.getEndTime() == null) throw new InvalidAvailabilityException("Start/End time required.");
        
        LocalTime start = LocalTime.parse(bean.getStartTime());
        LocalTime end = LocalTime.parse(bean.getEndTime());
        
        if (!end.isAfter(start)) throw new InvalidAvailabilityException("End must be after Start.");
        if (start.equals(end)) throw new InvalidAvailabilityException("Start and End time cannot be the same.");
        
        if (java.time.LocalDate.parse(bean.getDate()).equals(java.time.LocalDate.now()) && start.isBefore(LocalTime.now())) {
            throw new InvalidAvailabilityException("Cannot set start time in the past.");
        }
    }

    public List<AvailabilityBean> getAvailabilities(String username) throws DAOException {
        AvailabilityDAO dao = DAOFactory.getDAOFactory().getAvailabilityDAO();
        List<Availability> entities = dao.findAllAvailabilities().stream()
                .filter(a -> a.getUsername().equals(username))
                .toList();
        List<AvailabilityBean> beans = new ArrayList<>();

        for (Availability entity : entities) {
            beans.add(mapToBean(entity));
        }

        
        // Sort by Date, then Start Time
        beans.sort(java.util.Comparator.comparing(AvailabilityBean::getDate)
                .thenComparing(AvailabilityBean::getStartTime));
                
        return beans;
    }

    public void deleteAvailability(AvailabilityBean bean) throws DAOException {
        AvailabilityDAO dao = DAOFactory.getDAOFactory().getAvailabilityDAO();
        Availability entity = mapToEntity(bean);
        dao.deleteAvailability(entity);
    }
    
    public static Availability mapToEntity(AvailabilityBean bean) {
        Availability entity = new Availability();
        if (bean.getId() != null && !bean.getId().isEmpty()) {
            entity.setId(Integer.parseInt(bean.getId()));
        }
        entity.setUsername(bean.getUsername());
        if (bean.getDate() != null) entity.setDate(java.time.LocalDate.parse(bean.getDate()));
        if (bean.getStartTime() != null) entity.setStartTime(LocalTime.parse(bean.getStartTime()));
        if (bean.getEndTime() != null) entity.setEndTime(LocalTime.parse(bean.getEndTime()));
        if (bean.getIsFullDay() != null) entity.setFullDay(Boolean.parseBoolean(bean.getIsFullDay()));
        return entity;
    }

    public static AvailabilityBean mapToBean(Availability entity) {
        AvailabilityBean bean = new AvailabilityBean();
        bean.setId(String.valueOf(entity.getId()));
        bean.setUsername(entity.getUsername());
        bean.setDate(entity.getDate() != null ? entity.getDate().toString() : null);
        bean.setStartTime(entity.getStartTime() != null ? entity.getStartTime().toString() : null);
        bean.setEndTime(entity.getEndTime() != null ? entity.getEndTime().toString() : null);
        bean.setIsFullDay(String.valueOf(entity.isFullDay()));
        return bean;
    }


}
