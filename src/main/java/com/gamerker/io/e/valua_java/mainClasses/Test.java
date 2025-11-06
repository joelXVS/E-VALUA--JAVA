/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gamerker.io.e.valua_java.mainClasses;
import java.util.*;
/**
 *
 * @author hp
 */
public class Test {
    private String title;
    private List<Question> questions = new ArrayList<>();

    public Test(String title) {
        this.title = title;
    }

    public void addQuestion(Question q) { questions.add(q); }
    public List<Question> getQuestions() { return questions; }
    public String getTitle() { return title; }
}
