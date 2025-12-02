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
import java.io.File;
import java.net.URL;

public class InfoPanel extends GradientPanel {
    public InfoPanel() {
        super(new Color(255, 160, 122), new Color(255, 99, 71)); // Naranja claro a tomate suave
        
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        // Logo centrado
        ImageIcon logo = loadLogo();
        if (logo != null) {
            JLabel logoLabel = new JLabel(logo);
            logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
            logoLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
            add(logoLabel, BorderLayout.NORTH);
        } else {
            // Logo alternativo si no se encuentra
            JLabel logoLabel = new JLabel("E-VALUA", SwingConstants.CENTER);
            logoLabel.setFont(new Font("Arial Black", Font.BOLD, 36));
            logoLabel.setForeground(Color.WHITE);
            logoLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
            add(logoLabel, BorderLayout.NORTH);
        }
        
        // Panel central para mejor organización
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        
        // Panel central con BorderLayout para mejor control
        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setOpaque(false);
        
        // Texto informativo detallado
        String infoHTML = "<html>" +
            "<div style='text-align: center; font-family: Verdana, sans-serif;'>" +
            "<h1 style='color: #8B0000; margin-bottom: 20px;'>SISTEMA DE EVALUACIÓN ACADÉMICA E-VALUA</h1>" +
            
            "<div style='background-color: rgba(255, 255, 255, 0.8); padding: 20px; border-radius: 10px; margin: 10px;'>" +
            
            "<h2 style='color: #8B0000;'>INFORMACIÓN DEL SISTEMA</h2>" +
            "<table style='margin: 0 auto; text-align: left; font-size: 14px;'>" +
            "<tr><td><b>Versión:</b></td><td> 1.7.0 (Estable)</td></tr>" +
            "<tr><td><b>Desarrollado por:</b></td><td> Gamerker IO</td></tr>" +
            "<tr><td><b>Fecha de lanzamiento:</b></td><td> 2025</td></tr>" +
            "<tr><td><b>Tecnologías:</b></td><td> Java Swing, JSON, GSON</td></tr>" +
            "</table><br>" +
            
            "<h2 style='color: #8B0000;'>FUNCIONALIDADES PRINCIPALES</h2>" +
            "<ul style='text-align: left; margin-left: 100px;'>" +
            "<li><b>Realización de pruebas académicas</b> - Evaluaciones en tiempo real</li>" +
            "<li><b>Sistema de recarga de saldo</b> - Tarjetas prepago integradas</li>" +
            "<li><b>Ranking competitivo</b> - Clasificación de estudiantes</li>" +
            "<li><b>Exportación de resultados</b> - PDF profesionales</li>" +
            "<li><b>Facturación automática</b> - Sistema de cobro</li>" +
            "<li><b>Gestión de usuarios</b> - Roles y permisos</li>" +
            "<li><b>Banco de preguntas</b> - Base de datos extensa</li>" +
            "<li><b>Reportes estadísticos</b> - Análisis de rendimiento</li>" +
            "</ul><br>" +
            
            "<h2 style='color: #8B0000;'>ROLES DEL SISTEMA</h2>" +
            "<table style='margin: 0 auto; width: 80%; border-collapse: collapse;'>" +
            "<tr style='background-color: #FFA07A;'>" +
            "<th style='padding: 8px; border: 1px solid #8B0000;'>Rol</th>" +
            "<th style='padding: 8px; border: 1px solid #8B0000;'>Permisos</th>" +
            "<th style='padding: 8px; border: 1px solid #8B0000;'>Acceso</th>" +
            "</tr>" +
            "<tr>" +
            "<td style='padding: 8px; border: 1px solid #8B0000;'><b>Estudiante</b></td>" +
            "<td style='padding: 8px; border: 1px solid #8B0000;'>Realizar pruebas, ver resultados, ranking</td>" +
            "<td style='padding: 8px; border: 1px solid #8B0000;'>Básico</td>" +
            "</tr>" +
            "<tr style='background-color: rgba(255, 255, 255, 0.5);'>" +
            "<td style='padding: 8px; border: 1px solid #8B0000;'><b>Profesor</b></td>" +
            "<td style='padding: 8px; border: 1px solid #8B0000;'>Crear pruebas, calificar, gestionar preguntas</td>" +
            "<td style='padding: 8px; border: 1px solid #8B0000;'>Intermedio</td>" +
            "</tr>" +
            "<tr>" +
            "<td style='padding: 8px; border: 1px solid #8B0000;'><b>Administrador</b></td>" +
            "<td style='padding: 8px; border: 1px solid #8B0000;'>Gestión completa, usuarios, reportes, sistema</td>" +
            "<td style='padding: 8px; border: 1px solid #8B0000;'>Total</td>" +
            "</tr>" +
            "</table><br>" +
            
            "<h2 style='color: #8B0000;'>ESTADÍSTICAS DEL SISTEMA</h2>" +
            "<ul style='text-align: left; margin-left: 100px;'>" +
            "<li><b>Capacidad:</b> Hasta 1000 usuarios simultáneos</li>" +
            "<li><b>Almacenamiento:</b> 10,000+ preguntas en base de datos</li>" +
            "<li><b>Rendimiento:</b> Respuesta en menos de 2 segundos</li>" +
            "<li><b>Seguridad:</b> Encriptación AES-256 para datos sensibles</li>" +
            "<li><b>Backup:</b> Copias de seguridad automáticas diarias</li>" +
            "</ul><br>" +
            
            "<h2 style='color: #8B0000;'>REQUISITOS TÉCNICOS</h2>" +
            "<table style='margin: 0 auto; width: 80%;'>" +
            "<tr><td><b>Sistema Operativo:</b></td><td> Windows 10/11, Linux, macOS</td></tr>" +
            "<tr><td><b>Java:</b></td><td> JDK 11 o superior</td></tr>" +
            "<tr><td><b>RAM:</b></td><td> Mínimo 2GB, Recomendado 4GB</td></tr>" +
            "<tr><td><b>Espacio:</b></td><td> 500MB de espacio libre</td></tr>" +
            "<tr><td><b>Conexión:</b></td><td> Internet para funciones en línea</td></tr>" +
            "</table><br>" +
            
            "<h2 style='color: #8B0000;'>SOPORTE Y CONTACTO</h2>" +
            "<p style='font-size: 13px;'>" +
            "<b>Email:</b> soporte@gamerker.io<br>" +
            "<b>Sitio Web:</b> www.gamerker.io/e-valua<br>" +
            "<b>Teléfono:</b> +1 (555) 123-4567<br>" +
            "<b>Horario:</b> Lunes a Viernes, 9:00 - 18:00<br><br>" +
            "</p>" +
            
            "</div>" +
            
            "<div style='margin-top: 20px; padding: 15px; background-color: rgba(139, 0, 0, 0.8); border-radius: 10px;'>" +
            "<p style='color: white; font-size: 12px;'>" +
            "© 2025 E-VALUA - Todos los derechos reservados. Gamerker IO es una marca registrada.<br>" +
            "Este software está protegido por leyes de derechos de autor internacionales.<br>" +
            "Uso no autorizado está estrictamente prohibido." +
            "</p>" +
            "</div>" +
            
            "</div></html>";
        
        JTextPane txtPane = new JTextPane();
        txtPane.setContentType("text/html");
        txtPane.setText(infoHTML);
        txtPane.setEditable(false);
        txtPane.setBackground(new Color(0, 0, 0, 0));
        txtPane.setBorder(null);

        // Panel con scroll
        JScrollPane scrollPane = new JScrollPane(txtPane);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(139, 0, 0, 100), 2));
        scrollPane.setPreferredSize(new Dimension(780, 225));

        JPanel txtPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        txtPanel.setOpaque(false);
        txtPanel.add(scrollPane);

        centerPanel.add(txtPanel, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);
        
        // Panel inferior para mensaje adicional
        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        
        JLabel versionLabel = new JLabel("E-VALUA v1.7.0 | Build 2025.12.01 | © Gamerker IO");
        versionLabel.setFont(new Font("Verdana", Font.BOLD, 11));
        versionLabel.setForeground(new Color(139, 0, 0));
        versionLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        bottomPanel.add(versionLabel);
        add(bottomPanel, BorderLayout.SOUTH);
        
        // Ajustar tamaño preferido
        setPreferredSize(new Dimension(800, 600));
    }
    
    // Método para actualizar información dinámica
    public void updateInfo(String additionalInfo) {
        // Método para actualizar información si es necesario
        System.out.println("Información actualizada: " + additionalInfo);
    }
    
    private ImageIcon loadLogo() {
        // Lista de rutas posibles donde podría estar la imagen
        String[] possiblePaths = {
            "/com/gamerker/io/e/valua_java/utils/resources/name_logo.png",
            "/com/gamerker/io/e/valua_java/utils/resources/name_logo.jpg",
            ""
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
                "utils/resources/name_logo.png",
                "src/com/gamerker/io/e/valua_java/utils/resources/name_logo.png",
                System.getProperty("user.dir") + "/utils/resources/name_logo.png"
            };
            
            for (String filePath : filePaths) {
                File file = new File(filePath);
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
}