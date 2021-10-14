/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.simpletecno.recobrapp.views.supervisor;

import com.simpletecno.recobrapp.main.RecobrAppUI;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.vaadin.dialogs.ConfirmDialog;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


@SuppressWarnings("serial")
public class SupervisorView extends VerticalLayout implements View {

    static final String ID_PROPERTY = "No";
    static final String USUARIO_PROPERTY = "Usuario";
    static final String NOMBRE_PROPERTY = "Nombre";
    static final String EMAIL_PROPERTY = "Email";
    static final String CODIGO_PROPERTY = "Codigo especial";
    static final String META_PROPERTY = "Meta diaria";
    static final String TELEFONO_PROPERTY = "Telefono";
    static final String ESTATUS_PROPERTY = "Estatus";

    public IndexedContainer supervisorContainer = new IndexedContainer();
    Grid supervisorGrid;

    String queryString;

    UI mainUI;
    Statement stQuery;
    ResultSet rsRecords;

    public SupervisorView() {
        this.mainUI = UI.getCurrent();
        setWidth("100%");
        setMargin(false);
        setSpacing(true);

        Label titleLbl = new Label("SUPERVISORES");
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

        crearGridSupervisores();

        llenarGridFiniquitos();
    }

    public void crearGridSupervisores() {

        VerticalLayout supervisoresLayout = new VerticalLayout();
        supervisoresLayout.addStyleName("rcorners3");
        supervisoresLayout.setWidth("100%");
        supervisoresLayout.setResponsive(true);
        supervisoresLayout.setSpacing(true);
        supervisoresLayout.setMargin(false);

        supervisorContainer.addContainerProperty(ID_PROPERTY, String.class, null);
        supervisorContainer.addContainerProperty(USUARIO_PROPERTY, String.class, null);
        supervisorContainer.addContainerProperty(NOMBRE_PROPERTY, String.class, null);
        supervisorContainer.addContainerProperty(EMAIL_PROPERTY, String.class, null);
        supervisorContainer.addContainerProperty(TELEFONO_PROPERTY, String.class, null);
        supervisorContainer.addContainerProperty(CODIGO_PROPERTY, String.class, null);
        supervisorContainer.addContainerProperty(META_PROPERTY, String.class, null);
        supervisorContainer.addContainerProperty(ESTATUS_PROPERTY, String.class, null);

        supervisorGrid = new Grid(supervisorContainer);
        supervisorGrid.setWidth("100%");
        supervisorGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        supervisorGrid.setHeightMode(HeightMode.ROW);
        supervisorGrid.setHeightByRows(12);
        supervisorGrid.setResponsive(true);


        Grid.HeaderRow filterRow = supervisorGrid.appendHeaderRow();

        Grid.HeaderCell cellB0 = filterRow.getCell(NOMBRE_PROPERTY);
        TextField filterFieldB0 = new TextField();
        filterFieldB0.addStyleName(ValoTheme.TEXTFIELD_TINY);
        filterFieldB0.setInputPrompt("Filtrar");
        filterFieldB0.setColumns(8);
        filterFieldB0.addTextChangeListener(change -> {
            supervisorContainer.removeContainerFilters(NOMBRE_PROPERTY);
            if (!change.getText().isEmpty()) {
                supervisorContainer.addContainerFilter(
                        new SimpleStringFilter(NOMBRE_PROPERTY,
                                change.getText(), true, false));
            }
        });
        cellB0.setComponent(filterFieldB0);

        Grid.HeaderCell cellB = filterRow.getCell(CODIGO_PROPERTY);
        TextField filterFieldB = new TextField();
        filterFieldB.addStyleName(ValoTheme.TEXTFIELD_TINY);
        filterFieldB.setInputPrompt("Filtrar");
        filterFieldB.setColumns(8);
        filterFieldB.addTextChangeListener(change -> {
            supervisorContainer.removeContainerFilters(CODIGO_PROPERTY);
            if (!change.getText().isEmpty()) {
                supervisorContainer.addContainerFilter(
                        new SimpleStringFilter(CODIGO_PROPERTY,
                                change.getText(), true, false));
            }
        });
        cellB.setComponent(filterFieldB);

        Grid.HeaderCell cellD = filterRow.getCell(USUARIO_PROPERTY);
        TextField filterFieldD = new TextField();
        filterFieldD.addStyleName(ValoTheme.TEXTFIELD_TINY);
        filterFieldD.setInputPrompt("Filtrar");
        filterFieldD.setColumns(20);
        filterFieldD.addTextChangeListener(change -> {
            supervisorContainer.removeContainerFilters(USUARIO_PROPERTY);
            if (!change.getText().isEmpty()) {
                supervisorContainer.addContainerFilter(
                        new SimpleStringFilter(USUARIO_PROPERTY,
                                change.getText(), true, false));
            }
        });
        cellD.setComponent(filterFieldD);

        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setSpacing(true);
        buttonLayout.setMargin(false);

        Button editarBtn = new Button("Editar");
        editarBtn.setStyleName(ValoTheme.BUTTON_PRIMARY);
        editarBtn.setIcon(FontAwesome.EDIT);
        editarBtn.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                if (supervisorGrid.getSelectedRow() != null){
                    SupervisorForm supervisorForm = new SupervisorForm(
                            Integer.valueOf(String.valueOf(supervisorContainer.getContainerProperty(supervisorGrid.getSelectedRow(), ID_PROPERTY).getValue())));
                    UI.getCurrent().addWindow(supervisorForm);
                    supervisorForm.llenarSupervisor();
                    supervisorForm.center();
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

                if(supervisorGrid.getSelectedRow() == null){
                    Notification.show("Por favor seleccione un registro para poder eliminarlo", Notification.Type.WARNING_MESSAGE);
                    return;
                }
                ConfirmDialog.show(UI.getCurrent(), "Confirme:", "Está seguro de Eliminar el registro relacionado?",
                        "SI", "NO", new ConfirmDialog.Listener() {

                            public void onClose(ConfirmDialog dialog) {
                                if (dialog.isConfirmed()) {
                                    try {

                                        queryString = " DELETE FROM usuario";
                                        queryString += " WHERE IdUsuario = " + String.valueOf(supervisorContainer.getContainerProperty(supervisorGrid.getSelectedRow(), ID_PROPERTY).getValue());

                                        stQuery = ((RecobrAppUI) UI.getCurrent()).databaseProvider.getCurrentConnection().createStatement();
                                        stQuery.executeUpdate(queryString);

                                        Notification.show("Registro eliminado exitosamente!.", Notification.Type.TRAY_NOTIFICATION);

                                        supervisorContainer.removeItem(supervisorGrid.getSelectedRow());

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

        buttonLayout.addComponents(editarBtn, eliminarBtn);

        supervisoresLayout.addComponent(supervisorGrid);
        supervisoresLayout.setComponentAlignment(supervisorGrid, Alignment.TOP_CENTER);

        supervisoresLayout.addComponent(buttonLayout);
        supervisoresLayout.setComponentAlignment(buttonLayout, Alignment.TOP_CENTER);

        addComponent(supervisoresLayout);
    }

    public void llenarGridFiniquitos() {

        supervisorContainer.removeAllItems();

        queryString = " Select *";
        queryString += " From usuario";
        queryString += " Where Perfil = 'SUPERVISOR'";
        queryString += " Order by IdUsuario";

        System.out.println("QUERY BUSCQUEDA" + queryString);

        try {
            stQuery = ((RecobrAppUI) mainUI).databaseProvider.getCurrentConnection().createStatement();
            rsRecords = stQuery.executeQuery(queryString);

            if (rsRecords.next()) { //  encontrado

                do {

                    Object itemId = supervisorContainer.addItem();

                    supervisorContainer.getContainerProperty(itemId, ID_PROPERTY).setValue(rsRecords.getString("IdUsuario"));
                    supervisorContainer.getContainerProperty(itemId, USUARIO_PROPERTY).setValue(rsRecords.getDate("Usuario"));
                    supervisorContainer.getContainerProperty(itemId, NOMBRE_PROPERTY).setValue(rsRecords.getString("Nombre"));
                    supervisorContainer.getContainerProperty(itemId, EMAIL_PROPERTY).setValue(rsRecords.getString("Email"));
                    supervisorContainer.getContainerProperty(itemId, TELEFONO_PROPERTY).setValue(rsRecords.getString("Telefono"));
                    supervisorContainer.getContainerProperty(itemId, CODIGO_PROPERTY).setValue(rsRecords.getString("CodigoEspecial"));
                    supervisorContainer.getContainerProperty(itemId, META_PROPERTY).setValue(rsRecords.getString("MetaDiaria"));
                    supervisorContainer.getContainerProperty(itemId, ESTATUS_PROPERTY).setValue(rsRecords.getString("Estatus"));

                } while (rsRecords.next());
            }

        } catch (Exception ex) {
            System.out.println("Error al listar tabla de SUPERVISORES : " + ex);
            ex.printStackTrace();
        }
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        Page.getCurrent().setTitle("Sopdi - Supervisores ");

    }
}