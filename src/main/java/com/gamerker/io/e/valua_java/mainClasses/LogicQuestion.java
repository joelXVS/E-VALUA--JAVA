/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gamerker.io.e.valua_java.mainClasses;
import java.util.List;
/**
 *
 * @author hp
 */
public class LogicQuestion extends Question {
    private List<String> options;
    private int correctOptionIndex;
    
    public LogicQuestion(String text, int points, List<String> options, int correctOptionIndex) {
        super(text, points, "LOGIC");
        this.options = options;
        this.correctOptionIndex = correctOptionIndex;
    }
    
    @Override
    public boolean verifyAnswer(String userAnswer) {
        try {
            int userChoice = Integer.parseInt(userAnswer.trim()) - 1; // Indice basado en 0
            return userChoice == correctOptionIndex;
        } catch (NumberFormatException e) {
            // Solo ingresar letras (A, B, C, D)
            if (userAnswer.length() == 1) {
                char choice = Character.toUpperCase(userAnswer.charAt(0));
                int userChoice = choice - 'A';
                return userChoice == correctOptionIndex;
            }
            return false;
        }
    }
    
    @Override
    public String renderQuestion() {
        StringBuilder sb = new StringBuilder();
        sb.append(text).append("\n");
        
        char optionLetter = 'A';
        for (String option : options) {
            sb.append(optionLetter).append(") ").append(option).append("\n");
            optionLetter++;
        }
        sb.append("Enter your choice (1-").append(options.size()).append(" or A-").append((char)('A' + options.size() - 1)).append("):");
        
        return sb.toString();
    }
    
    // Getters
    public List<String> getOptions() { return options; }
    public int getCorrectOptionIndex() { return correctOptionIndex; }
}
