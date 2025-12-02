/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gamerker.io.e.valua_java.visuals;

/**
 *
 * @author hp
 */
import com.gamerker.io.e.valua_java.controllersPack.*;
import com.gamerker.io.e.valua_java.mainClasses.*;
import com.gamerker.io.e.valua_java.utils.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Diálogo de recarga de saldo con todas las opciones:
 * - Ver tarjeta personal
 * - Recargar con tarjeta personal
 * - Migrar tarjeta de bienvenida
 * - Usar tarjeta de bienvenida
 * - Cambiar tarjeta personal
 */
public class RechargeDialog extends JDialog {
    private final User currentUser;
    private final AppController appController;
    private final RechargeController recharge;
    private final BillingController billing;
    
    // Componentes UI
    private JLabel statusLabel;
    private JPanel buttonPanel;
    private JLabel balanceLabel;
    private JLabel statusInfoLabel;
    
    // Colores
    private final Color COLOR_FONDO = new Color(255, 218, 185);
    private final Color COLOR_BOTON = new Color(255, 140, 0);
    private final Color COLOR_BOTON_HOVER = new Color(255, 165, 0);
    private final Color COLOR_EXITO = new Color(40, 167, 69);
    private final Color COLOR_ERROR = new Color(220, 53, 69);
    
    public RechargeDialog(JFrame owner, User user, AppController controller) {
        super(owner, "💳 Recargar Saldo", true);
        this.currentUser = user;
        this.appController = controller;
        this.recharge = appController.getRechargeController();
        this.billing = appController.getBillingController();
        
        setSize(700, 500);
        setMinimumSize(new Dimension(700, 500));
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        initComponents();
    }
    
    private void initComponents() {
        // Panel principal con gradiente
        GradientPanel mainPanel = new GradientPanel(COLOR_FONDO, new Color(255, 179, 71));
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Panel superior con estado de saldo
        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        
        // Panel central con botones de acción
        buttonPanel = createButtonPanel();
        mainPanel.add(new JScrollPane(buttonPanel), BorderLayout.CENTER);
        
        // Panel inferior con mensajes
        mainPanel.add(createStatusPanel(), BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        
        // Título
        JLabel title = new JLabel("GESTIÓN DE SALDO Y TARJETAS");
        title.setFont(new Font("Verdana", Font.BOLD, 20));
        title.setForeground(Color.BLACK);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(title);
        
        // Saldo actual
        panel.add(Box.createVerticalStrut(10));
        String balanceText = String.format("$ Saldo Actual: $%,.0f", currentUser.getBalance());
        balanceLabel = new JLabel(balanceText);
        balanceLabel.setFont(new Font("Verdana", Font.BOLD, 16));
        balanceLabel.setForeground(Color.GRAY);
        balanceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(balanceLabel);
        
        // Estado de saldo
        panel.add(Box.createVerticalStrut(5));
        String statusText = recharge.getBalanceStatus(currentUser);
        statusInfoLabel = new JLabel(statusText);
        statusInfoLabel.setFont(new Font("Verdana", Font.PLAIN, 11));
        statusInfoLabel.setForeground(Color.BLACK);
        statusInfoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(statusInfoLabel);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 15, 15));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        boolean hasPersonal = recharge.hasPersonalCard(currentUser);
        boolean hasWelcome = recharge.findActiveWelcomeCard(
            "EVAX-WELCOME-" + currentUser.getUsername().toUpperCase()) != null;
        
        if (hasPersonal) {
            // Usuario con tarjeta personal
            addButton(panel, "Ver Tarjeta Personal", e -> mostrarTarjetaPersonal());
            addButton(panel, "Recargar Personal", e -> recargarPersonal());
            addButton(panel, "Cambiar Tarjeta ($7.500)", e -> cambiarTarjeta());
        } else {
            // Usuario sin tarjeta personal
            addButton(panel, "Migrar a Personal", e -> migrarTarjeta());
            
            if (hasWelcome) {
                addButton(panel, "Usar Bienvenida", e -> usarBienvenida());
                addButton(panel, "Ver Bienvenida", e -> verBienvenida());
            }
        }
        
        // Botón para actualizar
        addButton(panel, "Actualizar", e -> refreshAll());
        
        return panel;
    }
    
    private JPanel createStatusPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setOpaque(false);
        
        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Verdana", Font.ITALIC, 12));
        statusLabel.setForeground(Color.BLACK);
        panel.add(statusLabel);
        
        return panel;
    }
    
    private void addButton(JPanel parent, String text, java.awt.event.ActionListener listener) {
        JButton button = new JButton(text);
        button.setFont(new Font("Verdana", Font.BOLD, 14));
        button.setBackground(COLOR_BOTON);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(280, 60));
        
        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(COLOR_BOTON_HOVER);
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(COLOR_BOTON);
            }
        });
        
        button.addActionListener(listener);
        parent.add(button);
    }
    
    // ==================== MÉTODOS DE ACCIÓN ====================
    
    private void mostrarTarjetaPersonal() {
        recharge.getPersonalCard(currentUser).ifPresentOrElse(card -> {
            String fecha = card.getUsedAt() != null ? 
                card.getUsedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : 
                "Nunca";
            
            String message = String.format("""
                ====== TARJETA PERSONAL ACTIVA ======
                
                NÚMERO: %s
                ESTADO: %s
                ÚLTIMO USO: %s
                
                ====================================
                
                Esta tarjeta se usa para recargas futuras.
                """,
                card.getCardNumber(),
                card.getStatus(),
                fecha
            );
            
            JOptionPane.showMessageDialog(this, message, "Tarjeta Personal", 
                JOptionPane.INFORMATION_MESSAGE);
            
        }, () -> showError("No tienes tarjeta personal activa"));
    }
    
    private void recargarPersonal() {
        if (!recharge.hasPersonalCard(currentUser)) {
            showError("Primero debes migrar a tarjeta personal");
            return;
        }
        
        // Diálogo de recarga
        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JTextField codeField = new JTextField(20);
        JTextField amountField = new JTextField(10);
        JTextField refField = new JTextField(15);
        JPasswordField passField = new JPasswordField(20);
        
        panel.add(new JLabel("Código tarjeta:"));
        panel.add(codeField);
        panel.add(new JLabel("Monto ($5.000 - $100.000):"));
        panel.add(amountField);
        panel.add(new JLabel("Referencia:"));
        panel.add(refField);
        panel.add(new JLabel("Contraseña:"));
        panel.add(passField);
        
        int result = JOptionPane.showConfirmDialog(this, panel, "Recargar con Tarjeta Personal", 
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result != JOptionPane.OK_OPTION) return;
        
        try {
            String code = codeField.getText().trim();
            double amount = Double.parseDouble(amountField.getText().trim());
            String ref = refField.getText().trim();
            String password = new String(passField.getPassword());
            
            // Validar monto
            if (amount < 5000 || amount > 100000) {
                showError("Monto fuera de rango permitido");
                return;
            }
            
            // Procesar recarga
            Transaction payment = recharge.rechargePersonalCard(currentUser, code, ref, password, amount);
            currentUser.addTransaction(payment);
            appController.getTransactions().add(payment);
            appController.saveAll();
            
            showSuccess(String.format("Recarga exitosa! Monto: $%,.0f", amount));
            refreshHeader();
            
            SwingUtilities.invokeLater(() -> {
                try {
                    InvoicePdfController invoiceController = new InvoicePdfController();
                    String facturaPath = invoiceController.generateRechargeInvoice(
                        currentUser, 
                        payment, 
                        code,
                        "Tarjeta Personal"
                    );
                    
                    if (facturaPath != null) {
                        JOptionPane.showMessageDialog(this,
                            "Comprobante generado: " + facturaPath,
                            "Comprobante de Recarga",
                            JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (Exception e) {
                    System.err.println("Error generando factura: " + e.getMessage());
                }
            });
            
        } catch (NumberFormatException e) {
            showError("Monto inválido");
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }
    
    private void cambiarTarjeta() {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Costo: $7.500. ¿Continuar?", "Cambiar Tarjeta Personal", 
            JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (confirm != JOptionPane.YES_OPTION) return;
        
        String password = JOptionPane.showInputDialog(this, "Confirma tu contraseña:");
        if (password == null) return;
        
        try {
            Transaction change = recharge.changePersonalCard(currentUser, password);
            currentUser.addTransaction(change);
            appController.getTransactions().add(change);
            appController.saveAll();
            
            showSuccess("Tarjeta personal cambiada exitosamente");
            refreshAll();
            
            recharge.getPersonalCard(currentUser).ifPresent(card -> {
                String message = String.format("""
                    ¡Nueva tarjeta creada!
                    
                    Número: %s
                    
                    Guarda este número para futuras recargas.
                    """, card.getCardNumber());
                    
                JOptionPane.showMessageDialog(this, message, "Nueva Tarjeta", 
                    JOptionPane.INFORMATION_MESSAGE);
            });
            
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }
    
    private void migrarTarjeta() {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Costo de migración: $7.500. ¿Continuar?", "Migrar a Tarjeta Personal", 
            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        
        if (confirm != JOptionPane.YES_OPTION) return;
        
        String password = JOptionPane.showInputDialog(this, "Ingresa tu contraseña:");
        if (password == null) return;
        
        try {
            Transaction migration = recharge.migrateToPersonalCard(currentUser, password);
            currentUser.addTransaction(migration);
            appController.getTransactions().add(migration);
            appController.saveAll();
            
            showSuccess("Migración exitosa! Se cobró: $7.500");
            refreshAll();
            
            // Mostrar nueva tarjeta
            recharge.getPersonalCard(currentUser).ifPresent(card -> {
                String message = String.format("""
                    ¡Tarjeta personal creada!
                    
                    Número: %s
                    
                    Guarda este número para futuras recargas.
                    """, card.getCardNumber());
                    
                JOptionPane.showMessageDialog(this, message, "Nueva Tarjeta Personal", 
                    JOptionPane.INFORMATION_MESSAGE);
            });
            
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }
    
    private void usarBienvenida() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JTextField codeField = new JTextField(20);
        JTextField refField = new JTextField(15);
        
        panel.add(new JLabel("Código (EVAX-WELCOME-...): "));
        panel.add(codeField);
        panel.add(new JLabel("Referencia (opcional):"));
        panel.add(refField);
        
        int result = JOptionPane.showConfirmDialog(this, panel, "Usar Tarjeta de Bienvenida", 
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result != JOptionPane.OK_OPTION) return;
        
        try {
            String code = codeField.getText().trim();
            String ref = refField.getText().trim();
            
            Transaction payment = recharge.processWelcomeCardRecharge(currentUser, code, ref);
            currentUser.addTransaction(payment);
            appController.getTransactions().add(payment);
            appController.saveAll();
            
            showSuccess("Recarga exitosa! Monto: $5.000");
            refreshAll();
            
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }
    
    private void verBienvenida() {
        List<RechargeCard> userCards = recharge.getUserCards(currentUser);
        RechargeCard welcomeCard = userCards.stream()
                .filter(RechargeCard::isWelcome)
                .filter(RechargeCard::isActive)
                .findFirst()
                .orElse(null);
        
        if (welcomeCard == null) {
            showError("No tienes tarjeta de bienvenida activa");
            return;
        }
        
        String message = String.format("""
            ====== TARJETA DE BIENVENIDA ======
            
            NÚMERO: %s
            MONTO: $%,.0f
            ESTADO: %s
            
            ===================================
            
            Esta tarjeta solo se puede usar UNA VEZ.
            Después debes migrar a tarjeta personal.
            """,
            welcomeCard.getCardNumber(),
            welcomeCard.getAmount(),
            welcomeCard.getStatus()
        );
        
        JOptionPane.showMessageDialog(this, message, "Tarjeta de Bienvenida", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void refreshAll() {
        refreshHeader();
        refreshButtons();
    }
    
    private void refreshHeader() {
        // Actualizar los textos sin reconstruir el panel
        balanceLabel.setText(String.format("$ Saldo Actual: $%,.0f", currentUser.getBalance()));
        statusInfoLabel.setText(recharge.getBalanceStatus(currentUser));
        
        // Forzar actualización
        balanceLabel.revalidate();
        balanceLabel.repaint();
        statusInfoLabel.revalidate();
        statusInfoLabel.repaint();
    }
    
    private void refreshButtons() {
        // Limpiar y reconstruir los botones
        buttonPanel.removeAll();
        
        boolean hasPersonal = recharge.hasPersonalCard(currentUser);
        boolean hasWelcome = recharge.findActiveWelcomeCard(
            "EVAX-WELCOME-" + currentUser.getUsername().toUpperCase()) != null;
        
        if (hasPersonal) {
            // Usuario con tarjeta personal
            addButton(buttonPanel, "Ver Tarjeta Personal", e -> mostrarTarjetaPersonal());
            addButton(buttonPanel, "Recargar Personal", e -> recargarPersonal());
            addButton(buttonPanel, "Cambiar Tarjeta ($7.500)", e -> cambiarTarjeta());
        } else {
            // Usuario sin tarjeta personal
            addButton(buttonPanel, "Migrar a Personal", e -> migrarTarjeta());
            
            if (hasWelcome) {
                addButton(buttonPanel, "Usar Bienvenida", e -> usarBienvenida());
                addButton(buttonPanel, "Ver Bienvenida", e -> verBienvenida());
            }
        }
        
        // Botón para actualizar
        addButton(buttonPanel, "Actualizar", e -> refreshAll());
        
        // Actualizar el panel de botones
        buttonPanel.revalidate();
        buttonPanel.repaint();
    }
    
    private void showSuccess(String message) {
        statusLabel.setText(message);
        statusLabel.setForeground(COLOR_EXITO);
        
        // Limpiar después de 3 segundos
        Timer timer = new Timer(3000, e -> statusLabel.setText(" "));
        timer.setRepeats(false);
        timer.start();
    }
    
    private void showError(String message) {
        statusLabel.setText(message);
        statusLabel.setForeground(COLOR_ERROR);
        
        // Limpiar después de 5 segundos
        Timer timer = new Timer(5000, e -> statusLabel.setText(" "));
        timer.setRepeats(false);
        timer.start();
    }
}