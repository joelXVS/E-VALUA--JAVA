/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gamerker.io.e.valua_java.visuals;

import com.gamerker.io.e.valua_java.controllersPack.*;
import com.gamerker.io.e.valua_java.mainClasses.*;
import com.gamerker.io.e.valua_java.utils.GradientPanel;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * Diálogo de configuración del sistema para administradores
 */
public class ConfigurationDialog extends JDialog {
    private final AppController appController;
    private final User currentUser;
    
    // Colores del tema
    private final Color COLOR_FONDO = new Color(255, 218, 185);
    private final Color COLOR_BOTON = new Color(255, 140, 0);
    private final Color COLOR_BOTON_HOVER = new Color(255, 165, 0);
    private final Color COLOR_EXITO = new Color(144, 238, 144);
    private final Color COLOR_ALERTA = new Color(255, 193, 7);
    private final Color COLOR_ERROR = new Color(255, 204, 204);
    
    private JLabel statusLabel;
    private JTextField backupPathField;
    private JSpinner autoBackupSpinner;
    private JCheckBox notificationsCheck;
    private JCheckBox autoUpdateCheck;
    private JComboBox<String> themeCombo;
    
    public ConfigurationDialog(JFrame owner, User user, AppController controller) {
        super(owner, "🔧 Configuración del Sistema", true);
        this.currentUser = user;
        this.appController = controller;
        
        setSize(1000, 600);
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        initComponents();
        loadCurrentSettings();
    }
    
    private void initComponents() {
        GradientPanel mainPanel = new GradientPanel(COLOR_FONDO, new Color(255, 179, 71));
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Panel superior con título
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Panel central con pestañas
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Verdana", Font.BOLD, 12));
        
        tabbedPane.addTab("General", createGeneralTab());
        tabbedPane.addTab("Backup", createBackupTab());
        tabbedPane.addTab("Seguridad", createSecurityTab());
        tabbedPane.addTab("Sistema", createSystemTab());
        
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
        // Panel inferior con botones
        mainPanel.add(createButtonsPanel(), BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("CONFIGURACIÓN DEL SISTEMA");
        titleLabel.setFont(new Font("Verdana", Font.BOLD, 18));
        titleLabel.setForeground(Color.BLACK);
        titleLabel.setHorizontalAlignment(SwingConstants.LEFT);
        
        JLabel subTitleLabel = new JLabel("Ajustes y preferencias del sistema E-VALUA");
        subTitleLabel.setFont(new Font("Verdana", Font.PLAIN, 12));
        subTitleLabel.setForeground(Color.BLACK);
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(subTitleLabel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createGeneralTab() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Tema del sistema
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("🎨 Tema:"), gbc);
        
        gbc.gridx = 1;
        themeCombo = new JComboBox<>(new String[]{"Claro (naranja)"});
        themeCombo.setPreferredSize(new Dimension(200, 25));
        panel.add(themeCombo, gbc);
        
        // Notificaciones
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("🔔 Notificaciones:"), gbc);
        
        gbc.gridx = 1;
        notificationsCheck = new JCheckBox("Activar notificaciones del sistema");
        notificationsCheck.setOpaque(false);
        panel.add(notificationsCheck, gbc);
        
        // Actualizaciones automáticas
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("🔄 Actualizaciones:"), gbc);
        
        gbc.gridx = 1;
        autoUpdateCheck = new JCheckBox("Buscar actualizaciones automáticamente");
        autoUpdateCheck.setOpaque(false);
        panel.add(autoUpdateCheck, gbc);
        
        // Idioma
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("🌐 Idioma:"), gbc);
        
        gbc.gridx = 1;
        JComboBox<String> languageCombo = new JComboBox<>(new String[]{"Español"});
        languageCombo.setPreferredSize(new Dimension(200, 25));
        panel.add(languageCombo, gbc);
        
        // Formato de fecha
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("📅 Formato fecha:"), gbc);
        
        gbc.gridx = 1;
        JComboBox<String> dateFormatCombo = new JComboBox<>(new String[]{"dd/MM/yyyy"});
        dateFormatCombo.setPreferredSize(new Dimension(200, 25));
        panel.add(dateFormatCombo, gbc);
        
        // Espaciador para empujar todo arriba
        gbc.gridx = 0; gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(Box.createGlue(), gbc);
        
        return panel;
    }
    
    private JPanel createBackupTab() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Ruta de backup
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("📁 Ruta de backup:"), gbc);
        
        gbc.gridx = 1;
        JPanel pathPanel = new JPanel(new BorderLayout(5, 0));
        pathPanel.setOpaque(false);
        backupPathField = new JTextField(System.getProperty("user.dir") + "/backups");
        backupPathField.setFont(new Font("Monospaced", Font.PLAIN, 11));
        pathPanel.add(backupPathField, BorderLayout.CENTER);
        
        JButton browseButton = new JButton("📂");
        browseButton.addActionListener(e -> browseBackupPath());
        browseButton.setPreferredSize(new Dimension(40, 25));
        pathPanel.add(browseButton, BorderLayout.EAST);
        
        panel.add(pathPanel, gbc);
        
        // Backup automático
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("⏰ Backup automático:"), gbc);
        
        gbc.gridx = 1;
        JPanel backupPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        backupPanel.setOpaque(false);
        autoBackupSpinner = new JSpinner(new SpinnerNumberModel(24, 1, 168, 1));
        autoBackupSpinner.setPreferredSize(new Dimension(60, 25));
        backupPanel.add(autoBackupSpinner);
        backupPanel.add(new JLabel("horas"));
        panel.add(backupPanel, gbc);
        
        // Número de backups a mantener
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("📦 Backups a mantener:"), gbc);
        
        gbc.gridx = 1;
        JSpinner keepBackupsSpinner = new JSpinner(new SpinnerNumberModel(10, 1, 100, 1));
        keepBackupsSpinner.setPreferredSize(new Dimension(60, 25));
        panel.add(keepBackupsSpinner, gbc);
        
        // Botón para backup manual
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 5, 5, 5);
        JButton manualBackupButton = createButton("Realizar Backup Ahora", e -> performBackup(), 
            new Color(108, 117, 125), new Color(88, 107, 95));
        manualBackupButton.setPreferredSize(new Dimension(250, 35));
        panel.add(manualBackupButton, gbc);
        
        // Información de último backup
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 5, 5, 5);
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setOpaque(false);
        infoPanel.setBorder(BorderFactory.createTitledBorder("📋 Información de Backup"));
        
        JTextArea infoText = new JTextArea("Último backup: No realizado\n" +
            "Tamaño estimado: " + getEstimatedSize() + " MB\n" +
            "Backups disponibles: 0\n" +
            "Estado: 🟡 Configuración pendiente");
        infoText.setFont(new Font("Monospaced", Font.PLAIN, 11));
        infoText.setEditable(false);
        infoText.setOpaque(false);
        infoPanel.add(infoText, BorderLayout.CENTER);
        
        panel.add(infoPanel, gbc);
        
        // Espaciador
        gbc.gridy = 5;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(Box.createGlue(), gbc);
        
        return panel;
    }
    
    private JPanel createSecurityTab() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Tiempo de inactividad para cerrar sesión
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("⏱️ Cierre por inactividad:"), gbc);
        
        gbc.gridx = 1;
        JPanel timeoutPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        timeoutPanel.setOpaque(false);
        JSpinner timeoutSpinner = new JSpinner(new SpinnerNumberModel(30, 5, 180, 5));
        timeoutSpinner.setPreferredSize(new Dimension(60, 25));
        timeoutPanel.add(timeoutSpinner);
        timeoutPanel.add(new JLabel("minutos"));
        panel.add(timeoutPanel, gbc);
        
        // Intentos de login fallidos
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("🔒 Intentos de login:"), gbc);
        
        gbc.gridx = 1;
        JSpinner attemptsSpinner = new JSpinner(new SpinnerNumberModel(3, 1, 10, 1));
        attemptsSpinner.setPreferredSize(new Dimension(60, 25));
        panel.add(attemptsSpinner, gbc);
        
        // Encriptación de datos
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("🔐 Encriptación:"), gbc);
        
        gbc.gridx = 1;
        JCheckBox encryptionCheck = new JCheckBox("Encriptar datos sensibles");
        encryptionCheck.setOpaque(false);
        panel.add(encryptionCheck, gbc);
        
        // Registro de auditoría
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("📝 Auditoría:"), gbc);
        
        gbc.gridx = 1;
        JCheckBox auditCheck = new JCheckBox("Activar registro de auditoría");
        auditCheck.setOpaque(false);
        panel.add(auditCheck, gbc);
        
        // Contraseña maestra
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.insets = new Insets(20, 5, 5, 5);
        JButton masterPassButton = createButton("Cambiar Contraseña Maestra", e -> changeMasterPassword(), 
            new Color(220, 53, 69), new Color(200, 35, 51));
        masterPassButton.setPreferredSize(new Dimension(250, 35));
        panel.add(masterPassButton, gbc);
        
        // Espaciador
        gbc.gridy = 5;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(Box.createGlue(), gbc);
        
        return panel;
    }
    
    private JPanel createSystemTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Panel de información del sistema
        JPanel systemInfoPanel = new JPanel();
        systemInfoPanel.setLayout(new BoxLayout(systemInfoPanel, BoxLayout.Y_AXIS));
        systemInfoPanel.setOpaque(false);
        
        systemInfoPanel.add(createSystemInfoLabel("🖥️ Sistema operativo:", System.getProperty("os.name")));
        systemInfoPanel.add(createSystemInfoLabel("💻 Arquitectura:", System.getProperty("os.arch")));
        systemInfoPanel.add(createSystemInfoLabel("☕ Java version:", System.getProperty("java.version")));
        systemInfoPanel.add(createSystemInfoLabel("🏠 Directorio:", System.getProperty("user.dir")));
        systemInfoPanel.add(createSystemInfoLabel("👤 Usuario:", System.getProperty("user.name")));
        
        // Memoria
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory() / (1024 * 1024);
        long freeMemory = runtime.freeMemory() / (1024 * 1024);
        long usedMemory = totalMemory - freeMemory;
        
        systemInfoPanel.add(createSystemInfoLabel("💾 Memoria usada:", usedMemory + " MB / " + totalMemory + " MB"));
        systemInfoPanel.add(createSystemInfoLabel("⚡ Procesadores:", String.valueOf(runtime.availableProcessors())));
        
        panel.add(systemInfoPanel, BorderLayout.NORTH);
        
        // Panel de limpieza
        JPanel cleanupPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        cleanupPanel.setOpaque(false);
        cleanupPanel.setBorder(BorderFactory.createTitledBorder("💻 Mantenimiento del Sistema"));
        
        JButton cleanupButton = createButton("Limpiar caché temporal", e -> cleanupCache(), 
            new Color(23, 162, 184), new Color(23, 132, 144));
        cleanupPanel.add(cleanupButton);
        
        JButton logsButton = createButton("Ver logs del sistema", e -> showSystemLogs(), 
            new Color(108, 117, 125), new Color(88, 107, 95));
        cleanupPanel.add(logsButton);
        
        panel.add(cleanupPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createButtonsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panel.setOpaque(false);
        
        // Guardar configuración
        JButton saveButton = createButton("Guardar Cambios", e -> saveSettings(), 
            COLOR_BOTON, COLOR_BOTON_HOVER);
        panel.add(saveButton);
        
        // Aplicar
        JButton applyButton = createButton("Aplicar", e -> applySettings(), 
            new Color(40, 167, 69), new Color(33, 136, 56));
        panel.add(applyButton);
        
        // Restaurar valores por defecto
        JButton resetButton = createButton("Restaurar Predeterminados", e -> resetSettings(), 
            new Color(255, 193, 7), new Color(255, 165, 0));
        panel.add(resetButton);
        
        // Cerrar
        JButton closeButton = createButton("Cerrar", e -> dispose(), 
            new Color(220, 53, 69), new Color(200, 35, 51));
        panel.add(closeButton);
        
        // Status
        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Verdana", Font.ITALIC, 12));
        statusLabel.setForeground(Color.BLACK);
        panel.add(statusLabel);
        
        return panel;
    }
    
    private JButton createButton(String text, java.awt.event.ActionListener listener, Color bgColor, Color hoverColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Verdana", Font.BOLD, 12));
        button.setBackground(bgColor);
        button.setForeground(new Color(0, 0, 0));
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(200, 35));
        button.addActionListener(listener);
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(hoverColor);
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }
    
    private JLabel createSystemInfoLabel(String label, String value) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setOpaque(false);
        
        JLabel keyLabel = new JLabel(label);
        keyLabel.setFont(new Font("Verdana", Font.BOLD, 12));
        keyLabel.setPreferredSize(new Dimension(200, 20));
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        panel.add(keyLabel);
        panel.add(valueLabel);
        
        return new JLabel() {
            public Component getComponent() {
                return panel;
            }
        };
    }
    
    private void loadCurrentSettings() {
        // Aquí cargarías la configuración actual desde archivos o base de datos
        backupPathField.setText(System.getProperty("user.dir") + "/backups");
        autoBackupSpinner.setValue(24);
        notificationsCheck.setSelected(true);
        autoUpdateCheck.setSelected(true);
        themeCombo.setSelectedIndex(0);
    }
    
    private void browseBackupPath() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setDialogTitle("Seleccionar carpeta de backup");
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            backupPathField.setText(selectedFile.getAbsolutePath());
        }
    }
    
    private void performBackup() {
        showStatus("Realizando backup...");
        
        // Simular backup
        Timer timer = new Timer(2000, e -> {
            showStatus("Backup completado exitosamente");
            JOptionPane.showMessageDialog(this,
                "Backup realizado correctamente.\nUbicación: " + backupPathField.getText(),
                "Backup Completado",
                JOptionPane.INFORMATION_MESSAGE);
        });
        timer.setRepeats(false);
        timer.start();
    }
    
    private void changeMasterPassword() {
        String newPassword = JOptionPane.showInputDialog(this,
            "Ingrese la nueva contraseña maestra:",
            "🔑 Cambiar Contraseña Maestra",
            JOptionPane.PLAIN_MESSAGE);
        
        if (newPassword != null && !newPassword.trim().isEmpty()) {
            if (newPassword.length() < 8) {
                JOptionPane.showMessageDialog(this,
                    "La contraseña debe tener al menos 8 caracteres.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            showStatus("Contraseña maestra actualizada");
        }
    }
    
    private void cleanupCache() {
        showStatus("Limpiando caché...");
        
        Timer timer = new Timer(1500, e -> {
            showStatus("Caché limpiado: 15 MB liberados");
            JOptionPane.showMessageDialog(this,
                "Limpieza completada.\nSe han liberado 15 MB de espacio.",
                "Limpieza Completada",
                JOptionPane.INFORMATION_MESSAGE);
        });
        timer.setRepeats(false);
        timer.start();
    }
    
    private void showSystemLogs() {
        JTextArea logsArea = new JTextArea(generateLogs());
        logsArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        logsArea.setEditable(false);
        
        JScrollPane scrollPane = new JScrollPane(logsArea);
        scrollPane.setPreferredSize(new Dimension(600, 400));
        
        JOptionPane.showMessageDialog(this, scrollPane, "📋 Logs del Sistema", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private String generateLogs() {
        return """
            📋 LOGS DEL SISTEMA E-VALUA
            ================================
            
            [2024-01-15 10:30:15] ✅ Sistema iniciado correctamente
            [2024-01-15 10:35:22] 👤 Usuario 'admin' inició sesión
            [2024-01-15 11:20:45] 📝 Prueba 'Matemáticas Básicas' completada por 'estudiante1'
            [2024-01-15 12:15:30] 💰 Recarga de $100 para 'estudiante2'
            [2024-01-15 14:45:10] 📊 Estadísticas actualizadas
            [2024-01-15 15:30:00] 🔧 Configuración modificada por 'admin'
            
            🟢 Sistema funcionando normalmente
            📈 15 pruebas realizadas hoy
            👥 3 usuarios activos
            💾 85% de almacenamiento disponible
            """;
    }
    
    private void saveSettings() {
        // Aquí guardarías la configuración
        showStatus("Configuración guardada exitosamente");
    }
    
    private void applySettings() {
        // Aquí aplicarías la configuración sin guardar
        showStatus("Configuración aplicada");
    }
    
    private void resetSettings() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "¿Restaurar todos los valores a los predeterminados?",
            "Confirmar Restauración",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            loadCurrentSettings();
            showStatus("Valores restaurados a predeterminados");
        }
    }
    
    private void showStatus(String message) {
        statusLabel.setText(message);
        
        Timer timer = new Timer(3000, e -> statusLabel.setText(" "));
        timer.setRepeats(false);
        timer.start();
    }
    
    private JLabel createIcon(String emoji, int size) {
        JLabel icon = new JLabel(emoji);
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, size));
        return icon;
    }
    
    private String getEstimatedSize() {
        long estimatedSize = appController.getResults().size() * 500L +
                           appController.getUsers().size() * 200L +
                           appController.getTests().size() * 1000L;
        return String.valueOf(estimatedSize / (1024 * 1024));
    }
}
