/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gamerker.io.e.valua_java.mainClasses;
/**
 *
 * @author hp
 */
public class Student extends User {
    // Student puede tomar un test y ver sus result
    
    public Student(String id, String name, String email, String password) {
        super(id, name, email, password);
    }

    // Student esta asociado a un Result
    
    @Override
    public void showMenu() {
        System.out.println("\n===== MENU DE ESTUDIANTES =====");
        System.out.println("1. Realizar prueba");
        System.out.println("2. Ver historial de resultados");
        System.out.println("3. Salir de E-valua");
        System.out.println("========================");
    }

    @Override
    public String getInfo() {
        return "";
    }
}