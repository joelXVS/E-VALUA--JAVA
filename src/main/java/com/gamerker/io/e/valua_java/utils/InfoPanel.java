/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gamerker.io.e.valua_java.utils;

/**
 *
 * @author hp
 */
import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class InfoPanel extends GradientPanel {
    public InfoPanel() {
        super(new Color(255, 160, 122), new Color(255, 99, 71)); // Naranja claro a tomate suave
        
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        // Logo centrado
        try {
            URL logoUrl = getClass().getResource("/resources/logo_name.png");
            if (logoUrl != null) {
                ImageIcon logo = new ImageIcon(logoUrl);
                JLabel logoLabel = new JLabel(logo);
                logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
                logoLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
                add(logoLabel, BorderLayout.NORTH);
            }
        } catch (Exception e) {
            System.err.println("No se pudo cargar logo_name.png: " + e.getMessage());
        }
        
        // Texto informativo
        JTextArea infoText = new JTextArea();
        infoText.setText("""
            SISTEMA DE EVALUACIÓN ACADÉMICA E-VALUA
            
            Versión: 1.0.0
            Desarrollado por: Gamerker IO
            
            Funcionalidades principales:
            • Realización de pruebas académicas
            • Recarga de saldo con tarjetas
            • Ranking de estudiantes
            • Exportación de resultados a PDF
            • Facturación automática
            
            Roles disponibles:
            • Estudiante: Acceso a pruebas y resultados
            • Profesor: Creación de pruebas
            • Administrador: Gestión completa del sistema
            
            © 2025 E-VALUA - Todos los derechos reservados
            """);
        infoText.setFont(new Font("Verdana", Font.PLAIN, 14));
        infoText.setForeground(Color.BLACK);
        infoText.setBackground(new Color(0, 0, 0, 0)); // Transparente
        infoText.setEditable(false);
        infoText.setLineWrap(true);
        infoText.setWrapStyleWord(true);
        infoText.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JScrollPane scrollPane = new JScrollPane(infoText);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.setPreferredSize(new Dimension(700, 400));
        
        add(scrollPane, BorderLayout.CENTER);
    }
}
