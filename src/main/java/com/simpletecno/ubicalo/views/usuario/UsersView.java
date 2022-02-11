/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.simpletecno.ubicalo.views.usuario;

import com.simpletecno.ubicalo.main.UbicaloUI;
import com.simpletecno.ubicalo.utileria.Utileria;
import com.vaadin.addon.tableexport.ExcelExport;
import com.vaadin.data.Property;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.server.Sizeable;
import com.vaadin.server.StreamResource;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.vaadin.dialogs.ConfirmDialog;

import java.io.*;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author JAguirre
 */
@SuppressWarnings("serial")
public class UsersView extends VerticalLayout implements View {
    
    public Statement stQuery = null;
    public ResultSet rsRecords  = null;
    public ResultSet rsRecords1 = null;
    
    protected static final String CODIGO_PROPERTY    = "Id";
//    protected static final String EMPRESA_PROPERTY   = "Empresa";
    protected static final String USUARIO_PROPERTY   = "Usuario";
    protected static final String NOMBRE_PROPERTY    = "Nombre";
//    protected static final String EMAIL_PROPERTY     = "eMail";
    protected static final String EQUIPO_PROPERTY    = "Equipo";
    protected static final String METADIARIA_PROPERTY = "Meta Diaria";
    protected static final String PERFIL_PROPERTY    = "Perfil";
    protected static final String ESTATUS_PROPERTY   = "Estatus";
    protected static final String OPTIONS_PROPERTY = "-";

    Button newBtn;
    Button exportExcelBtn;

    TextField nombreTxt;
    public Table usersTable;
            
    final UI mainUI = UI.getCurrent();
       
    public UsersView() {
        
        setResponsive(true);
        MarginInfo marginInfo = new MarginInfo(true,true,false,true); 

        HorizontalLayout filterLayout = new HorizontalLayout();
        filterLayout.setMargin(false);
        filterLayout.setSpacing(true);
        filterLayout.addStyleName("rcorners3");
        filterLayout.setResponsive(true);
        
        addComponent(filterLayout);
        setComponentAlignment(filterLayout, Alignment.TOP_CENTER);

        Label viewCaption = new Label("Usuarios del sistema");

        viewCaption.setStyleName(ValoTheme.LABEL_H3);
        
        nombreTxt = new TextField("Nombre");
        nombreTxt.setDescription("Buscar por nombre");
        nombreTxt.setWidth("100%");
        nombreTxt.addValueChangeListener(new Property.ValueChangeListener() {
            public void valueChange(Property.ValueChangeEvent event) {
                fillReportTable();
            }
        });
        
//        nombreTxt.focus();

        filterLayout.addComponent(viewCaption);        
        filterLayout.addComponent(nombreTxt);

        createReportTable();
                            
        newBtn    = new Button("Nuevo");
        newBtn.setIcon(FontAwesome.PLUS_CIRCLE);
        newBtn.setWidth(130,Sizeable.UNITS_PIXELS);
//        newBtn.addStyleName(ValoTheme.BUTTON_BORDERLESS);
        newBtn.setDescription("Registrar nuevo cliente");
        newBtn.addListener ( new Button.ClickListener()
        {
            @Override
            public void buttonClick ( Button.ClickEvent event )
            {

            }
        });

        exportExcelBtn    = new Button("Excel");
        exportExcelBtn.setIcon(FontAwesome.FILE_EXCEL_O);
        exportExcelBtn.setWidth(120,Sizeable.UNITS_PIXELS);
        exportExcelBtn.addListener ( new Button.ClickListener()
        {
            @Override
            public void buttonClick ( Button.ClickEvent event )
            {
                if(usersTable.size() > 0) {
//                    PronetWebPayMain.getInstance().mainWindow.getWindow().showNotification("EN CONSTRUCCION!");            
                    exportToExcel();
                }
            }
        });
        
        HorizontalLayout buttonsLayout = new HorizontalLayout();
        buttonsLayout.setSpacing(true);
        buttonsLayout.setMargin(true);
        buttonsLayout.addComponent(newBtn);
        buttonsLayout.addComponent(exportExcelBtn);

        addComponent(buttonsLayout);
        setComponentAlignment(buttonsLayout, Alignment.BOTTOM_CENTER);

        fillReportTable();
    }
            
    public void createReportTable() {

        HorizontalLayout reportLayout = new HorizontalLayout();
        reportLayout.setWidth("95%");
        reportLayout.addStyleName("rcorners3");
        reportLayout.setResponsive(true);

        usersTable = new Table("Usuarios del sistema ");

        reportLayout.addComponent(usersTable);
        reportLayout.setComponentAlignment(usersTable, Alignment.MIDDLE_CENTER);

        usersTable.setWidth("100%");
        usersTable.setResponsive(true);
        usersTable.setPageLength(10);
        
        usersTable.setImmediate(true);
        usersTable.setSelectable(true);
        
        usersTable.addContainerProperty(CODIGO_PROPERTY,    String.class, null);
//        usersTable.addContainerProperty(EMPRESA_PROPERTY,   String.class, null);
        usersTable.addContainerProperty(USUARIO_PROPERTY,   String.class, null);
        usersTable.addContainerProperty(NOMBRE_PROPERTY,    String.class, null);
//        usersTable.addContainerProperty(EMAIL_PROPERTY,     String.class, null);
        usersTable.addContainerProperty(PERFIL_PROPERTY,    String.class, null);
        usersTable.addContainerProperty(ESTATUS_PROPERTY,   String.class, null);
        usersTable.addContainerProperty(EQUIPO_PROPERTY,    String.class, null);
        usersTable.addContainerProperty(METADIARIA_PROPERTY,    String.class, null);
        usersTable.addContainerProperty(OPTIONS_PROPERTY,   MenuBar.class, null);

        usersTable.setColumnAlignments(new Table.Align[] { 
                Table.Align.CENTER, Table.Align.LEFT,  Table.Align.LEFT,
                Table.Align.LEFT,   Table.Align.LEFT,  Table.Align.LEFT,
                Table.Align.RIGHT,  Table.Align.CENTER
        });

        addComponent(reportLayout);        
        setComponentAlignment(reportLayout, Alignment.MIDDLE_CENTER);
    }

    public void fillReportTable() {
        
        usersTable.removeAllItems();        
        usersTable.setFooterVisible(false);
                
        String queryString = "";
        
        queryString =  "Select Usr.*, Emp.Nombre EmpresaNombre ";
        queryString += " From  usuario Usr";
        queryString += " Inner Join empresa Emp On Emp.IdEmpresa = Usr.IdEmpresa";
        queryString += " Where Usr.IdEmpresa  > 0"; //solo para tener los And's
        if(((UbicaloUI) mainUI).sessionInformation.getStrUserProfile().compareTo("DESARROLLADOR") == 0) {
            queryString += " And Usr.IdEmpresa = " + ((UbicaloUI)mainUI).sessionInformation.getStrCompanyId();
        }
        if(nombreTxt != null) {
            if(!nombreTxt.getValue().trim().isEmpty()) {
                queryString += " And Usr.Nombre Like '%" + nombreTxt.getValue().trim() + "%'";
            }
        }
        queryString += " Order By Usr.IdEmpresa, Usr.Nombre";

//System.out.println("\n\n"+queryString);

        try {
            
            stQuery = ((UbicaloUI) mainUI).databaseProvider.getCurrentConnection().createStatement();
            rsRecords = stQuery.executeQuery (queryString);

            if(rsRecords.next()) { //  encontrado

                // Define a common menu command for all the menu items.
                MenuBar.Command mycommand = new MenuBar.Command() {
                    @Override
                    public void menuSelected(MenuBar.MenuItem selectedItem) {
                        if(usersTable.getValue() != null) {
                            MenuBar menuBar = (MenuBar)usersTable.getContainerProperty(usersTable.getValue(), OPTIONS_PROPERTY).getValue();
                            if(menuBar.getItems().get(0).getChildren().contains(selectedItem)) {
                                String msg = String.valueOf(selectedItem.getId()) + "  ";
                                msg += usersTable.getContainerProperty(usersTable.getValue(), NOMBRE_PROPERTY).getValue();
                                Notification.show(msg, Notification.Type.TRAY_NOTIFICATION);
                                
                                System.out.println("get select item" + selectedItem.getId());
                                
                                if(selectedItem.getId() == 3) { // editar
                                    
                                    UserForm userForm = new UserForm();
                                    userForm.idUsuario = Integer.valueOf(String.valueOf(usersTable.getValue()));
                                    userForm.fillUserData();
                                    userForm.nombreTxt.focus();
                                    UI.getCurrent().addWindow(userForm);

                                }                                
                                if(selectedItem.getId() == 7) { // eliminar
                                    ConfirmDialog.show(UI.getCurrent(), "Confirme:", "Está seguro de eliminar el registro?",
                                        "SI", "NO", new ConfirmDialog.Listener() {

                                        public void onClose(ConfirmDialog dialog) {
                                            if (dialog.isConfirmed()) {
                                                Notification.show("NO DISPONIBLE EN ESTA VERSION!", Notification.Type.WARNING_MESSAGE);
                                            }
                                        }
                                    });
                                }
                            }
                            else {
                                Notification.show("Por favor, seleccione el registro correspondiente.", Notification.Type.WARNING_MESSAGE);
                            }
                        }
                    }  
                };
                                
                SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                
                do {

                    MenuBar contactMenu = new MenuBar();
                    contactMenu.setCaption("Menú");
                    contactMenu.addStyleName(ValoTheme.MENUBAR_BORDERLESS);
                    contactMenu.addStyleName(ValoTheme.MENUBAR_SMALL);
                    contactMenu.addStyleName(ValoTheme.MENU_APPEAR_ON_HOVER);
                    contactMenu.setSizeUndefined();
                    contactMenu.setData(rsRecords.getInt("IdUsuario"));
                    MenuBar.MenuItem menuItem = contactMenu.addItem("", FontAwesome.EDIT, null);
                    menuItem.addItem("Editar", FontAwesome.EYE, mycommand);                                                          
                    menuItem.addSeparator();
                    menuItem.addItem("Eliminar", FontAwesome.TRASH, mycommand);
                    
                    usersTable.addItem(new Object[] {    
                        rsRecords.getString("IdUsuario"),
//                        rsRecords.getString("EmpresaNombre"),
                        rsRecords.getString("Usuario"),
                        rsRecords.getString("Nombre"),
                        rsRecords.getString("Perfil"),
                        rsRecords.getString("Estatus"),
                        rsRecords.getString("Equipo"),
                        rsRecords.getString("MetaDiaria"),
                        contactMenu
                    }, rsRecords.getInt("IdUsuario"));

                }while(rsRecords.next());

            }
            else {

                Notification.show("No ha creado ningun usuario asesor para este proyecto,  por favor ingrese un usuario asesor.", Notification.Type.WARNING_MESSAGE);
            }
        } 
        catch (Exception ex) {
            Logger.getLogger(UsersView.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Error al intentar leer registros de usuarios : " + ex.getMessage());
            Notification.show("Error al intentar leer registros usuarios..!", Notification.Type.ERROR_MESSAGE);
        }

    } 
        
    public boolean exportToExcel() {
        ExcelExport excelExport;

        excelExport = new ExcelExport(usersTable);
        excelExport.excludeCollapsedColumns();
        excelExport.setExportFileName("UBICALO_Usuarios.xls");
        
        String mainTitle = "UBICALO - USUARIOS AL: "  + new Utileria().getFechaYYYYMMDD_1(new Date());
  
        excelExport.setReportTitle(mainTitle);

        excelExport.export();
        
        return true;

    }

    /**
     * This class creates a streamresource. This class implements
     * the StreamSource interface which defines the getStream method.
     */
    public static class ShowExcelFile implements StreamResource.StreamSource {

        private final ByteArrayOutputStream os = new ByteArrayOutputStream();

        public ShowExcelFile(File fileToOpen) {
            try {           
               
                FileOutputStream fost = new FileOutputStream(fileToOpen);

            } catch (Exception e) {
                e.printStackTrace();
            }
            
        }

        @Override
        public InputStream getStream() {
            // Here we return the pdf contents as a byte-array
            return new ByteArrayInputStream(os.toByteArray());
        }    
    }
    
    void setTableTitle(String tableTitle) {
        if(usersTable != null) {
            usersTable.setCaption(tableTitle);
            usersTable.setDescription(tableTitle);
        }            
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        Page.getCurrent().setTitle("UBICALO - USUARIOS ");

    }
}