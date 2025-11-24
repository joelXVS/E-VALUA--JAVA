/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gamerker.io.e.valua_java.mainClasses;
/**
 *
 * @author hp
 */
/**
 * representa un usuario administrador
 * rol con permisos completos sobre el sistema
 */
public class Admin extends User {
    
    // constructor que inicializa username y nombre visible
    public Admin(String username, String displayName) {
        super(username, displayName);
    }

    // devuelve el rol especifico
    @Override
    public String getRole() { return "admin"; }
}