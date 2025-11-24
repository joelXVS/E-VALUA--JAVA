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
import java.util.Collections;
/**
 * clase abstracta base para todas las preguntas
 * define la estructura comun de texto y respuesta
 */
public abstract class Question {
    protected String questionText;
    protected List<String> options;      // A, B, C, D
    protected String correctAnswer;      // "A", "B", "C" o "D" (índice como letra)
    protected String type;               // "logic", "math", "verbal", etc.

    public Question(String questionText, List<String> options, String correctAnswer, String type) {
        this.questionText = questionText != null ? questionText.trim() : "";
        this.options = options != null ? List.copyOf(options) : Collections.emptyList();
        this.correctAnswer = correctAnswer != null ? correctAnswer.trim().toUpperCase() : "";
        this.type = type != null ? type : "unknown";
        
        // Validación básica
        if (this.options.size() != 4) {
            throw new IllegalArgumentException("Las preguntas deben tener exactamente 4 opciones (A, B, C, D)");
        }
        if (!"A".equals(this.correctAnswer) && !"B".equals(this.correctAnswer) && 
            !"C".equals(this.correctAnswer) && !"D".equals(this.correctAnswer)) {
            throw new IllegalArgumentException("La respuesta correcta debe ser A, B, C o D");
        }
    }

    // Getters
    public String getQuestionText() { return questionText; }
    public List<String> getOptions() { return options; }
    public String getCorrectAnswer() { return correctAnswer; }
    public String getType() { return type; }

    // Devuelve la opción correcta como texto (ej: "32")
    public String getCorrectOptionText() {
        int index = correctAnswer.charAt(0) - 'A';
        return (index >= 0 && index < options.size()) ? options.get(index) : "";
    }

    // Método abstracto: cada tipo de pregunta decide cómo verificar
    public abstract boolean verifyAnswer(String userAnswer);

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(questionText).append("\n");
        char letter = 'A';
        for (String opt : options) {
            sb.append(letter).append(") ").append(opt).append("\n");
            letter++;
        }
        return sb.toString();
    }
}