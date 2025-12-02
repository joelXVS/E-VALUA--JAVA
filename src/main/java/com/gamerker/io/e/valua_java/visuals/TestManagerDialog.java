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
import com.gamerker.io.e.valua_java.utils.*;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Diálogo de gestión de pruebas para profesores y administradores.
 * Permite: Crear, Editar, Eliminar y Visualizar pruebas con sus preguntas.
 * Solo usuarios con rol 'teacher' o 'admin' pueden acceder.
 */
public class TestManagerDialog extends JDialog {
    private User currentUser;
    private AppController appController;
    
    private JTable testsTable;
    private DefaultTableModel tableModel;
    private JLabel statusLabel, testCountLabel;
    private JButton createButton, editButton, deleteButton, previewButton, refreshButton;
    
    // Colores del tema
    private final Color COLOR_FONDO = new Color(255, 218, 185);
    private final Color COLOR_BOTON = new Color(255, 140, 0);
    private final Color COLOR_BOTON_HOVER = new Color(255, 165, 0);
    private final Color COLOR_EXITO = new Color(40, 167, 69);
    private final Color COLOR_ERROR = new Color(220, 53, 69);
    private final Color COLOR_INFO = new Color(23, 162, 184);
    
    public TestManagerDialog(JFrame owner, User user, AppController controller) {
        super(owner, "📚 Gestión de Pruebas", true);
        
        // Verificar permisos
        if (!user.getRole().equals("teacher") && !user.getRole().equals("admin")) {
            JOptionPane.showMessageDialog(owner, "❌ Acceso denegado. Requiere rol de Profesor o Administrador.", 
                "Error de Permisos", JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }
        
        this.currentUser = user;
        this.appController = controller;
        
        controller.getTests();
        
        setSize(1200, 700);
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        initComponents();
        loadTests();
    }
    
    private void initComponents() {
        // Panel principal con gradiente
        GradientPanel mainPanel = new GradientPanel(COLOR_FONDO, new Color(255, 179, 71));
        mainPanel.setLayout(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Panel superior: título y contador
        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        
        // Panel central: tabla de pruebas
        mainPanel.add(createTablePanel(), BorderLayout.CENTER);
        
        // Panel inferior: botones de acción
        mainPanel.add(createButtonsPanel(), BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        panel.setOpaque(false);
        
        // Título
        JLabel titleLabel = new JLabel("Gestión de Pruebas Académicas");
        titleLabel.setFont(new Font("Verdana", Font.BOLD, 22));
        titleLabel.setForeground(Color.BLACK);
        panel.add(titleLabel);
        
        // Contador de pruebas
        testCountLabel = new JLabel("Total: 0 pruebas");
        testCountLabel.setFont(new Font("Verdana", Font.PLAIN, 14));
        testCountLabel.setForeground(Color.BLACK);
        panel.add(testCountLabel);
        
        // Info del creador
        JLabel creatorLabel = new JLabel("Creador: " + currentUser.getDisplayName());
        creatorLabel.setFont(new Font("Verdana", Font.ITALIC, 12));
        creatorLabel.setForeground(Color.BLACK);
        panel.add(creatorLabel);
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.WHITE, 1),
            "Pruebas Disponibles",
            0, 0,
            new Font("Verdana", Font.BOLD, 14),
            Color.WHITE
        ));
        
        // Modelo de tabla
        String[] columnas = {"ID", "Título", "Precio", "Preguntas", "Creador", "Estado"};
        tableModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        testsTable = new JTable(tableModel);
        testsTable.setFont(new Font("Verdana", Font.PLAIN, 12));
        testsTable.setRowHeight(30);
        testsTable.getTableHeader().setFont(new Font("Verdana", Font.BOLD, 13));
        testsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        testsTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        
        // Renderizador personalizado
        testsTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    // Colorear según precio
                    double price = (double) table.getValueAt(row, 2);
                    if (price > 10000) {
                        c.setBackground(new Color(255, 200, 200));
                    } else if (price > 5000) {
                        c.setBackground(new Color(255, 255, 200));
                    } else {
                        c.setBackground(new Color(200, 255, 200));
                    }
                }
                return c;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(testsTable);
        scrollPane.setPreferredSize(new Dimension(1100, 500));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createButtonsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panel.setOpaque(false);
        
        // Botón Crear Prueba
        addButton(panel, "Crear Prueba", e -> createTest(), COLOR_BOTON);
        
        // Botón Editar Prueba
        addButton(panel, "Editar Prueba", e -> editTest(), new Color(23, 162, 184));
        
        // Botón Eliminar Prueba
        addButton(panel, "Eliminar Prueba", e -> deleteTest(), new Color(220, 53, 69));
        
        // Botón Vista Previa
        addButton(panel, "Vista Previa", e -> previewTest(), new Color(111, 66, 193));
        
        // Botón Refrescar
        addButton(panel, "Refrescar", e -> loadTests(), COLOR_BOTON);
        
        // Etiqueta de estado
        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Verdana", Font.ITALIC, 12));
        statusLabel.setForeground(Color.BLACK);
        panel.add(statusLabel);
        
        return panel;
    }
    
    private void addButton(JPanel parent, String text, java.awt.event.ActionListener listener, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Verdana", Font.BOLD, 13));
        button.setBackground(bgColor);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(160, 35));
        button.addActionListener(listener);
        
        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(COLOR_BOTON_HOVER);
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
        
        parent.add(button);
    }
    
    void loadTests() {
        List<Test> tests = appController.getTests();
        tableModel.setRowCount(0);

        if (tests.isEmpty()) {
            testCountLabel.setText("Total: 0 pruebas");
            return;
        }

        for (int i = 0; i < tests.size(); i++) {
            Test test = tests.get(i);

            // Formatear precio
            String precio = String.format("$%,.0f", test.getPrice());

            // Determinar creador (si el test tiene creador)
            String creador = "Sistema";

            // Determinar estado
            String estado;
            if (test.getQuestions().isEmpty()) {
                estado = "VACÍA";
            } else if (test.getTotalQuestions() < 3) {
                estado = "MINIMA";
            } else {
                estado = "ACTIVA";
            }

            tableModel.addRow(new Object[]{
                i + 1,                      // ID
                test.getTitle(),            // Título
                precio,                     // Precio formateado
                test.getTotalQuestions(),   // Número de preguntas
                creador,                    // Creador
                estado                      // Estado
            });
        }

        testCountLabel.setText("Total: " + tests.size() + " pruebas");
    }
    
    private void createTest() {
        new TestEditorDialog(this, null, appController).setVisible(true);
        loadTests();
    }
    
    private void editTest() {
        int selectedRow = testsTable.getSelectedRow();
        if (selectedRow == -1) {
            showError("Selecciona una prueba para editar");
            return;
        }
        
        int testIndex = (int) tableModel.getValueAt(selectedRow, 0) - 1;
        Test test = appController.getTests().get(testIndex);
        
        new TestEditorDialog(this, test, appController).setVisible(true);
        loadTests();
        showSuccess("Prueba actualizada");
    }
    
    private void deleteTest() {
        int selectedRow = testsTable.getSelectedRow();
        if (selectedRow == -1) {
            showError("Selecciona una prueba para eliminar");
            return;
        }
        
        String testTitle = (String) tableModel.getValueAt(selectedRow, 1);
        int testIndex = (int) tableModel.getValueAt(selectedRow, 0) - 1;
        
        // Verificar si hay resultados asociados
        boolean hasResults = appController.getResults().stream()
            .anyMatch(r -> r.getTestTitle().equals(testTitle));
        
        if (hasResults) {
            int confirm = JOptionPane.showConfirmDialog(this,
                "⚠️ Esta prueba tiene resultados asociados.\n¿Eliminar prueba y todos sus resultados?",
                "Confirmar Eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
            
            if (confirm == JOptionPane.YES_OPTION) {
                // Eliminar resultados
                appController.getResults().removeIf(r -> r.getTestTitle().equals(testTitle));
            } else {
                return;
            }
        } else {
            int confirm = JOptionPane.showConfirmDialog(this,
                "¿Eliminar prueba '" + testTitle + "'?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION);
            
            if (confirm != JOptionPane.YES_OPTION) return;
        }
        
        // Eliminar prueba
        appController.getTests().remove(testIndex);
        appController.saveAll();
        loadTests();
        showSuccess("Prueba eliminada correctamente");
    }
    
    private void previewTest() {
        int selectedRow = testsTable.getSelectedRow();
        if (selectedRow == -1) {
            showError("Selecciona una prueba para ver");
            return;
        }
        
        int testIndex = (int) tableModel.getValueAt(selectedRow, 0) - 1;
        Test test = appController.getTests().get(testIndex);
        
        if (test.getQuestions().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Esta prueba no tiene preguntas.", "Vista Previa", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Construir vista previa
        StringBuilder preview = new StringBuilder();
        preview.append("=== PRUEBA: ").append(test.getTitle()).append(" ===\n");
        preview.append("Precio: $").append(String.format("%,.0f", test.getPrice())).append("\n");
        preview.append("Preguntas: ").append(test.getTotalQuestions()).append("\n\n");
        
        for (int i = 0; i < test.getQuestions().size(); i++) {
            Question q = test.getQuestions().get(i);
            preview.append("PREGUNTA ").append(i + 1).append(": ").append(q.getQuestionText()).append("\n");
            
            char letter = 'A';
            for (String option : q.getOptions()) {
                preview.append("  ").append(letter).append(") ").append(option).append("\n");
                letter++;
            }
            preview.append("✓ Respuesta correcta: ").append(q.getCorrectAnswer()).append(")\n\n");
        }
        
        JTextArea textArea = new JTextArea(preview.toString());
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(800, 500));
        
        JOptionPane.showMessageDialog(this, scrollPane, "Vista Previa: " + test.getTitle(), 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showSuccess(String message) {
        statusLabel.setText(message);
        statusLabel.setForeground(COLOR_EXITO);
        
        Timer timer = new Timer(3000, e -> statusLabel.setText(" "));
        timer.setRepeats(false);
        timer.start();
    }
    
    private void showError(String message) {
        statusLabel.setText(message);
        statusLabel.setForeground(COLOR_ERROR);
        
        Timer timer = new Timer(5000, e -> statusLabel.setText(" "));
        timer.setRepeats(false);
        timer.start();
    }
}

/**
 * Diálogo para crear/editar una prueba completa con todas sus preguntas.
 */
class TestEditorDialog extends JDialog {
    private final Test existingTest;
    private final AppController appController;
    private JTextField titleField, priceField;
    private JTable questionsTable;
    private DefaultTableModel questionsModel;
    private JLabel statusLabel;
    
    // Variable temporal para preguntas
    private final List<Question> tempQuestions = new ArrayList<>();
    private String lastQuestionText = "";
    private String[] lastOptions = new String[4];
    private String lastCorrectAnswer = "A";
    private String lastType = "logic";
    
    public TestEditorDialog(JDialog owner, Test test, AppController controller) {
        super(owner, test == null ? "➕ Nueva Prueba" : "✏️ Editar Prueba", true);
        this.existingTest = test;
        this.appController = controller;
        
        setSize(1100, 700);
        setLocationRelativeTo(owner);
        
        initComponents();
        
        if (existingTest != null) {
            loadTestData();
        }
    }
    
    private void showSuccess(String message) {
        statusLabel.setText(message);
        statusLabel.setForeground(new Color(40, 167, 69));

        // El Timer es javax.swing.Timer, ya usado en el otro diálogo
        Timer timer = new Timer(3000, e -> statusLabel.setText(" "));
        timer.setRepeats(false);
        timer.start();
    }
    
    private void initComponents() {
        // Panel principal
        GradientPanel panel = new GradientPanel(new Color(255, 218, 185), new Color(255, 179, 71));
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Panel superior: datos de la prueba
        panel.add(createTestInfoPanel(), BorderLayout.NORTH);
        
        // Panel central: tabla de preguntas
        panel.add(createQuestionsPanel(), BorderLayout.CENTER);
        
        // Panel inferior: botones
        panel.add(createButtonsPanel(), BorderLayout.SOUTH);
        
        setContentPane(panel);
    }
    
    private JPanel createTestInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.WHITE), 
            "Información de la Prueba", 0, 0, 
            new Font("Verdana", Font.BOLD, 12), Color.WHITE));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Título
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Título:"), gbc);
        gbc.gridx = 1;
        titleField = new JTextField(40);
        titleField.setFont(new Font("Verdana", Font.PLAIN, 14));
        panel.add(titleField, gbc);
        
        // Precio
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Precio ($):"), gbc);
        gbc.gridx = 1;
        priceField = new JTextField(10);
        priceField.setText("5000");
        panel.add(priceField, gbc);
        
        return panel;
    }
    
    private JPanel createQuestionsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.WHITE), 
            "Preguntas", 0, 0, 
            new Font("Verdana", Font.BOLD, 12), Color.WHITE));
        
        // Modelo de tabla de preguntas
        String[] columnas = {"#", "Pregunta", "Tipo", "Opciones", "Correcta"};
        questionsModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        questionsTable = new JTable(questionsModel);
        questionsTable.setFont(new Font("Verdana", Font.PLAIN, 11));
        questionsTable.setRowHeight(25);
        questionsTable.getTableHeader().setFont(new Font("Verdana", Font.BOLD, 12));
        questionsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollPane = new JScrollPane(questionsTable);
        scrollPane.setPreferredSize(new Dimension(1000, 400));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Botones de gestión de preguntas
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        buttonPanel.setOpaque(false);
        
        addButton(buttonPanel, "Agregar Pregunta", e -> addQuestion(), new Color(40, 167, 69));
        addButton(buttonPanel, "Editar Pregunta", e -> editQuestion(), new Color(23, 162, 184));
        addButton(buttonPanel, "Eliminar Pregunta", e -> deleteQuestion(), new Color(220, 53, 69));
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createButtonsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panel.setOpaque(false);
        
        // Guardar
        JButton saveButton = new JButton("Guardar Prueba");
        saveButton.setFont(new Font("Verdana", Font.BOLD, 14));
        saveButton.setBackground(new Color(40, 167, 69));
        saveButton.setForeground(Color.BLACK);
        saveButton.addActionListener(e -> saveTest());
        panel.add(saveButton);
        
        // Cancelar
        JButton cancelButton = new JButton("Cancelar");
        cancelButton.setFont(new Font("Verdana", Font.BOLD, 14));
        cancelButton.setBackground(new Color(220, 53, 69));
        cancelButton.setForeground(Color.BLACK);
        cancelButton.addActionListener(e -> dispose());
        panel.add(cancelButton);
        
        // Status
        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Verdana", Font.ITALIC, 12));
        statusLabel.setForeground(Color.BLACK);
        panel.add(statusLabel);
        
        return panel;
    }
    
    private void addButton(JPanel parent, String text, java.awt.event.ActionListener listener, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Verdana", Font.PLAIN, 11));
        button.setBackground(bgColor);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.addActionListener(listener);
        parent.add(button);
    }
    
    private void loadTestData() {
        titleField.setText(existingTest.getTitle());
        priceField.setText(String.valueOf(existingTest.getPrice()));
        
        questionsModel.setRowCount(0);
        
        // Si es prueba existente, cargar preguntas existentes
        if (existingTest != null) {
            for (int i = 0; i < existingTest.getQuestions().size(); i++) {
                Question q = existingTest.getQuestions().get(i);
                questionsModel.addRow(new Object[]{
                    i + 1,
                    truncate(q.getQuestionText(), 50),
                    q.getType(),
                    q.getOptions().size() + " opciones",
                    q.getCorrectAnswer()
                });
            }
        }
        // Si es prueba nueva, cargar preguntas temporales
        else {
            for (int i = 0; i < tempQuestions.size(); i++) {
                Question q = tempQuestions.get(i);
                questionsModel.addRow(new Object[]{
                    i + 1,
                    truncate(q.getQuestionText(), 50),
                    q.getType(),
                    q.getOptions().size() + " opciones",
                    q.getCorrectAnswer()
                });
            }
        }
    }
    
    private void addQuestion() {
        new QuestionDialog(this, null, q -> {
            if (existingTest != null) {
                // Para prueba existente: agregar a la prueba
                existingTest.addQuestion(q);
            } else {
                // Para nueva prueba: agregar a la lista temporal
                tempQuestions.add(q);
            }
            
            // Agregar a la tabla para visualización
            questionsModel.addRow(new Object[]{
                questionsModel.getRowCount() + 1,
                truncate(q.getQuestionText(), 50),
                q.getType(),
                q.getOptions().size() + " opciones",
                q.getCorrectAnswer()
            });
        }).setVisible(true);
    }
    
    private void editQuestion() {
        int selectedRow = questionsTable.getSelectedRow();
        if (selectedRow == -1) {
            showError("Selecciona una pregunta para editar");
            return;
        }
        
        Question question;
        if (existingTest != null) {
            // Para prueba existente
            if (selectedRow >= existingTest.getQuestions().size()) {
                showError("Índice de pregunta inválido");
                return;
            }
            question = existingTest.getQuestions().get(selectedRow);
        } else {
            // Para nueva prueba
            if (selectedRow >= tempQuestions.size()) {
                showError("Índice de pregunta inválido");
                return;
            }
            question = tempQuestions.get(selectedRow);
        }
        
        new QuestionDialog(this, question, q -> {
            if (existingTest != null) {
                // Actualizar en prueba existente
                existingTest.getQuestions().set(selectedRow, q);
            } else {
                // Actualizar en lista temporal
                tempQuestions.set(selectedRow, q);
            }
            
            // Actualizar la tabla
            questionsModel.setValueAt(truncate(q.getQuestionText(), 50), selectedRow, 1);
            questionsModel.setValueAt(q.getType(), selectedRow, 2);
            questionsModel.setValueAt(q.getOptions().size() + " opciones", selectedRow, 3);
            questionsModel.setValueAt(q.getCorrectAnswer(), selectedRow, 4);
        }).setVisible(true);
    }
    
    private void deleteQuestion() {
        int selectedRow = questionsTable.getSelectedRow();
        if (selectedRow == -1) {
            showError("Selecciona una pregunta para eliminar");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "¿Eliminar pregunta " + (selectedRow + 1) + "?",
            "Confirmar",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (existingTest != null) {
                // Eliminar de prueba existente
                existingTest.getQuestions().remove(selectedRow);
            } else {
                // Eliminar de lista temporal
                tempQuestions.remove(selectedRow);
            }
            
            // Eliminar de la tabla
            questionsModel.removeRow(selectedRow);
            
            // Renumerar las filas restantes
            for (int i = 0; i < questionsModel.getRowCount(); i++) {
                questionsModel.setValueAt(i + 1, i, 0);
            }
            
            showSuccess("Pregunta eliminada");
        }
    }
    private void saveTest() {
        String title = titleField.getText().trim();
        if (title.isEmpty()) {
            showError("El título es obligatorio");
            return;
        }
        
        double price;
        try {
            price = Double.parseDouble(priceField.getText());
            if (price <= 0) price = 5000;
        } catch (NumberFormatException e) {
            price = 5000;
        }
        
        // Verificar si ya existe una prueba con el mismo título (solo para nuevas)
        if (existingTest == null) {
            boolean exists = appController.getTests().stream()
                .anyMatch(t -> t.getTitle().equalsIgnoreCase(title));
            if (exists) {
                showError("Ya existe una prueba con ese título");
                return;
            }
        }
        
        Test testToSave;
        
        if (existingTest == null) {
            // === CREAR NUEVA PRUEBA ===
            
            // 1. Verificar que tenga al menos una pregunta
            if (tempQuestions.isEmpty()) {
                showError("La prueba debe tener al menos una pregunta");
                return;
            }
            
            // 2. Crear la prueba
            testToSave = new Test(title);
            testToSave.setPrice(price);
            
            // 3. Agregar todas las preguntas de la lista temporal
            for (Question q : tempQuestions) {
                testToSave.addQuestion(q);
            }
            
            // 4. Agregar a la lista del AppController
            appController.getTests().add(testToSave);
            showSuccess("Prueba creada exitosamente: " + title);
            
        } else {
            // === EDITAR PRUEBA EXISTENTE ===
            testToSave = existingTest;
            
            // 1. Actualizar propiedades
            testToSave.setTitle(title);
            testToSave.setPrice(price);
            
            // 2. Verificar que tenga preguntas
            if (testToSave.getQuestions().isEmpty()) {
                showError("La prueba debe tener al menos una pregunta");
                return;
            }
            
            showSuccess("Prueba actualizada exitosamente: " + title);
        }
        
        // 5. GUARDAR TODOS LOS DATOS
        appController.saveAll();
        
        // 6. Cerrar el diálogo después de un breve retraso
        Timer timer = new Timer(1500, e -> {
            dispose();
            // Notificar al diálogo padre para que refresque
            if (getOwner() instanceof TestManagerDialog) {
                ((TestManagerDialog) getOwner()).loadTests();
            }
        });
        timer.setRepeats(false);
        timer.start();
    }
    
    private String truncate(String text, int length) {
        return text.length() > length ? text.substring(0, length - 3) + "..." : text;
    }
    
    private void showError(String message) {
        statusLabel.setText(message);
        statusLabel.setForeground(new Color(220, 53, 69));
    }
}

/**
 * Diálogo para crear/editar una pregunta individual con sus 4 opciones.
 */
class QuestionDialog extends JDialog {
    private final Question existingQuestion;
    private final java.util.function.Consumer<Question> onSave;
    
    private JTextArea questionArea;
    private JTextField[] optionFields;
    private JComboBox<String> correctCombo, typeCombo;
    private JLabel statusLabel;
    
    public QuestionDialog(JDialog owner, Question question, java.util.function.Consumer<Question> onSave) {
        super(owner, question == null ? "➕ Nueva Pregunta" : "✏️ Editar Pregunta", true);
        this.existingQuestion = question;
        this.onSave = onSave;
        
        setSize(700, 550);
        setLocationRelativeTo(owner);
        
        initComponents();
        
        if (existingQuestion != null) {
            loadQuestionData();
        }
    }
    
    private void initComponents() {
        GradientPanel panel = new GradientPanel(new Color(255, 218, 185), new Color(255, 179, 71));
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Panel superior: texto de la pregunta
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.WHITE), "Texto de la Pregunta", 0, 0, 
            new Font("Verdana", Font.BOLD, 12), Color.WHITE));
        
        questionArea = new JTextArea(3, 50);
        questionArea.setFont(new Font("Verdana", Font.PLAIN, 14));
        questionArea.setLineWrap(true);
        questionArea.setWrapStyleWord(true);
        topPanel.add(new JScrollPane(questionArea), BorderLayout.CENTER);
        
        panel.add(topPanel, BorderLayout.NORTH);
        
        // Panel central: opciones
        JPanel centerPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        centerPanel.setOpaque(false);
        centerPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.WHITE), "Opciones de Respuesta", 0, 0, 
            new Font("Verdana", Font.BOLD, 12), Color.WHITE));
        
        optionFields = new JTextField[4];
        String[] letters = {"A", "B", "C", "D"};
        
        for (int i = 0; i < 4; i++) {
            centerPanel.add(new JLabel("Opción " + letters[i] + ":"));
            optionFields[i] = new JTextField(20);
            centerPanel.add(optionFields[i]);
        }
        
        // Respuesta correcta
        centerPanel.add(new JLabel("Respuesta Correcta:"));
        correctCombo = new JComboBox<>(letters);
        centerPanel.add(correctCombo);
        
        // Tipo de pregunta
        centerPanel.add(new JLabel("Tipo:"));
        typeCombo = new JComboBox<>(new String[]{"logic", "math", "verbal", "general"});
        centerPanel.add(typeCombo);
        
        panel.add(centerPanel, BorderLayout.CENTER);
        
        // Panel inferior: botones
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        bottomPanel.setOpaque(false);
        
        JButton saveButton = new JButton("Guardar Pregunta");
        saveButton.setBackground(new Color(40, 167, 69));
        saveButton.setForeground(Color.BLACK);
        saveButton.addActionListener(e -> saveQuestion());
        bottomPanel.add(saveButton);
        
        JButton cancelButton = new JButton("Cancelar");
        cancelButton.setBackground(new Color(220, 53, 69));
        cancelButton.setForeground(Color.BLACK);
        cancelButton.addActionListener(e -> dispose());
        bottomPanel.add(cancelButton);
        
        // Status
        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Verdana", Font.ITALIC, 12));
        statusLabel.setForeground(Color.BLACK);
        bottomPanel.add(statusLabel);
        
        panel.add(bottomPanel, BorderLayout.SOUTH);
        
        setContentPane(panel);
    }
    
    private void loadQuestionData() {
        questionArea.setText(existingQuestion.getQuestionText());
        
        for (int i = 0; i < existingQuestion.getOptions().size(); i++) {
            optionFields[i].setText(existingQuestion.getOptions().get(i));
        }
        
        correctCombo.setSelectedItem(existingQuestion.getCorrectAnswer());
        typeCombo.setSelectedItem(existingQuestion.getType());
    }
    
    private void saveQuestion() {
        String text = questionArea.getText().trim();
        if (text.isEmpty()) {
            showError("El texto de la pregunta es obligatorio");
            return;
        }
        
        // Validar opciones
        java.util.List<String> options = new java.util.ArrayList<>();
        for (JTextField field : optionFields) {
            String opt = field.getText().trim();
            if (opt.isEmpty()) {
                showError("Todas las opciones deben estar llenas");
                return;
            }
            options.add(opt);
        }
        
        String correct = (String) correctCombo.getSelectedItem();
        String type = (String) typeCombo.getSelectedItem();
        
        try {
            Question question = new MultipleChoiceQuestion(text, options, correct, type);
            onSave.accept(question);
            dispose();
        } catch (Exception e) {
            showError("Error: " + e.getMessage());
        }
    }
    
    private void showError(String message) {
        statusLabel.setText(message);
        statusLabel.setForeground(new Color(220, 53, 69));
    }
}
