package com.example.testlogin.models;

import com.example.testlogin.interfaces.JSONable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Token
 * Objeto model que es utilizado para gestionar el acceso
 * a la API provista por la cátedra
 */
public class Token implements JSONable {

    private String activeToken;
    private String refreshToken;
    private Date emmitedDate;

    public Token() {}

    public Token(String activeToken, String refreshToken, Date emmitedDate) {
        this.activeToken = activeToken;
        this.refreshToken = refreshToken;
        this.emmitedDate = emmitedDate;
    }

    public String getActiveToken() {
        return activeToken;
    }

    private void setActiveToken(String activeToken) {
        this.activeToken = activeToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    private void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public Date getEmmitedDate() {
        return emmitedDate;
    }

    private void setEmmitedDate(Date emmitedDate) {
        this.emmitedDate = emmitedDate;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();

        try {
            json.put("activeToken", getActiveToken());
            json.put("refreshToken", getRefreshToken());
            json.put("emmitedDate", getEmmitedDate().getTime());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }

    @Override
    public void getFromJSON(JSONObject jsonObject) {
        try {
            setActiveToken(jsonObject.getString("activeToken"));
            setRefreshToken(jsonObject.getString("refreshToken"));
            setEmmitedDate(new Date(jsonObject.getLong("emmitedDate")));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
