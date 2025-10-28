/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gamerker.io.e.valua_java.mainClasses;
import java.util.Random;

/**
 *
 * @author hp
 */
public class MathQuestion extends Question {
    private double correctAnswer;
    private String operation;
    private Random random;
    
    public MathQuestion(String text, int points, double correctAnswer, String operation) {
        super(text, points, "MATH");
        this.correctAnswer = correctAnswer;
        this.operation = operation;
        this.random = new Random();
    }
    
    @Override
    public boolean verifyAnswer(String userAnswer) {
        
        try {
            double validAnswer = Double.parseDouble(userAnswer.trim());
            // Redondeo de valores para mejor analisis
            return Math.abs(validAnswer - correctAnswer) < 0.001;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    @Override
    public String renderQuestion() {
        return text + ":";
    }
    
    public static MathQuestion generateRandomQuestion(int difficulty) {
        
        Random rand = new Random();
        int a = rand.nextInt(999 * difficulty) + 1;
        int b = rand.nextInt(999 * difficulty) + 1;
        
        String[] operations = {"+", "-", "*", "/"};
        String operation = operations[rand.nextInt(operations.length)];
        
        String text;
        double correctAnswer;
        
        switch (operation) {
            case "-" -> {
                text = "¿Cuánto es " + a + " - " + b + "?";
                correctAnswer = a - b;
            }
            case "*" -> {
                text = "¿Cuánto es " + a + " × " + b + "?";
                correctAnswer = a * b;
            }
            case "/" -> {
                b = rand.nextInt(10) + 1;
                a = b * (rand.nextInt(999) + 1);
                text = "¿Cuánto es " + a + " ÷ " + b + "?";
                correctAnswer = a / b;
            }
            default -> {
                text = "¿Cuánto es " + a + " + " + b + "?";
                correctAnswer = a + b;
            }
        }
        
        return new MathQuestion(text, difficulty / 50, correctAnswer, operation);
    }
    
    // Getters
    public double getCorrectAnswer() { return correctAnswer; }
    public String getOperation() { return operation; }
}
