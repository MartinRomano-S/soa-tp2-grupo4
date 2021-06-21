package com.example.testlogin.models;

import com.example.testlogin.interfaces.JSONable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Credentials
 * Objeto model que representa las credenciales
 * con las que el usuario inicia sesión en la app
 */
public class Credentials implements JSONable {
    private String email;
    private String password;

    public Credentials(){}

    public Credentials(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public JSONObject toJSON() {

        JSONObject json = new JSONObject();

        try {
            json.put("email", getEmail());
            json.put("password", getPassword());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }

    @Override
    public void getFromJSON(JSONObject jsonObject) {

    }
}
