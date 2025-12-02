/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.gamerker.io.e.valua_java.controllersPack;
import com.gamerker.io.e.valua_java.mainClasses.*;
import com.gamerker.io.e.valua_java.utils.*;
import com.gamerker.io.e.valua_java.interfaces.Manageable;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.lang.reflect.Type;
import java.time.LocalDateTime;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.time.Duration;
/**
 *
 * @author hp
 */
/**
 * controlador de base de datos (archivos JSON)
 * maneja carga y guardado de usuarios, pruebas, resultados, transacciones y tarjetas
 */
public class DBController implements Manageable {

    private static final String DATA_DIR = "data";
    private static final Gson gson = new GsonBuilder()
        .setPrettyPrinting()
        .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
        .registerTypeAdapter(Question.class, new QuestionDeserializer())
        .registerTypeAdapter(User.class, new UserDeserializer()) // NUEVA LÍNEA
        .create();

    static {
        try {
            Files.createDirectories(Paths.get(DATA_DIR));
        } catch (IOException e) {
            System.err.println("No se pudo crear directorio 'data': " + e.getMessage());
        }
    }
    
    // ====================== CLASE INTERNA PARA DESERIALIZAR USUARIOS ======================
    private static class UserDeserializer implements JsonDeserializer<User> {
        @Override
        public User deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            String role = jsonObject.get("role").getAsString().toLowerCase();

            // Extraer campos comunes
            String username = jsonObject.get("username").getAsString();
            String displayName = jsonObject.get("displayName").getAsString();
            double balance = jsonObject.get("balance").getAsDouble();
            String passwordHash = jsonObject.has("passwordHash") ? jsonObject.get("passwordHash").getAsString() : "";

            // Crear instancia según el rol
            User user;
            switch (role) {
                case "admin":
                    user = new Admin(username, displayName);
                    break;
                case "teacher":
                    user = new Teacher(username, displayName);
                    break;
                default:
                    user = new Student(username, displayName);
            }

            // Establecer valores
            user.setBalance(balance);
            user.setPasswordHash(passwordHash);

            return user;
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
        String json = readFile("results.json");
        if (json == null || json.trim().isEmpty()) {
            return new ArrayList<>();
        }
        try {
            JsonArray array = JsonParser.parseString(json).getAsJsonArray();
            List<Result> results = new ArrayList<>();
            for (JsonElement elem : array) {
                JsonObject obj = elem.getAsJsonObject();
                String studentUsername = obj.get("studentUsername").getAsString();
                String testTitle = obj.get("testTitle").getAsString();
                int score = obj.get("score").getAsInt();
                int total = obj.get("total").getAsInt();
                double percentage = obj.get("percentage").getAsDouble();

                JsonArray answersArray = obj.getAsJsonArray("answers");
                List<String> answers = new ArrayList<>();
                for (JsonElement ans : answersArray) {
                    answers.add(ans.getAsString());
                }
                
                Duration simulatedTime = Duration.ofMinutes(3).plusSeconds(15);
                Result result = new Result(studentUsername, testTitle, score, total, percentage, answers, simulatedTime);

                if (obj.has("archived")) {
                    result.setArchived(obj.get("archived").getAsBoolean());
                }

                // Cargar timestamp si existe
                if (obj.has("timestamp")) {
                    String timestampStr = obj.get("timestamp").getAsString();
                    result.setTimestamp(LocalDateTime.parse(timestampStr));
                }

                results.add(result);
            }
            return results;
        } catch (Exception e) {
            System.err.println("Error cargando resultados: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public void saveResults(List<Result> results) {
        JsonArray array = new JsonArray();
        for (Result r : results) {
            JsonObject obj = new JsonObject();
            obj.addProperty("studentUsername", r.getStudentUsername());
            obj.addProperty("testTitle", r.getTestTitle());
            obj.addProperty("score", r.getScore());
            obj.addProperty("total", r.getTotal());
            obj.addProperty("percentage", r.getPercentage());

            JsonArray answersArray = new JsonArray();
            for (String ans : r.getAnswers()) {
                answersArray.add(ans);
            }
            obj.add("answers", answersArray);

            obj.addProperty("archived", r.isArchived());
            obj.addProperty("timestamp", r.getTimestamp().toString()); // Guardar timestamp

            array.add(obj);
        }
        writeFile("results.json", gson.toJson(array));
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
        String json = readFile("cards.json");
        if (json == null || json.trim().isEmpty()) {
            return new ArrayList<>();
        }
        try {
            JsonArray array = JsonParser.parseString(json).getAsJsonArray();
            List<RechargeCard> loaded = new ArrayList<>();

            for (JsonElement elem : array) {
                JsonObject obj = elem.getAsJsonObject();

                String number   = obj.get("cardNumber").getAsString();
                double amount   = obj.get("amount").getAsDouble();
                String status   = obj.get("status").getAsString();
                String usedBy   = obj.has("usedBy")   ? obj.get("usedBy").getAsString()   : null;
                String usedAt   = obj.has("usedAt")   ? obj.get("usedAt").getAsString()   : null;
                String typeStr  = obj.has("type")     ? obj.get("type").getAsString()     : "WELCOME";

                RechargeCard.CardType type = 
                    "PERSONAL".equalsIgnoreCase(typeStr) 
                    ? RechargeCard.CardType.PERSONAL 
                    : RechargeCard.CardType.WELCOME;

                RechargeCard card = new RechargeCard(number, amount, type == RechargeCard.CardType.WELCOME);
                card.setStatus(status);
                if (usedBy != null) card.setUsedBy(usedBy);
                if (usedAt != null) card.setUsedAt(LocalDateTime.parse(usedAt));
                loaded.add(card);
            }
            return loaded;

        } catch (Exception e) {
            System.err.println("Error cargando tarjetas: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public void saveCards(List<RechargeCard> cards) {
        JsonArray array = new JsonArray();
        for (RechargeCard c : cards) {
            JsonObject obj = new JsonObject();
            obj.addProperty("cardNumber", c.getCardNumber());
            obj.addProperty("amount",     c.getAmount());
            obj.addProperty("status",     c.getStatus());
            obj.addProperty("type",       c.getType().name()); // GUARDAR TIPO

            if (c.getUsedBy() != null) {
                obj.addProperty("usedBy", c.getUsedBy());
                if (c.getUsedAt() != null) { // usadoAt puede no haberse seteado
                    obj.addProperty("usedAt", c.getUsedAt().toString());
                }
            }
            array.add(obj);
        }
        writeFile("cards.json", gson.toJson(array));
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
            System.out.println("Advertencia: " + filename + " está vacío o no existe. Creando lista vacía.");
            return new ArrayList<>();
        }
        try {
            return gson.fromJson(json, type);
        } catch (Exception e) {
            System.err.println("ERROR CRÍTICO: " + filename + " está corrupto: " + e.getMessage());
            System.err.println("Se renombrará a " + filename + ".backup y se creará uno nuevo.");

            // Crear backup
            try {
                Files.move(Paths.get(DATA_DIR, filename), 
                           Paths.get(DATA_DIR, filename + ".backup"));
            } catch (Exception ex) {
                System.err.println("No se pudo crear backup: " + ex.getMessage());
            }

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
    
    /**
    * Recupera un archivo JSON corrupto desde backup o crea uno nuevo
    */
    public void recoverCorruptedFile(String filename) {
       String backupFile = DATA_DIR + "/" + filename + ".backup";
       String originalFile = DATA_DIR + "/" + filename;

        try {
            if (Files.exists(Paths.get(backupFile))) {
                System.out.println("Restaurando " + filename + " desde backup...");
                Files.copy(Paths.get(backupFile), Paths.get(originalFile), StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Restauración completada.");
            } else {
                System.out.println("No hay backup disponible. Creando " + filename + " limpio...");

                if (filename.equals("users.json")) {
                    createDefaultUsersFile();
                } else if (filename.equals("tests.json")) {
                    createDefaultTestsFile();
                } else {
                    writeFile(filename, "[]"); // Crear archivo vacío
                }
            }
        } catch (Exception e) {
            System.err.println("Error durante la recuperación: " + e.getMessage());
        }
    }

    private void createDefaultUsersFile() {
       List<User> defaultUsers = new ArrayList<>();
       Admin admin = new Admin("admin", "Administrador");
       admin.setPassword("admin123");
       defaultUsers.add(admin);
       saveUsers(defaultUsers);
       System.out.println("Archivo users.json creado con usuario admin (contraseña: admin123)");
    }

    private void createDefaultTestsFile() {
       writeFile("tests.json", "[]");
       System.out.println("Archivo tests.json vacío creado");
    }
    
    @Override
    public void saveToFile(List<User> users, String filename) {
        // Lógica: si filename es "users.json", llama saveUsers(users); adapta para otros archivos
        if ("users.json".equals(filename)) {
            saveUsers(users);
        } // Agrega para tests, results, etc.
    }

    @Override
    public List<User> loadFromFile(String filename) {
        // Similar: si "users.json", return loadUsers(); devuelve List<User> genérica (adapta si es necesario)
        if ("users.json".equals(filename)) {
            return loadUsers();
        }
        return new ArrayList<>(); // Stub para otros
    }
}