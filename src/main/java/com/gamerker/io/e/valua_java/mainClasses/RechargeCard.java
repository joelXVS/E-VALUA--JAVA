/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gamerker.io.e.valua_java.mainClasses;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
/**
 *
 * @author hp
 */
/**
 * representa una tarjeta de recarga con codigo, monto y estado
 * formato: EVAX-XXXX-XXXX-XXXX-XXXX (19 caracteres)
 */
public class RechargeCard {
    private String cardNumber; // formato EVAX-XXXX-XXXX-XXXX-XXXX
    private double amount;
    private String status; // "ACTIVE" o "USED"
    private LocalDateTime usedAt;
    private String usedBy; // usuario que la utilizo

    // constructor que crea una tarjeta nueva
    public RechargeCard(String cardNumber, double amount, boolean isWelcomeCard) {
        if (isWelcomeCard) {
            // Aceptamos cualquier formato legible para tarjetas de bienvenida
            this.cardNumber = cardNumber.trim().toUpperCase();
            if (!this.cardNumber.startsWith("EVAX-WELCOME-")) {
                this.cardNumber = "EVAX-WELCOME-" + this.cardNumber;
            }
        } else {
            this.cardNumber = formatCardNumber(cardNumber); // Validación normal
        }
        this.amount = amount;
        this.status = "ACTIVE";
        this.usedAt = null;
        this.usedBy = null;
    }

    /**
     * formatea el numero de tarjeta asegurando el formato correcto
     * agrega prefijo EVAX si no existe y valida longitud
     */
    private String formatCardNumber(String number) {
        String clean = number.replaceAll("[^A-Z0-9]", "").toUpperCase();
        if (!clean.startsWith("EVAX")) clean = "EVAX" + clean;

        // Si es tarjeta normal: exigimos 19 caracteres (EVAX + 15)
        if (!clean.startsWith("EVAXWELCOME") && clean.length() != 19) {
            throw new IllegalArgumentException("Formato invalido: debe tener EVAX + 15 caracteres alfanuméricos");
        }

        // Formatear con guiones solo si es tarjeta normal
        if (clean.length() == 19 && !clean.startsWith("EVAXWELCOME")) {
            return String.format("%s-%s-%s-%s-%s", 
                clean.substring(0,4), clean.substring(4,8), 
                clean.substring(8,12), clean.substring(12,16), clean.substring(16,19));
        } else {
            // Tarjetas de bienvenida: devolvemos como están o con formato bonito
            return clean.replaceAll("(.{4})(?=.{4})", "$1-");
        }
    }

    // getters para acceder a los campos
    public String getCardNumber() { return cardNumber; }
    public double getAmount() { return amount; }
    public String getStatus() { return status; }
    public LocalDateTime getUsedAt() { return usedAt; }
    public String getUsedBy() { return usedBy; }

    // verifica si la tarjeta esta activa
    public boolean isActive() { return "ACTIVE".equals(status); }

    /**
     * marca la tarjeta como usada por un usuario
     * actualiza estado, fecha y usuario
     */
    public void markAsUsed(String username) {
        this.status = "USED";
        this.usedAt = LocalDateTime.now();
        this.usedBy = username;
    }

    // devuelve el monto formateado con signo de pesos
    public String getFormattedAmount() {
        return String.format("$%,.0f", amount);
    }
}