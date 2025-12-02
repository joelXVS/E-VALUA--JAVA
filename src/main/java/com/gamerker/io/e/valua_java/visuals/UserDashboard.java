/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gamerker.io.e.valua_java.visuals;
import com.gamerker.io.e.valua_java.controllersPack.*;
import com.gamerker.io.e.valua_java.mainClasses.*;
import com.gamerker.io.e.valua_java.utils.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
/**
 *
 * @author hp
 */
/**
 * Dashboard del usuario logueado con diseño de mosaicos tipo Windows.
 * Muestra íconos grandes, descripción breve y datos del usuario.
 * Se reutiliza para todos los roles (Student, Teacher, Admin).
 */
public class UserDashboard extends JFrame {
    private final AppController appController;
    private final User currentUser;
    private final BillingController billing;
    private final RechargeController recharge;
    private final DBController db;
    
    // Colores del tema
    private final Color COLOR_FONDO = new Color(255, 218, 185);
    private final Color COLOR_USER_PANEL = new Color(255, 140, 0, 180);
    private final Color COLOR_BOTON = new Color(255, 140, 0);
    private final Color COLOR_BOTON_HOVER = new Color(255, 165, 0);
    
    // Fuente para emojis
    private Font emojiFont;
    
    public UserDashboard(AppController appController, User currentUser) {
        this.appController = appController;
        this.currentUser = currentUser;
        this.billing = new BillingController();
        this.recharge = new RechargeController();
        this.db = new DBController();
        
        // Inicializar fuente para emojis
        initializeEmojiFont();
        
        // Actualizar actividad de sesión
        appController.saveActiveSession();
        
        // Configurar ventana
        setTitle("E-VALUA - Dashboard: " + currentUser.getDisplayName());
        setSize(1280, 720);
        setMinimumSize(new Dimension(1280, 720));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Panel principal con gradiente
        GradientPanel mainPanel = new GradientPanel(COLOR_FONDO, new Color(255, 179, 71));
        mainPanel.setLayout(new BorderLayout());
        
        // Panel superior con datos del usuario
        mainPanel.add(createUserPanel(), BorderLayout.NORTH);
        
        // Panel central con mosaicos (scrollable)
        JScrollPane scrollPane = new JScrollPane(createTilesPanel());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Panel inferior con botón de cerrar sesión
        mainPanel.add(createLogoutPanel(), BorderLayout.SOUTH);
        
        add(mainPanel);
        
        // Registrar inicio de sesión para facturación
        billing.startSession(currentUser.getUsername());
    }
    
    /**
     * Inicializa la fuente para soportar emojis Unicode
     */
    private void initializeEmojiFont() {
        // Primero intentar con fuentes comunes que soportan emojis bien
        String[] emojiFonts = {
            "Segoe UI Emoji",
            "Apple Color Emoji",
            "Noto Color Emoji",
            "Segoe UI Symbol",
            "DejaVu Sans",
            "Arial Unicode MS",
            "Symbola",
            "EmojiOne Color",
            "Twemoji Mozilla"
        };
        
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] availableFonts = ge.getAvailableFontFamilyNames();
        
        for (String fontName : emojiFonts) {
            for (String available : availableFonts) {
                if (available.equalsIgnoreCase(fontName)) {
                    emojiFont = new Font(fontName, Font.PLAIN, 48);
                    System.out.println("Fuente de emojis seleccionada: " + fontName);
                    return;
                }
            }
        }
        
        // Si no encuentra fuentes específicas, usa la fuente por defecto pero con un tamaño mayor
        emojiFont = new Font(Font.SANS_SERIF, Font.PLAIN, 48);
        System.out.println("Usando fuente por defecto para emojis");
    }
    
    /**
     * Panel superior con foto/ícono de usuario y datos principales
     */
    private JPanel createUserPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Ícono de usuario según rol con emoji Unicode
        String emojiIcon = switch (currentUser.getRole()) {
            case "admin" -> "👑";
            case "teacher" -> "🎓";
            default -> "👤"; // student
        };
        
        JLabel userIcon = new JLabel(emojiIcon);
        userIcon.setFont(emojiFont.deriveFont(48f)); // Usar fuente de emojis
        userIcon.setForeground(Color.BLACK);
        
        // Añadir margen superior al JLabel para que los emojis no se corten
        userIcon.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0)); // 5px arriba
        panel.add(userIcon);
        
        // Datos del usuario
        JPanel dataPanel = new JPanel();
        dataPanel.setLayout(new BoxLayout(dataPanel, BoxLayout.Y_AXIS));
        dataPanel.setOpaque(false);
        
        JLabel nameLabel = new JLabel(currentUser.getDisplayName());
        nameLabel.setFont(new Font("Verdana", Font.BOLD, 20));
        nameLabel.setForeground(Color.BLACK);
        dataPanel.add(nameLabel);
        
        JLabel roleLabel = new JLabel("Rol: " + currentUser.getRole().toUpperCase());
        roleLabel.setFont(new Font("Verdana", Font.PLAIN, 14));
        roleLabel.setForeground(Color.BLACK);
        dataPanel.add(roleLabel);
            
        // Saldo y crédito con emoji
        JLabel balanceLabel = new JLabel(String.format("Saldo: $%,.0f | %s", 
            currentUser.getBalance(), recharge.getBalanceStatus(currentUser)));
        balanceLabel.setFont(new Font("Verdana", Font.PLAIN, 12));
        balanceLabel.setForeground(Color.BLACK);
        dataPanel.add(balanceLabel);
        
        // Fecha de login con emoji
        JLabel dateLabel = new JLabel("Login: " + LocalDateTime.now().format(
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        dateLabel.setFont(new Font("Verdana", Font.ITALIC, 11));
        dateLabel.setForeground(Color.BLACK);
        dataPanel.add(dateLabel);
        
        panel.add(dataPanel);
        
        // Espacio para meter el botón de cerrar sesión en la esquina superior derecha
        panel.add(Box.createHorizontalGlue());
        
        return panel;
    }
    
    /**
     * Panel central con mosaicos de funciones (íconos grandes + texto)
     * Se muestran diferentes opciones según el rol del usuario
     */
    private JPanel createTilesPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 4, 15, 15)); // 4 columnas, automático
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // ===== TILES PARA TODOS LOS ROLES =====
        addTile(panel, "📋", "Ver Pruebas", "Explora todas las pruebas disponibles", "1");
        addTile(panel, "📝", "Realizar Prueba", "Inicia una nueva evaluación", "2");
        addTile(panel, "📊", "Mis Resultados", "Revisa tus resultados recientes", "3");
        addTile(panel, "💰", "Recargar Saldo", "Añade fondos a tu cuenta", "4");
        addTile(panel, "📄", "Factura del Día", "Genera tu factura de consumo", "5");
        addTile(panel, "🏆", "Ranking", "Top estudiantes del sistema", "6");
        addTile(panel, "🗄️", "Almacenamiento", "Gestiona tu espacio de resultados", "7");
        
        // ===== TILES EXCLUSIVOS PARA PROFESORES =====
        if (currentUser.getRole().equals("teacher")) {
            addTile(panel, "➕", "Crear Prueba", "Diseña nuevas evaluaciones", "8");
            addTile(panel, "📈", "Estadísticas", "Ver estadísticas de pruebas", "10");
            addTile(panel, "👨‍🏫", "Mis Estudiantes", "Ver progreso de estudiantes", "11");
        }
        
        // ===== TILES EXCLUSIVOS PARA ADMINISTRADORES =====
        if (currentUser.getRole().equals("admin")) {
            addTile(panel, "➕", "Crear Prueba", "Diseña nuevas evaluaciones", "8");
            addTile(panel,"🙍‍️", "Gestión de Usuarios", "Administra usuarios del sistema", "9");
            addTile(panel, "📈", "Estadísticas", "Ver estadísticas del sistema", "10");
            addTile(panel, "🔧", "Configuración", "Configurar sistema", "12");
            addTile(panel, "📋", "Reportes", "Generar reportes del sistema", "13");
            addTile(panel, "💾", "Backup", "Respaldar datos del sistema", "14");
        }
        
        return panel;
    }
    
    /**
     * Crea un mosaico individual (ícono + texto + descripción)
     */
    private void addTile(JPanel parent, String emoji, String title, String description, String actionCommand) {
        JPanel tile = new JPanel();
        tile.setLayout(new BoxLayout(tile, BoxLayout.Y_AXIS));
        tile.setBackground(COLOR_BOTON);
        tile.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 165, 0), 2),
            BorderFactory.createEmptyBorder(15, 10, 15, 10)
        ));
        tile.setCursor(new Cursor(Cursor.HAND_CURSOR));
        tile.setPreferredSize(new Dimension(200, 200));
        
        // Hover effect
        tile.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                tile.setBackground(COLOR_BOTON_HOVER);
                tile.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(255, 120, 0), 3),
                    BorderFactory.createEmptyBorder(15, 10, 15, 10)
                ));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                tile.setBackground(COLOR_BOTON);
                tile.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(255, 165, 0), 2),
                    BorderFactory.createEmptyBorder(15, 10, 15, 10)
                ));
            }
        });
        
        // Hacer el tile clicable
        tile.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                handleTileClick(actionCommand);
            }
        });
        
        // Espaciador
        tile.add(Box.createRigidArea(new Dimension(0, 30)));
        
        // Ícono con emoji Unicode
        JLabel iconLabel = new JLabel(emoji, SwingConstants.CENTER);
        iconLabel.setFont(emojiFont.deriveFont(36f));
        iconLabel.setForeground(Color.BLACK);
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Añadir margen superior al JLabel para que los emojis no se corten
        iconLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0)); // 5px arriba

        tile.add(iconLabel);
        
        // Espaciador
        tile.add(Box.createRigidArea(new Dimension(0, 5)));
        
        // Título
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Verdana", Font.BOLD, 16));
        titleLabel.setForeground(Color.BLACK);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        tile.add(titleLabel);
        
        // Espaciador
        tile.add(Box.createRigidArea(new Dimension(0, 5)));
        
        // Descripción
        JLabel descLabel = new JLabel("<html><center>" + description + "</center></html>", SwingConstants.CENTER);
        descLabel.setFont(new Font("Verdana", Font.PLAIN, 11));
        descLabel.setForeground(Color.BLACK);
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        descLabel.setMaximumSize(new Dimension(180, 60));
        descLabel.setPreferredSize(new Dimension(180, 60));
        tile.add(descLabel);
        
        parent.add(tile);
    }
    
    /**
     * Panel inferior con botón grande de cerrar sesión
     */
    private JPanel createLogoutPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        JButton logoutButton = new JButton("🚪 Cerrar Sesión");
        logoutButton.setFont(emojiFont.deriveFont(16f)); // Usar fuente de emojis para el ícono
        logoutButton.setBackground(new Color(220, 53, 69)); // Rojo suave
        logoutButton.setForeground(Color.BLACK);
        logoutButton.setFocusPainted(false);
        logoutButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 30, 40), 2),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        logoutButton.setPreferredSize(new Dimension(200, 45));
        logoutButton.addActionListener(e -> handleLogout());
        
        // Efecto hover para el botón
        logoutButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                logoutButton.setBackground(new Color(200, 35, 51));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                logoutButton.setBackground(new Color(220, 53, 69));
            }
        });
        
        panel.add(logoutButton);
        return panel;
    }
    
    /**
     * Maneja el clic en un mosaico según el comando de acción
     */
    private void handleTileClick(String actionCommand) {
        switch (actionCommand) {
            case "1" -> mostrarPruebasDisponibles();
            case "2" -> realizarPrueba();
            case "3" -> mostrarResultados();
            case "4" -> recargarSaldo();
            case "5" -> generarFactura();
            case "6" -> mostrarRankingPanel();
            case "7" -> gestionarAlmacenamiento();
            case "8" -> crearPrueba();
            case "9" -> gestionarUsuarios();
            case "10" -> mostrarEstadisticas();
            case "11" -> gestionarEstudiantes();
            case "12" -> mostrarConfiguracion();
            case "13" -> generarReportes();
            case "14" -> realizarBackup();
            default -> JOptionPane.showMessageDialog(this, "Opción no implementada", "Información", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    // ==================== MÉTODOS DE FUNCIONALIDAD ====================
    
    private void mostrarPruebasDisponibles() {
        List<Test> tests = appController.getTests();
        if (tests.isEmpty()) {
            JOptionPane.showMessageDialog(this, "📭 No hay pruebas disponibles.", "Pruebas", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        StringBuilder sb = new StringBuilder("📋 PRUEBAS DISPONIBLES 📋\n\n");
        for (int i = 0; i < tests.size(); i++) {
            Test t = tests.get(i);
            String emoji;
            
            if (t.getTitle().toLowerCase().contains("lógica")) {
                emoji = "🤓";
            } else if (t.getTitle().toLowerCase().contains("matemática") || t.getTitle().toLowerCase().contains("matematica")) {
                emoji = "🔢";
            } else if (t.getTitle().toLowerCase().contains("verbal")) {
                emoji = "🗣️";
            } else {
                emoji = "📝";
            }
            
            sb.append(String.format("%s %d. %s - $%,.0f\n", emoji, i + 1, t.getTitle(), t.getPrice()));
        }
        
        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(600, 400));
        
        JOptionPane.showMessageDialog(this, scrollPane, "📋 Pruebas Disponibles", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void realizarPrueba() {
        TakeTestDialog takeTest = new TakeTestDialog(this, currentUser, appController);
        takeTest.setVisible(true);
    }
    
    private void mostrarResultados() {
        ResultsDialog resultDg = new ResultsDialog(this, currentUser, appController);
        resultDg.setVisible(true);
    }
    
    private void recargarSaldo() {
        RechargeDialog dialog = new RechargeDialog(this, currentUser, appController);
        dialog.setVisible(true);
    }
    
    private void generarFactura() {
        LocalDateTime today = LocalDateTime.now();
        List<Transaction> daily = billing.getDailyTransactions(currentUser.getUsername(), today);
        
        if (daily.isEmpty()) {
            JOptionPane.showMessageDialog(this, "📭 No hay consumos hoy.", "Factura", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        String fileName = new InvoicePdfController().generateInvoice(currentUser, daily, today);
        if (fileName != null) {
            JOptionPane.showMessageDialog(this, "✅ Factura generada correctamente.", "Factura", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "❌ Error al generar factura.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void mostrarRankingPanel() {
        RankingDialog rankingDg = new RankingDialog(this, currentUser, appController);
        rankingDg.setVisible(true);
    }
    
    private void gestionarAlmacenamiento() {
        StorageManagerDialog storageMG = new StorageManagerDialog(this, currentUser, appController);
        storageMG.setVisible(true);
    }
    
    private void crearPrueba() {
        if (!currentUser.getRole().equals("teacher") && !currentUser.getRole().equals("admin")) {
            JOptionPane.showMessageDialog(this, "❌ Acceso denegado.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        TestManagerDialog testCreator = new TestManagerDialog(this, currentUser, appController);
        testCreator.setVisible(true);
    }
    
    private void gestionarUsuarios() {
        if (!currentUser.getRole().equals("admin")) {
            JOptionPane.showMessageDialog(this, "❌ Acceso denegado.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        ManageUsersDialog usuariosMG = new ManageUsersDialog(this, currentUser, appController);
        usuariosMG.setVisible(true);
    }
    
    private void mostrarEstadisticas() {
        if (!currentUser.getRole().equals("teacher") && !currentUser.getRole().equals("admin")) {
            JOptionPane.showMessageDialog(this, "❌ Acceso denegado.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        StatisticsDialog statics = new StatisticsDialog(this, currentUser, appController);
        statics.setVisible(true);
    }
    
    private void gestionarEstudiantes() {
        if (!currentUser.getRole().equals("teacher")) {
            JOptionPane.showMessageDialog(this, "❌ Acceso denegado.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        ManageStudentsDialog studentsManager = new ManageStudentsDialog(this, currentUser, appController);
        studentsManager.setVisible(true);
    }
    
    private void mostrarConfiguracion() {
        if (!currentUser.getRole().equals("admin")) {
            JOptionPane.showMessageDialog(this, "❌ Acceso denegado.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        ConfigurationDialog settings = new ConfigurationDialog(this, currentUser, appController);
        settings.setVisible(true);
    }
    
    private void generarReportes() {
        if (!currentUser.getRole().equals("admin")) {
            JOptionPane.showMessageDialog(this, "❌ Acceso denegado.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        ReportsDialog reportes = new ReportsDialog(this, currentUser, appController);
        reportes.setVisible(true);
    }
    
    private void realizarBackup() {
        if (!currentUser.getRole().equals("admin")) {
            JOptionPane.showMessageDialog(this, "❌ Acceso denegado.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        BackupDialog doBackup = new BackupDialog(this, currentUser, appController);
        doBackup.setVisible(true);
    }
    
    /**
     * Maneja el cierre de sesión con confirmación
     */
    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "¿Desea cerrar sesión?", "🔒 Confirmar cierre", 
            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            appController.saveAll();
            
            appController.clearActiveSession();
            
            // Volver al login
            dispose();
            SwingUtilities.invokeLater(() -> {
                MainFrame mainFrame = new MainFrame(appController);
                mainFrame.setVisible(true);
            });
        }
    }
    
    /**
     * Método para obtener la fuente de emojis con tamaño específico
     */
    public Font getEmojiFont(float size) {
        if (emojiFont != null) {
            return emojiFont.deriveFont(size);
        }
        return new Font(Font.SANS_SERIF, Font.PLAIN, (int)size);
    }
}