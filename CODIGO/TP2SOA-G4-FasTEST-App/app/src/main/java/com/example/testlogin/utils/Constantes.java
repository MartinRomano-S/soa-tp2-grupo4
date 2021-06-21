package com.example.testlogin.utils;

/**
 * Clase con algunas constantes para el test de COVID-19
 * y los eventos disponibles para registrar
 */
public class Constantes {
    public static final double UMBRAL_TEMPERATURA_CORPORAL = 37.2;
    public static final double TEMPERATURA_MINIMA_CUERPO_HUMANO = 36;
    public static final double TEMPERATURA_MAXIMA_CUERPO_HUMANO = 42;
    public static final String TELEFONO_ATENCION_COVID = "148";
    public static final String PREFIJO_TELEFONO_11 = "11";
    public static final String PREFIJO_TELEFONO_15 = "15";
    public static final int LONGITUD_PREFIJO = 2;
    public static final int LONGITUD_TELEFONO = 10;

    public enum EVENT_TYPES{PROXIMITY, SHAKE}
}
