/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.gamerker.io.e.valua_java.controllersPack;

import com.gamerker.io.e.valua_java.mainClasses.*;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author hp
 */
/**
 * gestiona tarjetas de recarga y procesa recargas de saldo
 * genera tarjetas de prueba si no existen
 */
public class RechargeController {
    private static final String PREFIX = "EVAX";
    private final SecureRandom random = new SecureRandom();
    private List<RechargeCard> cards;
    private final DBController db = new DBController();

    public RechargeController() {
        cards = db.loadCards();
    }

    private void generateBatch(int count, double amount) {
        for (int i = 0; i < count; i++) {
            cards.add(generateCard(amount));
        }
    }

    private RechargeCard generateCard(double amount) {
        String number = PREFIX + String.format("-%04d-%04d-%04d-%04d",
                random.nextInt(10000),
                random.nextInt(10000),
                random.nextInt(10000),
                random.nextInt(10000)).substring(0, 15); // 15 dígitos
        return new RechargeCard(number, amount, false);
    }

    public RechargeCard findActiveCard(String cardNumber) {
        if (cardNumber == null) return null;
        String clean = cardNumber.replaceAll("[^A-Z0-9]", "").toUpperCase();
        return cards.stream()
                .filter(c -> c.getCardNumber().equals(clean) && c.isActive())
                .findFirst()
                .orElse(null);
    }

    public Transaction processRecharge(User user, String cardNumber, String reference) {
        RechargeCard card = findActiveCard(cardNumber);
        if (card == null) {
            throw new SecurityException("Tarjeta inválida o ya usada");
        }

        card.markAsUsed(user.getUsername());
        saveCards();

        String concept = "Recarga con tarjeta " + card.getCardNumber() +
                (reference != null ? " [Ref: " + reference + "]" : "");

        return new Transaction(user.getUsername(), concept, card.getAmount(), "PAYMENT");
    }

    public void saveCards() {
        db.saveCards(cards);
    }

    // Saldo disponible (incluye crédito)
    public static double getAvailableBalance(User user) {
        double credit = "student".equals(user.getRole()) ? 10000.0 : 50000.0;
        return user.getBalance() + credit;
    }

    public static boolean hasSufficientBalance(User user, double required) {
        return getAvailableBalance(user) >= required;
    }

    public static String getBalanceStatus(User user) {
        double balance = user.getBalance();
        if (balance >= 0) {
            return String.format("Saldo: $%,.0f", balance);
        } else {
            double available = getAvailableBalance(user);
            return String.format("Saldo: -$%,.0f (Disponible con crédito: $%,.0f)",
                    Math.abs(balance), available);
        }
    }
    
    public RechargeCard generateWelcomeCard(User user) {
        String usernamePart = user.getUsername().toUpperCase().replaceAll("[^A-Z0-9]", "");
        if (usernamePart.isEmpty()) usernamePart = "USER";
        if (usernamePart.length() > 8) usernamePart = usernamePart.substring(0, 8);

        String number = "EVAX-WELCOME-" + usernamePart + "-" + String.format("%04d", random.nextInt(10000));

        // Usamos el constructor especial que permite formato libre
        RechargeCard welcomeCard = new RechargeCard(number, 5000.0, true); 
        welcomeCard.markAsUsed(user.getUsername());
        cards.add(welcomeCard);
        saveCards();
        return welcomeCard;
    }
}