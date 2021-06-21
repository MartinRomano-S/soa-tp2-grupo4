package com.example.testlogin.interfaces;

import org.json.JSONObject;

/**
 * JSONable
 *
 * Esta interfaz obliga a los Objetos model que la implementen
 * a proveer un método para ser convertidos a JSON y otro método
 * para llenar sus atributos a partir de un JSON.
 *
 * Similar a Gson.
 */
public interface JSONable {
    JSONObject toJSON();
    void getFromJSON(JSONObject jsonObject);
}
