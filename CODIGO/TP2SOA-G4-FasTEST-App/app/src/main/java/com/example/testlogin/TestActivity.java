package com.example.testlogin;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.testlogin.models.TestAnswers;
import com.example.testlogin.services.TestResolver;
import com.example.testlogin.utils.Constantes;
import com.example.testlogin.utils.SharedPreferencesManager;

import org.json.JSONException;

/**
 * Pantalla del test de covid-19
 */
public class TestActivity extends AppCompatActivity {

    RadioGroup grupoRespuestaOlfato, grupoRespuestaGusto, grupoRespuestaTos, grupoRespuestaDolorGarganta, grupoRespuestaDificultarRespiratoria, grupoRespuestaDolorCabeza, grupoRespuestaDiarrea, grupoRespuestaVomitos, grupoRespuestaDolorMuscular;
    Button botonEnviarTest, botonAumentarTemperatura, botonDisminuirTemperatura;
    TestAnswers testAnswers;
    AlertDialog.Builder builder;
    TextView textTemperaturaCorporal;
    SharedPreferencesManager spm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        textTemperaturaCorporal = findViewById(R.id.temperaturaCorporal);
        botonAumentarTemperatura = findViewById(R.id.temperaturaMas);
        botonDisminuirTemperatura = findViewById(R.id.temperaturaMenos);
        grupoRespuestaOlfato = findViewById(R.id.radioGroupOlfato);
        grupoRespuestaGusto = findViewById(R.id.radioGroupGusto);
        grupoRespuestaTos = findViewById(R.id.radioGroupTos);
        grupoRespuestaDolorGarganta = findViewById(R.id.radioGroupDolorGarganta);
        grupoRespuestaDificultarRespiratoria = findViewById(R.id.radioGroupDificultadRespiratoria);
        grupoRespuestaDolorCabeza = findViewById(R.id.radioGroupDolorCabeza);
        grupoRespuestaDiarrea = findViewById(R.id.radioGroupDiarrea);
        grupoRespuestaVomitos = findViewById(R.id.radioGroupVomitos);
        grupoRespuestaDolorMuscular = findViewById(R.id.radioGroupDolorMuscular);
        botonEnviarTest = findViewById(R.id.testConsultar);

        spm = SharedPreferencesManager.getInstance(TestActivity.this);

        builder = new AlertDialog.Builder(this);

        botonEnviarTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                testAnswers = new TestAnswers();
                obtenerRespuestas(); //Lee las respuestas del formulario y las carga en testAnswers
                TestResolver testResolver = new TestResolver(testAnswers);
                boolean testResult=testResolver.resolve();

                try {
                    spm.saveLastTestResult(testResult);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(testResult){
                    startActivity(new Intent(TestActivity.this, TestResultActivity.class));

                }else{
                    builder.setMessage(R.string.sinSintomas) .setTitle(R.string.resultadoTest);
                    builder.setCancelable(false)
                            .setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    finish();
                                }
                            })
                            .setNegativeButton(R.string.volverAlTest, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }
        });

        botonDisminuirTemperatura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double temperaturaCorporal = Double.parseDouble(textTemperaturaCorporal.getText().toString());
                temperaturaCorporal-=0.1;
                if(temperaturaCorporal< Constantes.TEMPERATURA_MINIMA_CUERPO_HUMANO){
                    Toast.makeText(getApplicationContext(),R.string.temperaturaMinimaAlcanzada,
                            Toast.LENGTH_SHORT).show();
                }else{
                    temperaturaCorporal=Math.round(temperaturaCorporal * 10.0) / 10.0;
                    textTemperaturaCorporal.setText(String.valueOf(temperaturaCorporal));
                }
            }
        });

        botonAumentarTemperatura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double temperaturaCorporal = Double.parseDouble(textTemperaturaCorporal.getText().toString());
                temperaturaCorporal+=0.1;
                if(temperaturaCorporal> Constantes.TEMPERATURA_MAXIMA_CUERPO_HUMANO){
                    Toast.makeText(getApplicationContext(),R.string.temperaturaMaximaAlcanzada,
                            Toast.LENGTH_SHORT).show();
                }else{
                    temperaturaCorporal=Math.round(temperaturaCorporal * 10.0) / 10.0;
                    textTemperaturaCorporal.setText(String.valueOf(temperaturaCorporal));
                }

            }
        });
    }

    private void obtenerRespuestas(){
        testAnswers.setTemperaturaCorporal(Double.parseDouble(textTemperaturaCorporal.getText().toString()));
        int radioId;
        RadioButton respuesta;
        radioId = grupoRespuestaOlfato.getCheckedRadioButtonId();
        respuesta = findViewById(radioId);
        testAnswers.setOlfato(respuesta.getText());
        radioId = grupoRespuestaGusto.getCheckedRadioButtonId();
        respuesta = findViewById(radioId);
        testAnswers.setGusto(respuesta.getText());
        radioId = grupoRespuestaTos.getCheckedRadioButtonId();
        respuesta = findViewById(radioId);
        testAnswers.setTos(respuesta.getText());
        radioId = grupoRespuestaDolorGarganta.getCheckedRadioButtonId();
        respuesta = findViewById(radioId);
        testAnswers.setDolorGarganta(respuesta.getText());
        radioId = grupoRespuestaDificultarRespiratoria.getCheckedRadioButtonId();
        respuesta = findViewById(radioId);
        testAnswers.setDificultadRespiratoria(respuesta.getText());
        radioId = grupoRespuestaDolorCabeza.getCheckedRadioButtonId();
        respuesta = findViewById(radioId);
        testAnswers.setDolorCabeza(respuesta.getText());
        radioId = grupoRespuestaDiarrea.getCheckedRadioButtonId();
        respuesta = findViewById(radioId);
        testAnswers.setDiarrea(respuesta.getText());
        radioId = grupoRespuestaVomitos.getCheckedRadioButtonId();
        respuesta = findViewById(radioId);
        testAnswers.setVomitos(respuesta.getText());
        radioId = grupoRespuestaDolorMuscular.getCheckedRadioButtonId();
        respuesta = findViewById(radioId);
        testAnswers.setDolorMuscular(respuesta.getText());

    }
}