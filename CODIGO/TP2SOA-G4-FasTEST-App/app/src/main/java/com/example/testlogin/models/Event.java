package com.example.testlogin.models;

import com.example.testlogin.interfaces.JSONable;
import com.example.testlogin.utils.Configuration;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Event
 * Objeto model que representa un evento ocurrido en la app
 * y es usado para registrarlo en el Listado de Eventos y
 * además en la API provista por la cátedra.
 *
 * Eventos registrables: Shake, Proximidad
 */
public class Event implements JSONable {
    private Date eventDate;
    private String type;
    private String description;

    public Event(){}

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();

        try {
            json.put("env", Configuration.API_ENVIRONMENT);
            json.put("type_events", getType());

            Date evDate = getEventDate();

            if(evDate == null)
                evDate = new Date();

            json.put("eventDate", evDate.getTime());
            json.put("description", getDescription());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }

    @Override
    public void getFromJSON(JSONObject jsonObject) {
        try {
            Date evDate = new Date(jsonObject.getLong("eventDate"));
            setEventDate(evDate);
            setType(jsonObject.getString("type_events"));
            setDescription(jsonObject.getString("description"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
