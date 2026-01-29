package com.romanimazione.dao.file;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.romanimazione.exception.DAOException;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    protected List<T> load(TypeReference<List<T>> typeRef) throws DAOException {
        if (!file.exists()) return new ArrayList<>();
        try {
            return mapper.readValue(file, typeRef);
        } catch (IOException e) {
            throw new DAOException("Error reading file: " + file.getName(), e);
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
