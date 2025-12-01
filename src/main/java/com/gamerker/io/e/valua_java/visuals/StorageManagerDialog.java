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
import com.gamerker.io.e.valua_java.utils.*;
import com.gamerker.io.e.valua_java.mainClasses.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Diálogo para gestionar el espacio de almacenamiento de resultados.
 * Permite archivar resultados activos para liberar espacio.
 * Los archivados no se muestran en "Mis Resultados" pero permanecen en rankings.
 */
public class StorageManagerDialog extends JDialog {
    private final User currentUser;
    private final AppController appController;
    
    private JTable resultsTable;
    private DefaultTableModel tableModel;
    private JLabel statusLabel, storageLabel;
    private JButton archiveButton, selectAllButton, deselectAllButton;
    
    // Colores
    private final Color COLOR_FONDO = new Color(255, 218, 185);
    private final Color COLOR_BOTON = new Color(255, 140, 0);
    private final Color COLOR_EXITO = new Color(40, 167, 69);
    private final Color COLOR_ERROR = new Color(220, 53, 69);
    private final Color COLOR_WARNING = new Color(255, 193, 7);
    
    public StorageManagerDialog(JFrame owner, User user, AppController controller) {
        super(owner, "🗄️ Gestión de Almacenamiento", true);
        this.currentUser = user;
        this.appController = controller;
        
        setSize(900, 600);
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        initComponents();
        loadActiveResults();
    }
    
    private void initComponents() {
        GradientPanel mainPanel = new GradientPanel(COLOR_FONDO, new Color(255, 179, 71));
        mainPanel.setLayout(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        mainPanel.add(createTablePanel(), BorderLayout.CENTER);
        mainPanel.add(createButtonsPanel(), BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 1, 10, 10));
        panel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("Gestión de Espacio de Almacenamiento");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        panel.add(titleLabel);
        
        storageLabel = new JLabel();
        storageLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        storageLabel.setForeground(Color.YELLOW);
        panel.add(storageLabel);
        
        JLabel legendLabel = new JLabel("  📌 Los archivados no aparecen en 'Mis Resultados' pero sí en rankings");
        legendLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        legendLabel.setForeground(Color.BLACK);
        panel.add(legendLabel);
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.WHITE, 1),
            "Resultados Activos (click para seleccionar)",
            0, 0,
            new Font("Arial", Font.BOLD, 12),
            Color.WHITE
        ));
        
        String[] columnas = {"", "#", "Prueba", "Puntaje", "Porcentaje", "Fecha"};
        tableModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0;
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 0 ? Boolean.class : String.class;
            }
        };
        
        resultsTable = new JTable(tableModel);
        resultsTable.setFont(new Font("Arial", Font.PLAIN, 12));
        resultsTable.setRowHeight(25);
        resultsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        resultsTable.getColumnModel().getColumn(0).setMaxWidth(30);
        resultsTable.getColumnModel().getColumn(1).setMaxWidth(50);
        
        JScrollPane scroll = new JScrollPane(resultsTable);
        scroll.setPreferredSize(new Dimension(800, 350));
        
        panel.add(scroll, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createButtonsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panel.setOpaque(false);
        
        selectAllButton = new JButton("✓ Seleccionar Todo");
        selectAllButton.setBackground(COLOR_BOTON);
        selectAllButton.setForeground(Color.WHITE);
        selectAllButton.addActionListener(e -> selectAll(true));
        panel.add(selectAllButton);
        
        deselectAllButton = new JButton("✗ Deseleccionar");
        deselectAllButton.setBackground(COLOR_BOTON);
        deselectAllButton.setForeground(Color.WHITE);
        deselectAllButton.addActionListener(e -> selectAll(false));
        panel.add(deselectAllButton);
        
        archiveButton = new JButton("📦 Archivar Seleccionados");
        archiveButton.setBackground(COLOR_WARNING);
        archiveButton.setForeground(Color.BLACK);
        archiveButton.setEnabled(false);
        archiveButton.addActionListener(e -> archiveSelected());
        panel.add(archiveButton);
        
        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        statusLabel.setForeground(Color.WHITE);
        panel.add(statusLabel);
        
        return panel;
    }
    
    private void loadActiveResults() {
        List<Result> activeResults = appController.getResults().stream()
            .filter(r -> r.getStudentUsername().equals(currentUser.getUsername()))
            .filter(r -> !r.isArchived())
            .sorted((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()))
            .collect(Collectors.toList());
        
        tableModel.setRowCount(0);
        
        for (int i = 0; i < activeResults.size(); i++) {
            Result r = activeResults.get(i);
            tableModel.addRow(new Object[]{
                false,
                i + 1,
                r.getTestTitle(),
                String.format("%d/%d", r.getScore(), r.getTotal()),
                String.format("%.2f%%", r.getPercentage()),
                r.getTimestamp().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
            });
        }
        
        int activeCount = activeResults.size();
        int maxResults = 5;
        int usedMB = activeCount * 5;
        int totalMB = maxResults * 5;
        boolean isFull = activeCount >= maxResults;
        
        storageLabel.setText(String.format(
            "<html>Resultados activos: <b>%d/%d</b> | Espacio usado: <b>%d/%d MB</b> | Estado: %s</html>",
            activeCount, maxResults, usedMB, totalMB,
            isFull ? "<font color='red'>LLENO - Debes archivar resultados</font>" : "<font color='green'>OK</font>"
        ));
        
        archiveButton.setEnabled(activeCount > 0);
    }
    
    private void selectAll(boolean select) {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            tableModel.setValueAt(select, i, 0);
        }
    }
    
    private void archiveSelected() {
        List<Integer> indicesToArchive = new ArrayList<>();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            Boolean selected = (Boolean) tableModel.getValueAt(i, 0);
            if (selected != null && selected) {
                indicesToArchive.add(i);
            }
        }
        
        if (indicesToArchive.isEmpty()) {
            showError("No has seleccionado ningún resultado para archivar");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            String.format("¿Archivar %d resultado(s)?\n\nLos archivados ya no aparecerán en 'Mis Resultados' pero seguirán en estadísticas.", 
                indicesToArchive.size()),
            "Confirmar Archivado",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm != JOptionPane.YES_OPTION) return;
        
        List<Result> userActiveResults = appController.getResults().stream()
            .filter(r -> r.getStudentUsername().equals(currentUser.getUsername()))
            .filter(r -> !r.isArchived())
            .sorted((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()))
            .collect(Collectors.toList());
        
        for (int idx : indicesToArchive) {
            if (idx < userActiveResults.size()) {
                Result r = userActiveResults.get(idx);
                r.setArchived(true);
            }
        }
        
        appController.saveAll();
        showSuccess(String.format("%d resultado(s) archivados", indicesToArchive.size()));
        loadActiveResults();
    }
    
    private void showSuccess(String message) {
        statusLabel.setText("✅ " + message);
        statusLabel.setForeground(COLOR_EXITO);
        new Timer(3000, e -> statusLabel.setText(" ")).start();
    }
    
    private void showError(String message) {
        statusLabel.setText("❌ " + message);
        statusLabel.setForeground(COLOR_ERROR);
        new Timer(5000, e -> statusLabel.setText(" ")).start();
    }
}
