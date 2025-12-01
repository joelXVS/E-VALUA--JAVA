/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.gamerker.io.e.valua_java;
import com.gamerker.io.e.valua_java.controllersPack.AppController;

/**
 *
 * @author hp
 */
/**
 * clase principal del sistema e-valua
 * punto de entrada de la aplicacion
 */
public class EVALUA_JAVA {
    public static void main(String[] args) {
        // Consola
        AppController app = new AppController();
        app.run();
        // Grafico
        AppLauncher.main(args);
    }
}