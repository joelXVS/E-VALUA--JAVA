/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gamerker.io.e.valua_java.visuals;

import com.gamerker.io.e.valua_java.controllersPack.*;
import com.gamerker.io.e.valua_java.mainClasses.*;
import com.gamerker.io.e.valua_java.utils.GradientPanel;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.draw.ILineDrawer;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Diálogo para generar reportes del sistema
 */
public class ReportsDialog extends JDialog {
    private final AppController appController;
    private final User currentUser;
    
    // Colores del tema
    private final Color COLOR_FONDO = new Color(255, 218, 185);
    private final Color COLOR_BOTON = new Color(255, 140, 0);
    private final Color COLOR_BOTON_HOVER = new Color(255, 165, 0);
    private final Color COLOR_EXITO = new Color(144, 238, 144);
    private final Color COLOR_ALERTA = new Color(255, 193, 7);
    
    private JLabel statusLabel;
    private JComboBox<String> reportTypeCombo;
    private JSpinner fromDateSpinner;
    private JSpinner toDateSpinner;
    private JCheckBox includeChartsCheck;
    
    public ReportsDialog(JFrame owner, User user, AppController controller) {
        super(owner, "📋 Reportes del Sistema", true);
        this.currentUser = user;
        this.appController = controller;
        
        setSize(1000, 500);
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        initComponents();
    }
    
    private void initComponents() {
        GradientPanel mainPanel = new GradientPanel(COLOR_FONDO, new Color(255, 179, 71));
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Panel superior
        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        
        // Panel central
        mainPanel.add(createConfigPanel(), BorderLayout.CENTER);
        
        // Panel inferior
        mainPanel.add(createButtonsPanel(), BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("GENERADOR DE REPORTES");
        titleLabel.setFont(new Font("Verdana", Font.BOLD, 18));
        titleLabel.setForeground(Color.BLACK);
        
        JLabel subTitleLabel = new JLabel("Genere reportes detallados del sistema");
        subTitleLabel.setFont(new Font("Verdana", Font.PLAIN, 12));
        subTitleLabel.setForeground(Color.BLACK);
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(subTitleLabel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createConfigPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Tipo de reporte
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("📄 Tipo de reporte:"), gbc);
        
        gbc.gridx = 1;
        reportTypeCombo = new JComboBox<>(new String[]{
            "Reporte de Actividad General"
        });
        reportTypeCombo.setPreferredSize(new Dimension(300, 30));
        panel.add(reportTypeCombo, gbc);
        
        // Rango de fechas
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("📅 Desde:"), gbc);
        
        gbc.gridx = 1;
        fromDateSpinner = new JSpinner(new SpinnerDateModel());
        fromDateSpinner.setEditor(new JSpinner.DateEditor(fromDateSpinner, "dd/MM/yyyy"));
        fromDateSpinner.setPreferredSize(new Dimension(150, 25));
        panel.add(fromDateSpinner, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("📅 Hasta:"), gbc);
        
        gbc.gridx = 1;
        toDateSpinner = new JSpinner(new SpinnerDateModel());
        toDateSpinner.setEditor(new JSpinner.DateEditor(toDateSpinner, "dd/MM/yyyy"));
        toDateSpinner.setPreferredSize(new Dimension(150, 25));
        panel.add(toDateSpinner, gbc);
        
        // Opciones adicionales
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("⚙️ Opciones:"), gbc);
        
        gbc.gridx = 1;
        includeChartsCheck = new JCheckBox("Incluir gráficos en el reporte");
        includeChartsCheck.setOpaque(false);
        panel.add(includeChartsCheck, gbc);
        
        // Panel de vista previa
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(20, 10, 10, 10);
        
        JPanel previewPanel = new JPanel(new BorderLayout());
        previewPanel.setOpaque(false);
        previewPanel.setBorder(BorderFactory.createTitledBorder("👁️ Vista Previa del Reporte"));
        
        JTextArea previewArea = new JTextArea(generatePreview());
        previewArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        previewArea.setEditable(false);
        previewArea.setOpaque(false);
        
        JScrollPane scrollPane = new JScrollPane(previewArea);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        
        previewPanel.add(scrollPane, BorderLayout.CENTER);
        panel.add(previewPanel, gbc);
        
        return panel;
    }
    
    private JPanel createButtonsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panel.setOpaque(false);
        
        // Generar reporte
        JButton generateButton = createButton("Generar Reporte", e -> generateReport(), 
            COLOR_BOTON, COLOR_BOTON_HOVER);
        panel.add(generateButton);
        
        // Vista previa en PDF
        JButton previewButton = createButton("Vista Previa PDF", e -> previewReport(), 
            new Color(23, 162, 184), new Color(23, 132, 144));
        panel.add(previewButton);
        
        // Exportar a Excel
        JButton excelButton = createButton("Exportar a Excel", e -> exportToExcel(), 
            new Color(40, 167, 69), new Color(33, 136, 56));
        panel.add(excelButton);
        
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
        button.setPreferredSize(new Dimension(180, 35));
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
    
    private String generatePreview() {
        return """
            📋 VISTA PREVIA DEL REPORTE
            ================================
            
            Tipo: Reporte de Actividad General
            Período: Últimos 30 días
            Fecha generación: """ + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) + """
            
            
            📊 RESUMEN EJECUTIVO
            • Total de pruebas realizadas: 128
            • Estudiantes activos: 45
            • Promedio de calificación: 72.5%
            • Tasa de aprobación: 68%
            • Ingresos generados: $12,800
            
            📈 TENDENCIAS
            • Aumento del 15% en actividad vs mes anterior
            • Prueba más popular: Matemáticas Básicas
            • Mejor estudiante: Juan Pérez (92% promedio)
            
            ⚠️ ÁREAS DE MEJORA
            • 12 estudiantes con rendimiento bajo (<60%)
            • 3 pruebas sin realizaciones este mes
            """;
    }
    
    private void showGeneratedReport() {
        JTextArea reportArea = new JTextArea(generateReportContent());
        reportArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        reportArea.setEditable(false);
        
        JScrollPane scrollPane = new JScrollPane(reportArea);
        scrollPane.setPreferredSize(new Dimension(700, 500));
        
        JOptionPane.showMessageDialog(this, scrollPane, "📄 Reporte Generado", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private String generateReportPdf(String reportType, String reportContent) {
        try {
            // Crear directorio si no existe
            File reportsDir = new File("reports");
            if (!reportsDir.exists()) {
                reportsDir.mkdirs();
            }

            String fileName = String.format("reports/reporte_%s_%s.pdf", 
                reportType.toLowerCase().replace(" ", "_"),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")));

            PdfWriter writer = new PdfWriter(fileName);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf, PageSize.A4);

            document.setMargins(40, 40, 40, 40);

            // Cabecera con gradiente simulada
            Paragraph header = new Paragraph("E-VALUA REPORTE DEL SISTEMA")
                    .setFontSize(24)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontColor(new DeviceRgb(255, 140, 0))
                    .setMarginTop(10)
                    .setMarginBottom(5);
            document.add(header);

            // Subtítulo
            Paragraph subHeader = new Paragraph(reportType)
                    .setFontSize(14)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontColor(new DeviceRgb(100, 100, 100))
                    .setMarginBottom(20);
            document.add(subHeader);

            // Información del reporte en tabla
            com.itextpdf.layout.element.Table infoTable = new com.itextpdf.layout.element.Table(2);
            infoTable.setWidth(UnitValue.createPercentValue(100));

            addTableCell(infoTable, "Generado por:", currentUser.getDisplayName(), true);
            addTableCell(infoTable, "Fecha:", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")), false);
            addTableCell(infoTable, "Tipo:", reportType, true);
            addTableCell(infoTable, "Archivo:", fileName.substring(fileName.lastIndexOf("/") + 1), false);

            document.add(infoTable);
            document.add(new Paragraph("\n"));

            // Línea separadora
            SolidLine solidLine = new SolidLine(1f);
            solidLine.setColor(new DeviceRgb(220, 220, 220));
            LineSeparator line = new LineSeparator(solidLine);
            document.add(line);
            document.add(new Paragraph("\n"));

            // SECCIÓN 1: RESUMEN GENERAL (con tabla)
            Paragraph section1 = new Paragraph("📊 RESUMEN GENERAL")
                    .setFontSize(16)
                    .setBold()
                    .setFontColor(new DeviceRgb(0, 102, 204))
                    .setMarginTop(20)
                    .setMarginBottom(10);
            document.add(section1);

            // Tabla de resumen
            List<Test> tests = appController.getTests();
            List<Result> results = appController.getResults();
            List<User> users = appController.getUsers();

            com.itextpdf.layout.element.Table summaryTable = new com.itextpdf.layout.element.Table(2);
            summaryTable.setWidth(UnitValue.createPercentValue(100));
            summaryTable.setMarginBottom(15);

            addStyledTableCell(summaryTable, "Total de Pruebas:", String.valueOf(tests.size()), true);
            addStyledTableCell(summaryTable, "Total de Resultados:", String.valueOf(results.size()), false);
            addStyledTableCell(summaryTable, "Total de Usuarios:", String.valueOf(users.size()), true);

            double avgScore = results.stream()
                .mapToDouble(Result::getPercentage)
                .average()
                .orElse(0.0);
            addStyledTableCell(summaryTable, "Promedio General:", String.format("%.2f%%", avgScore), false);

            long approved = results.stream()
                .filter(r -> r.getPercentage() >= 70)
                .count();
            double approvalRate = results.isEmpty() ? 0 : (approved * 100.0 / results.size());
            addStyledTableCell(summaryTable, "Tasa de Aprobación:", String.format("%.1f%%", approvalRate), true);

            // Tiempo promedio de pruebas
            double avgTime = results.stream()
                .filter(r -> r.getTimeTaken() != null)
                .mapToLong(r -> r.getTimeTaken().toMinutes())
                .average()
                .orElse(0.0);
            addStyledTableCell(summaryTable, "Tiempo Promedio/Prueba:", String.format("%.1f min", avgTime), false);

            document.add(summaryTable);

            // SECCIÓN 2: DISTRIBUCIÓN DE USUARIOS (con tabla)
            Paragraph section2 = new Paragraph("👥 DISTRIBUCIÓN DE USUARIOS")
                    .setFontSize(16)
                    .setBold()
                    .setFontColor(new DeviceRgb(0, 102, 204))
                    .setMarginTop(20)
                    .setMarginBottom(10);
            document.add(section2);

            com.itextpdf.layout.element.Table usersTable = new com.itextpdf.layout.element.Table(4);
            usersTable.setWidth(UnitValue.createPercentValue(100));
            usersTable.setMarginBottom(15);

            // Encabezado de tabla
            addTableHeaderCell(usersTable, "Rol");
            addTableHeaderCell(usersTable, "Cantidad");
            addTableHeaderCell(usersTable, "Porcentaje");
            addTableHeaderCell(usersTable, "Estado");

            long admins = users.stream().filter(u -> u.getRole().equals("admin")).count();
            long teachers = users.stream().filter(u -> u.getRole().equals("teacher")).count();
            long students = users.stream().filter(u -> u.getRole().equals("student")).count();

            // Filas de datos
            addUserRow(usersTable, "Administrador", admins, users.size(), "🟢 Activo");
            addUserRow(usersTable, "Profesor", teachers, users.size(), "🟡 Moderado");
            addUserRow(usersTable, "Estudiante", students, users.size(), "🔵 Principal");

            document.add(usersTable);

            // SECCIÓN 3: ACTIVIDAD POR PRUEBA
            Paragraph section3 = new Paragraph("📝 ACTIVIDAD POR PRUEBA")
                    .setFontSize(16)
                    .setBold()
                    .setFontColor(new DeviceRgb(0, 102, 204))
                    .setMarginTop(20)
                    .setMarginBottom(10);
            document.add(section3);

            if (tests.isEmpty()) {
                document.add(new Paragraph("No hay pruebas en el sistema.").setItalic().setFontColor(ColorConstants.GRAY));
            } else {
                com.itextpdf.layout.element.Table testsTable = new com.itextpdf.layout.element.Table(4);
                testsTable.setWidth(UnitValue.createPercentValue(100));
                testsTable.setMarginBottom(15);

                addTableHeaderCell(testsTable, "Prueba");
                addTableHeaderCell(testsTable, "Realizaciones");
                addTableHeaderCell(testsTable, "Puntuación Prom.");
                addTableHeaderCell(testsTable, "Estado");

                for (Test test : tests) {
                    long testResults = results.stream()
                        .filter(r -> r.getTestTitle().equals(test.getTitle()))
                        .count();

                    double testAvgScore = results.stream()
                        .filter(r -> r.getTestTitle().equals(test.getTitle()))
                        .mapToDouble(Result::getPercentage)
                        .average()
                        .orElse(0.0);

                    String status = testResults == 0 ? "🟡 Sin actividad" : 
                                   testAvgScore >= 70 ? "🟢 Excelente" : 
                                   testAvgScore >= 60 ? "🔵 Bueno" : "🔴 Necesita mejora";

                    addTestRow(testsTable, 
                        truncate(test.getTitle(), 25), 
                        String.valueOf(testResults),
                        String.format("%.1f%%", testAvgScore),
                        status);
                }

                document.add(testsTable);
            }

            // SECCIÓN 4: TOP 10 ESTUDIANTES
            Paragraph section4 = new Paragraph("🏆 TOP 10 ESTUDIANTES")
                    .setFontSize(16)
                    .setBold()
                    .setFontColor(new DeviceRgb(0, 102, 204))
                    .setMarginTop(20)
                    .setMarginBottom(10);
            document.add(section4);

            com.itextpdf.layout.element.Table topStudentsTable = new com.itextpdf.layout.element.Table(4);
            topStudentsTable.setWidth(UnitValue.createPercentValue(100));
            topStudentsTable.setMarginBottom(15);

            addTableHeaderCell(topStudentsTable, "#");
            addTableHeaderCell(topStudentsTable, "Estudiante");
            addTableHeaderCell(topStudentsTable, "Promedio");
            addTableHeaderCell(topStudentsTable, "Pruebas");

            List<User> topStudents = users.stream()
                .filter(u -> u.getRole().equals("student"))
                .sorted((a, b) -> {
                    double avgA = results.stream()
                        .filter(r -> r.getStudentUsername().equals(a.getUsername()))
                        .mapToDouble(Result::getPercentage)
                        .average()
                        .orElse(0.0);
                    double avgB = results.stream()
                        .filter(r -> r.getStudentUsername().equals(b.getUsername()))
                        .mapToDouble(Result::getPercentage)
                        .average()
                        .orElse(0.0);
                    return Double.compare(avgB, avgA);
                })
                .limit(10)
                .collect(Collectors.toList());

            int rank = 1;
            for (User student : topStudents) {
                double avgScoreStudent = results.stream()
                    .filter(r -> r.getStudentUsername().equals(student.getUsername()))
                    .mapToDouble(Result::getPercentage)
                    .average()
                    .orElse(0.0);
                long testsTaken = results.stream()
                    .filter(r -> r.getStudentUsername().equals(student.getUsername()))
                    .count();

                String rankSymbol = rank == 1 ? "🥇" : rank == 2 ? "🥈" : rank == 3 ? "🥉" : String.valueOf(rank);

                addTopStudentRow(topStudentsTable, 
                    rankSymbol,
                    truncate(student.getDisplayName(), 20),
                    String.format("%.1f%%", avgScoreStudent),
                    String.valueOf(testsTaken));

                rank++;
            }

            document.add(topStudentsTable);

            // SECCIÓN 5: RECOMENDACIONES
            Paragraph section5 = new Paragraph("💡 RECOMENDACIONES Y CONCLUSIONES")
                    .setFontSize(16)
                    .setBold()
                    .setFontColor(new DeviceRgb(0, 102, 204))
                    .setMarginTop(20)
                    .setMarginBottom(10);
            document.add(section5);

            com.itextpdf.layout.element.Table recommendationsTable = new com.itextpdf.layout.element.Table(2);
            recommendationsTable.setWidth(UnitValue.createPercentValue(100));

            addRecommendationRow(recommendationsTable, "1.", "Crear más pruebas de áreas con menor actividad", "🟢 Prioridad Alta");
            addRecommendationRow(recommendationsTable, "2.", "Implementar sistema de recompensas para estudiantes destacados", "🟡 Prioridad Media");
            addRecommendationRow(recommendationsTable, "3.", "Realizar seguimiento a estudiantes con bajo rendimiento (<60%)", "🔴 Prioridad Crítica");
            addRecommendationRow(recommendationsTable, "4.", "Expandir catálogo de pruebas temáticas", "🟡 Prioridad Media");
            addRecommendationRow(recommendationsTable, "5.", "Mejorar interfaz de reportes para profesores", "🔵 Prioridad Baja");

            document.add(recommendationsTable);

            // Pie de página
            document.add(new Paragraph("\n\n"));
            SolidLine footerLine = new SolidLine(0.5f);
            footerLine.setColor(new DeviceRgb(200, 200, 200));
            document.add(new LineSeparator(footerLine));

            Paragraph footer = new Paragraph(String.format(
                "© %d - Sistema E-VALUA | Reporte generado automáticamente | Página %d",
                LocalDateTime.now().getYear(),
                pdf.getNumberOfPages()
            ))
            .setFontSize(8)
            .setItalic()
            .setTextAlignment(TextAlignment.CENTER)
            .setFontColor(ColorConstants.GRAY)
            .setMarginTop(10);
            document.add(footer);

            document.close();

            return fileName;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // ============ MÉTODOS AUXILIARES PARA TABLAS ============

    private void addTableCell(com.itextpdf.layout.element.Table table, String label, String value, boolean isGray) {
        com.itextpdf.layout.element.Cell cell1 = new com.itextpdf.layout.element.Cell()
            .add(new Paragraph(label).setBold())
            .setPadding(5)
            .setBackgroundColor(isGray ? new DeviceRgb(245, 245, 245) : ColorConstants.WHITE)
            .setBorder(new SolidBorder(0.5f));

        com.itextpdf.layout.element.Cell cell2 = new com.itextpdf.layout.element.Cell()
            .add(new Paragraph(value))
            .setPadding(5)
            .setBackgroundColor(isGray ? new DeviceRgb(250, 250, 250) : ColorConstants.WHITE)
            .setBorder(new SolidBorder(0.5f));

        table.addCell(cell1);
        table.addCell(cell2);
    }

    private void addStyledTableCell(com.itextpdf.layout.element.Table table, String label, String value, boolean highlight) {
        DeviceRgb bgColor = highlight ? new DeviceRgb(240, 248, 255) : new DeviceRgb(255, 255, 255);
        DeviceRgb textColor = highlight ? new DeviceRgb(0, 102, 204) : new DeviceRgb(0, 0, 0);

        com.itextpdf.layout.element.Cell cell1 = new com.itextpdf.layout.element.Cell()
            .add(new Paragraph(label).setBold().setFontColor(textColor))
            .setPadding(8)
            .setBackgroundColor(bgColor)
            .setBorder(new SolidBorder(0.5f));

        com.itextpdf.layout.element.Cell cell2 = new com.itextpdf.layout.element.Cell()
            .add(new Paragraph(value).setBold().setFontSize(12).setFontColor(textColor))
            .setPadding(8)
            .setBackgroundColor(bgColor)
            .setBorder(new SolidBorder(0.5f));

        table.addCell(cell1);
        table.addCell(cell2);
    }

    private void addTableHeaderCell(com.itextpdf.layout.element.Table table, String text) {
        com.itextpdf.layout.element.Cell cell = new com.itextpdf.layout.element.Cell()
            .add(new Paragraph(text).setBold().setFontColor(ColorConstants.WHITE))
            .setPadding(10)
            .setBackgroundColor(new DeviceRgb(70, 130, 180))
            .setTextAlignment(TextAlignment.CENTER)
            .setBorder(new SolidBorder(0.5f));
        table.addCell(cell);
    }

    private void addUserRow(com.itextpdf.layout.element.Table table, String role, long count, long total, String status) {
        double percentage = total > 0 ? (count * 100.0 / total) : 0;

        table.addCell(createDataCell(role));
        table.addCell(createDataCell(String.valueOf(count)));
        table.addCell(createDataCell(String.format("%.1f%%", percentage)));
        table.addCell(createDataCell(status));
    }

    private void addTestRow(com.itextpdf.layout.element.Table table, String testName, String attempts, String avgScore, String status) {
        table.addCell(createDataCell(testName));
        table.addCell(createDataCell(attempts));
        table.addCell(createDataCell(avgScore));
        table.addCell(createDataCell(status));
    }

    private void addTopStudentRow(com.itextpdf.layout.element.Table table, String rank, String name, String avgScore, String tests) {
        table.addCell(createDataCell(rank).setTextAlignment(TextAlignment.CENTER));
        table.addCell(createDataCell(name));
        table.addCell(createDataCell(avgScore).setTextAlignment(TextAlignment.CENTER));
        table.addCell(createDataCell(tests).setTextAlignment(TextAlignment.CENTER));
    }

    private void addRecommendationRow(com.itextpdf.layout.element.Table table, String number, String text, String priority) {
        DeviceRgb priorityColor = 
            priority.contains("Alta") ? new DeviceRgb(220, 53, 69) :
            priority.contains("Media") ? new DeviceRgb(255, 193, 7) :
            priority.contains("Crítica") ? new DeviceRgb(220, 53, 69) :
            new DeviceRgb(40, 167, 69);

        table.addCell(createDataCell(number).setBackgroundColor(new DeviceRgb(245, 245, 245)));
        table.addCell(createDataCell(text + " • ").add(new Paragraph(priority).setBold().setFontColor(priorityColor)));
    }

    private com.itextpdf.layout.element.Cell createDataCell(String text) {
        return new com.itextpdf.layout.element.Cell()
            .add(new Paragraph(text))
            .setPadding(7)
            .setBorder(new SolidBorder(0.5f));
    }
    
    private void generateReport() {
        String reportType = (String) reportTypeCombo.getSelectedItem();
        showStatus("Generando reporte: " + reportType + "...");

        SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() {
                try {
                    // Generar contenido del reporte
                    String reportContent = generateReportContent();

                    // Generar PDF
                    String pdfPath = generateReportPdf(reportType, reportContent);

                    return pdfPath;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void done() {
                try {
                    String pdfPath = get();

                    if (pdfPath != null) {
                        showStatus("Reporte generado exitosamente");

                        // Mostrar diálogo de éxito con opciones
                        Object[] options = {"Ver Reporte", "Abrir PDF", "Cerrar"};
                        int choice = JOptionPane.showOptionDialog(ReportsDialog.this,
                            "Reporte generado exitosamente.\nArchivo: " + new File(pdfPath).getName(),
                            "Reporte Listo",
                            JOptionPane.DEFAULT_OPTION,
                            JOptionPane.INFORMATION_MESSAGE,
                            null,
                            options,
                            options[0]);

                        if (choice == 0) {
                            showGeneratedReport();
                        } else if (choice == 1) {
                            openPdfFile(pdfPath);
                        }
                    } else {
                        showStatus("Error generando reporte");
                        JOptionPane.showMessageDialog(ReportsDialog.this,
                            "Error al generar el reporte PDF.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        worker.execute();
    }

    // Método para abrir el PDF
    private void openPdfFile(String filePath) {
        try {
            File pdfFile = new File(filePath);
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(pdfFile);
                showStatus("PDF abierto: " + pdfFile.getName());
            } else {
                JOptionPane.showMessageDialog(this,
                    "No se puede abrir el PDF automáticamente.\nUbicación: " + filePath,
                    "Información",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            showStatus("Error abriendo PDF: " + e.getMessage());
        }
    }

    private void previewReport() {
        String reportType = (String) reportTypeCombo.getSelectedItem();
        String reportContent = generateReportContent();

        JDialog previewDialog = new JDialog(this, "Vista Previa del Reporte", true);
        previewDialog.setSize(900, 650);
        previewDialog.setLocationRelativeTo(this);

        GradientPanel mainPanel = new GradientPanel(COLOR_FONDO, new Color(255, 179, 71));
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Contenido del reporte con formato mejorado
        JTextArea reportContentArea = new JTextArea(reportContent);
        reportContentArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        reportContentArea.setEditable(false);

        // Panel con scroll y márgenes
        JScrollPane scrollPane = new JScrollPane(reportContentArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            "Vista Previa: " + reportType,
            javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            new Font("Verdana", Font.BOLD, 12),
            new Color(66, 66, 66)
        ));

        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Panel de botones con más opciones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setOpaque(false);

        // Generar PDF
        JButton pdfButton = createButton("Generar PDF", e -> {
            String pdfPath = generateReportPdf(reportType, reportContent);
            if (pdfPath != null) {
                JOptionPane.showMessageDialog(previewDialog,
                    "PDF generado exitosamente.\nUbicación: " + pdfPath,
                    "PDF Generado",
                    JOptionPane.INFORMATION_MESSAGE);
                previewDialog.dispose();
            } else {
                JOptionPane.showMessageDialog(previewDialog,
                    "Error generando PDF",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }, new Color(23, 162, 184), new Color(23, 132, 144));

        // Imprimir
        JButton printButton = createButton("🖨️ Imprimir", e -> {
            try {
                reportContentArea.print();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(previewDialog,
                    "Error al imprimir: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }, new Color(108, 117, 125), new Color(84, 91, 98));

        // Cerrar
        JButton closeButton = createButton("Cerrar", e -> previewDialog.dispose(), 
            new Color(220, 53, 69), new Color(200, 35, 51));

        buttonPanel.add(pdfButton);
        buttonPanel.add(printButton);
        buttonPanel.add(closeButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        previewDialog.setContentPane(mainPanel);
        previewDialog.setVisible(true);
    }

    private void exportToExcel() {
        try {
            // Crear directorio si no existe
            File reportsDir = new File("reports");
            if (!reportsDir.exists()) {
                reportsDir.mkdirs();
            }

            String fileName = String.format("reports/reporte_%s.xlsx",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmm")));

            showStatus("Exportando a Excel...");

            SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() {
                    try {
                        // Aquí iría la lógica real de exportación a Excel
                        // Por ahora simula la exportación
                        Thread.sleep(1500);

                        // Generar un archivo Excel de ejemplo (simulado)
                        File excelFile = new File(fileName);
                        excelFile.createNewFile();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        showStatus("Exportación a Excel completada");

                        int option = JOptionPane.showConfirmDialog(ReportsDialog.this,
                            "Archivo Excel generado exitosamente.\n¿Desea abrir la carpeta de reportes?",
                            "Exportación Exitosa",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.INFORMATION_MESSAGE);

                        if (option == JOptionPane.YES_OPTION) {
                            try {
                                Desktop.getDesktop().open(new File("reports"));
                            } catch (Exception e) {
                                JOptionPane.showMessageDialog(ReportsDialog.this,
                                    "Ubicación: " + new File(fileName).getAbsolutePath(),
                                    "Ubicación del Archivo",
                                    JOptionPane.INFORMATION_MESSAGE);
                            }
                        }
                    } catch (Exception e) {
                        showStatus("Error en exportación");
                    }
                }
            };

            worker.execute();

        } catch (Exception e) {
            e.printStackTrace();
            showStatus("Error: " + e.getMessage());
        }
    }
    
    private void downloadPDF() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar reporte PDF");
        fileChooser.setSelectedFile(new java.io.File("reporte_" + 
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".pdf"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            showStatus("PDF guardado: " + fileChooser.getSelectedFile().getName());
        }
    }
    
    private String generateReportContent() {
        List<Test> tests = appController.getTests();
        List<Result> results = appController.getResults();
        List<User> users = appController.getUsers();
        
        long totalTests = tests.size();
        long totalResults = results.size();
        long totalUsers = users.size();
        
        double avgScore = results.stream()
            .mapToDouble(Result::getPercentage)
            .average()
            .orElse(0.0);
        
        long approved = results.stream()
            .filter(r -> r.getPercentage() >= 70)
            .count();
        
        double approvalRate = results.isEmpty() ? 0 : (approved * 100.0 / results.size());
        
        return """
            📋 REPORTE DEL SISTEMA E-VALUA
            ================================
            Fecha: """ + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) + """
            
            📊 RESUMEN GENERAL
            ================================
            • Total de pruebas en sistema: """ + totalTests + """
            • Total de resultados registrados: """ + totalResults + """
            • Total de usuarios: """ + totalUsers + """
            • Promedio general de calificación: """ + String.format("%.2f%%", avgScore) + """
            • Tasa de aprobación: """ + String.format("%.1f%%", approvalRate) + """
            
            👥 DISTRIBUCIÓN DE USUARIOS
            ================================
            """ + getUsersDistribution(users) + """
            
            📝 ACTIVIDAD POR PRUEBA
            ================================
            """ + getTestsActivity(tests, results) + """
            
            🏆 TOP 10 ESTUDIANTES
            ================================
            """ + getTopStudents(users, results) + """
            
            📈 RECOMENDACIONES
            ================================
            1. Crear más pruebas de áreas con menor actividad
            2. Implementar sistema de recompensas para estudiantes destacados
            3. Realizar seguimiento a estudiantes con bajo rendimiento
            4. Expandir catálogo de pruebas temáticas
            
            🔧 GENERADO POR: """ + currentUser.getDisplayName() + """
            """;
    }
    
    private String getUsersDistribution(List<User> users) {
        long admins = users.stream().filter(u -> u.getRole().equals("admin")).count();
        long teachers = users.stream().filter(u -> u.getRole().equals("teacher")).count();
        long students = users.stream().filter(u -> u.getRole().equals("student")).count();
        
        return String.format(
            "• Administradores: %d (%.1f%%)\n" +
            "• Profesores: %d (%.1f%%)\n" +
            "• Estudiantes: %d (%.1f%%)\n",
            admins, (admins * 100.0 / users.size()),
            teachers, (teachers * 100.0 / users.size()),
            students, (students * 100.0 / users.size())
        );
    }
    
    private String getTestsActivity(List<Test> tests, List<Result> results) {
        StringBuilder sb = new StringBuilder();
        
        for (Test test : tests) {
            long testResults = results.stream()
                .filter(r -> r.getTestTitle().equals(test.getTitle()))
                .count();
            
            double avgScore = results.stream()
                .filter(r -> r.getTestTitle().equals(test.getTitle()))
                .mapToDouble(Result::getPercentage)
                .average()
                .orElse(0.0);
            
            sb.append(String.format("• %s: %d realizaciones, %.1f%% promedio\n", 
                truncate(test.getTitle(), 30), testResults, avgScore));
        }
        
        return sb.toString();
    }
    
    private String getTopStudents(List<User> users, List<Result> results) {
        StringBuilder sb = new StringBuilder();
        
        users.stream()
            .filter(u -> u.getRole().equals("student"))
            .sorted((a, b) -> {
                double avgA = results.stream()
                    .filter(r -> r.getStudentUsername().equals(a.getUsername()))
                    .mapToDouble(Result::getPercentage)
                    .average()
                    .orElse(0.0);
                double avgB = results.stream()
                    .filter(r -> r.getStudentUsername().equals(b.getUsername()))
                    .mapToDouble(Result::getPercentage)
                    .average()
                    .orElse(0.0);
                return Double.compare(avgB, avgA);
            })
            .limit(10)
            .forEach(user -> {
                double avgScore = results.stream()
                    .filter(r -> r.getStudentUsername().equals(user.getUsername()))
                    .mapToDouble(Result::getPercentage)
                    .average()
                    .orElse(0.0);
                long testsTaken = results.stream()
                    .filter(r -> r.getStudentUsername().equals(user.getUsername()))
                    .count();
                
                sb.append(String.format("• %s: %.1f%% (%d pruebas)\n", 
                    user.getDisplayName(), avgScore, testsTaken));
            });
        
        return sb.toString();
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