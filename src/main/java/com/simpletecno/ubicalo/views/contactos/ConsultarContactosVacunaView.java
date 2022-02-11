package com.simpletecno.ubicalo.views.contactos;

import com.simpletecno.ubicalo.main.UbicaloUI;
import com.simpletecno.ubicalo.utileria.Utileria;
import com.vaadin.addon.tableexport.DefaultTableHolder;
import com.vaadin.addon.tableexport.ExcelExport;
import com.vaadin.addon.tableexport.TableHolder;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.shared.Position;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

import java.sql.ResultSet;
import java.sql.Statement;

/**
 * @author user
 */
public class ConsultarContactosVacunaView extends VerticalLayout implements View {

    public static final String CORRELATIVO_PROPERTY = "Correlativo";
    public static final String NOMBRE_PROPERTY = "Nombre";
    public static final String COMUNIDAD_LINGUISTICA_PROPERTY = "Comunidad linguistica";
    public static final String IDENTIFICACION_PROPERTY = "Identificación";
    public static final String DEPARTAMENTO_PROPERTY = "Departamento";
    public static final String DISCAPACIDAD_PROPERTY = "Discapacidad";
    public static final String ESCOLARIDAD_PROPERTY = "Escolaridad";
    public static final String FECHA_PROPERTY = "Fecha nacimiento";
    public static final String NACIONALIDAD_PROPERTY = "Nacionalidad";
    public static final String PUEBLO_PROPERTY = "Pueblo";
    public static final String SEXO_PROPERTY = "Sexo";
    public static final String TELEFONO_PROPERTY = "TELEFONO";

    Grid contactosGrid;
    public IndexedContainer contactosContainer = new IndexedContainer();

    Button excelBtn;

    UI mainUI;
    Statement stQuery;
    ResultSet rsRecords;

    String queryString;
    TextField nombreTxt;

    public ConsultarContactosVacunaView() {

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
                if (contactosContainer.size() > 0) {
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

        Label titleLbl = new Label("CONSULTAR CONTACTOS");
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

        nombreTxt = new TextField("NOMBRE O DPI:");
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

        contactosContainer.addContainerProperty(CORRELATIVO_PROPERTY, String.class, null);
        contactosContainer.addContainerProperty(NOMBRE_PROPERTY, String.class, null);
        contactosContainer.addContainerProperty(COMUNIDAD_LINGUISTICA_PROPERTY, String.class, null);
        contactosContainer.addContainerProperty(IDENTIFICACION_PROPERTY, String.class, null);
        contactosContainer.addContainerProperty(DEPARTAMENTO_PROPERTY, String.class, null);
        contactosContainer.addContainerProperty(DISCAPACIDAD_PROPERTY, String.class, null);
        contactosContainer.addContainerProperty(ESCOLARIDAD_PROPERTY, String.class, null);
        contactosContainer.addContainerProperty(FECHA_PROPERTY, String.class, null);
        contactosContainer.addContainerProperty(NACIONALIDAD_PROPERTY, String.class, null);
        contactosContainer.addContainerProperty(PUEBLO_PROPERTY, String.class, null);
        contactosContainer.addContainerProperty(SEXO_PROPERTY, String.class, null);
        contactosContainer.addContainerProperty(TELEFONO_PROPERTY, String.class, null);

        contactosGrid = new Grid(contactosContainer);
        contactosGrid.setWidth("100%");
        contactosGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        contactosGrid.setHeightMode(HeightMode.ROW);
        contactosGrid.setHeightByRows(15);
        contactosGrid.setResponsive(true);

        contactosGrid.getColumn(CORRELATIVO_PROPERTY).setExpandRatio(1);
        contactosGrid.getColumn(NOMBRE_PROPERTY).setExpandRatio(3);
        contactosGrid.getColumn(COMUNIDAD_LINGUISTICA_PROPERTY).setExpandRatio(1);
        contactosGrid.getColumn(IDENTIFICACION_PROPERTY).setExpandRatio(2);
        contactosGrid.getColumn(DEPARTAMENTO_PROPERTY).setExpandRatio(2);
        contactosGrid.getColumn(ESCOLARIDAD_PROPERTY).setExpandRatio(1);
        contactosGrid.getColumn(FECHA_PROPERTY).setExpandRatio(2);
        contactosGrid.getColumn(NACIONALIDAD_PROPERTY).setExpandRatio(2);
        contactosGrid.getColumn(PUEBLO_PROPERTY).setExpandRatio(1);
        contactosGrid.getColumn(SEXO_PROPERTY).setExpandRatio(1);
        contactosGrid.getColumn(TELEFONO_PROPERTY).setExpandRatio(3);;

        Grid.HeaderRow filterRow = contactosGrid.appendHeaderRow();

        Grid.HeaderCell cellB = filterRow.getCell(IDENTIFICACION_PROPERTY);
        TextField filterFieldB = new TextField();
        filterFieldB.addStyleName(ValoTheme.TEXTFIELD_TINY);
        filterFieldB.setInputPrompt("Filtrar");
        filterFieldB.addTextChangeListener(change -> {
            contactosContainer.removeContainerFilters(IDENTIFICACION_PROPERTY);
            if (!change.getText().isEmpty()) {
                contactosContainer.addContainerFilter(
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
            contactosContainer.removeContainerFilters(NOMBRE_PROPERTY);
            if (!change.getText().isEmpty()) {
                contactosContainer.addContainerFilter(
                        new SimpleStringFilter(NOMBRE_PROPERTY,
                                change.getText(), true, false));
            }
        });
        cellC.setComponent(filterFieldC);

        finiquitosLayout.addComponent(contactosGrid);
        finiquitosLayout.setComponentAlignment(contactosGrid, Alignment.TOP_CENTER);

        addComponent(finiquitosLayout);
    }

    public void llenarGrid(String nombre) {

        contactosContainer.removeAllItems();

        queryString = "Select *";
        queryString += " From base_vacuna";
        if (!nombre.trim().isEmpty()) {
            queryString += " WHERE Nombre Like '%" + nombre + "%'";
            queryString += " OR cui LIKE '%" + nombreTxt.getValue() + "%'";
            queryString += " Order by Nombre";
        } else {
            if (((UbicaloUI) UI.getCurrent()).sessionInformation.getStrUserProfileName().equals("ADMINISTRADOR")) {
                queryString += " Order by Nombre Limit 1000";
            }
        }

        try {
            stQuery = ((UbicaloUI) mainUI).databaseProvider.getCurrentConnection().createStatement();
            rsRecords = stQuery.executeQuery(queryString);

            if (rsRecords.next()) { //  encontrado
                do {

                    Object itemId = contactosContainer.addItem();

                    contactosContainer.getContainerProperty(itemId, CORRELATIVO_PROPERTY).setValue(rsRecords.getString("Id"));
                    contactosContainer.getContainerProperty(itemId, NOMBRE_PROPERTY).setValue(rsRecords.getString("Nombre"));
                    contactosContainer.getContainerProperty(itemId, COMUNIDAD_LINGUISTICA_PROPERTY).setValue(rsRecords.getString("comunidad_linguistica"));
                    contactosContainer.getContainerProperty(itemId, IDENTIFICACION_PROPERTY).setValue(rsRecords.getString("cui"));
                    contactosContainer.getContainerProperty(itemId, DEPARTAMENTO_PROPERTY).setValue(rsRecords.getString("Departamento"));
                    contactosContainer.getContainerProperty(itemId, DISCAPACIDAD_PROPERTY).setValue(rsRecords.getString("discapacidad"));
                    contactosContainer.getContainerProperty(itemId, ESCOLARIDAD_PROPERTY).setValue(rsRecords.getString("escolaridad"));
                    contactosContainer.getContainerProperty(itemId, FECHA_PROPERTY).setValue(Utileria.getFechaDDMMYYYY(rsRecords.getDate("Fecha_nacimiento")));
                    contactosContainer.getContainerProperty(itemId, NACIONALIDAD_PROPERTY).setValue(rsRecords.getString("nacionalidad"));
                    contactosContainer.getContainerProperty(itemId, PUEBLO_PROPERTY).setValue(rsRecords.getString("pueblo"));
                    contactosContainer.getContainerProperty(itemId, SEXO_PROPERTY).setValue(rsRecords.getString("sexo"));
                    contactosContainer.getContainerProperty(itemId, TELEFONO_PROPERTY).setValue(rsRecords.getString("telefono"));

                } while (rsRecords.next());
            }

        } catch (Exception ex) {
            System.out.println("Error al listar tabla de CONTACTOS VACUNA : " + ex);
            ex.printStackTrace();
        }
    }

    public boolean exportToExcel() {
        if (this.contactosGrid.getHeightByRows() > 0) {
            TableHolder tableHolder = new DefaultTableHolder(contactosGrid);
            ExcelExport excelExport = new ExcelExport(tableHolder);
            excelExport.excludeCollapsedColumns();
            excelExport.setDisplayTotals(false);
            String fileexport;
            fileexport = ("UBICALO_contactos_vacuna.xls").replaceAll(" ", "").replaceAll(",", "");
            excelExport.setExportFileName(fileexport);
            excelExport.export();
        }
        return true;
    }

    private void ingresarBitacora() {

        try {

            stQuery = ((UbicaloUI) mainUI).databaseProvider.getCurrentConnection().createStatement();

            for (Object rid : contactosGrid.getContainerDataSource().getItemIds()) {
                if (rid == null) {
                    return;
                }
                if (contactosContainer.getContainerProperty(rid, CORRELATIVO_PROPERTY).getValue() == null) {
                    return;
                }

                queryString = "Insert Into usuario_bitacora (IdUsuario, Usuario, Correlativo, Identificacion, Nombre, Tipo, FechaYHora)";
                queryString += " Values (";
                queryString += ((UbicaloUI) mainUI).sessionInformation.getStrUserId();
                queryString += ",'" + ((UbicaloUI) mainUI).sessionInformation.getStrUserFullName() + "'";
                queryString += "," + String.valueOf(contactosContainer.getContainerProperty(rid, CORRELATIVO_PROPERTY).getValue());
                queryString += ",'" + String.valueOf(contactosContainer.getContainerProperty(rid, IDENTIFICACION_PROPERTY).getValue()) + "'";
                queryString += ",'" + String.valueOf(contactosContainer.getContainerProperty(rid, NOMBRE_PROPERTY).getValue()) + "'";
                queryString += ",'" + String.valueOf(contactosContainer.getContainerProperty(rid, "TIPO").getValue()) + "'";
                queryString += ",current_timestamp";
                queryString += ")";

                stQuery.executeUpdate(queryString);

            }

        } catch (Exception ex) {
            System.out.println("Error al REGISTRAR BITACORA : " + ex.getMessage());
            ex.printStackTrace();
        }

    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        Page.getCurrent().setTitle("UBICALO - Consulta de Contactos Vacuna");
    }
}
