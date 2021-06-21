package com.example.testlogin.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;

import androidx.core.content.ContextCompat;

import com.example.testlogin.R;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * Configuration
 *
 * Clase con algunas utilidades usadas por la app
 * y algunas constantes necesarias.
 */
public class Configuration {

    //API SOA
    public static final String API_ENVIRONMENT = "PROD";
    public static final int COMMISSION = 3900;
    public static final int GROUP = 4;
    public static final String API_BASE_URL = "http://so-unlam.net.ar/api/api/";
    public static final String API_EVENT_ENDPOINT = API_BASE_URL + "event";
    public static final int REQUEST_READ_TIMEOUT = 10000;
    public static final int REQUEST_CONNECTION_TIMEOUT = 20000;
    public static final int MINIMUM_PASSWORD_LENGTH = 8;
    public static final int MAX_DNI_LENGTH = 8;
    public static final String DNI_PATTERN = "[0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]";
    public static final int TOKEN_REFRESH_TIME = 28;

    //Con esta cuenta se realiza el envío de mail.
    //Prohibido robarla (?)
    public static final String VERIFICATION_EMAIL = "fastestappsoa@gmail.com";
    public static final String VERIFICATION_PASSWORD = "prueba123";

    //Constantes para generación de código para doble factor
    private static final int RANDOM_CODE_NUM_BITS = 30;
    private static final int RANDOM_CODE_STRING_RADIX = 32;

    /**
     * Método para validar si la app tiene permisos específicos
     * @param c: Contexto. La activity que hizo el llamado
     * @param permission: Permiso que se quiere validar
     * @return true o false según si tiene permiso o no
     */
    public static boolean checkPermission(Context c, String permission) {
        int check = ContextCompat.checkSelfPermission(c, permission);
        return (check == PackageManager.PERMISSION_GRANTED);
    }

    /**
     * Método para validar si el dispositivo está conectado a
     * WI-FI, Ethernet o redes móviles (3G, 4G, etc)
     * @param c: Contexto. La activity que hizo el llamado
     * @return true o false según si está conectado o no
     */
    public static boolean isNetworkConnected(Context c) {
        ConnectivityManager connectivityManager = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);

        if(connectivityManager != null) {
            Network network = connectivityManager.getActiveNetwork();

            if (network == null) return false;

            NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(network);
            return networkCapabilities != null && (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                    || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
                    || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR));
        }
        return false;
    }

    /**
     * Método para mostrar un modal genérico con título y mensaje
     * @param c: Contexto. La activity que hizo el llamado
     * @param title: Título del modal
     * @param msg: Mensaje del modal
     */
    public static void showModalMessage(Context c, String title, String msg) {
        AlertDialog.Builder dialog;
        dialog = new AlertDialog.Builder(c);
        dialog.setTitle(title);
        dialog.setMessage(msg);

        dialog.setPositiveButton(c.getString(R.string.acceptButton), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {}
        });

        dialog.create().show();
    }

    /**
     * Método que se encarga de generar un código aleatorio para
     * la autenticación de doble factor
     * @return código aleatorio
     */
    public static String generateRandomCode() {
        SecureRandom random = new SecureRandom();

        /*
        Esta línea se encarga de generar un número aleatorio para
        la autenticación de doble factor
         */
        return new BigInteger(RANDOM_CODE_NUM_BITS, random).toString(RANDOM_CODE_STRING_RADIX);
    }

    /**
     * Método para validar si una cadena no está vacía o nula
     * @param s: Cadena a validar
     * @return true o false según si no está vacía o si lo está
     */
    public static boolean isNotNullOrEmpty(String s) {
        return s != null && !s.equals("") && !s.isEmpty();
    }
}
