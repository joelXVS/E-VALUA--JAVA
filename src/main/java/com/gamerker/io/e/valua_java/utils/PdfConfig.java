/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gamerker.io.e.valua_java.utils;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.layout.Document;
/**
 *
 * @author hp
 */
public class PdfConfig {
    public static void configureDocument(Document doc) {
        doc.setMargins(40, 40, 40, 40);
        doc.getPdfDocument().getDefaultPageSize().applyMargins(40, 40, 40, 40, false);
    }
}