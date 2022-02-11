package com.simpletecno.ubicalo.views.finiquitos;

import com.simpletecno.ubicalo.main.UbicaloUI;
import com.simpletecno.ubicalo.utileria.Utileria;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.*;
import com.vaadin.ui.Grid.HeaderCell;
import com.vaadin.ui.Grid.HeaderRow;
import com.vaadin.ui.themes.ValoTheme;
import org.vaadin.dialogs.ConfirmDialog;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

/**
 *
 * @author user
 */
public class FiniquitosView extends VerticalLayout implements View {

    static final String CORRELATIVO_PROPERTY = "Correlativo";
    static final String FECHA_PROPERTY = "Fecha";
    static final String IDENTIFICACION_PROPERTY = "Identificación";
    static final String NOMBRE_PROPERTY = "Nombre";
    static final String MUNICIPIO_PROPERTY = "Municipio";
    static final String DEPARTAMENTO_PROPERTY = "Departamento";
    static final String CUENTA_PROPERTY = "Cuenta";
    static final String TIPO_PROPERTY = "Tipo";

    public IndexedContainer finiquitosContainer = new IndexedContainer();
    Grid finiquitosGrid;

    DateField inicioDt, finDt;

    String queryString;

    UI mainUI;
    Statement stQuery;
    ResultSet rsRecords;

    public FiniquitosView() {

        this.mainUI = UI.getCurrent();
        setWidth("100%");
        setMargin(new MarginInfo(true, false, false, false));
        setSpacing(true);

        Label titleLbl = new Label("MANTENIMIENTO DE FINIQUITOS");
        titleLbl.addStyleName(ValoTheme.LABEL_H2);
        titleLbl.setSizeUndefined();

        HorizontalLayout titleLayout = new HorizontalLayout();
        titleLayout.addStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
        titleLayout.setResponsive(true);
        titleLayout.setSpacing(true);
        titleLayout.setWidth("100%");
        titleLayout.setMargin(new MarginInfo(false, true, false, true));
        titleLayout.addComponents(titleLbl);
        titleLayout.setComponentAlignment(titleLbl, Alignment.MIDDLE_LEFT);

        addComponent(titleLayout);
        setComponentAlignment(titleLayout, Alignment.TOP_CENTER);

        crearGridFiniquitos();

        llenarGridFiniquitos();
    }

    public void crearGridFiniquitos() {

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

        Button consultarBtn = new Button("Consultar");
        consultarBtn.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                llenarGridFiniquitos();
            }
        });

        filtrosLayout.addComponents(inicioDt, finDt, consultarBtn);
        filtrosLayout.setComponentAlignment(inicioDt, Alignment.BOTTOM_CENTER);
        filtrosLayout.setComponentAlignment(finDt, Alignment.BOTTOM_CENTER);
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

        finiquitosGrid = new Grid(finiquitosContainer);
        finiquitosGrid.setWidth("100%");
        finiquitosGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        finiquitosGrid.setHeightMode(HeightMode.ROW);
        finiquitosGrid.setHeightByRows(12);
        finiquitosGrid.setResponsive(true);

        finiquitosGrid.getColumn(CORRELATIVO_PROPERTY).setExpandRatio(3);
        finiquitosGrid.getColumn(FECHA_PROPERTY).setExpandRatio(3);
        finiquitosGrid.getColumn(IDENTIFICACION_PROPERTY).setExpandRatio(1);
        finiquitosGrid.getColumn(NOMBRE_PROPERTY).setExpandRatio(2);
        finiquitosGrid.getColumn(MUNICIPIO_PROPERTY).setExpandRatio(1);
        finiquitosGrid.getColumn(DEPARTAMENTO_PROPERTY).setExpandRatio(1);
        finiquitosGrid.getColumn(CUENTA_PROPERTY).setExpandRatio(1);
        finiquitosGrid.getColumn(TIPO_PROPERTY).setExpandRatio(2);

//        finiquitosGrid.getColumns().forEach(col -> col.setWidthUndefined());

        HeaderRow filterRow = finiquitosGrid.appendHeaderRow();

        HeaderCell cellB0 = filterRow.getCell(IDENTIFICACION_PROPERTY);
        TextField filterFieldB0 = new TextField();
        filterFieldB0.addStyleName(ValoTheme.TEXTFIELD_TINY);
        filterFieldB0.setInputPrompt("Filtrar");
        filterFieldB0.addTextChangeListener(change -> {
            finiquitosContainer.removeContainerFilters(IDENTIFICACION_PROPERTY);
            if (!change.getText().isEmpty()) {
                finiquitosContainer.addContainerFilter(
                        new SimpleStringFilter(IDENTIFICACION_PROPERTY,
                                change.getText(), true, false));
            }
        });
        cellB0.setComponent(filterFieldB0);

        HeaderCell cellB = filterRow.getCell(NOMBRE_PROPERTY);
        TextField filterFieldB = new TextField();
        filterFieldB.addStyleName(ValoTheme.TEXTFIELD_TINY);
        filterFieldB.setInputPrompt("Filtrar");
        filterFieldB.addTextChangeListener(change -> {
            finiquitosContainer.removeContainerFilters(NOMBRE_PROPERTY);
            if (!change.getText().isEmpty()) {
                finiquitosContainer.addContainerFilter(
                        new SimpleStringFilter(NOMBRE_PROPERTY,
                                change.getText(), true, false));
            }
        });
        cellB.setComponent(filterFieldB);

        HeaderCell cellD = filterRow.getCell(CUENTA_PROPERTY);
        TextField filterFieldD = new TextField();
        filterFieldD.addStyleName(ValoTheme.TEXTFIELD_TINY);
        filterFieldD.setInputPrompt("Filtrar");
        filterFieldD.addTextChangeListener(change -> {
            finiquitosContainer.removeContainerFilters(CUENTA_PROPERTY);
            if (!change.getText().isEmpty()) {
                finiquitosContainer.addContainerFilter(
                        new SimpleStringFilter(CUENTA_PROPERTY,
                                change.getText(), true, false));
            }
        });
        cellD.setComponent(filterFieldD);

        HeaderCell cell1 = filterRow.getCell(MUNICIPIO_PROPERTY);
        TextField filterField1 = new TextField();
        filterField1.addStyleName(ValoTheme.TEXTFIELD_TINY);
        filterField1.setInputPrompt("Filtrar");
        filterField1.addTextChangeListener(change -> {
            finiquitosContainer.removeContainerFilters(MUNICIPIO_PROPERTY);
            if (!change.getText().isEmpty()) {
                finiquitosContainer.addContainerFilter(
                        new SimpleStringFilter(MUNICIPIO_PROPERTY,
                                change.getText(), true, false));
            }
        });
        cell1.setComponent(filterField1);

        HeaderCell cell2 = filterRow.getCell(DEPARTAMENTO_PROPERTY);
        TextField filterField2 = new TextField();
        filterField2.addStyleName(ValoTheme.TEXTFIELD_TINY);
        filterField2.setInputPrompt("Filtrar");
        filterField2.addTextChangeListener(change -> {
            finiquitosContainer.removeContainerFilters(DEPARTAMENTO_PROPERTY);
            if (!change.getText().isEmpty()) {
                finiquitosContainer.addContainerFilter(
                        new SimpleStringFilter(DEPARTAMENTO_PROPERTY,
                                change.getText(), true, false));
            }
        });
        cell2.setComponent(filterField2);

        HeaderCell cell3 = filterRow.getCell(TIPO_PROPERTY);
        TextField filterField3 = new TextField();
        filterField3.addStyleName(ValoTheme.TEXTFIELD_TINY);
        filterField3.setInputPrompt("Filtrar");
        //filterField3.setColumns(8);
        filterField3.addTextChangeListener(change -> {
            finiquitosContainer.removeContainerFilters(TIPO_PROPERTY);
            if (!change.getText().isEmpty()) {
                finiquitosContainer.addContainerFilter(
                        new SimpleStringFilter(TIPO_PROPERTY,
                                change.getText(), true, false));
            }
        });
        cell3.setComponent(filterField3);

        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setSpacing(true);
        buttonLayout.setMargin(false);

        Button nuevoBtn = new Button("Nuevo");
        nuevoBtn.setStyleName(ValoTheme.BUTTON_PRIMARY);
        nuevoBtn.setIcon(FontAwesome.SAVE);
        nuevoBtn.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                FiniquitosForm finiquitosForm = new FiniquitosForm(0);
                UI.getCurrent().addWindow(finiquitosForm);
                finiquitosForm.center();
            }
        });

        Button editarBtn = new Button("Editar");
        editarBtn.setStyleName(ValoTheme.BUTTON_PRIMARY);
        editarBtn.setIcon(FontAwesome.EDIT);
        editarBtn.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                if (finiquitosGrid.getSelectedRow() != null){
                    FiniquitosForm finiquitosForm = new FiniquitosForm(
                            Integer.valueOf(String.valueOf(finiquitosContainer.getContainerProperty(finiquitosGrid.getSelectedRow(), CORRELATIVO_PROPERTY).getValue())));
                    UI.getCurrent().addWindow(finiquitosForm);
                    finiquitosForm.llenarFiniquito();
                    finiquitosForm.center();
                }else{
                    Notification.show("Por favor seleccione un registro para poder editarlo", Notification.Type.WARNING_MESSAGE);
                }
            }
        });

        Button eliminarBtn = new Button("Eliminar");
        eliminarBtn.setIcon(FontAwesome.TRASH);
        eliminarBtn.setStyleName(ValoTheme.BUTTON_PRIMARY);
        eliminarBtn.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {

                if(finiquitosGrid.getSelectedRow() == null){
                    Notification.show("Por favor seleccione un registro para poder eliminarlo", Notification.Type.WARNING_MESSAGE);
                    return;
                }
                ConfirmDialog.show(UI.getCurrent(), "Confirme:", "Está seguro de Eliminar el registro relacionado?",
                        "SI", "NO", new ConfirmDialog.Listener() {

                            public void onClose(ConfirmDialog dialog) {
                                if (dialog.isConfirmed()) {
                                    try {

                                        queryString = " DELETE FROM finiquito";
                                        queryString += " WHERE Correlativo = " + String.valueOf(finiquitosContainer.getContainerProperty(finiquitosGrid.getSelectedRow(), CORRELATIVO_PROPERTY).getValue());

                                        stQuery = ((UbicaloUI) UI.getCurrent()).databaseProvider.getCurrentConnection().createStatement();
                                        stQuery.executeUpdate(queryString);

                                        Notification.show("Registro eliminado exitosamente!.", Notification.Type.TRAY_NOTIFICATION);

                                        finiquitosContainer.removeItem(finiquitosGrid.getSelectedRow());

                                    } catch (SQLException ex) {
                                        System.out.println("Error al intentar eliminar " + ex);
                                        ex.printStackTrace();
                                    }
                                } else {
                                    Notification.show("Operación cacelada!", Notification.Type.WARNING_MESSAGE);
                                }
                            }
                        });
            }
        });

        buttonLayout.addComponents(nuevoBtn, editarBtn, eliminarBtn);

        finiquitosLayout.addComponent(finiquitosGrid);
        finiquitosLayout.setComponentAlignment(finiquitosGrid, Alignment.TOP_CENTER);
        finiquitosLayout.addComponent(buttonLayout);
        finiquitosLayout.setComponentAlignment(buttonLayout, Alignment.TOP_CENTER);

        addComponent(finiquitosLayout);
    }

    public void llenarGridFiniquitos() {

        finiquitosContainer.removeAllItems();

        queryString = " Select *";
        queryString += " From finiquito";
        queryString += " Where (Fecha BETWEEN ";
        queryString += "     '" + Utileria.getFechaYYYYMMDD_1(inicioDt.getValue()) + "'";
        queryString += " AND '" + Utileria.getFechaYYYYMMDD_1(finDt.getValue()) + "')";
        queryString += " Order by Correlativo";

        try {
            stQuery = ((UbicaloUI) mainUI).databaseProvider.getCurrentConnection().createStatement();
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

                } while (rsRecords.next());
            }

        } catch (Exception ex) {
            System.out.println("Error al listar tabla de finiquitos : " + ex);
            ex.printStackTrace();
        }

    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        Page.getCurrent().setTitle("UBICALO - Finiquitos");
    }
}
