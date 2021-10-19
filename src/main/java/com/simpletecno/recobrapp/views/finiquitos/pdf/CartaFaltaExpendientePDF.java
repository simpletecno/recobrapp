package com.simpletecno.recobrapp.views.finiquitos.pdf;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import com.simpletecno.recobrapp.main.RecobrAppUI;
import com.simpletecno.recobrapp.utileria.EnvironmentVars;
import com.simpletecno.recobrapp.utileria.Utileria;
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


public class CartaFaltaExpendientePDF extends Window {

    public static Statement stQuery;
    public static PreparedStatement stPreparedQuery;
    public static ResultSet rsRecords;

    static final DecimalFormat df1 = new DecimalFormat("###,##0.00");

    String fileName;
    String nombreArchivo;
    String queryString;
    UI mainUI;

    String correlativo, banco, credito, sexo = "";

    String departamento, nombre, identificacion, municipio, fechaCarta, tipoDoc;

    public CartaFaltaExpendientePDF(String correlativo) {
        this.correlativo = correlativo;
        mainUI = UI.getCurrent();

        try {
            BrowserFrame e = new BrowserFrame();
            e.setSizeFull();
            setWidth("1100");
            setHeight("700");
            center();

            llenarDatos();

            fileName = tipoDoc + correlativo+"_"+  fechaCarta.replaceAll("/","_") + ".pdf";
            nombreArchivo = fileName;

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

                guardarArchivo(correlativo, nombreArchivo);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        public InputStream getStream() {
            return new ByteArrayInputStream(os.toByteArray());
        }

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

            leerBaseDatosYEscribirLineas(document);

            preface = new Paragraph();
            addEmptyLine(preface, 1);
            document.add(preface);

        }

        private void leerBaseDatosYEscribirLineas(Document document)
                throws DocumentException {

            Utileria utileria = new Utileria();

            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);

            Paragraph guatemalaFechaYHora = new Paragraph();
            guatemalaFechaYHora.add("Guatemala " + utileria.getFechaLetras(utileria.getFecha()));
            guatemalaFechaYHora.setFont(small12);
            guatemalaFechaYHora.setAlignment(Element.ALIGN_RIGHT);

            document.add(guatemalaFechaYHora);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);

            Paragraph senol = new Paragraph();
            if (sexo.equals("Sra.")){
                senol.add("Señora: ");
            }else{
                senol.add("Señor: ");
            }
            senol.setFont(small12);
            senol.setAlignment(Element.ALIGN_LEFT);

            document.add(senol);

            Paragraph nombrePgh = new Paragraph();
            nombrePgh.add(nombre);
            nombrePgh.setFont(small12);
            nombrePgh.setAlignment(Element.ALIGN_LEFT);

            document.add(nombrePgh);;

            Paragraph identificacionPgh = new Paragraph();
            identificacionPgh.add(identificacion);
            identificacionPgh.setFont(small12);
            identificacionPgh.setAlignment(Element.ALIGN_LEFT);

            document.add(identificacionPgh);

            Paragraph presentePgh = new Paragraph();
            presentePgh.add("Presente");
            presentePgh.setFont(small12);
            presentePgh.setAlignment(Element.ALIGN_LEFT);

            document.add(presentePgh);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);

            Paragraph estimadaPgh = new Paragraph();

            if (sexo.equals("Sra.")){
                estimadaPgh.add("Estimada Señora: " + nombre);
            }else{
                estimadaPgh.add("Estimado Señor: " + nombre);
            }
            estimadaPgh.setFont(small12);
            estimadaPgh.setAlignment(Element.ALIGN_LEFT);

            document.add(estimadaPgh);
            document.add(Chunk.NEWLINE);

            Paragraph contenido = new Paragraph();
            contenido.add("Por este medio hacemos de su conocimiento que: “Gestionadora de Créditos de Guatemala, S.A.” " +
                    "se convirtió en el dueño del crédito o tarjeta de crédito No. " + credito + " que" +
                    " Banco "+ banco +" DE GUATEMALA mantenía con usted en virtud de una venta y/o cesión del" +
                    " mismo por lo cual dicha obligación fue transferida a esta empresa.");
            contenido.setFont(small10);
            contenido.setAlignment(Element.ALIGN_JUSTIFIED);

            document.add(Chunk.NEWLINE);
            document.add(contenido);

            Paragraph contenido2 = new Paragraph();
            contenido2.add("Hacemos constar que, de acuerdo a la investigación realizada a solicitud de su persona " +
                    "se detendrá el proceso de cobro de la tarjeta de crédito/préstamo " +
                    "número " +credito+" que aparece a su nombre, ya que el expediente no fue proporcionado " +
                    "por la institución emisora; por lo anterior su persona no tiene ninguna responsabilidad sobre el saldo de la misma");
            contenido2.setFont(small10);
            contenido2.setAlignment(Element.ALIGN_JUSTIFIED);

            document.add(Chunk.NEWLINE);
            document.add(contenido2);

            document.add(Chunk.NEWLINE);

            Paragraph atentamentePgh = new Paragraph();
            atentamentePgh.add("Atentamente,");
            atentamentePgh.setFont(small10);
            atentamentePgh.setAlignment(Element.ALIGN_LEFT);

            document.add(Chunk.NEWLINE);
            document.add(atentamentePgh);
            document.add(Chunk.NEWLINE);

            Paragraph linea = new Paragraph();
            addEmptyLine(linea, 2);
            document.add(linea);

            Paragraph firma1 = new Paragraph();
            firma1.add("________________");
            firma1.setFont(small10);
            firma1.setAlignment(Element.ALIGN_LEFT);

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

            document.add(firma1);
            document.add(firma2);
            document.add(firma3);
            document.add(firma4);

        }

        public void guardarArchivo(String codigoPartida, String fileName) {
            try {

                EnvironmentVars enviromentsVars = new EnvironmentVars();
                String filePath = enviromentsVars.getDtePath();

                nombreArchivo = filePath + tipoDoc +correlativo + "_"+ fechaCarta.replaceAll("/", "_") + ".pdf";

                //String direccion = "C:\\\\Users\\\\jzepeda\\\\Documents\\\\DESARROLLO APP\\\\VAADIN 7\\\\recobroweb\\\\src\\\\main\\\\webapp\\\\pdfreceipts\\\\"+nombreArchivo;

               // String path = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath() + "/pdfreceipts/"+nombreArchivo;

                queryString = " Update finiquito set  ";
                queryString += "  ArchivoTipo = 'application/pdf'";
                queryString += ", ArchivoNombre = '" + nombreArchivo +"'";
                queryString += " where Correlativo = " + codigoPartida ;

                System.out.println("query insert imagen" + queryString);

                stPreparedQuery = ((RecobrAppUI) mainUI).databaseProvider.getCurrentConnection().prepareStatement(queryString);
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

        queryString = "Select * from finiquito ";
        queryString += " Where Correlativo = " + correlativo;

        System.out.println("Query a imprimir es " + queryString);

        try{
            stQuery = ((RecobrAppUI) mainUI).databaseProvider.getCurrentConnection().createStatement();
            rsRecords = stQuery.executeQuery(queryString);

            if (rsRecords.next()){
                System.out.println("Encontro los registros en query");

                identificacion = rsRecords.getString("Identificacion");
                nombre = rsRecords.getString("Nombre");
                departamento = rsRecords.getString("Departamento");
                municipio = rsRecords.getString("Municipio");
                sexo = rsRecords.getString("Sexo");
                banco = rsRecords.getString("Banco");
                credito = rsRecords.getString("Credito");
                fechaCarta = Utileria.getFechaDDMMYYYY(rsRecords.getDate("Fecha"));
                tipoDoc = rsRecords.getString("Tipo");

            }
        }catch (Exception ex){
            System.out.println("Error al intentar buscar el correlativo " +ex);
            ex.printStackTrace();
        }

    }
}
