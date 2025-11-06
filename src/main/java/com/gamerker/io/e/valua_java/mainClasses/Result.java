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
public class Result {
    private String studentUsername;
    private String testTitle;
    private int score;
    private int total;
    private double percentage;
    private List<String> answers;

    public Result(String studentUsername, String testTitle, int score, int total, double percentage, List<String> answers) {
        this.studentUsername = studentUsername;
        this.testTitle = testTitle;
        this.score = score;
        this.total = total;
        this.percentage = percentage;
        this.answers = new ArrayList<>(answers);
    }

    public String getStudentUsername() { return studentUsername; }
    public String getTestTitle() { return testTitle; }

    public String summary() {
        return String.format("%s - %s: %d/%d (%.2f%%)", studentUsername, testTitle, score, total, percentage);
    }

    public String detailed() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Estudiante: %s%nPrueba: %s%nPuntuación: %d/%d (%.2f%%)%n", studentUsername, testTitle, score, total, percentage));
        sb.append("Respuestas del estudiante:\n");
        for (int i=0;i<answers.size();i++) {
            sb.append(String.format("%d) %s%n", i+1, answers.get(i)));
        }
        return sb.toString();
    }

    public List<String> getAnswers() {
        return answers;
    }

    public int getScore() {
        return score;
    }

    public int getTotal() {
        return total;
    }

    public double getPercentage() {
        return percentage;
    }
}