/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gamerker.io.e.valua_java.visuals;

import com.gamerker.io.e.valua_java.controllersPack.AppController;
import com.gamerker.io.e.valua_java.mainClasses.User;
import com.gamerker.io.e.valua_java.utils.GradientPanel;
import javax.swing.*;
import java.awt.*;
import java.net.URL;

/**
 * Ventana de splash que se muestra al iniciar la aplicación.
 * Muestra el logo.png durante 2 segundos con fondo gradiente.
 */
public class SplashScreen extends JWindow {
    
    private static final int SPLASH_DURATION = 7000;
    
    /**
     * Crea y muestra la ventana de splash
     */
    public SplashScreen() {
        // Tamaño de la ventana
        setSize(600, 400);
        // Centrar en pantalla
        setLocationRelativeTo(null);
        
        // Panel principal con gradiente naranja pastel
        GradientPanel panel = new GradientPanel(
            new Color(255, 179, 71),  // Naranja pastel claro
            new Color(255, 107, 107)  // Rosa/naranja suave
        );
        panel.setLayout(new BorderLayout());
        
        // Panel para centrar la imagen
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        
        // Intentar cargar la imagen
        ImageIcon logo = loadLogo();
        
        if (logo != null) {
            // Si se cargó la imagen, mostrarla
            JLabel logoLabel = new JLabel(logo);
            centerPanel.add(logoLabel);
        } else {
            // Fallback si no encuentra la imagen
            JLabel textLabel = new JLabel("E-VALUA", SwingConstants.CENTER);
            textLabel.setFont(new Font("Verdana", Font.BOLD, 48));
            textLabel.setForeground(Color.BLACK);
            centerPanel.add(textLabel);
        }
        
        panel.add(centerPanel, BorderLayout.CENTER);
        
        // Texto de carga en la parte inferior
        JLabel loadingLabel = new JLabel("Cargando E-VALUA...", SwingConstants.CENTER);
        loadingLabel.setFont(new Font("Verdana", Font.BOLD, 16));
        loadingLabel.setForeground(Color.BLACK);
        loadingLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 30, 0));
        panel.add(loadingLabel, BorderLayout.SOUTH);
        
        add(panel);
    }
    
    /**
     * Intenta cargar el logo desde varias ubicaciones posibles
     */
    private ImageIcon loadLogo() {
        // Lista de rutas posibles donde podría estar la imagen
        String[] possiblePaths = {
            "/com/gamerker/io/e/valua_java/utils/resources/logo.png",  // Si está en resources
            "/com/gamerker/io/e/valua_java/utils/resources/logo.jpg",
            "/com/gamerker/io/e/valua_java/utils/resources/name_logo.png",
            "/com/gamerker/io/e/valua_java/utils/resources/name_logo.jpg",
            "resources/logo.png",  // Ruta relativa
            "logo.png",
            "src/main/resources/logo.png"  // Si usas Maven/Gradle
        };
        
        for (String path : possiblePaths) {
            try {
                URL url = getClass().getResource(path);
                if (url != null) {
                    System.out.println("Logo encontrado en: " + path);
                    ImageIcon icon = new ImageIcon(url);
                    
                    // Redimensionar la imagen si es muy grande
                    if (icon.getIconWidth() > 400 || icon.getIconHeight() > 300) {
                        Image img = icon.getImage();
                        Image scaledImg = img.getScaledInstance(300, 300, Image.SCALE_SMOOTH);
                        icon = new ImageIcon(scaledImg);
                    }
                    
                    return icon;
                }
            } catch (Exception e) {
                // Continuar con la siguiente ruta
            }
        }
        
        // Si no encuentra en ninguna ruta, intentar cargar desde el sistema de archivos
        try {
            // Rutas absolutas comunes
            String[] filePaths = {
                "utils/resources/logo.png",
                "src/com/gamerker/io/e/valua_java/utils/resources/logo.png",
                System.getProperty("user.dir") + "/utils/resources/logo.png"
            };
            
            for (String filePath : filePaths) {
                java.io.File file = new java.io.File(filePath);
                if (file.exists()) {
                    System.out.println("Logo encontrado en archivo: " + filePath);
                    ImageIcon icon = new ImageIcon(filePath);
                    
                    // Redimensionar
                    if (icon.getIconWidth() > 400 || icon.getIconHeight() > 300) {
                        Image img = icon.getImage();
                        Image scaledImg = img.getScaledInstance(400, 300, Image.SCALE_SMOOTH);
                        icon = new ImageIcon(scaledImg);
                    }
                    
                    return icon;
                }
            }
        } catch (Exception e) {
            System.err.println("Error cargando logo desde archivo: " + e.getMessage());
        }
        
        System.err.println("No se pudo encontrar el logo en ninguna ubicación");
        return null;
    }
    
    /**
     * Muestra la ventana y la cierra automáticamente después de SPLASH_DURATION ms
     */
    public void showWithTimer(AppController appController) {
        setVisible(true);
        
        // Temporizador para cerrar la ventana
        Timer timer = new Timer(SPLASH_DURATION, e -> {
            dispose();
            // Crear y mostrar la ventana principal
            SwingUtilities.invokeLater(() -> {
                try {
                    User activeUser = appController.loadActiveSession();
                        if (activeUser != null) {
                            // Si hay sesión activa, ir directamente al dashboard
                            SwingUtilities.invokeLater(() -> {
                                UserDashboard dashboard = new UserDashboard(appController, activeUser);
                                dashboard.setVisible(true);
                            });
                        } else {
                            // Si no hay sesión, mostrar MainFrame normal
                            SwingUtilities.invokeLater(() -> {
                                MainFrame mainFrame = new MainFrame(appController);
                                mainFrame.setVisible(true);
                            });
                        }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, 
                        "Error al iniciar la aplicación: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                    System.exit(1);
                }
            });
        });
        timer.setRepeats(false);
        timer.start();
    }
}