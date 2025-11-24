/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.gamerker.io.e.valua_java.controllersPack;
import com.gamerker.io.e.valua_java.mainClasses.*;
import com.gamerker.io.e.valua_java.utils.*;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
/**
 *
 * @author hp
 */
/**
 * controlador de base de datos (archivos JSON)
 * maneja carga y guardado de usuarios, pruebas, resultados, transacciones y tarjetas
 */
public class DBController {

    private static final String DATA_DIR = "data";
    private static final Gson gson = new GsonBuilder()
        .setPrettyPrinting()
        .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
        .registerTypeAdapter(Question.class, new QuestionDeserializer())
        .create();

    static {
        try {
            Files.createDirectories(Paths.get(DATA_DIR));
        } catch (IOException e) {
            System.err.println("No se pudo crear directorio 'data': " + e.getMessage());
        }
    }

    // ====================== USUARIOS ======================
    public List<User> loadUsers() {
        return loadList("users.json", new TypeToken<List<User>>() {}.getType());
    }

    public void saveUsers(List<User> users) {
        List<UserJsonWrapper> wrappers = users.stream()
                .map(u -> new UserJsonWrapper(u.getRole(), u.getUsername(), u.getDisplayName(),
                        u.getBalance(), u.getPasswordHash()))
                .toList();
        saveList(wrappers, "users.json");
    }

    // ====================== PRUEBAS ======================
    public List<Test> loadTests() {
        String json = readFile("tests.json");
        if (json == null || json.trim().isEmpty()) {
            System.out.println("No se encontraron pruebas. Se creará archivo vacío.");
            return new ArrayList<>();
        }

        try {
            JsonArray array = JsonParser.parseString(json).getAsJsonArray();
            List<Test> tests = new ArrayList<>();

            for (JsonElement elem : array) {
                JsonObject obj = elem.getAsJsonObject();
                String title = obj.get("title").getAsString();
                double price = obj.has("price") ? obj.get("price").getAsDouble() : 5000.0;

                Test test = new Test(title);
                test.setPrice(price);

                JsonArray questions = obj.getAsJsonArray("questions");
                for (JsonElement qElem : questions) {
                    JsonObject qObj = qElem.getAsJsonObject();
                    String text = qObj.get("questionText").getAsString();
                    String type = qObj.has("type") ? qObj.get("type").getAsString() : "unknown";

                    JsonArray optsArray = qObj.getAsJsonArray("options");
                    List<String> options = new ArrayList<>();
                    for (JsonElement opt : optsArray) {
                        options.add(opt.getAsString());
                    }

                    String correct = qObj.get("correctAnswer").getAsString().toUpperCase();

                    Question q = new MultipleChoiceQuestion(text, options, correct, type);
                    test.addQuestion(q);
                }
                tests.add(test);
            }
            return tests;

        } catch (Exception e) {
            System.err.println("Error cargando pruebas: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void saveTests(List<Test> tests) {
        JsonArray array = new JsonArray();
        for (Test t : tests) {
            JsonObject obj = new JsonObject();
            obj.addProperty("title", t.getTitle());
            obj.addProperty("price", t.getPrice());

            JsonArray questions = new JsonArray();
            for (Question q : t.getQuestions()) {
                JsonObject qObj = new JsonObject();
                qObj.addProperty("questionText", q.getQuestionText());
                qObj.addProperty("type", q.getType());
                qObj.addProperty("correctAnswer", q.getCorrectAnswer());

                JsonArray opts = new JsonArray();
                for (String opt : q.getOptions()) {
                    opts.add(opt);
                }
                qObj.add("options", opts);
                questions.add(qObj);
            }
            obj.add("questions", questions);
            array.add(obj);
        }
        writeFile("tests.json", gson.toJson(array));
    }

    // ====================== RESULTADOS ======================
    public List<Result> loadResults() {
        return loadList("results.json", new TypeToken<List<Result>>() {}.getType());
    }

    public void saveResults(List<Result> results) {
        saveList(results, "results.json");
    }

    // ====================== TRANSACCIONES ======================
    public List<Transaction> loadTransactions() {
        return loadList("transactions.json", new TypeToken<List<Transaction>>() {}.getType());
    }

    public void saveTransactions(List<Transaction> transactions) {
        saveList(transactions, "transactions.json");
    }

    // ====================== TARJETAS DE RECARGA ======================
    public List<RechargeCard> loadCards() {
        return loadList("cards.json", new TypeToken<List<RechargeCard>>() {}.getType());
    }

    public void saveCards(List<RechargeCard> cards) {
        saveList(cards, "cards.json");
    }

    // ====================== MÉTODOS GENÉRICOS ======================
    private String readFile(String filename) {
        try {
            Path path = Paths.get(DATA_DIR, filename);
            if (!Files.exists(path)) {
                Files.createFile(path);
                return "[]";
            }
            return Files.readString(path);
        } catch (Exception e) {
            System.err.println("Error leyendo " + filename + ": " + e.getMessage());
            return null;
        }
    }

    private void writeFile(String filename, String content) {
        try {
            Path path = Paths.get(DATA_DIR, filename);
            Files.writeString(path, content, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (Exception e) {
            System.err.println("Error escribiendo " + filename + ": " + e.getMessage());
        }
    }

    private <T> List<T> loadList(String filename, Type type) {
        String json = readFile(filename);
        if (json == null || json.trim().isEmpty() || "null".equals(json)) {
            return new ArrayList<>();
        }
        try {
            return gson.fromJson(json, type);
        } catch (Exception e) {
            System.err.println("Error parseando " + filename + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private void saveList(Object list, String filename) {
        writeFile(filename, gson.toJson(list));
    }

    // ====================== CLASES INTERNAS ======================
    private static class UserJsonWrapper {
        String role, username, displayName, passwordHash;
        double balance;

        UserJsonWrapper(String role, String username, String displayName, double balance, String passwordHash) {
            this.role = role;
            this.username = username;
            this.displayName = displayName;
            this.balance = balance;
            this.passwordHash = passwordHash;
        }

        String toUserLine() {
            return String.format("%s|%s|%s|%.2f|%s", role, username, displayName, balance, passwordHash != null ? passwordHash : "");
        }
    }

    // Necesario porque Question es abstracta
    private static class QuestionDeserializer implements JsonDeserializer<Question> {
        @Override
        public Question deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
            JsonObject obj = json.getAsJsonObject();
            String text = obj.get("questionText").getAsString();
            String type = obj.has("type") ? obj.get("type").getAsString() : "unknown";
            String correct = obj.get("correctAnswer").getAsString().toUpperCase();

            JsonArray opts = obj.getAsJsonArray("options");
            List<String> options = new ArrayList<>();
            for (JsonElement e : opts) options.add(e.getAsString());

            return new MultipleChoiceQuestion(text, options, correct, type);
        }
    }
}