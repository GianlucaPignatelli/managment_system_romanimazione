package com.romanimazione.entity;

public class User {
    private int id;
    private String username;
    private String password;
    private String role;
    private String nome;
    private String cognome;
    private String email;

    public User(String username, String password, String role, String nome, String cognome, String email) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
    }

    public User() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCognome() { return cognome; }
    public void setCognome(String cognome) { this.cognome = cognome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
