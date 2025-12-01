/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gamerker.io.e.valua_java.controllersPack;

import com.gamerker.io.e.valua_java.mainClasses.*;
import com.gamerker.io.e.valua_java.utils.PdfWatermarkUtil;
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
import com.itextpdf.kernel.colors.DeviceRgb;

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

    /**
     * Genera factura de consumo diario
     */
    public String generateInvoice(User user, List<Transaction> dailyTransactions, LocalDateTime date) {
        String fileName = String.format("%s/diarias/Factura_%s_%s.pdf",
                DEST_FOLDER, user.getUsername(),
                date.format(DateTimeFormatter.ofPattern("yyyyMMdd")));

        try (PdfWriter writer = new PdfWriter(fileName);
             PdfDocument pdf = new PdfDocument(writer);
             Document doc = new Document(pdf, PageSize.A4)) {

            doc.setMargins(40, 40, 40, 40);

            // Agregar header con logo
            PdfWatermarkUtil.addSimpleHeader(doc, "FACTURA DE CONSUMO - E-VALUA");

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
                    .setBackgroundColor(new DeviceRgb(255, 218, 185)) // Naranja pastel
                    .setPadding(10);
            doc.add(totalParagraph);

            doc.add(new Paragraph("\nGracias por usar E-valua © " + LocalDateTime.now().getYear())
                    .setTextAlignment(TextAlignment.CENTER).setItalic()
                    .setFontSize(10));

            // Agregar marca de agua
            PdfWatermarkUtil.addWatermarkToAllPages(pdf);

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

    /**
     * NUEVO: Genera factura específica para recarga
     */
    public String generateRechargeInvoice(User user, Transaction rechargeTransaction, 
                                          String cardNumber, String cardType) {
        String fileName = String.format("%s/recargas/Recarga_%s_%s.pdf",
                DEST_FOLDER, user.getUsername(),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmm")));

        try (PdfWriter writer = new PdfWriter(fileName);
             PdfDocument pdf = new PdfDocument(writer);
             Document doc = new Document(pdf, PageSize.A4)) {

            doc.setMargins(40, 40, 40, 40);

            // Header con logo
            PdfWatermarkUtil.addHeaderWithLogo(doc, "COMPROBANTE DE RECARGA - E-VALUA");

            // Información de la factura
            doc.add(new Paragraph("COMPROBANTE #" + 
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm")))
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setFontSize(10)
                    .setItalic());

            doc.add(new Paragraph("\n"));

            // Tabla con información del cliente
            Table infoTable = new Table(2);
            infoTable.setWidth(400);
            
            infoTable.addCell(createInfoCell("Cliente:", true));
            infoTable.addCell(createInfoCell(user.getDisplayName(), false));
            
            infoTable.addCell(createInfoCell("Usuario:", true));
            infoTable.addCell(createInfoCell(user.getUsername(), false));
            
            infoTable.addCell(createInfoCell("Fecha:", true));
            infoTable.addCell(createInfoCell(
                rechargeTransaction.getTimestamp().format(
                    DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")), false));
            
            infoTable.addCell(createInfoCell("Tarjeta:", true));
            infoTable.addCell(createInfoCell(cardType + ": " + cardNumber, false));
            
            doc.add(infoTable);
            doc.add(new Paragraph("\n"));

            // Detalles de la recarga
            Paragraph rechargeTitle = new Paragraph("DETALLES DE LA RECARGA")
                    .setFontSize(14)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBackgroundColor(new DeviceRgb(255, 218, 185))
                    .setPadding(8);
            doc.add(rechargeTitle);

            Table rechargeTable = new Table(2);
            rechargeTable.setWidth(400);
            
            rechargeTable.addCell(createDetailCell("Concepto:", true));
            rechargeTable.addCell(createDetailCell(rechargeTransaction.getConcept(), false));
            
            rechargeTable.addCell(createDetailCell("Monto recargado:", true));
            rechargeTable.addCell(createDetailCell(
                "$" + String.format("%,.0f", rechargeTransaction.getAmount()), false));
            
            rechargeTable.addCell(createDetailCell("Tipo de operación:", true));
            rechargeTable.addCell(createDetailCell("PAGO", false));
            
            rechargeTable.addCell(createDetailCell("Nuevo saldo:", true));
            rechargeTable.addCell(createDetailCell(
                "$" + String.format("%,.0f", user.getBalance()), false));
            
            doc.add(rechargeTable);
            doc.add(new Paragraph("\n"));

            // Total destacado
            Paragraph totalPara = new Paragraph("RECARGA EXITOSA")
                    .setFontSize(18)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontColor(ColorConstants.GREEN)
                    .setMarginTop(20);
            doc.add(totalPara);

            Paragraph amountPara = new Paragraph("$" + String.format("%,.0f", rechargeTransaction.getAmount()))
                    .setFontSize(24)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontColor(new DeviceRgb(0, 102, 204))
                    .setMarginBottom(20);
            doc.add(amountPara);

            // Pie de página
            doc.add(new Paragraph("\n\nEste comprobante es válido como justificante de pago.")
                    .setFontSize(10)
                    .setItalic()
                    .setTextAlignment(TextAlignment.CENTER));

            doc.add(new Paragraph("E-VALUA - Sistema de Evaluación Académica")
                    .setFontSize(9)
                    .setTextAlignment(TextAlignment.CENTER));

            doc.add(new Paragraph("© " + LocalDateTime.now().getYear() + " Todos los derechos reservados")
                    .setFontSize(8)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(10));

            // Agregar marca de agua
            PdfWatermarkUtil.addWatermarkToAllPages(pdf);

            System.out.println("Comprobante de recarga generado: " + fileName);

        } catch (Exception e) {
            System.err.println("Error generando comprobante de recarga: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
        
        try {
            File pdfFile = new File(fileName);
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(pdfFile);
                System.out.println("Abriendo comprobante de recarga...");
            }
        } catch (Exception e) {
            System.out.println("No se pudo abrir el PDF automáticamente: " + e.getMessage());
        }

        return fileName;
    }

    private Cell createInfoCell(String text, boolean isBold) {
        Cell cell = new Cell();
        if (isBold) {
            cell.add(new Paragraph(text).setBold());
        } else {
            cell.add(new Paragraph(text));
        }
        cell.setPadding(5);
        cell.setBorder(com.itextpdf.layout.borders.Border.NO_BORDER);
        return cell;
    }

    private Cell createDetailCell(String text, boolean isLabel) {
        Cell cell = new Cell();
        if (isLabel) {
            cell.add(new Paragraph(text).setBold())
                .setBackgroundColor(new DeviceRgb(240, 240, 240));
        } else {
            cell.add(new Paragraph(text));
        }
        cell.setPadding(8);
        return cell;
    }
}