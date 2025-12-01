/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gamerker.io.e.valua_java.utils;
/**
 *
 * @author hp
 */
import com.gamerker.io.e.valua_java.controllersPack.AppController;
import com.gamerker.io.e.valua_java.mainClasses.Admin;
import com.gamerker.io.e.valua_java.mainClasses.Student;
import com.gamerker.io.e.valua_java.mainClasses.Teacher;
import com.gamerker.io.e.valua_java.mainClasses.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class RegisterPanel extends GradientPanel {
    private JTextField usernameField, displayNameField;
    private JPasswordField passwordField, confirmPasswordField;
    private JComboBox<String> roleCombo;
    private JTextField adminCodeField;
    private JLabel statusLabel;
    
    public RegisterPanel(ActionListener registerCallback) {
        super(new Color(255, 179, 71), new Color(255, 126, 95)); // Naranja pastel
        
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Título
        JLabel title = new JLabel("Registro de Usuario", SwingConstants.CENTER);
        title.setFont(new Font("Verdana", Font.BOLD, 24));
        title.setForeground(Color.BLACK);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        add(title, gbc);
        
        // Rol
        gbc.gridy = 1; gbc.gridwidth = 1;
        add(new JLabel("Rol:"), gbc);
        gbc.gridx = 1;
        roleCombo = new JComboBox<>(new String[]{"Estudiante", "Profesor", "Administrador"});
        roleCombo.addActionListener(e -> {
            boolean isAdmin = roleCombo.getSelectedIndex() == 2;
            adminCodeField.setVisible(isAdmin);
            ((JLabel)adminCodeField.getClientProperty("label")).setVisible(isAdmin);
        });
        add(roleCombo, gbc);
        
        // Código admin (oculto por defecto)
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel codeLabel = new JLabel("Código Admin:");
        codeLabel.setVisible(false);
        add(codeLabel, gbc);
        gbc.gridx = 1;
        adminCodeField = new JTextField(20);
        adminCodeField.setVisible(false);
        adminCodeField.putClientProperty("label", codeLabel);
        add(adminCodeField, gbc);
        
        // Usuario
        gbc.gridx = 0; gbc.gridy = 3;
        add(new JLabel("Usuario:"), gbc);
        gbc.gridx = 1;
        usernameField = new JTextField(20);
        add(usernameField, gbc);
        
        // Nombre visible
        gbc.gridx = 0; gbc.gridy = 4;
        add(new JLabel("Nombre visible:"), gbc);
        gbc.gridx = 1;
        displayNameField = new JTextField(20);
        add(displayNameField, gbc);
        
        // Contraseña
        gbc.gridx = 0; gbc.gridy = 5;
        add(new JLabel("Contraseña:"), gbc);
        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
        add(passwordField, gbc);
        
        // Confirmar contraseña
        gbc.gridx = 0; gbc.gridy = 6;
        add(new JLabel("Confirmar contraseña:"), gbc);
        gbc.gridx = 1;
        confirmPasswordField = new JPasswordField(20);
        add(confirmPasswordField, gbc);
        
        // Botón registrar
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2;
        JButton registerButton = new JButton("Registrarse");
        registerButton.setFont(new Font("Verdana", Font.BOLD, 16));
        registerButton.setBackground(new Color(0, 0, 0));
        registerButton.setForeground(Color.BLACK);
        registerButton.addActionListener(registerCallback);
        add(registerButton, gbc);
        
        // Status
        gbc.gridy = 8;
        statusLabel = new JLabel(" ", SwingConstants.CENTER);
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setFont(new Font("Verdana", Font.ITALIC, 12));
        add(statusLabel, gbc);
    }
    
    public String getUsername() { return usernameField.getText().trim(); }
    public String getDisplayName() { return displayNameField.getText().trim(); }
    public String getPassword() { return new String(passwordField.getPassword()); }
    public String getConfirmPassword() { return new String(confirmPasswordField.getPassword()); }
    public String getRole() {
        return switch (roleCombo.getSelectedIndex()) {
            case 0 -> "student";
            case 1 -> "teacher";
            case 2 -> "admin";
            default -> "student";
        };
    }
    public String getAdminCode() { return adminCodeField.getText().trim(); }
    public boolean isAdminSelected() { return roleCombo.getSelectedIndex() == 2; }
    
    public void showMessage(String message, boolean isError) {
        statusLabel.setText(message);
        statusLabel.setForeground(isError ? Color.RED : Color.GREEN);
    }
    
    public void clear() {
        usernameField.setText("");
        displayNameField.setText("");
        passwordField.setText("");
        confirmPasswordField.setText("");
        adminCodeField.setText("");
        roleCombo.setSelectedIndex(0);
        statusLabel.setText(" ");
    }
}
