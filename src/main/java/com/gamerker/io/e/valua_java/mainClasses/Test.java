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
/**
 * representa una prueba con titulo, lista de preguntas y precio
 */
public class Test {
    private String title;
    private List<Question> questions = new ArrayList<>();
    private double price = 5000.0;

    public Test(String title) {
        this.title = title != null ? title.trim() : "Prueba sin título";
    }

    public void addQuestion(Question q) {
        if (q != null) questions.add(q);
    }

    // Getters
    public String getTitle() { return title; }
    public List<Question> getQuestions() { return questions; }
    public int getTotalQuestions() { return questions.size(); }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price > 0 ? price : 5000.0; }

    @Override
    public String toString() {
        return title + " (" + questions.size() + " preguntas - $" + String.format("%,.0f", price) + ")";
    }
}