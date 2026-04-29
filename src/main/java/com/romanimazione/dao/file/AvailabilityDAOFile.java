package com.romanimazione.dao.file;

import com.romanimazione.dao.AvailabilityDAO;

import com.romanimazione.entity.Availability;
import com.romanimazione.exception.DAOException;
import java.util.List;

public class AvailabilityDAOFile implements AvailabilityDAO {

    private final java.io.File file;
    private final com.fasterxml.jackson.databind.ObjectMapper mapper;

    public AvailabilityDAOFile() {
        this.file = new java.io.File("availabilities.json");
        this.mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        mapper.activateDefaultTyping(
                mapper.getPolymorphicTypeValidator(),
                com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping.NON_FINAL
        );
        mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        mapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    private List<Availability> load() throws DAOException {
        if (!file.exists()) return new java.util.ArrayList<>();
        try {
            return mapper.readValue(file, new com.fasterxml.jackson.core.type.TypeReference<List<Availability>>() {
            });
        } catch (java.io.IOException e) {
            throw new DAOException("Error reading availabilities file: " + e.getMessage(), e);
        }
    }

    private void save(List<Availability> list) throws DAOException {
        try {
            mapper.writeValue(file, list);
        } catch (java.io.IOException e) {
            throw new DAOException("Error writing availabilities file: " + e.getMessage(), e);
        }
    }

    @Override
    public void saveAvailability(Availability availability) throws DAOException {
        List<Availability> list = load();
        // Generate ID
        int maxId = list.stream().mapToInt(Availability::getId).max().orElse(0);
        availability.setId(maxId + 1);
        list.add(availability);
        save(list);
    }

    @Override
    public List<Availability> findAllAvailabilities() throws DAOException {
        return load();
    }

    @Override
    public void updateAvailability(Availability availability) throws DAOException {
        List<Availability> list = load();
        boolean found = false;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId() == availability.getId()) {
                list.set(i, availability);
                found = true;
                break;
            }
        }
        if (!found) throw new DAOException("Availability not found for update");
        save(list);
    }

    @Override
    public void deleteAvailability(Availability availability) throws DAOException {
        List<Availability> list = load();
        boolean removed = list.removeIf(a -> a.getId() == availability.getId());
        if (!removed && availability.getId() == 0) {
            removed = list.removeIf(a -> a.getUsername().equals(availability.getUsername()) && a.getDate().equals(availability.getDate()));
        }
        if (removed) save(list);
        else throw new DAOException("Availability not found to delete");
    }
}