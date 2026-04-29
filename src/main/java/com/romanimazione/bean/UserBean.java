package com.romanimazione.bean;

public class UserBean {

    private String id;
    private String username;
    private String role;
    private String nome;
    private String cognome;
    private String email;
    private String password;
    private String securityCode;
    private String superAdmin;
    private String timeCompatible = "true";

    public UserBean() {}

    public UserBean(String username, String password, String role, String nome, String cognome, String email) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCognome() { return cognome; }
    public void setCognome(String cognome) { this.cognome = cognome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getSecurityCode() { return securityCode; }
    public void setSecurityCode(String securityCode) { this.securityCode = securityCode; }

    public String getIsSuperAdmin() { return superAdmin; }
    public void setIsSuperAdmin(String superAdmin) { this.superAdmin = superAdmin; }

    public String getIsTimeCompatible() { return timeCompatible; }
    public void setIsTimeCompatible(String timeCompatible) { this.timeCompatible = timeCompatible; }

    public void validateSyntax() throws IllegalArgumentException {
        if (this.username == null || this.username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username is required.");
        }
        if (this.password == null || this.password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password is required.");
        }
        if (this.email == null || !this.email.endsWith("@gmail.com")) {
            throw new IllegalArgumentException("Email must be a valid @gmail.com address.");
        }
        if (this.nome == null || this.nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome is required.");
        }
        if (this.cognome == null || this.cognome.trim().isEmpty()) {
            throw new IllegalArgumentException("Cognome is required.");
        }
    }
}
