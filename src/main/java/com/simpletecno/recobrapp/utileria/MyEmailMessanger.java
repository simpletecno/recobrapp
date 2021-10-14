package com.simpletecno.recobrapp.utileria;

/**
 * Some SMTP servers require a username and password authentication before you
 * can use their Server for Sending mail. This is most common with couple of
 * ISP's who provide SMTP Address to Send Mail.
 *
 * This Program gives any example on how to do SMTP Authentication (User and
 * Password verification)
 *
 * This is a free source code and is provided as it is without any warranties
 * and it can be used in any your code for free.
 *
 * Author : Sudhir Ancha
*
 */

import com.vaadin.server.VaadinService;
import com.vaadin.ui.Notification;
import java.io.File;
import java.security.Security;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.*;

public class MyEmailMessanger {

    String emailActivo = "false";
    String smtpHostName = "";
    String smtpHostPort = "";
    String smtpAuthReq = "";
    String smtpAuthUser = "";
    String smtpAuthPasswd = "";
    String smtpMailFrom = "";

    private Properties props = null;
    private final String basePath = VaadinService.getCurrent()
            .getBaseDirectory().getAbsolutePath();

    EnvironmentVars variablesAmbiente = new EnvironmentVars();

    public MyEmailMessanger() {

        this.emailActivo = variablesAmbiente.getEMAIL_ACTIVE();
        this.smtpHostName = variablesAmbiente.getSMTP_HOST_NAME();
        this.smtpHostPort = variablesAmbiente.getSMTP_HOST_PORT();
        this.smtpAuthReq = variablesAmbiente.getSMTP_AUTH_REQ();
        this.smtpAuthUser = variablesAmbiente.getSMTP_AUTH_USER();
        this.smtpAuthPasswd = variablesAmbiente.getSMTP_AUTH_PWD();
        this.smtpMailFrom = variablesAmbiente.getSMTP_FROM();

        //Set the host smtp address
        props = new Properties();

//        Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
        java.security.Security.addProvider(Security.getProvider("SunJSSE"));
        
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", smtpAuthReq);
        props.put("mail.smtp.user", smtpAuthUser);
        props.put("mail.smtp.password", smtpAuthPasswd);
        props.put("mail.smtp.host", smtpHostName);
        props.put("mail.smtp.port", Integer.valueOf(smtpHostPort));
        props.put("mail.smtp.ssl.enable", "true");
//        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.debug", "true");

    }

    /*
    public void EMailInspection() {

           //Set the host smtp address
            props = new Properties();
            
            props.put("mail.smtp.host", smtpHostName);
            props.put("mail.smtp.port", smtpHostPort);
            props.put("mail.smtp.auth", smtpAuthReq);

    }
     */
    /**
     * Envia un correo electronico.
     *
     * @param recipients String[] contiene los destinatarios (TO) del correo.
     * @param subject String contiene el Sujeto del correo.
     * @param message String contiene el cuerpo del mensaje del correo.
     * @throws MessagingException 
     *
     */
    public void postMail(String recipients[], String subject, String message) throws MessagingException {
        boolean debug = false;

        if (this.emailActivo.equals("false")) {
            return;
        }
        Authenticator auth = new MyEmailMessanger.SMTPAuthenticator();
        javax.mail.Session session = javax.mail.Session.getDefaultInstance(props, auth);
        session.setDebug(debug);

        // create a message
        Message msg = new MimeMessage(session);

        // set the from and to address
        InternetAddress addressFrom = new InternetAddress(smtpMailFrom);
        msg.setFrom(addressFrom);

        InternetAddress[] addressTo = new InternetAddress[recipients.length];
        for (int i = 0; i < recipients.length; i++) {
            addressTo[i] = new InternetAddress(recipients[i]);
        }
        msg.setRecipients(Message.RecipientType.TO, addressTo);

        // Setting the Subject and Content Type
        msg.setSubject(subject);

        MimeBodyPart messageBodyPart
                = new MimeBodyPart();

        //fill message
        messageBodyPart.setText(message);

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBodyPart);

        // Put parts in message
        msg.setContent(multipart);
        msg.setSentDate(new Date());

        Transport t = session.getTransport("smtp");
        try {
            t.connect(smtpHostName, Integer.valueOf(smtpHostPort), smtpAuthUser, smtpAuthPasswd);
            t.sendMessage(msg, msg.getAllRecipients());
        } finally {
            t.close();
        }

//        Transport.send(msg);
    }

    /**
     * Envia un correo electronico con un archivo adjunto.
     *
     * @param recipients String[] contiene los destinatarios (TO) del correo.
     * @param subject String contiene el Sujeto del correo.
     * @param message String contiene el cuerpo del mensaje del correo.
     * @param fileAttachment String contiene el nombre completo (incluye ruta)
     * del archivo a adjuntar.
     * @throws MessagingException 
     *
     */
    public void postMail(String recipients[], String subject, String message, String fileAttachment) throws MessagingException {
        boolean debug = false;

        if (this.emailActivo.equals("false")) {
            return;
        }

        Authenticator auth = new MyEmailMessanger.SMTPAuthenticator();
        javax.mail.Session session = javax.mail.Session.getDefaultInstance(props, auth);

        session.setDebug(debug);

        // create a message
        Message msg = new MimeMessage(session);

        // set the from and to address
        InternetAddress addressFrom = new InternetAddress(smtpMailFrom);

        msg.setFrom(addressFrom);

        int cantidad;
        for (cantidad = 0; cantidad < recipients.length; cantidad++) {
            if (recipients[cantidad] == null) {
                break;
            }
        }

        InternetAddress[] addressTo = new InternetAddress[cantidad];
        for (int i = 0; i < recipients.length; i++) {
            if (recipients[i] != null) {
                addressTo[i] = new InternetAddress(recipients[i]);
            }

        }
        msg.setRecipients(Message.RecipientType.TO, addressTo);

        // Setting the Subject and Content Type
        msg.setSubject(subject);

        MimeBodyPart messageBodyPart
                = new MimeBodyPart();

        //fill message
        messageBodyPart.setText(message);

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBodyPart);

        if (!fileAttachment.isEmpty()) {
            // Part two is attachment
            messageBodyPart = new MimeBodyPart();
            DataSource source
                    = new FileDataSource(fileAttachment);
            messageBodyPart.setDataHandler(
                    new DataHandler(source));
            messageBodyPart.setFileName(new File(fileAttachment).getName());
            multipart.addBodyPart(messageBodyPart);
        }

        // Put parts in message
        msg.setContent(multipart);
        msg.setSentDate(new Date());

        Transport t = session.getTransport("smtp");
        try {
            t.connect(smtpHostName, Integer.valueOf(smtpHostPort), smtpAuthUser, smtpAuthPasswd);
            t.sendMessage(msg, msg.getAllRecipients());
        } finally {
            t.close();
        }

//        Transport.send(msg);
    }

    /**
     * SimpleAuthenticator is used to do simple authentication when the SMTP
     * server requires it.
    *
     */
    private class SMTPAuthenticator extends javax.mail.Authenticator {

        @Override
        public PasswordAuthentication getPasswordAuthentication() {
            String username = smtpAuthUser;
            String password = smtpAuthPasswd;
            return new PasswordAuthentication(username, password);
        }
    }

    public void avisoVentaNueva(String gestionId, String eMailTo, String gestor, String nombreCliente) {

        String recipients[] = {eMailTo};

        String asunto = "NisaGV -- Control de Gestiones de Venta -- Cliente:" + nombreCliente;

        String body = "El sistema de control de gestiones de ventas NisaGV,";
        body += "te informa que hay una nueva Gestion.\n";
        body += "\nDatos importantes: \n";
        body += "\tCaso No.      = " + gestionId;
        body += "\n\tCliente          = " + nombreCliente.toUpperCase();
        body += "\n\tIngresada  por   = " + gestor + " Hoy : " + new Utileria().getFechaHoraFormateada();
        body += "\n\n";
        body += "-*-Enviado automaticamente por Sistema NisaGV-*-";
        body += "\n--Por codificacion del correo, se han suprimido los acentos y caracteres especiales intencionalmente--";

        try {
            postMail(recipients, asunto, body, basePath + "/WEB-INF/plantillas/Brochure-Siena.pdf");
        } catch (MessagingException ex) {
            Logger.getLogger(MyEmailMessanger.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void avisoNuevoSeguimiento(String gestionId, String[] emailsTo, String gestor, String nombreCliente,
            String estatus, String ubicacion, String seguimiento) {

        String asunto = "NisaGV -- Control de Gestiones de Venta -- Cliente : " + nombreCliente;

        String body = "El sistema de control de gestiones de ventas  NisaGV,";
        body += "te informa que hay un nuevo seguimiento de una venta de un cliente.\n";
        body += "\nDatos importantes: \n";
        body += "\tCaso No.      = " + gestionId;
        body += "\n\tCliente       = " + nombreCliente.toUpperCase();
        body += "\n\tEstado        = " + estatus;
//            body += "\n\tUbicacion     = " + ubicacion;
        body += "\n\tObservacion   = " + seguimiento.toUpperCase();
//            body += "\n\tCreado por    = " + mainWindow.getSessionInformation().getStrUserName() + " Hoy : " + new Utileria().getFechaHoraFormateada();
        body += "\n\n";
        body += "-*-Enviado automaticamente por Sistema NisaGV-*-";
        body += "\n--Por codificacion del correo, se han suprimido los acentos y caracteres especiales intencionalmente--";

        try {
            MyEmailMessanger eMail = new MyEmailMessanger();
            eMail.postMail(emailsTo, "NisaGV", body);

        } catch (MessagingException ex2) {
            Logger.getLogger(MyEmailMessanger.class.getName()).log(Level.SEVERE, null, ex2);
            Notification.show("Ocurrió un error al enviar correo electrónico a : " + emailsTo, Notification.Type.ERROR_MESSAGE);
        }

    }

}
