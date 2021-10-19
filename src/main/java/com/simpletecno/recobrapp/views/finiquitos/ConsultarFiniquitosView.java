package com.simpletecno.recobrapp.views.finiquitos;

import com.simpletecno.recobrapp.main.RecobrAppUI;
import com.simpletecno.recobrapp.utileria.Utileria;
import com.vaadin.addon.tableexport.DefaultTableHolder;
import com.vaadin.addon.tableexport.ExcelExport;
import com.vaadin.addon.tableexport.TableHolder;
import com.vaadin.data.Property;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.server.StreamResource;
import com.vaadin.shared.Position;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.*;
import com.vaadin.ui.renderers.ButtonRenderer;
import com.vaadin.ui.renderers.ClickableRenderer;
import com.vaadin.ui.themes.ValoTheme;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.Date;
/**
 * @author user
 */
public class ConsultarFiniquitosView extends VerticalLayout implements View {

    public static final String CORRELATIVO_PROPERTY = "Correlativo";
    public static final String FECHA_PROPERTY = "Fecha";
    public static final String IDENTIFICACION_PROPERTY = "Identificación";
    public static final String NOMBRE_PROPERTY = "Nombre";
    public static final String MUNICIPIO_PROPERTY = "Municipio";
    public static final String DEPARTAMENTO_PROPERTY = "Departamento";
    public static final String CUENTA_PROPERTY = "Operación";
    public static final String TIPO_PROPERTY = "Tipo";
    public static final String IMAGEN_PROPERTY = "Documento";
    public static final String ARCHIVO_PROPERTY = "Nombre Doc";
    public static final String ARCHIVO_TIPO_PROPERTY = "Archivo Tipo";

    Grid finiquitosGrid;
    public IndexedContainer finiquitosContainer = new IndexedContainer();

    DateField inicioDt, finDt;

    Button excelBtn;

    UI mainUI;
    Statement stQuery;
    ResultSet rsRecords;

    String queryString;

    TextField documentoTxt;

    static DecimalFormat numberFormat = new DecimalFormat("#,###,##0.00");

    StreamResource logoStreamResource = null;
    String parametro1, parametro2;
    Long parametro3;
    long fileSize;
    public File file;

    static PreparedStatement stPreparedQuery;

    public ConsultarFiniquitosView() {

        this.mainUI = UI.getCurrent();
        setWidth("100%");
        setMargin(new MarginInfo(true, false, false, false));
        setSpacing(true);

        excelBtn = new Button("Excel");
        excelBtn.setIcon(FontAwesome.FILE_EXCEL_O);
        excelBtn.addStyleName(ValoTheme.BUTTON_PRIMARY);
        excelBtn.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                if (finiquitosContainer.size() > 0) {
                    exportToExcel();
                } else {
                    Notification.show("La vista no contiene registros disponibles.. ", Notification.Type.WARNING_MESSAGE);
                }
            }
        });

        Label titleLbl = new Label("CONSULTAR FINIQUITOS");
        titleLbl.addStyleName(ValoTheme.LABEL_H2);
        titleLbl.setSizeUndefined();

        HorizontalLayout titleLayout = new HorizontalLayout();
        titleLayout.setResponsive(true);
        titleLayout.setSpacing(true);
        titleLayout.setWidth("100%");
        titleLayout.setMargin(new MarginInfo(false, true, false, true));
        titleLayout.addComponents(titleLbl, excelBtn);
        titleLayout.setComponentAlignment(titleLbl, Alignment.MIDDLE_LEFT);
        titleLayout.setComponentAlignment(excelBtn, Alignment.MIDDLE_RIGHT);
        titleLayout.addStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);

        addComponent(titleLayout);
        setComponentAlignment(titleLayout, Alignment.TOP_CENTER);

        agregarHistorialPagos();

        llenarGridFiniquitos();

    }

    public void agregarHistorialPagos() {

        VerticalLayout finiquitosLayout = new VerticalLayout();
        finiquitosLayout.addStyleName("rcorners3");
        finiquitosLayout.setWidth("100%");
        finiquitosLayout.setResponsive(true);
        finiquitosLayout.setSpacing(true);
        finiquitosLayout.setMargin(false);

        HorizontalLayout filtrosLayout = new HorizontalLayout();
        filtrosLayout.setSpacing(true);
        filtrosLayout.setMargin(false);

        inicioDt = new DateField("Desde:");
        inicioDt.setDateFormat("dd/MM/yyyy");
        Date primerDia = Utileria.getPrimerDiaDelMes();
        inicioDt.setValue(primerDia);
        inicioDt.setWidth("9em");

        finDt = new DateField("Hasta:");
        finDt.setDateFormat("dd/MM/yyyy");
        Date ultimoDia = Utileria.getUltimoDiaDelMes();
        finDt.setValue(ultimoDia);
        finDt.setWidth("9em");

        documentoTxt = new TextField("Correlativo/Identif./Nombre");
        documentoTxt.addStyleName(ValoTheme.TEXTFIELD_INLINE_ICON);
        documentoTxt.setWidth("10em");
        documentoTxt.setIcon(FontAwesome.SEARCH);
        documentoTxt.setInputPrompt("Buscar");
        documentoTxt.setDescription("Escriba el correlativo, Identificación o Nombre a buscar.");
        documentoTxt.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {

                if (finiquitosGrid != null) {
                    llenarGridFiniquitos();
                }
            }
        });


        Button consultarBtn;
        consultarBtn = new Button("Consultar");
        consultarBtn.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                llenarGridFiniquitos();
//                ingresarBitacora();
            }
        });

        filtrosLayout.addComponents(inicioDt, finDt,documentoTxt, consultarBtn);
        filtrosLayout.setComponentAlignment(inicioDt, Alignment.BOTTOM_CENTER);
        filtrosLayout.setComponentAlignment(finDt, Alignment.BOTTOM_CENTER);
        filtrosLayout.setComponentAlignment(documentoTxt, Alignment.BOTTOM_CENTER);
        filtrosLayout.setComponentAlignment(consultarBtn, Alignment.BOTTOM_CENTER);

        finiquitosLayout.addComponent(filtrosLayout);
        finiquitosLayout.setComponentAlignment(filtrosLayout, Alignment.TOP_CENTER);

        finiquitosContainer.addContainerProperty(CORRELATIVO_PROPERTY, String.class, null);
        finiquitosContainer.addContainerProperty(FECHA_PROPERTY, String.class, null);
        finiquitosContainer.addContainerProperty(IDENTIFICACION_PROPERTY, String.class, null);
        finiquitosContainer.addContainerProperty(NOMBRE_PROPERTY, String.class, null);
        finiquitosContainer.addContainerProperty(MUNICIPIO_PROPERTY, String.class, null);
        finiquitosContainer.addContainerProperty(DEPARTAMENTO_PROPERTY, String.class, null);
        finiquitosContainer.addContainerProperty(CUENTA_PROPERTY, String.class, null);
        finiquitosContainer.addContainerProperty(TIPO_PROPERTY, String.class, null);
        finiquitosContainer.addContainerProperty(IMAGEN_PROPERTY, String.class, null);
        finiquitosContainer.addContainerProperty(ARCHIVO_PROPERTY, String.class, null);
        finiquitosContainer.addContainerProperty(ARCHIVO_TIPO_PROPERTY, String.class, null);

        finiquitosGrid = new Grid(finiquitosContainer);
        finiquitosGrid.setWidth("100%");
        finiquitosGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        finiquitosGrid.setHeightMode(HeightMode.ROW);
        finiquitosGrid.setHeightByRows(12);
        finiquitosGrid.setResponsive(true);

        finiquitosGrid.getColumns().forEach(col -> col.setWidthUndefined());
        finiquitosGrid.getColumn(IMAGEN_PROPERTY).setRenderer(new ButtonRenderer(e
                -> {
            if (finiquitosContainer.getContainerProperty(e.getItemId(), IMAGEN_PROPERTY).getValue().equals("Visualizar")) {
                actualizarArchivo(e);
            }else{
                String  correlativo = String.valueOf(finiquitosContainer.getContainerProperty(e.getItemId(), CORRELATIVO_PROPERTY).getValue());
                String  fechaDoc = String.valueOf(finiquitosContainer.getContainerProperty(e.getItemId(), FECHA_PROPERTY).getValue());
                String  tipoDoc = String.valueOf(finiquitosContainer.getContainerProperty(e.getItemId(), TIPO_PROPERTY).getValue());

                finiquitosGrid.select(e.getItemId());

                CargarArchivoIngresoDocumentos cargarArchivo
                        = new CargarArchivoIngresoDocumentos(e.getItemId(), correlativo, tipoDoc, fechaDoc);
                UI.getCurrent().addWindow(cargarArchivo);
                cargarArchivo.center();
            }
        }));

        finiquitosGrid.getColumn(CORRELATIVO_PROPERTY).setExpandRatio(3);
        finiquitosGrid.getColumn(FECHA_PROPERTY).setExpandRatio(3);
        finiquitosGrid.getColumn(IDENTIFICACION_PROPERTY).setExpandRatio(1);
        finiquitosGrid.getColumn(NOMBRE_PROPERTY).setExpandRatio(2);
        finiquitosGrid.getColumn(MUNICIPIO_PROPERTY).setExpandRatio(1);
        finiquitosGrid.getColumn(DEPARTAMENTO_PROPERTY).setExpandRatio(1);
        finiquitosGrid.getColumn(CUENTA_PROPERTY).setExpandRatio(1);
        finiquitosGrid.getColumn(TIPO_PROPERTY).setExpandRatio(2);
        finiquitosGrid.getColumn(IMAGEN_PROPERTY).setExpandRatio(1);
        finiquitosGrid.getColumn(ARCHIVO_PROPERTY).setExpandRatio(1);
        finiquitosGrid.getColumn(ARCHIVO_TIPO_PROPERTY).setExpandRatio(1);;

        finiquitosGrid.getColumn(ARCHIVO_PROPERTY).setHidden(true);
        finiquitosGrid.getColumn(ARCHIVO_TIPO_PROPERTY).setHidden(true);

        Grid.HeaderRow filterRow = finiquitosGrid.appendHeaderRow();

        Grid.HeaderCell cellB = filterRow.getCell(IDENTIFICACION_PROPERTY);
        TextField filterFieldB = new TextField();
        filterFieldB.addStyleName(ValoTheme.TEXTFIELD_TINY);
        filterFieldB.setInputPrompt("Filtrar");
        filterFieldB.addTextChangeListener(change -> {
            finiquitosContainer.removeContainerFilters(IDENTIFICACION_PROPERTY);
            if (!change.getText().isEmpty()) {
                finiquitosContainer.addContainerFilter(
                        new SimpleStringFilter(IDENTIFICACION_PROPERTY,
                                change.getText(), true, false));
            }
        });
        cellB.setComponent(filterFieldB);

        Grid.HeaderCell cellC = filterRow.getCell(NOMBRE_PROPERTY);
        TextField filterFieldC = new TextField();
        filterFieldC.addStyleName(ValoTheme.TEXTFIELD_TINY);
        filterFieldC.setInputPrompt("Filtrar");
        filterFieldC.addTextChangeListener(change -> {
            finiquitosContainer.removeContainerFilters(NOMBRE_PROPERTY);
            if (!change.getText().isEmpty()) {
                finiquitosContainer.addContainerFilter(
                        new SimpleStringFilter(NOMBRE_PROPERTY,
                                change.getText(), true, false));
            }
        });
        cellC.setComponent(filterFieldC);

        Grid.HeaderCell cellD = filterRow.getCell(MUNICIPIO_PROPERTY);
        TextField filterFieldD = new TextField();
        filterFieldD.addStyleName(ValoTheme.TEXTFIELD_TINY);
        filterFieldD.setInputPrompt("Filtrar");
        filterFieldD.addTextChangeListener(change -> {
            finiquitosContainer.removeContainerFilters(MUNICIPIO_PROPERTY);
            if (!change.getText().isEmpty()) {
                finiquitosContainer.addContainerFilter(
                        new SimpleStringFilter(MUNICIPIO_PROPERTY,
                                change.getText(), true, false));
            }
        });
        cellD.setComponent(filterFieldD);

        Grid.HeaderCell cellE = filterRow.getCell(DEPARTAMENTO_PROPERTY);
        TextField filterFieldE = new TextField();
        filterFieldE.addStyleName(ValoTheme.TEXTFIELD_TINY);
        filterFieldE.setInputPrompt("Filtrar");
        filterFieldE.addTextChangeListener(change -> {
            finiquitosContainer.removeContainerFilters(DEPARTAMENTO_PROPERTY);
            if (!change.getText().isEmpty()) {
                finiquitosContainer.addContainerFilter(
                        new SimpleStringFilter(DEPARTAMENTO_PROPERTY,
                                change.getText(), true, false));
            }
        });
        cellE.setComponent(filterFieldE);

        Grid.HeaderCell cellF = filterRow.getCell(CUENTA_PROPERTY);
        TextField filterFieldF = new TextField();
        filterFieldF.addStyleName(ValoTheme.TEXTFIELD_TINY);
        filterFieldF.setInputPrompt("Filtrar");
        filterFieldF.addTextChangeListener(change -> {
            finiquitosContainer.removeContainerFilters(CUENTA_PROPERTY);
            if (!change.getText().isEmpty()) {
                finiquitosContainer.addContainerFilter(
                        new SimpleStringFilter(CUENTA_PROPERTY  ,
                                change.getText(), true, false));
            }
        });
        cellF.setComponent(filterFieldF);

        Grid.HeaderCell cellG = filterRow.getCell(TIPO_PROPERTY);
        TextField filterFieldG = new TextField();
        filterFieldG.addStyleName(ValoTheme.TEXTFIELD_TINY);
        filterFieldG.setInputPrompt("Filtrar");
        filterFieldG.addTextChangeListener(change -> {
            finiquitosContainer.removeContainerFilters(TIPO_PROPERTY);
            if (!change.getText().isEmpty()) {
                finiquitosContainer.addContainerFilter(
                        new SimpleStringFilter(TIPO_PROPERTY  ,
                                change.getText(), true, false));
            }
        });
        cellG.setComponent(filterFieldG);

        finiquitosLayout.addComponent(finiquitosGrid);
        finiquitosLayout.setComponentAlignment(finiquitosGrid, Alignment.TOP_CENTER);

        addComponent(finiquitosLayout);
    }

    public void llenarGridFiniquitos() {

        finiquitosContainer.removeAllItems();

        queryString = "Select *";
        queryString += " From finiquito";
        queryString += " Where (Fecha BETWEEN ";
        queryString += "     '" + Utileria.getFechaYYYYMMDD_1(inicioDt.getValue()) + "'";
        queryString += " AND '" + Utileria.getFechaYYYYMMDD_1(finDt.getValue()) + "')";
        if (!documentoTxt.getValue().trim().isEmpty()) {
            String documentoSerie[] = documentoTxt.getValue().split(" ");
            queryString += " AND (Correlativo = '" + documentoSerie[0] + "'" + " Or Nombre = '" + documentoSerie[0] + "'";
            queryString += " OR Identificacion = '" + documentoSerie[0] + "' OR Cuenta LIKE '" + documentoTxt.getValue().trim() + "%')";
        }
        queryString += " Order by Correlativo";

        try {
            stQuery = ((RecobrAppUI) mainUI).databaseProvider.getCurrentConnection().createStatement();
            rsRecords = stQuery.executeQuery(queryString);

            if (rsRecords.next()) { //  encontrado
                do {

                    Object itemId = finiquitosContainer.addItem();

                    finiquitosContainer.getContainerProperty(itemId, CORRELATIVO_PROPERTY).setValue(rsRecords.getString("Correlativo"));
                    finiquitosContainer.getContainerProperty(itemId, FECHA_PROPERTY).setValue(Utileria.getFechaDDMMYYYY(rsRecords.getDate("Fecha")));
                    finiquitosContainer.getContainerProperty(itemId, IDENTIFICACION_PROPERTY).setValue(rsRecords.getString("Identificacion"));
                    finiquitosContainer.getContainerProperty(itemId, NOMBRE_PROPERTY).setValue(rsRecords.getString("Nombre"));
                    finiquitosContainer.getContainerProperty(itemId, MUNICIPIO_PROPERTY).setValue(rsRecords.getString("Municipio"));
                    finiquitosContainer.getContainerProperty(itemId, DEPARTAMENTO_PROPERTY).setValue(rsRecords.getString("Departamento"));
                    finiquitosContainer.getContainerProperty(itemId, CUENTA_PROPERTY).setValue(rsRecords.getString("Cuenta"));
                    finiquitosContainer.getContainerProperty(itemId, TIPO_PROPERTY).setValue(rsRecords.getString("Tipo"));

                    if (rsRecords.getString("ArchivoTipo") == null || rsRecords.getString("ArchivoTipo").equals("Sin archivo")) {
                        finiquitosContainer.getContainerProperty(itemId, IMAGEN_PROPERTY).setValue("Sin archivo");
                    } else {
                        finiquitosContainer.getContainerProperty(itemId, IMAGEN_PROPERTY).setValue("Visualizar");
                    }
                    finiquitosContainer.getContainerProperty(itemId, ARCHIVO_PROPERTY).setValue(rsRecords.getString("ArchivoNombre"));
                    finiquitosContainer.getContainerProperty(itemId, ARCHIVO_TIPO_PROPERTY).setValue(rsRecords.getString("ArchivoTipo"));

                } while (rsRecords.next());
            }

        } catch (Exception ex) {
            System.out.println("Error al listar tabla de Finiquitos : " + ex);
            ex.printStackTrace();
        }
    }

    public boolean exportToExcel() {
        if (this.finiquitosGrid.getHeightByRows() > 0) {
            TableHolder tableHolder = new DefaultTableHolder(finiquitosGrid);
            ExcelExport excelExport = new ExcelExport(tableHolder);
            excelExport.excludeCollapsedColumns();
            excelExport.setDisplayTotals(false);
            String fileexport;
            fileexport = ("RECOBRAPP.xls").replaceAll(" ", "").replaceAll(",", "");
            excelExport.setExportFileName(fileexport);
            excelExport.export();
        }
        return true;
    }


    private void ingresarBitacora() {

        try {

            stQuery = ((RecobrAppUI) mainUI).databaseProvider.getCurrentConnection().createStatement();

            for (Object rid : finiquitosGrid.getContainerDataSource().getItemIds()) {
                if (rid == null) {
                    return;
                }
                if (finiquitosContainer.getContainerProperty(rid, CORRELATIVO_PROPERTY).getValue() == null) {
                    return;
                }

                queryString = "Insert Into usuario_bitacora (IdUsuario, Usuario, Correlativo, Identificacion, Nombre, Tipo, FechaYHora)";
                queryString += " Values (";
                queryString += ((RecobrAppUI) mainUI).sessionInformation.getStrUserId();
                queryString += ",'" + ((RecobrAppUI) mainUI).sessionInformation.getStrUserFullName() + "'";
                queryString += "," + String.valueOf(finiquitosContainer.getContainerProperty(rid, CORRELATIVO_PROPERTY).getValue());
                queryString += ",'" + String.valueOf(finiquitosContainer.getContainerProperty(rid, IDENTIFICACION_PROPERTY).getValue()) + "'";
                queryString += ",'" + String.valueOf(finiquitosContainer.getContainerProperty(rid, NOMBRE_PROPERTY).getValue()) + "'";
                queryString += ",'" + String.valueOf(finiquitosContainer.getContainerProperty(rid, TIPO_PROPERTY).getValue()) + "'";
                queryString += ",current_timestamp";
                queryString += ")";

                stQuery.executeUpdate(queryString);

            }

        } catch (Exception ex) {
            System.out.println("Error al ingresar bitacora" + ex);
            ex.printStackTrace();
        }

    }

    public void actualizarArchivo(ClickableRenderer.RendererClickEvent e) {

        Object selectedObject = e.getItemId();
        String correlativo = String.valueOf(finiquitosContainer.getContainerProperty(e.getItemId(), CORRELATIVO_PROPERTY).getValue());
        String archivoNombre = String.valueOf(finiquitosContainer.getContainerProperty(e.getItemId(), ARCHIVO_PROPERTY).getValue());
        String archivoTipo = String.valueOf(finiquitosContainer.getContainerProperty(e.getItemId(), ARCHIVO_TIPO_PROPERTY).getValue());
        String tipoDoc = String.valueOf(finiquitosContainer.getContainerProperty(e.getItemId(), TIPO_PROPERTY).getValue());
        String fechaDoc = String.valueOf(finiquitosContainer.getContainerProperty(e.getItemId(), FECHA_PROPERTY).getValue());

        finiquitosGrid.select(e.getItemId());

        System.out.println("ARCHIVO NOMBRE " + archivoNombre);

        try {

            final byte docBytes[] = Files.readAllBytes(new File(archivoNombre).toPath());

            System.out.println("file " + archivoNombre);
            final String fileName = archivoNombre;

            if (docBytes == null) {
                Notification notif = new Notification("Documento no disponible, por favor ingrese uno nuevo!",
                        Notification.Type.WARNING_MESSAGE);
                notif.setDelayMsec(1500);
                notif.setPosition(Position.MIDDLE_CENTER);
                notif.setIcon(FontAwesome.CHECK);
                notif.show(Page.getCurrent());
                return;
            }

            Window window = new Window();
            window.setResizable(true);
            window.setWidth("50%");
            window.setHeight("50%");
            window.center();

            StreamResource documentStreamResource = null;

            if (docBytes != null) {
                documentStreamResource = new StreamResource(
                        new StreamResource.StreamSource() {
                            public InputStream getStream() {
                                return new ByteArrayInputStream(docBytes);
                            }
                        }, fileName
                );
            }
            documentStreamResource.setMIMEType(archivoTipo);
            documentStreamResource.setFilename(archivoNombre);
            documentStreamResource.getStream().setParameter("Content-Disposition", "attachment; filename=" + fileName);

            if (archivoTipo.contains("pdf")) {
                window.setWidth("98%");
                window.setHeight("98%");

                VerticalLayout pdfLayout = new VerticalLayout();
                pdfLayout.setSizeFull();
                pdfLayout.setSpacing(true);

                Button cambiarDocumento = new Button("Cambiar Documento");
                cambiarDocumento.addClickListener(new Button.ClickListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent clickEvent) {
                        CargarArchivoIngresoDocumentos cargarArchivo
                                = new CargarArchivoIngresoDocumentos(e.getItemId(), correlativo, tipoDoc, fechaDoc.replaceAll("/", "-"));
                        UI.getCurrent().addWindow(cargarArchivo);
                        cargarArchivo.center();
                        window.close();
                    }
                });

                BrowserFrame browserFrame = new BrowserFrame();
                browserFrame.setSizeFull();
                browserFrame.setSource(documentStreamResource);

                pdfLayout.addComponent(browserFrame);
                pdfLayout.addComponent(cambiarDocumento);

                JavaScript.getCurrent().execute("document.getElementsByClassName('gwt-FileUpload')[0].setAttribute('accept', '.png')");
                JavaScript.getCurrent().execute("document.getElementsByClassName('gwt-FileUpload')[1].setAttribute('accept', '.jpg')");
                JavaScript.getCurrent().execute("document.getElementsByClassName('gwt-FileUpload')[2].setAttribute('accept', '.PDF')");

                window.setContent(pdfLayout);

                pdfLayout.setExpandRatio(browserFrame, 2);

            }


            UI.getCurrent().addWindow(window);
            window.center();

        } catch (Exception allEx) {
            Notification notif = new Notification("Error al intentar visualizar el archivo.!",
                    Notification.Type.WARNING_MESSAGE);
            notif.setDelayMsec(1500);
            notif.setPosition(Position.MIDDLE_CENTER);
            notif.setIcon(FontAwesome.WARNING);
            notif.show(Page.getCurrent());

            allEx.printStackTrace();
        }

    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        Page.getCurrent().setTitle("RECOBRAPP - Consulta de finiquitos");
    }
}
