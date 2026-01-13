package com.romanimazione.entity;

public class Amministratore extends User {
    // Specific fields for Amministratore can be added here
    
    public Amministratore(String username, String password, String nome, String cognome, String email) {
        super(username, password, "AMMINISTRATORE", nome, cognome, email);
    }

    public Amministratore() {
         setRole("AMMINISTRATORE");
    }
}
