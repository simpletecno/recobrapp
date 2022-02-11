/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.simpletecno.ubicalo.views.supervisor;

import com.simpletecno.ubicalo.main.UbicaloUI;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.Runo;
import com.vaadin.ui.themes.ValoTheme;

import java.sql.ResultSet;
import java.sql.Statement;

/**
 * @author joseaguirre
 */
public class SupervisorForm extends Window {

    FormLayout supervisorForm;

    TextField usuarioTxt, nombreTxt, emailTxt, telefonoTxt, codigoEspecialTxt, metaTxt;

    ComboBox estatusCbx;

    Button exitBtn, saveBtn;

    String queryString;

    int idUsuario;

    UI mainUI;
    Statement stQuery;
    ResultSet rsRecords;

    public SupervisorForm(int idUsuario) {
        this.idUsuario = idUsuario;
        this.mainUI = UI.getCurrent();

        setResponsive(true);
        setCaption("Formulario.");
        setSizeUndefined();
        setClosable(false);

        supervisorForm = new FormLayout();
        supervisorForm.setMargin(true);
        supervisorForm.setSpacing(true);

        usuarioTxt = new TextField("Usuario :");
        usuarioTxt.setWidth("16em");
        usuarioTxt.setIcon(FontAwesome.USER);

        nombreTxt = new TextField("Nombre :");
        nombreTxt.setWidth("16em");
        nombreTxt.setIcon(FontAwesome.TEXT_HEIGHT);

        emailTxt = new TextField("Email :");
        emailTxt.setWidth("16em");
        emailTxt.setIcon(FontAwesome.ENVELOPE_O);

        telefonoTxt = new TextField("Telefono :");
        telefonoTxt.setWidth("16em");
        telefonoTxt.setIcon(FontAwesome.MOBILE);

        codigoEspecialTxt = new TextField("Codigo especial :");
        codigoEspecialTxt.setWidth("16em");
        codigoEspecialTxt.setIcon(FontAwesome.CODE);

        metaTxt = new TextField("Meta diaria :");
        metaTxt.setWidth("16em");
        metaTxt.setIcon(FontAwesome.MONEY);

        estatusCbx = new ComboBox("Estatus : ");
        estatusCbx.setWidth("16em");
        estatusCbx.setIcon(FontAwesome.FILTER);
        estatusCbx.setNewItemsAllowed(false);
        estatusCbx.setInvalidAllowed(false);
        estatusCbx.addItem("INACTIVO");
        estatusCbx.addItem("ACTIVO");
        estatusCbx.select("ACTIVO");

        exitBtn = new Button("Salir");
        exitBtn.addStyleName(ValoTheme.BUTTON_BORDERLESS);
        exitBtn.setIcon(FontAwesome.ARROW_RIGHT);
        exitBtn.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                close();
            }
        });

        saveBtn = new Button("Guardar");
        saveBtn.setIcon(FontAwesome.SAVE);
        saveBtn.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                saveUsuario();
            }
        });

        HorizontalLayout titleLayout = new HorizontalLayout();
        titleLayout.setWidth("100%");
        titleLayout.setMargin(false);

        Label titleLbl = new Label();
        titleLbl.addStyleName(Runo.LABEL_H2);
        titleLbl.setSizeUndefined();
        titleLayout.addComponent(titleLbl);
        titleLayout.setComponentAlignment(titleLbl, Alignment.TOP_LEFT);

        HorizontalLayout buttonsLayout = new HorizontalLayout();
        buttonsLayout.setSpacing(true);
        buttonsLayout.addComponent(exitBtn);
        buttonsLayout.addComponent(saveBtn);

        supervisorForm.addComponent(usuarioTxt);
        supervisorForm.addComponent(nombreTxt);
        supervisorForm.addComponent(emailTxt);
        supervisorForm.addComponent(telefonoTxt);
        supervisorForm.addComponent(codigoEspecialTxt);
        supervisorForm.addComponent(metaTxt);
        supervisorForm.addComponent(estatusCbx);
        supervisorForm.addComponent(buttonsLayout);
        supervisorForm.setComponentAlignment(buttonsLayout, Alignment.BOTTOM_CENTER);

        VerticalLayout contentLayout = new VerticalLayout();
        contentLayout.setSizeUndefined();
        contentLayout.addComponent(titleLayout);
        contentLayout.setComponentAlignment(titleLayout, Alignment.TOP_CENTER);
        contentLayout.addComponent(supervisorForm);
        contentLayout.setComponentAlignment(supervisorForm, Alignment.TOP_CENTER);

        setContent(contentLayout);

        titleLbl.setValue("Editar Registro");
        llenarSupervisor();
    }

    public void llenarSupervisor() {
        queryString = "Select * ";
        queryString += " From  usuario ";
        queryString += " Where IdUsuario = " + idUsuario;

        System.out.println("\n\n" + queryString);

        try {
            stQuery = ((UbicaloUI) mainUI).databaseProvider.getCurrentConnection().createStatement();
            rsRecords = stQuery.executeQuery(queryString);

            if (rsRecords.next()) { //  encontrado
                usuarioTxt.setValue(rsRecords.getString("Usuario"));
                nombreTxt.setValue(rsRecords.getString("Nombre"));
                emailTxt.setValue(rsRecords.getString("Email"));
                telefonoTxt.setValue(rsRecords.getString("Telefono"));
                codigoEspecialTxt.setValue(rsRecords.getString("CodigoEspecial"));
                metaTxt.setValue(rsRecords.getString("MetaDiaria"));
                estatusCbx.select(rsRecords.getString("Estatus"));
            }

        } catch (Exception ex) {
            System.out.println("Error al llenar usuario : " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void saveUsuario() {

        if (usuarioTxt.getValue().trim().isEmpty()) {
            Notification.show("Error, falta el usuario!", Notification.Type.ERROR_MESSAGE);
            usuarioTxt.focus();
            return;
        }

        if (nombreTxt.getValue().trim().isEmpty()) {
            Notification.show("Error, falta el nombre!", Notification.Type.ERROR_MESSAGE);
            nombreTxt.focus();
            return;
        }

        if (estatusCbx.getValue() == null) {
            Notification.show("Error, falta el Estatus!", Notification.Type.ERROR_MESSAGE);
            estatusCbx.focus();
            return;
        }


        try {

            queryString = "Update usuario Set ";
            queryString += " Usuario = '" + usuarioTxt.getValue() + "'";
            queryString += ",Nombre = '" + nombreTxt.getValue() + "'";
            queryString += ",Email = '" + emailTxt.getValue() + "'";
            queryString += ",Telefono  = '" + telefonoTxt.getValue() + "'";
            queryString += ",MetaDiaria = " + metaTxt.getValue();
            queryString += ",CodigoEspecial = '" + codigoEspecialTxt.getValue() + "'";
            queryString += ",Estatus = '" + estatusCbx.getValue() + "'";
            queryString += " Where IdUsuario = " + idUsuario;

            System.out.println("queryString=" + queryString);

            stQuery = ((UbicaloUI) mainUI).databaseProvider.getCurrentConnection().createStatement();
            stQuery.executeUpdate(queryString);

            Notification.show("OPERACION EXITOSA!", Notification.Type.HUMANIZED_MESSAGE);

            ((SupervisorView) (mainUI.getNavigator().getCurrentView())).llenarGridFiniquitos();
        } catch (Exception ex) {
            Notification.show("Error al hacer transaccion del usuario : " + ex.getMessage(), Notification.Type.ERROR_MESSAGE);
            ex.printStackTrace();
        }
        close();
    }
}

    