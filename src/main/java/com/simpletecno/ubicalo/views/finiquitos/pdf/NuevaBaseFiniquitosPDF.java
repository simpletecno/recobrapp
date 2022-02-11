package com.simpletecno.ubicalo.views.finiquitos.pdf;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.simpletecno.ubicalo.main.UbicaloUI;
import com.simpletecno.ubicalo.utileria.EnvironmentVars;
import com.simpletecno.ubicalo.utileria.Utileria;
import com.vaadin.server.StreamResource;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

import java.io.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;



public class NuevaBaseFiniquitosPDF extends Window {

    public static Statement stQuery;
    public static PreparedStatement stPreparedQuery;
    public static ResultSet rsRecords;

    String fileName;

    UI mainUI;

    String correlativo;

    String nombreArchivo;

    Date fechaDt;

    String queryString, cuenta, nombre, identificacion, municipio, departamento;
    String credito, banco, sexo, fechaFiniquito;

    public NuevaBaseFiniquitosPDF(String correlativo) {
        this.correlativo = correlativo;
        this.mainUI = UI.getCurrent();
        try {

            BrowserFrame e = new BrowserFrame();
            e.setSizeFull();
            setWidth("1100");
            setHeight("700");
            center();

            llenarDatos();

            fileName = "Finiquito_" + correlativo + "_" +  fechaFiniquito.replaceAll("/","-") + ".pdf";
            nombreArchivo = fileName;

            StreamResource pdfResource;
            pdfResource = new StreamResource(new Pdf(fileName), fileName);
            pdfResource.setMIMEType("application/pdf");

            e.setSource(pdfResource);
            setContent(e);

            File file = new File(fileName);
            file.delete();

        } catch (Exception allEx) {
            Notification.show("Error al generar el pdf." + allEx, Notification.Type.ERROR_MESSAGE);
            allEx.printStackTrace();
        }

    }
    public class Pdf implements StreamResource.StreamSource, Serializable {

        private final String RECEIPTFILE
                = VaadinService.getCurrent()
                .getBaseDirectory().getAbsolutePath() + "/pdfreceipts/";

        public final Font smallBold12 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
        public final Font smallBold14 = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD);
        private final Font small10 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);
        private final Font small12 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);

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
                addContent(document);
                document.close();

                addMetaData(document2);
                addContent(document2);
                document2.close();

                guardarArchivo(correlativo, fileName);

            } catch (Exception e) {
                Notification.show("Error al generar el pdf." + e, Notification.Type.ERROR_MESSAGE);
                e.printStackTrace();
            }

        }

        @Override
        public InputStream getStream() {

            return new ByteArrayInputStream(os.toByteArray());

        }

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

            leerBaseDatosYEscribirLineas(document);

            preface = new Paragraph();
            addEmptyLine(preface, 1);
            document.add(preface);

        }

        private void leerBaseDatosYEscribirLineas(Document document)
                throws DocumentException {

            Utileria utileria = new Utileria();

            Paragraph header = new Paragraph("Guatemala " + utileria.getFechaLetras(utileria.getFecha()));
            header.setFont(small12);
            header.setAlignment(Element.ALIGN_RIGHT);

            document.add(header);

            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
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

            Paragraph contenido = new Paragraph();
            contenido.add("Por este medio hacemos de su conocimiento que Gestionadora de Créditos de Guatemala, S.A. " +
                    "es propietaria del crédito o tarjeta de crédito No. "+ credito + " que "+ banco +
                    " mantenía con usted en virtud de una venta y cesión del crédito por medio de un contrato de " +
                    "subrogación en la cual dicho activo crediticio fue transferido a esta empresa.");
            contenido.setFont(small10);
            contenido.setAlignment(Element.ALIGN_JUSTIFIED);

            document.add(Chunk.NEWLINE);
            document.add(contenido);

            Paragraph contenido2 = new Paragraph();
            contenido2.add("En atención a lo anterior hacemos de su conocimiento que todo tema relacionado con " +
                    "dicha obligación ha de ser manejada con los personeros de Gestionadora de " +
                    "Créditos de Guatemala, S.A.");
            contenido2.setFont(small10);
            contenido2.setAlignment(Element.ALIGN_JUSTIFIED);

            document.add(Chunk.NEWLINE);
            document.add(contenido2);


            Label finiquito = new Label("Finiquito");
            finiquito.addStyleName(ValoTheme.LABEL_H3);

            Paragraph contenido3 = new Paragraph();

            contenido3.add("GESTIONADORA DE CREDITOS DE GUATEMALA, S.A. en su calidad de propietaria " +
                    "del activo crediticio arriba indicado extiende el presente "+ finiquito.getValue() + ".") ;
            contenido3.setFont(small10);
            contenido3.setAlignment(Element.ALIGN_JUSTIFIED);

            document.add(Chunk.NEWLINE);
            document.add(contenido3);

            if (sexo.equals("Sr.")){
                sexo = "El señor";
            }else{
                sexo = "La señora";
            }
            Paragraph contenido4 = new Paragraph();
            contenido4.add(sexo + " " + nombre + " quien se identifica con el Documento " +
                    "Personal de Identificación " + identificacion +" Registro Nacional de las personas " +
                    "RENAP municipio "+ municipio +" del departamento de "+departamento +" quien canceló la totalidad de la " +
                    "obligación que mantenía con nuestra representada bajo la operación "+ cuenta + ".");
            contenido4.setFont(small10);
            contenido4.setAlignment(Element.ALIGN_JUSTIFIED);

            document.add(Chunk.NEWLINE);
            document.add(contenido4);

            Paragraph contenido5 = new Paragraph();
            contenido5.add("Dicha cuenta se encuentra cancelada en su totalidad desde el " + utileria.getFechaLetras(fechaFiniquito) + ".");
            contenido5.setFont(small10);
            contenido5.setAlignment(Element.ALIGN_JUSTIFIED);

            document.add(Chunk.NEWLINE);
            document.add(contenido5);

            Paragraph contenido6 = new Paragraph();
            contenido6.add("Se extiende la presente a solicitud del interesado el " + utileria.getFechaLetras(utileria.getFecha()) +".");
            contenido6.setFont(small10);
            contenido6.setAlignment(Element.ALIGN_JUSTIFIED);

            document.add(Chunk.NEWLINE);
            document.add(contenido6);

            document.add(Chunk.NEWLINE);

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
            firma3.add("Gestionadora de Créditos de Guatemala, S.A");
            firma3.setFont(small10);
            firma3.setAlignment(Element.ALIGN_LEFT);

            document.add(firma1);
            document.add(firma2);
            document.add(firma3);

        }

        public void guardarArchivo(String correlativo, String file) {
            try {

                EnvironmentVars enviromentsVars = new EnvironmentVars();
                String filePath = enviromentsVars.getDtePath();
                nombreArchivo = filePath + "Finiquito_" +correlativo + "_"+ fechaFiniquito.replaceAll("/", "-") + ".pdf";

                System.out.println("path " + nombreArchivo);

                queryString = " Update finiquito set  ";
                queryString += "  ArchivoTipo = 'application/pdf'";
                queryString += ", ArchivoNombre = '" + nombreArchivo + "'";
                queryString += " where Correlativo = " + correlativo ;

                System.out.println("query insert doc" + queryString);

                stPreparedQuery = ((UbicaloUI) mainUI).databaseProvider.getCurrentConnection().prepareStatement(queryString);
                stPreparedQuery.executeUpdate();

            } catch (Exception ex) {
                System.out.println("Error al intentar insertar Imagen" + ex);
                Notification.show("Error al insertar la imagen : " + ex.getMessage(), Notification.Type.ERROR_MESSAGE);
            }
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

    public void llenarDatos(){
        Utileria utileria = new Utileria();

        queryString = "Select * from finiquito ";
        queryString += " Where Correlativo = " + correlativo;

        try{
            stQuery = ((UbicaloUI) mainUI).databaseProvider.getCurrentConnection().createStatement();
            rsRecords = stQuery.executeQuery(queryString);

            if (rsRecords.next()){

                nombre = rsRecords.getString("Nombre");
                cuenta = rsRecords.getString("Cuenta");
                municipio = rsRecords.getString("Municipio");
                departamento = rsRecords.getString("Departamento");
                identificacion = rsRecords.getString("Identificacion");
                sexo = rsRecords.getString("Sexo");
                banco = rsRecords.getString("Banco");
                credito = rsRecords.getString("Credito");
                fechaFiniquito = utileria.getFechaDDMMYYYY(rsRecords.getDate("Fecha"));

            }
        }catch (Exception ex){
            System.out.println("Error al intentar buscar el correlativo " + ex);
            Notification.show("Error al generar el pdf." + ex, Notification.Type.ERROR_MESSAGE);

            ex.printStackTrace();
        }


    }
}
