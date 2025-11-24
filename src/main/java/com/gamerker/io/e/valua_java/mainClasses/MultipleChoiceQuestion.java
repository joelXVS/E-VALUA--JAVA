/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gamerker.io.e.valua_java.mainClasses;
/**
 *
 * @author hp
 */
import java.util.List;
/**
 * clase para preguntas
 */
public class MultipleChoiceQuestion extends Question {

    public MultipleChoiceQuestion(String questionText, List<String> options, String correctAnswer, String type) {
        super(questionText, options, correctAnswer, type);
    }

    @Override
    public boolean verifyAnswer(String userAnswer) {
        if (userAnswer == null) return false;
        String normalized = userAnswer.trim().toUpperCase();
        
        // Acepta tanto "A" como "32" (el texto de la opción correcta)
        if (normalized.equals(correctAnswer)) {
            return true;
        }
        
        // También acepta el texto completo de la opción correcta
        String correctText = getCorrectOptionText();
        return normalized.equalsIgnoreCase(correctText.trim());
    }
}