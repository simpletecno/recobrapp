package com.simpletecno.recobrapp.views.finiquitos;


import com.simpletecno.recobrapp.main.RecobrAppUI;
import com.simpletecno.recobrapp.utileria.Utileria;
import com.simpletecno.recobrapp.views.finiquitos.pdf.*;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.Runo;
import com.vaadin.ui.themes.ValoTheme;

import java.sql.ResultSet;
import java.sql.Statement;

public class FiniquitosForm extends Window {

    FormLayout finiquitoForm;

    TextField correlativoTxt, identificacionTxt, nombreTxt, municipioTxt;
    TextField departamentoTxt, creditoTxt, cuentaTxt;

    ComboBox sexoCbx, tipoCbx, bancoCbx;

    DateField fechaCancelacionDt;
    DateField fechaImpresionDt;

    Button exitBtn, saveBtn;

    String queryString;
    int correlativo;

    UI mainUI;
    Statement stQuery;
    ResultSet rsRecords;

    public FiniquitosForm(int correlativo) {

        this.correlativo = correlativo;
        this.mainUI = UI.getCurrent();
        setResponsive(true);
        setCaption("Formulario.");
        setSizeUndefined();
        setClosable(false);

        finiquitoForm = new FormLayout();
        finiquitoForm.setMargin(true);
        finiquitoForm.setSpacing(true);

        correlativoTxt = new TextField("Correlativo :");
        correlativoTxt.setReadOnly(true);
        correlativoTxt.setWidth("16em");
        correlativoTxt.setIcon(FontAwesome.SORT_NUMERIC_DESC);

        fechaCancelacionDt = new DateField("Fecha :");
        fechaCancelacionDt.setWidth("16em");
        fechaCancelacionDt.setDateFormat("dd/MM/yyyy");
        fechaCancelacionDt.setIcon(FontAwesome.TABLE);

        fechaImpresionDt = new DateField("Fecha :");
        fechaImpresionDt.setWidth("16em");
        fechaImpresionDt.setDateFormat("dd/MM/yyyy");
        fechaImpresionDt.setIcon(FontAwesome.TABLE);

        identificacionTxt = new TextField("Identificación :");
        identificacionTxt.setWidth("16em");
        identificacionTxt.addStyleName("mayusculas");
        identificacionTxt.setIcon(FontAwesome.TICKET);

        tipoCbx = new ComboBox("Tipo : ");
        tipoCbx.setWidth("16em");
        tipoCbx.setIcon(FontAwesome.FILTER);
        tipoCbx.setNewItemsAllowed(false);
        tipoCbx.setInvalidAllowed(false);
        tipoCbx.addItem("Finiquito");
        tipoCbx.addItem("Carta por falta de expediente");
        tipoCbx.addItem("Carta por exclusión por cancelación en otra Institución");
        tipoCbx.addItem("Carta por homónimo de expediente");
        tipoCbx.addItem("Carta por usurpación por investigación");
        tipoCbx.select("Finiquito");

        sexoCbx = new ComboBox("Sr. o Sra.: ");
        sexoCbx.setWidth("16em");
        sexoCbx.setIcon(FontAwesome.FILTER);
        sexoCbx.setNewItemsAllowed(false);
        sexoCbx.setInvalidAllowed(false);
        sexoCbx.addItem("Sr.");
        sexoCbx.addItem("Sra.");
        sexoCbx.select("Sr.");

        nombreTxt = new TextField("Nombre :");
        nombreTxt.setWidth("16em");
        nombreTxt.setIcon(FontAwesome.USER);

        municipioTxt = new TextField("Municipio :");
        municipioTxt.setWidth("16em");
        municipioTxt.setIcon(FontAwesome.GLOBE);

        departamentoTxt = new TextField("Departamento :");
        departamentoTxt.setWidth("16em");
        departamentoTxt.setIcon(FontAwesome.GLOBE);

        cuentaTxt = new TextField("Operacion :");
        cuentaTxt.setWidth("16em");
        //cuentaTxt.addStyleName("mayusculas");
        cuentaTxt.setIcon(FontAwesome.ALIGN_JUSTIFY);

        creditoTxt = new TextField("Credito o Tarjeta :");
        creditoTxt.setWidth("16em");
        creditoTxt.addStyleName("mayusculas");
        creditoTxt.setIcon(FontAwesome.CREDIT_CARD);

        bancoCbx = new ComboBox("Banco :");
        bancoCbx.setWidth("16em");
        bancoCbx.setIcon(FontAwesome.BUILDING);
        bancoCbx.setNewItemsAllowed(false);
        bancoCbx.setInvalidAllowed(false);
        bancoCbx.addItem("BANCO DE ANTIGUA S.A");
        bancoCbx.addItem("Banco Promerica de Guatemala S.A. ");
        bancoCbx.addItem("PROMERICA S.A.");
        bancoCbx.select("BANCO DE ANTIGUA S.A");

        tipoCbx = new ComboBox("Tipo : ");
        tipoCbx.setWidth("16em");
        tipoCbx.setIcon(FontAwesome.FILTER);
        tipoCbx.setNewItemsAllowed(false);
        tipoCbx.setInvalidAllowed(false);
        tipoCbx.addItem("Finiquito");
        tipoCbx.addItem("Carta por falta de expediente");
        tipoCbx.addItem("Carta por exclusión por cancelación en otra Institución");
        tipoCbx.addItem("Carta por homónimo de expediente");
        //tipoCbx.addItem("Carta por usurpación por investigación");
        tipoCbx.select("Finiquito");

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
                guardarFiniquito();
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

        finiquitoForm.addComponent(correlativoTxt);
        finiquitoForm.addComponent(fechaCancelacionDt);
        finiquitoForm.addComponent(identificacionTxt);
        finiquitoForm.addComponent(sexoCbx);
        finiquitoForm.addComponent(nombreTxt);
        finiquitoForm.addComponent(municipioTxt);
        finiquitoForm.addComponent(departamentoTxt);
        finiquitoForm.addComponent(cuentaTxt);
        finiquitoForm.addComponent(creditoTxt);
        finiquitoForm.addComponent(bancoCbx);
        finiquitoForm.addComponent(tipoCbx);
        finiquitoForm.addComponent(buttonsLayout);
        finiquitoForm.setComponentAlignment(buttonsLayout, Alignment.BOTTOM_CENTER);

        VerticalLayout contentLayout = new VerticalLayout();
        contentLayout.setSizeUndefined();
        contentLayout.addComponent(titleLayout);
        contentLayout.setComponentAlignment(titleLayout, Alignment.TOP_CENTER);
        contentLayout.addComponent(finiquitoForm);
        contentLayout.setComponentAlignment(finiquitoForm, Alignment.TOP_CENTER);

        setContent(contentLayout);

        if (correlativo == 0){
            titleLbl.setValue("Nuevo Registro");
            buscarCorrelativo();
        }else{
            titleLbl.setValue("Editar Registro");
            llenarFiniquito();
        }

    }

    public  void buscarCorrelativo(){

        queryString = "Select * ";
        queryString += " From  finiquito ";
        queryString += " order by Correlativo DESC";

        int nuevoCorrelativo = 0;

System.out.println("\n\n" + queryString);

        try {
            stQuery = ((RecobrAppUI) mainUI).databaseProvider.getCurrentConnection().createStatement();
            rsRecords = stQuery.executeQuery(queryString);

            correlativoTxt.setReadOnly(false);

            if (rsRecords.next()) { // Si encuentra el registro asignar correlativo nuevo

                   nuevoCorrelativo = rsRecords.getInt("Correlativo") + 1;
                   correlativoTxt.setValue(String.valueOf(nuevoCorrelativo));

            }else { // Si no encuentra datos, es el primer registro correlativo = 1
                correlativoTxt.setValue("1");
            }
            correlativoTxt.setReadOnly(true);

        } catch (Exception ex) {
            System.out.println("Error al buscar correlativo : " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void llenarFiniquito() {
        queryString = "Select * ";
        queryString += " From  finiquito ";
        queryString += " Where Correlativo = " + correlativo;

System.out.println("\n\n" + queryString);

        try {
            stQuery = ((RecobrAppUI) mainUI).databaseProvider.getCurrentConnection().createStatement();
            rsRecords = stQuery.executeQuery(queryString);

            if (rsRecords.next()) { //  encontrado
                correlativoTxt.setReadOnly(false);
                correlativoTxt.setValue(rsRecords.getString("Correlativo"));
                correlativoTxt.setReadOnly(true);
                fechaCancelacionDt.setValue(rsRecords.getDate("Fecha"));
                identificacionTxt.setValue(rsRecords.getString("Identificacion"));
                nombreTxt.setValue(rsRecords.getString("Nombre"));
                municipioTxt.setValue(rsRecords.getString("Municipio"));
                departamentoTxt.setValue(rsRecords.getString("Departamento"));
                cuentaTxt.setValue(rsRecords.getString("Cuenta"));
                tipoCbx.select(rsRecords.getString("Tipo"));
                sexoCbx.select(rsRecords.getString("Sexo"));
                creditoTxt.setValue(rsRecords.getString("Credito"));
                bancoCbx.select(rsRecords.getString("Banco"));
            }

        } catch (Exception ex) {
            System.out.println("Error al llenar finiquito : " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void guardarFiniquito() {

        if (identificacionTxt.getValue().trim().isEmpty()) {
            Notification.show("Error, falta la Identificación!", Notification.Type.ERROR_MESSAGE);
            identificacionTxt.focus();
            return;
        }

        if (nombreTxt.getValue().trim().isEmpty()) {
            Notification.show("Error, falta el nombre!", Notification.Type.ERROR_MESSAGE);
            nombreTxt.focus();
            return;
        }

        if (cuentaTxt.getValue().trim().isEmpty()) {
            Notification.show("Error, falta la cuenta!", Notification.Type.ERROR_MESSAGE);
            cuentaTxt.focus();
            return;
        }

        if (tipoCbx.getValue() == null) {
            Notification.show("Error, falta el tipo!", Notification.Type.ERROR_MESSAGE);
            tipoCbx.focus();
            return;
        }

        try {

            if (correlativo == 0) {

                queryString = "Insert Into finiquito (Fecha, Identificacion, Nombre, Municipio, Departamento, Cuenta, Tipo, " +
                        "Banco, Credito, Sexo)";
                queryString += " Values (";
                queryString += "'" + Utileria.getFechaYYYYMMDD_1(fechaCancelacionDt.getValue()) + "'";
                queryString += ",'" + identificacionTxt.getValue() + "'";
                queryString += ",'" + nombreTxt.getValue() + "'";
                queryString += ",'" + municipioTxt.getValue() + "'";
                queryString += ",'" + departamentoTxt.getValue() + "'";
                queryString += ",'" + cuentaTxt.getValue() + "'";
                queryString += ",'" + tipoCbx.getValue() + "'";
                queryString += ",'" + bancoCbx.getValue() + "'";
                queryString += ",'" + creditoTxt.getValue() + "'";
                queryString += ",'" + sexoCbx.getValue() + "'";
                queryString += ")";
            } else {
                queryString = "Update finiquito Set ";
                queryString += " Fecha = '" + Utileria.getFechaYYYYMMDD_1(fechaCancelacionDt.getValue()) + "'";
                queryString += ",Identificacion = '" + identificacionTxt.getValue() +"'";
                queryString += ",Nombre = '" + nombreTxt.getValue() + "'";
                queryString += ",Municipio = '" + municipioTxt.getValue() + "'";
                queryString += ",Departamento = '" + departamentoTxt.getValue() + "'";
                queryString += ",Cuenta = '" + cuentaTxt.getValue() + "'";
                queryString += ",Tipo = '" + tipoCbx.getValue() + "'";
                queryString += ",Banco = '" + bancoCbx.getValue() + "'";
                queryString += ",Sexo = '" + sexoCbx.getValue() + "'";
                queryString += ",Credito = '" + creditoTxt.getValue() + "'";
                queryString += " Where Correlativo = " + correlativo;
            }

System.out.println("queryString=" + queryString);

            stQuery = ((RecobrAppUI) mainUI).databaseProvider.getCurrentConnection().createStatement();
            stQuery.executeUpdate(queryString);


            if(tipoCbx.getValue() == "Finiquito"){
                System.out.println("\nFiniquito \n");
                NuevaBaseFiniquitosPDF nuevaBaseFiniquitosPDF = new NuevaBaseFiniquitosPDF(correlativoTxt.getValue());
                mainUI.addWindow(nuevaBaseFiniquitosPDF);
                nuevaBaseFiniquitosPDF.center();

            }else if (tipoCbx.getValue().equals("Carta por falta de expediente")){
                System.out.println("\nCARTA POR FALTA DE EXPENDIENTE \n");
                CartaFaltaExpendientePDF cartaFaltaExpendientePDF = new CartaFaltaExpendientePDF(correlativoTxt.getValue());
                mainUI.addWindow(cartaFaltaExpendientePDF);
                cartaFaltaExpendientePDF.center();
            }else if(tipoCbx.getValue().equals("Carta por exclusión por cancelación en otra Institución")){
                System.out.println("\nCARTA POR EXCLUSION POR CANCELACION EN OTRA INSTITUCION \n");
                CartaExclusionCancelacionOtraInstitucionPDF cartaExclusionPDF = new CartaExclusionCancelacionOtraInstitucionPDF(correlativoTxt.getValue());
                mainUI.addWindow(cartaExclusionPDF);
                cartaExclusionPDF.center();
            }else if(tipoCbx.getValue().equals("Carta por homónimo de expediente")){
                System.out.println("\nCARTA POR HOMONIO DE EXPENDIENTE \n");
                CartaPorHononimoPDF cartaHomonioPDF = new CartaPorHononimoPDF(correlativoTxt.getValue());
                mainUI.addWindow(cartaHomonioPDF);
                cartaHomonioPDF.center();
            }

            Notification.show("OPERACION EXITOSA!", Notification.Type.HUMANIZED_MESSAGE);

            ((FiniquitosView) (mainUI.getNavigator().getCurrentView())).llenarGridFiniquitos();

        } catch (Exception ex) {
            Notification.show("Error al hacer transaccion de finiquito : " + ex.getMessage(), Notification.Type.ERROR_MESSAGE);
            ex.printStackTrace();
        }
        close();
    }
}