package com.example.testlogin.models;

import com.example.testlogin.interfaces.JSONable;
import com.example.testlogin.utils.Configuration;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * User
 * Objeto model que representa al usuario que inicia sesión.
 */
public class User implements JSONable {
    private String name;
    private String lastname;
    private Integer dni;
    private Credentials credentials;

    public User(){}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    private Integer getDni() {
        return dni;
    }

    public void setDni(Integer dni) {
        this.dni = dni;
    }

    public Credentials getCredentials() {
        return credentials;
    }

    public void setCredentials(Credentials credentials) {
        this.credentials = credentials;
    }

    @Override
    public JSONObject toJSON() {

        JSONObject json = new JSONObject();

        try {
            json.put("env", Configuration.API_ENVIRONMENT);
            json.put("name", getName());
            json.put("lastname", getLastname());
            json.put("dni", getDni());
            json.put("email", credentials.getEmail());
            json.put("password", credentials.getPassword());
            json.put("commission", Configuration.COMMISSION);
            json.put("group", Configuration.GROUP);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }

    @Override
    public void getFromJSON(JSONObject jsonObject) {

    }
}
