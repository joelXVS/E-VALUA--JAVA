/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gamerker.io.e.valua_java.mainClasses;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author hp
 */
public abstract class User {
    protected String username, rol;
    
    public User(String username, String rol) {
        this.username = username;
        this.rol = rol;
    }
       
    // Getters
    public String getUsername() { return username; }
    public String getRol() { return rol; }
    public abstract String getInfo();
    
    // Setters
    public void setUsername(String username) { this.username = username; }
    public void setRol(String rol) { this.rol = rol; }
}