package com.romanimazione.entity;

import java.util.ArrayList;
import java.util.List;

public class Amministratore extends User {
    
    // Collegamento all'inventario gestito dall'amministratore
    private List<Equipment> managedEquipment = new ArrayList<>();
    
    // Collegamento alle feste create da questo amministratore
    private List<Party> createdParties = new ArrayList<>();

    public Amministratore(String username, String password, String nome, String cognome, String email) {
        super(username, password, "AMMINISTRATORE", nome, cognome, email);
    }

    public Amministratore() {
         setRole("AMMINISTRATORE");
    }

    public List<Equipment> getManagedEquipment() { return managedEquipment; }
    public void setManagedEquipment(List<Equipment> managedEquipment) { this.managedEquipment = managedEquipment; }

    public void addEquipment(Equipment eq) {
        if (!this.managedEquipment.contains(eq)) {
            this.managedEquipment.add(eq);
            eq.setAdminUsername(this.getUsername()); // Sincronizza l'oggetto dipendente
        }
    }

    public List<Party> getCreatedParties() { return createdParties; }
    public void setCreatedParties(List<Party> createdParties) { this.createdParties = createdParties; }

    public void addCreatedParty(Party party) {
        if (!this.createdParties.contains(party)) {
            this.createdParties.add(party);
        }
    }
}
