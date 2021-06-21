
package com.example.testlogin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.testlogin.interfaces.Asyncronable;
import com.example.testlogin.models.Event;
import com.example.testlogin.models.Token;
import com.example.testlogin.services.AsyncHttpRequest;
import com.example.testlogin.services.ShakeDetector;
import com.example.testlogin.utils.Configuration;
import com.example.testlogin.utils.Constantes;
import com.example.testlogin.utils.PhoneCaller;
import com.example.testlogin.utils.SOAAPIallowedMethodsEnum;
import com.example.testlogin.utils.SharedPreferencesManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Pantalla del resultado con sintomas del test de covid-19
 * permite hacer llamados al 148 a traves del sensor de proximidad
 * envío de SMS al listado de contactos de emergencia por medio del Shake
 */
public class TestResultActivity extends AppCompatActivity implements Asyncronable<JSONObject> {

    Button botonLlamar, botonMensajear, botonVolverAHome;
    SensorManager sensorManager;
    Sensor proximitySensor, accelerometerSensor;
    private ShakeDetector shakeDetectorEventListener;
    SharedPreferencesManager spm;
    Token token;
    boolean tokenWasInvalid;
    Event pendingEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_result);

        botonLlamar = findViewById(R.id.botonLlamarAtencionCovid);
        botonMensajear = findViewById(R.id.botonMensajearCovid);
        botonVolverAHome = findViewById(R.id.botonVolverAHome);

        spm = SharedPreferencesManager.getInstance(TestResultActivity.this);

        try {
            token = spm.getTokenInfo();
        } catch (JSONException e) {
            token = null;
        }

        if(!Configuration.checkPermission(this, Manifest.permission.CALL_PHONE))
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CALL_PHONE}, 1);

        if(!Configuration.checkPermission(this, Manifest.permission.SEND_SMS))
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.SEND_SMS}, 1);

        botonVolverAHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TestResultActivity.this, HomeActivity.class));
            }
        });

        botonLlamar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!Configuration.checkPermission(TestResultActivity.this, Manifest.permission.CALL_PHONE))
                    ActivityCompat.requestPermissions(TestResultActivity.this, new String[] {Manifest.permission.CALL_PHONE}, 1);

                if(Configuration.checkPermission(TestResultActivity.this, Manifest.permission.CALL_PHONE))
                    PhoneCaller.makePhoneCall(TestResultActivity.this,Constantes.TELEFONO_ATENCION_COVID);
            }
        });

        botonMensajear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    spm.sendMessageToEmergencyContactList(TestResultActivity.this);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        // Invoco al sensor service
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        if(sensorManager != null) {

            // Desde el sensor service llamo al sensor de proximidad
            proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
            // SHAKE
            // ShakeDetector initialization
            accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            shakeDetectorEventListener = new ShakeDetector();
            shakeDetectorEventListener.setOnShakeListener(new ShakeDetector.OnShakeListener() {

                @Override
                public void onShake(int count) {
                    try {

                        Event eventInfo = new Event();
                        eventInfo.setEventDate(new Date());
                        eventInfo.setDescription("Se ha activado el acelerómetro.");
                        eventInfo.setType(Constantes.EVENT_TYPES.SHAKE.toString());

                        try {
                            spm.saveEvent(eventInfo);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        pendingEvent = eventInfo;
                        registerEvent();

                        spm.sendMessageToEmergencyContactList(TestResultActivity.this);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            Toast.makeText(this, "Se produjo un error al comunicarse con los sensores.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private boolean isTokenValid(Token token) throws JSONException {

        if(token == null) return false;

        token = spm.getTokenInfo();
        Date emmitedDate = token.getEmmitedDate();

        if(emmitedDate == null) return false;

        Date todayNow = new Date();
        long diff = todayNow.getTime() - emmitedDate.getTime();
        long diffMinutes = diff / (60 * 1000) % 60;

        return diffMinutes < Configuration.TOKEN_REFRESH_TIME;
    }

    @Override
    public void onResume() {
        super.onResume();

        // Si el teléfono no tiene sensor de proximidad
        if (proximitySensor == null) {
            Toast.makeText(this, "El teléfono no cuenta con sensor de proximidad.", Toast.LENGTH_SHORT).show();
            finish();
        } else
            // Registro el sensor con el sensor manager
            sensorManager.registerListener(proximitySensorEventListener, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);

        sensorManager.registerListener(shakeDetectorEventListener, accelerometerSensor,	SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onPause() {
        // Add the following line to unregister the Sensor Manager onPause
        sensorManager.unregisterListener(proximitySensorEventListener);
        sensorManager.unregisterListener(shakeDetectorEventListener);
        super.onPause();
    }

    //Llamo a la clase sensorEventListener para detectar cambio en el estado del sensor.
    SensorEventListener proximitySensorEventListener = new SensorEventListener() {
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // No hago nada
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            // Chequeo si el sensor de proximidad cambió.
            if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
                if (event.values[0] == 0) {

                    try {

                        Event eventInfo = new Event();
                        eventInfo.setEventDate(new Date());
                        eventInfo.setDescription("Se ha activado el sensor de proximidad.");
                        eventInfo.setType(Constantes.EVENT_TYPES.PROXIMITY.toString());

                        try {
                            spm.saveEvent(eventInfo);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        pendingEvent = eventInfo;
                        registerEvent();

                        PhoneCaller.makePhoneCall(TestResultActivity.this,Constantes.TELEFONO_ATENCION_COVID);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };

    @Override
    public void showProgress(String msg) {}

    @Override
    public void hideProgress() {}

    @Override
    public void afterRequest(JSONObject response) {

        boolean success;

        if(response == null) {
            Toast.makeText(this, "Evento NO registrado", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            success = response.getBoolean("success");

        } catch (JSONException e) {
            success = false;
        }

        if(success && tokenWasInvalid) {
            tokenWasInvalid = false;

            String newToken;
            String newTokenRefresh;

            try {
                newToken = response.getString("token");
                newTokenRefresh = response.getString("token_refresh");

                token = new Token(newToken, newTokenRefresh, new Date());
                spm.saveTokenInfo(token);
                registerEvent();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return;
        }

        if(success)
            Toast.makeText(this, "Evento registrado", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, "Evento NO registrado", Toast.LENGTH_SHORT).show();

    }

    public synchronized void registerEvent() throws JSONException {

        if(Configuration.isNetworkConnected(TestResultActivity.this)) {

            //Si el token está vencido hay que pedir uno nuevo
            tokenWasInvalid = !isTokenValid(token);

            if(tokenWasInvalid) {
                AsyncHttpRequest asyncHttpRequest = new AsyncHttpRequest(TestResultActivity.this, getString(R.string.api_refresh_url), SOAAPIallowedMethodsEnum.PUT, token.getRefreshToken(), null);
                asyncHttpRequest.execute();
            } else {
                AsyncHttpRequest asyncHttpRequest = new AsyncHttpRequest(TestResultActivity.this, getString(R.string.api_event_url), SOAAPIallowedMethodsEnum.POST, token.getActiveToken(), pendingEvent.toJSON());
                asyncHttpRequest.execute();
            }
        }
    }
}