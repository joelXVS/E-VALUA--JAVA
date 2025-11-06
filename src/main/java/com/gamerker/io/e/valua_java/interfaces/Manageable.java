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
public interface Manageable {
    void saveToFile(List<User> users, String filename);
    java.util.List<User> loadFromFile(String filename);
}
