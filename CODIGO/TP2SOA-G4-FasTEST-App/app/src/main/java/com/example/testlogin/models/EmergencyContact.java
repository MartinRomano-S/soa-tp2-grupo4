package com.example.testlogin.models;

import com.example.testlogin.interfaces.JSONable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * EmergencyContact
 * Objeto model que representa un contacto de emergencia
 * estos contactos son ingresados por el usuario y sirven
 * para enviarles notificaciones por SMS.
 */
public class EmergencyContact implements JSONable {

    private int id;
    private String name;
    private int phoneNumber;

    public EmergencyContact() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(int phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();

        try {
            json.put("id", getId());
            json.put("name", getName());
            json.put("phoneNumber", getPhoneNumber());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }

    @Override
    public void getFromJSON(JSONObject jsonObject) {
        try {
            setId(jsonObject.getInt("id"));
            setName(jsonObject.getString("name"));
            setPhoneNumber(jsonObject.getInt("phoneNumber"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
