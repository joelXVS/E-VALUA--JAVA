/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gamerker.io.e.valua_java.controllersPack;
import com.gamerker.io.e.valua_java.mainClasses.*;
import com.gamerker.io.e.valua_java.interfaces.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.*;
import java.util.*;
import java.nio.charset.StandardCharsets;
/**
 *
 * @author hp
 */
public class DBController implements Manageable {
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private final String usersFile = "users.json";
    private final String testsFile = "tests.json";
    private final String resultsFile = "results.json";

    // ----------------- GUARDAR -----------------
    public void saveAll(List<User> users, List<Test> tests, List<Result> results) {
        saveUsers(users, usersFile);
        saveTests(tests, testsFile);
        saveResults(results, resultsFile);
    }

    private void saveUsers(List<User> users, String filename) {
        JsonArray arr = new JsonArray();
        for (User u : users) {
            JsonObject o = new JsonObject();
            o.addProperty("username", u.getUsername());
            o.addProperty("displayName", u.getDisplayName());
            o.addProperty("role", u.getRole());
            arr.add(o);
        }
        writeJsonToFile(arr, filename);
    }

    private void saveTests(List<Test> tests, String filename) {
        JsonArray arr = new JsonArray();
        for (Test t : tests) {
            JsonObject to = new JsonObject();
            to.addProperty("title", t.getTitle());
            // si tienes campo createdBy, agrégalo: to.addProperty("createdBy", t.getCreatedBy());
            JsonArray qarr = new JsonArray();
            if (t.getQuestions() != null) {
                for (Question q : t.getQuestions()) {
                    JsonObject qo = new JsonObject();
                    qo.addProperty("questionText", q.getQuestionText());
                    qo.addProperty("correctAnswer", q.getCorrectAnswer());
                    if (q instanceof MathQuestion) qo.addProperty("type", "math");
                    else if (q instanceof LogicQuestion) qo.addProperty("type", "logic");
                    else qo.addProperty("type", "unknown");
                    qarr.add(qo);
                }
            }
            to.add("questions", qarr);
            arr.add(to);
        }
        writeJsonToFile(arr, filename);
    }

    private void saveResults(List<Result> results, String filename) {
        JsonArray arr = new JsonArray();
        for (Result r : results) {
            JsonObject ro = new JsonObject();
            ro.addProperty("studentUsername", r.getStudentUsername());
            ro.addProperty("testTitle", r.getTestTitle());
            JsonArray ans = new JsonArray();
            ro.add("answers", ans);
            arr.add(ro);
        }
        writeJsonToFile(arr, filename);
    }

    private void writeJsonToFile(JsonElement json, String filename) {
        try (Writer writer = Files.newBufferedWriter(Paths.get(filename), StandardCharsets.UTF_8)) {
            gson.toJson(json, writer);
            System.out.println("Datos guardados en " + filename);
        } catch (IOException e) {
            System.out.println("Error al guardar " + filename + ": " + e.getMessage());
        }
    }

    // ----------------- CARGAR -----------------
    public List<User> loadUsers() {
        JsonElement root = readJsonFromFileOrResource(usersFile);
        List<User> out = new ArrayList<>();
        if (root == null || !root.isJsonArray()) return out;
        JsonArray arr = root.getAsJsonArray();
        for (JsonElement e : arr) {
            JsonObject o = e.getAsJsonObject();
            String role = o.has("role") ? o.get("role").getAsString() : "student";
            String username = o.has("username") ? o.get("username").getAsString() : "";
            String display = o.has("displayName") ? o.get("displayName").getAsString() : username;
            switch (role.toLowerCase()) {
                case "teacher": out.add(new Teacher(username, display)); break;
                case "admin": out.add(new Admin(username, display)); break;
                default: out.add(new Student(username, display)); break;
            }
        }
        System.out.println("Usuarios cargados: " + out.size());
        return out;
    }

    public List<Test> loadTests() {
        JsonElement root = readJsonFromFileOrResource(testsFile);
        List<Test> out = new ArrayList<>();
        if (root == null || !root.isJsonArray()) return out;
        JsonArray arr = root.getAsJsonArray();
        for (JsonElement e : arr) {
            JsonObject o = e.getAsJsonObject();
            String title = o.has("title") ? o.get("title").getAsString() : "Sin título";
            Test t = new Test(title);
            if (o.has("questions") && o.get("questions").isJsonArray()) {
                for (JsonElement qe : o.get("questions").getAsJsonArray()) {
                    JsonObject qo = qe.getAsJsonObject();
                    String qtext = qo.has("questionText") ? qo.get("questionText").getAsString() : "";
                    String correct = qo.has("correctAnswer") ? qo.get("correctAnswer").getAsString() : "";
                    String type = qo.has("type") ? qo.get("type").getAsString() : "logic";
                    Question q;
                    if ("math".equalsIgnoreCase(type)) q = new MathQuestion(qtext, correct);
                    else q = new LogicQuestion(qtext, correct);
                    t.addQuestion(q);
                }
            }
            out.add(t);
        }
        System.out.println("Pruebas cargadas: " + out.size());
        return out;
    }

    public List<Result> loadResults() {
        JsonElement root = readJsonFromFileOrResource(resultsFile);
        List<Result> out = new ArrayList<>();
        if (root == null || !root.isJsonArray()) return out;
        JsonArray arr = root.getAsJsonArray();
        for (JsonElement e : arr) {
            JsonObject o = e.getAsJsonObject();
            String student = o.has("studentUsername") ? o.get("studentUsername").getAsString() : "";
            String testTitle = o.has("testTitle") ? o.get("testTitle").getAsString() : "";
            int score = o.has("score") ? o.get("score").getAsInt() : 0;
            int total = o.has("total") ? o.get("total").getAsInt() : 0;
            double percentage = o.has("percentage") ? o.get("percentage").getAsDouble() : 0.0;
            List<String> answers = new ArrayList<>();
            if (o.has("answers") && o.get("answers").isJsonArray()) {
                for (JsonElement ae : o.get("answers").getAsJsonArray()) answers.add(ae.getAsString());
            }
            Result r = new Result(student, testTitle, score, total, percentage, answers);
            out.add(r);
        }
        System.out.println("Resultados cargados: " + out.size());
        return out;
    }

    // ----------------- UTIL -----------------
    private JsonElement readJsonFromFileOrResource(String filename) {
        // 1) intenta desde working directory (ruta relativa)
        Path p = Paths.get(filename);
        if (Files.exists(p)) {
            try (Reader r = Files.newBufferedReader(p, StandardCharsets.UTF_8)) {
                return JsonParser.parseReader(r);
            } catch (IOException ex) {
                System.out.println("Error leyendo " + filename + " desde ruta: " + ex.getMessage());
            }
        }

        // 2) fallback: intentar cargar como recurso del classpath (p. ej. si está en src o dentro del JAR)
        try (InputStream is = getClass().getResourceAsStream("/com/gamerker/io/e/valua_java/" + filename)) {
            if (is != null) {
                try (Reader r = new InputStreamReader(is, StandardCharsets.UTF_8)) {
                    return JsonParser.parseReader(r);
                }
            }
        } catch (Exception ex) {
            // noop
        }

        // 3) no existe
        System.out.println("No se encontró " + filename + " (vía ruta relativa ni recurso de clase).");
        return null;
    }

    // Métodos legacy para Manageable (compatibilidad)
    @Override
    public void saveToFile(List<User> users, String filename) {
        saveUsers(users, filename);
    }

    @Override
    public List<User> loadFromFile(String filename) {
        return loadUsers();
    }
}
