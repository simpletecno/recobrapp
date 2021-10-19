package com.simpletecno.recobrapp.views.reporto;

import com.simpletecno.recobrapp.main.RecobrAppUI;
import com.simpletecno.recobrapp.utileria.Utileria;
import com.vaadin.data.Property;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.event.SelectionEvent;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.*;

import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.Date;

public class ResumenReportoMetaView extends VerticalLayout implements View {

    Grid resumenGrid;
    public IndexedContainer resumenContainer = new IndexedContainer();

    static final String ID_PROPERTY = "ID";
    static final String ID_USUARIO_PROPERTY = "ID USUARIO";
    static final String USUARIO_PROPERTY = "Supervisor";
    static final String EQUIPO_PROPERTY = "Equipo";
    static final String META_PROPERTY = "Meta del dia";
    static final String ACUMULADO_PROPERTY = "Acumulado";
    static final String SEGURO_PROPERTY = "Seguro";
    static final String PROBABLE_PROPERTY = "Probable";
    static final String PORCENTAJE_PROPERTY = "Porcentaje";

    static final String FECHA_PROPERTY = "Fecha";
    static final String HORA_PROPERTY = "Hora";
    static final String COMENTARIO_PROPERTY = "Comentario";

    Grid bitacoraGrid;
    public IndexedContainer bitacoraContainer = new IndexedContainer();
    Grid.FooterRow footer;


    DateField fechaDt;
    UI mainUI = UI.getCurrent();

    Statement stQuery;
    ResultSet rsRecords;

    Statement stQuery2;
    ResultSet rsRecords2;

    String queryString;

    TextField comentarioTxt;

    Button guardarBtn;

    static DecimalFormat numberFormat = new DecimalFormat("#,###,##0.00");

    public ResumenReportoMetaView() {
        setWidth("100%");
        setMargin(new MarginInfo(true, false, false, false));
        setSpacing(true);

        Label titleLbl = new Label("Resumen de recuperación");
        titleLbl.addStyleName("h2");
        titleLbl.setSizeUndefined();

        fechaDt = new DateField("Fecha : ");
        fechaDt.setDateFormat("dd/MM/yyyy");
        fechaDt.setValue(new Date());
        fechaDt.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                llenarResumenGrid();
            }
        });

        HorizontalLayout titleLayout = new HorizontalLayout();
        titleLayout.addStyleName("v-component-group");
        titleLayout.setResponsive(true);
        titleLayout.setSpacing(true);
        titleLayout.setWidth("100%");
        titleLayout.setMargin(new MarginInfo(false, true, false, true));
        titleLayout.addComponents(titleLbl, fechaDt);
        titleLayout.setComponentAlignment(titleLbl, Alignment.MIDDLE_LEFT);
        titleLayout.setComponentAlignment(fechaDt, Alignment.MIDDLE_RIGHT);

        addComponent(titleLayout);
        setComponentAlignment(titleLayout, Alignment.TOP_CENTER);

        agregarHistorialPagos();
        llenarResumenGrid();
    }

    public void agregarHistorialPagos() {

        VerticalLayout gridLayout = new VerticalLayout();
        gridLayout.addStyleName("rcorners3");
        gridLayout.setWidth("100%");
        gridLayout.setResponsive(true);
        gridLayout.setSpacing(true);
        gridLayout.setMargin(false);

        resumenContainer.addContainerProperty(ID_PROPERTY, String.class, null);
        resumenContainer.addContainerProperty(ID_USUARIO_PROPERTY, String.class, null);
        resumenContainer.addContainerProperty(USUARIO_PROPERTY, String.class, null);
        resumenContainer.addContainerProperty(EQUIPO_PROPERTY, String.class, null);
        resumenContainer.addContainerProperty(META_PROPERTY, String.class, null);
        resumenContainer.addContainerProperty(ACUMULADO_PROPERTY, String.class, null);
        resumenContainer.addContainerProperty(SEGURO_PROPERTY, String.class, null);
        resumenContainer.addContainerProperty(PROBABLE_PROPERTY, String.class, null);
        resumenContainer.addContainerProperty(PORCENTAJE_PROPERTY, String.class, null);

        resumenGrid = new Grid("Supervisores", resumenContainer);
        resumenGrid.setWidth("100%");
        resumenGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        resumenGrid.setHeightMode(HeightMode.ROW);
        resumenGrid.setHeightByRows(6);
        resumenGrid.setResponsive(true);
        resumenGrid.addSelectionListener(new SelectionEvent.SelectionListener() {
            @Override
            public void select(SelectionEvent selectionEvent) {
                if (resumenGrid.getSelectedRow() != null) {
                    llenarGridBitacora(String.valueOf(resumenContainer.getContainerProperty(resumenGrid.getSelectedRow(), ID_USUARIO_PROPERTY).getValue()),
                            String.valueOf(resumenContainer.getContainerProperty(resumenGrid.getSelectedRow(), EQUIPO_PROPERTY).getValue()));
                }
            }
        });
        
        footer = resumenGrid.appendFooterRow();
        footer.getCell(META_PROPERTY).setText("0.00");        
        footer.getCell(ACUMULADO_PROPERTY).setText("0.00");        
        footer.getCell(SEGURO_PROPERTY).setText("0.00");
        footer.getCell(PROBABLE_PROPERTY).setText("0.00");
        footer.getCell(PORCENTAJE_PROPERTY).setText("0.00");
        
        footer.getCell(META_PROPERTY).setStyleName("rightalign");
        footer.getCell(ACUMULADO_PROPERTY).setStyleName("rightalign");
        footer.getCell(SEGURO_PROPERTY).setStyleName("rightalign");
        footer.getCell(PROBABLE_PROPERTY).setStyleName("rightalign");
        footer.getCell(PORCENTAJE_PROPERTY).setStyleName("rightalign");        

        resumenGrid.setCellStyleGenerator((cellReference) -> {
            if (META_PROPERTY.equals(cellReference.getPropertyId())) {
                return "rightalign";
            } else if (ACUMULADO_PROPERTY.equals(cellReference.getPropertyId())) {
                return "rightalign";
            } else if (SEGURO_PROPERTY.equals(cellReference.getPropertyId())) {
                return "rightalign";
            } else if (PROBABLE_PROPERTY.equals(cellReference.getPropertyId())) {
                return "rightalign";
            } else {
                return PORCENTAJE_PROPERTY.equals(cellReference.getPropertyId()) ? "rightalign" : null;
            }
        });

        resumenGrid.getColumn(ID_PROPERTY).setExpandRatio(1).setHidable(true).setHidden(true);
        resumenGrid.getColumn(ID_USUARIO_PROPERTY).setExpandRatio(1).setHidable(true).setHidden(true);
        resumenGrid.getColumn(USUARIO_PROPERTY).setExpandRatio(2);
        resumenGrid.getColumn(EQUIPO_PROPERTY).setExpandRatio(2);
        resumenGrid.getColumn(META_PROPERTY).setExpandRatio(1);
        resumenGrid.getColumn(ACUMULADO_PROPERTY).setExpandRatio(1);
        resumenGrid.getColumn(SEGURO_PROPERTY).setExpandRatio(1);
        resumenGrid.getColumn(PROBABLE_PROPERTY).setExpandRatio(1);
        resumenGrid.getColumn(PORCENTAJE_PROPERTY).setExpandRatio(1);
        resumenGrid.setDescription("Elija un registro para visualizar la bitácora.");

        Grid.HeaderRow filterRow = resumenGrid.appendHeaderRow();
        Grid.HeaderCell cellF = (Grid.HeaderCell) filterRow.getCell(USUARIO_PROPERTY);
        TextField filterFieldF = new TextField();
        filterFieldF.addStyleName("tiny");
        filterFieldF.setInputPrompt("Filtrar");
        filterFieldF.setColumns(10);
        filterFieldF.addTextChangeListener((change) -> {
            resumenContainer.removeContainerFilters(USUARIO_PROPERTY);
            if (!change.getText().isEmpty()) {
                resumenContainer.addContainerFilter(new SimpleStringFilter(USUARIO_PROPERTY, change.getText(), true, false));
            }

        });
        cellF.setComponent(filterFieldF);

        Grid.HeaderCell cellB = (Grid.HeaderCell) filterRow.getCell(EQUIPO_PROPERTY);
        TextField filterFieldB = new TextField();
        filterFieldB.addStyleName("tiny");
        filterFieldB.setInputPrompt("Filtrar");
        filterFieldB.setColumns(8);
        filterFieldB.addTextChangeListener((change) -> {
            resumenContainer.removeContainerFilters(EQUIPO_PROPERTY);
            if (!change.getText().isEmpty()) {
                resumenContainer.addContainerFilter(new SimpleStringFilter(EQUIPO_PROPERTY, change.getText(), true, false));
            }

        });
        cellB.setComponent(filterFieldB);

        bitacoraContainer.addContainerProperty(FECHA_PROPERTY, String.class, null);
        bitacoraContainer.addContainerProperty(HORA_PROPERTY, String.class, null);
        bitacoraContainer.addContainerProperty(EQUIPO_PROPERTY, String.class, null);
        bitacoraContainer.addContainerProperty(ACUMULADO_PROPERTY, String.class, null);
        bitacoraContainer.addContainerProperty(SEGURO_PROPERTY, String.class, null);
        bitacoraContainer.addContainerProperty(PROBABLE_PROPERTY, String.class, null);
        bitacoraContainer.addContainerProperty(COMENTARIO_PROPERTY, String.class, null);

        bitacoraGrid = new Grid("Bitacora del dia", bitacoraContainer);
        bitacoraGrid.setWidth("100%");
        bitacoraGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        bitacoraGrid.setHeightMode(HeightMode.ROW);
        bitacoraGrid.setHeightByRows(5);
        bitacoraGrid.setResponsive(true);

        bitacoraGrid.getColumn(FECHA_PROPERTY).setExpandRatio(1);
        bitacoraGrid.getColumn(HORA_PROPERTY).setExpandRatio(1);
        bitacoraGrid.getColumn(EQUIPO_PROPERTY).setExpandRatio(2);
        bitacoraGrid.getColumn(ACUMULADO_PROPERTY).setExpandRatio(1);
        bitacoraGrid.getColumn(SEGURO_PROPERTY).setExpandRatio(1);
        bitacoraGrid.getColumn(PROBABLE_PROPERTY).setExpandRatio(1);
        bitacoraGrid.getColumn(COMENTARIO_PROPERTY).setExpandRatio(2);

        bitacoraGrid.setCellStyleGenerator((cellReference) -> {
            if (ACUMULADO_PROPERTY.equals(cellReference.getPropertyId())) {
                return "rightalign";
            } else if (SEGURO_PROPERTY.equals(cellReference.getPropertyId())) {
                return "rightalign";
            } else if (PROBABLE_PROPERTY.equals(cellReference.getPropertyId())) {
                return "rightalign";
            }
            return null;

        });

        HorizontalLayout comentarioLayout = new HorizontalLayout();
        comentarioLayout.setSpacing(true);
        comentarioLayout.setWidth("100%");
        comentarioLayout.addStyleName("rcorners3");

        comentarioTxt = new TextField("Comentario para supervisores");
        comentarioTxt.setWidth("80%");
        comentarioTxt.setDescription("Escriba aqui un mensaje que quiere que le aparezca al supervisor.");

        guardarBtn = new Button("Enviar");
        guardarBtn.setIcon(FontAwesome.SEND);
        guardarBtn.setWidth("20%");
        guardarBtn.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                insetarComentario();
            }
        });

        comentarioLayout.addComponents(comentarioTxt, guardarBtn);
        comentarioLayout.setComponentAlignment(comentarioTxt, Alignment.BOTTOM_LEFT);
        comentarioLayout.setComponentAlignment(guardarBtn, Alignment.BOTTOM_RIGHT);

        gridLayout.addComponent(resumenGrid);
        gridLayout.setComponentAlignment(resumenGrid, Alignment.TOP_CENTER);

        gridLayout.addComponent(bitacoraGrid);
        gridLayout.setComponentAlignment(bitacoraGrid, Alignment.TOP_CENTER);

        gridLayout.addComponent(comentarioLayout);
        gridLayout.setComponentAlignment(comentarioLayout, Alignment.TOP_CENTER);

        addComponent(gridLayout);
    }

    public void llenarResumenGrid() {
        
        footer.getCell(META_PROPERTY).setText("0.00");        
        footer.getCell(ACUMULADO_PROPERTY).setText("0.00");        
        footer.getCell(SEGURO_PROPERTY).setText("0.00");
        footer.getCell(PROBABLE_PROPERTY).setText("0.00");
        footer.getCell(PORCENTAJE_PROPERTY).setText("0.00");
        
        resumenContainer.removeAllItems();
        bitacoraContainer.removeAllItems();

        queryString = "Select rep.MontoAcumulado, rep.MontoSeguro , rep.MontoProbable, usr.IdUsuario, usr.Nombre, usr.MetaDiaria, usr.Equipo ";
        queryString += " From reporto rep";
        queryString += " Inner join usuario usr on usr.IdUsuario = rep.IdUsuario";
        queryString += " where DATE_FORMAT(rep.FechaYHora,'%d/%m/%Y') = '" + Utileria.getFechaDDMMYYYY((fechaDt.getValue())) + "'";
        queryString += " Group by rep.IdUsuario ";

//System.out.println("query de resumen grid " + queryString);

        double porcentaje = 0.00;
        double meta = 0.00;
        double acumulado = 0.00;
        double probable = 0.00;
        double seguros = 0.00;
        double totalPorcentaje = 0.00;

        try {
            stQuery = ((RecobrAppUI) mainUI).databaseProvider.getCurrentConnection().createStatement();
            rsRecords = stQuery.executeQuery(queryString);
            
            if (rsRecords.next()) {
                do {

                    queryString = "Select *";
                    queryString += " From reporto ";
                    queryString += " where DATE_FORMAT(FechaYHora,'%d/%m/%Y') = '" + Utileria.getFechaDDMMYYYY((fechaDt.getValue())) + "'";
                    queryString += " and IdUsuario = " + rsRecords.getString("IdUsuario");
                    queryString += " ORDER by IdReporto desc";

                    stQuery2 = ((RecobrAppUI) mainUI).databaseProvider.getCurrentConnection().createStatement();
                    rsRecords2 = stQuery2.executeQuery(queryString);

                    if (rsRecords2.next()) {
                        
                        seguros += rsRecords2.getDouble("MontoSeguro");
                        acumulado += rsRecords2.getDouble("MontoAcumulado");
                        probable += rsRecords2.getDouble("MontoProbable");
                        meta += rsRecords.getDouble("usr.MetaDiaria");
                        
                        porcentaje = (rsRecords2.getDouble("MontoAcumulado") / rsRecords.getDouble("usr.MetaDiaria")) * 100;
                        totalPorcentaje += porcentaje;
                                                
                        Object itemId = resumenContainer.addItem();
                        resumenContainer.getContainerProperty(itemId, ID_PROPERTY).setValue(rsRecords2.getString("IdReporto"));
                        resumenContainer.getContainerProperty(itemId, ID_USUARIO_PROPERTY).setValue(rsRecords.getString("usr.IdUsuario"));
                        resumenContainer.getContainerProperty(itemId, USUARIO_PROPERTY).setValue(rsRecords.getString("usr.Nombre"));
                        resumenContainer.getContainerProperty(itemId, EQUIPO_PROPERTY).setValue(rsRecords.getString("usr.Equipo"));
                        resumenContainer.getContainerProperty(itemId, META_PROPERTY).setValue(numberFormat.format(rsRecords.getDouble("usr.MetaDiaria")));
                        resumenContainer.getContainerProperty(itemId, ACUMULADO_PROPERTY).setValue(numberFormat.format(rsRecords2.getDouble("MontoAcumulado")));
                        resumenContainer.getContainerProperty(itemId, SEGURO_PROPERTY).setValue(numberFormat.format(rsRecords2.getDouble("MontoSeguro")));
                        resumenContainer.getContainerProperty(itemId, PROBABLE_PROPERTY).setValue(numberFormat.format(rsRecords2.getDouble("MontoProbable")));
                        
                        resumenContainer.getContainerProperty(itemId, PORCENTAJE_PROPERTY).setValue(numberFormat.format(porcentaje) + "%");
                        
                    }                    
                } while (rsRecords.next());
                
                footer.getCell(META_PROPERTY).setText("Q." + numberFormat.format(meta));                
                footer.getCell(ACUMULADO_PROPERTY).setText("Meta - Acumulado = Q." + numberFormat.format((meta - acumulado)));
                footer.getCell(SEGURO_PROPERTY).setText("Q" + numberFormat.format(seguros));
                footer.getCell(PROBABLE_PROPERTY).setText("Q" + numberFormat.format(probable));                
                footer.getCell(PORCENTAJE_PROPERTY).setText(numberFormat.format((totalPorcentaje)/resumenContainer.size()) + "%");
            }
        } catch (Exception var4) {
            System.out.println("Error al llenar resumen grid : " + var4);
            var4.printStackTrace();
        }

    }

    public void llenarGridBitacora(String idUsuario, String equipo) {

        bitacoraContainer.removeAllItems();

        queryString = " Select *";
        queryString = queryString + " From reporto rep";
        queryString = queryString + " where DATE_FORMAT(rep.FechaYHora,'%d/%m/%Y') = '" + Utileria.getFechaDDMMYYYY((Date) this.fechaDt.getValue()) + "'";
        queryString = queryString + " and IdUsuario = " + idUsuario;

 //       System.out.println("query de bitacora" + queryString);

        try {
            stQuery = ((RecobrAppUI) mainUI).databaseProvider.getCurrentConnection().createStatement();
            rsRecords = stQuery.executeQuery(queryString);

            if (rsRecords.next()) {
                do {
                    Object itemId = bitacoraContainer.addItem();

                    bitacoraContainer.getContainerProperty(itemId, FECHA_PROPERTY).setValue(Utileria.getFechaDDMMYYYY(rsRecords.getDate("FechaYHora")));
                    String hora = rsRecords.getString("FechaYHora").substring(11, rsRecords.getString("FechaYHora").length());
                    bitacoraContainer.getContainerProperty(itemId, HORA_PROPERTY).setValue(hora);
                    bitacoraContainer.getContainerProperty(itemId, EQUIPO_PROPERTY).setValue(equipo);
                    bitacoraContainer.getContainerProperty(itemId, ACUMULADO_PROPERTY).setValue(numberFormat.format(rsRecords.getDouble("MontoAcumulado")));
                    bitacoraContainer.getContainerProperty(itemId, SEGURO_PROPERTY).setValue(numberFormat.format(rsRecords.getDouble("MontoSeguro")));
                    bitacoraContainer.getContainerProperty(itemId, PROBABLE_PROPERTY).setValue(numberFormat.format(rsRecords.getDouble("MontoProbable")));
                    bitacoraContainer.getContainerProperty(itemId, COMENTARIO_PROPERTY).setValue(rsRecords.getString("Comentario"));
                } while (rsRecords.next());
            }
        } catch (Exception var4) {
            System.out.println("Error al llenar resumen grid : " + var4);
            var4.printStackTrace();
        }

    }

    public void insetarComentario() {
        if (((String) comentarioTxt.getValue()).trim().isEmpty()) {
            Notification.show("Por favor ingrese un comentario.. ", Notification.Type.WARNING_MESSAGE);
            comentarioTxt.focus();
        } else {
            try {
                queryString = "Insert Into comentario (Comentario, IdUsuario, FechaYHora)";
                queryString += " Values (";
                queryString += "'" + (String) comentarioTxt.getValue() + "'";
                queryString += "," + String.valueOf(resumenContainer.getContainerProperty(resumenGrid.getSelectedRow(), ID_USUARIO_PROPERTY).getValue());
                queryString += ",current_timestamp";
                queryString = queryString + ")";

                stQuery = ((RecobrAppUI) mainUI).databaseProvider.getCurrentConnection().createStatement();
                stQuery.executeUpdate(queryString);

                Notification.show("Comentario creado exitosamente!", Notification.Type.TRAY_NOTIFICATION);

                comentarioTxt.setValue("");
            } catch (Exception var2) {
                System.out.println("Error al momento de ingresar comentario " + var2);
                var2.printStackTrace();
            }

        }
    }

    public void enter(ViewChangeListener.ViewChangeEvent event) {
        Page.getCurrent().setTitle("RECOBRAPP - Resumen de recuperación");
    }
}
