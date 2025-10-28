/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gamerker.io.e.valua_java.mainClasses;
/**
 *
 * @author hp
 */
public class Teacher extends User {
    // Teacher crea y evalua tests
    
    public Teacher(String id, String name, String email, String password) {
        super(id, name, email, password);
    }
    
    // Teacher se agrega a Tests
    
    @Override
    public void showMenu() {
        System.out.println("\n===== MENU DE DOCENTES =====");
        System.out.println("1. Ver pruebas creadas");
        System.out.println("2. Revisar estudiantes");
        System.out.println("3. Exportar resultados");
        System.out.println("4. Salir de E-valua");
        System.out.println("========================");
    }
    
    @Override
    public String getInfo() {
        return "";
    }
}