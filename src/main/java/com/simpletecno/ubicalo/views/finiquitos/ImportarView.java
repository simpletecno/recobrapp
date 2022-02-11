/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.simpletecno.ubicalo.views.finiquitos;

import com.simpletecno.ubicalo.main.UbicaloUI;
import com.simpletecno.ubicalo.utileria.Utileria;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinService;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import com.wcs.wcslib.vaadin.widget.multifileupload.ui.MultiFileUpload;
import com.wcs.wcslib.vaadin.widget.multifileupload.ui.UploadFinishedHandler;
import com.wcs.wcslib.vaadin.widget.multifileupload.ui.UploadStateWindow;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.vaadin.dialogs.ConfirmDialog;

import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author joseaguirre
 */
public class ImportarView extends VerticalLayout implements View {

    UI mainUI;
    Statement stQuery = null, stQuery1 = null;
    ResultSet rsRecords;

    MarginInfo marginInfo;

    MultiFileUpload singleUpload;

    Button limpiarBtn, cargarBtn;

    Table finiquitosTable;
    public File finiquitosFile;
    public XSSFWorkbook workbook;
    public XSSFSheet sheet;
    private FileInputStream fileInputStream;

    String queryString;
    String identificacion = "";
    String nombre = "";
    String municipio = "";
    String departamento = "";
    String cuenta = "";
    String tipo = "";

    ArrayList<String> repetidos = new ArrayList<String>();

    public ImportarView() {
        Page.getCurrent().setTitle("UBICALO- Importar Finiquitos");
        this.mainUI = UI.getCurrent();
        marginInfo = new MarginInfo(true, false, false, false);
        setSpacing(true);

        HorizontalLayout titleLayout = new HorizontalLayout();
        titleLayout.setMargin(new MarginInfo(true, true, false, true));
        titleLayout.addStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
        titleLayout.setResponsive(true);
        titleLayout.setSpacing(true);
        titleLayout.setWidth("100%");

        Label titleLbl = new Label("IMPORTAR FINIQUITOS");
        titleLbl.addStyleName(ValoTheme.LABEL_H2);
        titleLbl.setSizeUndefined();
        titleLbl.addStyleName("h1_custom");

        UploadFinishedHandler handler;
        handler = (InputStream stream, String fileName, String mimeType, long length) -> {
            File targetFile;

            try {

                byte[] buffer = new byte[stream.available()];
                stream.read(buffer);
                String filePath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath() + "/projectfiles/" + ((UbicaloUI) mainUI).sessionInformation.getStrCompanyId() + "/";

                new File(filePath).mkdirs();

                fileName = filePath + fileName;
                targetFile = new File(fileName);
                OutputStream outStream = new FileOutputStream(targetFile);
                outStream.write(buffer);
                outStream.close();
                stream.close();

                cargarPlanilla(targetFile);
                finiquitosFile = targetFile;

            } catch (java.io.IOException fIoEx) {
                fIoEx.printStackTrace();
                Notification.show("Error al cargar el archivo adjunto!", Notification.Type.ERROR_MESSAGE);
                return;
            }
        };

        UploadStateWindow window = new UploadStateWindow();

        singleUpload = new MultiFileUpload(handler, window, false);
        singleUpload.setIcon(FontAwesome.UPLOAD);
        singleUpload.setImmediate(true);
        singleUpload.getSmartUpload().setUploadButtonCaptions("Abrir archivo", "");

        JavaScript.getCurrent().execute("document.getElementsByClassName('gwt-FileUpload')[0].setAttribute('accept', '.xlsx')");

        List<String> acceptedMimeTypes = new ArrayList();
        acceptedMimeTypes.add("application/octet-stream");
        acceptedMimeTypes.add("application/ovnd.ms-excel");
        acceptedMimeTypes.add("application/msexcel");
        acceptedMimeTypes.add("application/x-msexcel");
        acceptedMimeTypes.add("application/x-ms-excel");
        acceptedMimeTypes.add("application/x-excel");
        acceptedMimeTypes.add("application/x-dos_ms_excel");
        acceptedMimeTypes.add("application/xls");
        acceptedMimeTypes.add("application/x-xls");

        VerticalLayout contenidoLayout = new VerticalLayout();
        contenidoLayout.setWidth("98%");
        contenidoLayout.setHeight("95%");
        contenidoLayout.setMargin(marginInfo);
        contenidoLayout.setSpacing(true);
        contenidoLayout.addStyleName("rcorners3");

        finiquitosTable = new Table();
        finiquitosTable.setWidth("100%");

        finiquitosTable.addContainerProperty("No", String.class, "");
        finiquitosTable.addContainerProperty("Fecha", String.class, "");

        finiquitosTable.addContainerProperty("Identificación", String.class, "");
        finiquitosTable.addContainerProperty("Nombre", String.class, "");
        finiquitosTable.addContainerProperty("Municipio", String.class, "");

        finiquitosTable.addContainerProperty("Departamento", String.class, "");
        finiquitosTable.addContainerProperty("Cuenta", String.class, "");
        finiquitosTable.addContainerProperty("Tipo", String.class, "");

        finiquitosTable.setColumnAlignments(new Table.Align[]{
                Table.Align.LEFT, Table.Align.LEFT, Table.Align.LEFT,
                Table.Align.LEFT, Table.Align.LEFT, Table.Align.LEFT,
                Table.Align.LEFT, Table.Align.LEFT
        });

        finiquitosTable.setColumnWidth("Correlativo", 50);
        finiquitosTable.setColumnWidth("Fecha", 90);
        finiquitosTable.setColumnWidth("Identificación", 140);
        finiquitosTable.setColumnWidth("Nombre", 300);
        finiquitosTable.setColumnWidth("Municipio", 150);
        finiquitosTable.setColumnWidth("Departamento", 150);
        finiquitosTable.setColumnWidth("Cuenta", 230);
        finiquitosTable.setColumnWidth("Tipo", 150);
        finiquitosTable.setSelectable(true);

        HorizontalLayout buttonsLayout = new HorizontalLayout();
        buttonsLayout.setSpacing(true);
        buttonsLayout.setMargin(false);

        titleLayout.addComponents(titleLbl);
        titleLayout.setComponentAlignment(titleLbl, Alignment.MIDDLE_LEFT);

        addComponent(titleLayout);
        setComponentAlignment(titleLayout, Alignment.TOP_CENTER);

        contenidoLayout.addComponent(finiquitosTable);
        contenidoLayout.setComponentAlignment(finiquitosTable, Alignment.MIDDLE_CENTER);

        cargarBtn = new Button("VALIDAR Y GUARDAR");
        cargarBtn.setIcon(FontAwesome.SAVE);
        cargarBtn.setEnabled(false);
        cargarBtn.addStyleName(ValoTheme.BUTTON_LINK);
        cargarBtn.addStyleName(".v-button { text-decoration : underline;}");
        cargarBtn.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                if (finiquitosTable.size() > 0) {
                    validarYGuardar();
                }
            }
        });

        limpiarBtn = new Button("LIMPIAR");
        limpiarBtn.setIcon(FontAwesome.ERASER);
        limpiarBtn.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                finiquitosTable.removeAllItems();
            }
        });

        buttonsLayout.addComponents(singleUpload, cargarBtn, limpiarBtn);
        buttonsLayout.setComponentAlignment(singleUpload, Alignment.BOTTOM_CENTER);
        buttonsLayout.setComponentAlignment(cargarBtn, Alignment.BOTTOM_CENTER);
        buttonsLayout.setComponentAlignment(limpiarBtn, Alignment.BOTTOM_LEFT);

        contenidoLayout.addComponent(buttonsLayout);
        contenidoLayout.setComponentAlignment(buttonsLayout, Alignment.BOTTOM_CENTER);

        addComponent(contenidoLayout);
        setComponentAlignment(contenidoLayout, Alignment.MIDDLE_CENTER);
    }

    private void cargarPlanilla(File planillaFile) {

        finiquitosTable.removeAllItems();

        singleUpload.setEnabled(false);

        try {

            fileInputStream = new FileInputStream(planillaFile);

            workbook = new XSSFWorkbook(fileInputStream);

            sheet = workbook.getSheetAt(0);

            System.out.println("\n Total lineas en archivo=" + sheet.getLastRowNum());
            System.out.println("...INICIO...");

            cargarBtn.setEnabled(true);

            for (int linea = 1; linea <= sheet.getLastRowNum(); linea++) {

                if (sheet.getRow(linea).getCell(0) != null && sheet.getRow(linea).getCell(0).getCellType() != Cell.CELL_TYPE_BLANK) {

                    if (sheet.getRow(linea).getCell(1) != null && sheet.getRow(linea).getCell(1).getCellType() != Cell.CELL_TYPE_BLANK) {

                        if (sheet.getRow(linea).getCell(2) != null && sheet.getRow(linea).getCell(2).getCellType() != Cell.CELL_TYPE_BLANK) {

                            if (sheet.getRow(linea).getCell(1).getCellType() != Cell.CELL_TYPE_STRING) {

                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

                                identificacion = new DataFormatter().formatCellValue(sheet.getRow(linea).getCell(2));

                                nombre = new DataFormatter().formatCellValue(sheet.getRow(linea).getCell(3));

                                municipio = new DataFormatter().formatCellValue(sheet.getRow(linea).getCell(4));

                                departamento = new DataFormatter().formatCellValue(sheet.getRow(linea).getCell(5));

                                cuenta = new DataFormatter().formatCellValue(sheet.getRow(linea).getCell(6));

                                tipo = new DataFormatter().formatCellValue(sheet.getRow(linea).getCell(7));

                                //System.out.println("linea:" + linea); // Comentado temporalmente descomentar para ver en que linea se produjo el error

                                //System.out.println(String.valueOf(sheet.getRow(linea).getCell(0).getNumericCellValue()));
                                //System.out.println(sdf.format(sheet.getRow(linea).getCell(1).getDateCellValue()));
                                //System.out.println(identificacion);
                                //System.out.println(nombre);
                                //System.out.println(municipio);
                                //System.out.println(departamento);
                                //System.out.println(cuenta);
                                //System.out.println(tipo);
                                //System.out.println(observaciones);

                                finiquitosTable.addItem(new Object[]{
                                        String.valueOf(sheet.getRow(linea).getCell(0).getNumericCellValue()),
                                        sdf.format(sheet.getRow(linea).getCell(1).getDateCellValue()),
                                        String.valueOf(identificacion),
                                        String.valueOf(nombre),
                                        String.valueOf(municipio),
                                        String.valueOf(departamento),
                                        String.valueOf(cuenta),
                                        String.valueOf(tipo)
                                }, finiquitosTable.size() + 1);

                            }
                        }
                    }
                } else {


                }

            } //endfor

            //System.out.println("...FIN...");

        } catch (Exception ex1) {
            new Notification("Error al intentar cargar los finiquitos del archivo EXCEL.",
                    ex1.getMessage(),
                    Notification.Type.ERROR_MESSAGE)
                    .show(Page.getCurrent());
            ex1.printStackTrace();
        }
        singleUpload.setEnabled(true);
    }

    void validarYGuardar() {
        ConfirmDialog.show(UI.getCurrent(), "Confirme:", "Está seguro de CARGAR este archivo ?",
                "SI", "NO", new ConfirmDialog.Listener() {
                    public void onClose(ConfirmDialog dialog) {
                        if (dialog.isConfirmed()) {

                            dialog.setCaption("Procesando...");
                            dialog.setDescription(" Espere por favor.... ");
                            dialog.getOkButton().setEnabled(false);

                            try {

                                stQuery1 = ((UbicaloUI) UI.getCurrent()).databaseProvider.getCurrentConnection().createStatement();
                                ((UbicaloUI) mainUI).databaseProvider.getCurrentConnection().setAutoCommit(false);
                                stQuery = ((UbicaloUI) mainUI).databaseProvider.getCurrentConnection().createStatement();

                                fileInputStream = new FileInputStream(finiquitosFile);
                                workbook = new XSSFWorkbook(fileInputStream);
                                sheet = workbook.getSheetAt(0);

                                for (int linea = 1; linea <= sheet.getLastRowNum(); linea++) {

                                    if (sheet.getRow(linea).getCell(0) != null && sheet.getRow(linea).getCell(0).getCellType() != Cell.CELL_TYPE_BLANK) {

                                        if (sheet.getRow(linea).getCell(1) != null && sheet.getRow(linea).getCell(1).getCellType() != Cell.CELL_TYPE_BLANK) {

                                            if (sheet.getRow(linea).getCell(2) != null && sheet.getRow(linea).getCell(2).getCellType() != Cell.CELL_TYPE_BLANK) {

                                                if (sheet.getRow(linea).getCell(1).getCellType() != Cell.CELL_TYPE_STRING) {

                                                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

                                                    identificacion = new DataFormatter().formatCellValue(sheet.getRow(linea).getCell(2));

                                                    nombre = new DataFormatter().formatCellValue(sheet.getRow(linea).getCell(3));

                                                    municipio = new DataFormatter().formatCellValue(sheet.getRow(linea).getCell(4));

                                                    departamento = new DataFormatter().formatCellValue(sheet.getRow(linea).getCell(5));

                                                    cuenta = new DataFormatter().formatCellValue(sheet.getRow(linea).getCell(6));

                                                    tipo = new DataFormatter().formatCellValue(sheet.getRow(linea).getCell(7));

                                                    queryString = " Select * from finiquito";
                                                    queryString += " Where Correlativo = " + sheet.getRow(linea).getCell(0).getNumericCellValue();

                                                    rsRecords = stQuery1.executeQuery(queryString);

                                                    if (rsRecords.next()) {

                                                        repetidos.add(String.valueOf(sheet.getRow(linea).getCell(0).getNumericCellValue()));

                                                    } else {
                                                        queryString = " Insert Into finiquito (Correlativo, Fecha, Identificacion, Nombre, Municipio,";
                                                        queryString += " Departamento, Cuenta, Tipo)";
                                                        queryString += " Values ";
                                                        queryString += " (";
                                                        queryString +=  String.valueOf(sheet.getRow(linea).getCell(0).getNumericCellValue()).substring(0, String.valueOf(sheet.getRow(linea).getCell(0).getNumericCellValue()).length()-2); // dpi
                                                        queryString += ",'" + Utileria.getFechaYYYYMMDD_1(sheet.getRow(linea).getCell(1).getDateCellValue()) + "'"; // FECHA
                                                        queryString += ",'" + identificacion + "'"; // dpi
                                                        queryString += ",'" + nombre.replace("'","") + "'"; // nombre
                                                        queryString += ",'" + municipio + "'"; // municipio
                                                        queryString += ",'" + departamento + "'"; // departamento
                                                        queryString += ",'" + cuenta + "'"; // cuenta
                                                        queryString += ",'" + tipo + "'"; // tip
                                                        queryString += ")";

                                                        stQuery.executeUpdate(queryString);

                                                    }
                                                }

                                            }
                                        }
                                    } else {

                                    }

                                }

                                if (repetidos.size()>0) {

                                    final Window window = new Window("Atención! ");
                                    window.setWidth(500.0f, Unit.PIXELS);
                                    final FormLayout content = new FormLayout();

                                    final ComboBox comboRepetidosCbx = new ComboBox("Listado de Correlativos Repetidos");
                                    content.setMargin(true);

                                    for (int i = 0; i < repetidos.size(); i++) {
                                        comboRepetidosCbx.addItem(repetidos.get(i).substring(0, repetidos.get(i).length()-2));
                                    }

                                    content.addComponent(comboRepetidosCbx);
                                    content.setComponentAlignment(comboRepetidosCbx, Alignment.MIDDLE_CENTER);
                                    window.setContent(content);
                                    window.center();

                                    mainUI.getUI().getUI().addWindow(window);
                                }

                                ((UbicaloUI) mainUI).databaseProvider.getCurrentConnection().commit();
                                ((UbicaloUI) mainUI).databaseProvider.getCurrentConnection().setAutoCommit(true);

                                Notification.show("Operacion finalizada! ", Notification.Type.TRAY_NOTIFICATION);

                                finiquitosTable.removeAllItems();


                            } catch (Exception ex1) {
                                try {
                                    ((UbicaloUI) mainUI).databaseProvider.getCurrentConnection().rollback();
                                    ((UbicaloUI) mainUI).databaseProvider.getCurrentConnection().setAutoCommit(true);
                                } catch (SQLException sqlE) {
                                    //
                                }
                                Notification.show("Error al insertar registro de finiquitos en base de datos..Transaccion abortada..!", Notification.Type.ERROR_MESSAGE);
                                ex1.printStackTrace();
                                return;
                            }

                        } else {
                            Notification.show("Operación cacelada!", Notification.Type.WARNING_MESSAGE);
                        }
                    }
                });


    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        Page.getCurrent().setTitle("UBICALO - Importar finiquitos");
    }
}