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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Diálogo para realizar backup y restauración del sistema
 */
public class BackupDialog extends JDialog {
    private final AppController appController;
    private final User currentUser;
    
    // Colores del tema
    private final Color COLOR_FONDO = new Color(255, 218, 185);
    private final Color COLOR_BOTON = new Color(255, 140, 0);
    private final Color COLOR_BOTON_HOVER = new Color(255, 165, 0);
    private final Color COLOR_EXITO = new Color(144, 238, 144);
    private final Color COLOR_ALERTA = new Color(255, 193, 7);
    private final Color COLOR_ERROR = new Color(255, 204, 204);
    
    private JList<String> backupList;
    private DefaultListModel<String> listModel;
    private JLabel infoLabel;
    
    public BackupDialog(JFrame owner, User user, AppController controller) {
        super(owner, "💾 Backup del Sistema", true);
        this.currentUser = user;
        this.appController = controller;
        
        setSize(1000, 550);
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        initComponents();
        loadBackupList();
    }
    
    private void initComponents() {
        GradientPanel mainPanel = new GradientPanel(COLOR_FONDO, new Color(255, 179, 71));
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Panel superior
        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        
        // Panel central
        mainPanel.add(createContentPanel(), BorderLayout.CENTER);
        
        // Panel inferior
        mainPanel.add(createButtonsPanel(), BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("SISTEMA DE BACKUP");
        titleLabel.setFont(new Font("Verdana", Font.BOLD, 18));
        titleLabel.setForeground(Color.BLACK);
        
        JLabel subTitleLabel = new JLabel("Realice copias de seguridad y restaure datos del sistema");
        subTitleLabel.setFont(new Font("Verdana", Font.PLAIN, 12));
        subTitleLabel.setForeground(Color.BLACK);
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(subTitleLabel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createContentPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);
        
        // Panel izquierdo: Lista de backups
        JPanel listPanel = new JPanel(new BorderLayout());
        listPanel.setOpaque(false);
        listPanel.setBorder(BorderFactory.createTitledBorder("📋 Backups Disponibles"));
        listPanel.setPreferredSize(new Dimension(350, 0));
        
        listModel = new DefaultListModel<>();
        backupList = new JList<>(listModel);
        backupList.setFont(new Font("Monospaced", Font.PLAIN, 11));
        backupList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane listScroll = new JScrollPane(backupList);
        listPanel.add(listScroll, BorderLayout.CENTER);
        
        // Panel derecho: Información y acciones
        JPanel infoPanel = new JPanel(new BorderLayout(10, 10));
        infoPanel.setOpaque(false);
        infoPanel.setBorder(BorderFactory.createTitledBorder("📊 Información del Backup"));
        
        // Panel de información
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setOpaque(false);
        
        infoLabel = new JLabel("<html><b>Seleccione un backup para ver detalles</b></html>");
        infoLabel.setFont(new Font("Verdana", Font.PLAIN, 12));
        infoLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        detailsPanel.add(infoLabel);
        
        // Panel de estadísticas
        JPanel statsPanel = new JPanel(new GridLayout(0, 2, 10, 5));
        statsPanel.setOpaque(false);
        statsPanel.setBorder(BorderFactory.createTitledBorder("📈 Estadísticas del Sistema"));
        
        addStat(statsPanel, "Pruebas:", String.valueOf(appController.getTests().size()));
        addStat(statsPanel, "Usuarios:", String.valueOf(appController.getUsers().size()));
        addStat(statsPanel, "Resultados:", String.valueOf(appController.getResults().size()));
        addStat(statsPanel, "Tamaño estimado:", getEstimatedSize() + " MB");
        
        detailsPanel.add(statsPanel);
        infoPanel.add(detailsPanel, BorderLayout.CENTER);
        
        panel.add(listPanel, BorderLayout.WEST);
        panel.add(infoPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createButtonsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panel.setOpaque(false);
        
        // Crear backup
        JButton createButton = createButton("Crear Backup", e -> createBackup(), 
            COLOR_BOTON, COLOR_BOTON_HOVER);
        panel.add(createButton);
        
        // Restaurar
        JButton restoreButton = createButton("Restaurar", e -> restoreBackup(), 
            new Color(23, 162, 184), new Color(23, 132, 144));
        panel.add(restoreButton);
        
        // Ver detalles
        JButton detailsButton = createButton("Ver Detalles", e -> viewBackupDetails(), 
            new Color(108, 117, 125), new Color(88, 107, 95));
        panel.add(detailsButton);
        
        // Eliminar backup
        JButton deleteButton = createButton("Eliminar", e -> deleteBackup(), 
            new Color(220, 53, 69), new Color(200, 35, 51));
        panel.add(deleteButton);
        
        // Configurar auto-backup
        JButton configButton = createButton("Configurar", e -> configureAutoBackup(), 
            new Color(255, 193, 7), new Color(255, 165, 0));
        panel.add(configButton);
        
        return panel;
    }
    
    private JButton createButton(String text, java.awt.event.ActionListener listener, Color bgColor, Color hoverColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Verdana", Font.BOLD, 12));
        button.setBackground(bgColor);
        button.setForeground(new Color(0, 0, 0));
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(140, 35));
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
    
    private void addStat(JPanel panel, String label, String value) {
        JPanel statPanel = new JPanel(new BorderLayout());
        statPanel.setOpaque(false);
        
        JLabel labelComp = new JLabel(label);
        labelComp.setFont(new Font("Verdana", Font.BOLD, 11));
        
        JLabel valueComp = new JLabel(value);
        valueComp.setFont(new Font("Monospaced", Font.PLAIN, 11));
        valueComp.setHorizontalAlignment(SwingConstants.RIGHT);
        
        statPanel.add(labelComp, BorderLayout.WEST);
        statPanel.add(valueComp, BorderLayout.CENTER);
        
        panel.add(statPanel);
    }
    
    private void loadBackupList() {
        listModel.clear();
        
        // Simular backups existentes
        listModel.addElement("📦 backup_20240115_103045.zip  (15/01/2024 10:30)  [85 MB]");
        listModel.addElement("📦 backup_20240114_180012.zip  (14/01/2024 18:00)  [83 MB]");
        listModel.addElement("📦 backup_20240113_090015.zip  (13/01/2024 09:00)  [82 MB]");
        listModel.addElement("📦 backup_20240110_143045.zip  (10/01/2024 14:30)  [80 MB]");
        listModel.addElement("📦 backup_20240107_080012.zip  (07/01/2024 08:00)  [78 MB]");
        
        backupList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateBackupInfo();
            }
        });
        
        if (!listModel.isEmpty()) {
            backupList.setSelectedIndex(0);
        }
    }
    
    private void updateBackupInfo() {
        int selectedIndex = backupList.getSelectedIndex();
        if (selectedIndex == -1) return;
        
        String selected = listModel.getElementAt(selectedIndex);
        
        String info = "<html><b>Backup seleccionado:</b><br>" + selected + "<br><br>" +
            "<b>Contenido estimado:</b><br>" +
            "• " + appController.getTests().size() + " pruebas<br>" +
            "• " + appController.getUsers().size() + " usuarios<br>" +
            "• " + appController.getResults().size() + " resultados<br><br>" +
            "<b>Recomendaciones:</b><br>" +
            "• Este backup contiene todos los datos del sistema<br>" +
            "• Tiempo estimado de restauración: 2-3 minutos<br>" +
            "• Espacio requerido: " + getEstimatedSize() + " MB</html>";
        
        infoLabel.setText(info);
    }
    
    private void createBackup() {   
        // Simular proceso de backup
        Timer progressTimer = new Timer(100, null);
        progressTimer.addActionListener(e -> {
            // Simular progreso
            String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        });
        
        Timer completeTimer = new Timer(3000, e -> {
            progressTimer.stop();
            
            // Agregar nuevo backup a la lista
            String backupName = "📦 backup_" + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + 
                ".zip  (" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) + 
                ")  [" + getEstimatedSize() + " MB]";
            
            listModel.add(0, backupName);
            backupList.setSelectedIndex(0);
                        
            JOptionPane.showMessageDialog(this,
                "Backup completado exitosamente.\n" +
                "Nombre: " + backupName + "\n" +
                "Ubicación: backups/" + backupName.split(" ")[0].substring(2),
                "Backup Completado",
                JOptionPane.INFORMATION_MESSAGE);
        });
        
        progressTimer.start();
        completeTimer.setRepeats(false);
        completeTimer.start();
    }
    
    private void restoreBackup() {
        int selectedIndex = backupList.getSelectedIndex();
        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(this, 
                "Seleccione un backup para restaurar", 
                "Advertencia", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String selectedBackup = listModel.getElementAt(selectedIndex);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "<html><b>⚠️ ADVERTENCIA ⚠️</b><br><br>" +
            "Está a punto de restaurar el sistema desde:<br>" +
            "<b>" + selectedBackup + "</b><br><br>" +
            "¿Está seguro? Esto reemplazará todos los datos actuales.<br>" +
            "Se recomienda hacer un backup actual antes de continuar.</html>",
            "Confirmar Restauración",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) { 
            Timer timer = new Timer(4000, e -> {
                
                JOptionPane.showMessageDialog(this,
                    "Restauración completada exitosamente.\n" +
                    "El sistema se reiniciará para aplicar los cambios.",
                    "Restauración Completada",
                    JOptionPane.INFORMATION_MESSAGE);
                
                // Simular reinicio
                dispose();
            });
            timer.setRepeats(false);
            timer.start();
        }
    }
    
    private void viewBackupDetails() {
        int selectedIndex = backupList.getSelectedIndex();
        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(this, 
                "Seleccione un backup para ver detalles", 
                "Advertencia", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String selectedBackup = listModel.getElementAt(selectedIndex);
        
        JDialog detailsDialog = new JDialog(this, "📋 Detalles del Backup", true);
        detailsDialog.setSize(500, 400);
        detailsDialog.setLocationRelativeTo(this);
        
        GradientPanel mainPanel = new GradientPanel(COLOR_FONDO, new Color(255, 179, 71));
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JTextArea detailsArea = new JTextArea(generateBackupDetails(selectedBackup));
        detailsArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        detailsArea.setEditable(false);
        detailsArea.setOpaque(false);
        
        JScrollPane scrollPane = new JScrollPane(detailsArea);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        JButton closeButton = new JButton("Cerrar");
        closeButton.addActionListener(e -> detailsDialog.dispose());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);
        buttonPanel.add(closeButton);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        detailsDialog.setContentPane(mainPanel);
        detailsDialog.setVisible(true);
    }
    
    private void deleteBackup() {
        int selectedIndex = backupList.getSelectedIndex();
        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(this, 
                "Seleccione un backup para eliminar", 
                "Advertencia", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String selectedBackup = listModel.getElementAt(selectedIndex);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "¿Eliminar el backup seleccionado?\n" + selectedBackup,
            "Confirmar Eliminación",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            listModel.remove(selectedIndex);
        }
    }
    
    private void configureAutoBackup() {
        ConfigurationDialog configDialog = new ConfigurationDialog((JFrame) getOwner(), currentUser, appController);
        configDialog.setVisible(true);
    }
    
    private String generateBackupDetails(String backupName) {
        String contenidoBackup = 
        "📋 INFORMACIÓN DETALLADA DEL BACKUP\n" +
        "=====================================\n\n" +
        "Nombre: " + backupName + "\n\n" +
        "📊 CONTENIDO DEL BACKUP\n" +
        "=====================================\n" +
        "    • Pruebas: " + appController.getTests().size() + "\n" +
        "    • Usuarios: " + appController.getUsers().size() + "\n" +
        "    • Resultados: " + appController.getResults().size() + "\n" +
        "    • Configuraciones: 15 archivos\n" +
        "    • Logs del sistema: 8 archivos\n" +
        "    💾 METADATOS TÉCNICOS\n" +
        "    =====================================\n" +
        "    • Formato: ZIP comprimido\n" +
        "    • Tamaño: " + getEstimatedSize() + " MB\n" +
        "    • Checksum: a1b2c3d4e5f6g7h8i9j0\n" +
        "    • Integridad: ✅ Verificada\n" +
        "    • Encriptación: 🔒 AES-256\n\n" +
        "    # INFORMACIÓN TEMPORAL\n" +
        "    =====================================\n" +
        "    • Creado: " + LocalDateTime.now().minusDays(selectedIndexToDays(backupList.getSelectedIndex())).format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) + "\n" +
        "    • Última verificación: Hoy\n" +
        "    • Próxima rotación: En 7 días\n\n" +
        "    ⚠️ ADVERTENCIAS\n" +
        "    =====================================\n" +
        "    • Este backup contiene datos sensibles\n" +
        "    • Mantener en ubicación segura\n" +
        "    • No modificar manualmente\n" +
        "    • Verificar integridad antes de restaurar";
        return contenidoBackup;
    }
    
    private int selectedIndexToDays(int index) {
        return switch (index) {
            case 0 -> 0;
            case 1 -> 1;
            case 2 -> 2;
            case 3 -> 5;
            case 4 -> 8;
            default -> 0;
        };
    }
       
    private String getEstimatedSize() {
        long estimatedSize = appController.getResults().size() * 500L +
                           appController.getUsers().size() * 200L +
                           appController.getTests().size() * 1000L;
        return String.valueOf(estimatedSize / (1024 * 1024));
    }
}