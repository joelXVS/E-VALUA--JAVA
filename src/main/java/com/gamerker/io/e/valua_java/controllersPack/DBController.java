/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gamerker.io.e.valua_java.controllersPack;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
/**
 *
 * @author hp
 */
public class DBController {
    private Gson gson;
    
    public DBController() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }
}