package com.simpletecno.recobrapp.utileria;

import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Clase Utileria,  contiene varios metodos utiles para el programa, como la obtencion de fechas en formatos especiales.
 *
 * @author (jaguirre)
 */
public class Utileria {

    private String referencia = "";

    /**
     *
     */
    public Utileria() {
        java.util.GregorianCalendar today = new java.util.GregorianCalendar();
        int x = new Double(Math.random() * 999).intValue();

        referencia = String.valueOf(today.get(java.util.GregorianCalendar.YEAR)).substring(2);
        referencia += String.format("%03d", today.get(java.util.GregorianCalendar.DAY_OF_YEAR));
        referencia += String.format("%02d", today.get(java.util.GregorianCalendar.HOUR_OF_DAY));
        referencia += String.format("%02d", today.get(java.util.GregorianCalendar.MINUTE));
        referencia += String.format("%03d", x);
    }

    public Utileria(int inicial) {
        java.util.GregorianCalendar today = new java.util.GregorianCalendar();
        int x = new Double(Math.random() * inicial).intValue();

        referencia = String.valueOf(today.get(java.util.GregorianCalendar.YEAR)).substring(2);
        referencia += String.format("%03d", today.get(java.util.GregorianCalendar.DAY_OF_YEAR));
        referencia += String.format("%02d", today.get(java.util.GregorianCalendar.HOUR_OF_DAY));
        referencia += String.format("%02d", today.get(java.util.GregorianCalendar.MINUTE));
        referencia += String.format("%03d", x);
    }

    /**
     * Get the value of referencia
     *
     * @return the value of referencia
     */
    public String getReferencia() {
        return referencia;
    }

    public String getAutorizacion() {
        java.util.GregorianCalendar today = new java.util.GregorianCalendar();

        String autorizacion = String.valueOf(today.get(java.util.GregorianCalendar.YEAR)).substring(3, 4);
        autorizacion += rellenaString(String.valueOf(today.get(java.util.GregorianCalendar.DAY_OF_YEAR)), '0', 3, 1);
        autorizacion += String.valueOf(today.get(java.util.GregorianCalendar.HOUR_OF_DAY));
        autorizacion += String.valueOf(today.get(java.util.GregorianCalendar.MINUTE));

        return autorizacion;
    }

    /**
     * Retorna la fecha del sistema en formato yyyy/mm/dd
     *
     * @return la fecha del sistema en formato yyyy/mm/dd
     */
    public String getFecha() {
        java.util.GregorianCalendar today = new java.util.GregorianCalendar();

        String fecha;
        fecha = String.format("%04d", today.get(java.util.GregorianCalendar.YEAR));
        fecha += "/";
        fecha += String.format("%02d", (today.get(java.util.GregorianCalendar.MONTH) + 1));
        fecha += "/";
        fecha += String.format("%02d", today.get(java.util.GregorianCalendar.DAY_OF_MONTH));

        return fecha;
    }


    /**
     * Retorna un string dd/mm/yyyy conteniendo el valor dateToConvert enviado dd/mm/yy
     *
     * @return la fecha del sistema en formato dd/mm/yy
     */
    public String getFecha(Date dateToConvert) {
        java.util.GregorianCalendar today = new java.util.GregorianCalendar();
        today.setTime(dateToConvert);

        String fecha;
        fecha = String.format("%02d", today.get(java.util.GregorianCalendar.DAY_OF_MONTH));
        fecha += "/";
        fecha += String.format("%02d", (today.get(java.util.GregorianCalendar.MONTH) + 1));
        fecha += "/";
        fecha += String.format("%04d", today.get(java.util.GregorianCalendar.YEAR));

        return fecha;
    }

    /**
     * Retorna un string mm/dd/yyyy conteniendo el valor dateToConvert enviado mm/dd/yyyy
     *
     * @return la fecha del sistema en formato mm/dd/yyyy
     */
    public String getFecha_mmddyyy() {
        java.util.GregorianCalendar today = new java.util.GregorianCalendar();
        today.setTime(new Date());

        String fecha = "";
        fecha = String.format("%02d", (today.get(java.util.GregorianCalendar.MONTH) + 1));
        fecha += "/";
        fecha += String.format("%02d", today.get(java.util.GregorianCalendar.DAY_OF_MONTH));
        fecha += "/";
        fecha += String.format("%04d", today.get(java.util.GregorianCalendar.YEAR));

        return fecha;
    }

    /**
     * Retorna un string yyyy/mm/dd conteniendo el valor dateToConvert enviado dd/mm/yy
     *
     * @return la fecha en formato yyyy/mm/dd
     */
    public String getFechaYYYYMMDD(Date dateToConvert) {
        java.util.GregorianCalendar today = new java.util.GregorianCalendar();
        today.setTime(dateToConvert);

        String fecha;
        fecha = String.format("%04d", today.get(java.util.GregorianCalendar.YEAR));
        fecha += "/";
        fecha += String.format("%02d", (today.get(java.util.GregorianCalendar.MONTH) + 1));
        fecha += "/";
        fecha += String.format("%02d", today.get(java.util.GregorianCalendar.DAY_OF_MONTH));

        return fecha;
    }

    public String getFechaLetras(String fechaNormal) {


        System.out.println("fecha normal "  + fechaNormal);

        String fechaConvertida = "";

        String anio  = fechaNormal.substring(0,4);
        String numeroMes  = fechaNormal.substring(5,7);
        String dia  = fechaNormal.substring(8,10);

        System.out.println("ANIO " +anio);
        System.out.println("numero mes " + numeroMes);
        System.out.println("numero dia "  + dia);


        String mes = "";

        if (numeroMes.equals("01")) {
            mes = "Enero";
        } else if (numeroMes.equals("02")) {
            mes = "Febrero";
        } else if(numeroMes.equals("03")){
            mes = "Marzo";
        }else if(numeroMes.equals("04")){
            mes = "Abril";
        }else if(numeroMes.equals("05")){
            mes = "Mayo";
        }else if(numeroMes.equals("06")){
            mes = "Junio";
        }else if(numeroMes.equals("07")){
            mes = "Julio";
        }else if(numeroMes.equals("08")){
            mes = "Agosto";
        }else if(numeroMes.equals("09")){
            mes = "Septiembre";
        }else if(numeroMes.equals("10")){
            mes = "Octubre";
        }else if(numeroMes.equals("11")){
            mes = "Noviembre";
        }else if(numeroMes.equals("12")){
            mes = "Diciembre";
        }

        fechaConvertida = dia + " de "+ mes + " del " + anio;

        return fechaConvertida;
    }

    /**
     * Retorna un string yyyy-mm-dd conteniendo el valor dateToConvert enviado dd-mm-yy
     *
     * @param dateToConvert
     * @return la fecha en formato yyyy-mm-dd
     */
    public static String getFechaYYYYMMDD_1(Date dateToConvert) {
        java.util.GregorianCalendar today = new java.util.GregorianCalendar();
        today.setTime(dateToConvert);

        String fecha;
        fecha = String.format("%04d", today.get(java.util.GregorianCalendar.YEAR));
        fecha += "-";
        fecha += String.format("%02d", (today.get(java.util.GregorianCalendar.MONTH) + 1));
        fecha += "-";
        fecha += String.format("%02d", today.get(java.util.GregorianCalendar.DAY_OF_MONTH));

        return fecha;
    }

    /**
     * Retorna un string yyyy-mm-dd HH:MM:SS conteniendo el valor dateToConvert
     *
     * @param dateToConvert
     * @return la fecha en formato yyyy-mm-dd HH:MM:SS
     */
    public static String getFechaYYYYMMDDHHMMSS(Date dateToConvert) {
        java.util.GregorianCalendar today = new java.util.GregorianCalendar();
        today.setTime(dateToConvert);

        String fechaHora;
        fechaHora = String.format("%04d", today.get(java.util.GregorianCalendar.YEAR));
        fechaHora += "-";
        fechaHora += String.format("%02d", (today.get(java.util.GregorianCalendar.MONTH) + 1));
        fechaHora += "-";
        fechaHora += String.format("%02d", today.get(java.util.GregorianCalendar.DAY_OF_MONTH));
        fechaHora += " ";
        fechaHora += String.format("%02d", today.get(java.util.GregorianCalendar.HOUR_OF_DAY));
        fechaHora += ":";
        fechaHora += String.format("%02d", today.get(java.util.GregorianCalendar.MINUTE));
        fechaHora += ":";
        fechaHora += String.format("%02d", today.get(java.util.GregorianCalendar.SECOND));
        System.out.println("fechaHora=" + fechaHora);
        return fechaHora;
    }

    /**
     * Para la fecha de nacimiento
     *
     * @return campo Date con la fecha 1990-01-01
     */
    public Date getPastDate() {
        java.util.GregorianCalendar today = new java.util.GregorianCalendar();
        today.set(1990, 1, 1);

        return today.getTime();
    }

    /**
     * Retorna la hora del sistema en formato HHmmss
     *
     * @return la hora del sistema en formato HHmmss
     */
    public String getHora() {
        java.util.GregorianCalendar today = new java.util.GregorianCalendar();

        String hora;
        hora = String.format("%02d", today.get(java.util.GregorianCalendar.HOUR_OF_DAY));
        hora += String.format("%02d", today.get(java.util.GregorianCalendar.MINUTE));
        hora += String.format("%02d", today.get(java.util.GregorianCalendar.SECOND));

        return hora;
    }

    /**
     * Retorna la hora del sistema en formato HH:mm:ss
     *
     * @return la hora del sistema en formato HH:mm:ss
     */
    public static String getHora_1(Date dateToConvert) {
        java.util.GregorianCalendar today = new java.util.GregorianCalendar();
        today.setTime(dateToConvert);

        String hora;
        hora = String.format("%02d", today.get(java.util.GregorianCalendar.HOUR_OF_DAY));
        hora += ":";
        hora += String.format("%02d", today.get(java.util.GregorianCalendar.MINUTE));
        hora += ":";
        hora += String.format("%02d", today.get(java.util.GregorianCalendar.SECOND));

        return hora;
    }

    /**
     * Retorna la fecha y la hora del sistema en formato yyyy/mm/yy HH:mm:ss
     *
     * @return la fecha y la hora del sistema en formato yyyy/mm/yy HH:mm:ss
     */
    public String getFechaHoraFormateada() {
        java.util.GregorianCalendar today = new java.util.GregorianCalendar();

        String fechaHora;
        fechaHora = String.format("%04d", today.get(java.util.GregorianCalendar.YEAR));
        fechaHora += "/";
        fechaHora += String.format("%02d", (today.get(java.util.GregorianCalendar.MONTH) + 1));
        fechaHora += "/";
        fechaHora += String.format("%02d", today.get(java.util.GregorianCalendar.DAY_OF_MONTH));
        fechaHora += " ";
        fechaHora += String.format("%02d", today.get(java.util.GregorianCalendar.HOUR_OF_DAY));
        fechaHora += ":";
        fechaHora += String.format("%02d", today.get(java.util.GregorianCalendar.MINUTE));
        fechaHora += ":";
        fechaHora += String.format("%02d", today.get(java.util.GregorianCalendar.SECOND));

        return fechaHora;
    }

    /**
     * Retorna la fecha y la hora del sistema en formato yyyymmddHHmmss
     *
     * @return la fecha y hora del sistema en formato yyyymmddHHmmss
     */
    public String getFechaHoraSinFormato() {
        java.util.GregorianCalendar today = new java.util.GregorianCalendar();

        String fechaHora;
        fechaHora = String.format("%04d", today.get(java.util.GregorianCalendar.YEAR));
        fechaHora += String.format("%02d", (today.get(java.util.GregorianCalendar.MONTH) + 1));
        fechaHora += String.format("%02d", today.get(java.util.GregorianCalendar.DAY_OF_MONTH));
        fechaHora += String.format("%02d", today.get(java.util.GregorianCalendar.HOUR_OF_DAY));
        fechaHora += String.format("%02d", today.get(java.util.GregorianCalendar.MINUTE));
        fechaHora += String.format("%02d", today.get(java.util.GregorianCalendar.SECOND));

        return fechaHora;
    }

    /**
     * Retorna la fecha y la hora del sistema en formato yyyymmddHHmmss
     *
     * @return la fecha y hora del sistema en formato yyyymmddHHmmss
     */
    public String getFechaHoraSinFormato(Date laFecha) {
        java.util.GregorianCalendar today = new java.util.GregorianCalendar();
        today.setTime(laFecha);

        String fechaHora;
        fechaHora = String.format("%04d", today.get(java.util.GregorianCalendar.YEAR));
        fechaHora += String.format("%02d", (today.get(java.util.GregorianCalendar.MONTH) + 1));
        fechaHora += String.format("%02d", today.get(java.util.GregorianCalendar.DAY_OF_MONTH));
        fechaHora += String.format("%02d", today.get(java.util.GregorianCalendar.HOUR_OF_DAY));
        fechaHora += String.format("%02d", today.get(java.util.GregorianCalendar.MINUTE));
        fechaHora += String.format("%02d", today.get(java.util.GregorianCalendar.SECOND));

        return fechaHora;
    }

    /**
     * Retorna un string mm/yyyy conteniendo el valor dateToConvert enviado mm/dd/yyyy
     *
     * @return la fecha del sistema en formato mm/yyyy
     */
    public String getFecha_mmyyy(Date laFecha) {
        java.util.GregorianCalendar today = new java.util.GregorianCalendar();
        today.setTime(laFecha);

        String fecha;
        fecha = String.format("%02d", (today.get(java.util.GregorianCalendar.MONTH) + 1));
        fecha += "/";
        fecha += String.format("%04d", today.get(java.util.GregorianCalendar.YEAR));

        return fecha;
    }

    public static Date getPrimerDiaDelMes() {
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.getActualMinimum(Calendar.DAY_OF_MONTH),
                cal.getMinimum(Calendar.HOUR_OF_DAY),
                cal.getMinimum(Calendar.MINUTE),
                cal.getMinimum(Calendar.SECOND));
        return cal.getTime();
    }

    public static Date getUltimoDiaDelMes() {
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.getActualMaximum(Calendar.DAY_OF_MONTH),
                cal.getMaximum(Calendar.HOUR_OF_DAY),
                cal.getMaximum(Calendar.MINUTE),
                cal.getMaximum(Calendar.SECOND));
        return cal.getTime();
    }

    public static Date getPrimerDiaDelAnio() {
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR),
                Calendar.JANUARY,
                cal.getActualMinimum(Calendar.DAY_OF_MONTH),
                cal.getMinimum(Calendar.HOUR_OF_DAY),
                cal.getMinimum(Calendar.MINUTE),
                cal.getMinimum(Calendar.SECOND)
        );
        return cal.getTime();
    }

    /**
     * Retorna la ip local
     *
     * @return la ip local
     */
    public String getLocalIpAddress() {
        String ipAddress = "";
        try {
            InetAddress ownIP = InetAddress.getLocalHost();
            ipAddress = ownIP.getHostAddress();
            //System.out.println("Identificacion de NODO " + ipAddress);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ipAddress;
    }

    /**
     * Rellena una cadena con el caracter de relleno,  de una longitud dada y hacia la izquierda o hacia la derecha.
     *
     * @param cadena          String con la cadena original
     * @param caracterRelleno char[1]  contiene el caracter de relleno
     * @param longitud        int largo del relleno o de la cadena final...
     * @param lado            int 0=izquierda,  1=derecha
     * @return String nueva cadena de caracteres.
     */
    public String rellenaString(String cadena, char caracterRelleno, int longitud, int lado) {
        String salida = cadena;

        //Si tiene la misma longitud la devuelve
        if (salida.length() == longitud) return salida;
        //Si es mas larga la trunca
        if (salida.length() > longitud) return salida.substring(0, longitud);
        //Si es menor, entonces modificamos
        if (salida.length() < longitud) {
            if (lado == 1) {
                //	Rellenar por la derecha
                for (int k = cadena.length(); k < longitud; k++)
                    salida = salida + caracterRelleno;
            } else {
                if (lado == 0) {
                    //	Rellenar por la izquierda
                    for (int k = cadena.length(); k < longitud; k++)
                        salida = caracterRelleno + salida;
                } else {
                    return "Lado es incorrecto";
                }
            }

            return salida;
        }
        return cadena;
    }

    /**
     * Escribe en la salida standard de JAVA o Tomcat.
     *
     * @param sessionID
     * @param eFace
     * @param textoLog
     */
    public void escribirLog(String sessionID, String eFace, String textoLog) {
        String sNuevoTexto;
        byte[] buf = new byte[1024];
        Date fechaActual;
        SimpleDateFormat formatoFechaHora;
        String fechaHoraActual;

        fechaActual = new Date();
        formatoFechaHora = new SimpleDateFormat("yyyyMMdd_HHmmss");
        fechaHoraActual = formatoFechaHora.format(fechaActual);
        String nuevoTexto = fechaHoraActual + "TellerServerWS v 1.0 (" + sessionID + " " + eFace + ") [" + textoLog + "]";

        System.out.println(nuevoTexto);

/**
 if(sMensaje.contains("-2|ERROR") || sMensaje.contains("-1|ERROR") || sMensaje.contains("INSERT INTO")) {
 EnviarCorreo enviarCorreo = new EnviarCorreo(sEmailSMTP, sEmailFrom,  sEmailFrom, "Error en SwitchTelepin", sNuevoTexto);
 System.out.println("EMAIL:" + sEmailSMTP + " " + sEmailFrom + " "  + sMensaje);

 enviarCorreo.send();
 }
 **/

/**
 try {

 MulticastSocket mServer =  new MulticastSocket();

 InetAddress group = InetAddress.getByName("225.4.5.6");

 DatagramPacket packet;

 buf = sNuevoTexto.getBytes();
 packet = new DatagramPacket(buf, buf.length, group, 4459);

 mServer.send(packet);

 }
 catch(Exception ex1) {
 System.out.println("ERROR (EscribirLog(()): " + ex1.getMessage().trim());
 }
 *****/
    }


    /**
     * Remueve caracteres especiales newline, carriage return, tab y white space,  de la cadena dada.
     *
     * @param toBeEscaped string to escape
     * @return String nueva cadena de carecteres sin los caracteres especiales.
     **/
    public static String removeFormattingCharacters(final String toBeEscaped) {
        StringBuilder escapedBuffer = new StringBuilder();
        for (int i = 0; i < toBeEscaped.length(); i++) {
            if ((toBeEscaped.charAt(i) != '\n') && (toBeEscaped.charAt(i) != '\r')
                    && (toBeEscaped.charAt(i) != '\t')) {
                escapedBuffer.append(toBeEscaped.charAt(i));
            }
        }
        String s = escapedBuffer.toString().trim();
        return s;//
        // Strings.replaceSubString(s, "\"", "")
    }

    /**
     * Retorna la fecha en formato dd/mm/yyyy.
     * <p>
     * //@param Date dateToConvert
     *
     * @return String dd/mm/yyyy .
     **/
    public static String getFechaDDMMYYYY(Date dateToConvert) {
        java.util.GregorianCalendar today = new java.util.GregorianCalendar();
        today.setTime(dateToConvert);

        String fecha;
        fecha = String.format("%02d", today.get(java.util.GregorianCalendar.DAY_OF_MONTH));
        fecha += "/";
        fecha += String.format("%02d", (today.get(java.util.GregorianCalendar.MONTH) + 1));
        fecha += "/";
        fecha += String.format("%04d", today.get(java.util.GregorianCalendar.YEAR));

        return fecha;
    }

    /**
     * Retorna la fecha y la hora del sistema en formato dd/mm/yyyy hh:mm
     *
     * @return la fecha y hora del sistema en formato dd/mm/yyyy hh:mm
     */
    public static String getFechaDDMMYYYY_HHMM() {
        java.util.GregorianCalendar today = new java.util.GregorianCalendar();
        today.setTime(new java.util.Date());

        String fechaHora = "";
        fechaHora = String.format("%02d", today.get(java.util.GregorianCalendar.DAY_OF_MONTH));
        fechaHora += "/";
        fechaHora += String.format("%02d", (today.get(java.util.GregorianCalendar.MONTH) + 1));
        fechaHora += "/";
        fechaHora += String.format("%04d", today.get(java.util.GregorianCalendar.YEAR));
        fechaHora += " ";
        fechaHora += String.format("%02d", today.get(java.util.GregorianCalendar.HOUR_OF_DAY));
        fechaHora += ":";
        fechaHora += String.format("%02d", today.get(java.util.GregorianCalendar.MINUTE));

        return fechaHora;
    }

    /**
     * Retorna la fecha y la hora del sistema en formato dd/mm/yyyy hh:mm
     * // @param DateTime
     *
     * @return la fecha y hora del sistema en formato dd/mm/yyyy hh:mm
     */
    public static String getFechaDDMMYYYY_HHMM_2(Date dateTime) {
        java.util.GregorianCalendar today = new java.util.GregorianCalendar();
        today.setTime(dateTime);

        String fechaHora = "";
        fechaHora = String.format("%02d", today.get(java.util.GregorianCalendar.DAY_OF_MONTH));
        fechaHora += "/";
        fechaHora += String.format("%02d", (today.get(java.util.GregorianCalendar.MONTH) + 1));
        fechaHora += "/";
        fechaHora += String.format("%04d", today.get(java.util.GregorianCalendar.YEAR));
        fechaHora += " ";
        fechaHora += String.format("%02d", today.get(java.util.GregorianCalendar.HOUR_OF_DAY));
        fechaHora += ":";
        fechaHora += String.format("%02d", today.get(java.util.GregorianCalendar.MINUTE));

        return fechaHora;
    }

    /**
     * Retorna la fecha en formato mm/yyyy
     *
     * @param dateTime fecha a ser convertida
     * @return String la fecha en formato mm/yyyy
     */
    public static String getFechaMMYYYY(Date dateTime) {
        java.util.GregorianCalendar today = new java.util.GregorianCalendar();
        today.setTime(dateTime);

        String fecha = "";
        fecha += String.format("%02d", (today.get(java.util.GregorianCalendar.MONTH) + 1));
        fecha += "/";
        fecha += String.format("%04d", today.get(java.util.GregorianCalendar.YEAR));

        return fecha;
    }
}
