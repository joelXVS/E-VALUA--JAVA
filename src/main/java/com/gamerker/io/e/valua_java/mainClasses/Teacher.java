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
public class Teacher extends User {
    private List<Test> testsCreated;
    
    public Teacher(String username) {
        super(username, "Teacher");
        this.testsCreated = new ArrayList<>();
    }
    
    public void addNewTest(Test newTest) {
        testsCreated.add(newTest);
    }
    
    public int getTestsCreated() {
        return testsCreated.size();
    }
    
    @Override
    public String getInfo() {
        return "";
    }
}