package com.example.examify.model;

/**
 * Reprezentuje użytkownika systemu (studenta lub administratora).
 */
public class User {
    private int id;
    private String username;
    private String email;
    private String passwordHash;
    private boolean isAdmin;

    public User(int id, String username, String email, String passwordHash, boolean isAdmin) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.isAdmin = isAdmin;
    }

    public User(String username, String email, String passwordHash) {
        this(0, username, email, passwordHash, false);
    }

    public User() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public boolean isAdmin() { return isAdmin; }
    public void setAdmin(boolean admin) { isAdmin = admin; }

    @Override
    public String toString() {
        return "Student{" + id + ", " + username + ", " + email + "}";
    }
}
