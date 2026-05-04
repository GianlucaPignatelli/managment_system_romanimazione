package com.romanimazione.entity;

public abstract class User {
    private int id;
    private String username;
    private String password;
    private String role;
    private String nome;
    private String cognome;
    private String email;

    protected User(String username, String password, String role, String nome, String cognome, String email) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
    }

    protected User() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getNome() { return this.nome; }
    public void setNome(String valNome) { this.nome = valNome; }

    public String getCognome() { return this.cognome; }
    public void setCognome(String valCognome) { this.cognome = valCognome; }

    public String getEmail() { return this.email; }
    public void setEmail(String valEmail) { this.email = valEmail; }
    
    // Security
    private boolean superAdmin;
    public boolean isSuperAdmin() { return superAdmin; }
    public void setSuperAdmin(boolean superAdmin) { this.superAdmin = superAdmin; }
}
