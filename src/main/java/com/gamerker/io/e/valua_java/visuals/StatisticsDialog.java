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
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Diálogo para mostrar estadísticas detalladas de pruebas y resultados
 */
public class StatisticsDialog extends JDialog {
    private final AppController appController;
    private final User currentUser;
    
    // Colores del tema
    private final Color COLOR_FONDO = new Color(255, 218, 185);
    private final Color COLOR_BOTON = new Color(255, 140, 0);
    private final Color COLOR_BOTON_HOVER = new Color(255, 165, 0);
    private final Color COLOR_PRUEBA = new Color(135, 206, 250);
    private final Color COLOR_EXITO = new Color(144, 238, 144);
    private final Color COLOR_PROMEDIO = new Color(255, 255, 153);
    private final Color COLOR_BAJO = new Color(255, 204, 204);
    
    private JTabbedPane tabbedPane;
    private JLabel summaryLabel;
    
    public StatisticsDialog(JFrame owner, User user, AppController controller) {
        super(owner, "📊 Estadísticas del Sistema", true);
        this.currentUser = user;
        this.appController = controller;
        
        setSize(1000, 650);
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        initComponents();
        loadStatistics();
    }
    
    private void initComponents() {
        GradientPanel mainPanel = new GradientPanel(COLOR_FONDO, new Color(255, 179, 71));
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Panel superior con resumen
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Panel central con pestañas
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Verdana", Font.BOLD, 12));
        tabbedPane.setBackground(COLOR_FONDO);
        
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
        setContentPane(mainPanel);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 5));
        panel.setOpaque(false);
        
        // Título
        JLabel titleLabel = new JLabel("ESTADÍSTICAS DEL SISTEMA");
        titleLabel.setFont(new Font("Verdana", Font.BOLD, 16));
        titleLabel.setForeground(Color.BLACK);
        titleLabel.setHorizontalAlignment(SwingConstants.LEFT);
        
        // Resumen
        summaryLabel = new JLabel("Cargando estadísticas...");
        summaryLabel.setFont(new Font("Verdana", Font.PLAIN, 12));
        summaryLabel.setForeground(Color.BLACK);
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(summaryLabel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void loadStatistics() {
        // Actualizar resumen
        updateSummary();
        
        // Crear pestañas
        createGeneralTab();
        createTestsTab();
        createUsersTab();
        createPerformanceTab();
        
        if (currentUser.getRole().equals("admin")) {
            createSystemTab();
        }
    }
    
    private void updateSummary() {
        List<Test> tests = appController.getTests();
        List<Result> results = appController.getResults();
        List<User> users = appController.getUsers();
        
        long totalTests = tests.size();
        long totalResults = results.size();
        long totalUsers = users.size();
        long totalTeachers = users.stream().filter(u -> u.getRole().equals("teacher")).count();
        long totalStudents = users.stream().filter(u -> u.getRole().equals("student")).count();
        
        double avgScore = results.stream()
            .mapToDouble(Result::getPercentage)
            .average()
            .orElse(0.0);
        
        summaryLabel.setText(String.format(
            "Pruebas: %d | Resultados: %d | Usuarios: %d (%d estudiantes | %d profesores) | Promedio: %.2f%%",
            totalTests, totalResults, totalUsers, totalStudents, totalTeachers, avgScore
        ));
    }
    
    private void createGeneralTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);
        
        // Panel superior con métricas
        JPanel metricsPanel = new JPanel(new GridLayout(2, 4, 10, 10));
        metricsPanel.setOpaque(false);
        metricsPanel.setBorder(BorderFactory.createTitledBorder("Métricas Generales"));
        
        List<Test> tests = appController.getTests();
        List<Result> results = appController.getResults();
        List<User> users = appController.getUsers();
        
        // Métrica 1: Total de pruebas
        addMetric(metricsPanel, "📋", "Pruebas", String.valueOf(tests.size()), COLOR_PRUEBA);
        
        // Métrica 2: Resultados totales
        addMetric(metricsPanel, "📊", "Resultados", String.valueOf(results.size()), COLOR_EXITO);
        
        // Métrica 3: Usuarios totales
        addMetric(metricsPanel, "👥", "Usuarios", String.valueOf(users.size()), new Color(173, 216, 230));
        
        // Métrica 4: Promedio general
        double avgScore = results.stream()
            .mapToDouble(Result::getPercentage)
            .average()
            .orElse(0.0);
        addMetric(metricsPanel, "📈", "Promedio", String.format("%.2f%%", avgScore), 
            avgScore >= 70 ? COLOR_EXITO : avgScore >= 60 ? COLOR_PROMEDIO : COLOR_BAJO);
        
        // Métrica 5: Tasa de aprobación
        long approved = results.stream()
            .filter(r -> r.getPercentage() >= 70)
            .count();
        double approvalRate = results.isEmpty() ? 0 : (approved * 100.0 / results.size());
        addMetric(metricsPanel, "✅", "Aprobación", String.format("%.1f%%", approvalRate),
            approvalRate >= 70 ? COLOR_EXITO : approvalRate >= 50 ? COLOR_PROMEDIO : COLOR_BAJO);
        
        // Métrica 6: Estudiantes activos (con al menos 1 resultado)
        long activeStudents = users.stream()
            .filter(u -> u.getRole().equals("student"))
            .filter(u -> results.stream().anyMatch(r -> r.getStudentUsername().equals(u.getUsername())))
            .count();
        addMetric(metricsPanel, "🎯", "Estudiantes Activos", String.valueOf(activeStudents), new Color(255, 182, 193));
        
        // Métrica 7: Prueba más tomada
        if (!results.isEmpty()) {
            String mostTakenTest = results.stream()
                .collect(Collectors.groupingBy(Result::getTestTitle, Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("N/A");
            addMetric(metricsPanel, "🏆", "Prueba Popular", truncate(mostTakenTest, 15), COLOR_BOTON);
        } else {
            addMetric(metricsPanel, "🏆", "Prueba Popular", "N/A", COLOR_BAJO);
        }
        
        // Métrica 8: Última actividad
        if (!results.isEmpty()) {
            LocalDateTime lastActivity = results.stream()
                .map(Result::getTimestamp)
                .max(LocalDateTime::compareTo)
                .orElse(LocalDateTime.now());
            addMetric(metricsPanel, "🕙", "Última Actividad", 
                lastActivity.format(DateTimeFormatter.ofPattern("dd/MM HH:mm")), new Color(200, 200, 200));
        }
        
        panel.add(metricsPanel, BorderLayout.NORTH);
        
        // Panel inferior con gráfico de actividad (simulado)
        JPanel activityPanel = new JPanel(new BorderLayout());
        activityPanel.setOpaque(false);
        activityPanel.setBorder(BorderFactory.createTitledBorder("Actividad Últimos 7 Días"));
        
        JTextArea activityText = new JTextArea(generateActivityText());
        activityText.setFont(new Font("Monospaced", Font.PLAIN, 11));
        activityText.setEditable(false);
        activityText.setOpaque(false);
        
        JScrollPane scrollPane = new JScrollPane(activityText);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        activityPanel.add(scrollPane, BorderLayout.CENTER);
        panel.add(activityPanel, BorderLayout.CENTER);
        
        tabbedPane.addTab("General", panel);
    }
    
    private void createTestsTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);
        
        // Tabla de estadísticas por prueba
        String[] columns = {"Prueba", "Realizaciones", "Promedio", "Aprobados", "Tasa %", "Mejor Puntaje"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable table = new JTable(model);
        table.setFont(new Font("Verdana", Font.PLAIN, 11));
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Verdana", Font.BOLD, 12));
        
        // Llenar datos
        Map<String, List<Result>> resultsByTest = appController.getResults().stream()
            .collect(Collectors.groupingBy(Result::getTestTitle));
        
        for (Map.Entry<String, List<Result>> entry : resultsByTest.entrySet()) {
            String testTitle = entry.getKey();
            List<Result> testResults = entry.getValue();
            
            long totalAttempts = testResults.size();
            double average = testResults.stream()
                .mapToDouble(Result::getPercentage)
                .average()
                .orElse(0.0);
            long approved = testResults.stream()
                .filter(r -> r.getPercentage() >= 70)
                .count();
            double approvalRate = totalAttempts == 0 ? 0 : (approved * 100.0 / totalAttempts);
            double bestScore = testResults.stream()
                .mapToDouble(Result::getPercentage)
                .max()
                .orElse(0.0);
            
            model.addRow(new Object[]{
                truncate(testTitle, 30),
                totalAttempts,
                String.format("%.2f%%", average),
                approved,
                String.format("%.1f%%", approvalRate),
                String.format("%.2f%%", bestScore)
            });
        }
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Estadísticas por Prueba"));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        tabbedPane.addTab("Pruebas", panel);
    }
    
    private void createUsersTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);
        
        // Panel con distribución de roles
        JPanel rolesPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        rolesPanel.setOpaque(false);
        rolesPanel.setBorder(BorderFactory.createTitledBorder("Distribución de Usuarios"));
        
        // Estadísticas de roles
        List<User> users = appController.getUsers();
        long totalUsers = users.size();
        long admins = users.stream().filter(u -> u.getRole().equals("admin")).count();
        long teachers = users.stream().filter(u -> u.getRole().equals("teacher")).count();
        long students = users.stream().filter(u -> u.getRole().equals("student")).count();
        
        JTextArea rolesText = new JTextArea();
        rolesText.setFont(new Font("Monospaced", Font.PLAIN, 12));
        rolesText.setEditable(false);
        rolesText.setOpaque(false);
        rolesText.setText(String.format("""
                                        \ud83d\udc51 Administradores: %d (%.1f%%)
                                        \ud83c\udf93 Profesores: %d (%.1f%%)
                                        \ud83d\udc64 Estudiantes: %d (%.1f%%)
                                        
                                        \ud83d\udc65 TOTAL: %d usuarios""",
            admins, (admins * 100.0 / totalUsers),
            teachers, (teachers * 100.0 / totalUsers),
            students, (students * 100.0 / totalUsers),
            totalUsers
        ));
        
        rolesPanel.add(rolesText);
        
        // Panel de actividad de usuarios
        JPanel activityPanel = new JPanel(new BorderLayout());
        activityPanel.setOpaque(false);
        activityPanel.setBorder(BorderFactory.createTitledBorder("Usuarios Más Activos"));
        
        String[] userColumns = {"Usuario", "Rol", "Pruebas Realizadas", "Mejor %", "Promedio"};
        DefaultTableModel userModel = new DefaultTableModel(userColumns, 0);
        
        // Agrupar resultados por usuario
        Map<String, List<Result>> resultsByUser = appController.getResults().stream()
            .collect(Collectors.groupingBy(Result::getStudentUsername));
        
        for (Map.Entry<String, List<Result>> entry : resultsByUser.entrySet()) {
            String username = entry.getKey();
            List<Result> userResults = entry.getValue();
            
            User user = users.stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst()
                .orElse(null);
            
            if (user != null) {
                long testCount = userResults.size();
                double bestScore = userResults.stream()
                    .mapToDouble(Result::getPercentage)
                    .max()
                    .orElse(0.0);
                double avgScore = userResults.stream()
                    .mapToDouble(Result::getPercentage)
                    .average()
                    .orElse(0.0);
                
                userModel.addRow(new Object[]{
                    user.getDisplayName(),
                    user.getRole(),
                    testCount,
                    String.format("%.2f%%", bestScore),
                    String.format("%.2f%%", avgScore)
                });
            }
        }
        
        JTable userTable = new JTable(userModel);
        userTable.setFont(new Font("Verdana", Font.PLAIN, 11));
        JScrollPane userScroll = new JScrollPane(userTable);
        
        activityPanel.add(userScroll, BorderLayout.CENTER);
        rolesPanel.add(activityPanel);
        
        panel.add(rolesPanel, BorderLayout.CENTER);
        tabbedPane.addTab("Usuarios", panel);
    }
    
    private void createPerformanceTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);
        
        // Análisis de rendimiento por hora del día
        JPanel analysisPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        analysisPanel.setOpaque(false);
        
        // Distribución por hora
        JPanel hourPanel = new JPanel(new BorderLayout());
        hourPanel.setOpaque(false);
        hourPanel.setBorder(BorderFactory.createTitledBorder("Actividad por Hora del Día"));
        
        Map<Integer, Long> hourDistribution = appController.getResults().stream()
            .collect(Collectors.groupingBy(r -> r.getTimestamp().getHour(), Collectors.counting()));
        
        StringBuilder hourText = new StringBuilder("🕙 Distribución de pruebas realizadas:\n\n");
        for (int hour = 0; hour < 24; hour++) {
            long count = hourDistribution.getOrDefault(hour, 0L);
            hourText.append(String.format("%02d:00 - %02d:59: %3d pruebas\n", hour, hour, count));
        }
        
        JTextArea hourArea = new JTextArea(hourText.toString());
        hourArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        hourArea.setEditable(false);
        hourArea.setOpaque(false);
        
        hourPanel.add(new JScrollPane(hourArea), BorderLayout.CENTER);
        analysisPanel.add(hourPanel);
        
        // Rendimiento por hora
        JPanel performancePanel = new JPanel(new BorderLayout());
        performancePanel.setOpaque(false);
        performancePanel.setBorder(BorderFactory.createTitledBorder("Rendimiento Promedio por Hora"));
        
        Map<Integer, Double> hourPerformance = new HashMap<>();
        for (Result result : appController.getResults()) {
            int hour = result.getTimestamp().getHour();
            hourPerformance.merge(hour, result.getPercentage(), (old, newVal) -> (old + newVal) / 2);
        }
        
        StringBuilder perfText = new StringBuilder("📈 Mejor hora para rendimiento:\n\n");
        hourPerformance.entrySet().stream()
            .sorted(Map.Entry.<Integer, Double>comparingByValue().reversed())
            .limit(5)
            .forEach(entry -> {
                perfText.append(String.format("🕐 %02d:00: %.2f%%\n", entry.getKey(), entry.getValue()));
            });
        
        JTextArea perfArea = new JTextArea(perfText.toString());
        perfArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        perfArea.setEditable(false);
        perfArea.setOpaque(false);
        
        performancePanel.add(new JScrollPane(perfArea), BorderLayout.CENTER);
        analysisPanel.add(performancePanel);
        
        panel.add(analysisPanel, BorderLayout.CENTER);
        tabbedPane.addTab("Rendimiento", panel);
    }
    
    private void createSystemTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);
        
        // Solo para administradores: estadísticas del sistema
        JPanel systemPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        systemPanel.setOpaque(false);
        
        // Información del sistema
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setOpaque(false);
        infoPanel.setBorder(BorderFactory.createTitledBorder("Información del Sistema"));
        
        StringBuilder sysInfo = new StringBuilder();
        sysInfo.append("🔧 CONFIGURACIÓN DEL SISTEMA\n\n");
        sysInfo.append("📁 Ubicación de datos: ").append(System.getProperty("user.dir")).append("\n");
        sysInfo.append("💾 Memoria total: ").append(Runtime.getRuntime().totalMemory() / (1024*1024)).append(" MB\n");
        sysInfo.append("📊 Memoria libre: ").append(Runtime.getRuntime().freeMemory() / (1024*1024)).append(" MB\n");
        sysInfo.append("🖥️ Procesadores: ").append(Runtime.getRuntime().availableProcessors()).append("\n");
        sysInfo.append("📅 Fecha sistema: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))).append("\n");
        
        JTextArea sysArea = new JTextArea(sysInfo.toString());
        sysArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        sysArea.setEditable(false);
        sysArea.setOpaque(false);
        
        infoPanel.add(new JScrollPane(sysArea), BorderLayout.CENTER);
        systemPanel.add(infoPanel);
        
        // Estadísticas de almacenamiento
        JPanel storagePanel = new JPanel(new BorderLayout());
        storagePanel.setOpaque(false);
        storagePanel.setBorder(BorderFactory.createTitledBorder("Uso de Almacenamiento"));
        
        long estimatedSize = appController.getResults().size() * 500L; // Estimación aproximada
        long usersSize = appController.getUsers().size() * 200L;
        long testsSize = appController.getTests().size() * 1000L;
        
        StringBuilder storageText = new StringBuilder();
        storageText.append("💾 ESTIMACIÓN DE ALMACENAMIENTO\n\n");
        storageText.append("📊 Resultados: ").append(estimatedSize / 1024).append(" KB\n");
        storageText.append("👥 Usuarios: ").append(usersSize / 1024).append(" KB\n");
        storageText.append("📝 Pruebas: ").append(testsSize / 1024).append(" KB\n");
        storageText.append("📈 TOTAL: ").append((estimatedSize + usersSize + testsSize) / (1024*1024)).append(" MB\n");
        
        JTextArea storageArea = new JTextArea(storageText.toString());
        storageArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        storageArea.setEditable(false);
        storageArea.setOpaque(false);
        
        storagePanel.add(new JScrollPane(storageArea), BorderLayout.CENTER);
        systemPanel.add(storagePanel);
        
        panel.add(systemPanel, BorderLayout.CENTER);
        tabbedPane.addTab("Sistema", panel);
    }
    
    private void addMetric(JPanel panel, String emoji, String title, String value, Color color) {
        JPanel metricPanel = new JPanel();
        metricPanel.setLayout(new BoxLayout(metricPanel, BoxLayout.Y_AXIS));
        metricPanel.setBackground(color);
        metricPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color.darker(), 1),
            BorderFactory.createEmptyBorder(10, 5, 10, 5)
        ));
        metricPanel.setPreferredSize(new Dimension(120, 90));
        
        JLabel emojiLabel = new JLabel(emoji);
        emojiLabel.setFont(getEmojiFont(24));
        emojiLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        emojiLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0)); // 5px arriba
        
        metricPanel.add(emojiLabel);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Verdana", Font.BOLD, 10));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        metricPanel.add(titleLabel);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Verdana", Font.BOLD, 14));
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        metricPanel.add(valueLabel);
        
        panel.add(metricPanel);
    }
    
    private String generateActivityText() {
        StringBuilder sb = new StringBuilder();
        Map<String, Long> dailyCount = new LinkedHashMap<>();
        
        // Últimos 7 días
        LocalDateTime now = LocalDateTime.now();
        for (int i = 6; i >= 0; i--) {
            LocalDateTime date = now.minusDays(i);
            String dateStr = date.format(DateTimeFormatter.ofPattern("dd/MM"));
            
            long count = appController.getResults().stream()
                .filter(r -> r.getTimestamp().toLocalDate().equals(date.toLocalDate()))
                .count();
            
            dailyCount.put(dateStr, count);
        }
        
        sb.append("📅 Actividad de los últimos 7 días:\n\n");
        for (Map.Entry<String, Long> entry : dailyCount.entrySet()) {
            sb.append(entry.getKey()).append(": ");
            for (int i = 0; i < Math.min(entry.getValue(), 20); i++) {
                sb.append("█");
            }
            sb.append(" ").append(entry.getValue()).append(" pruebas\n");
        }
        
        return sb.toString();
    }
    
    private JLabel createIcon(String emoji, int size) {
        JLabel icon = new JLabel(emoji);
        icon.setFont(getEmojiFont(size));
        return icon;
    }
    
    private Font getEmojiFont(float size) {
        return new Font("Segoe UI Emoji", Font.PLAIN, (int)size);
    }
    
    private String truncate(String text, int length) {
        return text.length() > length ? text.substring(0, length - 3) + "..." : text;
    }
}
