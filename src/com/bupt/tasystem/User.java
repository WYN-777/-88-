package com.bupt.tasystem;

public class User {
    private String username;
    private String passwordHash;
    private String salt;
    private String role;  // "TA", "MO", "ADMIN"

    // method
    public User(String username, String passwordHash, String salt, String role) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.salt = salt;
        this.role = role;
    }

    // Getter method
    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getSalt() {
        return salt;
    }

    public String getRole() {
        return role;
    }

    // Setter
    public void setRole(String role) {
        this.role = role;
    }
}