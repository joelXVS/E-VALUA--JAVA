/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.gamerker.io.e.valua_java.controllersPack;

import com.gamerker.io.e.valua_java.mainClasses.*;
import java.security.SecureRandom;
import java.util.*;
/**
 *
 * @author hp
 */
/**
 * controlador de tarjetas de recarga
 * gestiona tarjetas de bienvenida, personales y su migración
 */
public class RechargeController {
    private static final String PREFIX = "EVAX";

    private final SecureRandom random = new SecureRandom();
    private final List<RechargeCard> cards;
    private final DBController db = new DBController();
    
    // scanner
    Scanner scanner = new Scanner(System.in);

    public RechargeController() {
        this.cards = db.loadCards();
    }

    /* ==================== MÉTODOS PÚBLICOS ==================== */

    /**
     * Verifica si el usuario tiene una tarjeta personal activa
     */
    public boolean hasPersonalCard(User user) {
        return getPersonalCard(user).isPresent();
    }

    /**
     * Obtiene la tarjeta personal activa del usuario
     */
    public Optional<RechargeCard> getPersonalCard(User user) {
        return cards.stream()
            .filter(RechargeCard::isPersonal)
            .filter(c -> c.getUsedBy() != null)
            .filter(c -> c.getUsedBy().equalsIgnoreCase(user.getUsername()))
            .filter(RechargeCard::isActive)
            .findFirst();
    }

    /**
     * Obtiene todas las tarjetas de un usuario
     */
    public List<RechargeCard> getUserCards(User user) {
        return cards.stream()
                .filter(c -> c.getUsedBy() != null)
                .filter(c -> c.getUsedBy().equalsIgnoreCase(user.getUsername()))
                .toList();
    }

    /**
     * Busca una tarjeta de bienvenida activa
     */
    public RechargeCard findActiveWelcomeCard(String cardNumber) {
        if (cardNumber == null) return null;

        String clean = cardNumber.replaceAll("[^A-Z0-9]", "").toUpperCase();

        return cards.stream()
                .filter(RechargeCard::isWelcome)
                .filter(RechargeCard::isActive)
                .filter(c -> c.getCardNumber().replaceAll("[^A-Z0-9]", "").equalsIgnoreCase(clean))
                .findFirst()
                .orElse(null);
    }

    /**
     * Procesa recarga con tarjeta de bienvenida (solo antes de migrar)
     */
    public Transaction processWelcomeCardRecharge(User user, String cardNumber, String reference) {
        RechargeCard card = findActiveWelcomeCard(cardNumber);
        if (card == null) {
            throw new SecurityException("Tarjeta de bienvenida inválida o ya usada");
        }

        card.markAsUsed(user.getUsername());
        saveCards();

        String concept = "Recarga con tarjeta de bienvenida " + card.getCardNumber() +
                        (reference != null && !reference.trim().isEmpty() ? " [Ref: " + reference + "]" : "");

        return new Transaction(user.getUsername(), concept, card.getAmount(), "PAYMENT");
    }

    /**
     * Migra de tarjeta de bienvenida a personal
     */
    public Transaction migrateToPersonalCard(User user, String password) {
        if (!user.verifyPassword(password)) {
            throw new SecurityException("Contraseña incorrecta");
        }

        RechargeCard welcomeCard = getUserCards(user).stream()
                .filter(RechargeCard::isWelcome)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No tienes tarjeta de bienvenida"));

        double migrationCost = 7500.0;
        if (!hasSufficientBalance(user, migrationCost)) {
            throw new IllegalStateException("Saldo insuficiente para migrar. Necesitas: $" +
                                          String.format("%,.0f", migrationCost));
        }

        Transaction migrationCharge = new Transaction(
                user.getUsername(),
                "Costo de migración a tarjeta personal",
                migrationCost,
                "CHARGE"
        );

        user.addTransaction(migrationCharge);

        // Marcar bienvenida como usada (no eliminar)
        welcomeCard.markAsUsed(user.getUsername());

        // Crear tarjeta personal NUEVA y ACTIVA
        String personalNumber = "EVAX-PERSONAL-" + user.getUsername().toUpperCase() +
                               "-" + String.format("%04d", random.nextInt(10000));

        RechargeCard personalCard = new RechargeCard(personalNumber, 0.0, false);
        // NO llamar a markAsUsed aquí -> debe quedar ACTIVE para futuras recargas
        personalCard.setUsedBy(user.getUsername()); // solo asignamos usuario
        cards.add(personalCard);

        saveCards();
        return migrationCharge;
    }

    /**
     * Recarga con tarjeta personal (después de migrar)
     */
    public Transaction rechargePersonalCard(User user, String cardNumber, String reference, String password) {
        if (!user.verifyPassword(password)) {
            throw new SecurityException("Contraseña incorrecta");
        }

        if (!hasPersonalCard(user)) {
            throw new IllegalStateException("Primero debes migrar a tarjeta personal");
        }

        RechargeCard personalCard = getPersonalCard(user)
                .orElseThrow(() -> new IllegalStateException("No tienes tarjeta personal activa"));

        // Validar que sea su tarjeta personal (sin guiones)
        String cleanInput = cardNumber.replaceAll("[^A-Z0-9]", "").toUpperCase();
        String cleanCard = personalCard.getCardNumber().replaceAll("[^A-Z0-9]", "");

        if (!cleanCard.equalsIgnoreCase(cleanInput)) {
            throw new SecurityException("Esta no es tu tarjeta personal");
        }

        // ---- NUEVO: pedir monto ----
        double amount;
        while (true) {
            System.out.print("Monto a recargar ($5.000 - $100.000): ");
            try {
                amount = Double.parseDouble(scanner.nextLine().trim());
                if (amount < 5000.0 || amount > 100000.0) {
                    System.out.println("Monto fuera de rango. Inténtalo de nuevo.");
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("Cantidad inválida.");
            }
        }

        String concept = "Recarga con tarjeta personal " + personalCard.getCardNumber() +
                        (reference != null && !reference.trim().isEmpty() ? " [Ref: " + reference + "]" : "");

        return new Transaction(user.getUsername(), concept, amount, "PAYMENT");
    }

    /**
     * Cambia la tarjeta personal actual por una nueva
     */
    public Transaction changePersonalCard(User user, String password) {
        if (!user.verifyPassword(password)) {
            throw new SecurityException("Contraseña incorrecta");
        }

        RechargeCard currentCard = getPersonalCard(user)
                .orElseThrow(() -> new IllegalStateException("No tienes tarjeta personal"));

        double changeCost = 7500.0;
        if (!hasSufficientBalance(user, changeCost)) {
            throw new IllegalStateException("Saldo insuficiente para cambiar de tarjeta");
        }

        // Marcar actual como inactiva
        currentCard.setStatus("INACTIVE");

        // Crear nueva
        String newNumber = "EVAX-PERSONAL-" + user.getUsername().toUpperCase() + 
                          "-NEW-" + String.format("%04d", random.nextInt(10000));
        
        RechargeCard newCard = new RechargeCard(newNumber, 0.0, false);
        newCard.markAsUsed(user.getUsername());
        cards.add(newCard);

        saveCards();

        return new Transaction(user.getUsername(), "Cambio de tarjeta personal", changeCost, "CHARGE");
    }

    /* ==================== MÉTODOS EXTRAS ==================== */

    public double getAvailableBalance(User user) {
        double credit = "student".equals(user.getRole()) ? 10000.0 : 50000.0;
        return user.getBalance() + credit;
    }

    public boolean hasSufficientBalance(User user, double required) {
        return getAvailableBalance(user) >= required;
    }

    public String getBalanceStatus(User user) {
        double balance = user.getBalance();
        double credit = "student".equals(user.getRole()) ? 10000.0 : 50000.0;
        double available = balance + credit;

        if (balance < 0) {
            return String.format("SALDO NEGATIVO: -$%,.0f | Crédito disponible: $%,.0f | " +
                               "DEBES RECARGAR PARA CONTINUAR", 
                               Math.abs(balance), credit);
        } else if (available < 5000) {
            return String.format("Saldo: $%,.0f | Crédito disponible: $%,.0f | " +
                               "Considere recargar pronto", balance, credit);
        } else {
            return String.format("Saldo: $%,.0f | Crédito disponible: $%,.0f", 
                               balance, credit);
        }
    }

    /* ==================== MÉTODOS PRIVADOS ==================== */

    private void saveCards() {
        db.saveCards(cards);
    }

    /**
     * Genera tarjeta de bienvenida (solo para uso interno)
     */
    private RechargeCard generateWelcomeCard(User user) {
        String usernamePart = user.getUsername().toUpperCase().replaceAll("[^A-Z0-9]", "");
        if (usernamePart.isEmpty()) usernamePart = "USER";
        if (usernamePart.length() > 8) usernamePart = usernamePart.substring(0, 8);

        String number = "EVAX-WELCOME-" + usernamePart + "-" + String.format("%04d", random.nextInt(10000));

        RechargeCard welcomeCard = new RechargeCard(number, 5000.0, true);
        welcomeCard.markAsUsed(user.getUsername());
        cards.add(welcomeCard);
        saveCards();

        return welcomeCard;
    }
}