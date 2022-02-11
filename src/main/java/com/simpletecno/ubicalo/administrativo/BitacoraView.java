package com.simpletecno.ubicalo.administrativo;

import com.simpletecno.ubicalo.main.UbicaloUI;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.Page;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

import java.sql.ResultSet;
import java.sql.Statement;

public class BitacoraView extends VerticalLayout implements View {

    Grid bitacoraGrid;
    public IndexedContainer bitacoraContainer = new IndexedContainer();

    static final String USUARIO_PROPERTY = "Usuario";
    static final String CORRELATIVO_PROPERTY = "Correlativo";
    static final String IDENTIFICACION_PROPERTY = "Identificación";
    static final String NOMBRE_PROPERTY = "Nombre";
    static final String TIPO_PROPERTY = "Tipo";
    static final String HORAYFECHA_PROPERTY = "HoraYFecha";

    UI mainUI;
    Statement stQuery;
    ResultSet rsRecords;

    public BitacoraView() {
        this.mainUI = UI.getCurrent();
        setWidth("100%");
        setMargin(false);
        setSpacing(true);

        Label titleLbl = new Label("BITACORA");
        titleLbl.addStyleName(ValoTheme.LABEL_H2);
        titleLbl.setSizeUndefined();

        Label empresaLbl = new Label("UBICALO");
        empresaLbl.addStyleName(ValoTheme.LABEL_H2);
        empresaLbl.setSizeUndefined();

        HorizontalLayout titleLayout = new HorizontalLayout();
        titleLayout.setMargin(new MarginInfo(false, true, false, true));
        titleLayout.addStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
        titleLayout.setResponsive(true);
        titleLayout.setSpacing(true);
        titleLayout.setWidth("100%");
        titleLayout.addComponents(empresaLbl, titleLbl);
        titleLayout.setComponentAlignment(empresaLbl, Alignment.MIDDLE_LEFT);
        titleLayout.setComponentAlignment(titleLbl, Alignment.MIDDLE_RIGHT);

        addComponent(titleLayout);
        setComponentAlignment(titleLayout, Alignment.TOP_CENTER);

        agregarGriBitacora();

        llenarGridBitacora();
    }

    public void agregarGriBitacora() {

        VerticalLayout gridLayout = new VerticalLayout();
        gridLayout.addStyleName("rcorners3");
        gridLayout.setWidth("100%");
        gridLayout.setResponsive(true);
        gridLayout.setSpacing(true);
        gridLayout.setMargin(false);

        bitacoraContainer.addContainerProperty(USUARIO_PROPERTY, String.class, null);
        bitacoraContainer.addContainerProperty(CORRELATIVO_PROPERTY, String.class, null);
        bitacoraContainer.addContainerProperty(IDENTIFICACION_PROPERTY, String.class, null);
        bitacoraContainer.addContainerProperty(NOMBRE_PROPERTY, String.class, null);
        bitacoraContainer.addContainerProperty(TIPO_PROPERTY, String.class, null);
        bitacoraContainer.addContainerProperty(HORAYFECHA_PROPERTY, String.class, null);

        bitacoraGrid = new Grid(bitacoraContainer);
        bitacoraGrid.setWidth("100%");
        bitacoraGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        bitacoraGrid.setHeightMode(HeightMode.ROW);
        bitacoraGrid.setHeightByRows(12);
        bitacoraGrid.setResponsive(true);

        bitacoraGrid.getColumn(USUARIO_PROPERTY).setExpandRatio(2);
        bitacoraGrid.getColumn(CORRELATIVO_PROPERTY).setExpandRatio(1);
        bitacoraGrid.getColumn(IDENTIFICACION_PROPERTY).setExpandRatio(3);
        bitacoraGrid.getColumn(NOMBRE_PROPERTY).setExpandRatio(3);
        bitacoraGrid.getColumn(TIPO_PROPERTY).setExpandRatio(2);
        bitacoraGrid.getColumn(HORAYFECHA_PROPERTY).setExpandRatio(2);

        Grid.HeaderRow filterRow = bitacoraGrid.appendHeaderRow();

        Grid.HeaderCell cellF = filterRow.getCell(USUARIO_PROPERTY);
        TextField filterFieldF = new TextField();
        filterFieldF.addStyleName(ValoTheme.TEXTFIELD_TINY);
        filterFieldF.setInputPrompt("Filtrar");
        filterFieldF.setColumns(10);
        filterFieldF.addTextChangeListener(change -> {
            bitacoraContainer.removeContainerFilters(USUARIO_PROPERTY);
            if (!change.getText().isEmpty()) {
                bitacoraContainer.addContainerFilter(
                        new SimpleStringFilter(USUARIO_PROPERTY,
                                change.getText(), true, false));
            }
        });
        cellF.setComponent(filterFieldF);

        Grid.HeaderCell cellB = filterRow.getCell(NOMBRE_PROPERTY);
        TextField filterFieldB = new TextField();
        filterFieldB.addStyleName(ValoTheme.TEXTFIELD_TINY);
        filterFieldB.setInputPrompt("Filtrar");
        filterFieldB.setColumns(8);
        filterFieldB.addTextChangeListener(change -> {
            bitacoraContainer.removeContainerFilters(NOMBRE_PROPERTY);
            if (!change.getText().isEmpty()) {
                bitacoraContainer.addContainerFilter(
                        new SimpleStringFilter(NOMBRE_PROPERTY,
                                change.getText(), true, false));
            }
        });
        cellB.setComponent(filterFieldB);

        Grid.HeaderCell cellZ = filterRow.getCell(IDENTIFICACION_PROPERTY);
        TextField filterFieldZ = new TextField();
        filterFieldZ.addStyleName(ValoTheme.TEXTFIELD_TINY);
        filterFieldZ.setInputPrompt("Filtrar");
        filterFieldZ.setColumns(8);
        filterFieldZ.addTextChangeListener(change -> {
            bitacoraContainer.removeContainerFilters(IDENTIFICACION_PROPERTY);
            if (!change.getText().isEmpty()) {
                bitacoraContainer.addContainerFilter(
                        new SimpleStringFilter(IDENTIFICACION_PROPERTY,
                                change.getText(), true, false));
            }
        });
        cellZ.setComponent(filterFieldZ);

        Grid.HeaderCell cellB0 = filterRow.getCell(TIPO_PROPERTY);
        TextField filterFieldB0 = new TextField();
        filterFieldB0.addStyleName(ValoTheme.TEXTFIELD_TINY);
        filterFieldB0.setInputPrompt("Filtrar");
        filterFieldB0.setColumns(8);
        filterFieldB0.addTextChangeListener(change -> {
            bitacoraContainer.removeContainerFilters(TIPO_PROPERTY);
            if (!change.getText().isEmpty()) {
                bitacoraContainer.addContainerFilter(
                        new SimpleStringFilter(TIPO_PROPERTY,
                                change.getText(), true, false));
            }
        });
        cellB0.setComponent(filterFieldB0);

        gridLayout.addComponent(bitacoraGrid);
        gridLayout.setComponentAlignment(bitacoraGrid, Alignment.TOP_CENTER);

        addComponent(gridLayout);
    }

    public void llenarGridBitacora() {

        bitacoraContainer.removeAllItems();

        String queryString = "Select *";
        queryString += " From usuario_bitacora";
        queryString += " Order by IdUsuarioBitacora";

        try {
            stQuery = ((UbicaloUI) mainUI).databaseProvider.getCurrentConnection().createStatement();
            rsRecords = stQuery.executeQuery(queryString);

            if (rsRecords.next()) { //  encontrado
                do {
                    Object itemId = bitacoraContainer.addItem();

                    bitacoraContainer.getContainerProperty(itemId, USUARIO_PROPERTY).setValue(rsRecords.getString("Usuario"));
                    bitacoraContainer.getContainerProperty(itemId, CORRELATIVO_PROPERTY).setValue(rsRecords.getString("Correlativo"));
                    bitacoraContainer.getContainerProperty(itemId, IDENTIFICACION_PROPERTY).setValue(rsRecords.getString("Identificacion"));
                    bitacoraContainer.getContainerProperty(itemId, NOMBRE_PROPERTY).setValue(rsRecords.getString("Nombre"));
                    bitacoraContainer.getContainerProperty(itemId, TIPO_PROPERTY).setValue(rsRecords.getString("Tipo"));
                    bitacoraContainer.getContainerProperty(itemId, HORAYFECHA_PROPERTY).setValue(rsRecords.getString("FechaYHora"));

                } while (rsRecords.next());
            }

        } catch (Exception ex) {
            System.out.println("Error al listar tabla de bitacora : " + ex);
            ex.printStackTrace();
        }

    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        Page.getCurrent().setTitle("UBICALO - Bitacora");
    }

}
