/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gamerker.io.e.valua_java.utils;
/**
 *
 * @author hp
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class LoginPanel extends GradientPanel {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel statusLabel;
    
    public LoginPanel(ActionListener loginCallback) {
        super(new Color(255, 165, 0), new Color(255, 107, 107)); // Naranja a rosa
        
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Título
        JLabel title = new JLabel("Iniciar Sesión", SwingConstants.CENTER);
        title.setFont(new Font("Verdana", Font.BOLD, 24));
        title.setForeground(Color.BLACK);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        add(title, gbc);
        
        // Usuario
        gbc.gridy = 1; gbc.gridwidth = 1;
        add(new JLabel("Usuario:"), gbc);
        gbc.gridx = 1;
        usernameField = new JTextField(20);
        usernameField.setFont(new Font("Verdana", Font.PLAIN, 14));
        add(usernameField, gbc);
        
        // Contraseña
        gbc.gridx = 0; gbc.gridy = 2;
        add(new JLabel("Contraseña:"), gbc);
        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Verdana", Font.PLAIN, 14));
        add(passwordField, gbc);
        
        // Botón login
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        JButton loginButton = new JButton("Ingresar");
        loginButton.setFont(new Font("Verdana", Font.BOLD, 16));
        loginButton.setBackground(new Color(255, 140, 0));
        loginButton.setForeground(Color.BLACK);
        loginButton.addActionListener(loginCallback);
        add(loginButton, gbc);
        
        // Status label
        gbc.gridy = 4;
        statusLabel = new JLabel(" ", SwingConstants.CENTER);
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setFont(new Font("Verdana", Font.ITALIC, 12));
        add(statusLabel, gbc);
    }
    
    public String getUsername() {
        return usernameField.getText().trim();
    }
    
    public String getPassword() {
        return new String(passwordField.getPassword());
    }
    
    public void showMessage(String message, boolean isError) {
        statusLabel.setText(message);
        statusLabel.setForeground(isError ? Color.RED : Color.GREEN);
    }
    
    public void clear() {
        usernameField.setText("");
        passwordField.setText("");
        statusLabel.setText(" ");
    }
}