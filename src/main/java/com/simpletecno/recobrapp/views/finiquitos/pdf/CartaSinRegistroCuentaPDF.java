package com.simpletecno.recobrapp.views.finiquitos.pdf;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.simpletecno.recobrapp.main.RecobrAppUI;
import com.simpletecno.recobrapp.utileria.Utileria;
import com.vaadin.server.StreamResource;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.*;
import com.vaadin.ui.Notification.Type;

import java.io.*;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.logging.Level;
import java.util.logging.Logger;


public class CartaSinRegistroCuentaPDF extends Window {

    public static Statement stQuery;
    public static ResultSet rsRecords, rsRecords2, rsRecords1;

    static final DecimalFormat df1 = new DecimalFormat("###,##0.00");

    String fileName;
    String queryString;
    String idEmpresa;
    UI mainUI;

    public CartaSinRegistroCuentaPDF(String idEmpresa) {
        this.idEmpresa = idEmpresa;

        try {
            BrowserFrame e = new BrowserFrame();
            e.setSizeFull();
            setWidth("1100");
            setHeight("700");
            center();

            fileName = "Finiquitos" +  new Utileria().getFechaHoraSinFormato() + ".pdf";

            StreamResource pdfResource;
            pdfResource = new StreamResource(new Pdf(fileName), fileName);
            pdfResource.setMIMEType("application/pdf");

            e.setSource(pdfResource);
            setContent(e);

            File file = new File(fileName);
            file.delete();

        } catch (Exception allEx) {
            Notification.show("Error al generar el pdf.", Type.ERROR_MESSAGE);
            allEx.printStackTrace();
        }

    }
    public class Pdf implements StreamResource.StreamSource, Serializable {

        private final String RECEIPTFILE
                = VaadinService.getCurrent()
                .getBaseDirectory().getAbsolutePath() + "/pdfreceipts/";

        public final Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD);
        public final Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL, BaseColor.RED);
        public final Font subFont = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.BOLD);
        public final Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 8, Font.BOLD);
        public final Font smallBold10 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);
        public final Font smallBold12 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
        public final Font smallBold14 = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD);
        public final Font fuenteMagenta36 = new Font(Font.FontFamily.TIMES_ROMAN, 36, Font.BOLD, BaseColor.MAGENTA);
        public final Font fuenteNegra12 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD, BaseColor.BLACK);
        public final Font fuenteVerde12 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL, BaseColor.GREEN);
        private final Font fuenteAzul25 = new Font(Font.FontFamily.TIMES_ROMAN, 25, Font.BOLD, BaseColor.BLUE);
        private final Font fuenteRoja12 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL, BaseColor.RED);
        private final Font fuenteAzul16 = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.BOLD, BaseColor.BLUE);
        private final Font fuenteAzul12 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD, BaseColor.BLUE);
        private final Font fuenteAzul10 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD, BaseColor.BLUE);
        private final Font small10 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);
        private final Font small12 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);
        private final Font small10Red = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL, BaseColor.RED);

        private FileOutputStream fost;
        private final ByteArrayOutputStream os = new ByteArrayOutputStream();

        private PdfWriter writer;

        public Pdf(String fileName) {

            try {

                new File(VaadinService.getCurrent().getBaseDirectory().getAbsolutePath() + "/pdfreceipts").mkdirs();

                fileName = RECEIPTFILE + fileName;

                Document document = new Document(PageSize.A4, 0, 0, 0, 0);
                Document document2 = new Document(PageSize.A4, 0, 0, 0, 0);
                fost = new FileOutputStream(fileName);

                document.setMargins(55, 55, 15, 15);
                document2.setMargins(55, 55, 15, 15);

                PdfWriter.getInstance(document, fost);

                writer = PdfWriter.getInstance(document2, os);

                document.open();
                document2.open();

                addMetaData(document);
                //addTitlePage(document);
                addContent(document);
                //addFooterPage(document);
                document.close();

                addMetaData(document2);
                //addTitlePage(document2);
                addContent(document2);
                //addFooterPage(document2);
                document2.close();

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        public InputStream getStream() {
            // Here we return the pdf contents as a byte-array
            return new ByteArrayInputStream(os.toByteArray());
        }

        // iText allows to add metadata to the PDF which can be viewed in your Adobe
        // Reader
        // under File -> Properties
        private void addMetaData(Document document) {
            document.addTitle("Reporte de documentos " + ((RecobrAppUI) UI.getCurrent()).sessionInformation.getStrProjectName());
            document.addSubject("Using iText");
            document.addKeywords("Java, PDF, iText");
            document.addAuthor("www.sopdi.com");
            document.addCreator("www.sopdi.com");
        }

        private void addContent(Document document) throws DocumentException {

            Paragraph preface = new Paragraph();

            addEmptyLine(preface, 3);
            document.add(preface);

            // Add a table
            leerBaseDatosYEscribirLineas(document);

            preface = new Paragraph();
            addEmptyLine(preface, 1);
            document.add(preface);

           // LineSeparator objectName = new LineSeparator();
           // document.add(objectName);

        }

        private void leerBaseDatosYEscribirLineas(Document document)
                throws DocumentException {


            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);

            Paragraph guatemalaHoraYfecha = new Paragraph();
            guatemalaHoraYfecha.add("Guatemala 29 de Noviembre del 2019");
            guatemalaHoraYfecha.setFont(small12);
            guatemalaHoraYfecha.setAlignment(Element.ALIGN_RIGHT);

            document.add(guatemalaHoraYfecha);

            document.add(Chunk.NEWLINE);

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

            c1 = new PdfPCell(new Paragraph("A QUIEN INTERESE", smallBold14));
            c1.setHorizontalAlignment(Element.ALIGN_CENTER);
            c1.setVerticalAlignment(Element.ALIGN_CENTER);
            c1.setBorderWidth(0);
            firstTable.addCell(c1);

            c1 = new PdfPCell(new Paragraph("", small12));
            c1.setHorizontalAlignment(Element.ALIGN_RIGHT);
            c1.setVerticalAlignment(Element.ALIGN_RIGHT);
            c1.setBorderWidth(0);
            firstTable.addCell(c1);

            document.add(firstTable);

            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);

            Paragraph contenido = new Paragraph();
            contenido.add("Por este medio hacemos de su conocimiento que en los registros de cuentas " +
                    "pendientes de pago en Gestionadora de Créditos de Guatemala, S.A. " +
                    "el Señor: JORGE EDUARDO MAZARIEGOS MÉNDEZ, que se identifica con " +
                    "DPI numero 2663 09011 0115 al día de hoy 29 de Noviembre 2019 no tiene deuda con nosotros.");
            contenido.setFont(small10);
            contenido.setAlignment(Element.ALIGN_JUSTIFIED);

            document.add(Chunk.NEWLINE);
            document.add(contenido);

            Paragraph contenido2 = new Paragraph();
            contenido2.add("Atentamente,");
            contenido2.setFont(small10);
            contenido2.setAlignment(Element.ALIGN_LEFT);

            document.add(Chunk.NEWLINE);
            document.add(contenido2);
            document.add(Chunk.NEWLINE);

            Paragraph linea = new Paragraph();
            addEmptyLine(linea, 2);
            document.add(linea);

            Paragraph firma2 = new Paragraph();
            firma2.add("Licda. Patricia de Aguirre");
            firma2.setFont(small10);
            firma2.setAlignment(Element.ALIGN_LEFT);

            Paragraph firma3 = new Paragraph();
            firma3.add("Gerente de País");
            firma3.setFont(small10);
            firma3.setAlignment(Element.ALIGN_LEFT);

            Paragraph firma4 = new Paragraph();
            firma4.add("Gestionadora de Créditos de Guatemala, S.A.");
            firma4.setFont(small10);
            firma4.setAlignment(Element.ALIGN_LEFT);

            document.add(firma2);
            document.add(firma3);
            document.add(firma4);

        }

        private void addFooterPage(Document document) {
            try {


            } catch (Exception ex) {
                Logger.getLogger(Pdf.class.getName()).log(Level.SEVERE, null, ex);
                ex.printStackTrace();
            }
        }

        private void addEmptyLine(Paragraph paragraph, int number) {
            for (int i = 0; i < number; i++) {
                paragraph.add(new Paragraph(" "));
            }
        }

    }
}
