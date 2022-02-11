/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.simpletecno.ubicalo.main;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.simpletecno.ubicalo.utileria.Utileria;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author joseaguirre
 */
public class HeaderFooterPageEvent extends PdfPageEventHelper {

    private final Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD);
    private final Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL, BaseColor.RED);
    private final Font subFont = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.BOLD);
    private final Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 8, Font.BOLD);
    private final Font smallBold14 = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD);
    private final Font smallBold12 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
    private final Font smallBold10 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);
    private final Font fuenteMagenta36 = new Font(Font.FontFamily.TIMES_ROMAN, 36, Font.BOLD, BaseColor.MAGENTA);
    private final Font fuenteNegra12 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD, BaseColor.BLACK);
    private final Font fuenteVerde12 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL, BaseColor.GREEN);
    private final Font fuenteAzul25 = new Font(Font.FontFamily.TIMES_ROMAN, 25, Font.BOLD, BaseColor.BLUE);
    private final Font fuenteRoja12 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL, BaseColor.RED);
    private final Font fuenteAzul16 = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.BOLD, BaseColor.BLUE);
    private final Font fuenteAzul12 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD, BaseColor.BLUE);
    private final Font fuenteAzul10 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD, BaseColor.BLUE);
    private final Font small10 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);
    private final Font small12 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL);
    private final Font small10Red = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL, BaseColor.RED);

    private String companyName = "";
    private String companyTaxid = "";
    private String firstLineReportTitle = "";
    private String secondLineReportTitle = "";
    private String thirdLineReportTitle = "";
    private String leftFooterText = "";
    private String printTime = "";
    private String printBy = "";

    @Override
    public void onStartPage(PdfWriter writer, Document document) {
        try {

            //PRIMERA TABLA
            PdfPTable firstTable = new PdfPTable(3);

            firstTable.setSplitRows(false);
            firstTable.setHeaderRows(0);
            firstTable.setWidthPercentage(95);

            float[] columnWidths = {1.5f, 3.0f, 1.5f};

            firstTable.setWidths(columnWidths);

//LINE 1
            PdfPCell c1;

            c1 = new PdfPCell(new Paragraph("", smallBold12));
            c1.setHorizontalAlignment(Element.ALIGN_LEFT);
            c1.setVerticalAlignment(Element.ALIGN_LEFT);
            c1.setBorderWidth(0);
            firstTable.addCell(c1);

            c1 = new PdfPCell(new Paragraph("", smallBold12));
            c1.setHorizontalAlignment(Element.ALIGN_CENTER);
            c1.setVerticalAlignment(Element.ALIGN_CENTER);
            c1.setBorderWidth(0);
            firstTable.addCell(c1);

            c1 = new PdfPCell(new Paragraph("", smallBold12));
            c1.setHorizontalAlignment(Element.ALIGN_RIGHT);
            c1.setVerticalAlignment(Element.ALIGN_RIGHT);
            c1.setBorderWidth(0);
            firstTable.addCell(c1);
//LINE 2
            c1 = new PdfPCell(new Paragraph(companyTaxid, small10));
            c1.setHorizontalAlignment(Element.ALIGN_LEFT);
            c1.setVerticalAlignment(Element.ALIGN_LEFT);
            c1.setBorderWidth(0);
            firstTable.addCell(c1);

            c1 = new PdfPCell(new Paragraph(secondLineReportTitle, smallBold12));
            c1.setHorizontalAlignment(Element.ALIGN_CENTER);
            c1.setVerticalAlignment(Element.ALIGN_CENTER);
            c1.setBorderWidth(0);
            firstTable.addCell(c1);

            c1 = new PdfPCell(new Paragraph(printBy, smallBold12));
            c1.setHorizontalAlignment(Element.ALIGN_RIGHT);
            c1.setVerticalAlignment(Element.ALIGN_RIGHT);
            c1.setBorderWidth(0);
//                c1.setBorderWidthBottom(1);
            firstTable.addCell(c1);

            c1 = new PdfPCell(new Paragraph("", small10));
            c1.setHorizontalAlignment(Element.ALIGN_LEFT);
            c1.setVerticalAlignment(Element.ALIGN_LEFT);
            c1.setBorderWidth(0);
            firstTable.addCell(c1);

            c1 = new PdfPCell(new Paragraph("", smallBold12));
            c1.setHorizontalAlignment(Element.ALIGN_CENTER);
            c1.setVerticalAlignment(Element.ALIGN_CENTER);
            c1.setBorderWidth(0);
            firstTable.addCell(c1);

            c1 = new PdfPCell(new Paragraph("", smallBold12));
            c1.setHorizontalAlignment(Element.ALIGN_LEFT);
            c1.setVerticalAlignment(Element.ALIGN_LEFT);
            c1.setBorderWidth(0);
//                c1.setBorderWidthBottom(1);
            firstTable.addCell(c1);
            
            document.add(firstTable);

            Paragraph preface2 = new Paragraph("");
            addEmptyLine(preface2, 1);
            document.add(preface2);

        } catch (Exception ex) {
            Logger.getLogger(HeaderFooterPageEvent.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("error " + ex);
            ex.printStackTrace();
        }

//        ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase("Top Left"), 30, 800, 0);
/*        
        ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase("Siena San Isidro"), 525, 800, 0);
        Utileria utilData = new Utileria(1);
        ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase("Fecha : "  + utilData.getFecha(new java.util.Date()) + " Hora : " + Utileria.getHora_1(new java.util.Date()).substring(0, 5)),  30, 800, 0);
        ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase("Página : " + document.getPageNumber()), 525, 800, 0);
        

//        ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase("Top Left"), 30, 800, 0);
/*        
        ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase("Siena San Isidro"), 525, 800, 0);
        Utileria utilData = new Utileria(1);
        ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase("Fecha : "  + utilData.getFecha(new java.util.Date()) + " Hora : " + Utileria.getHora_1(new java.util.Date()).substring(0, 5)),  30, 800, 0);
        ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase("Página : " + document.getPageNumber()), 525, 800, 0);
         */
    }

    private void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }

    public void onEndPage(PdfWriter writer, Document document) {
        Utileria utilData = new Utileria(1);
        ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_LEFT, new Phrase(leftFooterText, smallBold10), 20, 20, 0);
        ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_RIGHT, new Phrase("Página : " + document.getPageNumber(), smallBold10), 550, 20, 0);
    }

    /**
     * @param leftFooterText the leftFootherText to set
     */
    public void setLeftFooterText(String leftFooterText) {
        this.leftFooterText = leftFooterText;
    }

    /**
     * @return the companyName
     */
    public String getCompanyName() {
        return companyName;
    }

    /**
     * @param companyName the companyName to set
     */
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    /**
     * @return the companyTaxid
     */
    public String getCompanyTaxid() {
        return companyTaxid;
    }

    /**
     * @param companyTaxid the companyTaxid to set
     */
    public void setCompanyTaxid(String companyTaxid) {
        this.companyTaxid = companyTaxid;
    }

    /**
     * @return the firstLineReportTitle
     */
    public String getFirstLineReportTitle() {
        return firstLineReportTitle;
    }

    /**
     * @param firstLineReportTitle the firstLineReportTitle to set
     */
    public void setFirstLineReportTitle(String firstLineReportTitle) {
        this.firstLineReportTitle = firstLineReportTitle;
    }

    /**
     * @return the secondLineReportTitle
     */
    public String getSecondLineReportTitle() {
        return secondLineReportTitle;
    }

    /**
     * @param secondLineReportTitle the secondLineReportTitle to set
     */
    public void setSecondLineReportTitle(String secondLineReportTitle) {
        this.secondLineReportTitle = secondLineReportTitle;
    }

    /**
     * @return the thirdLineReportTitle
     */
    public String getThirdLineReportTitle() {
        return thirdLineReportTitle;
    }

    /**
     * @param thirdLineReportTitle the thirdLineReportTitle to set
     */
    public void setThirdLineReportTitle(String thirdLineReportTitle) {
        this.thirdLineReportTitle = thirdLineReportTitle;
    }

    /**
     * @return the leftFooterText
     */
    public String getLeftFooterText() {
        return leftFooterText;
    }

    /**
     * @return the printBy
     */
    public String getPrintBy() {
        return printBy;
    }

    /**
     * @param printBy the printBy to set
     */
    public void setPrintBy(String printBy) {
        this.printBy = printBy;
    }

    /**
     * @return the printTime
     */
    public String getPrintTime() {
        return printTime;
    }

    /**
     * @param printTime the printTime to set
     */
    public void setPrintTime(String printTime) {
        this.printTime = printTime;
    }

}
