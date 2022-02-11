package com.simpletecno.ubicalo.views.finiquitos.pdf;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import com.simpletecno.ubicalo.main.UbicaloUI;
import com.simpletecno.ubicalo.utileria.Utileria;
import com.vaadin.server.StreamResource;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.BrowserFrame;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

import java.io.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.logging.Level;
import java.util.logging.Logger;


public class CartaUsurpacionInvestigacionPDF extends Window {

    public static Statement stQuery, stQuery2, stQuery1;
    public static PreparedStatement stPreparedQuery;
    public static ResultSet rsRecords, rsRecords2, rsRecords1;

    static final DecimalFormat df1 = new DecimalFormat("###,##0.00");

    String fileName;
    String queryString;
    String idEmpresa;
    UI mainUI;

    public CartaUsurpacionInvestigacionPDF(String idEmpresa) {
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
            document.addTitle("Reporte de documentos " + ((UbicaloUI) UI.getCurrent()).sessionInformation.getStrProjectName());
            document.addSubject("Using iText");
            document.addKeywords("Java, PDF, iText");
            document.addAuthor("www.ubicalo.com");
            document.addCreator("www.ubicalo.com");
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


            Paragraph guatemalaHorayFecha = new Paragraph();
            guatemalaHorayFecha.add("GT2021623109");
            guatemalaHorayFecha.setFont(small12);
            guatemalaHorayFecha.setAlignment(Element.ALIGN_RIGHT);

            document.add(guatemalaHorayFecha);

            document.add(Chunk.NEWLINE);

            Paragraph senol = new Paragraph();
            senol.add("Señor(a):");
            senol.setFont(small12);
            senol.setAlignment(Element.ALIGN_LEFT);

            document.add(senol);

            Paragraph nombre = new Paragraph();
            nombre.add("GUERRA LILIAM AMPARO");
            nombre.setFont(small12);
            nombre.setAlignment(Element.ALIGN_LEFT);

            document.add(nombre);

            Paragraph identificacion = new Paragraph();
            identificacion.add("DPI: 2305 68815 2004");
            identificacion.setFont(small12);
            identificacion.setAlignment(Element.ALIGN_LEFT);

            document.add(identificacion);

            Paragraph presente = new Paragraph();
            presente.add("Presente");
            presente.setFont(small12);
            presente.setAlignment(Element.ALIGN_LEFT);

            document.add(presente);
            document.add(Chunk.NEWLINE);

            Paragraph estimadoNombre = new Paragraph();
            estimadoNombre.add("Estimada Señora: GUERRA LILIAM AMPARO");
            estimadoNombre.setFont(small12);
            estimadoNombre.setAlignment(Element.ALIGN_LEFT);

            document.add(estimadoNombre);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);

            Paragraph contenido = new Paragraph();
            contenido.add("Por este medio hacemos de su conocimiento que: “Gestionadora de Créditos de Guatemala, S.A.” " +
                    "se convirtió en el dueño del crédito o tarjeta de crédito No. 4907517400648795 que Banco Promerica de Guatemala S.A. " +
                    "mantenía con usted en virtud de una venta y/o cesión del mismo por lo cual dicha obligación fue transferida a esta empresa.");
            contenido.setFont(small10);
            contenido.setAlignment(Element.ALIGN_JUSTIFIED);

            document.add(Chunk.NEWLINE);
            document.add(contenido);

            Paragraph contenido2 = new Paragraph();
            contenido2.add("Por este medio hacemos de su conocimiento que: “Gestionadora de Créditos de Guatemala, S.A.” " +
                    "se convirtió en el dueño del crédito o tarjeta de crédito No. 4907517400648795 que Banco Promerica de Guatemala S.A. " +
                    "mantenía con usted en virtud de una venta y/o cesión del mismo por lo cual dicha obligación fue transferida a esta empresa.");
            contenido2.setFont(small10);
            contenido2.setAlignment(Element.ALIGN_JUSTIFIED);

            document.add(Chunk.NEWLINE);
            document.add(contenido2);

            Paragraph contenido3 = new Paragraph();
            contenido3.add("Por este medio hacemos de su conocimiento que: “Gestionadora de Créditos de Guatemala, S.A.” se convirtió en el" +
                    " dueño del crédito o tarjeta de crédito No. 4907517400648795 que Banco Promerica de Guatemala S.A. mantenía con usted en virtud de una venta y/o cesión del mismo por lo cual dicha obligación fue transferida a esta empresa.  ");
            contenido3.setFont(small10);
            contenido3.setAlignment(Element.ALIGN_JUSTIFIED);

            document.add(Chunk.NEWLINE);
            document.add(contenido3);

            Paragraph contenido4 = new Paragraph();
            contenido4.add("Para los efectos que al interesado convengan.");
            contenido4.setFont(small10);
            contenido4.setAlignment(Element.ALIGN_JUSTIFIED);

            document.add(Chunk.NEWLINE);
            document.add(contenido4);

            Paragraph contenido5 = new Paragraph();
            contenido5.add("Atentamente,");
            contenido5.setFont(small10);
            contenido5.setAlignment(Element.ALIGN_LEFT);

            document.add(Chunk.NEWLINE);
            document.add(contenido5);

            Paragraph linea = new Paragraph();
            addEmptyLine(linea, 2);
            document.add(linea);

            Paragraph firma1 = new Paragraph();
            firma1.add("______________");
            firma1.setFont(small10);
            firma1.setAlignment(Element.ALIGN_LEFT);

            Paragraph firma2 = new Paragraph();
            firma2.add("Sergio Fajardo");
            firma2.setFont(small10);
            firma2.setAlignment(Element.ALIGN_LEFT);

            Paragraph firma3 = new Paragraph();
            firma3.add("Jefe de Departamento Administrativo");
            firma3.setFont(small10);
            firma3.setAlignment(Element.ALIGN_LEFT);

            Paragraph firma4 = new Paragraph();
            firma4.add("Gestionadora de Créditos de Guatemala, S.A");
            firma4.setFont(small10);
            firma4.setAlignment(Element.ALIGN_LEFT);

            document.add(firma1);
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
