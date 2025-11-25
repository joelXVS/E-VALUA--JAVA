/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gamerker.io.e.valua_java.controllersPack;
import com.gamerker.io.e.valua_java.mainClasses.*;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.VerticalAlignment;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.awt.Desktop;
import com.itextpdf.kernel.colors.DeviceRgb;
/**
 *
 * @author hp
 */
/**
 * exporta resultados de pruebas a pdf
 * implementa interfaz exportable
 */
public class ResultPdfController {

    private static final String DEST_FOLDER = "resultados_pdf";
    private static final String FONT_BOLD = "/fonts/Roboto-Bold.ttf";   // opcional (puedes omitir)
    private static final String FONT_REGULAR = "/fonts/Roboto-Regular.ttf";

    static {
        new File(DEST_FOLDER).mkdirs();
    }

    public String exportResultToPdf(Result result, User student) {
        String fileName = String.format("%s/Resultado_%s_%s_%s.pdf",
                DEST_FOLDER,
                student.getUsername(),
                result.getTestTitle().replaceAll("[^a-zA-Z0-9]", "_"),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmm"))
        );

        try (PdfWriter writer = new PdfWriter(fileName);
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf, PageSize.A4)) {

            document.setMargins(50, 50, 50, 50);

            // Título principal
            document.add(new Paragraph("RESULTADO OFICIAL - E-VALUA")
                    .setFontSize(20)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontColor(ColorConstants.BLUE));

            document.add(new Paragraph("Sistema de Evaluación Académica")
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setItalic());

            document.add(new Paragraph("\n"));

            // Información del estudiante
            Table infoTable = new Table(2).setWidth(500).setFixedLayout();
            infoTable.addCell(createCell("Estudiante:", student.getDisplayName(), true));
            infoTable.addCell(createCell("Usuario:", student.getUsername(), true));
            infoTable.addCell(createCell("Prueba:", result.getTestTitle(), true));
            infoTable.addCell(createCell("Fecha:", LocalDateTime.now().format(
                    DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")), true));

            document.add(infoTable);
            document.add(new Paragraph("\n"));

            // Resultado destacado
            String scoreText = String.format("%d / %d (%.2f%%)", 
                    result.getScore(), result.getTotal(), result.getPercentage());
            Paragraph scoreParagraph = new Paragraph("PUNTAJE FINAL: " + scoreText)
                    .setFontSize(18)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBackgroundColor(result.getPercentage() >= 70 ? new DeviceRgb(144, 238, 144) : ColorConstants.LIGHT_GRAY)
                    .setPadding(15);

            document.add(scoreParagraph);

            String status = result.getPercentage() >= 70 ? "APROBADO" : "NO APROBADO";
            Color statusColor = result.getPercentage() >= 70 ? ColorConstants.GREEN : ColorConstants.RED;
            document.add(new Paragraph(status)
                    .setFontSize(22)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontColor(statusColor)
                    .setMarginTop(10));

            document.add(new Paragraph("\nDetalles de respuestas:\n")
                    .setBold().setFontSize(14));

            // Tabla de respuestas
            Table answersTable = new Table(4);
            answersTable.setWidth(500);
            answersTable.addHeaderCell(createHeaderCell("N°"));
            answersTable.addHeaderCell(createHeaderCell("Pregunta"));
            answersTable.addHeaderCell(createHeaderCell("Tu respuesta"));
            answersTable.addHeaderCell(createHeaderCell("Correcta"));

            List<Question> questions = findTestByTitle(result.getTestTitle()).getQuestions();

            for (int i = 0; i < questions.size(); i++) {
                Question q = questions.get(i);
                String userAns = result.getAnswers().get(i);
                boolean isCorrect = q.verifyAnswer(userAns);

                answersTable.addCell(createCell(String.valueOf(i + 1)));
                answersTable.addCell(createCell(q.getQuestionText()).setFontSize(9));
                answersTable.addCell(createCell(userAns)
                        .setFontColor(isCorrect ? ColorConstants.GREEN : ColorConstants.RED));
                answersTable.addCell(createCell(q.getCorrectAnswer() + ") " + q.getCorrectOptionText())
                        .setFontColor(ColorConstants.BLUE));
            }

            document.add(answersTable);

            // Pie de página
            document.add(new Paragraph("\n\nGracias por usar E-valua © 2025")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(10)
                    .setItalic());

            System.out.println("PDF generado exitosamente: " + fileName);

        } catch (Exception e) {
            System.err.println("Error generando PDF de resultado: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
        
        try {
            File pdfFile = new File(fileName);
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(pdfFile);
                System.out.println("Abriendo resultado...");
            }
        } catch (Exception e) {
            System.out.println("No se pudo abrir el PDF: " + e.getMessage());
        }

        return fileName;
    }

    private Cell createCell(String content) {
        return new Cell().add(new Paragraph(content)).setPadding(5).setBorder(Border.NO_BORDER);
    }

    private Cell createCell(String label, String value, boolean bold) {
        Paragraph p = new Paragraph().add(new Text(label + " ").setBold()).add(value);
        return new Cell(1, 1).add(p).setPadding(5).setBorder(Border.NO_BORDER);
    }

    private Cell createHeaderCell(String text) {
        return new Cell().add(new Paragraph(text).setBold().setBackgroundColor(ColorConstants.LIGHT_GRAY))
                .setTextAlignment(TextAlignment.CENTER).setPadding(8);
    }

    private Test findTestByTitle(String title) {
        DBController db = new DBController();
        return db.loadTests().stream()
                .filter(t -> t.getTitle().equals(title))
                .findFirst()
                .orElse(null);
    }
}