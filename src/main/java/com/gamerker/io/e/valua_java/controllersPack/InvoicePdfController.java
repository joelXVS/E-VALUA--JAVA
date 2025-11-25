/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gamerker.io.e.valua_java.controllersPack;

import com.gamerker.io.e.valua_java.mainClasses.*;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.awt.Desktop;
/**
 *
 * @author hp
 */
/**
 * imprimir facturacion y cobros
 */
public class InvoicePdfController {

    private static final String DEST_FOLDER = "facturas";

    static {
        new File(DEST_FOLDER).mkdirs();
    }

    public String generateInvoice(User user, List<Transaction> dailyTransactions, LocalDateTime date) {
        String fileName = String.format("%s/Factura_%s_%s.pdf",
                DEST_FOLDER, user.getUsername(),
                date.format(DateTimeFormatter.ofPattern("yyyyMMdd")));

        try (PdfWriter writer = new PdfWriter(fileName);
             PdfDocument pdf = new PdfDocument(writer);
             Document doc = new Document(pdf, PageSize.A4)) {

            doc.setMargins(40, 40, 40, 40);

            doc.add(new Paragraph("FACTURA DE CONSUMO - E-VALUA")
                    .setFontSize(18).setBold().setTextAlignment(TextAlignment.CENTER));

            doc.add(new Paragraph("Fecha: " + date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                    .setTextAlignment(TextAlignment.RIGHT));

            doc.add(new Paragraph("\nCliente: " + user.getDisplayName() + " (" + user.getUsername() + ")")
                    .setFontSize(12));

            Table table = new Table(4).setWidth(500);
            table.addHeaderCell(new Cell().add(new Paragraph("Concepto").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Tipo").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Monto").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Fecha").setBold()));

            double total = 0;
            for (Transaction t : dailyTransactions) {
                if ("CHARGE".equals(t.getType())) {
                    total += t.getAmount();
                    table.addCell(t.getConcept());
                    table.addCell("Consumo");
                    table.addCell("$" + String.format("%,.0f", t.getAmount()));
                    table.addCell(t.getFormattedDate());
                }
            }

            doc.add(table);

            Paragraph totalParagraph = new Paragraph("TOTAL FACTURADO: $" + String.format("%,.0f", total))
                    .setFontSize(16).setBold().setTextAlignment(TextAlignment.RIGHT)
                    .setBackgroundColor(ColorConstants.YELLOW).setPadding(10);
            doc.add(totalParagraph);

            doc.add(new Paragraph("\nGracias por usar E-valua.")
                    .setTextAlignment(TextAlignment.CENTER).setItalic());

            System.out.println("Factura generada: " + fileName);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        
        try {
            File pdfFile = new File(fileName);
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(pdfFile);
                System.out.println("Abriendo factura...");
            }
        } catch (Exception e) {
            System.out.println("No se pudo abrir el PDF automáticamente: " + e.getMessage());
        }

        return fileName;
    }
}