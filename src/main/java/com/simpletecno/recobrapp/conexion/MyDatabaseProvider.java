
package com.simpletecno.recobrapp.conexion;

import com.simpletecno.recobrapp.utileria.EnvironmentVars;
import com.simpletecno.recobrapp.utileria.Utileria;
import com.vaadin.ui.Notification;

import java.sql.Connection;
import java.sql.DriverManager;
import javax.sql.DataSource;

/**
 * Clase para manejar la conectividad a base de datos.
 * Lee variables de ambiente y recursos del archivo context.xml
 * @author jaguirre 
 **/
public class MyDatabaseProvider {
    private Connection currentConnection = null;
    public Utileria utileria = null;
    private EnvironmentVars variablesAmbiente = null;
    public String DtePath = "";

    public MyDatabaseProvider() {
        this.utileria = new Utileria();
        this.variablesAmbiente = new EnvironmentVars();
    }

    public Connection getNewConnection() {
        try {
            this.currentConnection = null;
            if (this.variablesAmbiente.getDbDataSourceName().equals("MYSQL")) {
                Class.forName("com.mysql.jdbc.Driver").newInstance();
            }

            if (this.variablesAmbiente.getDbDataSourceName().equals("MSSQL")) {
                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver").newInstance();
            }

            System.out.println("1) " + this.variablesAmbiente.getDB_URL());
            System.out.println("2) " + this.variablesAmbiente.getDB_USERNAME());
            System.out.println("3) " + this.variablesAmbiente.getDB_PASSWORD());
            this.currentConnection = DriverManager.getConnection(this.variablesAmbiente.getDB_URL(), this.variablesAmbiente.getDB_USERNAME(), this.variablesAmbiente.getDB_PASSWORD());
        } catch (Exception var2) {
            Notification.show("Error al obtener conexion  " + var2);
            this.utileria.escribirLog("N/A", "", "\n\nConnectionBD->getConnection()..Error....: " + var2.getMessage());
            var2.printStackTrace();
        }

        return this.currentConnection;
    }

    private DataSource getDBDataSource() {
        DataSource ds = null;

        try {
            if (this.variablesAmbiente.getDbDataSourceName().equals("MYSQL")) {
                Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
                ds = (DataSource)this.variablesAmbiente.getCurrentContext().lookup("java:comp/env/MYSQLDS");
            }

            if (this.variablesAmbiente.getDbDataSourceName().equals("MSSQL")) {
                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver").newInstance();
                ds = (DataSource)this.variablesAmbiente.getCurrentContext().lookup("java:comp/env/MSSQLDS");
            }

            if (this.variablesAmbiente.getDbDataSourceName().equals("ORACLE")) {
                Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
                ds = (DataSource)this.variablesAmbiente.getCurrentContext().lookup("java:comp/env/ORACLEDS");
            }

            if (this.variablesAmbiente.getDbDataSourceName().equals("SQLITE")) {
                Class.forName("org.sqlite.JDBC").newInstance();
                ds = (DataSource)this.variablesAmbiente.getCurrentContext().lookup("java:comp/env/SQLITEDS");
            }
        } catch (Exception var3) {
            this.utileria.escribirLog("N/A", "", "\n\nConnectionBD->getDBDataSource()...Error, no se pudo instanciar la clase..: " + var3.getMessage());
        }

        return ds;
    }

    public String getUsedDBDataSource() {
        String usedDataSource = null;

        try {
            if (this.variablesAmbiente.getDbDataSourceName().equals("MYSQL")) {
                usedDataSource = "MYSQL";
            }

            if (this.variablesAmbiente.getDbDataSourceName().equals("MSSQL")) {
                usedDataSource = "MSSQL";
            }

            if (this.variablesAmbiente.getDbDataSourceName().equals("ORACLE")) {
                usedDataSource = "ORACLE";
            }
        } catch (Exception var3) {
            this.utileria.escribirLog("N/A", "", "\n\nConnectionBD->getUsedDBDataSource()...Error, no se pudo instanciar la clase..: " + var3.getMessage());
        }

        return usedDataSource;
    }

    public Connection getCurrentConnection() {
        return this.currentConnection;
    }
}