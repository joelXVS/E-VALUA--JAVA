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
 * representa una transaccion de pago o cobro
 * registra usuario, concepto, monto y tipo
 */
public class Transaction {
    private String username;
    private LocalDateTime timestamp;
    private String concept;
    private double amount;
    private String type;

    // constructor que inicializa la transaccion
    public Transaction(String username, String concept, double amount, String type) {
        this.username = username;
        this.concept = concept;
        this.amount = amount;
        this.type = type;
        this.timestamp = LocalDateTime.now();
    }

    // getters para acceder a los campos
    public String getUsername() { return username; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getConcept() { return concept; }
    public double getAmount() { return amount; }
    public String getType() { return type; }
    
    // devuelve la fecha formateada
    public String getFormattedDate() {
        return timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}