package com.romanimazione.entity;

public class Animatore extends User {

    public Animatore(String username, String password, String nome, String cognome, String email) {
        super(username, password, "ANIMATORE", nome, cognome, email);
    }
    
    public Animatore() {
        setRole("ANIMATORE");
    }
}
