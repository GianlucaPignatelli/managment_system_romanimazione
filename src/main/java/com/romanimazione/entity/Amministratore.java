package com.romanimazione.entity;


import java.util.ArrayList;
import java.util.List;

public class Amministratore extends User {
    
    // Collegamento all'inventario gestito dall'amministratore
    private List<Equipment> managedEquipment;
    
    // Collegamento alle feste create da questo amministratore
    private List<Party> createdParties;

    public Amministratore(String username, String password, String nome, String cognome, String email) {
        super(username, password, "ADMIN", nome, cognome, email);
        setManagedEquipment(new ArrayList<>());
        setCreatedParties(new ArrayList<>());
    }

    public Amministratore() {
         setRole("ADMIN");
         setManagedEquipment(new ArrayList<>());
         setCreatedParties(new ArrayList<>());
    }

    public List<Equipment> getManagedEquipment() { return managedEquipment; }
    public void setManagedEquipment(List<Equipment> managedEquipment) { this.managedEquipment = managedEquipment; }

    public void addEquipment(Equipment eq) {
        if (!getManagedEquipment().contains(eq)) {
            getManagedEquipment().add(eq);
            eq.setAdminUsername(this.getUsername()); // Sincronizza l'oggetto dipendente
        }
    }

    public List<Party> getCreatedParties() { return createdParties; }
    public void setCreatedParties(List<Party> createdParties) { this.createdParties = createdParties; }

    public void addCreatedParty(Party party) {
        if (!getCreatedParties().contains(party)) {
            getCreatedParties().add(party);
        }
    }
}
