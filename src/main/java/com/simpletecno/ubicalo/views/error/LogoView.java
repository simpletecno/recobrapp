package com.simpletecno.ubicalo.views.error;

import com.simpletecno.ubicalo.main.UbicaloUI;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.*;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;

import java.io.*;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings("unchecked")
public class LogoView extends VerticalLayout implements View {

    UI mainUI = UI.getCurrent();

    private static Statement stQuery;
    private static PreparedStatement stPreparedQuery;
    private static ResultSet rsRecords;

    String queryString;

    Upload uploader;
    Image personPhoto;

    Button guardarBtn;

    ImageUploader receiver = new ImageUploader();

    public LogoView() {
        setWidth("100%");
        setMargin(new MarginInfo(true, false, false, false));
        setSpacing(true);

        Label titleLbl = new Label("Prueba Cargar Logo");
        titleLbl.addStyleName("h2");
        titleLbl.setSizeUndefined();

        ThemeResource resource = new ThemeResource("img/profile-pic-300px.jpg");

        personPhoto = new Image("", resource);
        personPhoto.setImmediate(true);
        personPhoto.setWidth("100px");
        personPhoto.setHeight("100px");
        personPhoto.addStyleName("user-menu");

        uploader = new Upload("Logo de la empresa", receiver);
        uploader.setButtonCaption("Cargar");
        uploader.addSucceededListener(receiver);

        Button guardarBtn = new Button("Gurdar");
        guardarBtn.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                guardarImagen();
            }
        });

        HorizontalLayout titleLayout = new HorizontalLayout();
        titleLayout.addStyleName("v-component-group");
        titleLayout.setResponsive(true);
        titleLayout.setSpacing(true);
        titleLayout.setWidth("100%");
        titleLayout.setMargin(new MarginInfo(false, true, false, true));
        titleLayout.addComponents(titleLbl);
        titleLayout.setComponentAlignment(titleLbl, Alignment.MIDDLE_LEFT);

        HorizontalLayout pictureLayout = new HorizontalLayout();
        pictureLayout.addComponent(personPhoto);
        pictureLayout.addComponent(uploader);
        pictureLayout.addComponent(guardarBtn);
        pictureLayout.setComponentAlignment(uploader, Alignment.MIDDLE_CENTER);
        pictureLayout.setComponentAlignment(uploader, Alignment.MIDDLE_RIGHT);

        addComponent(titleLayout);
        setComponentAlignment(titleLayout, Alignment.TOP_CENTER);

        addComponent(pictureLayout);
        setComponentAlignment(pictureLayout, Alignment.TOP_CENTER);

    }

    public void guardarImagen(){
        try {

            queryString = "update empresa Set ";

            ByteArrayInputStream inputStream1 = null;

            if(receiver.fis != null) {

                System.out.println("\nfis file size=" + receiver.fis.getChannel().size());

                final byte []thisArray = new byte[(int)receiver.fis.getChannel().size()];
                receiver.fis.read(thisArray,0,thisArray.length);
                inputStream1 = new ByteArrayInputStream(thisArray);
                queryString += "LogoNombre = '" + receiver.file.getName() + "'";
                queryString += ",Documento = ?";
                ((UbicaloUI)UI.getCurrent()).sessionInformation.setPhotoStreamResource(new StreamResource(
                        new StreamResource.StreamSource() {
                            public InputStream getStream() {
                                return new ByteArrayInputStream(thisArray);
                            }
                        },receiver.file.getName()));                
            }

            queryString += " Where IdEmpresa = 1";

            System.out.println("insertar " + queryString);

            stPreparedQuery  = ((UbicaloUI) UI.getCurrent()).databaseProvider.getCurrentConnection().prepareStatement(queryString);

            if(receiver.file != null) {
                stPreparedQuery.setBinaryStream(1, inputStream1, inputStream1.available());
//                    receiver.file.delete();
            }

            System.out.println("preparedQuery="+stPreparedQuery.toString());

            stPreparedQuery.execute();

            Notification.show("Actualización de perfil exitoso!", Notification.Type.HUMANIZED_MESSAGE);

            mostrar();

        }
        catch(Exception ex1) {
            Logger.getLogger(LogoView.class.getName()).log(Level.SEVERE, ex1.getMessage());
            Notification.show("ERROR FATAL DEL SISTEMA", Notification.Type.ERROR_MESSAGE);
            System.out.println("ERROR AL INTENTAR ACTUALIZAR PERFIL : " + ex1.getMessage());
            ex1.printStackTrace();
        }
    }

    public void mostrar()  {

        try {
            String sql = "SELECT Documento FROM empresa where IdEmpresa=1";

            stQuery = ((UbicaloUI) mainUI).databaseProvider.getCurrentConnection().createStatement();
            rsRecords = stQuery.executeQuery(sql);

            if (rsRecords.next()) { //  encontrado
                do {
                    String pathname = "C:\\Users\\jzepeda\\Desktop\\pdf_recupado.pdf";
                    File file = new File(pathname);
                    FileOutputStream output = new FileOutputStream(file);
                    Blob archivo = rsRecords.getBlob("Documento");
                    InputStream inStream = archivo.getBinaryStream();
                    int length = -1;
                    int size = (int) archivo.length();
                    byte[] buffer = new byte[size];
                    while ((length = inStream.read(buffer)) != -1) {
                        output.write(buffer, 0, length);
                    }
                    output.close();
                } while (rsRecords.next());
            }
        }catch (Exception exception) {
            System.out.println("error ;" + exception);
            exception.printStackTrace();
        }
    }

    class ImageUploader implements Receiver, SucceededListener {
        public File file;
        public FileOutputStream fos = null; // Stream to write to
        public FileInputStream fis = null;
        @Override
        public OutputStream receiveUpload(String filename,
                                          String mimeType) {
            // Create upload stream
            try {
                // Open the file for writing.

                new File(VaadinService.getCurrent().getBaseDirectory().getAbsolutePath() + "/WEB-INF/LOGOS/" + ((UbicaloUI) UI.getCurrent()).sessionInformation.getStrCompanyId()).mkdirs();
                file = new File(VaadinService.getCurrent().getBaseDirectory().getAbsolutePath() + "/WEB-INF/LOGOS/" + ((UbicaloUI) UI.getCurrent()).sessionInformation.getStrCompanyId() + "/" + filename);
                System.out.println("\nfile="+file.getAbsolutePath());
                fos = new FileOutputStream(file);
            } catch (final java.io.FileNotFoundException e) {
                new Notification("El archivo se puede abrir o leer",
                        e.getMessage(),
                        Notification.Type.ERROR_MESSAGE)
                        .show(Page.getCurrent());
                return null;
            }
            return fos;
        }
        @Override
        public void uploadSucceeded(SucceededEvent event) {
            Notification.show("Archivo cargado con exito!", Notification.Type.TRAY_NOTIFICATION);
            personPhoto.setSource(new FileResource(file));
            try {
                fis = new FileInputStream(file);
                //file.delete();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(LogoView.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    };



    public void enter(ViewChangeListener.ViewChangeEvent event) {
        Page.getCurrent().setTitle("REPORTOWEB - Prueba Imagen" );
    }
}

