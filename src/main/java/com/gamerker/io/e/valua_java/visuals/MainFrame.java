/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gamerker.io.e.valua_java.visuals;
/**
 *
 * @author hp
 */
import com.gamerker.io.e.valua_java.controllersPack.AppController;
import com.gamerker.io.e.valua_java.mainClasses.*;
import com.gamerker.io.e.valua_java.utils.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.net.URL;
/**
 * Ventana principal de la aplicación con TabbedPane.
 * Contiene las pestañas: Iniciar Sesión, Registrarse y Acerca de.
 * Se reutiliza cada vez que un usuario cierra sesión.
 */
public class MainFrame extends JFrame {
    private final AppController appController;
    private JTabbedPane tabbedPane;
    private LoginPanel loginPanel;
    private RegisterPanel registerPanel;
    private InfoPanel infoPanel;
    
    // Colores de la paleta naranja pastel
    private final Color COLOR_VENTANA = new Color(255, 140, 0); // Naranja diferenciable
    private final Color COLOR_TABS = new Color(255, 179, 71); // Naranja pastel
    
    /**
     * Constructor principal
     */
    public MainFrame(AppController controller) {
        this.appController = controller;
        
        // Configuración de la ventana
        setTitle("E-VALUA - Sistema de Evaluación Académica");
        setSize(1280, 720);
        setMinimumSize(new Dimension(1280, 720));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Fondo de la ventana principal (color diferenciable)
        getContentPane().setBackground(COLOR_VENTANA);
        setLayout(new BorderLayout());
        
        // Panel superior con logo
        add(createLogoPanel(), BorderLayout.NORTH);
        
        // TabbedPane central
        tabbedPane = createTabbedPane();
        add(tabbedPane, BorderLayout.CENTER);
        
        // Panel inferior con botón de salir
        add(createExitPanel(), BorderLayout.SOUTH);
    }
    
    /**
     * Crea el panel del logo superior centrado
     */
    private JPanel createLogoPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setOpaque(false); // Transparente para ver el fondo
        panel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        
        try {
            URL logoUrl = getClass().getResource("/resources/logo_name.png");
            if (logoUrl != null) {
                ImageIcon logo = new ImageIcon(logoUrl);
                JLabel logoLabel = new JLabel(logo);
                panel.add(logoLabel);
            } else {
                // Fallback si no encuentra la imagen
                JLabel logoLabel = new JLabel("E-VALUA");
                logoLabel.setFont(new Font("Verdana", Font.BOLD, 36));
                logoLabel.setForeground(Color.BLACK);
                panel.add(logoLabel);
            }
        } catch (Exception e) {
            System.err.println("Error cargando logo_name.png: " + e.getMessage());
        }
        
        return panel;
    }
    
    /**
     * Crea el TabbedPane con las 3 pestañas
     */
    private JTabbedPane createTabbedPane() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(COLOR_TABS);
        tabs.setForeground(Color.BLACK);
        tabs.setFont(new Font("Verdana", Font.BOLD, 14));
        tabs.setTabPlacement(JTabbedPane.TOP);
        
        // Crear paneles de contenido
        loginPanel = new LoginPanel(this::handleLogin);
        registerPanel = new RegisterPanel(this::handleRegister);
        infoPanel = new InfoPanel();
        
        // Añadir pestañas
        tabs.addTab("Iniciar Sesión", loginPanel);
        tabs.addTab("Registrarse", registerPanel);
        tabs.addTab("Acerca de", infoPanel);
        
        return tabs;
    }
    
    /**
     * Crea el panel inferior con botón de salir
     */
    private JPanel createExitPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 0, 30, 0));
        
        // Botón con icono de apagado Unicode
        JButton exitButton = new JButton("Salir");
        exitButton.setFont(new Font("Verdana", Font.BOLD, 16));
        exitButton.setBackground(new Color(220, 53, 69)); // Rojo suave
        exitButton.setForeground(Color.BLACK);
        exitButton.setFocusPainted(false);
        exitButton.setPreferredSize(new Dimension(150, 40));
        exitButton.addActionListener(e -> System.exit(0));
        
        panel.add(exitButton);
        return panel;
    }
    
    /**
     * Maneja el evento de login desde LoginPanel
     */
    private void handleLogin(ActionEvent e) {
        String username = loginPanel.getUsername();
        String password = loginPanel.getPassword();
        
        if (username.isEmpty() || password.isEmpty()) {
            loginPanel.showMessage("Usuario y contraseña son obligatorios", true);
            return;
        }
        
        User user = appController.getUsers().stream()
                .filter(u -> u.getUsername().equalsIgnoreCase(username))
                .filter(u -> u.verifyPassword(password))
                .findFirst()
                .orElse(null);
        
        if (user != null) {
            loginPanel.showMessage("¡Login exitoso!", false);
            
            // Abrir dashboard del usuario (en EDT)
            SwingUtilities.invokeLater(() -> {
                // Cerrar esta ventana
                setVisible(false);
                dispose();
                
                // Abrir ventana del usuario
                UserDashboard dashboard = new UserDashboard(appController, user);
                dashboard.setVisible(true);
            });
        } else {
            loginPanel.showMessage("Credenciales incorrectas", true);
        }
    }
    
    /**
     * Maneja el evento de registro desde RegisterPanel
     */
    private void handleRegister(ActionEvent e) {
        String username = registerPanel.getUsername();
        String displayName = registerPanel.getDisplayName();
        String password = registerPanel.getPassword();
        String confirmPassword = registerPanel.getConfirmPassword();
        String role = registerPanel.getRole();
        
        // Validaciones
        if (username.isEmpty()) {
            registerPanel.showMessage("El usuario no puede estar vacío", true);
            return;
        }
        
        if (appController.getUsers().stream().anyMatch(u -> u.getUsername().equalsIgnoreCase(username))) {
            registerPanel.showMessage("El usuario ya existe", true);
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            registerPanel.showMessage("Las contraseñas no coinciden", true);
            return;
        }
        
        // Validar código admin si es necesario
        if ("admin".equals(role) && !"3-V4LU4D0R3S".equals(registerPanel.getAdminCode())) {
            registerPanel.showMessage("Código de administrador incorrecto", true);
            return;
        }
        
        // Crear usuario
        User newUser = switch (role) {
            case "admin" -> new com.gamerker.io.e.valua_java.mainClasses.Admin(username, displayName);
            case "teacher" -> new com.gamerker.io.e.valua_java.mainClasses.Teacher(username, displayName);
            default -> new com.gamerker.io.e.valua_java.mainClasses.Student(username, displayName);
        };
        
        newUser.setPassword(password.isEmpty() ? "password" : password);
        
        // Recarga inicial de bienvenida
        Transaction welcomeTx = new Transaction(
            username, 
            "Saldo inicial de bienvenida", 
            5000.0, 
            "PAYMENT"
        );
        newUser.addTransaction(welcomeTx);
        
        // Guardar
        appController.getUsers().add(newUser);
        appController.getTransactions().add(welcomeTx);
        appController.saveAll();
        
        registerPanel.showMessage("¡Registro exitoso! Saldo inicial: $5.000", false);
        
        // Limpiar formulario después de 2 segundos
        Timer timer = new Timer(2000, evt -> {
            registerPanel.clear();
            registerPanel.showMessage(" ", false);
            tabbedPane.setSelectedIndex(0); // Ir a login
        });
        timer.setRepeats(false);
        timer.start();
    }
    
    /**
     * Método para volver a mostrar esta ventana desde UserDashboard
     */
    public void showAgain() {
        loginPanel.clear();
        registerPanel.clear();
        setVisible(true);
    }
}
