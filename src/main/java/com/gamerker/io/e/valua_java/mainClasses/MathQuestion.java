/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gamerker.io.e.valua_java.mainClasses;
/**
 *
 * @author hp
 */
public class MathQuestion extends Question {

    public MathQuestion(String questionText, String correctAnswer) {
        super(questionText, correctAnswer);
    }

    @Override
    public boolean verifyAnswer(String userAnswer) {
        if (userAnswer == null) return false;
        try {
            // Comparamos numéricamente si es posible (acepta enteros y decimales)
            double a = Double.parseDouble(userAnswer.trim());
            double b = Double.parseDouble(correctAnswer.trim());
            return Math.abs(a - b) < 1e-6;
        } catch (NumberFormatException e) {
            // Si no son números comparamos texto ignorando espacios y case
            return userAnswer.trim().equalsIgnoreCase(correctAnswer.trim());
        }
    }
}