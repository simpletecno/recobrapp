package com.simpletecno.ubicalo.views.reporto;

import com.simpletecno.ubicalo.main.UbicaloUI;
import com.simpletecno.ubicalo.utileria.Utileria;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.*;
import org.vaadin.ui.NumberField;

import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.Date;

public class ReportoMetaView extends VerticalLayout implements View {

    static final String FECHA_PROPERTY = "Fecha";
    static final String HORA_PROPERTY = "Hora";
    static final String CODIGO_PROPERTY = "Equipo";
    static final String ACUMULADO_PROPERTY = "Acumulado";
    static final String SEGURO_PROPERTY = "Seguro";
    static final String PROBABLE_PROPERTY = "Probable";
    static final String COMENTARIO_PROPERTY = "Comentario";
    public IndexedContainer avenceContainer = new IndexedContainer();

    Grid avenceGrid;
    String queryString;
    UI mainUI = UI.getCurrent();
    Statement stQuery;
    ResultSet rsRecords;
    NumberField alMomentoTxt;
    NumberField segurosTxt;
    NumberField probableTxt;
    TextArea comentarioArea;
    TextField comentarioTxt;

    static DecimalFormat numberFormat = new DecimalFormat("#,###,##0.00");

    public ReportoMetaView() {
        setWidth("100%");
        setMargin(new MarginInfo(true, false, false, false ));
        setSpacing(true);

        Label titleLbl = new Label(((UbicaloUI)this.mainUI).sessionInformation.getStrUserFullName());
        titleLbl.addStyleName("h2");
        titleLbl.setSizeUndefined();

        comentarioArea = new TextArea();
        comentarioArea.setWordwrap(true);
        comentarioArea.setWidth("100%");
        comentarioArea.setStyleName("huge");

        llenarComentarioAdminsitrador();

        HorizontalLayout titleLayout = new HorizontalLayout();
        titleLayout.addStyleName("v-component-group");
        titleLayout.setResponsive(true);
        titleLayout.setSpacing(true);
        titleLayout.setWidth("100%");
        titleLayout.setMargin(new MarginInfo(false, true,false, true));
        titleLayout.addComponents(new Component[]{titleLbl, this.comentarioArea});
        titleLayout.setComponentAlignment(titleLbl, Alignment.MIDDLE_LEFT);
        titleLayout.setComponentAlignment(this.comentarioArea, Alignment.TOP_CENTER);

        addComponent(titleLayout);
        setComponentAlignment(titleLayout, Alignment.TOP_CENTER);
        crearGridAvanceMeta();
        llenarGridAvanceMeta();
    }

    public void llenarComentarioAdminsitrador() {
        queryString = " SELECT comentario.*, usuario.Nombre ";
        queryString += " FROM comentario";
        queryString += " INNER JOIN usuario ON usuario.IdUsuario = comentario.CreadoUsuario";
        queryString += " WHERE comentario.IdUsuario = " + ((UbicaloUI)this.mainUI).sessionInformation.getStrUserId();
        queryString += " ORDER BY comentario.IdComentario Desc";

        try {
            stQuery = ((UbicaloUI)this.mainUI).databaseProvider.getCurrentConnection().createStatement();
            rsRecords = stQuery.executeQuery(queryString);
            if (rsRecords.next()) {
                comentarioArea.setReadOnly(false);
                comentarioArea.setValue(rsRecords.getString("Comentario") + ".  Creado por : " + rsRecords.getString("Nombre") + ". Fecha : " + Utileria.getFechaDDMMYYYY_HHMM_2(rsRecords.getTimestamp("CreadoFechaYHora")));
                comentarioArea.setReadOnly(true);
            } else {
                comentarioArea.setVisible(false);
            }
        } catch (Exception var2) {
            System.out.println("Error al llenar comentariotxt : " + var2);
            var2.printStackTrace();
        }

    }

    public void crearGridAvanceMeta() {

        VerticalLayout gridLayout = new VerticalLayout();
        gridLayout.addStyleName("rcorners3");
        gridLayout.setWidth("100%");
        gridLayout.setResponsive(true);
        gridLayout.setSpacing(true);
        gridLayout.setMargin(false);

        avenceContainer.addContainerProperty(FECHA_PROPERTY, String.class, null);
        avenceContainer.addContainerProperty(HORA_PROPERTY, String.class, null);
        avenceContainer.addContainerProperty(CODIGO_PROPERTY, String.class, null);
        avenceContainer.addContainerProperty(ACUMULADO_PROPERTY, String.class, null);
        avenceContainer.addContainerProperty(SEGURO_PROPERTY, String.class, null);
        avenceContainer.addContainerProperty(PROBABLE_PROPERTY, String.class, null);
        avenceContainer.addContainerProperty(CODIGO_PROPERTY, String.class, null);
        avenceContainer.addContainerProperty(COMENTARIO_PROPERTY, String.class, null);

        avenceGrid = new Grid("Bitácora de hoy " + Utileria.getFechaDDMMYYYY(new Date()), avenceContainer);
        avenceGrid.setWidth("100%");
        avenceGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        avenceGrid.setHeightMode(HeightMode.ROW);
        avenceGrid.setHeightByRows(11);
        avenceGrid.setResponsive(true);
        
        avenceGrid.getColumn(FECHA_PROPERTY).setExpandRatio(1);
        avenceGrid.getColumn(HORA_PROPERTY).setExpandRatio(1);
        avenceGrid.getColumn(CODIGO_PROPERTY).setExpandRatio(1);
        avenceGrid.getColumn(ACUMULADO_PROPERTY).setExpandRatio(1);
        avenceGrid.getColumn(SEGURO_PROPERTY).setExpandRatio(1);
        avenceGrid.getColumn(PROBABLE_PROPERTY).setExpandRatio(1);
        avenceGrid.getColumn(COMENTARIO_PROPERTY).setExpandRatio(1);

        HorizontalLayout camposLayout = new HorizontalLayout();
        camposLayout.setSpacing(true);
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setSpacing(true);

        alMomentoTxt = new NumberField("Al momento : ");
        alMomentoTxt.setDecimalAllowed(true);
        alMomentoTxt.setDecimalPrecision(2);
        alMomentoTxt.setMinimumFractionDigits(2);
        alMomentoTxt.setDecimalSeparator('.');
        alMomentoTxt.setDecimalSeparatorAlwaysShown(true);
        alMomentoTxt.setValue(0.0D);
        alMomentoTxt.setGroupingUsed(true);
        alMomentoTxt.setGroupingSeparator(',');
        alMomentoTxt.setGroupingSize(3);
        alMomentoTxt.setImmediate(true);
        alMomentoTxt.addStyleName("align-right");

        segurosTxt = new NumberField("Seguros : ");
        segurosTxt.setDecimalAllowed(true);
        segurosTxt.setDecimalPrecision(2);
        segurosTxt.setMinimumFractionDigits(2);
        segurosTxt.setDecimalSeparator('.');
        segurosTxt.setDecimalSeparatorAlwaysShown(true);
        segurosTxt.setValue(0.0D);
        segurosTxt.setGroupingUsed(true);
        segurosTxt.setGroupingSeparator(',');
        segurosTxt.setGroupingSize(3);
        segurosTxt.setImmediate(true);
        segurosTxt.addStyleName("align-right");

        probableTxt = new NumberField("Probable : ");
        probableTxt.setDecimalAllowed(true);
        probableTxt.setDecimalPrecision(2);
        probableTxt.setMinimumFractionDigits(2);
        probableTxt.setDecimalSeparator('.');
        probableTxt.setDecimalSeparatorAlwaysShown(true);
        probableTxt.setValue(0.0D);
        probableTxt.setGroupingUsed(true);
        probableTxt.setGroupingSeparator(',');
        probableTxt.setGroupingSize(3);
        probableTxt.setImmediate(true);
        probableTxt.addStyleName("align-right");

        comentarioTxt = new TextField("Comentario : ");
        comentarioTxt.setWidth("25em");
        comentarioTxt.setValue("");

        Button reportarBtn = new Button("Reportar");
        reportarBtn.setStyleName("primary");
        reportarBtn.setIcon(FontAwesome.CHECK);
        reportarBtn.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                insertReporto();
            }
        });

        camposLayout.addComponents(alMomentoTxt, segurosTxt, probableTxt, comentarioTxt);
        
        buttonLayout.addComponents(camposLayout, reportarBtn);
        buttonLayout.setComponentAlignment(camposLayout, Alignment.BOTTOM_LEFT);
        buttonLayout.setComponentAlignment(reportarBtn, Alignment.BOTTOM_RIGHT);
        
        gridLayout.addComponents(avenceGrid, buttonLayout);
        gridLayout.setComponentAlignment(avenceGrid, Alignment.TOP_CENTER);
        gridLayout.setComponentAlignment(buttonLayout, Alignment.TOP_CENTER);
        
        addComponent(gridLayout);
    }

    public void llenarGridAvanceMeta() {
        avenceContainer.removeAllItems();

        queryString = " Select rep.*, usr.*";
        queryString = queryString + " From reporto rep";
        queryString = queryString + " Inner join usuario usr on usr.IdUsuario = rep.IdUsuario";
        queryString = queryString + " where DATE_FORMAT(rep.FechaYHora,'%d/%m/%Y') = '" + Utileria.getFechaDDMMYYYY(new Date()) + "'";
        queryString = queryString + " and usr.IdUsuario = " + ((UbicaloUI)mainUI).sessionInformation.getStrUserId();

        System.out.println("Listar grid avance" + queryString);

        try {
            stQuery = ((UbicaloUI)mainUI).databaseProvider.getCurrentConnection().createStatement();
            rsRecords = stQuery.executeQuery(queryString);
            if (rsRecords.next()) {
                do {
                    String hora = rsRecords.getString("FechaYHora").substring(11, rsRecords.getString("FechaYHora").length());
                    Object itemId = avenceContainer.addItem();
                    avenceContainer.getContainerProperty(itemId, FECHA_PROPERTY).setValue(Utileria.getFechaDDMMYYYY(rsRecords.getDate("FechaYHora")));
                    avenceContainer.getContainerProperty(itemId, HORA_PROPERTY).setValue(hora);
                    avenceContainer.getContainerProperty(itemId, CODIGO_PROPERTY).setValue(rsRecords.getString("Equipo"));
                    avenceContainer.getContainerProperty(itemId, ACUMULADO_PROPERTY).setValue(numberFormat.format(rsRecords.getDouble("montoAcumulado")));
                    avenceContainer.getContainerProperty(itemId, SEGURO_PROPERTY).setValue(numberFormat.format(rsRecords.getDouble("montoSeguro")));
                    avenceContainer.getContainerProperty(itemId, PROBABLE_PROPERTY).setValue(numberFormat.format(rsRecords.getDouble("montoProbable")));
                    avenceContainer.getContainerProperty(itemId, COMENTARIO_PROPERTY).setValue(rsRecords.getString("comentario"));
                } while(rsRecords.next());
            }
        } catch (Exception var3) {
            System.out.println("Error al listar tabla de reporto : " + var3);
            var3.printStackTrace();
        }

    }

    public void insertReporto() {

        queryString = "Insert Into reporto (IdUsuario, FechaYHora, MontoAcumulado, MontoSeguro, MontoProbable, Comentario)";
        queryString = queryString + " Values (";
        queryString = queryString + ((UbicaloUI)mainUI).sessionInformation.getStrUserId();
        queryString = queryString + ",CURRENT_TIMESTAMP";
        queryString = queryString + "," + alMomentoTxt.getDoubleValueDoNotThrow();
        queryString = queryString + "," + segurosTxt.getDoubleValueDoNotThrow();
        queryString = queryString + "," + probableTxt.getDoubleValueDoNotThrow();
        queryString = queryString + ",'" + (String)comentarioTxt.getValue() + "'";
        queryString = queryString + ")";

        try {
            stQuery = ((UbicaloUI)mainUI).databaseProvider.getCurrentConnection().createStatement();
            stQuery.executeUpdate(queryString);
            llenarGridAvanceMeta();

            limpiarCampos();
        } catch (Exception var2) {
            System.out.println("Error al momento de insertar registros en reporto " + var2);
            var2.printStackTrace();
        }

    }

    public void limpiarCampos(){
        alMomentoTxt.clear();
        segurosTxt.clear();
        probableTxt.clear();
        comentarioTxt.clear();
    }

    public void enter(ViewChangeListener.ViewChangeEvent event) {
        Page.getCurrent().setTitle("UBICALO - Reportar Recuperación");
    }
}

