/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gamerker.io.e.valua_java.utils;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.extgstate.PdfExtGState;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.VerticalAlignment;

import java.io.File;
import java.net.URL;

/**
 * Utilidades para agregar marcas de agua y logos a PDFs
 */
public class PdfWatermarkUtil {
    
    private static ImageData logoImageData = null;
    
    /**
     * Carga el logo desde recursos
     */
    private static ImageData loadLogo() {
        if (logoImageData != null) {
            return logoImageData;
        }
        
        try {
            // Intentar varias rutas posibles
            String[] possiblePaths = {
                "/com/gamerker/io/e/valua_java/utils/resources/logo.png",
                "/logo.png",
                "logo.png",
                "utils/resources/logo.png"
            };
            
            for (String path : possiblePaths) {
                try {
                    URL url = PdfWatermarkUtil.class.getResource(path);
                    if (url != null) {
                        logoImageData = ImageDataFactory.create(url);
                        System.out.println("Logo cargado desde recurso: " + path);
                        return logoImageData;
                    }
                } catch (Exception e) {
                    // Continuar con la siguiente ruta
                }
            }
            
            // Si no se encuentra en recursos, buscar en archivos
            String[] filePaths = {
                "utils/resources/logo.png",
                "src/main/resources/com/gamerker/io/e/valua_java/utils/resources/logo.png",
                System.getProperty("user.dir") + "/utils/resources/logo.png",
                System.getProperty("user.dir") + "/logo.png"
            };
            
            for (String filePath : filePaths) {
                File file = new File(filePath);
                if (file.exists()) {
                    try {
                        logoImageData = ImageDataFactory.create(filePath);
                        System.out.println("Logo cargado desde archivo: " + filePath);
                        return logoImageData;
                    } catch (Exception e) {
                        // Continuar con la siguiente ruta
                    }
                }
            }
            
            System.err.println("No se pudo cargar el logo. Se usará marca de agua de texto.");
            
        } catch (Exception e) {
            System.err.println("Error cargando logo: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Agrega marca de agua con logo a todas las páginas
     */
    public static void addWatermarkToAllPages(PdfDocument pdfDoc) {
        try {
            ImageData logoData = loadLogo();
            
            if (logoData != null) {
                addImageWatermark(pdfDoc, logoData);
            } else {
                addTextWatermark(pdfDoc, "E-VALUA");
            }
            
        } catch (Exception e) {
            System.err.println("Error agregando marca de agua: " + e.getMessage());
            addTextWatermark(pdfDoc, "E-VALUA"); // Fallback
        }
    }
    
    /**
     * Agrega marca de agua con imagen
     */
    private static void addImageWatermark(PdfDocument pdfDoc, ImageData imageData) {
        try {
            for (int i = 1; i <= pdfDoc.getNumberOfPages(); i++) {
                PdfPage page = pdfDoc.getPage(i);
                float pageWidth = page.getPageSize().getWidth();
                float pageHeight = page.getPageSize().getHeight();
                
                PdfCanvas canvas = new PdfCanvas(page);
                
                // Configurar transparencia
                PdfExtGState gs1 = new PdfExtGState();
                gs1.setFillOpacity(0.15f); // 15% de opacidad
                canvas.setExtGState(gs1);
                
                // Tamaño de la marca de agua
                float watermarkWidth = 200;
                float watermarkHeight = 200;
                
                // Calcular posición centrada
                float x = (pageWidth - watermarkWidth) / 2;
                float y = (pageHeight - watermarkHeight) / 2;
                
                // Dibujar la imagen
                canvas.addImageWithTransformationMatrix(
                    imageData,
                    watermarkWidth, 0, 0, watermarkHeight, // Escalar
                    x, y
                );
                
                canvas.release();
            }
            
        } catch (Exception e) {
            System.err.println("Error agregando marca de agua de imagen: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Agrega marca de agua de texto
     */
    private static void addTextWatermark(PdfDocument pdfDoc, String text) {
        try {
            for (int i = 1; i <= pdfDoc.getNumberOfPages(); i++) {
                PdfPage page = pdfDoc.getPage(i);
                float pageWidth = page.getPageSize().getWidth();
                float pageHeight = page.getPageSize().getHeight();
                
                PdfCanvas canvas = new PdfCanvas(page);
                
                // Configurar transparencia
                PdfExtGState gs1 = new PdfExtGState();
                gs1.setFillOpacity(0.1f);
                canvas.setExtGState(gs1);
                
                // Configurar fuente y color
                canvas.setFillColor(new DeviceRgb(200, 200, 200)); // Gris claro
                
                // Rotar texto 45 grados
                double angle = Math.PI / 4; // 45 grados en radianes
                float cos = (float) Math.cos(angle);
                float sin = (float) Math.sin(angle);
                
                canvas.beginText()
                    .setFontAndSize(
                        com.itextpdf.kernel.font.PdfFontFactory.createFont(
                            com.itextpdf.io.font.constants.StandardFonts.HELVETICA
                        ), 
                        48
                    )
                    .setTextMatrix(cos, -sin, sin, cos, pageWidth / 2 - 100, pageHeight / 2)
                    .showText(text)
                    .endText();
                
                canvas.release();
            }
            
        } catch (Exception e) {
            System.err.println("Error agregando marca de agua de texto: " + e.getMessage());
        }
    }
    
    /**
     * Agrega encabezado con logo al documento
     */
    public static void addHeaderWithLogo(Document doc, String title) {
        try {
            ImageData logoData = loadLogo();
            
            // Crear tabla para header (2 columnas)
            Table headerTable = new Table(2);
            headerTable.setWidth(com.itextpdf.layout.properties.UnitValue.createPercentValue(100));
            
            if (logoData != null) {
                // Celda con logo
                Image logo = new Image(logoData);
                logo.scaleToFit(80, 80); // Tamaño para header
                logo.setOpacity(1.0f); // Sin transparencia en header
                
                Cell logoCell = new Cell()
                    .add(logo)
                    .setBorder(com.itextpdf.layout.borders.Border.NO_BORDER)
                    .setVerticalAlignment(VerticalAlignment.MIDDLE)
                    .setPaddingRight(10);
                headerTable.addCell(logoCell);
            } else {
                // Celda vacía si no hay logo
                headerTable.addCell(new Cell()
                    .add(new Paragraph(" "))
                    .setBorder(com.itextpdf.layout.borders.Border.NO_BORDER));
            }
            
            // Celda con título
            Cell titleCell = new Cell()
                .add(new Paragraph(title)
                    .setFontSize(16)
                    .setBold()
                    .setFontColor(new DeviceRgb(255, 140, 0))) // Naranja E-VALUA
                .setTextAlignment(TextAlignment.RIGHT)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setBorder(com.itextpdf.layout.borders.Border.NO_BORDER);
            headerTable.addCell(titleCell);
            
            doc.add(headerTable);
            doc.add(new Paragraph("\n"));
            
        } catch (Exception e) {
            System.err.println("Error agregando header con logo: " + e.getMessage());
            // Fallback: solo título
            try {
                doc.add(new Paragraph(title)
                    .setFontSize(16)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontColor(new DeviceRgb(255, 140, 0)));
                doc.add(new Paragraph("\n"));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    
    /**
     * Versión alternativa simple para cuando falla la carga del logo
     */
    public static void addSimpleHeader(Document doc, String title) {
        try {
            // Línea decorativa arriba
            doc.add(new Paragraph("─".repeat(50))
                .setFontColor(new DeviceRgb(255, 140, 0))
                .setTextAlignment(TextAlignment.CENTER));
            
            // Título
            doc.add(new Paragraph(title)
                .setFontSize(16)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(new DeviceRgb(255, 140, 0)));
            
            // Línea decorativa abajo
            doc.add(new Paragraph("─".repeat(50))
                .setFontColor(new DeviceRgb(255, 140, 0))
                .setTextAlignment(TextAlignment.CENTER));
            
            doc.add(new Paragraph("\n"));
            
        } catch (Exception e) {
            System.err.println("Error agregando header simple: " + e.getMessage());
            // Último fallback
            doc.add(new Paragraph(title)
                .setFontSize(16)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER));
            doc.add(new Paragraph("\n"));
        }
    }
    
    /**
     * Agrega pie de página estándar
     */
    public static void addStandardFooter(Document doc) {
        try {
            doc.add(new Paragraph("\n\n"));
            
            // Línea separadora
            doc.add(new Paragraph("─".repeat(50))
                .setFontColor(new DeviceRgb(200, 200, 200))
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(8));
            
            // Texto del pie
            doc.add(new Paragraph(
                "© " + java.time.LocalDateTime.now().getYear() + 
                " | E-VALUA - Sistema de Evaluación Académica | " +
                "Documento generado: " + 
                java.time.LocalDateTime.now().format(
                    java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")))
                .setFontSize(9)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(new DeviceRgb(100, 100, 100)));
            
        } catch (Exception e) {
            // Ignorar errores en el pie de página
        }
    }
}