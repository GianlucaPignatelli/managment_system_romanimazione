package com.romanimazione.bean;

public class CredentialsBean {
    private String username;
    private String password;
    private String role; // Optional, might be used in registration or needed to be returned

    public CredentialsBean() {}
     public CredentialsBean(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
