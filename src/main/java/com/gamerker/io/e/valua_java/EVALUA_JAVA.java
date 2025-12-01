/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.gamerker.io.e.valua_java;
import com.gamerker.io.e.valua_java.controllersPack.AppController;
import com.gamerker.io.e.valua_java.visuals.SplashScreen;

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
        //AppController app = new AppController();
        //app.run();
        // Grafico
        SplashScreen splash = new SplashScreen();
        splash.showWithTimer(new AppController());
    }
}