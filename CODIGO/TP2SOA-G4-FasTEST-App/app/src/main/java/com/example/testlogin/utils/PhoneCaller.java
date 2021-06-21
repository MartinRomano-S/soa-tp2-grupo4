package com.example.testlogin.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import androidx.core.app.ActivityCompat;

/**
 * Clase que se encarga de validar si la app tiene
 * permisos para hacer llamados y en caso de tenerlo
 * lo hace.
 *
 * Sirve para el llamado al 148 en caso de presentar síntomas
 * de covid-19
 */
public class PhoneCaller {
    public static void makePhoneCall(Activity activity, String number){
        Intent call = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));

        if(!Configuration.checkPermission(activity, Manifest.permission.CALL_PHONE))
            ActivityCompat.requestPermissions(activity, new String[] {Manifest.permission.CALL_PHONE}, 1);

        if(Configuration.checkPermission(activity, Manifest.permission.CALL_PHONE))
            activity.startActivity(call);
    }
}
