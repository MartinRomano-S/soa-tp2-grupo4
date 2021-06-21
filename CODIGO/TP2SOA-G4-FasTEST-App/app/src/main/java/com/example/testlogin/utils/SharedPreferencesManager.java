package com.example.testlogin.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.example.testlogin.R;
import com.example.testlogin.models.EmergencyContact;
import com.example.testlogin.models.Event;
import com.example.testlogin.models.Token;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * SharedPreferencesManager
 *
 * Gestiona el acceso para la consulta y guardado de información en el dispositivo
 *
 * La manejamos como un Singleton de acceso a las preferences con métodos sincronizados
 * para evitar fallos al guardar
 */
public class SharedPreferencesManager {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor sharedPrefEditor;
    private static SharedPreferencesManager singletonInstance;

    private static final String SP_NAMESPACE = "FasTEST-SharedPreferences";
    private static final String SP_DEFAULT_VALUE = "";
    private static final String SP_VERIFICATION_CODE = "currentVerificationCode";
    private static final String SP_EMERGENCY_CONTACT_LIST = "emergencyContacts";
    private static final String SP_LAST_LOGIN_DATE = "lastLoginDate";
    private static final String SP_CURRENT_TOKEN_INFO = "currentTokenInfo";
    private static final String SP_EVENT_LIST = "eventList";
    private static final String SP_LAST_TEST_RESULT = "lastTestResultObj";

    @SuppressLint("CommitPrefEdits")
    private SharedPreferencesManager(Context context) {
        this.sharedPreferences = context.getSharedPreferences(SP_NAMESPACE, Context.MODE_PRIVATE);
        this.sharedPrefEditor = sharedPreferences.edit();
    }

    public static SharedPreferencesManager getInstance(Context context) {
        if (singletonInstance == null)
            singletonInstance = new SharedPreferencesManager(context);

        return singletonInstance;
    }

    public synchronized void saveCurrentVerificationCode(String currentVerificationCode) {
        sharedPrefEditor.putString(SP_VERIFICATION_CODE, currentVerificationCode);
        sharedPrefEditor.apply();
    }

    public String getCurrentVerificationCode() {
        return sharedPreferences.getString(SP_VERIFICATION_CODE, SP_DEFAULT_VALUE);
    }

    public synchronized void saveEmergencyContactList(List<EmergencyContact> emergencyContactList) {

        JSONArray jsonArray = new JSONArray();

        for(EmergencyContact ec : emergencyContactList)
            jsonArray.put(ec.toJSON());

        sharedPrefEditor.putString(SP_EMERGENCY_CONTACT_LIST, jsonArray.toString());
        sharedPrefEditor.apply();
    }

    public List<EmergencyContact> getEmergencyContactList() throws JSONException {
        JSONArray jsonArray = new JSONArray(sharedPreferences.getString(SP_EMERGENCY_CONTACT_LIST, new JSONArray().toString()));
        List<EmergencyContact> list = new ArrayList<>();

        for(int i = 0; i < jsonArray.length(); i++) {
            EmergencyContact ec = new EmergencyContact();
            ec.getFromJSON(jsonArray.getJSONObject(i));
            list.add(ec);
        }

        return list;
    }

    public synchronized void saveEvent(Event event) throws JSONException {

        JSONArray jsonArray = new JSONArray();
        List<Event> eventList = getEventList();

        eventList.add(event);

        for(Event ev : eventList)
            jsonArray.put(ev.toJSON());

        sharedPrefEditor.putString(SP_EVENT_LIST, jsonArray.toString());
        sharedPrefEditor.apply();
    }

    public List<Event> getEventList() throws JSONException {
        JSONArray jsonArray = new JSONArray(sharedPreferences.getString(SP_EVENT_LIST, new JSONArray().toString()));
        List<Event> list = new ArrayList<>();

        for(int i = 0; i < jsonArray.length(); i++) {
            Event ev = new Event();
            ev.getFromJSON(jsonArray.getJSONObject(i));
            list.add(ev);
        }

        return list;
    }

    public synchronized void saveTokenInfo(Token token) {

        JSONObject jsonObject = token.toJSON();

        sharedPrefEditor.putString(SP_CURRENT_TOKEN_INFO, jsonObject.toString());
        sharedPrefEditor.apply();
    }

    public Token getTokenInfo() throws JSONException {
        JSONObject jsonObject = new JSONObject(sharedPreferences.getString(SP_CURRENT_TOKEN_INFO, SP_DEFAULT_VALUE));
        Token token = new Token();
        token.getFromJSON(jsonObject);

        return token;
    }

    public synchronized void saveLastTestResult(boolean testResult) throws JSONException {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("lastTestResult", testResult);
        jsonObject.put("lastTestResultDate", new Date().getTime());

        sharedPrefEditor.putString(SP_LAST_TEST_RESULT, jsonObject.toString());
        sharedPrefEditor.apply();
    }

    public JSONObject getLastTestResult() throws JSONException {
        return new JSONObject(sharedPreferences.getString(SP_LAST_TEST_RESULT, SP_DEFAULT_VALUE));
    }

    public void sendMessageToEmergencyContactList(Activity activity) throws JSONException{

        if(!Configuration.checkPermission(activity, Manifest.permission.SEND_SMS))
            ActivityCompat.requestPermissions(activity, new String[] {Manifest.permission.SEND_SMS}, 1);

        if(Configuration.checkPermission(activity, Manifest.permission.SEND_SMS)) {

            JSONArray jsonArray = new JSONArray(sharedPreferences.getString(SP_EMERGENCY_CONTACT_LIST, new JSONArray().toString()));
            String message = activity.getResources().getString(R.string.smsMessage);
            for (int i = 0; i < jsonArray.length(); i++) {
                EmergencyContact ec = new EmergencyContact();
                ec.getFromJSON(jsonArray.getJSONObject(i));
                SMSSender.sendSMS("+549" + ec.getPhoneNumber(), message);
            }
            Toast.makeText(activity, "Mensaje enviado a " + jsonArray.length() + " contacto/s de emergencia.", Toast.LENGTH_SHORT).show();
        }
    }

    public synchronized void saveLastLoginDate(long dateInMillis) {
        sharedPrefEditor.putLong(SP_LAST_LOGIN_DATE, dateInMillis);
        sharedPrefEditor.apply();
    }

    public void delete() {
        sharedPrefEditor.clear();
        sharedPrefEditor.apply();
    }
}
