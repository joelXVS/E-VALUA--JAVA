/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.gamerker.io.e.valua_java.interfaces;
import java.util.List;
import com.gamerker.io.e.valua_java.mainClasses.*;
/**
 *
 * @author hp
 */
/**
 * interfaz para clases que gestionan persistencia de usuarios
 */
public interface Manageable {
    // guarda lista de usuarios en archivo
    void saveToFile(List<User> users, String filename);
    
    // carga lista de usuarios desde archivo
    List<User> loadFromFile(String filename);
}