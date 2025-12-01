/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gamerker.io.e.valua_java.visuals;
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
 *
 * @author hp
 */
/**
 * Dashboard del usuario logueado con diseño de mosaicos tipo Windows.
 * Muestra íconos grandes, descripción breve y datos del usuario.
 * Se reutiliza para todos los roles (Student, Teacher, Admin).
 */
public class UserDashboard extends JFrame {
    private final AppController appController;
    private final User currentUser;
    private final BillingController billing;
    private final RechargeController recharge;
    private final DBController db;
    
    // Colores del tema
    private final Color COLOR_FONDO = new Color(255, 218, 185);
    private final Color COLOR_USER_PANEL = new Color(255, 140, 0, 180);
    private final Color COLOR_BOTON = new Color(255, 140, 0);
    private final Color COLOR_BOTON_HOVER = new Color(255, 165, 0);
    
    public UserDashboard(AppController appController, User currentUser) {
        this.appController = appController;
        this.currentUser = currentUser;
        this.billing = new BillingController();
        this.recharge = new RechargeController();
        this.db = new DBController();
        
        // Configurar ventana
        setTitle("E-VALUA - Dashboard: " + currentUser.getDisplayName());
        setSize(1280, 720);
        setMinimumSize(new Dimension(1280, 720));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Panel principal con gradiente
        GradientPanel mainPanel = new GradientPanel(COLOR_FONDO, new Color(255, 179, 71));
        mainPanel.setLayout(new BorderLayout());
        
        // Panel superior con datos del usuario
        mainPanel.add(createUserPanel(), BorderLayout.NORTH);
        
        // Panel central con mosaicos (scrollable)
        JScrollPane scrollPane = new JScrollPane(createTilesPanel());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Panel inferior con botón de cerrar sesión
        mainPanel.add(createLogoutPanel(), BorderLayout.SOUTH);
        
        add(mainPanel);
        
        // Registrar inicio de sesión para facturación
        billing.startSession(currentUser.getUsername());
    }
    
    /**
     * Panel superior con foto/ícono de usuario y datos principales
     */
    private JPanel createUserPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Ícono de usuario (Unicode o imagen)
        JLabel userIcon = new JLabel("👤");
        userIcon.setFont(new Font("Segoe UI", Font.PLAIN, 48));
        userIcon.setForeground(Color.BLACK);
        panel.add(userIcon);
        
        // Datos del usuario
        JPanel dataPanel = new JPanel();
        dataPanel.setLayout(new BoxLayout(dataPanel, BoxLayout.Y_AXIS));
        dataPanel.setOpaque(false);
        
        // Nombre y rol
        JLabel nameLabel = new JLabel(currentUser.getDisplayName());
        nameLabel.setFont(new Font("Verdana", Font.BOLD, 20));
        nameLabel.setForeground(Color.BLACK);
        dataPanel.add(nameLabel);
        
        JLabel roleLabel = new JLabel("Rol: " + currentUser.getRole().toUpperCase());
        roleLabel.setFont(new Font("Verdana", Font.PLAIN, 14));
        roleLabel.setForeground(Color.BLACK);
        dataPanel.add(roleLabel);
        
        // Saldo y crédito
        JLabel balanceLabel = new JLabel(String.format("Saldo: $%,.0f | %s", 
            currentUser.getBalance(), recharge.getBalanceStatus(currentUser)));
        balanceLabel.setFont(new Font("Verdana", Font.PLAIN, 12));
        balanceLabel.setForeground(Color.BLACK);
        dataPanel.add(balanceLabel);
        
        // Fecha de login
        JLabel dateLabel = new JLabel("Login: " + LocalDateTime.now().format(
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        dateLabel.setFont(new Font("Verdana", Font.ITALIC, 11));
        dateLabel.setForeground(Color.BLACK);
        dataPanel.add(dateLabel);
        
        panel.add(dataPanel);
        
        // Espacio para meter el botón de cerrar sesión en la esquina superior derecha
        panel.add(Box.createHorizontalGlue());
        
        return panel;
    }
    
    /**
     * Panel central con mosaicos de funciones (íconos grandes + texto)
     */
    private JPanel createTilesPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 4, 15, 15)); // 4 columnas, automático
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Tiles base para todos los roles
        addTile(panel, "📋", "Ver Pruebas", "Explora todas las pruebas disponibles", "1");
        addTile(panel, "📝", "Realizar Prueba", "Inicia una nueva evaluación", "2");
        addTile(panel, "📊", "Mis Resultados", "Revisa tus resultados recientes", "3");
        addTile(panel, "💰", "Recargar Saldo", "Añade fondos a tu cuenta", "4");
        addTile(panel, "📄", "Factura del Día", "Genera tu factura de consumo", "5");
        addTile(panel, "🏆", "Ranking Global", "Top estudiantes del sistema", "6");
        addTile(panel, "🎯", "Ranking por Prueba", "Mejores puntuaciones por prueba", "7");
        addTile(panel, "🗄️", "Almacenamiento", "Gestiona tu espacio de resultados", "8");
        
        // Tiles exclusivos según rol
        if (currentUser.getRole().equals("teacher") || currentUser.getRole().equals("admin")) {
            addTile(panel, "➕", "Crear Prueba", "Diseña nuevas evaluaciones", "9");
        }
        
        if (currentUser.getRole().equals("admin")) {
            addTile(panel, "⚙️", "Gestión de Usuarios", "Administra el sistema", "10");
        }
        
        return panel;
    }
    
    /**
     * Crea un mosaico individual (ícono + texto + descripción)
     */
    private void addTile(JPanel parent, String icon, String title, String description, String actionCommand) {
        JPanel tile = new JPanel();
        tile.setLayout(new BoxLayout(tile, BoxLayout.Y_AXIS));
        tile.setBackground(COLOR_BOTON);
        tile.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 165, 0), 2),
            BorderFactory.createEmptyBorder(15, 10, 15, 10)
        ));
        tile.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Hover effect
        tile.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                tile.setBackground(COLOR_BOTON_HOVER);
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                tile.setBackground(COLOR_BOTON);
            }
        });
        
        // Hacer el tile clicable
        tile.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                handleTileClick(actionCommand);
            }
        });
        
        // Ícono (Unicode emoji o imagen)
        JLabel iconLabel = new JLabel(icon, SwingConstants.CENTER);
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 48));
        iconLabel.setForeground(Color.BLACK);
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        tile.add(iconLabel);
        
        // Título
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Verdana", Font.BOLD, 16));
        titleLabel.setForeground(Color.BLACK);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        tile.add(titleLabel);
        
        // Descripción
        JLabel descLabel = new JLabel(description, SwingConstants.CENTER);
        descLabel.setFont(new Font("Verdana", Font.PLAIN, 11));
        descLabel.setForeground(Color.BLACK);
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        descLabel.setMaximumSize(new Dimension(180, 30));
        descLabel.setPreferredSize(new Dimension(180, 30));
        tile.add(descLabel);
        
        parent.add(tile);
    }
    
    /**
     * Panel inferior con botón grande de cerrar sesión
     */
    private JPanel createLogoutPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        JButton logoutButton = new JButton("⏻ Cerrar Sesión");
        logoutButton.setFont(new Font("Verdana", Font.BOLD, 16));
        logoutButton.setBackground(new Color(220, 53, 69)); // Rojo suave
        logoutButton.setForeground(Color.BLACK);
        logoutButton.setFocusPainted(false);
        logoutButton.setPreferredSize(new Dimension(200, 45));
        logoutButton.addActionListener(e -> handleLogout());
        
        panel.add(logoutButton);
        return panel;
    }
    
    /**
     * Maneja el clic en un mosaico
     */
    private void handleTileClick(String actionCommand) {
        switch (actionCommand) {
            case "1": mostrarPruebasDisponibles(); break;
            case "2": realizarPrueba(); break;
            case "3": mostrarResultados(); break;
            case "4": recargarSaldo(); break;
            case "5": generarFactura(); break;
            case "6": mostrarRankingGlobal(); break;
            case "7": mostrarRankingPrueba(); break;
            case "8": gestionarAlmacenamiento(); break;
            case "9": crearPrueba(); break;
            case "10": gestionarUsuarios(); break;
        }
    }
    
    // ==================== MÉTODOS DE FUNCIONALIDAD ====================
    
    private void mostrarPruebasDisponibles() {
        List<Test> tests = appController.getTests();
        if (tests.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay pruebas disponibles.", "Pruebas", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        StringBuilder sb = new StringBuilder("=== PRUEBAS DISPONIBLES ===\n\n");
        for (int i = 0; i < tests.size(); i++) {
            Test t = tests.get(i);
            sb.append(String.format("%d. %s - $%,.0f\n", i + 1, t.getTitle(), t.getPrice()));
        }
        
        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(600, 400));
        
        JOptionPane.showMessageDialog(this, scrollPane, "Pruebas Disponibles", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void realizarPrueba() {
        TakeTestDialog takeTest = new TakeTestDialog(this, currentUser, appController);
        takeTest.setVisible(true);
    }
    
    private void mostrarResultados() {
        ResultsDialog resultDg = new ResultsDialog(this, currentUser, appController);
        resultDg.setVisible(true);
    }
    
    private void recargarSaldo() {
        // Ya implementado en RechargeDialog
        RechargeDialog dialog = new RechargeDialog(this, currentUser, appController);
        dialog.setVisible(true);
    }
    
    private void generarFactura() {
        LocalDateTime today = LocalDateTime.now();
        List<Transaction> daily = billing.getDailyTransactions(currentUser.getUsername(), today);
        
        if (daily.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay consumos hoy.", "Factura", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        String fileName = new InvoicePdfController().generateInvoice(currentUser, daily, today);
        if (fileName != null) {
            JOptionPane.showMessageDialog(this, "Factura generada correctamente.", "Factura", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Error al generar factura.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void mostrarRankingGlobal() {
        RankingDialog rankingGlobal = new RankingDialog(this, currentUser, appController);
        rankingGlobal.createGlobalTablePanel();
        rankingGlobal.createStatusPanel();
        rankingGlobal.setVisible(true);
    }
    
    private void mostrarRankingPrueba() {
        RankingDialog rankingGlobal = new RankingDialog(this, currentUser, appController);
        rankingGlobal.createTestTablePanel();
        rankingGlobal.createStatusPanel();
        rankingGlobal.setVisible(true);
    }
    
    private void gestionarAlmacenamiento() {
        StorageManagerDialog storageMG = new StorageManagerDialog(this, currentUser, appController);
        storageMG.setVisible(true);
    }
    
    private void crearPrueba() {
        if (!currentUser.getRole().equals("teacher") && !currentUser.getRole().equals("admin")) {
            JOptionPane.showMessageDialog(this, "❌ Acceso denegado.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // TODO: Implementar creación de prueba
        TestManagerDialog testCreator = new TestManagerDialog(this, currentUser, appController);
        testCreator.setVisible(true);
    }
    
    private void gestionarUsuarios() {
        if (!currentUser.getRole().equals("admin")) {
            JOptionPane.showMessageDialog(this, "❌ Acceso denegado.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // TODO: Implementar gestión de usuarios
        ManageUsersDialog usuariosMG = new ManageUsersDialog(this, currentUser, appController);
        usuariosMG.setVisible(true);
    }
    
    /**
     * Maneja el cierre de sesión con confirmación
     */
    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "¿Desea cerrar sesión?", "Confirmar cierre", 
            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            // Procesar cierre de sesión
            Transaction sessionCharge = billing.endSession(currentUser.getUsername());
            if (sessionCharge != null) {
                currentUser.addTransaction(sessionCharge);
                appController.getTransactions().add(sessionCharge);
                appController.saveAll();
            }
            
            // Volver al login
            dispose();
            SwingUtilities.invokeLater(() -> {
                MainFrame mainFrame = new MainFrame(appController);
                mainFrame.setVisible(true);
            });
        }
    }
}
