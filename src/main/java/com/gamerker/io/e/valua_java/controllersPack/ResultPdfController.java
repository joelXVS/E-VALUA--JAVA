/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gamerker.io.e.valua_java.controllersPack;
import com.gamerker.io.e.valua_java.mainClasses.Result;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.FileOutputStream;
import java.time.format.DateTimeFormatter;
/**
 *
 * @author hp
 */
public class ResultPdfController {   
    public void generateReport(Result result, String filePath) {
        Document document = new Document();
        
        try {
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();
            
            // Title
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph title = new Paragraph("E-valua - Resultados", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);
            
            // Student Information
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
            
                document.add(new Paragraph("Información del Usuario:", headerFont));
            document.add(new Paragraph("Nombre: " + result.getUser().getUsername(), normalFont));
            document.add(new Paragraph(" "));
            
            // Exam Information
            document.add(new Paragraph("Información de Prueba:", headerFont));
            document.add(new Paragraph("Prueba: " + result.getTest().getName(), normalFont));
            document.add(new Paragraph("Fecha: " + result.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), normalFont));
            document.add(new Paragraph(" "));
            
            // Results Summary
            document.add(new Paragraph("Resumén de Resultados:", headerFont));
            document.add(new Paragraph("Puntaje: " + result.getTotalScore() + "/" + result.getMaxPossibleScore(), normalFont));
            document.add(new Paragraph("Porcentaje: " + String.format("%.1f%%", result.getPercentage()), normalFont));
            document.add(new Paragraph("Valoración en letras: " + result.getFinalVL(), normalFont));
            document.add(new Paragraph(" "));
            
            // Detailed Results Table
            document.add(new Paragraph("Resultados Detallados:", headerFont));
            PdfPTable table = new PdfPTable(3);
            table.setWidthPercentage(100);
            
            // Table headers
            table.addCell(createCell("Pregunta", headerFont, Element.ALIGN_CENTER));
            table.addCell(createCell("Puntos obtenidos", headerFont, Element.ALIGN_CENTER));
            table.addCell(createCell("Resultado", headerFont, Element.ALIGN_CENTER));
            
            // Table content
            int questionNum = 1;
            for (var entry : result.getAnswers().entrySet()) {
                table.addCell(createCell("Pregunta " + questionNum, normalFont, Element.ALIGN_LEFT));
                table.addCell(createCell(String.valueOf(entry.getKey().getPoints()), normalFont, Element.ALIGN_CENTER));
                table.addCell(createCell(entry.getValue() ? "Correcto" : "Incorrecto", 
                                       entry.getValue() ? FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.GREEN) 
                                                       : FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.RED), 
                                       Element.ALIGN_CENTER));
                questionNum++;
            }
            
            document.add(table);
            
            // Footer
            document.add(new Paragraph(" "));
            Paragraph footer = new Paragraph("Generado por E-valua System", 
                                           FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10));
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);
            
        } catch (Exception e) {
            System.err.println("Error al crear y generar PDF: " + e.getMessage());
        } finally {
            document.close();
        }
    }
    
    private PdfPCell createCell(String text, Font font, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(alignment);
        cell.setPadding(5);
        return cell;
    }
}