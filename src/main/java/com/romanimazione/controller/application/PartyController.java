package com.romanimazione.controller.application;

import com.romanimazione.bean.PartyBean;
import com.romanimazione.dao.DAOFactory;
import com.romanimazione.dao.PartyDAO;
import com.romanimazione.entity.Party;
import com.romanimazione.exception.DAOException;
import com.romanimazione.exception.InvalidPartyException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PartyController {

    private static final List<String> ALLOWED_TYPES = Arrays.asList(
        "Full Party", "Smart Party", "Consegna", "Ritiro in sede", "Servizio carretti", "Evento in piazza"
    );

    public void createParty(PartyBean bean) throws InvalidPartyException, DAOException {
        // 1. Validate Mandatory Fields
        if (bean.getName() == null || bean.getName().trim().isEmpty()) throw new InvalidPartyException("Party name is required.");
        if (bean.getAddress() == null || bean.getAddress().trim().isEmpty()) throw new InvalidPartyException("Address is required.");
        if (bean.getClientName() == null || bean.getClientName().trim().isEmpty()) throw new InvalidPartyException("Client Name is required.");
        if (bean.getClientPhone() == null || !bean.getClientPhone().matches("^\\d{10}$")) {
            throw new InvalidPartyException("Client Phone must be exactly 10 digits.");
        }
        
        // 2. Validate Type (Strict Enum check)
        if (bean.getType() == null || !ALLOWED_TYPES.contains(bean.getType())) {
            throw new InvalidPartyException("Invalid Party Type. Allowed: " + ALLOWED_TYPES);
        }

        // 3. Validate Date (Future)
        if (bean.getDate() == null || bean.getDate().isBefore(LocalDate.now())) {
            throw new InvalidPartyException("Date must be today or in the future.");
        }

        // 4. Validate Time
        if (bean.getStartTime() == null || bean.getEndTime() == null) {
            throw new InvalidPartyException("Start and End times are required.");
        }
        if (!bean.getEndTime().isAfter(bean.getStartTime())) {
            throw new InvalidPartyException("End Time must be after Start Time.");
        }

        // 5. Validate Numbers
        if (bean.getAnimatorsRequired() < 1) throw new InvalidPartyException("At least 1 animator is required.");
        if (bean.getCost() < 0) throw new InvalidPartyException("Cost cannot be negative.");

        // Persistence
        PartyDAO dao = DAOFactory.getDAOFactory().getPartyDAO();
        dao.saveParty(bean.toEntity());
    }

    public List<PartyBean> getAllParties() throws DAOException {
        PartyDAO dao = DAOFactory.getDAOFactory().getPartyDAO();
        List<Party> entities = dao.findAllParties();
        List<PartyBean> beans = new ArrayList<>();
        
        for (Party p : entities) {
            beans.add(PartyBean.fromEntity(p));
        }
        return beans;
    }
    
    public List<String> getPartyTypes() {
        return ALLOWED_TYPES;
    }
}
