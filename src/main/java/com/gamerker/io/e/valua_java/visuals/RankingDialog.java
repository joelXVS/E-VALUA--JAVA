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
import com.gamerker.io.e.valua_java.mainClasses.*;
import com.gamerker.io.e.valua_java.utils.GradientPanel;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import javax.swing.Timer;

/**
 * Diálogo de rankings con dos pestañas:
 * 1. Ranking Global: Mejores estudiantes por porcentaje general
 * 2. Ranking por Prueba: Mejores resultados para cada prueba específica
 */
public class RankingDialog extends JDialog {
    private final User currentUser;
    private final AppController appController;
    
    private JTabbedPane tabbedPane;
    private JTable globalTable, testTable;
    private DefaultTableModel globalModel, testModel;
    private JComboBox<String> testFilterCombo;
    private JLabel statusLabel;
    
    // Colores del tema
    private final Color COLOR_FONDO = new Color(255, 218, 185);
    private final Color COLOR_BOTON = new Color(255, 140, 0);
    private final Color COLOR_BOTON_HOVER = new Color(255, 165, 0);
    private final Color COLOR_ORO = new Color(255, 215, 0);
    private final Color COLOR_PLATA = new Color(192, 192, 192);
    private final Color COLOR_BRONCE = new Color(205, 127, 50);
    
    public RankingDialog(JFrame owner, User user, AppController controller) {
        super(owner, "🏆 Rankings y Estadísticas", true);
        this.currentUser = user;
        this.appController = controller;
        
        setSize(1000, 650);
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        initComponents();
        loadGlobalRanking();
        loadTestRanking();
    }
    
    private void initComponents() {
        // Panel principal con gradiente
        GradientPanel mainPanel = new GradientPanel(COLOR_FONDO, new Color(255, 179, 71));
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Panel superior: título y filtros
        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        
        // TabbedPane con las dos pestañas
        tabbedPane = createTabbedPane();
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
        // Panel inferior: estado
        mainPanel.add(createStatusPanel(), BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        panel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("Rankings y Estadísticas Globales");
        titleLabel.setFont(new Font("Verdana", Font.BOLD, 20));
        titleLabel.setForeground(Color.BLACK);
        panel.add(titleLabel);
        
        // Filtro de prueba (solo para la pestaña de ranking por prueba)
        panel.add(new JLabel("Filtrar prueba:"));
        testFilterCombo = new JComboBox<>();
        testFilterCombo.setPreferredSize(new Dimension(250, 28));
        testFilterCombo.addActionListener(e -> loadTestRanking());
        panel.add(testFilterCombo);
        
        JButton refreshButton = new JButton("🔄 Actualizar");
        refreshButton.setBackground(COLOR_BOTON);
        refreshButton.setForeground(Color.BLACK);
        refreshButton.addActionListener(e -> {
            loadGlobalRanking();
            loadTestRanking();
            showSuccess("Rankings actualizados");
        });
        panel.add(refreshButton);
        
        return panel;
    }
    
    private JTabbedPane createTabbedPane() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Verdana", Font.BOLD, 14));
        
        // Pestaña 1: Ranking Global
        JPanel globalPanel = new JPanel(new BorderLayout());
        globalPanel.setOpaque(false);
        globalPanel.add(createGlobalTablePanel(), BorderLayout.CENTER);
        tabs.addTab("🏆 Ranking Global", globalPanel);
        
        // Pestaña 2: Ranking por Prueba
        JPanel testPanel = new JPanel(new BorderLayout());
        testPanel.setOpaque(false);
        testPanel.add(createTestTablePanel(), BorderLayout.CENTER);
        tabs.addTab("📊 Ranking por Prueba", testPanel);
        
        return tabs;
    }
    
    public JPanel createGlobalTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.WHITE, 1),
            "Top 20 Estudiantes - Mejor Puntuación General",
            0, 0,
            new Font("Verdana", Font.BOLD, 14),
            Color.WHITE));
        
        // Modelo de tabla global
        String[] columnas = {"Pos", "🥇🥈🥉", "Estudiante", "Prueba", "Puntaje", "%", "Fecha"};
        globalModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        globalTable = new JTable(globalModel);
        globalTable.setFont(new Font("Verdana", Font.PLAIN, 12));
        globalTable.setRowHeight(28);
        globalTable.getTableHeader().setFont(new Font("Verdana", Font.BOLD, 13));
        globalTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        globalTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        
        // Renderizador con medallas
        globalTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            private final Font EMOJI_FONT = new Font("Segoe UI", Font.PLAIN, 18);
            
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                // Colorear según posición
                if (!isSelected) {
                    String pos = table.getValueAt(row, 0).toString();
                    if ("1".equals(pos)) {
                        c.setBackground(COLOR_ORO);
                        if (column == 1) ((JLabel)c).setFont(EMOJI_FONT);
                    } else if ("2".equals(pos)) {
                        c.setBackground(COLOR_PLATA);
                    } else if ("3".equals(pos)) {
                        c.setBackground(COLOR_BRONCE);
                    } else {
                        c.setBackground(Color.WHITE);
                    }
                }
                
                // Alinear columnas numéricas
                if (column == 4 || column == 5) {
                    ((JLabel)c).setHorizontalAlignment(SwingConstants.CENTER);
                }
                
                return c;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(globalTable);
        scrollPane.setPreferredSize(new Dimension(950, 450));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    public JPanel createTestTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.WHITE, 1),
            "Top 10 por Prueba Específica",
            0, 0,
            new Font("Verdana", Font.BOLD, 14),
            Color.WHITE));
        
        // Modelo de tabla por prueba
        String[] columnas = {"Pos", "🥇", "Estudiante", "Puntaje", "%", "Tiempo"};
        testModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        testTable = new JTable(testModel);
        testTable.setFont(new Font("Verdana", Font.PLAIN, 12));
        testTable.setRowHeight(26);
        testTable.getTableHeader().setFont(new Font("Verdana", Font.BOLD, 13));
        testTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        testTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        
        // Renderizador
        testTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    String pos = table.getValueAt(row, 0).toString();
                    if ("1".equals(pos)) {
                        c.setBackground(COLOR_ORO);
                    } else if ("2".equals(pos)) {
                        c.setBackground(COLOR_PLATA);
                    } else if ("3".equals(pos)) {
                        c.setBackground(COLOR_BRONCE);
                    } else {
                        c.setBackground(Color.WHITE);
                    }
                }
                
                if (column == 3 || column == 4) {
                    ((JLabel)c).setHorizontalAlignment(SwingConstants.CENTER);
                }
                
                return c;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(testTable);
        scrollPane.setPreferredSize(new Dimension(950, 450));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    public JPanel createStatusPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setOpaque(false);
        
        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Verdana", Font.ITALIC, 12));
        statusLabel.setForeground(Color.BLACK);
        panel.add(statusLabel);
        
        return panel;
    }
    
    // ==================== CARGA DE DATOS ====================
    
    private void loadGlobalRanking() {
        List<Result> allResults = appController.getResults();
        
        // Agrupar por usuario (mejor resultado de cada uno)
        Map<String, Result> bestResults = new HashMap<>();
        
        for (Result result : allResults) {
            String username = result.getStudentUsername();
            Result currentBest = bestResults.get(username);
            
            if (currentBest == null || result.getPercentage() > currentBest.getPercentage()) {
                bestResults.put(username, result);
            }
        }
        
        // Ordenar por porcentaje descendente
        List<Map.Entry<String, Result>> ranking = new ArrayList<>(bestResults.entrySet());
        ranking.sort((a, b) -> Double.compare(b.getValue().getPercentage(), a.getValue().getPercentage()));
        
        // Limpiar tabla
        globalModel.setRowCount(0);
        
        // Cargar top 20
        for (int i = 0; i < Math.min(20, ranking.size()); i++) {
            Map.Entry<String, Result> entry = ranking.get(i);
            Result result = entry.getValue();
            User student = findUserByUsername(result.getStudentUsername());
            
            String medal = i == 0 ? "🥇" : i == 1 ? "🥈" : i == 2 ? "🥉" : "";
            
            globalModel.addRow(new Object[]{
                String.valueOf(i + 1),
                medal,
                student != null ? student.getDisplayName() : result.getStudentUsername(),
                result.getTestTitle(),
                String.format("%d/%d", result.getScore(), result.getTotal()),
                String.format("%.2f%%", result.getPercentage()),
                result.getTimestamp().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            });
        }
        
        // Cargar combo de pruebas
        loadTestCombo();
    }
    
    private void loadTestRanking() {
        String selectedTest = (String) testFilterCombo.getSelectedItem();
        if (selectedTest == null || selectedTest.isEmpty()) {
            testModel.setRowCount(0);
            return;
        }
        
        // Filtrar resultados por prueba seleccionada
        List<Result> testResults = appController.getResults().stream()
            .filter(r -> r.getTestTitle().equals(selectedTest))
            .sorted((a, b) -> Double.compare(b.getPercentage(), a.getPercentage()))
            .limit(10)
            .toList();
        
        testModel.setRowCount(0);
        
        for (int i = 0; i < testResults.size(); i++) {
            Result result = testResults.get(i);
            User student = findUserByUsername(result.getStudentUsername());
            
            // Calcular tiempo aproximado (simulado)
            String tiempo = String.format("%d min", result.getTotal() * 2);
            
            testModel.addRow(new Object[]{
                String.valueOf(i + 1),
                i == 0 ? "👑" : "",
                student != null ? student.getDisplayName() : result.getStudentUsername(),
                String.format("%d/%d", result.getScore(), result.getTotal()),
                String.format("%.2f%%", result.getPercentage()),
                tiempo
            });
        }
    }
    
    private void loadTestCombo() {
        testFilterCombo.removeAllItems();
        
        // Obtener pruebas únicas de los resultados
        Set<String> testTitles = new LinkedHashSet<>();
        appController.getTests().forEach(t -> testTitles.add(t.getTitle()));
        
        for (String title : testTitles) {
            testFilterCombo.addItem(title);
        }
        
        // Seleccionar primera por defecto
        if (!testTitles.isEmpty()) {
            testFilterCombo.setSelectedIndex(0);
        }
    }
    
    private User findUserByUsername(String username) {
        return appController.getUsers().stream()
            .filter(u -> u.getUsername().equals(username))
            .findFirst()
            .orElse(null);
    }
    
    private void showSuccess(String message) {
        statusLabel.setText("✅ " + message);
        statusLabel.setForeground(new Color(40, 167, 69));
        
        Timer timer = new Timer(3000, e -> statusLabel.setText(" "));
        timer.setRepeats(false);
        timer.start();
    }
}