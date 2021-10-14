package com.simpletecno.recobrapp.main;

import com.simpletecno.recobrapp.utileria.MyEmailMessanger;
import com.simpletecno.recobrapp.utileria.MySessionInitListener;
import com.simpletecno.recobrapp.administrativo.*;
import com.simpletecno.recobrapp.conexion.MyDatabaseProvider;
import com.simpletecno.recobrapp.conexion.SessionInformation;
import com.simpletecno.recobrapp.views.error.ErrorView;
import com.simpletecno.recobrapp.views.error.LogoView;
import com.simpletecno.recobrapp.views.login.ChangePassword;
import com.simpletecno.recobrapp.views.login.LoginForm;
import com.simpletecno.recobrapp.views.reporto.ResumenReportoMetaView;
import com.simpletecno.recobrapp.views.usuario.UsersView;
import com.simpletecno.recobrapp.views.finiquitos.ConsultarFiniquitosView;
import com.simpletecno.recobrapp.views.finiquitos.FiniquitosView;
import com.simpletecno.recobrapp.views.finiquitos.ImportarView;
import com.simpletecno.recobrapp.views.reporto.ReportoMetaView;
import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.*;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.vaadin.dialogs.ConfirmDialog;

import javax.mail.MessagingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

@Theme("tests-valo-facebook")
@Title("RECOBRAPP")
@PreserveOnRefresh
public class RecobrAppUI extends UI implements Button.ClickListener {

    private boolean testMode = false;
    String loginMsg;
    com.simpletecno.recobrapp.views.login.LoginForm loginForm;

    Statement stQuery = null;
    ResultSet rsRecords = null;

    public MyDatabaseProvider databaseProvider;
    public SessionInformation sessionInformation;

    MenuLayout root = new MenuLayout();
    ComponentContainer viewDisplay = root.getContentContainer();

    CssLayout menu = new CssLayout();
    CssLayout menuItemsLayout = new CssLayout();

    {
        menu.setId("testMenu");
    }

    private Navigator navigator;
    private LinkedHashMap<String, String> menuItems = new LinkedHashMap<String, String>();
    private final LinkedHashMap<String, FontAwesome> iconItems = new LinkedHashMap< String, FontAwesome>();

    public ThemeResource projectLogo = new ThemeResource("img/logo_Gestionadora.jpg");
    public Embedded projectCover = new Embedded(null, projectLogo);

    @WebServlet(urlPatterns = "/*", name = "GCUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = RecobrAppUI.class, productionMode = true)
    public static class GCUIServlet extends VaadinServlet {

        @Override
        protected void servletInitialized() throws ServletException {
            super.servletInitialized();
            getService().addSessionInitListener(
                    new MySessionInitListener());
        }
    }

    public String currentViewName;

    @Override
    protected void init(VaadinRequest vaadinRequest) {

        getPage().setTitle("RECOBRAPP v 1.0");
        setLocale(Locale.US);
        addDetachListener(new DetachListener() {
            @Override
            public void detach(DetachEvent event) {
                logOff();
            }
        });

        if (vaadinRequest.getParameter("test") != null) {
            testMode = true;

            if (browserCantRenderFontsConsistently()) {
                getPage().getStyles().add(
                        ".v-app.v-app.v-app {font-family: Sans-Serif;}");
            }
        }
        if (getPage().getWebBrowser().isIE()
                && getPage().getWebBrowser().getBrowserMajorVersion() == 9) {
            menu.setWidth("320px");
        }
        if (!testMode) {
            Responsive.makeResponsive(this);
        }

        if (vaadinRequest.getParameter("test") != null) {
            testMode = true;

            if (browserCantRenderFontsConsistently()) {
                getPage().getStyles().add(
                        ".v-app.v-app.v-app {font-family: Sans-Serif;}");
            }
        }
        addStyleName(ValoTheme.UI_WITH_MENU);

        buildLoginView();
    }

    private boolean browserCantRenderFontsConsistently() {
        return getPage().getWebBrowser().getBrowserApplication()
                .contains("PhantomJS")
                || (getPage().getWebBrowser().isIE() && getPage()
                .getWebBrowser().getBrowserMajorVersion() <= 9);
    }

    static boolean isTestMode() {
        return ((RecobrAppUI) getCurrent()).testMode;
    }

    public void buildLoginView() {
        getUI().getPage().setLocation("");
        loginForm = new LoginForm();
        setContent(loginForm);
        addStyleName("loginview");
        loginForm.userName.focus();
    }

    public boolean validateUser(String userName, String passWord) {

        try {

            if (!connectToDB()) {
                return false;
            }

            stQuery = databaseProvider.getCurrentConnection().createStatement();

            String queryString;

            queryString = "Select Usr.*, Emp.Nombre EmpresaNombre, Emp.Estatus EmpresaEstatus, Emp.Logo ";
            queryString += " From       usuario Usr ";
            queryString += " Inner Join empresa Emp On Emp.IdEmpresa = Usr.IdEmpresa ";
            queryString += " Where Upper(Usr.Usuario)  = '" + userName.toUpperCase() + "'";
            queryString += " And  Usr.Clave    = Sha1('" + passWord + "')";      
            
            System.out.println("query consulta " + queryString);
            System.out.println("usuario consultar " + userName);

            stQuery = databaseProvider.getCurrentConnection().createStatement();
            rsRecords = stQuery.executeQuery(queryString);

            if (rsRecords.next()) { //  encontrado

                if (rsRecords.getString("Estatus").toUpperCase().compareTo("INACTIVO") == 0) {
                    Notification.show("Usuario tiene estatus INACTIVO, por favor consulte a su administrador!", Notification.Type.HUMANIZED_MESSAGE);
                    loginMsg = "Usuario INACTIVO!";
                    return false;
                }
                if (rsRecords.getString("EmpresaEstatus").toUpperCase().compareTo("INACTIVA") == 0) {
                    Notification.show("Su empresa tiene estatus INACTIVA, por favor consulte a su administrador!", Notification.Type.HUMANIZED_MESSAGE);
                    loginMsg = "Empresa INACTIVA!";
                    return false;
                }

                sessionInformation = new SessionInformation();

                sessionInformation.setStrUserId(rsRecords.getString("IdUsuario"));
                sessionInformation.setStrUserName(rsRecords.getString("Usuario"));
                sessionInformation.setStrUserFullName(rsRecords.getString("Nombre"));
                sessionInformation.setStrUserProfile(rsRecords.getString("Perfil"));
                sessionInformation.setStrUserProfileName(rsRecords.getString("Perfil"));
                sessionInformation.setStrCompanyId(rsRecords.getString("IdEmpresa"));
                sessionInformation.setStrCompanyName(rsRecords.getString("EmpresaNombre"));
                sessionInformation.setStrAliasName(loginForm.farmName.getValue().trim().toUpperCase());
                sessionInformation.setStrLastLogin(rsRecords.getString("UltimoLogin"));
                sessionInformation.setStrUserSpecialCode(rsRecords.getString("Equipo"));

                sessionInformation.setPhotoStreamResource(null);

                getPage().setTitle("RECOBRAPP / " + sessionInformation.getStrCompanyName());

                final byte docBytes[] = rsRecords.getBytes("Logo");

                if (docBytes != null) {
                    sessionInformation.setPhotoStreamResource(new StreamResource(
                            new StreamResource.StreamSource() {
                        public InputStream getStream() {
                            return new ByteArrayInputStream(docBytes);
                        }
                    }, rsRecords.getString("IdUsuario")
                    ));
                }

                if (databaseProvider.getUsedDBDataSource().equals("MYSQL")) {
                    rsRecords = stQuery.executeQuery("SELECT UUID() AS SESION_UNICA");
                } else {
                    rsRecords = stQuery.executeQuery("SELECT NEWID() AS SESION_UNICA");
                }
                rsRecords.next();

                String sessionUnica = rsRecords.getString("SESION_UNICA");
                sessionInformation.setStrSessionId(sessionUnica);

                if (sessionInformation.getStrLastLogin() != null) {
                    stQuery.executeUpdate("Update usuario set UltimoLogin = current_timestamp Where IdUsuario = " + sessionInformation.getStrUserId());
                }

            } else {
                System.out.println(" error ");
                Notification.show("Usuario incorrecto o contraseña incorrecta, intente de nuevo!", Notification.Type.WARNING_MESSAGE);
                loginMsg = "Usuario incorrecto o contraseña incorrecta. <span>Intente de nuevo o registrese.</span>";
                return false;
            }

        } catch (Exception ex1) {
            Logger.getLogger(RecobrAppUI.class.getName()).log(Level.SEVERE, null, ex1);
            System.out.println("Error : " + ex1.getMessage());
            Notification.show("Error al intentar consultar base de datos..!", Notification.TYPE_ERROR_MESSAGE);
            ex1.printStackTrace();

            try {
                String emailsTo[] = {"alerta@simpletecno.com"};
                MyEmailMessanger eMail = new MyEmailMessanger();

                eMail.postMail(emailsTo, "Error en RECOBRAPP", "Error al consultar base de datos (login)..! " + ex1.getMessage());
            } catch (Exception ex2) {
                Logger.getLogger(RecobrAppUI.class.getName()).log(Level.SEVERE, null, ex2);
            }

            return false;
        }

        return true;
    }

    public boolean connectToDB() {
        try {

            if (databaseProvider == null) {
                databaseProvider = new MyDatabaseProvider();
                databaseProvider.getNewConnection();
            }

            if (databaseProvider.getCurrentConnection() == null) {

                databaseProvider = null;

                Notification.show("PROBLEMA AL CONECTARSE A BASE DE DATOS, POR FAVOR CONTACTE AL DESARROLLADOR", Notification.Type.ERROR_MESSAGE);

                String emailRecipients[] = {"alerta@simpletecno.com"};

                MyEmailMessanger eMail = new MyEmailMessanger();

                try {
                    eMail.postMail(emailRecipients, "Error RECOBRAPP ", "NO HAY CONEXION A BASE DE DATOS");
                } catch (Exception ex) {
                    Logger.getLogger(RecobrAppUI.class.getName()).log(Level.SEVERE, null, ex);
                }

                return false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            Notification.show("PROBLEMA AL CONECTARSE A BASE DE DATOS, POR FAVOR CONTACTE AL DESARROLLADOR.", Notification.Type.ERROR_MESSAGE);

            return false;
        }

        return true;
    }

    public void buildMainView() {

        removeStyleName("loginview");

        if (getPage().getWebBrowser().isIE()
                && getPage().getWebBrowser().getBrowserMajorVersion() == 9) {
            menu.setWidth("320px");
        }

        if (!testMode) {
            Responsive.makeResponsive(this);
        }

        getPage().setTitle("RECOBRAPP");
        addStyleName(ValoTheme.UI_WITH_MENU);

        navigator = new Navigator(this, viewDisplay);

        getNavigator().addView("importar finiquitos", ImportarView.class);
        getNavigator().addView("consultar finiquitos", ConsultarFiniquitosView.class);
        getNavigator().addView("finiquitos", FiniquitosView.class);
        getNavigator().addView("reportar recuperacion", ReportoMetaView.class);
        getNavigator().addView("control", ResumenReportoMetaView.class);
        getNavigator().addView("bitacora", BitacoraView.class);
        getNavigator().addView("usuarios", UsersView.class);
        //getNavigator().addView("logo", LogoView.class);

        getNavigator().setErrorView(ErrorView.class);

        String f = Page.getCurrent().getUriFragment();
        if (f == null || f.equals("")) {
            if (sessionInformation.getStrUserProfileName().equals("ADMINISTRADOR")){
                getNavigator().navigateTo("finiquitos");
            }else{
                getNavigator().navigateTo("consultar");
            }

        }

        getNavigator().addViewChangeListener(new ViewChangeListener() {

            @Override
            public boolean beforeViewChange(ViewChangeEvent event) {
                return true;
            }

            @Override
            public void afterViewChange(ViewChangeEvent event) {

                for (Iterator<Component> it = menuItemsLayout.iterator(); it
                        .hasNext();) {
                    it.next().removeStyleName("selected");
                }
                currentViewName = event.getViewName();

                for (final Entry<String, String> item : menuItems.entrySet()) {
                    if (event.getViewName().equals(item.getKey())) {
                        for (final Iterator<Component> it = menuItemsLayout
                                .iterator(); it.hasNext();) {
                            final Component c = it.next();
                            if (c.getCaption() != null
                                    && c.getCaption().startsWith(
                                            item.getValue())) {
                                if (verificarAcceso(item.getValue())) {
                                    c.addStyleName("selected");
                                }
                                break;
                            }
                        }
                        break;
                    }
                }

                menu.removeStyleName("valo-menu-visible");
            }
        });

        setContent(root);
        root.setWidth("100%");

        root.addMenu(buildMenu());

    }

    CssLayout buildMenu() {
        // Add items
        menuItems.put("importar finiquitos", "Importar Finiquitos");
        iconItems.put("importar finiquitos", FontAwesome.SORT_AMOUNT_ASC);

        menuItems.put("consultar finiquitos", "Consultar Finiquitos");
        iconItems.put("consultar finiquitos", FontAwesome.SEARCH);

        menuItems.put("finiquitos", "Mantenimiento Finiquitos");
        iconItems.put("finiquitos", FontAwesome.PLUS);

        menuItems.put("reportar recuperacion", "Reportar Recuperación");
        iconItems.put("reportar recuperacion", FontAwesome.CHECK);

        menuItems.put("control", "Resumen de Recuperación");
        iconItems.put("control", FontAwesome.BOOK);

        menuItems.put("bitacora", "Bitacora");
        iconItems.put("bitacora", FontAwesome.BINOCULARS);

        menuItems.put("usuarios", "Usuarios");
        iconItems.put("usuarios", FontAwesome.USERS);

        ///menuItems.put("logo", "Logo");
        //iconItems.put("logo", FontAwesome.USERS);

        HorizontalLayout top = new HorizontalLayout();
        top.setWidth("100%");
        top.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
        top.addStyleName(ValoTheme.MENU_TITLE);
        menu.addComponent(top);

        Button showMenu = new Button("Menu", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                if (menu.getStyleName().contains("valo-menu-visible")) {
                    menu.removeStyleName("valo-menu-visible");
                } else {
                    menu.addStyleName("valo-menu-visible");
                }
            }
        });

        showMenu.addStyleName(ValoTheme.BUTTON_PRIMARY);
        showMenu.addStyleName(ValoTheme.BUTTON_SMALL);
        showMenu.addStyleName("valo-menu-toggle");
        showMenu.setIcon(FontAwesome.LIST);
        menu.addComponent(showMenu);

        Label title = new Label("<strong>" + sessionInformation.getStrProjectName() + "</strong>", ContentMode.HTML);
        title.setSizeUndefined();
        top.addComponent(title);
        top.setExpandRatio(title, 1);

        MenuBar settings = new MenuBar();
        settings.addStyleName("user-menu");

        // Define a common menu command for all the menu items.
        MenuBar.Command mycommand = new MenuBar.Command() {
            @Override
            public void menuSelected(MenuBar.MenuItem selectedItem) {

                if (selectedItem.getId() == 4) { // cambiar clave
                    ChangePassword changePassword = new ChangePassword(sessionInformation.getStrUserId(), sessionInformation.getStrUserName(), sessionInformation.getStrLastLogin());
                    getUI().addWindow(changePassword);
                    changePassword.center();
                    changePassword.txtPasswordActual.focus();
                }
                if (selectedItem.getId() == 7) {

                    ConfirmDialog.show(UI.getCurrent(), "Confirme:", "Está seguro de salir del sistema?",
                            "SI", "NO", new ConfirmDialog.Listener() {

                        public void onClose(ConfirmDialog dialog) {
                            if (dialog.isConfirmed()) {
                                logOff();
                            }
                        }
                    });
                }

            }
        };

        settings.addStyleName("menu-logo-empresa");
        settings.setAutoOpen(true);

        MenuBar.MenuItem userSettingsItem = settings.addItem(sessionInformation.getStrUserFullName(), sessionInformation.getPhotoStreamResource(), null);
        userSettingsItem.addItem("Preferencias", FontAwesome.HEART, mycommand).setDescription("Preferencias del usuario.");
        userSettingsItem.addSeparator();
        userSettingsItem.addItem("Cambiar clave", FontAwesome.USER_SECRET, mycommand).setDescription("Cambio de contraseña/clave.");
        userSettingsItem.addSeparator();
        userSettingsItem.addItem("Salir", FontAwesome.SIGN_OUT, mycommand).setDescription("Salir (logout) del sistema."); //10
        menu.addComponent(settings);

        menuItemsLayout.setPrimaryStyleName("valo-menuitems");
        menu.addComponent(menuItemsLayout);

        Label label = null;
        int count = -1;
        for (final Map.Entry<String, String> item : menuItems.entrySet()) {
            if (item.getKey().equals("importar finiquitos")) {
                label = new Label("Finiquitos", ContentMode.HTML);
                label.setPrimaryStyleName(ValoTheme.MENU_SUBTITLE);
                label.addStyleName(ValoTheme.LABEL_H4);
                label.setSizeUndefined();
                menuItemsLayout.addComponent(label);
            }

            if (item.getKey().equals("reportar recuperacion")) {
                label = new Label("Reporto ", ContentMode.HTML);
                label.setPrimaryStyleName(ValoTheme.MENU_SUBTITLE);
                label.addStyleName(ValoTheme.LABEL_H4);
                label.setSizeUndefined();
                menuItemsLayout.addComponent(label);
            }

            if (item.getKey().equals("bitacora")) {
                label = new Label("Sistema ", ContentMode.HTML);
                label.setPrimaryStyleName(ValoTheme.MENU_SUBTITLE);
                label.addStyleName(ValoTheme.LABEL_H4);
                label.setSizeUndefined();
                menuItemsLayout.addComponent(label);
            }
            Button b = new Button(item.getValue(), new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent event) {
                    if (verificarAcceso(event.getButton().getCaption())) {
                        getNavigator().navigateTo(item.getKey());
                    } else {
                        Notification.show("Usuario no tiene acceso a esta operación.", Notification.Type.WARNING_MESSAGE);
                    }

                }
            });

            b.setVisible((verificarAcceso(item.getValue())));
            b.setHtmlContentAllowed(true);
            b.setPrimaryStyleName(ValoTheme.MENU_ITEM);
            b.setIcon(iconItems.get(item.getKey()));
            menuItemsLayout.addComponent(b);
            count++;
        }

        return menu;
    }

    private boolean verificarAcceso(String acceso) {

        if (sessionInformation.getStrUserProfileName().equals("ADMINISTRADOR")) {
            return true;
        }
        if (sessionInformation.getStrUserProfileName().equals("SUPERVISOR")) {
            return (acceso.equals("Consultar Finiquitos")
                    || acceso.equals("Mantenimiento Finiquitos")
                    || acceso.equals("Reportar Recuperación"));
        }

        if (sessionInformation.getStrUserProfileName().equals("BACKOFFICE")) {
            return (acceso.equals("Consultar Finiquitos")
                    || acceso.equals("Mantenimiento Finiquitos"));
        }

        return false;

    }

    @Override
    public void buttonClick(Button.ClickEvent event) {

        if (event.getButton().getId().compareTo("LOGIN") == 0) {
            if (validateUser(loginForm.userName.getValue(), loginForm.password.getValue())) {//System.out.println("login 1...");
                 if (sessionInformation.getStrLastLogin() == null) {
                     getUI().addWindow(new ChangePassword(sessionInformation.getStrUserId(), sessionInformation.getStrUserName(), sessionInformation.getStrLastLogin()));
                 }
                 buildMainView();

            } else {

                if (loginForm.loginPanel.getComponentCount() > 3) {
                    loginForm.loginPanel.removeComponent(loginForm.loginPanel.getComponent(3));
                }
                Label error = new Label(loginMsg, ContentMode.HTML);
                error.addStyleName("error");
                error.setSizeUndefined();
                error.addStyleName("light");
                // Add animation
                error.addStyleName("v-animate-reveal");
                loginForm.loginPanel.addComponent(error);
                loginForm.userName.focus();
            }
        } else {
            Notification.show(event.getButton().getCaption() + " NO DISPONIBLE EN ESTA VERSION!!", Notification.Type.TRAY_NOTIFICATION);
        }
    }

    public void logOff() {

        try {
            if (databaseProvider != null) {
                databaseProvider.getCurrentConnection().close();
            }
            databaseProvider = null;
        } catch (Exception sqlE) {
            sqlE.printStackTrace();
        }
        if (getNavigator() != null) {
            getNavigator().destroy();
        }
        if (VaadinService.getCurrentRequest() != null) {
            VaadinService.getCurrentRequest().getWrappedSession().invalidate();
            VaadinSession.getCurrent().close();
        }

        getSession().close();

        getUI().getPage().setLocation("");

        setContent(null);
    }

    public Navigator getNavigator() {
        return navigator;
    }
}
