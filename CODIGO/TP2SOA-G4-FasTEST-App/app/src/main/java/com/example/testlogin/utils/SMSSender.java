package com.example.testlogin.utils;

import android.telephony.SmsManager;

/**
 * Se encarga de enviar SMS a los contactos de emergencia configurados
 */
class SMSSender {
    static void sendSMS(String phoneNumber, String message){
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNumber, null, message, null, null);
    }
}
