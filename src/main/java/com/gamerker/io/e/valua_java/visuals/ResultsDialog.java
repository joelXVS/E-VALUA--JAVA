/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gamerker.io.e.valua_java.visuals;
/**
 *
 * @author hp
 */
import com.gamerker.io.e.valua_java.controllersPack.*;
import com.gamerker.io.e.valua_java.mainClasses.*;
import com.gamerker.io.e.valua_java.utils.GradientPanel;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Diálogo completo para ver, filtrar y gestionar resultados del usuario actual.
 * Sustituye la vista básica de JOptionPane en UserDashboard con funcionalidad completa.
 */
public class ResultsDialog extends JDialog {
    private final User currentUser;
    private final AppController appController;
    private final ResultPdfController pdfController;
    
    private JTable resultsTable;
    private DefaultTableModel tableModel;
    private JLabel statusLabel, countLabel;
    private JComboBox<String> testFilterCombo, dateFilterCombo;
    private JCheckBox archivedCheckBox;
    private JButton detailsButton, exportButton, archiveButton, refreshButton;
    
    // Colores del tema
    private final Color COLOR_FONDO = new Color(255, 218, 185);
    private final Color COLOR_BOTON = new Color(255, 140, 0);
    private final Color COLOR_BOTON_HOVER = new Color(255, 165, 0);
    private final Color COLOR_EXITO = new Color(40, 167, 69);
    private final Color COLOR_ERROR = new Color(220, 53, 69);
    private final Color COLOR_ARCHIVADO = new Color(108, 117, 125);
    
    public ResultsDialog(JFrame owner, User user, AppController controller) {
        super(owner, "📊 Mis Resultados y Calificaciones", true);
        this.currentUser = user;
        this.appController = controller;
        this.pdfController = new ResultPdfController();
        
        setSize(1100, 700);
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        initComponents();
        loadResults();
    }
    
    private void initComponents() {
        // Panel principal con gradiente
        GradientPanel mainPanel = new GradientPanel(COLOR_FONDO, new Color(255, 179, 71));
        mainPanel.setLayout(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Panel superior: filtros y controles
        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        
        // Panel central: tabla de resultados
        mainPanel.add(createTablePanel(), BorderLayout.CENTER);
        
        // Panel inferior: botones de acción
        mainPanel.add(createButtonsPanel(), BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.WHITE, 1),
            "Filtros y Herramientas",
            0, 0,
            new Font("Verdana", Font.BOLD, 12),
            Color.WHITE));
        
        // Filtro por prueba
        panel.add(new JLabel("Prueba:"));
        testFilterCombo = new JComboBox<>();
        testFilterCombo.setPreferredSize(new Dimension(200, 25));
        testFilterCombo.addActionListener(e -> loadResults());
        panel.add(testFilterCombo);
        
        // Filtro por fecha
        panel.add(new JLabel("Periodo:"));
        dateFilterCombo = new JComboBox<>(new String[]{"Todas", "Hoy", "Esta semana", "Este mes", "Últimos 3 meses"});
        dateFilterCombo.setPreferredSize(new Dimension(120, 25));
        dateFilterCombo.addActionListener(e -> loadResults());
        panel.add(dateFilterCombo);
        
        // Checkbox para incluir archivados
        archivedCheckBox = new JCheckBox("Incluir archivados");
        archivedCheckBox.setOpaque(false);
        archivedCheckBox.setForeground(Color.BLACK);
        archivedCheckBox.addActionListener(e -> loadResults());
        panel.add(archivedCheckBox);
        
        // Contador de resultados
        countLabel = new JLabel("Resultados: 0");
        countLabel.setForeground(Color.YELLOW);
        panel.add(countLabel);
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        
        // Modelo de tabla
        String[] columnas = {"ID", "Fecha", "Prueba", "Puntaje", "%", "Estado", "Tiempo", "Archivado"};
        tableModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        resultsTable = new JTable(tableModel);
        resultsTable.setFont(new Font("Verdana", Font.PLAIN, 11));
        resultsTable.setRowHeight(25);
        resultsTable.getTableHeader().setFont(new Font("Verdana", Font.BOLD, 12));
        resultsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        resultsTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        
        // Renderizador personalizado
        resultsTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    // Colorear según aprobación
                    String percentageStr = table.getValueAt(row, 4).toString().replace("%", "");
                    double percentage = Double.parseDouble(percentageStr);
                    
                    if (percentage >= 70) {
                        c.setBackground(new Color(144, 238, 144)); // Verde claro
                    } else if (percentage >= 60) {
                        c.setBackground(new Color(255, 255, 153)); // Amarillo claro
                    } else {
                        c.setBackground(new Color(255, 204, 204)); // Rojo claro
                    }
                    
                    // Marcar archivados
                    boolean isArchived = (Boolean) table.getValueAt(row, 7);
                    if (isArchived) {
                        c.setForeground(COLOR_ARCHIVADO);
                        c.setFont(c.getFont().deriveFont(Font.ITALIC));
                    }
                }
                
                // Alinear columnas numéricas
                if (column == 3 || column == 4) {
                    ((JLabel)c).setHorizontalAlignment(SwingConstants.CENTER);
                }
                
                return c;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(resultsTable);
        scrollPane.setPreferredSize(new Dimension(1050, 450));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createButtonsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panel.setOpaque(false);
        
        // Ver Detalles
        detailsButton = createButton("👁️ Ver Detalles", e -> viewResultDetails(), new Color(23, 162, 184));
        panel.add(detailsButton);
        
        // Exportar a PDF
        exportButton = createButton("📄 Exportar PDF", e -> exportToPDF(), COLOR_BOTON);
        panel.add(exportButton);
        
        // Archivar/Desarchivar
        archiveButton = createButton("🗄️ Archivar", e -> toggleArchive(), new Color(108, 117, 125));
        panel.add(archiveButton);
        
        // Refrescar
        refreshButton = createButton("🔄 Refrescar", e -> loadResults(), COLOR_BOTON);
        panel.add(refreshButton);
        
        // Status
        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Verdana", Font.ITALIC, 12));
        statusLabel.setForeground(Color.BLACK);
        panel.add(statusLabel);
        
        return panel;
    }
    
    private JButton createButton(String text, java.awt.event.ActionListener listener, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Verdana", Font.BOLD, 12));
        button.setBackground(bgColor);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(150, 30));
        button.addActionListener(listener);
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(COLOR_BOTON_HOVER);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }
    
    // ==================== LÓGICA DE CARGA ====================
    
    private void loadResults() {
        // Cargar pruebas para el filtro
        loadTestFilter();
        
        // Obtener resultados del usuario actual
        List<Result> results = appController.getResults().stream()
            .filter(r -> r.getStudentUsername().equals(currentUser.getUsername()))
            .filter(r -> archivedCheckBox.isSelected() || !r.isArchived())
            .filter(r -> filterByTest(r))
            .filter(r -> filterByDate(r))
            .sorted((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()))
            .collect(Collectors.toList());
        
        // Limpiar tabla
        tableModel.setRowCount(0);
        
        // Cargar datos
        for (Result result : results) {
            tableModel.addRow(new Object[]{
                result,
                result.getTimestamp().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                result.getTestTitle(),
                String.format("%d/%d", result.getScore(), result.getTotal()),
                String.format("%.2f%%", result.getPercentage()),
                result.getPercentage() >= 70 ? "APROBADO" : 
                result.getPercentage() >= 60 ? "MEDIO" : "NO APROBADO",
                result.getTotal() * 2 + " min",
                result.isArchived()
            });
        }
        
        countLabel.setText("Resultados: " + results.size());
        updateButtonStates();
    }
    
    private void loadTestFilter() {
        // Solo cargar si está vacío
        if (testFilterCombo.getItemCount() == 0) {
            Set<String> testTitles = appController.getResults().stream()
                .filter(r -> r.getStudentUsername().equals(currentUser.getUsername()))
                .map(Result::getTestTitle)
                .collect(Collectors.toSet());
            
            testFilterCombo.addItem("Todas");
            for (String title : testTitles) {
                testFilterCombo.addItem(title);
            }
        }
    }
    
    private boolean filterByTest(Result result) {
        String selectedTest = (String) testFilterCombo.getSelectedItem();
        return "Todas".equals(selectedTest) || result.getTestTitle().equals(selectedTest);
    }
    
    private boolean filterByDate(Result result) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime resultDate = result.getTimestamp();
        
        return switch (dateFilterCombo.getSelectedIndex()) {
            case 0 -> true; // Todas
            case 1 -> resultDate.toLocalDate().equals(now.toLocalDate()); // Hoy
            case 2 -> resultDate.isAfter(now.minusDays(7)); // Esta semana
            case 3 -> resultDate.isAfter(now.minusDays(30)); // Este mes
            case 4 -> resultDate.isAfter(now.minusDays(90)); // Últimos 3 meses
            default -> true;
        };
    }
    
    private void updateButtonStates() {
        boolean hasSelection = resultsTable.getSelectedRow() != -1;
        detailsButton.setEnabled(hasSelection);
        exportButton.setEnabled(hasSelection);
        archiveButton.setEnabled(hasSelection);
        
        if (hasSelection) {
            int row = resultsTable.getSelectedRow();
            boolean isArchived = (Boolean) tableModel.getValueAt(row, 7);
            archiveButton.setText(isArchived ? "📤 Desarchivar" : "🗄️ Archivar");
        }
    }
    
    // ==================== ACCIONES ====================
    
    private void viewResultDetails() {
        int selectedRow = resultsTable.getSelectedRow();
        if (selectedRow == -1) return;
        
        Result result = (Result) tableModel.getValueAt(selectedRow, 0);
        
        // Construir vista detallada
        StringBuilder details = new StringBuilder();
        details.append("<html><h2>Detalle de Resultado</h2>");
        details.append("<table style='font-size:12px;'>");
        details.append("<tr><td><b>Prueba:</b></td><td>").append(result.getTestTitle()).append("</td></tr>");
        details.append("<tr><td><b>Fecha:</b></td><td>").append(result.getTimestamp().format(
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))).append("</td></tr>");
        details.append("<tr><td><b>Puntaje:</b></td><td>").append(
            String.format("%d/%d (%.2f%%)", result.getScore(), result.getTotal(), result.getPercentage())).append("</td></tr>");
        details.append("<tr><td><b>Estado:</b></td><td><font color='%s'>%s</font></td></tr>".formatted(
            result.getPercentage() >= 70 ? "green" : result.getPercentage() >= 60 ? "orange" : "red",
            result.getPercentage() >= 70 ? "APROBADO" : result.getPercentage() >= 60 ? "MEDIO" : "NO APROBADO"));
        details.append("</table>");
        
        // Respuestas
        details.append("<h3>Respuestas:</h3><table style='font-size:11px;'>");
        details.append("<tr><th>#</th><th>Pregunta</th><th>Tu Respuesta</th><th>Correcta</th><th>Resultado</th></tr>");
        
        Test test = findTestByTitle(result.getTestTitle());
        if (test != null) {
            for (int i = 0; i < test.getQuestions().size(); i++) {
                Question q = test.getQuestions().get(i);
                String userAnswer = result.getAnswers().get(i);
                boolean isCorrect = q.verifyAnswer(userAnswer);
                
                details.append("<tr><td>").append(i + 1).append("</td>");
                details.append("<td>").append(truncate(q.getQuestionText(), 30)).append("</td>");
                details.append("<td>").append(userAnswer.isEmpty() ? "❌ Sin respuesta" : userAnswer).append("</td>");
                details.append("<td>").append(q.getCorrectAnswer()).append(") ").append(q.getCorrectOptionText()).append("</td>");
                details.append("<td>").append(isCorrect ? "✅" : "❌").append("</td></tr>");
            }
        }
        details.append("</table></html>");
        
        JOptionPane.showMessageDialog(this, new JLabel(details.toString()), 
            "Detalles del Resultado", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void exportToPDF() {
        int selectedRow = resultsTable.getSelectedRow();
        if (selectedRow == -1) return;
        
        Result result = (Result) tableModel.getValueAt(selectedRow, 0);
        
        try {
            // Cobrar si no ha sido exportado antes (simplificado)
            String path = pdfController.exportResultToPdf(result, currentUser);
            if (path != null) {
                showSuccess("PDF exportado: " + path);
                JOptionPane.showMessageDialog(this, 
                    "PDF generado exitosamente.", "Exportación", JOptionPane.INFORMATION_MESSAGE);
            } else {
                showError("Error al exportar PDF");
            }
        } catch (Exception e) {
            showError("Error: " + e.getMessage());
        }
    }
    
    private void toggleArchive() {
        int selectedRow = resultsTable.getSelectedRow();
        if (selectedRow == -1) return;
        
        Result result = (Result) tableModel.getValueAt(selectedRow, 0);
        result.setArchived(!result.isArchived());
        
        appController.saveAll();
        loadResults();
        
        showSuccess(result.isArchived() ? "Resultado archivado" : "Resultado desarchivado");
    }
    
    private Test findTestByTitle(String title) {
        return appController.getTests().stream()
            .filter(t -> t.getTitle().equals(title))
            .findFirst()
            .orElse(null);
    }
    
    private String truncate(String text, int length) {
        return text.length() > length ? text.substring(0, length - 3) + "..." : text;
    }
    
    private void showSuccess(String message) {
        statusLabel.setText("✅ " + message);
        statusLabel.setForeground(new Color(40, 167, 69));
        
        Timer timer = new Timer(3000, e -> statusLabel.setText(" "));
        timer.setRepeats(false);
        timer.start();
    }
    
    private void showError(String message) {
        statusLabel.setText("❌ " + message);
        statusLabel.setForeground(COLOR_ERROR);
        
        Timer timer = new Timer(5000, e -> statusLabel.setText(" "));
        timer.setRepeats(false);
        timer.start();
    }
}
