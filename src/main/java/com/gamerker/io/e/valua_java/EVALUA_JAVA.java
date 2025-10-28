/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.gamerker.io.e.valua_java;
import com.gamerker.io.e.valua_java.controllersPack.AppController;
import com.gamerker.io.e.valua_java.controllersPack.DBController;
import com.gamerker.io.e.valua_java.mainClasses.*;
import java.util.Scanner;
/**
 *
 * @author hp
 */
public class EVALUA_JAVA {
    public static void main(String[] args) throws InterruptedException {
        AppController appManagerObj = new AppController();
        DBController dbManagerObj = new DBController();
        Scanner getInput = new Scanner(System.in);
        User actualUser;
        
        System.out.println("=== ¡Iniciando sistema de juego! ===");
        Thread.sleep(1500);
        System.out.println("=== CARGANDO... ===");
        Thread.sleep(1500);
        
        actualUser = new Student("Jose Angel Perea Valencias");
        System.out.println(actualUser.getUsername());
        
        showMainMenu(actualUser);
    }

    public static void showMainMenu(User userLogged) {
        while (true) {
            System.out.println("\n=== MENU PRINCIPAL - E-VALUA ===");
            System.out.println("1. Ver pruebas disponibles");
            System.out.println("2. Ver historial de resultados");
            System.out.println("3. Exportar resultados a PDF");
            
            switch (userLogged.rol) {
                case "Admin" -> {
                    System.out.println("4. Gestionar usuarios");
                    System.out.println("5. Ajustes globales");
                    System.out.println("6. Salir");
                }
                case "Teacher" -> {
                    System.out.println("4. Gestionar pruebas");
                    System.out.println("5. Salir");
                }
                case "Student" -> System.out.println("4. Salir");
            }
            
            System.out.print("Elije una opcion: ");
            /*

            int choice = getInput.nextInt();
                getInput.nextLine(); // limpiar buffer

            switch (choice) {
                case 1 -> viewTests();
                case 2 -> viewResults();
                case 3 -> exportResultsToPDF();
                case 4 -> manageUsers();
                case 5 -> manageExams();
                case 6 -> {
                    System.out.println("¡Cerrando sistema!");
                    return;
                }
                default -> System.out.println("¡Opcion Invalida!");
            }
*/
        }
    }
}