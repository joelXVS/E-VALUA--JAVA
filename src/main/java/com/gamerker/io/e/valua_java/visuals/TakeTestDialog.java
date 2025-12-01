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
            dispose();
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
        timerLabel = new JLabel("⏱️ 00:00:00");
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
        
        prevButton = createNavButton("⬅️ Anterior", e -> previousQuestion());
        prevButton.setEnabled(false);
        panel.add(prevButton);
        
        finishButton = createNavButton("✅ Finalizar", e -> finishTest());
        finishButton.setEnabled(false);
        finishButton.setBackground(COLOR_EXITO);
        panel.add(finishButton);
        
        nextButton = createNavButton("Siguiente ➡️", e -> nextQuestion());
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
                
                timerLabel.setText(String.format("⏱️ %02d:%02d:%02d", hours, minutes, seconds));
                
                // Alerta si supera 30 minutos
                if (elapsedTime.toMinutes() > 30) {
                    timerLabel.setForeground(Color.RED);
                    timerLabel.setText(timerLabel.getText() + " ⚠️");
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
            userAnswers
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
        String message = String.format("""
            <html>
            <h2>Resultado Final</h2>
            <p><b>Prueba:</b> %s</p>
            <p><b>Puntuación:</b> %d/%d (%.2f%%)</p>
            <p><b>Estado:</b> <font color='%s'>%s</font></p>
            <p><b>Tiempo:</b> %s</p>
            </html>
            """,
            result.getTestTitle(),
            result.getScore(),
            result.getTotal(),
            result.getPercentage(),
            result.getPercentage() >= 60 ? "green" : "red",
            status,
            elapsedTime != null ? String.format("%d min %d sec", 
                elapsedTime.toMinutes(), elapsedTime.getSeconds() % 60) : "N/A"
        );
        
        JOptionPane.showMessageDialog(this, message, "Resultado", 
            result.getPercentage() >= 60 ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE);
    }
}
