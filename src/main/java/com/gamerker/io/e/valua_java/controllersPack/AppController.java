/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gamerker.io.e.valua_java.controllersPack;
import com.gamerker.io.e.valua_java.mainClasses.*;
import java.util.*;
/**
 *
 * @author hp
 */
public class AppController {
    private Scanner scanner = new Scanner(System.in);
    private List<User> users = new ArrayList<>();
    private List<Test> tests = new ArrayList<>();
    private List<Result> results = new ArrayList<>();
    private DBController db = new DBController();

    public AppController() {
        // Cargar desde JSON si existen
        users = db.loadUsers();
        tests = db.loadTests();
        results = db.loadResults();
    }

    public void run() {
        System.out.println("=== Bienvenido a E-valua ===");
        while (true) {
            System.out.println("\nMenú principal:");
            System.out.println("1. Iniciar sesión");
            System.out.println("2. Registrar usuario (estudiante/profesor)");
            System.out.println("3. Guardar y salir");
            System.out.print("Seleccione una opción: ");
            String option = scanner.nextLine().trim();
            switch (option) {
                case "1": login(); break;
                case "2": register(); break;
                case "3":
                    db.saveAll(users, tests, results);
                    System.out.println("Datos guardados. ¡Hasta luego!");
                    return;
                default:
                    System.out.println("Opción no válida.");
            }
        }
    }

    private void login() {
        System.out.print("Usuario: ");
        String username = scanner.nextLine().trim();
        Optional<User> opt = users.stream().filter(u -> u.getUsername().equals(username)).findFirst();
        if (!opt.isPresent()) {
            System.out.println("Usuario no encontrado.");
            return;
        }
        User user = opt.get();
        System.out.println("Bienvenido, " + user.getDisplayName() + " (" + user.getRole() + ")");
        if (user instanceof Teacher) teacherMenu((Teacher) user);
        else if (user instanceof Student) studentMenu((Student) user);
        else System.out.println("Tipo de usuario no soportado.");
    }

    private void register() {
        System.out.print("Tipo (student/teacher): ");
        String tipo = scanner.nextLine().trim().toLowerCase();
        System.out.print("Usuario: ");
        String username = scanner.nextLine().trim();
        System.out.print("Nombre para mostrar: ");
        String display = scanner.nextLine().trim();

        if (users.stream().anyMatch(u -> u.getUsername().equals(username))) {
            System.out.println("Ya existe un usuario con ese nombre.");
            return;
        }

        if (tipo.equals("teacher")) users.add(new Teacher(username, display));
        else users.add(new Student(username, display));

        db.saveToFile(users, "users.json");
        System.out.println("Usuario registrado con éxito.");
    }

    private void teacherMenu(Teacher t) {
        while (true) {
            System.out.println("\nMenú Profesor:");
            System.out.println("1. Crear prueba");
            System.out.println("2. Listar pruebas");
            System.out.println("3. Ver resultados");
            System.out.println("4. Volver");
            System.out.print("Opción: ");
            String opt = scanner.nextLine().trim();
            switch (opt) {
                case "1":
                    Test test = createTest();
                    if (test != null) {
                        tests.add(test);
                        System.out.println("Prueba creada: " + test.getTitle());
                    }
                    break;
                case "2":
                    listTests();
                    break;
                case "3":
                    listResults();
                    break;
                case "4": return;
                default:
                    System.out.println("Opción no válida.");
            }
        }
    }

    private void studentMenu(Student s) {
        while (true) {
            System.out.println("\nMenú Estudiante:");
            System.out.println("1. Ver pruebas disponibles");
            System.out.println("2. Realizar prueba");
            System.out.println("3. Ver mis resultados");
            System.out.println("4. Volver");
            System.out.print("Opción: ");
            String opt = scanner.nextLine().trim();
            switch (opt) {
                case "1": listTests(); break;
                case "2": takeTest(s); break;
                case "3": listResultsForStudent(s); break;
                case "4": return;
                default: System.out.println("Opción no válida.");
            }
        }
    }

    private void adminMenu(Admin a) {
        System.out.println("Me falta poner esto");
    }

    private Test createTest() {
        System.out.print("Título de la prueba: ");
        String title = scanner.nextLine().trim();
        Test test = new Test(title);
        System.out.println("Agregar preguntas. Escriba 'fin' para terminar.");
        while (true) {
            System.out.print("Tipo de pregunta (math/logic): ");
            String type = scanner.nextLine().trim().toLowerCase();
            if (type.equals("fin")) break;
            if (!(type.equals("math") || type.equals("logic"))) {
                System.out.println("Tipo no válido. Use 'math' o 'logic' o 'fin'.");
                continue;
            }
            System.out.print("Texto de la pregunta: ");
            String text = scanner.nextLine();
            System.out.print("Respuesta correcta (texto): ");
            String correct = scanner.nextLine();
            Question q;
            if (type.equals("math")) {
                q = new MathQuestion(text, correct);
            } else {
                q = new LogicQuestion(text, correct);
            }
            test.addQuestion(q);
            System.out.println("Pregunta añadida.");
        }
        return test;
    }

    private void listTests() {
        if (tests.isEmpty()) {
            System.out.println("No hay pruebas disponibles.");
            return;
        }
        System.out.println("Pruebas disponibles:");
        for (int i=0;i<tests.size();i++) {
            System.out.printf("%d) %s (%d preguntas)%n", i+1, tests.get(i).getTitle(), tests.get(i).getQuestions().size());
        }
    }

    private void takeTest(Student s) {
        if (tests.isEmpty()) {
            System.out.println("No hay pruebas para realizar.");
            return;
        }
        listTests();
        System.out.print("Seleccione el número de la prueba: ");
        String sel = scanner.nextLine().trim();
        int idx;
        try {
            idx = Integer.parseInt(sel)-1;
            if (idx < 0 || idx >= tests.size()) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            System.out.println("Selección inválida.");
            return;
        }
        Test selected = tests.get(idx);
        System.out.println("Iniciando prueba: " + selected.getTitle());
        int score = 0;
        List<String> answers = new ArrayList<>();
        for (Question q : selected.getQuestions()) {
            System.out.println("\nPregunta: " + q.getQuestionText());
            System.out.print("Tu respuesta: ");
            String resp = scanner.nextLine();
            answers.add(resp);
            if (q.verifyAnswer(resp)) {
                score++;
                System.out.println("Respuesta correcta.");
            } else {
                System.out.println("Respuesta incorrecta. Respuesta correcta: " + q.getCorrectAnswer());
            }
        }
        int total = selected.getQuestions().size();
        double percentage = (total == 0) ? 0 : (100.0 * score / total);
        Result r = new Result(s.getUsername(), selected.getTitle(), score, total, percentage, answers);
        results.add(r);
        System.out.printf("Has obtenido %d/%d (%.2f%%)%n", score, total, percentage);
        ResultPdfController exporter = new ResultPdfController();
        // Guardar en carpeta por defecto "resultados_pdf"
        exporter.exportToPDF(r, "resultados_pdf");

        // O guardar con nombre específico (.pdf)
        String customPath = "resultados_pdf/resultado_" + s.getUsername() + "_" + selected.getTitle().replaceAll("\\s+","_") + ".pdf";
        exporter.exportToPDF(r, customPath);
    }

    private void listResults() {
        if (results.isEmpty()) {
            System.out.println("No hay resultados registrados.");
            return;
        }
        System.out.println("Resultados:");
        for (Result r : results) {
            System.out.println(r.summary());
        }
    }

    private void listResultsForStudent(Student s) {
        boolean found = false;
        for (Result r : results) {
            if (r.getStudentUsername().equals(s.getUsername())) {
                System.out.println(r.detailed());
                found = true;
            }
        }
        if (!found) System.out.println("No tienes resultados registrados.");
    }
}
