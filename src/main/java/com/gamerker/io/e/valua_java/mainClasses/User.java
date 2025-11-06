/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gamerker.io.e.valua_java.mainClasses;
/**
 *
 * @author hp
 */
public abstract class User {
    protected String username;
    protected String displayName;

    public User(String username, String displayName) {
        this.username = username;
        this.displayName = displayName;
    }

    public String getUsername() { return username; }
    public String getDisplayName() { return displayName; }

    public abstract String getRole();

    // Serialización simple para DBController (formato: role|username|displayName)
    public String serialize() {
        return String.format("%s|%s|%s", getRole(), username, displayName);
    }

    public static User deserialize(String line) {
        if (line == null || line.isEmpty()) return null;
        String[] parts = line.split("\\|", 3);
        if (parts.length < 3) return null;
        String role = parts[0];
        String user = parts[1];
        String display = parts[2];
        switch (role.toLowerCase()) {
            case "teacher": return new Teacher(user, display);
            case "student": return new Student(user, display);
            case "admin": return new Admin(user, display);
            default: return null;
        }
    }
}