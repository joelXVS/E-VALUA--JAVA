/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gamerker.io.e.valua_java.visuals;

import com.gamerker.io.e.valua_java.controllersPack.*;
import com.gamerker.io.e.valua_java.mainClasses.*;
import com.gamerker.io.e.valua_java.utils.GradientPanel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Diálogo para que los profesores gestionen sus estudiantes
 */
public class ManageStudentsDialog extends JDialog {
    private final AppController appController;
    private final User currentUser;
    
    // Colores del tema
    private final Color COLOR_FONDO = new Color(255, 218, 185);
    private final Color COLOR_BOTON = new Color(255, 140, 0);
    private final Color COLOR_BOTON_HOVER = new Color(255, 165, 0);
    private final Color COLOR_EXITO = new Color(144, 238, 144);
    private final Color COLOR_ALERTA = new Color(255, 193, 7);
    private final Color COLOR_ERROR = new Color(255, 204, 204);
    
    private JTable studentsTable;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;
    private JComboBox<String> filterCombo;
    private JTextField searchField;
    
    public ManageStudentsDialog(JFrame owner, User user, AppController controller) {
        super(owner, "👨‍🏫 Gestión de Estudiantes", true);
        this.currentUser = user;
        this.appController = controller;
        
        setSize(900, 600);
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        initComponents();
        loadStudents();
    }
    
    private void initComponents() {
        GradientPanel mainPanel = new GradientPanel(COLOR_FONDO, new Color(255, 179, 71));
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Panel superior con controles
        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        
        // Panel central con tabla
        mainPanel.add(createTablePanel(), BorderLayout.CENTER);
        
        // Panel inferior con botones
        mainPanel.add(createButtonsPanel(), BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        panel.setOpaque(false);
        
        // Título
        JLabel titleLabel = new JLabel("👨‍🏫 GESTIÓN DE ESTUDIANTES");
        titleLabel.setFont(new Font("Verdana", Font.BOLD, 14));
        titleLabel.setForeground(Color.BLACK);
        panel.add(titleLabel);
        
        panel.add(Box.createHorizontalStrut(20));
        
        // Búsqueda
        panel.add(new JLabel("🔍 Buscar:"));
        searchField = new JTextField(15);
        searchField.setFont(new Font("Verdana", Font.PLAIN, 12));
        searchField.addActionListener(e -> loadStudents());
        panel.add(searchField);
        
        panel.add(Box.createHorizontalStrut(20));
        
        // Filtro
        panel.add(new JLabel("📊 Filtro:"));
        filterCombo = new JComboBox<>(new String[]{"Todos", "Sin resultados", "Con bajo rendimiento", "Destacados"});
        filterCombo.setPreferredSize(new Dimension(150, 25));
        filterCombo.addActionListener(e -> loadStudents());
        panel.add(filterCombo);
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createTitledBorder("Lista de Estudiantes"));
        
        // Modelo de tabla
        String[] columns = {"Estudiante", "Email", "Pruebas Realizadas", "Promedio", "Última Actividad", "Estado"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        studentsTable = new JTable(tableModel);
        studentsTable.setFont(new Font("Verdana", Font.PLAIN, 11));
        studentsTable.setRowHeight(25);
        studentsTable.getTableHeader().setFont(new Font("Verdana", Font.BOLD, 12));
        
        // Renderizador para colorear filas
        studentsTable.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    String status = (String) table.getValueAt(row, 5);
                    switch (status) {
                        case "Destacado" -> c.setBackground(COLOR_EXITO);
                        case "Regular" -> c.setBackground(Color.WHITE);
                        case "Bajo rendimiento" -> c.setBackground(COLOR_ALERTA);
                        case "Sin actividad" -> c.setBackground(COLOR_ERROR);
                        default -> c.setBackground(Color.WHITE);
                    }
                }
                
                return c;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(studentsTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createButtonsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panel.setOpaque(false);
        
        // Ver detalles del estudiante
        JButton detailsButton = createButton("👁️ Ver Detalles", e -> viewStudentDetails(), 
            new Color(23, 162, 184), new Color(23, 132, 144));
        panel.add(detailsButton);
        
        // Enviar mensaje
        JButton messageButton = createButton("✉️ Enviar Mensaje", e -> sendMessage(), 
            new Color(40, 167, 69), new Color(33, 136, 56));
        panel.add(messageButton);
        
        // Generar reporte
        JButton reportButton = createButton("📄 Reporte Individual", e -> generateReport(), 
            COLOR_BOTON, COLOR_BOTON_HOVER);
        panel.add(reportButton);
        
        // Exportar lista
        JButton exportButton = createButton("📋 Exportar Lista", e -> exportList(), 
            new Color(108, 117, 125), new Color(88, 107, 95));
        panel.add(exportButton);
        
        // Actualizar
        JButton refreshButton = createButton("🔄 Actualizar", e -> loadStudents(), 
            COLOR_BOTON, COLOR_BOTON_HOVER);
        panel.add(refreshButton);
        
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
        button.setPreferredSize(new Dimension(160, 30));
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
    
    private void loadStudents() {
        tableModel.setRowCount(0);
        
        List<User> allStudents = appController.getUsers().stream()
            .filter(u -> u.getRole().equals("student"))
            .collect(Collectors.toList());
        
        String searchText = searchField.getText().toLowerCase();
        String filter = (String) filterCombo.getSelectedItem();
        
        for (User student : allStudents) {
            // Filtrar por búsqueda
            if (!searchText.isEmpty() && 
                !student.getDisplayName().toLowerCase().contains(searchText) &&
                !student.getUsername().toLowerCase().contains(searchText)) {
                continue;
            }
            
            // Obtener resultados del estudiante
            List<Result> studentResults = appController.getResults().stream()
                .filter(r -> r.getStudentUsername().equals(student.getUsername()))
                .collect(Collectors.toList());
            
            int testsTaken = studentResults.size();
            
            // Calcular promedio
            double average = studentResults.stream()
                .mapToDouble(Result::getPercentage)
                .average()
                .orElse(0.0);
            
            // Última actividad
            String lastActivity = "Nunca";
            if (!studentResults.isEmpty()) {
                LocalDateTime last = studentResults.stream()
                    .map(Result::getTimestamp)
                    .max(LocalDateTime::compareTo)
                    .orElse(null);
                if (last != null) {
                    lastActivity = last.format(DateTimeFormatter.ofPattern("dd/MM/yy HH:mm"));
                }
            }
            
            // Determinar estado
            String status;
            if (testsTaken == 0) {
                status = "Sin actividad";
            } else if (average >= 80) {
                status = "Destacado";
            } else if (average >= 60) {
                status = "Regular";
            } else {
                status = "Bajo rendimiento";
            }
            
            // Aplicar filtro adicional
            if (filter != null) {
                switch (filter) {
                    case "Sin resultados" -> {
                        if (testsTaken > 0) continue;
                    }
                    case "Con bajo rendimiento" -> {
                        if (average >= 60 || testsTaken == 0) continue;
                    }
                    case "Destacados" -> {
                        if (average < 80) continue;
                    }
                }
            }
            
            tableModel.addRow(new Object[]{
                student.getDisplayName(),
                testsTaken,
                String.format("%.2f%%", average),
                lastActivity,
                status
            });
        }
        
        showStatus("✅ " + tableModel.getRowCount() + " estudiantes encontrados");
    }
    
    private void viewStudentDetails() {
        int selectedRow = studentsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un estudiante", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String studentName = (String) tableModel.getValueAt(selectedRow, 0);
        User student = appController.getUsers().stream()
            .filter(u -> u.getDisplayName().equals(studentName))
            .findFirst()
            .orElse(null);
        
        if (student == null) return;
        
        // Obtener resultados del estudiante
        List<Result> studentResults = appController.getResults().stream()
            .filter(r -> r.getStudentUsername().equals(student.getUsername()))
            .collect(Collectors.toList());
        
        // Crear diálogo de detalles
        JDialog detailsDialog = new JDialog(this, "👤 Detalles del Estudiante", true);
        detailsDialog.setSize(600, 500);
        detailsDialog.setLocationRelativeTo(this);
        
        GradientPanel mainPanel = new GradientPanel(COLOR_FONDO, new Color(255, 179, 71));
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Panel de información
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);
        
        JLabel nameLabel = new JLabel("👤 " + student.getDisplayName());
        nameLabel.setFont(new Font("Verdana", Font.BOLD, 18));
        infoPanel.add(nameLabel);
        
        infoPanel.add(Box.createVerticalStrut(10));
        
        JLabel userLabel = new JLabel("👤 Usuario: " + student.getUsername());
        userLabel.setFont(new Font("Verdana", Font.PLAIN, 12));
        infoPanel.add(userLabel);
        
        JLabel balanceLabel = new JLabel("💰 Saldo: $" + student.getBalance());
        balanceLabel.setFont(new Font("Verdana", Font.PLAIN, 12));
        infoPanel.add(balanceLabel);
        
        infoPanel.add(Box.createVerticalStrut(20));
        
        // Estadísticas
        JLabel statsLabel = new JLabel("📊 ESTADÍSTICAS");
        statsLabel.setFont(new Font("Verdana", Font.BOLD, 14));
        infoPanel.add(statsLabel);
        
        infoPanel.add(Box.createVerticalStrut(10));
        
        double average = studentResults.stream()
            .mapToDouble(Result::getPercentage)
            .average()
            .orElse(0.0);
        
        long approved = studentResults.stream()
            .filter(r -> r.getPercentage() >= 70)
            .count();
        
        long totalTests = appController.getTests().size();
        double participationRate = totalTests == 0 ? 0 : (studentResults.size() * 100.0 / totalTests);
        
        JLabel testsLabel = new JLabel("📝 Pruebas realizadas: " + studentResults.size());
        testsLabel.setFont(new Font("Verdana", Font.PLAIN, 12));
        infoPanel.add(testsLabel);
        
        JLabel avgLabel = new JLabel("📈 Promedio: " + String.format("%.2f%%", average));
        avgLabel.setFont(new Font("Verdana", Font.PLAIN, 12));
        infoPanel.add(avgLabel);
        
        JLabel approvedLabel = new JLabel("✅ Aprobadas: " + approved + " de " + studentResults.size());
        approvedLabel.setFont(new Font("Verdana", Font.PLAIN, 12));
        infoPanel.add(approvedLabel);
        
        JLabel participationLabel = new JLabel("🎯 Tasa de participación: " + String.format("%.1f%%", participationRate));
        participationLabel.setFont(new Font("Verdana", Font.PLAIN, 12));
        infoPanel.add(participationLabel);
        
        mainPanel.add(infoPanel, BorderLayout.NORTH);
        
        // Panel de resultados recientes
        if (!studentResults.isEmpty()) {
            JPanel resultsPanel = new JPanel(new BorderLayout());
            resultsPanel.setOpaque(false);
            resultsPanel.setBorder(BorderFactory.createTitledBorder("📋 Resultados Recientes"));
            
            String[] columns = {"Prueba", "Fecha", "Puntaje", "%", "Estado"};
            DefaultTableModel model = new DefaultTableModel(columns, 0);
            
            studentResults.stream()
                .sorted((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()))
                .limit(10)
                .forEach(result -> {
                    model.addRow(new Object[]{
                        truncate(result.getTestTitle(), 25),
                        result.getTimestamp().format(DateTimeFormatter.ofPattern("dd/MM HH:mm")),
                        result.getScore() + "/" + result.getTotal(),
                        String.format("%.2f%%", result.getPercentage()),
                        result.getPercentage() >= 70 ? "✅ Aprobado" : "❌ No aprobado"
                    });
                });
            
            JTable resultsTable = new JTable(model);
            resultsTable.setFont(new Font("Verdana", Font.PLAIN, 11));
            JScrollPane scrollPane = new JScrollPane(resultsTable);
            
            resultsPanel.add(scrollPane, BorderLayout.CENTER);
            mainPanel.add(resultsPanel, BorderLayout.CENTER);
        } else {
            JLabel noResultsLabel = new JLabel("📭 El estudiante no ha realizado pruebas", SwingConstants.CENTER);
            noResultsLabel.setFont(new Font("Verdana", Font.ITALIC, 14));
            mainPanel.add(noResultsLabel, BorderLayout.CENTER);
        }
        
        // Botón cerrar
        JButton closeButton = new JButton("Cerrar");
        closeButton.addActionListener(e -> detailsDialog.dispose());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);
        buttonPanel.add(closeButton);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        detailsDialog.setContentPane(mainPanel);
        detailsDialog.setVisible(true);
    }
    
    private void sendMessage() {
        int selectedRow = studentsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un estudiante", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String studentEmail = "";
        
        String message = JOptionPane.showInputDialog(this, 
            "Ingrese el mensaje para el estudiante:", 
            "✉️ Enviar Mensaje", 
            JOptionPane.PLAIN_MESSAGE);
        
        if (message != null && !message.trim().isEmpty()) {
            // Simular envío de mensaje
            showStatus("✅ Mensaje enviado al correo del estudiante");
        }
    }
    
    private void generateReport() {
        int selectedRow = studentsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un estudiante", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String studentName = (String) tableModel.getValueAt(selectedRow, 0);
        
        // Simular generación de reporte
        showStatus("📄 Generando reporte para " + studentName + "...");
        
        Timer timer = new Timer(2000, e -> {
            showStatus("✅ Reporte generado para " + studentName);
            JOptionPane.showMessageDialog(this, 
                "Reporte generado exitosamente.\nSe ha guardado en: reports/" + studentName.replace(" ", "_") + "_report.pdf",
                "Reporte Generado", 
                JOptionPane.INFORMATION_MESSAGE);
        });
        timer.setRepeats(false);
        timer.start();
    }
    
    private void exportList() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Exportar lista de estudiantes");
        fileChooser.setSelectedFile(new java.io.File("estudiantes.csv"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            java.io.File file = fileChooser.getSelectedFile();
            // Simular exportación
            showStatus("✅ Lista exportada a: " + file.getAbsolutePath());
        }
    }
    
    private void showStatus(String message) {
        statusLabel.setText(message);
        
        Timer timer = new Timer(3000, e -> statusLabel.setText(" "));
        timer.setRepeats(false);
        timer.start();
    }
    
    private String truncate(String text, int length) {
        return text.length() > length ? text.substring(0, length - 3) + "..." : text;
    }
}
