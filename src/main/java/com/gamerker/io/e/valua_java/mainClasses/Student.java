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
public class Student extends User {
    private List<Result> testsHistory;
    
    public Student(String username) {
        super(username, "Student");
        this.testsHistory = new ArrayList<>();
    }
    
    public void addTestResult(Result result) {
        testsHistory.add(result);
    }
    
    public double getAverageScore() {
        if (testsHistory.isEmpty()) return 0.0;
        
        double total = 0;
        for (Result result : testsHistory) {
            total += result.getPercentage();
        }
        return total / testsHistory.size();
    }
    
    public int getTestsTaken() {
        return testsHistory.size();
    }
    
    public List<Result> getTestHistory() { return new ArrayList<>(testsHistory); }

    @Override
    public String getInfo() {
        return "";
    }
}