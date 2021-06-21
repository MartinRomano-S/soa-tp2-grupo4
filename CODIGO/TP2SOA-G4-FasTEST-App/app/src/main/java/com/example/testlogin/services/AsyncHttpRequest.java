package com.example.testlogin.services;

import android.os.AsyncTask;

import com.example.testlogin.interfaces.Asyncronable;
import com.example.testlogin.utils.Configuration;
import com.example.testlogin.utils.SOAAPIallowedMethodsEnum;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * AsyncHttpRequest
 * Clase genérica para gestionar request a la API provista por la
 * cátedra en segundo plano.
 *
 * Tiene la capacidad de enviar tanto las request iniciales sin token
 * como las request de eventos que necesitan token. Además, permite
 * obtener un nuevo token en caso de que esté vencido.
 */
public class AsyncHttpRequest extends AsyncTask<String, Void, JSONObject> {

    private Asyncronable<JSONObject> asyncActivityUI;
    private String endpoint;
    private SOAAPIallowedMethodsEnum method;
    private JSONObject data;
    private String token;

    public AsyncHttpRequest(Asyncronable<JSONObject> asyncActivityUI, String endpoint, SOAAPIallowedMethodsEnum method, String token, JSONObject data) {
        this.asyncActivityUI = asyncActivityUI;
        this.endpoint = Configuration.API_BASE_URL + endpoint;
        this.method = method;
        this.data = data;
        this.token = token;
    }

    @Override
    protected void onPreExecute() { asyncActivityUI.showProgress(""); }

    @Override
    protected JSONObject doInBackground(String... strings) {

        InputStream in;
        OutputStream out;

        try {
            URL url = new URL(endpoint);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            addHeaders(urlConnection);


            out = new BufferedOutputStream(urlConnection.getOutputStream());

            writeRequest(out);
            out.flush();
            urlConnection.connect();

            int status = urlConnection.getResponseCode();

            if (status == HttpURLConnection.HTTP_OK || (status == HttpURLConnection.HTTP_CREATED && endpoint.equals(Configuration.API_EVENT_ENDPOINT)))
                in = urlConnection.getInputStream();
            else
                in = urlConnection.getErrorStream();

            JSONObject response = readResponse(in, status);
            in.close();
            out.close();
            urlConnection.disconnect();

            return response;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(JSONObject response) {
        asyncActivityUI.afterRequest(response);
        asyncActivityUI.hideProgress();
    }

    private void addHeaders(HttpURLConnection urlConnection) throws ProtocolException {
        urlConnection.setReadTimeout(Configuration.REQUEST_READ_TIMEOUT);
        urlConnection.setConnectTimeout(Configuration.REQUEST_CONNECTION_TIMEOUT);
        urlConnection.setDoOutput(true);
        urlConnection.setRequestMethod(method.toString());
        urlConnection.setRequestProperty("Content-Type", "application/json; utf-8");
        urlConnection.setRequestProperty("Accept", "application/json");

        if(token != null)
            urlConnection.setRequestProperty("Authorization", "Bearer " + token);
    }

    private void writeRequest(OutputStream out) throws IOException {
        if(data != null) {
            byte[] input = data.toString().getBytes(StandardCharsets.UTF_8);
            out.write(input, 0, input.length);
        }
    }

    private JSONObject readResponse(InputStream in, int status) throws IOException, JSONException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder builder = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null)
            builder.append(line);

        reader.close();

        JSONObject response = new JSONObject(builder.toString());
        response.put("status", status);
        return response;
    }
}
