package com.example.examify.model;

public class Student {
    private int id;
    private String username;
    private String email;
    private String passwordHash;

    public Student(int id, String username, String email, String passwordHash) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
    }
    public Student(String username, String email, String passwordHash) {
        this(0, username, email, passwordHash);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "Student{" + id + ", " + username + ", " + email + "}";
    }
}
