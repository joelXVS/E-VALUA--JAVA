/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gamerker.io.e.valua_java.visuals;
/**
 *
 * @author hp
 */
import com.gamerker.io.e.valua_java.utils.GradientPanel;
import javax.swing.*;
import java.awt.*;
import java.net.URL;

/**
 * Ventana de splash que se muestra al iniciar la aplicación.
 * Muestra el logo.png durante 2 segundos con fondo gradiente.
 */
public class SplashScreen extends JWindow {
    
    private static final int SPLASH_DURATION = 7000; // 7 segundos
    
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
        
        // Cargar y mostrar el logo
        try {
            URL logoUrl = getClass().getResource("../resources/logo.png");
            if (logoUrl != null) {
                ImageIcon logo = new ImageIcon(logoUrl);
                JLabel logoLabel = new JLabel(logo);
                logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
                panel.add(logoLabel, BorderLayout.CENTER);
            } else {
                // Fallback si no encuentra la imagen
                JLabel logoLabel = new JLabel("E-VALUA", SwingConstants.CENTER);
                logoLabel.setFont(new Font("Verdana", Font.BOLD, 48));
                logoLabel.setForeground(Color.BLACK);
                panel.add(logoLabel, BorderLayout.CENTER);
            }
        } catch (Exception e) {
            System.err.println("Error al cargar logo.png: " + e.getMessage());
            JLabel errorLabel = new JLabel("Logo no encontrado", SwingConstants.CENTER);
            errorLabel.setFont(new Font("Verdana", Font.PLAIN, 24));
            errorLabel.setForeground(Color.BLACK);
            panel.add(errorLabel, BorderLayout.CENTER);
        }
        
        // Texto de carga en la parte inferior
        JLabel loadingLabel = new JLabel("Cargando E-VALUA...", SwingConstants.CENTER);
        loadingLabel.setFont(new Font("Verdana", Font.BOLD, 16));
        loadingLabel.setForeground(Color.BLACK);
        loadingLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 30, 0));
        panel.add(loadingLabel, BorderLayout.SOUTH);
        
        add(panel);
    }
    
    /**
     * Muestra la ventana y la cierra automáticamente después de SPLASH_DURATION ms
     */
    public void showWithTimer() {
        setVisible(true);
        
        // Temporizador para cerrar la ventana
        Timer timer = new Timer(SPLASH_DURATION, e -> dispose());
        timer.setRepeats(false);
        timer.start();
    }
}