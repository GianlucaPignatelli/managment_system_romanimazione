package com.romanimazione.dao.file;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.romanimazione.exception.DAOException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class GenericFileDAO<T> {

    protected final File file;
    protected final ObjectMapper mapper;

    protected GenericFileDAO(String filename) {
        this.file = new File(filename);
        this.mapper = new ObjectMapper();
        
        // Polymorphic Type Handling
        mapper.activateDefaultTyping(
            mapper.getPolymorphicTypeValidator(), 
            ObjectMapper.DefaultTyping.NON_FINAL
        );
        
        // Date/Time Handling
        mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        mapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    protected List<T> load(TypeReference<List<T>> typeRef) throws DAOException {
        if (!file.exists()) return new ArrayList<>();
        
        // Handle manually created "[]" files which break Jackson DefaultTyping
        if (file.length() < 10) {
            try {
                String content = new String(java.nio.file.Files.readAllBytes(file.toPath())).trim();
                if ("[]".equals(content)) {
                    return new ArrayList<>();
                }
            } catch (IOException e) {
                // Ignore read error here, let Jackson fail normally if real issue
            }
        }

        try {
            return mapper.readValue(file, typeRef);
        } catch (IOException e) {
            Logger.getLogger(GenericFileDAO.class.getName()).log(Level.SEVERE, "GenericFileDAO Read Error: " + e.getMessage(), e);
            throw new DAOException("Error reading file: " + file.getName() + " (" + e.getMessage() + ")", e);
        }
    }

    protected void save(List<T> items) throws DAOException {
        try {
            mapper.writeValue(file, items);
        } catch (IOException e) {
            throw new DAOException("Error writing file: " + file.getName(), e);
        }
    }
}
