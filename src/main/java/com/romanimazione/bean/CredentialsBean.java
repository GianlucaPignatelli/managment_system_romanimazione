package com.romanimazione.bean;

public class CredentialsBean {
    private String username;
    private String password;
    private String role;

    public CredentialsBean() {}
     public CredentialsBean(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() { return this.username; }
    public void setUsername(String vUsername) { this.username = vUsername; }

    public String getPassword() { return this.password; }
    public void setPassword(String vPassword) { this.password = vPassword; }
    
    public String getRole() { return this.role; }
    public void setRole(String vRole) { this.role = vRole; }
}
