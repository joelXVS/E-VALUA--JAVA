/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gamerker.io.e.valua_java.controllersPack;
import com.gamerker.io.e.valua_java.interfaces.Manageable;
import com.gamerker.io.e.valua_java.interfaces.Exportable;
import com.gamerker.io.e.valua_java.mainClasses.*;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author hp
 */
public class AppController implements Exportable, Manageable {
    private List<User> users;
    private List<Test> tests;
    private List<Result> results;
    
    public AppController() {
        this.users = new ArrayList<>();
        this.tests = new ArrayList<>();
        this.results = new ArrayList<>();
    }

    @Override
    public void exportToPDF(Result result, String filePath) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void saveToFile(String filename) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void loadFromFile(String filename) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}