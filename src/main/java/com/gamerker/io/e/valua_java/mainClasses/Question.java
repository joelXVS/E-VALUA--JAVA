/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gamerker.io.e.valua_java.mainClasses;

/**
 *
 * @author hp
 */
public abstract class Question {
    protected String text, type;
    protected int points;
    
    public Question(String text, int points, String type) {
        this.text = text;
        this.type = type;
        this.points = points;
    }
    
    // Verificar si se selecciono la respuesta correcta
    public abstract boolean verifyAnswer(String userAnswer);
    // Mostrar la pregunta y lesperar para responder
    public abstract String renderQuestion();
    
    // Obtener puntos por respuesta correcta
    public int getPoints() { return points; }
    // Obtener tipo de pregunta
    public String getType() { return type; }
}