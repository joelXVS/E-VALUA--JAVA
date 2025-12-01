/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gamerker.io.e.valua_java.mainClasses;
import java.time.LocalDateTime;
/**
 *
 * @author hp
 */
/**
 * representa una tarjeta de recarga con codigo, monto y estado
 * formato: EVAX-XXXX-XXXX-XXXX-XXXX (19 caracteres)
 */
public class RechargeCard {
    public enum CardType { WELCOME, PERSONAL }

    private String cardNumber;
    private double amount;
    private String status;
    private LocalDateTime usedAt;
    private String usedBy;
    private CardType type;

    public RechargeCard(String cardNumber, double amount, boolean isWelcomeCard) {
        this.cardNumber = formatCardNumber(cardNumber);
        this.amount = amount;
        this.status = "ACTIVE";
        this.usedAt = null;
        this.usedBy = null;
        this.type = isWelcomeCard ? CardType.WELCOME : CardType.PERSONAL;
    }

    /* ==================== GETTERS ==================== */
    public String getCardNumber() { return cardNumber; }
    public double getAmount() { return amount; }
    public String getStatus() { return status; }
    public LocalDateTime getUsedAt() { return usedAt; }
    public String getUsedBy() { return usedBy; }
    public CardType getType() { return type; }
    
    /* ==================== SETTERS ==================== */
    public void setStatus(String status) { this.status = status; }
    public void setUsedAt(LocalDateTime usedAt) { this.usedAt = usedAt; }
    public void setUsedBy(String usedBy) { this.usedBy = usedBy; }

    public boolean isActive() { return "ACTIVE".equals(status); }
    public boolean isWelcome() { return type == CardType.WELCOME; }
    public boolean isPersonal() { return type == CardType.PERSONAL; }

    public String getFormattedAmount() {
        return String.format("$%,.0f", amount);
    }

    /* ==================== MÉTODOS ==================== */
    public void markAsUsed(String username) {
        this.status = "USED";
        this.usedAt = LocalDateTime.now();
        this.usedBy = username;
    }

    private String formatCardNumber(String number) {
        String clean = number.replaceAll("[^A-Z0-9]", "").toUpperCase();
        if (!clean.startsWith("EVAX")) clean = "EVAX" + clean;

        if (clean.length() < 15) {
            throw new IllegalArgumentException("Formato inválido: muy corto");
        }

        // Formato con guiones para legibilidad
        if (clean.startsWith("EVAXWELCOME")) {
            return clean.replaceAll("(.{4})(?=.{4})", "$1-");
        } else {
            return String.format("%s-%s-%s-%s-%s",
                    clean.substring(0, 4),
                    clean.substring(4, 8),
                    clean.substring(8, 12),
                    clean.substring(12, 16),
                    clean.substring(16, Math.min(19, clean.length())));
        }
    }
}