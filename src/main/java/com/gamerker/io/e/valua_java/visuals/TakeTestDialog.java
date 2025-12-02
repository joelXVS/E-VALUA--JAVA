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
import com.gamerker.io.e.valua_java.utils.*;

import javax.swing.*;
import java.awt.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Diálogo para realizar una prueba completa con:
 * - Selección de prueba
 * - Temporizador de sesión
 * - Visualización de preguntas
 * - Validación de respuestas
 * - Cálculo de resultado
 * - Cobro automático
 * - Exportación a PDF
 */
public class TakeTestDialog extends JDialog {
    private final User currentUser;
    private final AppController appController;
    private final BillingController billing;
    private final RechargeController recharge;
    private final DBController db;
    
    private JComboBox<String> testCombo;
    private JLabel timerLabel, questionLabel, statusLabel;
    private JPanel optionsPanel;
    private JButton nextButton, prevButton, finishButton;
    private JProgressBar progressBar;
    
    private List<Test> availableTests;
    private Test selectedTest;
    private List<String> userAnswers;
    private int currentQuestionIndex = 0;
    private LocalDateTime testStartTime;
    private Timer timer;
    private Duration elapsedTime;
    
    // Colores
    private final Color COLOR_FONDO = new Color(255, 218, 185);
    private final Color COLOR_BOTON = new Color(255, 140, 0);
    private final Color COLOR_EXITO = new Color(40, 167, 69);
    private final Color COLOR_ERROR = new Color(220, 53, 69);
    private final Color COLOR_INFO = new Color(23, 162, 184);
    
    public TakeTestDialog(JFrame owner, User user, AppController controller) {
        super(owner, "📝 Realizar Prueba", true);
        this.currentUser = user;
        this.appController = controller;
        this.billing = new BillingController();
        this.recharge = new RechargeController();
        this.db = new DBController();
        
        setSize(900, 650);
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        availableTests = appController.getTests();
        if (availableTests.isEmpty()) {
            JOptionPane.showMessageDialog(owner, "No hay pruebas disponibles.", "Error", JOptionPane.ERROR_MESSAGE);
            dispose(); dispose();
            return;
        }
        
        initComponents();
        loadTestSelection();
    }
    
    private void initComponents() {
        // Panel principal con gradiente
        GradientPanel mainPanel = new GradientPanel(COLOR_FONDO, new Color(255, 179, 71));
        mainPanel.setLayout(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Panel superior: selección de prueba y temporizador
        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        
        // Panel central: pregunta y opciones
        mainPanel.add(createQuestionPanel(), BorderLayout.CENTER);
        
        // Panel inferior: controles de navegación
        mainPanel.add(createNavigationPanel(), BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Combo de selección de prueba
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Selecciona prueba:"), gbc);
        
        gbc.gridx = 1;
        testCombo = new JComboBox<>();
        testCombo.setPreferredSize(new Dimension(300, 30));
        testCombo.addActionListener(e -> onTestSelected());
        panel.add(testCombo, gbc);
        
        // Temporizador
        gbc.gridx = 2;
        timerLabel = new JLabel("Tiempo: 00:00:00");
        timerLabel.setFont(new Font("Verdana", Font.BOLD, 18));
        timerLabel.setForeground(Color.RED);
        panel.add(timerLabel, gbc);
        
        // Barra de progreso
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setForeground(COLOR_INFO);
        panel.add(progressBar, gbc);
        
        return panel;
    }
    
    private JPanel createQuestionPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);
        
        // Etiqueta de la pregunta
        questionLabel = new JLabel("Selecciona una prueba para comenzar");
        questionLabel.setFont(new Font("Verdana", Font.BOLD, 16));
        questionLabel.setForeground(Color.BLACK);
        questionLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(questionLabel, BorderLayout.NORTH);
        
        // Panel de opciones
        optionsPanel = new JPanel();
        optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
        optionsPanel.setOpaque(false);
        optionsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JScrollPane scrollPane = new JScrollPane(optionsPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createNavigationPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panel.setOpaque(false);
        
        prevButton = createNavButton("Anterior", e -> previousQuestion());
        prevButton.setEnabled(false);
        panel.add(prevButton);
        
        finishButton = createNavButton("Finalizar", e -> finishTest());
        finishButton.setEnabled(false);
        finishButton.setBackground(COLOR_EXITO);
        panel.add(finishButton);
        
        nextButton = createNavButton("Siguiente️", e -> nextQuestion());
        nextButton.setEnabled(false);
        panel.add(nextButton);
        
        // Etiqueta de estado
        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Verdana", Font.ITALIC, 12));
        statusLabel.setForeground(Color.BLACK);
        panel.add(statusLabel);
        
        return panel;
    }
    
    private JButton createNavButton(String text, java.awt.event.ActionListener listener) {
        JButton button = new JButton(text);
        button.setFont(new Font("Verdana", Font.BOLD, 14));
        button.setBackground(COLOR_BOTON);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(150, 35));
        button.addActionListener(listener);
        return button;
    }
    
    private void loadTestSelection() {
        testCombo.removeAllItems();
        for (Test test : availableTests) {
            testCombo.addItem(String.format("%s - $%,.0f", test.getTitle(), test.getPrice()));
        }
    }
    
    private void onTestSelected() {
        int selectedIndex = testCombo.getSelectedIndex();
        if (selectedIndex >= 0) {
            selectedTest = availableTests.get(selectedIndex);
            startTest();
        }
    }
    
    private void startTest() {
        // Verificar saldo y espacio
        if (currentUser.getBalance() < 0) {
            JOptionPane.showMessageDialog(this, 
                "❌ SALDO NEGATIVO: No puedes realizar pruebas hasta recargar.", 
                "Error", JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }
        
        if (!recharge.hasSufficientBalance(currentUser, selectedTest.getPrice())) {
            JOptionPane.showMessageDialog(this, 
                "❌ Saldo insuficiente.\n" + recharge.getBalanceStatus(currentUser), 
                "Error", JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }
        
        // Confirmar inicio
        int confirm = JOptionPane.showConfirmDialog(this,
            String.format("Iniciar prueba '%s'?\nPrecio: $%,.0f\nTiempo estimado: ~%d min",
                selectedTest.getTitle(),
                selectedTest.getPrice(),
                selectedTest.getTotalQuestions() * 2),
            "Confirmar Prueba",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        
        // Inicializar datos de la prueba
        userAnswers = new ArrayList<>();
        for (int i = 0; i < selectedTest.getTotalQuestions(); i++) {
            userAnswers.add(""); // Respuestas vacías inicialmente
        }
        currentQuestionIndex = 0;
        testStartTime = LocalDateTime.now();
        
        // Cobrar la prueba
        Transaction charge = billing.chargeForTest(currentUser.getUsername(), selectedTest);
        currentUser.addTransaction(charge);
        appController.getTransactions().add(charge);
        appController.saveAll();
        
        // Iniciar temporizador
        startTimer();
        
        // Mostrar primera pregunta
        showQuestion(currentQuestionIndex);
        
        // Habilitar controles
        nextButton.setEnabled(true);
        finishButton.setEnabled(true);
        testCombo.setEnabled(false);
    }
    
    private void startTimer() {
        timer = new Timer(1000, e -> {
            if (testStartTime != null) {
                elapsedTime = Duration.between(testStartTime, LocalDateTime.now());
                long hours = elapsedTime.toHours();
                long minutes = elapsedTime.toMinutes() % 60;
                long seconds = elapsedTime.getSeconds() % 60;
                
                timerLabel.setText(String.format("Tiempo:️ %02d:%02d:%02d", hours, minutes, seconds));
                
                // Alerta si supera 30 minutos
                if (elapsedTime.toMinutes() > 30) {
                    timerLabel.setForeground(Color.RED);
                    timerLabel.setText(timerLabel.getText());
                }
            }
        });
        timer.start();
    }
    
    private void showQuestion(int index) {
        if (index < 0 || index >= selectedTest.getTotalQuestions()) return;
        
        Question question = selectedTest.getQuestions().get(index);
        
        // Actualizar etiqueta de pregunta
        questionLabel.setText(String.format("Pregunta %d/%d: %s", 
            index + 1, selectedTest.getTotalQuestions(), question.getQuestionText()));
        
        // Limpiar y crear opciones
        optionsPanel.removeAll();
        ButtonGroup group = new ButtonGroup();
        
        char letter = 'A';
        for (String option : question.getOptions()) {
            JRadioButton radioButton = new JRadioButton(String.format("%c) %s", letter, option));
            radioButton.setActionCommand(String.valueOf(letter));
            radioButton.setFont(new Font("Verdana", Font.PLAIN, 14));
            radioButton.setOpaque(false);
            radioButton.setForeground(Color.BLACK);
            
            // Seleccionar respuesta guardada
            if (userAnswers.get(index).equals(String.valueOf(letter))) {
                radioButton.setSelected(true);
            }
            
            radioButton.addActionListener(e -> userAnswers.set(index, e.getActionCommand()));
            group.add(radioButton);
            optionsPanel.add(radioButton);
            optionsPanel.add(Box.createVerticalStrut(5));
            
            letter++;
        }
        
        // Actualizar barra de progreso
        progressBar.setValue((int)((index + 1) * 100.0 / selectedTest.getTotalQuestions()));
        progressBar.setString(String.format("%d/%d", index + 1, selectedTest.getTotalQuestions()));
        
        // Actualizar botones de navegación
        prevButton.setEnabled(index > 0);
        nextButton.setEnabled(index < selectedTest.getTotalQuestions() - 1);
        
        optionsPanel.revalidate();
        optionsPanel.repaint();
    }
    
    private void nextQuestion() {
        if (currentQuestionIndex < selectedTest.getTotalQuestions() - 1) {
            currentQuestionIndex++;
            showQuestion(currentQuestionIndex);
        }
    }
    
    private void previousQuestion() {
        if (currentQuestionIndex > 0) {
            currentQuestionIndex--;
            showQuestion(currentQuestionIndex);
        }
    }
    
    private void finishTest() {
        // Confirmar finalización
        int confirm = JOptionPane.showConfirmDialog(this,
            "¿Finalizar la prueba? No podrás cambiar tus respuestas.",
            "Finalizar",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm != JOptionPane.YES_OPTION) return;
        
        // Detener temporizador
        if (timer != null) {
            timer.stop();
            elapsedTime = Duration.between(testStartTime, LocalDateTime.now());
        }
        
        // Calcular resultado
        int correctCount = 0;
        for (int i = 0; i < selectedTest.getQuestions().size(); i++) {
            Question q = selectedTest.getQuestions().get(i);
            String answer = userAnswers.get(i);
            if (q.verifyAnswer(answer)) {
                correctCount++;
            }
        }
        
        double percentage = selectedTest.getTotalQuestions() > 0 ? 
            (correctCount * 100.0 / selectedTest.getTotalQuestions()) : 0;
        
        // Crear resultado
        Result result = new Result(
            currentUser.getUsername(),
            selectedTest.getTitle(),
            correctCount,
            selectedTest.getTotalQuestions(),
            percentage,
            userAnswers,
            elapsedTime
        );
        
        appController.getResults().add(result);
        appController.saveAll();
        
        // Mostrar resultado
        showResult(result);
        
        // Preguntar por exportación a PDF
        if (JOptionPane.showConfirmDialog(this,
            "¿Deseas exportar el resultado a PDF por $2.000?",
            "Exportar PDF",
            JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            
            try {
                Transaction pdfCharge = billing.chargeForPdfExport(currentUser.getUsername(), result.getTestTitle());
                currentUser.addTransaction(pdfCharge);
                appController.getTransactions().add(pdfCharge);
                appController.saveAll();
                
                String pdfPath = new ResultPdfController().exportResultToPdf(result, currentUser);
                JOptionPane.showMessageDialog(this, "PDF generado: " + pdfPath, "Exportado", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error al exportar: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        
        dispose();
    }
    
    private void showResult(Result result) {
        String status = result.getPercentage() >= 60 ? "APROBADO" : "NO APROBADO";
        String color = result.getPercentage() >= 60 ? "#2E7D32" : "#C62828";
        String bgColor = result.getPercentage() >= 60 ? "#E8F5E9" : "#FFEBEE";
        String icon = result.getPercentage() >= 60 ? "🎉" : "📝";

        // Crear un panel principal con scroll
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        // Panel de contenido que irá dentro del scroll
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Panel superior con título e icono
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBackground(Color.WHITE);
        topPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        iconLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0)); // 5px arriba

        JLabel titleLabel = new JLabel("RESULTADO DE LA PRUEBA");
        titleLabel.setFont(new Font("Verdana", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setForeground(new Color(33, 33, 33));

        topPanel.add(iconLabel, BorderLayout.WEST);
        topPanel.add(titleLabel, BorderLayout.CENTER);

        // Panel central con datos - ahora dentro del scroll
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // Información de la prueba en paneles expandibles
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                "Información de la Prueba",
                javax.swing.border.TitledBorder.LEFT,
                javax.swing.border.TitledBorder.TOP,
                new Font("Verdana", Font.BOLD, 13),
                new Color(66, 66, 66)
            ),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        addInfoRow(infoPanel, "Prueba:", result.getTestTitle());
        addInfoRow(infoPanel, "Fecha:", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        addInfoRow(infoPanel, "Duración:", result.getFormattedTime());

        // Panel de puntuación
        JPanel scorePanel = new JPanel();
        scorePanel.setLayout(new BoxLayout(scorePanel, BoxLayout.Y_AXIS));
        scorePanel.setBackground(Color.WHITE);
        scorePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                "Puntuación",
                javax.swing.border.TitledBorder.LEFT,
                javax.swing.border.TitledBorder.TOP,
                new Font("Verdana", Font.BOLD, 13),
                new Color(66, 66, 66)
            ),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        // Mostrar puntuación en texto grande
        JLabel scoreTextLabel = new JLabel(String.format("%d / %d", result.getScore(), result.getTotal()));
        scoreTextLabel.setFont(new Font("Verdana", Font.BOLD, 36));
        scoreTextLabel.setForeground(getPercentageColor(result.getPercentage()));
        scoreTextLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        scorePanel.add(scoreTextLabel);

        scorePanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Barra de progreso
        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setValue((int) result.getPercentage());
        progressBar.setString(String.format("%.1f%%", result.getPercentage()));
        progressBar.setStringPainted(true);
        progressBar.setFont(new Font("Verdana", Font.BOLD, 14));
        progressBar.setForeground(getPercentageColor(result.getPercentage()));
        progressBar.setBackground(new Color(240, 240, 240));
        progressBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(2, 2, 2, 2)
        ));
        progressBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        progressBar.setAlignmentX(Component.CENTER_ALIGNMENT);

        scorePanel.add(progressBar);

        // Etiqueta de interpretación
        JLabel interpretationLabel = new JLabel(getInterpretation(result.getPercentage()));
        interpretationLabel.setFont(new Font("Verdana", Font.ITALIC, 12));
        interpretationLabel.setForeground(new Color(97, 97, 97));
        interpretationLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        interpretationLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        scorePanel.add(interpretationLabel);

        // Panel de estado destacado
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBackground(Color.decode(bgColor));
        statusPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.decode(color), 3),
            BorderFactory.createEmptyBorder(20, 30, 20, 30)
        ));
        statusPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        JLabel statusLabel = new JLabel(status, SwingConstants.CENTER);
        statusLabel.setFont(new Font("Verdana", Font.BOLD, 22));
        statusLabel.setForeground(Color.decode(color));
        statusPanel.add(statusLabel, BorderLayout.CENTER);

        // Panel de recomendaciones (expandible)
        JPanel recommendationsPanel = new JPanel();
        recommendationsPanel.setLayout(new BoxLayout(recommendationsPanel, BoxLayout.Y_AXIS));
        recommendationsPanel.setBackground(Color.WHITE);
        recommendationsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                "Recomendaciones",
                javax.swing.border.TitledBorder.LEFT,
                javax.swing.border.TitledBorder.TOP,
                new Font("Verdana", Font.BOLD, 13),
                new Color(66, 66, 66)
            ),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        String recommendation = result.getPercentage() >= 60 ? 
            "¡Excelente trabajo! Continúa practicando para mejorar aún más." :
            "Te recomendamos revisar los temas y realizar más ejercicios de práctica.";

        JTextArea recommendationText = new JTextArea(recommendation);
        recommendationText.setFont(new Font("Verdana", Font.PLAIN, 12));
        recommendationText.setForeground(new Color(97, 97, 97));
        recommendationText.setLineWrap(true);
        recommendationText.setWrapStyleWord(true);
        recommendationText.setEditable(false);
        recommendationText.setBackground(new Color(250, 250, 250));
        recommendationText.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        recommendationsPanel.add(recommendationText);

        // Añadir todos los paneles al contenido
        contentPanel.add(topPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        contentPanel.add(infoPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        contentPanel.add(scorePanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        contentPanel.add(statusPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        contentPanel.add(recommendationsPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Panel inferior con botones
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setBackground(new Color(245, 245, 245));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 20, 0));

        JButton closeButton = createStyledButton("Cerrar", Color.decode("#4CAF50"));
        closeButton.addActionListener(e -> SwingUtilities.getWindowAncestor(mainPanel).dispose());

        // Centrar el botón
        closeButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        bottomPanel.add(Box.createVerticalStrut(10));
        bottomPanel.add(closeButton);
        bottomPanel.add(Box.createVerticalStrut(10));
    
        // Añadir scroll al panel de contenido
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        // Ensamblar todo
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        // Crear y mostrar el diálogo
        JDialog dialog = new JDialog(this, "📋 Resultado Final", true);
        dialog.setContentPane(mainPanel);
        dialog.setSize(600, 490); // Tamaño más grande
        dialog.setMinimumSize(new Dimension(500, 400));
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    // Método auxiliar para añadir filas de información
    private void addInfoRow(JPanel parent, String labelText, String value) {
        JPanel rowPanel = new JPanel(new BorderLayout(10, 5));
        rowPanel.setBackground(Color.WHITE);
        rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Verdana", Font.BOLD, 12));
        label.setForeground(new Color(66, 66, 66));
        label.setPreferredSize(new Dimension(100, 20));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Verdana", Font.PLAIN, 12));
        valueLabel.setForeground(new Color(97, 97, 97));

        rowPanel.add(label, BorderLayout.WEST);
        rowPanel.add(valueLabel, BorderLayout.CENTER);

        parent.add(rowPanel);
        parent.add(Box.createRigidArea(new Dimension(0, 5)));
    }

    // Método para obtener interpretación del porcentaje
    private String getInterpretation(double percentage) {
        if (percentage >= 90) return "¡Excelente! Dominio total del tema.";
        if (percentage >= 80) return "Muy bueno. Buen dominio del tema.";
        if (percentage >= 70) return "Buen trabajo. Comprensión adecuada.";
        if (percentage >= 60) return "Aprobado. Puedes mejorar con práctica.";
        if (percentage >= 50) return "Regular. Necesitas repasar los conceptos.";
        if (percentage >= 40) return "Insuficiente. Requiere estudio adicional.";
        return "Muy bajo. Se recomienda estudiar desde lo básico.";
    }

    // Método auxiliar para obtener color según porcentaje
    private Color getPercentageColor(double percentage) {
        if (percentage >= 90) return new Color(0, 150, 136); // Verde turquesa
        if (percentage >= 80) return new Color(76, 175, 80); // Verde
        if (percentage >= 70) return new Color(139, 195, 74); // Verde claro
        if (percentage >= 60) return new Color(205, 220, 57); // Lima
        if (percentage >= 50) return new Color(255, 193, 7); // Ámbar
        if (percentage >= 40) return new Color(255, 152, 0); // Naranja
        return new Color(244, 67, 54); // Rojo
    }

    // Método auxiliar para crear botones estilizados
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Verdana", Font.BOLD, 13));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(bgColor.darker(), 1),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Efecto hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.darker());
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }
}
