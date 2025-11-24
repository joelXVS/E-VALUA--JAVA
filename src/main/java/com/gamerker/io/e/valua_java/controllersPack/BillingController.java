/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gamerker.io.e.valua_java.controllersPack;

import com.gamerker.io.e.valua_java.mainClasses.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 *
 * @author hp
 */
/**
 * controla la facturacion y cobros por uso del sistema
 * cobra por tiempo de sesion, pruebas y exportaciones
 */
public class BillingController {

    private static final double HOURLY_RATE = 3000.0;        // $3.000 por hora
    private static final double PDF_EXPORT_COST = 2000.0;    // $2.000 por exportar PDF

    private final Map<String, LocalDateTime> sessionStartTimes = new HashMap<>();
    private final DBController db = new DBController();

    // Inicia sesión de usuario
    public void startSession(String username) {
        if (username != null && !username.isEmpty()) {
            sessionStartTimes.put(username, LocalDateTime.now());
        }
    }

    // Finaliza sesión y devuelve transacción de cobro por tiempo
    public Transaction endSession(String username) {
        LocalDateTime start = sessionStartTimes.remove(username);
        if (start == null) return null;

        long minutes = ChronoUnit.MINUTES.between(start, LocalDateTime.now());
        double hours = minutes / 60.0;
        double amount = Math.ceil(hours * HOURLY_RATE); // Redondea hacia arriba

        if (amount <= 0) amount = 100.0; // Mínimo simbólico

        return new Transaction(username,
                String.format("Uso del sistema (%.2f horas)", hours),
                amount, "CHARGE");
    }

    // Cobro por realizar una prueba
    public Transaction chargeForTest(String username, Test test) {
        if (test == null) return null;
        return new Transaction(username,
                "Realización de prueba: " + test.getTitle(),
                test.getPrice(), "CHARGE");
    }

    // Cobro por exportar resultado a PDF
    public Transaction chargeForPdfExport(String username, String testTitle) {
        return new Transaction(username,
                "Exportación PDF: " + testTitle,
                PDF_EXPORT_COST, "CHARGE");
    }

    // Registro de pago (recarga)
    public Transaction registerPayment(String username, double amount, String concept) {
        return new Transaction(username, concept != null ? concept : "Recarga de saldo", amount, "PAYMENT");
    }

    // Obtiene transacciones del día
    public List<Transaction> getDailyTransactions(String username, LocalDateTime date) {
        List<Transaction> all = db.loadTransactions();
        List<Transaction> daily = new ArrayList<>();

        for (Transaction t : all) {
            if (t.getUsername().equals(username) &&
                t.getTimestamp().toLocalDate().equals(date.toLocalDate())) {
                daily.add(t);
            }
        }
        return daily;
    }

    // Total cobrado en el día
    public double calculateDailyTotal(String username, LocalDateTime date) {
        return getDailyTransactions(username, date).stream()
                .filter(t -> "CHARGE".equals(t.getType()))
                .mapToDouble(Transaction::getAmount)
                .sum();
    }
}