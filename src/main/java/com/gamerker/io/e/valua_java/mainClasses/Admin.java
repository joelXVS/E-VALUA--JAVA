/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gamerker.io.e.valua_java.mainClasses;
/**
 *
 * @author hp
 */
public class Admin extends User {
    // Admin puede gestionar teacher, test y export clases

    public Admin(String id, String name, String email, String password) {
        super(id, name, email, password);
    }

    @Override
    public void showMenu() {
        System.out.println("\n===== MENU DE ADMINISTRADOR =====");
        System.out.println("1. Crear Prueba");
        System.out.println("2. Administrar Docentes");
        System.out.println("3. Reportes");
        System.out.println("4. Salir de E-valua");
        System.out.println("======================");
    }

    @Override
    public String getInfo() {
        return "";
    }
}