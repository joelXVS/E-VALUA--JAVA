/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gamerker.io.e.valua_java.mainClasses;
import java.util.List;
import java.util.ArrayList;
/**
 *
 * @author hp
 */
public class Test {
    private String name, description;
    private List<Question> questions;
    private int timeLimit; // minutos
    
    public Test(String name, String description, int timeLimit) {
        this.name = name;
        this.description = description;
        this.timeLimit = timeLimit;
        this.questions = new ArrayList<>();
    }
    
    public void addQuestion(Question question) {
        questions.add(question);
    }
    
    public void removeQuestion(Question question) {
        questions.remove(question);
    }
    
    // Getters
    public int getTotalPoints() {
        return questions.stream().mapToInt(Question::getPoints).sum();
    }
    
    public int getQuestionCount() {
        return questions.size();
    }

    public String getName() { return name; }
    public List<Question> getQuestions() { return new ArrayList<>(questions); }
    public int getTimeLimit() { return timeLimit; }
    public String getInfo() {
        return "Exam{name='" + name + "', questions=" + questions.size() + ", timeLimit=" + timeLimit + "}";
    }
    
    // Setters
    public void setName(String name) { this.name = name; }
    public void setTimeLimit(int timeLimit) { this.timeLimit = timeLimit; }
}
