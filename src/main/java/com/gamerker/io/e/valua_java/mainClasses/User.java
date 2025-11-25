/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gamerker.io.e.valua_java.mainClasses;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author hp
 */
/**
 * clase abstracta base para todos los tipos de usuario
 * maneja autenticacion, balance y transacciones
 */
public abstract class User {
    protected String username;
    protected String displayName;
    protected double balance;
    protected List<Transaction> transactions;
    protected String passwordHash; // SHA-256 con salt (username)

    public User(String username, String displayName) {
        this.username = username != null ? username.trim() : "";
        this.displayName = displayName != null ? displayName.trim() : username;
        this.balance = 0.0;
        this.transactions = new ArrayList<>();
        this.passwordHash = ""; // Sin contraseña por defecto (usuarios antiguos)
    }

    // ====================== GETTERS ======================
    public String getUsername() { return username; }
    public String getDisplayName() { return displayName; }
    public double getBalance() { return balance; }
    public List<Transaction> getTransactions() { return new ArrayList<>(transactions); }
    public String getPasswordHash() { return passwordHash; }
    public abstract String getRole();

    // ====================== TRANSACCIONES ======================
    public void addTransaction(Transaction t) {
        if (t != null) {
            transactions.add(t);
            if ("CHARGE".equals(t.getType())) {
                balance -= t.getAmount();
                // Alerta si el saldo se vuelve muy negativo
                if (balance < -50000) {
                    System.err.println("ALERTA: Usuario " + username + " tiene saldo muy negativo: " + balance);
                }
            } else if ("PAYMENT".equals(t.getType())) {
                balance += t.getAmount();
            }
        }
    }

    // ====================== CONTRASEÑAS ======================
    public void setPassword(String password) {
        if (password != null && !password.trim().isEmpty()) {
            this.passwordHash = hashPassword(password.trim(), username);
        }
    }

    public boolean verifyPassword(String password) {
        // Si no tiene contraseña (usuario antiguo), permite acceso
        if (passwordHash == null || passwordHash.isEmpty()) {
            return true;
        }
        if (password == null || password.trim().isEmpty()) {
            return false;
        }
        String inputHash = hashPassword(password.trim(), username);
        return passwordHash.equals(inputHash);
    }

    private String hashPassword(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            String input = salt + password + salt; // Salt al inicio y final
            byte[] hash = md.digest(input.getBytes());
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error: SHA-256 no disponible", e);
        }
    }

    public void setPasswordHash(String hash) {
        this.passwordHash = hash != null ? hash : "";
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    // ====================== SERIALIZACIÓN ======================
    public String serialize() {
        return String.format("%s|%s|%s|%.2f|%s",
                getRole(), username, displayName, balance,
                passwordHash != null ? passwordHash : "");
    }

    public static User deserialize(String line) {
        if (line == null || line.trim().isEmpty()) return null;
        String[] parts = line.split("\\|", 5);
        if (parts.length < 3) return null;

        String role = parts[0].toLowerCase();
        String user = parts[1];
        String display = parts[2];
        double bal = parts.length >= 4 && !parts[3].isEmpty() ? Double.parseDouble(parts[3]) : 0.0;
        String hash = parts.length >= 5 ? parts[4] : "";

        User u;
        switch (role) {
            case "admin":   u = new Admin(user, display);   break;
            case "teacher": u = new Teacher(user, display); break;
            default:        u = new Student(user, display); break;
        }
        u.setBalance(bal);
        u.setPasswordHash(hash);
        return u;
    }

    @Override
    public String toString() {
        return String.format("%s (%s) - Saldo: $%,.0f", displayName, getRole(), balance);
    }
    
    // ====================== SETTERS ======================
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public void setTransactions(List<Transaction> transactions) { this.transactions = transactions; }
}