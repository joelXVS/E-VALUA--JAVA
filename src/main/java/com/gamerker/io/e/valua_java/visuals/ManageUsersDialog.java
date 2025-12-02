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
import com.gamerker.io.e.valua_java.mainClasses.Admin;
import com.gamerker.io.e.valua_java.mainClasses.Student;
import com.gamerker.io.e.valua_java.mainClasses.Teacher;
import com.gamerker.io.e.valua_java.mainClasses.User;
import com.gamerker.io.e.valua_java.utils.*;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Diálogo de gestión de usuarios para administradores.
 * Permite: Listar, Crear, Editar, Eliminar y Ver detalles de usuarios.
 * Solo usuarios con rol 'admin' pueden acceder.
 */
public class ManageUsersDialog extends JDialog {
    private User currentUser;
    private AppController appController;
    
    private JTable usersTable;
    private DefaultTableModel tableModel;
    private JLabel statusLabel, userCountLabel;
    private JButton createButton, editButton, deleteButton, detailsButton, refreshButton;
    
    // Colores
    private final Color COLOR_FONDO = new Color(255, 218, 185);
    private final Color COLOR_BOTON = new Color(255, 140, 0);
    private final Color COLOR_BOTON_HOVER = new Color(255, 165, 0);
    private final Color COLOR_EXITO = new Color(40, 167, 69);
    private final Color COLOR_ERROR = new Color(220, 53, 69);
    
    public ManageUsersDialog(JFrame owner, User user, AppController controller) {
        super(owner, "⚙️ Gestión de Usuarios (Administrador)", true);
        
        // Verificar permisos
        if (!"admin".equals(user.getRole())) {
            JOptionPane.showMessageDialog(owner, "❌ Acceso denegado. Requiere rol de Administrador.", 
                "Error de Permisos", JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }
        
        this.currentUser = user;
        this.appController = controller;
        
        setSize(1100, 700);
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        initComponents();
        loadUsers();
    }
    
    private void initComponents() {
        // Panel principal con gradiente
        GradientPanel mainPanel = new GradientPanel(COLOR_FONDO, new Color(255, 179, 71));
        mainPanel.setLayout(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Panel superior: título y contador
        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        
        // Panel central: tabla de usuarios
        mainPanel.add(createTablePanel(), BorderLayout.CENTER);
        
        // Panel inferior: botones de acción
        mainPanel.add(createButtonsPanel(), BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        panel.setOpaque(false);
        
        // Título
        JLabel titleLabel = new JLabel("Gestión de Usuarios del Sistema");
        titleLabel.setFont(new Font("Verdana", Font.BOLD, 20));
        titleLabel.setForeground(Color.BLACK);
        panel.add(titleLabel);
        
        // Contador de usuarios
        userCountLabel = new JLabel("Total: 0 usuarios");
        userCountLabel.setFont(new Font("Verdana", Font.PLAIN, 14));
        userCountLabel.setForeground(Color.BLACK);
        panel.add(userCountLabel);
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.WHITE, 1),
            "Usuarios Registrados",
            0, 0,
            new Font("Verdana", Font.BOLD, 14),
            Color.WHITE
        ));
        
        // Modelo de tabla
        String[] columnas = {"ID", "Usuario", "Nombre Visible", "Rol", "Saldo", "Estado"};
        tableModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Solo lectura
            }
        };
        
        usersTable = new JTable(tableModel);
        usersTable.setFont(new Font("Verdana", Font.PLAIN, 12));
        usersTable.setRowHeight(25);
        usersTable.getTableHeader().setFont(new Font("Verdana", Font.BOLD, 13));
        usersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        usersTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        
        // Renderizador personalizado para colores según rol
        usersTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    String rol = (String) table.getValueAt(row, 3);
                    switch (rol.toLowerCase()) {
                        case "admin":
                            c.setBackground(new Color(255, 200, 200));
                            break;
                        case "teacher":
                            c.setBackground(new Color(200, 255, 200));
                            break;
                        default:
                            c.setBackground(new Color(200, 200, 255));
                    }
                }
                return c;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(usersTable);
        scrollPane.setPreferredSize(new Dimension(1000, 500));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createButtonsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panel.setOpaque(false);
        
        // Botón Crear Usuario
        addButton(panel, "Crear Usuario", e -> createUser(), COLOR_BOTON);
        
        // Botón Editar Usuario
        addButton(panel, "Editar Usuario", e -> editUser(), new Color(23, 162, 184));
        
        // Botón Eliminar Usuario
        addButton(panel, "Eliminar Usuario", e -> deleteUser(), new Color(220, 53, 69));
        
        // Botón Ver Detalles
        addButton(panel, "Ver Detalles", e -> viewUserDetails(), new Color(111, 66, 193));
        
        // Botón Refrescar
        addButton(panel, "Refrescar", e -> loadUsers(), COLOR_BOTON);
        
        // Etiqueta de estado
        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Verdana", Font.ITALIC, 12));
        statusLabel.setForeground(Color.BLACK);
        panel.add(statusLabel);
        
        return panel;
    }
    
    private void addButton(JPanel panel, String text, java.awt.event.ActionListener listener, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Verdana", Font.BOLD, 13));
        button.setBackground(bgColor);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(160, 35));
        button.addActionListener(listener);
        
        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(COLOR_BOTON_HOVER);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
        
        panel.add(button);
    }
    
    private void loadUsers() {
        // Obtener usuarios directamente del controlador
        List<User> users = appController.getUsers();
        tableModel.setRowCount(0);

        for (User user : users) {
            String estado;
            if (user.getBalance() < -1000) {
                estado = "DEUDA";
            } else if (user.getBalance() < 0) {
                estado = "NEGATIVO";
            } else if (user.getBalance() == 0) {
                estado = "CERO";
            } else if (user.getBalance() < 10000) {
                estado = "BAJO";
            } else {
                estado = "ALTO";
            }

            tableModel.addRow(new Object[]{
                user.getUsername(),
                user.getUsername(),
                user.getDisplayName(),
                user.getRole().toUpperCase(),
                String.format("$%,.2f", user.getBalance()),
                estado
            });
        }

        userCountLabel.setText("Total: " + users.size() + " usuarios");
    }
    
    private void createUser() {
        UserDialog dialog = new UserDialog(this, null, appController);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            // Recargar datos desde el controlador
            loadUsers();
            showSuccess("Usuario creado exitosamente");
        }
    }
    
    private void editUser() {
        int selectedRow = usersTable.getSelectedRow();
        if (selectedRow == -1) {
            showError("Selecciona un usuario para editar");
            return;
        }

        String username = (String) tableModel.getValueAt(selectedRow, 0);
        User user = appController.getUsers().stream()
            .filter(u -> u.getUsername().equals(username))
            .findFirst()
            .orElse(null);

        if (user != null) {
            UserDialog dialog = new UserDialog(this, user, appController);
            dialog.setVisible(true);

            if (dialog.isConfirmed()) {
                loadUsers();
                showSuccess("Usuario actualizado");
            }
        } else {
            showError("Usuario no encontrado");
        }
    }
    
    private void deleteUser() {
        int selectedRow = usersTable.getSelectedRow();
        if (selectedRow == -1) {
            showError("Selecciona un usuario para eliminar");
            return;
        }

        String username = (String) tableModel.getValueAt(selectedRow, 0);

        // No permitir eliminarse a sí mismo
        if (username.equals(currentUser.getUsername())) {
            showError("No puedes eliminarte a ti mismo");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "⚠️ ¿ELIMINAR usuario '" + username + "'?\nEsta acción es irreversible y eliminará también sus resultados.",
            "Confirmar Eliminación",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.ERROR_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            // Usar el AppController directamente para eliminar
            appController.getUsers().removeIf(u -> u.getUsername().equals(username));

            // Eliminar resultados del usuario usando el controlador principal
            appController.getResults().removeIf(r -> r.getStudentUsername().equals(username));

            // Guardar cambios en el controlador principal
            appController.saveAll();

            // Forzar recarga desde el controlador
            appController.setUsers(appController.getUsers()); // Actualizar lista interna
            appController.setCurrentUser(currentUser); // Mantener usuario actual

            loadUsers();
            showSuccess("Usuario eliminado correctamente");
        }
    }
    
    private void viewUserDetails() {
        int selectedRow = usersTable.getSelectedRow();
        if (selectedRow == -1) {
            showError("Selecciona un usuario para ver detalles");
            return;
        }

        String username = (String) tableModel.getValueAt(selectedRow, 0);
        User user = appController.getUsers().stream()
            .filter(u -> u.getUsername().equals(username))
            .findFirst()
            .orElse(null);

        if (user == null) {
            showError("Usuario no encontrado");
            return;
        }

        // Calcular estadísticas adicionales
        long totalResults = appController.getResults().stream()
            .filter(r -> r.getStudentUsername().equals(username))
            .count();

        long activeResults = appController.getResults().stream()
            .filter(r -> r.getStudentUsername().equals(username))
            .filter(r -> !r.isArchived())
            .count();

        double avgScore = appController.getResults().stream()
            .filter(r -> r.getStudentUsername().equals(username))
            .mapToDouble(r -> r.getPercentage())
            .average()
            .orElse(0.0);

        // Construir mensaje detallado
        StringBuilder details = new StringBuilder();
        details.append("<html><body style='font-family: Verdana; font-size: 12px;'>");

        details.append("<h2 style='color: #333;'>👤 Detalles del Usuario</h2>");

        // Información básica
        details.append("<table style='width: 100%; border-collapse: collapse;'>");
        details.append("<tr style='background-color: #f0f0f0;'><td style='padding: 5px; font-weight: bold;'>Usuario:</td><td style='padding: 5px;'>").append(user.getUsername()).append("</td></tr>");
        details.append("<tr><td style='padding: 5px; font-weight: bold;'>Nombre:</td><td style='padding: 5px;'>").append(user.getDisplayName()).append("</td></tr>");
        details.append("<tr style='background-color: #f0f0f0;'><td style='padding: 5px; font-weight: bold;'>Rol:</td><td style='padding: 5px;'><b>").append(user.getRole().toUpperCase()).append("</b></td></tr>");
        details.append("<tr><td style='padding: 5px; font-weight: bold;'>Saldo:</td><td style='padding: 5px; color: ").append(user.getBalance() < 0 ? "red" : "green").append(";'><b>$").append(String.format("%,.2f", user.getBalance())).append("</b></td></tr>");
        details.append("</table>");

        // Estadísticas
        details.append("<h3 style='color: #333; margin-top: 15px;'>📊 Estadísticas</h3>");
        details.append("<table style='width: 100%; border-collapse: collapse;'>");
        details.append("<tr style='background-color: #f0f0f0;'><td style='padding: 5px;'>Total Pruebas:</td><td style='padding: 5px;'>").append(totalResults).append("</td></tr>");
        details.append("<tr><td style='padding: 5px;'>Pruebas Activas:</td><td style='padding: 5px;'>").append(activeResults).append("</td></tr>");
        details.append("<tr style='background-color: #f0f0f0;'><td style='padding: 5px;'>Promedio:</td><td style='padding: 5px; color: ").append(avgScore >= 70 ? "green" : avgScore >= 60 ? "orange" : "red").append(";'><b>").append(String.format("%.2f%%", avgScore)).append("</b></td></tr>");
        details.append("<tr><td style='padding: 5px;'>Transacciones:</td><td style='padding: 5px;'>").append(user.getTransactions().size()).append("</td></tr>");
        details.append("</table>");

        // Últimos movimientos
        if (!user.getTransactions().isEmpty()) {
            details.append("<h3 style='color: #333; margin-top: 15px;'>💳 Últimos 5 Movimientos</h3>");
            details.append("<table style='width: 100%; border-collapse: collapse; border: 1px solid #ddd;'>");
            details.append("<tr style='background-color: #4CAF50; color: white;'><th style='padding: 5px; text-align: left;'>Fecha</th><th style='padding: 5px; text-align: left;'>Concepto</th><th style='padding: 5px; text-align: right;'>Monto</th></tr>");

            user.getTransactions().stream()
                .sorted((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()))
                .limit(5)
                .forEach(t -> {
                    String color = t.getAmount() > 0 ? "green" : "red";
                    String sign = t.getAmount() > 0 ? "+" : "";
                    details.append(String.format("<tr><td style='padding: 5px; border-bottom: 1px solid #ddd;'>%s</td><td style='padding: 5px; border-bottom: 1px solid #ddd;'>%s</td><td style='padding: 5px; border-bottom: 1px solid #ddd; text-align: right; color: %s;'><b>%s$%,.0f</b></td></tr>",
                        t.getTimestamp().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM HH:mm")),
                        t.getConcept(),
                        color,
                        sign,
                        Math.abs(t.getAmount())));
                });

            details.append("</table>");
        }

        details.append("</body></html>");

        JEditorPane editorPane = new JEditorPane("text/html", details.toString());
        editorPane.setEditable(false);
        editorPane.setPreferredSize(new Dimension(500, 400));

        JScrollPane scrollPane = new JScrollPane(editorPane);

        JOptionPane.showMessageDialog(this, scrollPane, 
            "👤 Detalles de " + user.getUsername(), JOptionPane.INFORMATION_MESSAGE);
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
 * Diálogo interno para crear/editar usuario
 */
class UserDialog extends JDialog {
    private final User existingUser;
    private final AppController appController;
    private boolean confirmed = false;
    
    private JTextField usernameField, displayNameField;
    private JPasswordField passwordField, confirmPasswordField;
    private JComboBox<String> roleCombo;
    private JTextField adminCodeField;
    private JLabel statusLabel;
    
    public UserDialog(JDialog owner, User user, AppController controller) {
        super(owner, user == null ? "➕ Nuevo Usuario" : "✏️ Editar Usuario", true);
        this.existingUser = user;
        this.appController = controller;
        
        setSize(500, 400);
        setLocationRelativeTo(owner);
        
        initComponents();
        
        if (existingUser != null) {
            loadUserData();
        }
    }
    
    private void initComponents() {
        GradientPanel panel = new GradientPanel(new Color(255, 218, 185), new Color(255, 179, 71));
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Formulario
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setOpaque(false);
        formPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.WHITE), "Datos del Usuario", 0, 0, 
            new Font("Verdana", Font.BOLD, 12), Color.WHITE));
        
        // Usuario
        formPanel.add(new JLabel("Usuario:"));
        usernameField = new JTextField(20);
        formPanel.add(usernameField);
        
        // Nombre visible
        formPanel.add(new JLabel("Nombre Visible:"));
        displayNameField = new JTextField(20);
        formPanel.add(displayNameField);
        
        // Rol
        formPanel.add(new JLabel("Rol:"));
        roleCombo = new JComboBox<>(new String[]{"student", "teacher", "admin"});
        roleCombo.addActionListener(e -> {
            boolean isAdmin = "admin".equals(roleCombo.getSelectedItem());
            adminCodeField.setEnabled(isAdmin);
            ((JLabel)adminCodeField.getClientProperty("label")).setEnabled(isAdmin);
        });
        formPanel.add(roleCombo);
        
        // Código admin (solo para admin)
        JLabel codeLabel = new JLabel("Código Admin:");
        adminCodeField = new JTextField(20);
        adminCodeField.setEnabled(false);
        adminCodeField.putClientProperty("label", codeLabel);
        formPanel.add(codeLabel);
        formPanel.add(adminCodeField);
        
        // Contraseña
        formPanel.add(new JLabel("Contraseña:"));
        passwordField = new JPasswordField(20);
        formPanel.add(passwordField);
        
        // Confirmar contraseña
        formPanel.add(new JLabel("Confirmar:"));
        confirmPasswordField = new JPasswordField(20);
        formPanel.add(confirmPasswordField);
        
        // Botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setOpaque(false);
        
        JButton okButton = new JButton("✅ Guardar");
        okButton.setBackground(new Color(40, 167, 69));
        okButton.setForeground(Color.BLACK);
        okButton.addActionListener(e -> {
            if (validateFields()) {
                saveUser();
                confirmed = true;
            }
        });
        
        JButton cancelButton = new JButton("❌ Cancelar");
        cancelButton.setBackground(new Color(220, 53, 69));
        cancelButton.setForeground(Color.BLACK);
        cancelButton.addActionListener(e -> dispose());
        
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        
        // Status label
        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Verdana", Font.ITALIC, 12));
        statusLabel.setForeground(Color.BLACK);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Ensamblar
        panel.add(formPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        panel.add(statusLabel, BorderLayout.NORTH);
        
        setContentPane(panel);
    }
    
    private void loadUserData() {
        usernameField.setText(existingUser.getUsername());
        usernameField.setEditable(false); // No permitir cambiar username
        displayNameField.setText(existingUser.getDisplayName());
        roleCombo.setSelectedItem(existingUser.getRole());
        passwordField.setText(""); // Dejar vacío para forzar nueva contraseña si quiere
        confirmPasswordField.setText("");
    }
    
    private boolean validateFields() {
        if (usernameField.getText().trim().isEmpty()) {
            showError("El usuario es obligatorio");
            return false;
        }
        
        if (existingUser == null && appController.getUsers().stream()
            .anyMatch(u -> u.getUsername().equalsIgnoreCase(usernameField.getText().trim()))) {
            showError("El usuario ya existe");
            return false;
        }
        
        if (displayNameField.getText().trim().isEmpty()) {
            showError("El nombre visible es obligatorio");
            return false;
        }
        
        // Validar código admin si se quiere crear admin
        if ("admin".equals(roleCombo.getSelectedItem()) && existingUser == null) {
            if (!"3-V4LU4D0R3S".equals(adminCodeField.getText().trim())) {
                showError("Código de administrador incorrecto");
                return false;
            }
        }
        
        // Validar contraseña solo si se ingresó una
        String password = new String(passwordField.getPassword());
        String confirm = new String(confirmPasswordField.getPassword());
        if (!password.isEmpty() && !password.equals(confirm)) {
            showError("Las contraseñas no coinciden");
            return false;
        }
        
        return true;
    }
    
    private void saveUser() {
        String username = usernameField.getText().trim();
        String displayName = displayNameField.getText().trim();
        String role = (String) roleCombo.getSelectedItem();
        String password = new String(passwordField.getPassword());

        User user;
        if (existingUser == null) {
            // Crear nuevo usuario
            user = switch (role) {
                case "admin" -> new Admin(username, displayName);
                case "teacher" -> new Teacher(username, displayName);
                default -> new Student(username, displayName);
            };

            // Saldo inicial de bienvenida
            Transaction welcome = new Transaction(username, "Saldo inicial", 5000.0, "PAYMENT");
            user.addTransaction(welcome);

            // Añadir a la lista de transacciones del controlador
            if (appController.getTransactions() != null) {
                appController.getTransactions().add(welcome);
            }

            // Añadir usuario a la lista del controlador
            appController.getUsers().add(user);

        } else {
            // Editar usuario existente
            user = existingUser;
            user.setDisplayName(displayName);

            // Cambiar rol si es diferente
            if (!user.getRole().equals(role)) {
                User newUser = switch (role) {
                    case "admin" -> new Admin(user.getUsername(), displayName);
                    case "teacher" -> new Teacher(user.getUsername(), displayName);
                    default -> new Student(user.getUsername(), displayName);
                };
                newUser.setBalance(user.getBalance());
                newUser.setPasswordHash(user.getPasswordHash());
                newUser.setTransactions(user.getTransactions());

                // Reemplazar en la lista del controlador
                int index = appController.getUsers().indexOf(existingUser);
                if (index != -1) {
                    appController.getUsers().set(index, newUser);
                }
                user = newUser;
            }
        }

        // Actualizar contraseña si se proporcionó
        if (!password.isEmpty()) {
            user.setPassword(password);
        }

        // Guardar cambios en el controlador
        appController.saveAll();

        // Marcar como confirmado
        confirmed = true;
        dispose();
    }
    
    private void showError(String message) {
        statusLabel.setText(message);
        statusLabel.setForeground(new Color(220, 53, 69));
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
}