/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gamerker.io.e.valua_java.mainClasses;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
/**
 *
 * @author hp
 */
/**
 * almacena el resultado de una prueba realizada por un estudiante
 * incluye puntuacion, respuestas y metodos para mostrar resumen
 */
public class Result {
    private String studentUsername;
    private String testTitle;
    private int score;
    private int total;
    private double percentage;
    private List<String> answers;
    private boolean archived;
    private LocalDateTime timestamp;
    private Duration timeTaken; 

    // constructor que inicializa todos los campos
    public Result(String studentUsername, String testTitle, int score, int total, double percentage, List<String> answers, Duration timeTaken) {
        this.studentUsername = studentUsername;
        this.testTitle = testTitle;
        this.score = score;
        this.total = total;
        this.percentage = percentage;
        this.answers = new ArrayList<>(answers);
        this.archived = false;
        this.timestamp = LocalDateTime.now();
        this.timeTaken = timeTaken;
    }

    // getters para acceder a los campos
    public String getStudentUsername() { return studentUsername; }
    public String getTestTitle() { return testTitle; }
    public List<String> getAnswers() { return answers; }
    public int getScore() { return score; }
    public int getTotal() { return total; }
    public double getPercentage() { return percentage; }
    public Duration getTimeTaken() { return timeTaken; }
    public String getFormattedTime() {
        if (timeTaken == null) return "N/A";
        long minutes = timeTaken.toMinutes();
        long seconds = timeTaken.getSeconds() % 60;
        return String.format("%d min %d sec", minutes, seconds);
    }
    public boolean isArchived() { return archived; }
    public LocalDateTime getTimestamp() { return timestamp; }

    // setters para modificar los campos
    public void setArchived(boolean archived) { this.archived = archived; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    
    /**
     * devuelve resumen compacto del resultado
     * formato: usuario - prueba: puntaje/total (porcentaje%)
     */
    public String summary() {
        return String.format("%s - %s: %d/%d (%.2f%%)", studentUsername, testTitle, score, total, percentage);
    }

    /**
     * devuelve detalle completo del resultado
     * incluye todas las respuestas del estudiante
     */
    public String detailed() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Estudiante: %s%nPrueba: %s%nPuntuacion: %d/%d (%.2f%%)%n", 
            studentUsername, testTitle, score, total, percentage));
        sb.append("Respuestas del estudiante:\n");
        for (int i = 0; i < answers.size(); i++) {
            sb.append(String.format("%d) %s%n", i + 1, answers.get(i)));
        }
        return sb.toString();
    }
}