package com.romanimazione.bean;

public class UserBean extends CredentialsBean {

    private String id;
    private String nome;
    private String cognome;
    private String email;
    private String securityCode;
    private String superAdmin;
    private String timeCompatible = "true";

    public UserBean() { super(); }

    public UserBean(String username, String password, String role, String nome, String cognome, String email) {
        super(username, password);
        this.setRole(role);
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }



    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCognome() { return cognome; }
    public void setCognome(String cognome) { this.cognome = cognome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }



    public String getSecurityCode() { return securityCode; }
    public void setSecurityCode(String securityCode) { this.securityCode = securityCode; }

    public String getIsSuperAdmin() { return superAdmin; }
    public void setIsSuperAdmin(String superAdmin) { this.superAdmin = superAdmin; }

    public String getIsTimeCompatible() { return timeCompatible; }
    public void setIsTimeCompatible(String timeCompatible) { this.timeCompatible = timeCompatible; }

    public void validateSyntax() throws IllegalArgumentException {
        if (this.getUsername() == null || this.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Username is required.");
        }
        if (this.getPassword() == null || this.getPassword().trim().isEmpty()) {
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
