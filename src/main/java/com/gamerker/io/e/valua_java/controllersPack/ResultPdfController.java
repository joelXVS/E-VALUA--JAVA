/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gamerker.io.e.valua_java.controllersPack;
import com.gamerker.io.e.valua_java.mainClasses.Result;
import com.gamerker.io.e.valua_java.interfaces.Exportable;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
/**
 *
 * @author hp
 */
public class ResultPdfController implements Exportable {
    private static final String DEFAULT_DIR = "resultados_pdf";

    @Override
    public void exportToPDF(Result result, String filePath) {
        try {
            Path targetPath;
            if (filePath == null || filePath.isEmpty() || !filePath.toLowerCase().endsWith(".pdf")) {
                // filePath interpretado como directorio (o usar el directorio por defecto)
                Path dir = (filePath == null || filePath.isEmpty()) ? Paths.get(DEFAULT_DIR) : Paths.get(filePath);
                if (!Files.exists(dir)) {
                    Files.createDirectories(dir);
                }
                // construir nombre seguro
                String safeTest = result.getTestTitle() == null ? "test" :
                        result.getTestTitle().replaceAll("\\s+", "_").replaceAll("[^a-zA-Z0-9_\\-]", "");
                String filename = String.format("resultado_%s_%s_%d.pdf",
                        result.getStudentUsername(), safeTest, System.currentTimeMillis());
                targetPath = dir.resolve(filename);
            } else {
                // filePath es una ruta a .pdf completa
                targetPath = Paths.get(filePath);
                Path parent = targetPath.getParent();
                if (parent != null && !Files.exists(parent)) {
                    Files.createDirectories(parent);
                }
            }

            // Crear documento PDF con iText
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(targetPath.toFile()));
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 12);

            document.add(new Paragraph("Resultado de E-valua", titleFont));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("Estudiante: " + nullSafe(result.getStudentUsername()), bodyFont));
            document.add(new Paragraph("Prueba: " + nullSafe(result.getTestTitle()), bodyFont));
            document.add(new Paragraph(String.format("Puntuación: %d/%d (%.2f%%)",
                    result.getScore(), result.getTotal(), result.getPercentage()), bodyFont));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("Respuestas:", titleFont));
            List<String> answers = result.getAnswers();
            if (answers == null || answers.isEmpty()) {
                document.add(new Paragraph("No hay respuestas registradas.", bodyFont));
            } else {
                for (int i = 0; i < answers.size(); i++) {
                    document.add(new Paragraph(String.format("%d) %s", i + 1, answers.get(i)), bodyFont));
                }
            }

            document.close();
            System.out.println("PDF generado en: " + targetPath.toAbsolutePath().toString());
        } catch (DocumentException | IOException ex) {
            System.out.println("Error generando PDF: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private String nullSafe(String s) {
        return s == null ? "" : s;
    }
}
