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
 * representa un usuario profesor
 * rol con permisos para crear pruebas
 */
public class Teacher extends User {

    // constructor que inicializa username y nombre visible
    public Teacher(String username, String displayName) {
        super(username, displayName);
    }

    // devuelve el rol especifico
    @Override
    public String getRole() { return "teacher"; }
}