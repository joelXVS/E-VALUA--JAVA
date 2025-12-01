/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gamerker.io.e.valua_java;
/**
 *
 * @author hp
 */
import com.gamerker.io.e.valua_java.controllersPack.AppController;
import com.gamerker.io.e.valua_java.visuals.*;
import javax.swing.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Lanzador principal de la aplicación GUI.
 * Gestiona:
 * - Splash screen inicial con logo
 * - Carga de datos en segundo plano
 - Manejo de errores críticos
 * - Transición a la ventana principal
 */
public class AppLauncher {
    private static final int SPLASH_DURATION = 2000; // Mínimo 2 segundos
    
    public static void main(String[] args) {
        // Configurar Look & Feel del sistema
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("No se pudo cargar el tema: " + e.getMessage());
        }
        
    // Crear splash screen
        SplashScreen splash = new SplashScreen();
        splash.setVisible(true);
        
        // Bandera de error
        AtomicBoolean errorCritico = new AtomicBoolean(false);
        
        // Worker para carga en background
        SwingWorker<AppController, Void> worker = new SwingWorker<AppController, Void>() {
            @Override
            protected AppController doInBackground() {
                try {
                    Thread.sleep(SPLASH_DURATION); // Mínimo tiempo visual
                    AppController controller = new AppController();
                    
                    // Validar datos básicos
                    if (controller.getUsers().isEmpty()) {
                        System.out.println("⚠️ Sin usuarios, se creará admin por defecto");
                    }
                    
                    return controller;
                } catch (Exception e) {
                    e.printStackTrace();
                    errorCritico.set(true);
                    return null;
                }
            }
            
            @Override
            protected void done() {
                try {
                    AppController controller = get();
                    splash.dispose();
                    
                    if (errorCritico.get() || controller == null) {
                        JOptionPane.showMessageDialog(null,
                            "❌ Error crítico al cargar:\n" +
                            "No se pudo inicializar la base de datos.\n" +
                            "Verifica los archivos JSON y la carpeta 'data'.",
                            "Error Fatal", JOptionPane.ERROR_MESSAGE);
                        System.exit(1);
                        return;
                    }
                    
                    // Mostrar ventana principal
                    SwingUtilities.invokeLater(() -> {
                        try {
                            MainFrame frame = new MainFrame(controller);
                            frame.setVisible(true);
                        } catch (Exception e) {
                            JOptionPane.showMessageDialog(null,
                                "Error en la interfaz: " + e.getMessage(),
                                "Error", JOptionPane.ERROR_MESSAGE);
                            e.printStackTrace();
                        }
                    });
                    
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null,
                        "Error inesperado: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                    System.exit(1);
                }
            }
        };
        
        worker.execute();
        
        // Timer de seguridad por si falla el worker
        Timer safetyTimer = new Timer(5000, e -> {
            if (splash.isVisible()) {
                System.err.println("⚠️ Timeout - Cerrando splash forzosamente");
                splash.dispose();
            }
        });
        safetyTimer.setRepeats(false);
        safetyTimer.start();
    }
}
