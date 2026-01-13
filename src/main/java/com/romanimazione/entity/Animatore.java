package com.romanimazione.entity;

public class Animatore extends User {
    // Specific fields for Animatore can be added here
    // e.g., availability, categorization

    public Animatore(String username, String password, String nome, String cognome, String email) {
        super(username, password, "ANIMATORE", nome, cognome, email);
    }
    
    public Animatore() {
        setRole("ANIMATORE");
    }
}
