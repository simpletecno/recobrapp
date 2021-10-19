package com.simpletecno.recobrapp.views.finiquitos;

import com.simpletecno.recobrapp.main.RecobrAppUI;
import com.simpletecno.recobrapp.utileria.EnvironmentVars;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import com.wcs.wcslib.vaadin.widget.multifileupload.ui.MultiFileUpload;
import com.wcs.wcslib.vaadin.widget.multifileupload.ui.UploadFinishedHandler;
import com.wcs.wcslib.vaadin.widget.multifileupload.ui.UploadStateWindow;

import java.io.*;

import static com.simpletecno.recobrapp.views.finiquitos.ConsultarFiniquitosView.stPreparedQuery;

public class CargarArchivoIngresoDocumentos extends Window {

    UI mainUI;
    MultiFileUpload singleUpload;
    public File file;
    StreamResource logoStreamResource = null;
    long fileSize;

    Object selectedObjectUpdate;
    String correlativo, fechaDoc, tipoDoc;

    public CargarArchivoIngresoDocumentos(Object selectedObject, String correlativo, String tipoDoc, String fechaDoc) {
        this.mainUI = UI.getCurrent();
        this.selectedObjectUpdate = selectedObject;
        this.correlativo = correlativo;
        this.fechaDoc = fechaDoc;
        this.tipoDoc  =tipoDoc;

        setResponsive(true);
        setWidth("50%");
        setHeight("30%");

        EnvironmentVars enviromentsVars = new EnvironmentVars();

        Label titleLbl = new Label("CARGAR DOCUMENTO");
        titleLbl.addStyleName(ValoTheme.LABEL_H1);
        titleLbl.setSizeUndefined();
        titleLbl.addStyleName("h1_custom");

        VerticalLayout contentLayout = new VerticalLayout();
        contentLayout.setSpacing(true);
        contentLayout.setMargin(true);
        contentLayout.addComponents(titleLbl);
        contentLayout.setComponentAlignment(titleLbl, Alignment.TOP_CENTER);

        UploadFinishedHandler handler;
        handler = new UploadFinishedHandler() {
            @Override
            public void handleFile(InputStream stream, String fileName, String mimeType, long length) {

                File targetFile;

                try {
                    if (mimeType.contains("png") || mimeType.contains("jpeg") || mimeType.contains("jpg") || mimeType.contains("pdf")) {

                        fileSize = stream.available();
                        byte[] buffer = new byte[stream.available()];
                        stream.read(buffer);

                        String filePath = enviromentsVars.getDtePath();

                        new File(filePath).mkdirs();

                        System.out.println("FILE PATH " + filePath);
                        System.out.println("tIPO DOC " + tipoDoc);
                        System.out.println("Correlativo " + correlativo);
                        System.out.println("Fechadoc " + fechaDoc);

                        fileName = filePath + tipoDoc +"_" +correlativo + "_"+ fechaDoc.replaceAll("/","-") ;

                        System.out.println("FILE NAME" + fileName);

                        targetFile = new File(fileName);
                        OutputStream outStream = new FileOutputStream(targetFile);
                        outStream.write(buffer);
                        outStream.close();

                        stream.close();

                        logoStreamResource = null;

                        if (buffer != null) {
                            logoStreamResource = new StreamResource(
                                    new StreamResource.StreamSource() {
                                        public InputStream getStream() {
                                            return new ByteArrayInputStream(buffer);
                                        }
                                    }, String.valueOf(System.currentTimeMillis())
                            );
                        }

                        file = targetFile;

                        Notification.show("Archivo cargado con exito!!..", Notification.Type.TRAY_NOTIFICATION);

                        guardarArchivo(selectedObject, correlativo, fileName);

                    } else {
                        Notification.show("El archivo no contiene un formato compatible. solo puede subir archivos con formato 'PNG','JEPG','JPG','PDF'", Notification.Type.ERROR_MESSAGE);
                        return;
                    }

                } catch (Exception fIoEx) {
                    fIoEx.printStackTrace();
                    Notification.show("Error al cargar el archivo adjunto!", Notification.Type.ERROR_MESSAGE);
                    return;
                }
            }
        };

        UploadStateWindow window = new UploadStateWindow();

        singleUpload = new MultiFileUpload(handler, window, false);
        singleUpload.setIcon(FontAwesome.UPLOAD);
        singleUpload.setImmediate(true);
        singleUpload.getSmartUpload().setUploadButtonCaptions("Buscar y cargar archivo", "");

        JavaScript.getCurrent().execute("document.getElementsByClassName('gwt-FileUpload')[0].setAttribute('accept', '.png')");
        JavaScript.getCurrent().execute("document.getElementsByClassName('gwt-FileUpload')[1].setAttribute('accept', '.jpg')");
        JavaScript.getCurrent().execute("document.getElementsByClassName('gwt-FileUpload')[2].setAttribute('accept', '.PDF')");

        HorizontalLayout componentsLayout = new HorizontalLayout();
        componentsLayout.setMargin(true);
        componentsLayout.setSpacing(true);

        componentsLayout.addComponents(singleUpload);
        contentLayout.addComponent(componentsLayout);
        contentLayout.setComponentAlignment(componentsLayout, Alignment.TOP_CENTER);

        setContent(contentLayout);

    }

    public void guardarArchivo(Object selectedObject, String correlativo, String fileName) {

        try {

           // String path = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath() + "/pdfreceipts/"+fileName;

            //String direccion = "C:\\\\Users\\\\jzepeda\\\\Documents\\\\DESARROLLO APP\\\\VAADIN 7\\\\recobrapp\\\\src\\\\main\\\\webapp\\\\pdfreceipts\\\\"+nombreDoc;

            String queryString = " Update finiquito set  ";
            queryString += "  ArchivoTipo = 'application/pdf'";
            queryString += ", ArchivoNombre = '" + fileName +"'";
            queryString += " where Correlativo = " + correlativo ;

            System.out.println("QUERY A INSERTART = " + queryString);

            stPreparedQuery = ((RecobrAppUI) mainUI).databaseProvider.getCurrentConnection().prepareStatement(queryString);
            stPreparedQuery.executeUpdate();

            if (mainUI.getNavigator().getCurrentView().getClass().getSimpleName().equals("ConsultarFiniquitosView")) {
                System.out.println("nombre que lleva para actualizar " + fileName);

                ((ConsultarFiniquitosView) (mainUI.getNavigator().getCurrentView())).finiquitosContainer.getContainerProperty(selectedObject, ConsultarFiniquitosView.IMAGEN_PROPERTY).setValue("Visualizar");
                ((ConsultarFiniquitosView) (mainUI.getNavigator().getCurrentView())).finiquitosContainer.getContainerProperty(selectedObject, ConsultarFiniquitosView.ARCHIVO_PROPERTY).setValue(fileName);
                //((ConsultarFiniquitosView) (mainUI.getNavigator().getCurrentView())).finiquitosContainer.getContainerProperty(selectedObject, ConsultarFiniquitosView.ARCHIVO_TIPO_PROPERTY).setValue(paramet);
            }
            close();

        } catch (Exception ex) {
            System.out.println("Error al intentar insertar Imagen" + ex);
            Notification.show("Error al insertar la imagen : " + ex.getMessage(), Notification.Type.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}
