/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gamerker.io.e.valua_java.controllersPack;
import com.gamerker.io.e.valua_java.mainClasses.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.Gson;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.io.IOException;
import java.util.*;
/**
 *
 * @author hp
 */
/**
 * controlador principal de la aplicacion
 * maneja menu, autenticacion y flujo de usuario
 */
public class AppController {

    private final Scanner scanner = new Scanner(System.in);
    private final DBController db = new DBController();
    private final BillingController billing = new BillingController();
    private final RechargeController recharge = new RechargeController();

    private List<User> users;
    private List<Test> tests;
    private List<Result> results;
    private List<Transaction> transactions;
    private User currentUser = null;
    
    // Persistencia de sesión
    private static final String SESSION_FILE = "data/session.json";
    
    public AppController() {
        try {
            loadAllData();
            currentUser = loadActiveSession();
        } catch (Exception e) {
            System.err.println("\nERROR CRÍTICO AL CARGAR DATOS: " + e.getMessage());
            System.err.println("Esto suele ocurrir si un archivo JSON está corrupto o mal formateado.");

            // Identificar archivo específico
            if (e.getMessage().contains("users.json")) {
                System.err.println("\nEl archivo users.json está corrupto o incompleto.");
                System.err.println("¿Deseas intentar recuperarlo? (s/n): ");

                Scanner scanner = new Scanner(System.in);
                String response = scanner.nextLine().trim().toLowerCase();

                if (response.equals("s")) {
                    db.recoverCorruptedFile("users.json");
                    System.out.println("\nReintentando carga de datos...");
                    loadAllData(); // Reintentar
                } else {
                    System.err.println("No se pudo iniciar el sistema. Saliendo...");
                    System.exit(1);
                }
            }
        }
    }
    
    // ========== MANEJO DE SESIÓN ==========
    // Cargar sesión activa
    private User loadActiveSession() {
        try {
            String json = new String(Files.readAllBytes(Paths.get(SESSION_FILE)));
            JsonObject session = JsonParser.parseString(json).getAsJsonObject();
            String username = session.get("activeUsername").getAsString();

            // Verificar que la sesión no haya expirado (máximo 24 horas)
            LocalDateTime lastActivity = LocalDateTime.parse(session.get("lastActivity").getAsString());
            if (ChronoUnit.HOURS.between(lastActivity, LocalDateTime.now()) > 24) {
                Files.deleteIfExists(Paths.get(SESSION_FILE));
                return null;
            }

            return users.stream()
                       .filter(u -> u.getUsername().equals(username))
                       .findFirst()
                       .orElse(null);
        } catch (Exception e) {
            return null; // No hay sesión activa
        }
    }

    // Guardar sesión activa
    private void saveActiveSession() {
        try {
            JsonObject session = new JsonObject();
            session.addProperty("activeUsername", currentUser.getUsername());
            session.addProperty("sessionStart", LocalDateTime.now().toString());
            session.addProperty("lastActivity", LocalDateTime.now().toString());
            Files.writeString(Paths.get(SESSION_FILE), new Gson().toJson(session));
        } catch (Exception e) {
            System.err.println("Error guardando sesión: " + e.getMessage());
        }
    }

    // Limpiar sesión al cerrar sesión
    private void clearActiveSession() {
        try {
            Files.deleteIfExists(Paths.get(SESSION_FILE));
            loginMenu();
        } catch (Exception e) {
            // Ignorar error
        }
    }

    public void run() {
        System.out.print("""
            |======================================|
            |          BIENVENIDO A E-VALUA        |
            |======================================|
            """);

        // Si hay sesión activa, saltar login
        if (currentUser != null) {
            System.out.println("\nSesión activa detectada para: " + currentUser.getDisplayName());
            System.out.println("Última actividad: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            System.out.println("Presione Enter para continuar o 'n' para cerrar sesión...");
            String option = scanner.nextLine().trim().toLowerCase();
            if (option.equals("n")) {
                clearActiveSession();
                currentUser = null;
            }
        }

        while (true) {
            if (currentUser == null) {
                loginMenu();
            } else {
                billing.startSession(currentUser.getUsername());
                userMenu();
                Transaction sessionCharge = billing.endSession(currentUser.getUsername());
                if (sessionCharge != null) {
                    currentUser.addTransaction(sessionCharge);
                    transactions.add(sessionCharge);
                    saveAll();
                }
            }
        }
    }
    
    private void registerNewUser() {
        System.out.print("Rol (student/teacher/admin) [Deje vacío para Estudiante]: ");
        String role = scanner.nextLine().trim().toLowerCase();
        if (role.isEmpty()) role = "student";

        System.out.print("Usuario: ");
        String username = scanner.nextLine().trim();
        if (username.isEmpty()) {
            System.out.println("Usuario no puede estar vacío.");
            return;
        }

        // Verificar si ya existe
        if (users.stream().anyMatch(u -> u.getUsername().equalsIgnoreCase(username))) {
            System.out.println("Ese usuario ya existe.");
            return;
        }

        System.out.print("Nombre visible: ");
        String displayName = scanner.nextLine().trim();
        if (displayName.isEmpty()) displayName = username;

        System.out.print("Contraseña: ");
        String password = scanner.nextLine();

        // Crear usuario según rol
        User newUser;
        switch (role) {
            case "admin" -> newUser = new Admin(username, displayName);
            case "teacher" -> newUser = new Teacher(username, displayName);
            default -> newUser = new Student(username, displayName);
        }

        if (!password.isEmpty()) {
            newUser.setPassword(password);
        }

        // === TARJETA DE BIENVENIDA ===
        RechargeCard welcomeCard = recharge.generateWelcomeCard(newUser);

        // Aplicar el saldo de la tarjeta al usuario
        Transaction welcomeTransaction = billing.registerPayment(
            newUser.getUsername(),
            welcomeCard.getAmount(),
            "Saldo inicial de bienvenida - " + welcomeCard.getCardNumber()
        );
        newUser.addTransaction(welcomeTransaction);

        // Agregar a la lista y guardar
        users.add(newUser);
        saveAll();

        System.out.println("\n¡Usuario creado con éxito!");
        System.out.println("→ Saldo inicial: " + welcomeCard.getFormattedAmount());
        System.out.println("→ Tarjeta de bienvenida: " + welcomeCard.getCardNumber());
        System.out.println("Ya puedes iniciar sesión.");
    }

    private void loginMenu() {
        System.out.print("\n1. Ingresar\n2. Registrarse\n3. Salir\nOpción: ");
        String opt = scanner.nextLine();

        switch (opt) {
            case "1" -> {
                System.out.print("Usuario: ");
                String user = scanner.nextLine().trim();
                System.out.print("Contraseña: ");
                String pass = scanner.nextLine();

                currentUser = users.stream()
                        .filter(u -> u.getUsername().equalsIgnoreCase(user))
                        .filter(u -> u.verifyPassword(pass))
                        .findFirst()
                        .orElse(null);

                if (currentUser != null) {
                    System.out.println("\n¡Bienvenido, " + currentUser.getDisplayName() + "!");
                    saveActiveSession(); // Guardamos sesión al iniciar
                    return;
                } else {
                    System.out.println("Credenciales incorrectas.");
                }
            }
            case "2" -> registerNewUser(); 
            case "3" -> {
                System.out.println("¡Hasta pronto!");
                System.exit(0);
            }
            default -> System.out.println("Opción no válida. Intenta de nuevo.");
        }
    }

    private void userMenu() {
        while (true) {
            // Verificar saldo negativo al inicio del menú
            if (currentUser.getBalance() < -1000) { // Umbral configurable
                System.out.println("\nADVERTENCIA: TU SALDO ES MUY NEGATIVO");
                System.out.printf("Deuda actual: -$%,.0f%n", Math.abs(currentUser.getBalance()));
                System.out.println("No puedes realizar pruebas hasta recargar saldo.\n");
            }

            System.out.println("\n=== Menú Principal - " + currentUser.getDisplayName() + " ===");
            System.out.println(RechargeController.getBalanceStatus(currentUser));

            // Menú base para todos
            System.out.println("0. Cerrar sesión");
            System.out.println("1. Ver pruebas disponibles");
            System.out.println("2. Realizar prueba");
            System.out.println("3. Mis resultados");
            System.out.println("4. Recargar saldo");
            System.out.println("5. Generar factura del día");
            System.out.println("6. Ver ranking global");
            System.out.println("7. Ver ranking por prueba");

            // Opciones exclusivas para profesor y admin
            if ("teacher".equals(currentUser.getRole()) || "admin".equals(currentUser.getRole())) {
                System.out.println("8. Crear nueva prueba");
                if ("admin".equals(currentUser.getRole())) {
                    System.out.println("9. Gestionar usuarios");
                    System.out.print("Opción: ");
                } else {
                    System.out.print("Opción (1-8): ");
                }
            } else {
                System.out.print("Opción (1-7): ");
            }

            String opt = scanner.nextLine().trim();

            switch (opt) {
                case "0" -> {
                    System.out.println("Sesión cerrada.");
                    clearActiveSession();
                    return;
                }
                case "1" -> listTests();
                case "2" -> {
                    if (canTakeTest()) {
                        takeTest();
                    } else {
                        System.out.println("No puedes realizar la prueba por saldo insuficiente.\n");
                    }
                }
                case "3" -> viewResults();
                case "4" -> rechargeBalance();
                case "5" -> generateDailyInvoice();
                case "6" -> showGlobalRanking();
                case "7" -> showTestRanking();

                // === OPCIONES EXCLUSIVAS PROFESOR/ADMIN ===
                case "8" -> {
                    if ("teacher".equals(currentUser.getRole()) || "admin".equals(currentUser.getRole())) {
                        createTest();
                    } else {
                        System.out.println("Opción no válida.");
                    }
                }
                case "9" -> {
                    if ("admin".equals(currentUser.getRole())) {
                        manageUsersMenu();
                    } else {
                        System.out.println("Opción no válida.");
                    }
                }
                default -> System.out.println("Opción no válida. Intenta de nuevo.");
            }
        }
    }

    private void loadAllData() {
        users = db.loadUsers();
        tests = db.loadTests();
        results = db.loadResults();
        transactions = db.loadTransactions();
    }

    private void saveAll() {
        db.saveUsers(users);
        db.saveTests(tests);
        db.saveResults(results);
        db.saveTransactions(transactions);
    }

    // Métodos auxiliares
    private boolean canCreateTests() {
        return "teacher".equals(currentUser.getRole()) || "admin".equals(currentUser.getRole());
    }

    // ====================== MÉTODOS DEL MENÚ ======================
    private void listTests() {
        System.out.println("\n=== PRUEBAS DISPONIBLES ===");
        if (tests.isEmpty()) {
            System.out.println("No hay pruebas disponibles.");
            return;
        }
        for (int i = 0; i < tests.size(); i++) {
            Test t = tests.get(i);
            System.out.printf("%d. %s - $%,.0f%n", i + 1, t.getTitle(), t.getPrice());
        }
    }
    
    private boolean canTakeTest() {
        if (tests.isEmpty()) {
            System.out.println("No hay pruebas disponibles.");
            return false;
        }

        return true;
    }

    private void takeTest() {
        if (tests.isEmpty()) {
            System.out.println("No hay pruebas disponibles.");
            return;
        }

        System.out.print("Seleccione número de prueba: ");
        try {
            int idx = Integer.parseInt(scanner.nextLine()) - 1;
            if (idx < 0 || idx >= tests.size()) {
                System.out.println("Número inválido.");
                return;
            }

            Test test = tests.get(idx);
            double testPrice = test.getPrice();

            // Verificar saldo real negativo
            if (currentUser.getBalance() < 0) {
                System.out.println("\nSALDO NEGATIVO DETECTADO");
                System.out.printf("Tu saldo actual es: -$%,.0f%n", Math.abs(currentUser.getBalance()));
                System.out.println("No puedes realizar más pruebas hasta recargar saldo.");
                System.out.println("Por favor, dirígete al menú de recarga (opción 4).");
                return; // Bloquear acceso
            }

            // Verificar saldo suficiente (incluyendo crédito)
            if (!RechargeController.hasSufficientBalance(currentUser, testPrice)) {
                System.out.println("\nSALDO INSUFICIENTE para realizar esta prueba.");
                System.out.println(RechargeController.getBalanceStatus(currentUser));
                System.out.println("Debe recargar antes de continuar.");
                return;
            }

            System.out.println("\nIniciando prueba: " + test.getTitle());
            System.out.println("Precio: $" + String.format("%,.0f", test.getPrice()));
            System.out.print("¿Confirmar? (s/n): ");
            if (!scanner.nextLine().trim().equalsIgnoreCase("s")) {
                System.out.println("Prueba cancelada.");
                return;
            }

            // Cobrar por la prueba
            Transaction charge = billing.chargeForTest(currentUser.getUsername(), test);
            currentUser.addTransaction(charge);
            transactions.add(charge);

            // Realizar la prueba
            List<String> answers = new ArrayList<>();
            int correctCount = 0;

            for (int q = 0; q < test.getQuestions().size(); q++) {
                Question question = test.getQuestions().get(q);
                System.out.printf("\nPregunta %d/%d:\n%s\n", q + 1, test.getQuestions().size(), question.getQuestionText());
                char opt = 'A';
                for (String option : question.getOptions()) {
                    System.out.println(opt + ") " + option);
                    opt++;
                }

                System.out.print("Tu respuesta (A/B/C/D): ");
                String userAnswer = scanner.nextLine().trim().toUpperCase();

                if (question.verifyAnswer(userAnswer)) {
                    correctCount++;
                    System.out.println("Correcto!");
                } else {
                    System.out.println("Incorrecto. Respuesta correcta: " +
                            question.getCorrectAnswer() + ") " + question.getCorrectOptionText());
                }
                answers.add(userAnswer.isEmpty() ? "(sin respuesta)" : userAnswer);
            }

            double percentage = test.getTotalQuestions() > 0 ?
                    (correctCount * 100.0 / test.getTotalQuestions()) : 0;

            Result result = new Result(
                    currentUser.getUsername(),
                    test.getTitle(),
                    correctCount,
                    test.getTotalQuestions(),
                    percentage,
                    answers
            );

            results.add(result);
            saveAll();
            
            exportResultPdf();

            System.out.println("\n=== RESULTADO FINAL ===");
            System.out.printf("Puntuación: %d/%d (%.2f%%)%n", correctCount, test.getTotalQuestions(), percentage);

            if (percentage >= 60.0) {
                if (percentage >= 90.0) {
                    System.out.println("¡EXCELENTE! Aprobado con honores");
                } else if (percentage >= 75.0) {
                    System.out.println("¡MUY BIEN! Has APROBADO");
                } else {
                    System.out.println("¡FELICITACIONES! Has APROBADO");
                }
            } else {
                System.out.println("Sigue practicando");
                System.out.println("¡No te rindas! La próxima será");
            }

            System.out.println(); // línea en blanco
        } catch (Exception e) {
            System.out.println("Error: Entrada inválida.");
        }
    }

    private void viewResults() {
        List<Result> myResults = results.stream()
                .filter(r -> r.getStudentUsername().equals(currentUser.getUsername()))
                .sorted((a, b) -> Double.compare(b.getPercentage(), a.getPercentage()))
                .toList();

        if (myResults.isEmpty()) {
            System.out.println("Aún no has realizado ninguna prueba.");
            return;
        }

        System.out.println("\n=== TUS RESULTADOS ===");
        for (int i = 0; i < myResults.size(); i++) {
            Result r = myResults.get(i);
            System.out.printf("%d. %s → %d/%d (%.2f%%)%n",
                    i + 1, r.getTestTitle(), r.getScore(), r.getTotal(), r.getPercentage());
        }
    }

    private void rechargeBalance() {
        System.out.println("\n=== RECARGA DE SALDO ===");
        System.out.print("Código de tarjeta (EVAX-XXXX-XXXX-XXXX-XXXX): ");
        String code = scanner.nextLine().trim();
        System.out.print("Referencia (opcional): ");
        String ref = scanner.nextLine().trim();

        try {
            Transaction payment = recharge.processRecharge(currentUser, code, ref);
            currentUser.addTransaction(payment);
            transactions.add(payment);
            saveAll();

            System.out.println("\nRECARGA EXITOSA");
            System.out.println("Monto: $" + String.format("%,.0f", payment.getAmount()));
            System.out.println("Nuevo saldo: $" + String.format("%,.0f", currentUser.getBalance()));
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void createTest() {
        if (!canCreateTests()) return;

        System.out.println("\n=== CREAR NUEVA PRUEBA ===");
        System.out.print("Título de la prueba: ");
        String title = scanner.nextLine().trim();
        if (title.isEmpty()) {
            System.out.println("Título no puede estar vacío.");
            return;
        }

        System.out.print("Precio (ej: 5000): ");
        double price = 5000.0;
        try {
            price = Double.parseDouble(scanner.nextLine());
            if (price <= 0) price = 5000.0;
        } catch (Exception e) {
            System.out.println("Precio inválido. Se usará $5.000");
        }

        Test test = new Test(title);
        test.setPrice(price);

        System.out.println("Agregue preguntas (deje el texto vacío para terminar):");
        int qNum = 1;
        while (true) {
            System.out.printf("\nPregunta %d:\nTexto: ", qNum);
            String qText = scanner.nextLine().trim();
            if (qText.isEmpty()) break;

            List<String> options = new ArrayList<>();
            for (char c = 'A'; c <= 'D'; c++) {
                System.out.print(c + ") ");
                String opt = scanner.nextLine().trim();
                if (opt.isEmpty()) {
                    System.out.println("Opción no puede estar vacía.");
                    c--;
                    continue;
                }
                options.add(opt);
            }

            System.out.print("Respuesta correcta (A/B/C/D): ");
            String correct = scanner.nextLine().trim().toUpperCase();
            if (!"A,B,C,D".contains(correct)) {
                System.out.println("Respuesta inválida. Se tomará 'A' por defecto.");
                correct = "A";
            }

            System.out.print("Tipo (logic/math/verbal) [Enter para 'logic']: ");
            String type = scanner.nextLine().trim();
            if (type.isEmpty()) type = "logic";

            try {
                Question q = new MultipleChoiceQuestion(qText, options, correct, type);
                test.addQuestion(q);
                qNum++;
            } catch (Exception e) {
                System.out.println("Error creando pregunta: " + e.getMessage());
            }
        }

        if (test.getQuestions().isEmpty()) {
            System.out.println("No se agregó ninguna pregunta. Prueba cancelada.");
        } else {
            tests.add(test);
            saveAll();
            System.out.println("Prueba '" + title + "' creada exitosamente con " +
                    test.getQuestions().size() + " preguntas.");
        }
    }
    
    private void exportResultPdf() {
        // Después de mostrar resultados
        System.out.print("¿Exportar resultado a PDF? (s/n): ");
        if (scanner.nextLine().trim().equalsIgnoreCase("s")) {
            Result lastResult = results.get(results.size() - 1);
            String path = new ResultPdfController().exportResultToPdf(lastResult, currentUser);
            if (path != null) {
                Transaction charge = billing.chargeForPdfExport(currentUser.getUsername(), lastResult.getTestTitle());
                currentUser.addTransaction(charge);
                transactions.add(charge);
                saveAll();
            }
        }
    }

    private void generateDailyInvoice() {
        LocalDateTime today = LocalDateTime.now();
        List<Transaction> daily = billing.getDailyTransactions(currentUser.getUsername(), today);
        if (daily.isEmpty()) {
            System.out.println("No hay consumos hoy.");
            return;
        }
        new InvoicePdfController().generateInvoice(currentUser, daily, today);
    }
    
    // RANKING MODE
    private void showGlobalRanking() {
        if (results.isEmpty()) {
            System.out.println("Aún no hay resultados registrados.");
            return;
        }

        // Agrupamos todos los resultados por usuario y calculamos su mejor puntaje general
        Map<String, Result> bestResults = new HashMap<>();

        for (Result r : results) {
            String user = r.getStudentUsername();
            Result currentBest = bestResults.get(user);

            if (currentBest == null || r.getPercentage() > currentBest.getPercentage()) {
                bestResults.put(user, r);
            }
        }

        // Ordenamos por porcentaje descendente
        List<Map.Entry<String, Result>> ranking = new ArrayList<>(bestResults.entrySet());
        ranking.sort((a, b) -> Double.compare(b.getValue().getPercentage(), a.getValue().getPercentage()));

        System.out.println("\n=== RANKING GLOBAL DE ESTUDIANTES ===");
        System.out.println("Posición | Estudiante                  | Mejor %   | Prueba");
        System.out.println("---------|-----------------------------|-----------|---------------------------");

        for (int i = 0; i < ranking.size(); i++) {
            Result r = ranking.get(i).getValue();
            User user = findUserByUsername(r.getStudentUsername());
            String name = user != null ? user.getDisplayName() : r.getStudentUsername();
            String position = (i + 1) + getPositionSuffix(i + 1);

            System.out.printf("%-8s | %-27s | %6.2f%%  | %s%n",
                    position, truncate(name, 27), r.getPercentage(), r.getTestTitle());
        }

        System.out.println("\n¡Sigue practicando para llegar al podio!\n");
    }

    private void showTestRanking() {
        if (tests.isEmpty()) {
            System.out.println("No hay pruebas disponibles.");
            return;
        }

        System.out.println("\n=== RANKING POR PRUEBA ===");
        for (int i = 0; i < tests.size(); i++) {
            System.out.printf("%d. %s%n", i + 1, tests.get(i).getTitle());
        }

        System.out.print("Selecciona el número de prueba: ");
        String input = scanner.nextLine().trim();
        int index;
        try {
            index = Integer.parseInt(input) - 1;
            if (index < 0 || index >= tests.size()) {
                System.out.println("Opción inválida.");
                return;
            }
        } catch (Exception e) {
            System.out.println("Entrada inválida.");
            return;
        }

        Test selected = tests.get(index);
        List<Result> testResults = results.stream()
                .filter(r -> r.getTestTitle().equals(selected.getTitle()))
                .sorted((a, b) -> Double.compare(b.getPercentage(), a.getPercentage()))
                .toList();

        if (testResults.isEmpty()) {
            System.out.printf("Aún nadie ha realizado la prueba: %s%n%n", selected.getTitle());
            return;
        }

        System.out.printf("%n=== RANKING: %s ===%n", selected.getTitle());
        System.out.println("Pos. | Estudiante                    | Puntaje   | Porcentaje | Fecha");
        System.out.println("-----|-------------------------------|-----------|------------|-----------");

        for (int i = 0; i < testResults.size(); i++) {
            Result r = testResults.get(i);
            User user = findUserByUsername(r.getStudentUsername());
            String name = user != null ? user.getDisplayName() : r.getStudentUsername();

            System.out.printf("%-4s | %-29s | %3d/%-3d   | %7.2f%%   | %s%n",
                    (i + 1) + getPositionSuffix(i + 1),
                    truncate(name, 29),
                    r.getScore(), r.getTotal(),
                    r.getPercentage(),
                    r.getAnswers().get(0)); // Hack: usamos primera respuesta como timestamp si no tienes campo fecha
        }
        System.out.println();
    }

    // Utilidades para el ranking
    private String getPositionSuffix(int position) {
        return switch (position) {
            case 1 -> "er";
            case 2 -> "do";
            case 3 -> "er";
            default -> "º";
        };
    }

    private String truncate(String text, int length) {
        return text.length() > length ? text.substring(0, length - 3) + "..." : text;
    }

    private User findUserByUsername(String username) {
        return users.stream()
                .filter(u -> u.getUsername().equalsIgnoreCase(username))
                .findFirst()
                .orElse(null);
    }
    
    // MANAGE USERS
    private void manageUsersMenu() {
        if (!"admin".equals(currentUser.getRole())) {
            System.out.println("Acceso denegado.");
            return;
        }

        while (true) {
            System.out.println("\n=== GESTIÓN DE USUARIOS (Administrador) ===");
            System.out.println("1. Listar todos los usuarios");
            System.out.println("2. Crear nuevo usuario");
            System.out.println("3. Modificar usuario");
            System.out.println("4. Eliminar usuario");
            System.out.println("5. Ver detalles de un usuario");
            System.out.println("0. Volver al menú principal");
            System.out.print("Opción: ");

            String opt = scanner.nextLine().trim();

            switch (opt) {
                case "1" -> listAllUsers();
                case "2" -> registerNewUser();
                case "3" -> modifyUser();
                case "4" -> deleteUser();
                case "5" -> viewUserDetails();
                case "0" -> {
                    return;
                }
                default -> System.out.println("Opción inválida.");
            }
        }
    }

    private void listAllUsers() {
        if (users.isEmpty()) {
            System.out.println("No hay usuarios registrados.");
            return;
        }

        System.out.println("\n=== LISTA DE USUARIOS ===");
        System.out.println("Usuario          | Nombre Visible           | Rol       | Saldo");
        System.out.println("-----------------|--------------------------|-----------|------------");

        for (User u : users) {
            System.out.printf("%-16s | %-24s | %-9s | $%,.0f%n",
                    u.getUsername(),
                    truncate(u.getDisplayName(), 24),
                    u.getRole().toUpperCase(),
                    u.getBalance());
        }
        System.out.println();
    }

    private void modifyUser() {
        System.out.print("Usuario a modificar: ");
        String username = scanner.nextLine().trim();
        User user = findUserByUsername(username);

        if (user == null) {
            System.out.println("Usuario no encontrado.");
            return;
        }

        System.out.println("Usuario encontrado: " + user.getDisplayName() + " (" + user.getRole() + ")");
        System.out.println("1. Cambiar nombre visible");
        System.out.println("2. Cambiar contraseña");
        System.out.println("3. Cambiar rol (admin/teacher/student)");
        System.out.print("Opción: ");
        String opt = scanner.nextLine().trim();

        switch (opt) {
            case "1" -> {
                System.out.print("Nuevo nombre visible: ");
                String newName = scanner.nextLine().trim();
                if (!newName.isEmpty()) {
                    user.setDisplayName(newName);
                    System.out.println("Nombre actualizado.");
                }
            }
            case "2" -> {
                System.out.print("Nueva contraseña: ");
                String pass = scanner.nextLine();
                if (pass.isEmpty()) {
                    user.setPasswordHash("0123456");
                    System.out.println("Contraseña nueva es: 0123456");
                } else {
                    user.setPassword(pass);
                    System.out.println("Contraseña actualizada.");
                }
            }
            case "3" -> {
                System.out.print("Nuevo rol (student/teacher/admin): ");
                String newRole = scanner.nextLine().trim().toLowerCase();
                if ("admin".equals(newRole) || "teacher".equals(newRole) || "student".equals(newRole)) {
                    // Cambiar rol: recreamos el objeto (mejor forma)
                    User newUser = switch (newRole) {
                        case "admin" -> new Admin(user.getUsername(), user.getDisplayName());
                        case "teacher" -> new Teacher(user.getUsername(), user.getDisplayName());
                        default -> new Student(user.getUsername(), user.getDisplayName());
                    };
                    newUser.setBalance(user.getBalance());
                    newUser.setPasswordHash(user.getPasswordHash());
                    newUser.setTransactions(user.getTransactions());

                    users.remove(user);
                    users.add(newUser);
                    System.out.println("Rol cambiado a: " + newRole);
                } else {
                    System.out.println("Rol inválido.");
                }
            }
            default -> System.out.println("Opción no válida.");
        }
        saveAll();
    }

    private void deleteUser() {
        System.out.print("Usuario a eliminar (escribe exactamente): ");
        String username = scanner.nextLine().trim();
        if (username.equalsIgnoreCase(currentUser.getUsername())) {
            System.out.println("No puedes eliminarte a ti mismo.");
            return;
        }

        User user = findUserByUsername(username);
        if (user == null) {
            System.out.println("Usuario no encontrado.");
            return;
        }

        System.out.print("¿Seguro que deseas eliminar a " + user.getDisplayName() + "? (s/N): ");
        if (!scanner.nextLine().trim().equalsIgnoreCase("s")) {
            System.out.println("Operación cancelada.");
            return;
        }

        users.remove(user);
        // Opcional: eliminar sus resultados
        results.removeIf(r -> r.getStudentUsername().equalsIgnoreCase(username));
        saveAll();
        System.out.println("Usuario eliminado correctamente.");
    }

    private void viewUserDetails() {
        System.out.print("Usuario: ");
        String username = scanner.nextLine().trim();
        User user = findUserByUsername(username);

        if (user == null) {
            System.out.println("Usuario no encontrado.");
            return;
        }

        System.out.println("\n=== DETALLES DEL USUARIO ===");
        System.out.println("Usuario: " + user.getUsername());
        System.out.println("Nombre: " + user.getDisplayName());
        System.out.println("Rol: " + user.getRole().toUpperCase());
        System.out.println("Saldo actual: $%,.0f".formatted(user.getBalance()));
        System.out.println("Crédito disponible: " + (user.getRole().equals("student") ? "$10.000" : "$50.000"));
        System.out.println("Total transacciones: " + user.getTransactions().size());

        // Mostrar últimos 5 movimientos
        var recent = user.getTransactions().stream()
                .sorted((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()))
                .limit(5)
                .toList();

        if (!recent.isEmpty()) {
            System.out.println("\nÚltimos movimientos:");
            for (Transaction t : recent) {
                System.out.printf(" • %s | %s | $%,.0f%n",
                        t.getFormattedDate().substring(0, 16),
                        t.getConcept(),
                        t.getAmount());
            }
        }
        System.out.println();
    }
}