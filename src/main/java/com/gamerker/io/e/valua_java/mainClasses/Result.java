/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gamerker.io.e.valua_java.mainClasses;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
/**
 *
 * @author hp
 */
public class Result {
    private String resultId;
    private User user;
    private Test test;
    private LocalDateTime date;
    private Map<Question, Boolean> saveAnswers;
    private int totalScore;
    
    public Result(User user, Test test) {
        this.resultId = UUID.randomUUID().toString();
        this.user = user;
        this.test = test;
        this.date = LocalDateTime.now();
        this.saveAnswers = new HashMap<>();
        this.totalScore = 0;
    }
    
    public void addAnswer(Question question, boolean isCorrect) {
        saveAnswers.put(question, isCorrect);
        if (isCorrect) {
            totalScore += question.getPoints();
        }
    }
    
    // Getters
    public int getMaxPossibleScore() {
        return test.getTotalPoints();
    }
    
    public double getPercentage() {
        if (getMaxPossibleScore() == 0) return 0.0;
        return (double) totalScore / getMaxPossibleScore() * 100;
    }
    
    public String getFinalVL() {
        double percentage = getPercentage();
        if (percentage >= 90) return "A";
        if (percentage >= 80) return "B";
        if (percentage >= 70) return "C";
        if (percentage >= 60) return "D";
        return "F";
    }
    
    public String getResultId() { return resultId; }
    public User getUser() { return user; }
    public Test getTest() { return test; }
    public LocalDateTime getDate() { return date; }
    public Map<Question, Boolean> getAnswers() { return new HashMap<>(saveAnswers); }
    public int getTotalScore() { return totalScore; }
    public String getInfo() {
        return "Result{user='" + user.getUsername() + "', exam='" + test.getName() + 
               "', score=" + totalScore + "/" + getMaxPossibleScore() + 
               ", grade=" + getFinalVL() + "}";
    }
}