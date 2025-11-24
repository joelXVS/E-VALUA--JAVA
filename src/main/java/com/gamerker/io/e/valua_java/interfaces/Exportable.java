/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.gamerker.io.e.valua_java.interfaces;
import com.gamerker.io.e.valua_java.mainClasses.Result;
/**
 *
 * @author hp
 */
/**
 * interfaz para clases que pueden exportar resultados a pdf
 */
public interface Exportable {
    // metodo que debe implementar para exportar un resultado a archivo pdf
    void exportToPDF(Result result, String filePath);
}