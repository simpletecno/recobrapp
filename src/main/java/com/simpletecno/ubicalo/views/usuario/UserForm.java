/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.simpletecno.ubicalo.views.usuario;

import com.simpletecno.ubicalo.main.UbicaloUI;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.Runo;
import com.vaadin.ui.themes.ValoTheme;
import org.vaadin.ui.NumberField;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author joseaguirre
 */
public class UserForm extends Window {
    
    public int idUsuario;
    
    MarginInfo  marginInfo;
        
    FormLayout userForm;
    
    Button exitBtn;
    Button saveBtn;
    
    Statement stQuery = null;
    PreparedStatement stPreparedQuery = null;
    ResultSet rsRecords = null;
        
    ComboBox companyCbx;
    TextField usuarioTxt;
    TextField nombreTxt;
    PasswordField passwordTxt;
    PasswordField password2Txt;
    TextField emailTxt;
    TextField telefonoTxt;    
    ComboBox estatusCbx;
    ComboBox perfilCbx;
    TextField equipoTxt;
    
    NumberField metaDiariaTxt;

    UI mainUI;
    
    public UserForm() {
        this.mainUI = UI.getCurrent();
        
        setResponsive(true);
        setCaption("Ficha de usuario");
        setModal(true);
               
        marginInfo = new MarginInfo(true,true,false,true);
        
        Page.getCurrent().setTitle("Ubicalo - Usuario");
        
        companyCbx = new ComboBox("Empresa :");
        fillCompanyCombo();
        
        userForm  = new FormLayout();
        userForm.setMargin(marginInfo);
        userForm.setSpacing(true);
        userForm.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);

        usuarioTxt = new TextField("Usuario :");
        usuarioTxt.setWidth("20em");
        usuarioTxt.setMaxLength(128);

        nombreTxt = new TextField("Nombre :");
        nombreTxt.setWidth("20em");
        nombreTxt.setRequired(true);
        nombreTxt.setRequiredError("POR FAVOR INGRESE EL NOMBRE DEL USUARIO");
        nombreTxt.setMaxLength(128);

        passwordTxt = new PasswordField("Contraseña :");
        passwordTxt.setWidth("8em");
        passwordTxt.setMaxLength(64);

        password2Txt = new PasswordField("Confirme :");
        password2Txt.setWidth("8em");
        password2Txt.setMaxLength(64);

        telefonoTxt = new TextField("Teléfono :");
        telefonoTxt.setWidth("8em");
        telefonoTxt.setMaxLength(16);

        emailTxt = new TextField("Email :");
        emailTxt.setWidth("15em");
        emailTxt.setMaxLength(128);
        emailTxt.setRequired(true);
        emailTxt.setRequiredError("POR FAVOR INGRESE EL EMAIL DEL USUARIO");

        perfilCbx = new ComboBox("Perfil :");
        perfilCbx.setNewItemsAllowed(false);
        perfilCbx.setInvalidAllowed(false);
        perfilCbx.addItem("ADMINISTRADOR");
        perfilCbx.addItem("SUPERVISOR");
        perfilCbx.addItem("GESTOR");
        perfilCbx.select("GESTOR");

        equipoTxt = new TextField("Equipo :");
        equipoTxt.setWidth("8em");
        equipoTxt.setMaxLength(16);

        estatusCbx = new ComboBox("Estatus : ");
        estatusCbx.setNewItemsAllowed(false);
        estatusCbx.setInvalidAllowed(false);
        estatusCbx.addItem("ACTIVO");
        estatusCbx.addItem("INACTIVO");
        estatusCbx.select("ACTIVO");
        
        metaDiariaTxt = new NumberField("Meta Diaria : ");
        metaDiariaTxt.setDecimalAllowed(true);
        metaDiariaTxt.setDecimalPrecision(2);
        metaDiariaTxt.setMinimumFractionDigits(2);
        metaDiariaTxt.setDecimalSeparator('.');
        metaDiariaTxt.setDecimalSeparatorAlwaysShown(true);
        metaDiariaTxt.setValue(0.0D);
        metaDiariaTxt.setGroupingUsed(true);
        metaDiariaTxt.setGroupingSeparator(',');
        metaDiariaTxt.setGroupingSize(3);
        metaDiariaTxt.setImmediate(true);
        //metaDiariaTxt.addStyleName("align-right");
        
        saveBtn = new Button("Guardar");
        saveBtn.setIcon(FontAwesome.SAVE); 
        saveBtn.addClickListener( new Button.ClickListener()
        {
            @Override
            public void buttonClick ( Button.ClickEvent event )
            {
                saveUsuario();
            }
        });
                
        exitBtn    = new Button("Salir");
        exitBtn.setIcon(FontAwesome.SIGN_OUT);
        exitBtn.setClickShortcut(ShortcutAction.KeyCode.ESCAPE);
        exitBtn.addClickListener( new Button.ClickListener()
        {
            @Override
            public void buttonClick ( Button.ClickEvent event )
            {
                close();
            }
        });

        HorizontalLayout buttonsLayout = new HorizontalLayout();
        buttonsLayout.setSpacing(true);
        buttonsLayout.addComponent(exitBtn);
        buttonsLayout.setComponentAlignment(exitBtn, Alignment.BOTTOM_LEFT);
        buttonsLayout.addComponent(saveBtn);
        buttonsLayout.setComponentAlignment(saveBtn, Alignment.BOTTOM_CENTER);

        userForm.addComponent(companyCbx);
        userForm.addComponent(usuarioTxt);
        userForm.addComponent(nombreTxt);
        userForm.addComponent(passwordTxt);
        userForm.addComponent(password2Txt);
        userForm.addComponent(telefonoTxt);
        userForm.addComponent(emailTxt);
        userForm.addComponent(perfilCbx);
        userForm.addComponent(equipoTxt);
        userForm.addComponent(metaDiariaTxt);
        userForm.addComponent(estatusCbx);
        userForm.addComponent(buttonsLayout);
        userForm.setComponentAlignment(buttonsLayout, Alignment.BOTTOM_CENTER);
        
        VerticalLayout  contentLayout = new VerticalLayout();
        contentLayout.setSizeUndefined();
        
        HorizontalLayout titleLayout = new HorizontalLayout();
        titleLayout.setWidth("100%");
        titleLayout.setMargin(false);

        Label titleLbl = new Label("EDITAR USUARIO");
        titleLbl.addStyleName(Runo.LABEL_H2);
        titleLbl.setSizeUndefined();

        titleLayout.addComponent(titleLbl);
        titleLayout.setComponentAlignment(titleLbl, Alignment.TOP_CENTER);
                
        contentLayout.addComponent(titleLayout);
        contentLayout.setComponentAlignment(titleLayout, Alignment.TOP_CENTER);
        contentLayout.addComponent(userForm);
        contentLayout.setComponentAlignment(userForm, Alignment.TOP_CENTER);

        setContent(contentLayout);
    }    
 
    public void fillCompanyCombo() {
        String queryString = "";
        
        queryString =  "Select * ";
        queryString += " From  empresa ";
        
         if(((UbicaloUI) mainUI).sessionInformation.getStrUserProfile().compareTo("DESARROLLADOR") == 0) {
            queryString += " Where IdEmpresa = " + ((UbicaloUI)mainUI).sessionInformation.getStrCompanyId();
        }
//System.out.println("\n\n"+queryString);

        try {
            stQuery = ((UbicaloUI) mainUI).databaseProvider.getCurrentConnection().createStatement();            
            rsRecords = stQuery.executeQuery (queryString);

            if(rsRecords.next()) { //  encontrado
                do {
                    companyCbx.addItem(rsRecords.getInt("IdEmpresa"));
                    companyCbx.setItemCaption(rsRecords.getInt("IdEmpresa"), rsRecords.getString("Nombre"));
                } while(rsRecords.next());
            }
            
        } 
        catch (Exception ex) {
            Logger.getLogger(UserForm.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Error al intentar leer registros de usuario : " + ex.getMessage());
            Notification.show("Error al intentar leer registros de usuario..!", Notification.Type.ERROR_MESSAGE);
        } 
    }
    
    public void fillUserData() {
        String queryString = "";
        
        queryString =  "Select * ";
        queryString += " From  usuario ";
        queryString += " Where IdUsuario = " + String.valueOf(idUsuario);

//System.out.println("\n\n"+queryString);

        try {
            stQuery = ((UbicaloUI) mainUI).databaseProvider.getCurrentConnection().createStatement();            
            rsRecords = stQuery.executeQuery (queryString);

            if(rsRecords.next()) { //  encontrado
                companyCbx.select(rsRecords.getInt("IdEmpresa"));
                usuarioTxt.setReadOnly(false);
                usuarioTxt.setValue(rsRecords.getString("Usuario"));
                usuarioTxt.setReadOnly(true);
                nombreTxt.setValue(rsRecords.getString("Nombre"));
                emailTxt.setValue(rsRecords.getString("Email"));
                telefonoTxt.setValue(rsRecords.getString("Telefono"));
                perfilCbx.select(rsRecords.getString("Perfil"));
                equipoTxt.setValue(rsRecords.getString("Equipo"));
                estatusCbx.select(rsRecords.getString("Estatus"));
                metaDiariaTxt.setValue(rsRecords.getDouble("MetaDiaria"));
            }
            
        } 
        catch (Exception ex) {
            Logger.getLogger(UserForm.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Error al intentar leer registros de usuario : " + ex.getMessage());
            Notification.show("Error al intentar leer registros de usuario..!", Notification.Type.ERROR_MESSAGE);
        } 
    }

    private void saveUsuario() {
        
        if(usuarioTxt.getValue().trim().isEmpty()) {
            Notification.show("Error, falta el usuario!", Notification.Type.ERROR_MESSAGE);
            usuarioTxt.focus();
            return;
        }
        if(nombreTxt.getValue().trim().isEmpty()) {
            Notification.show("Error, falta el nombre!", Notification.Type.ERROR_MESSAGE);
            nombreTxt.focus();
            return;
        }
        if(!passwordTxt.getValue().trim().isEmpty() || !password2Txt.getValue().trim().isEmpty()) {
            if(password2Txt.getValue().compareTo(passwordTxt.getValue()) != 0) {
                Notification.show("Error, las contraseñas no coinciden!", Notification.Type.ERROR_MESSAGE);
                passwordTxt.focus();
                return;
            }
        }

        String queryString;
        
        try {
            if(idUsuario == 0) {
                queryString =  "Select * ";
                queryString += " From  usuario ";
                queryString += " Where Usuario = '" + usuarioTxt.getValue() + "'";
                queryString += " And IdEmpresa = "  + ((UbicaloUI) mainUI).sessionInformation.getStrCompanyId();

//System.out.println("\n\n"+queryString);

                stQuery = ((UbicaloUI) mainUI).databaseProvider.getCurrentConnection().createStatement();            
                rsRecords = stQuery.executeQuery (queryString);

                if(rsRecords.next()) { //  encontrado
                    Notification.show("Usuario existente, por favor elija otro nombre de usuario!", Notification.TYPE_ERROR_MESSAGE.WARNING_MESSAGE);
                    usuarioTxt.focus();
                    return;
                }
                queryString =  "Insert Into usuario (Usuario, Nombre, Clave, Email, Telefono, Perfil, Equipo, Estatus, MetaDiaria, IdEmpresa)";
                queryString += " Values (";
                queryString += "'"   + usuarioTxt.getValue() + "'";
                queryString += ",'"  + nombreTxt.getValue()  + "'";

                if(((UbicaloUI) mainUI).databaseProvider.getUsedDBDataSource().equals("MYSQL")) {            
                    queryString += ",Sha1('" + passwordTxt.getValue() + "')";
                }
                else {
                    queryString += ",SUBSTRING(master.dbo.fn_varbintohexstr(HASHBYTES('SHA1', '" + passwordTxt.getValue() + "')),3,40)";
                }            

                queryString += ",'" + emailTxt.getValue()    + "'";
                queryString += ",'" + telefonoTxt.getValue() + "'";
                queryString += ",'" + String.valueOf(perfilCbx.getValue())   + "'";
                queryString += ",'" + equipoTxt.getValue() + "'";
                queryString += ",'" + String.valueOf(estatusCbx.getValue())  + "'";
                queryString += "," + String.valueOf(metaDiariaTxt.getValue());
                queryString += ","  + String.valueOf(companyCbx.getValue());
                queryString += ")";                        
            }
            else {
                queryString =  "Update usuario Set ";
                queryString += " Nombre = '"  + nombreTxt.getValue()      + "'";
                if(!passwordTxt.getValue().trim().isEmpty()) {
                    if(((UbicaloUI) mainUI).databaseProvider.getUsedDBDataSource().equals("MYSQL")) {            
                        queryString += ",Clave = Sha1('" + passwordTxt.getValue() + "')";
                    }
                    else {
                        queryString += ",Clave = SUBSTRING(master.dbo.fn_varbintohexstr(HASHBYTES('SHA1', '" + passwordTxt.getValue() + "')),3,40)";
                    }            
                }
                queryString += ",Email = '" + emailTxt.getValue()   + "'";
                queryString += ",Telefono = '" + telefonoTxt.getValue()    + "'";
                queryString += ",Perfil = '" + String.valueOf(perfilCbx.getValue())    + "'";
                queryString += ",Equipo = '" + equipoTxt.getValue() + "'";
                queryString += ",Estatus = '" + String.valueOf(estatusCbx.getValue())    + "'";
                queryString += ",MetaDiaria = " + String.valueOf(metaDiariaTxt.getValue());
                queryString += ",IdEmpresa ="  + String.valueOf(companyCbx.getValue());
                queryString += " Where IdUsuario = " + String.valueOf(idUsuario);
            }

System.out.println("queryString="+queryString);

            if(idUsuario > 0) {
                stQuery = ((UbicaloUI) mainUI).databaseProvider.getCurrentConnection().createStatement();            
                stQuery.executeUpdate(queryString);
            }
            else {
                stPreparedQuery  = ((UbicaloUI) mainUI).databaseProvider.getCurrentConnection().prepareStatement(queryString, Statement.RETURN_GENERATED_KEYS);
                stPreparedQuery.executeUpdate();
                rsRecords = stPreparedQuery.getGeneratedKeys();

                rsRecords.next();

                idUsuario = rsRecords.getInt(1);                
            }

            Notification.show("OPERACION EXITOSA!", Notification.Type.HUMANIZED_MESSAGE);

            ((UsersView)(mainUI.getNavigator().getCurrentView())).fillReportTable();            
        }
        catch(Exception ex)
        {
            Notification.show("Error al actualizar usuario : " + ex.getMessage(), Notification.Type.ERROR_MESSAGE);
            ex.printStackTrace();
        }
        close();
    }    
}