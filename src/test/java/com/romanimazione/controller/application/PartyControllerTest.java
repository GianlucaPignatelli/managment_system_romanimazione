package com.romanimazione.controller.application;

import com.romanimazione.bean.PartyBean;
import com.romanimazione.dao.DAOFactory;
import com.romanimazione.exception.InvalidPartyException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class PartyControllerTest {

    private PartyController partyController;

    @BeforeEach
    void setUp() {
        DAOFactory.setFactoryType(DAOFactory.DEMO);
        partyController = new PartyController();
    }

    @Test
    void testCreatePartySuccess() {
        PartyBean party = new PartyBean();
        party.setName("Test Party");
        party.setType("Full Party");
        party.setAddress("Via Roma 1");
        party.setDate(LocalDate.now().plusDays(10).toString()); // Future date
        party.setStartTime(LocalTime.of(15, 0).toString());
        party.setEndTime(LocalTime.of(18, 0).toString());
        party.setClientName("Mario Rossi");
        party.setClientPhone("1234567890");
        party.setCost("100.0");
        party.setAnimatorsRequired("2");

        assertDoesNotThrow(() -> partyController.createParty(party));
    }



    @Test
    void testCreatePartyFailure_PastDate() {
        PartyBean party = new PartyBean();
        party.setName("Past Party");
        party.setAddress("Via Roma 1");
        party.setDate(LocalDate.now().minusDays(1).toString()); // Past date
        
        assertThrows(InvalidPartyException.class, () -> {
            partyController.createParty(party);
        });
    }

    @Test
    void testCreatePartyFailure_InvalidTime() {
        PartyBean party = new PartyBean();
        party.setDate(LocalDate.now().plusDays(1).toString());
        party.setStartTime(LocalTime.of(18, 0).toString());
        party.setEndTime(LocalTime.of(17, 0).toString()); // End before Start

        assertThrows(InvalidPartyException.class, () -> {
            partyController.createParty(party);
        });
    }
}
