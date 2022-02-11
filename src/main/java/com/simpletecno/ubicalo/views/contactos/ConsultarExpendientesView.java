package com.simpletecno.ubicalo.views.contactos;

import com.simpletecno.ubicalo.main.UbicaloUI;
import com.vaadin.addon.tableexport.DefaultTableHolder;
import com.vaadin.addon.tableexport.ExcelExport;
import com.vaadin.addon.tableexport.TableHolder;
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

import java.sql.ResultSet;
import java.sql.Statement;

/**
 * @author user
 */
public class ConsultarExpendientesView extends VerticalLayout implements View {

    public static final String CORRELATIVO_PROPERTY = "Correlativo";
    public static final String IDENTIFICACION_PROPERTY = "Identificación";
    public static final String OPERACION_PROPERTY = "Operación";
    public static final String NOMBRE_PROPERTY = "Nombre";
    public static final String ESTADO_PROPERTY = "Estado";
    public static final String INSTITUCION_PROPERTY = "Institución";
    public static final String ALQUIMIA_PROPERTY = "Alquimia";
    public static final String DOCUMENTO_PROPERTY = "Documento";
    public static final String ARCHIVONOMBRE_PROPERTY = "Archivo";
    public static final String ARCHIVOTIPO_PROPERTY = "-";

    Grid expedientesGrid;
    public IndexedContainer expendientesContainer = new IndexedContainer();

    Button excelBtn;

    UI mainUI;
    Statement stQuery;
    ResultSet rsRecords;

    String queryString;
    TextField nombreTxt;

    public ConsultarExpendientesView() {

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
                if (expendientesContainer.size() > 0) {
                    if (!((UbicaloUI) UI.getCurrent()).sessionInformation.getStrUserProfileName().equals("ADMINISTRADOR")) {
                        Notification notif = new Notification("No tiene permisos para poder exportar a Excel.", Notification.Type.WARNING_MESSAGE);
                        notif.setDelayMsec(1500);
                        notif.setPosition(Position.MIDDLE_CENTER);
                        notif.setIcon(FontAwesome.WARNING);
                        notif.show(Page.getCurrent());
                    } else {
                        exportToExcel();
                    }
                } else {
                    Notification.show("La vista no contiene registros disponibles.. ", Notification.Type.WARNING_MESSAGE);
                }

            }
        });

        Label titleLbl = new Label("CONSULTAR EXPENDIENTES");
        titleLbl.addStyleName(ValoTheme.LABEL_H2);
        titleLbl.setSizeUndefined();

        HorizontalLayout titleLayout = new HorizontalLayout();
        titleLayout.setResponsive(true);
        titleLayout.setSpacing(true);
        titleLayout.setWidth("100%");
        titleLayout.setMargin(new MarginInfo(false, true, false, true));
        titleLayout.addComponents(titleLbl, excelBtn);
        titleLayout.setComponentAlignment(titleLbl, Alignment.MIDDLE_LEFT);
//        titleLayout.setComponentAlignment(excelBtn, Alignment.MIDDLE_RIGHT);
        titleLayout.addStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);

        addComponent(titleLayout);
        setComponentAlignment(titleLayout, Alignment.TOP_CENTER);

        agregarHistorialPagos();

        llenarGrid("");

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

        nombreTxt = new TextField("OPERACION O NOMBRE:");
        nombreTxt.setWidth("300px");

        Button consultarBtn;
        consultarBtn = new Button("Consultar");
        consultarBtn.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                llenarGrid(nombreTxt.getValue().replaceAll("\'", ""));
            }
        });

        filtrosLayout.addComponents(nombreTxt, consultarBtn);
        filtrosLayout.setComponentAlignment(consultarBtn, Alignment.BOTTOM_CENTER);

        finiquitosLayout.addComponent(filtrosLayout);
        finiquitosLayout.setComponentAlignment(filtrosLayout, Alignment.TOP_CENTER);

        expendientesContainer.addContainerProperty(CORRELATIVO_PROPERTY, String.class, null);
        expendientesContainer.addContainerProperty(OPERACION_PROPERTY, String.class, null);
        expendientesContainer.addContainerProperty(IDENTIFICACION_PROPERTY, String.class, null);
        expendientesContainer.addContainerProperty(NOMBRE_PROPERTY, String.class, null);
        expendientesContainer.addContainerProperty(ESTADO_PROPERTY, String.class, null);
        expendientesContainer.addContainerProperty(INSTITUCION_PROPERTY, String.class, null);
        expendientesContainer.addContainerProperty(ALQUIMIA_PROPERTY, String.class, null);
        expendientesContainer.addContainerProperty(DOCUMENTO_PROPERTY, String.class, null);
        expendientesContainer.addContainerProperty(ARCHIVONOMBRE_PROPERTY, String.class, null);
        expendientesContainer.addContainerProperty(ARCHIVOTIPO_PROPERTY, String.class, null);

        expedientesGrid = new Grid(expendientesContainer);
        expedientesGrid.setWidth("100%");
        expedientesGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        expedientesGrid.setHeightMode(HeightMode.ROW);
        expedientesGrid.setHeightByRows(15);
        expedientesGrid.setResponsive(true);

        expedientesGrid.getColumns().forEach(col -> col.setWidthUndefined());
        expedientesGrid.getColumn(DOCUMENTO_PROPERTY).setRenderer(new ButtonRenderer(e
                -> {
            if (expendientesContainer.getContainerProperty(e.getItemId(), DOCUMENTO_PROPERTY).getValue().equals("Visualizar")) {
                actualizarArchivo(e);
            } else {
                String correlativo = String.valueOf(expendientesContainer.getContainerProperty(e.getItemId(), CORRELATIVO_PROPERTY).getValue());
                String operacion = String.valueOf(expendientesContainer.getContainerProperty(e.getItemId(), OPERACION_PROPERTY).getValue());
                String nombre = String.valueOf(expendientesContainer.getContainerProperty(e.getItemId(), NOMBRE_PROPERTY).getValue());
                expedientesGrid.select(e.getItemId());

                CargarExpendientesForm cargarArchivo
                        = new CargarExpendientesForm(e.getItemId(), correlativo, operacion, nombre);
                UI.getCurrent().addWindow(cargarArchivo);
                cargarArchivo.center();
            }
        }));

        expedientesGrid.getColumn(ARCHIVONOMBRE_PROPERTY).setHidden(true);
        expedientesGrid.getColumn(ARCHIVOTIPO_PROPERTY).setHidden(true);

        /*
        expedientesGrid.getColumn(CORRELATIVO_PROPERTY).setExpandRatio(1);
        expedientesGrid.getColumn(NOMBRE_PROPERTY).setExpandRatio(3);
        expedientesGrid.getColumn(COMUNIDAD_LINGUISTICA_PROPERTY).setExpandRatio(1);
        expedientesGrid.getColumn(IDENTIFICACION_PROPERTY).setExpandRatio(2);
        expedientesGrid.getColumn(DEPARTAMENTO_PROPERTY).setExpandRatio(2);
        expedientesGrid.getColumn(ESCOLARIDAD_PROPERTY).setExpandRatio(1);
        expedientesGrid.getColumn(FECHA_PROPERTY).setExpandRatio(2);
        expedientesGrid.getColumn(NACIONALIDAD_PROPERTY).setExpandRatio(2);
        expedientesGrid.getColumn(PUEBLO_PROPERTY).setExpandRatio(1);
        expedientesGrid.getColumn(SEXO_PROPERTY).setExpandRatio(1);
        expedientesGrid.getColumn(TELEFONO_PROPERTY).setExpandRatio(3);;
¨*/
        Grid.HeaderRow filterRow = expedientesGrid.appendHeaderRow();

        Grid.HeaderCell cellB = filterRow.getCell(IDENTIFICACION_PROPERTY);
        TextField filterFieldB = new TextField();
        filterFieldB.addStyleName(ValoTheme.TEXTFIELD_TINY);
        filterFieldB.setInputPrompt("Filtrar");
        filterFieldB.addTextChangeListener(change -> {
            expendientesContainer.removeContainerFilters(IDENTIFICACION_PROPERTY);
            if (!change.getText().isEmpty()) {
                expendientesContainer.addContainerFilter(
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
            expendientesContainer.removeContainerFilters(NOMBRE_PROPERTY);
            if (!change.getText().isEmpty()) {
                expendientesContainer.addContainerFilter(
                        new SimpleStringFilter(NOMBRE_PROPERTY,
                                change.getText(), true, false));
            }
        });
        cellC.setComponent(filterFieldC);

        finiquitosLayout.addComponent(expedientesGrid);
        finiquitosLayout.setComponentAlignment(expedientesGrid, Alignment.TOP_CENTER);

        addComponent(finiquitosLayout);
    }

    public void llenarGrid(String nombre) {

        expendientesContainer.removeAllItems();

        queryString = "Select *";
        queryString += " From base_expedientes";
        if (!nombre.trim().isEmpty()) {
            queryString += " WHERE nombre Like '%" + nombre + "%'";
            queryString += " OR cui LIKE '%" + nombreTxt.getValue() + "%'";
            queryString += " OR operacion LIKE '%" + nombreTxt.getValue() + "%'";
            queryString += " Order by Nombre";
        } else {
            queryString += " Order by Nombre Limit 1000";
        }

        try {
            stQuery = ((UbicaloUI) mainUI).databaseProvider.getCurrentConnection().createStatement();
            rsRecords = stQuery.executeQuery(queryString);

            if (rsRecords.next()) { //  encontrado
                do {

                    Object itemId = expendientesContainer.addItem();

                    expendientesContainer.getContainerProperty(itemId, CORRELATIVO_PROPERTY).setValue(rsRecords.getString("id"));
                    expendientesContainer.getContainerProperty(itemId, OPERACION_PROPERTY).setValue(rsRecords.getString("operacion"));
                    expendientesContainer.getContainerProperty(itemId, IDENTIFICACION_PROPERTY).setValue(rsRecords.getString("cui"));
                    expendientesContainer.getContainerProperty(itemId, NOMBRE_PROPERTY).setValue(rsRecords.getString("nombre"));
                    expendientesContainer.getContainerProperty(itemId, ALQUIMIA_PROPERTY).setValue(rsRecords.getString("alquimia"));
                    expendientesContainer.getContainerProperty(itemId, INSTITUCION_PROPERTY).setValue(rsRecords.getString("institucion"));

                    if (rsRecords.getString("ArchivoTipo") == null || rsRecords.getString("ArchivoTipo").equals("Sin archivo")) {
                        expendientesContainer.getContainerProperty(itemId, DOCUMENTO_PROPERTY).setValue("Sin archivo");
                    } else {
                        expendientesContainer.getContainerProperty(itemId, DOCUMENTO_PROPERTY).setValue("Visualizar");
                    }
                    expendientesContainer.getContainerProperty(itemId, ARCHIVONOMBRE_PROPERTY).setValue(rsRecords.getString("ArchivoNombre"));
                    expendientesContainer.getContainerProperty(itemId, ARCHIVOTIPO_PROPERTY).setValue(rsRecords.getString("ArchivoTipo"));

                } while (rsRecords.next());
            }

        } catch (Exception ex) {
            System.out.println("Error al listar expendientes : " + ex);
            ex.printStackTrace();
        }
    }

    public boolean exportToExcel() {
        if (this.expedientesGrid.getHeightByRows() > 0) {
            TableHolder tableHolder = new DefaultTableHolder(expedientesGrid);
            ExcelExport excelExport = new ExcelExport(tableHolder);
            excelExport.excludeCollapsedColumns();
            excelExport.setDisplayTotals(false);
            String fileexport;
            fileexport = ("Listado_Expendientes.xls").replaceAll(" ", "").replaceAll(",", "");
            excelExport.setExportFileName(fileexport);
            excelExport.export();
        }
        return true;
    }

    private void ingresarBitacora() {

        try {

            stQuery = ((UbicaloUI) mainUI).databaseProvider.getCurrentConnection().createStatement();

            for (Object rid : expedientesGrid.getContainerDataSource().getItemIds()) {
                if (rid == null) {
                    return;
                }
                if (expendientesContainer.getContainerProperty(rid, CORRELATIVO_PROPERTY).getValue() == null) {
                    return;
                }

                queryString = "Insert Into usuario_bitacora (IdUsuario, Usuario, Correlativo, Identificacion, Nombre, Tipo, FechaYHora)";
                queryString += " Values (";
                queryString += ((UbicaloUI) mainUI).sessionInformation.getStrUserId();
                queryString += ",'" + ((UbicaloUI) mainUI).sessionInformation.getStrUserFullName() + "'";
                queryString += "," + String.valueOf(expendientesContainer.getContainerProperty(rid, CORRELATIVO_PROPERTY).getValue());
                queryString += ",'" + String.valueOf(expendientesContainer.getContainerProperty(rid, IDENTIFICACION_PROPERTY).getValue()) + "'";
                queryString += ",'" + String.valueOf(expendientesContainer.getContainerProperty(rid, NOMBRE_PROPERTY).getValue()) + "'";
                queryString += ",''";
                queryString += ",current_timestamp";
                queryString += ")";

                stQuery.executeUpdate(queryString);

            }

        } catch (Exception ex) {
            System.out.println("Error al REGISTRAR BITACORA : " + ex.getMessage());
            ex.printStackTrace();
        }

    }

    public void actualizarArchivo(ClickableRenderer.RendererClickEvent e) {

        Object selectedObject = e.getItemId();
        String correlativo = String.valueOf(expendientesContainer.getContainerProperty(e.getItemId(), CORRELATIVO_PROPERTY).getValue());
        String operacion = String.valueOf(expendientesContainer.getContainerProperty(e.getItemId(), OPERACION_PROPERTY).getValue());
        String nombre = String.valueOf(expendientesContainer.getContainerProperty(e.getItemId(), NOMBRE_PROPERTY).getValue());
        String archivoNombre = String.valueOf(expendientesContainer.getContainerProperty(e.getItemId(), ARCHIVONOMBRE_PROPERTY).getValue());
        String archivoTipo = String.valueOf(expendientesContainer.getContainerProperty(e.getItemId(), ARCHIVOTIPO_PROPERTY).getValue());

        expedientesGrid.select(e.getItemId());

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
                        CargarExpendientesForm cargarArchivo
                                = new CargarExpendientesForm(e.getItemId(), correlativo, operacion, nombre);
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
        Page.getCurrent().setTitle("UBICALO - Consulta de Expendientes");
    }
}
