package com.romanimazione.entity;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class EntityTest {

    @Test
    public void testAmministratoreMethods() {
        Amministratore admin = new Amministratore("admin", "pass", "Mario", "Rossi", "mario@gmail.com");
        
        Equipment eq = new Equipment();
        eq.setName("Test Eq");
        admin.addEquipment(eq);
        
        List<Equipment> eqList = new ArrayList<>();
        eqList.add(eq);
        admin.setManagedEquipment(eqList);
        assertEquals(1, admin.getManagedEquipment().size());
        
        Party party = new Party();
        party.setName("Test Party");
        admin.addCreatedParty(party);
        
        List<Party> partyList = new ArrayList<>();
        partyList.add(party);
        admin.setCreatedParties(partyList);
        assertEquals(1, admin.getCreatedParties().size());
    }

    @Test
    public void testAnimatoreMethods() {
        Animatore anim = new Animatore();
        anim.setUsername("anim");
        
        Availability av = new Availability("anim", LocalDate.now(), LocalTime.of(10, 0), LocalTime.of(14, 0), false);
        anim.addAvailability(av);
        
        List<Availability> avList = new ArrayList<>();
        avList.add(av);
        anim.setAvailabilities(avList);
        assertEquals(1, anim.getAvailabilities().size());
        
        Party party = new Party();
        party.setName("Test Party");
        anim.addAcceptedJob(party);
        
        List<Party> partyList = new ArrayList<>();
        partyList.add(party);
        anim.setAcceptedJobs(partyList);
        assertEquals(1, anim.getAcceptedJobs().size());
        
        assertTrue(anim.isAvailableFor(LocalDate.now(), LocalTime.of(11, 0), LocalTime.of(12, 0)));
    }
}
