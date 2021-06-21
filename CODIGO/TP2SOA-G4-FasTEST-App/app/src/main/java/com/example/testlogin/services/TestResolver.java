package com.example.testlogin.services;


import com.example.testlogin.models.TestAnswers;
import com.example.testlogin.utils.Constantes;

/**
 * TestResolver
 * Esta clase se encarga de validar las TestAnswers ingresadas
 * por el usuario
 */
public class TestResolver {
    private TestAnswers testAnswers;

    public TestResolver(TestAnswers testAnswers){
        this.testAnswers=testAnswers;
    }

    //Esta función evalúa todas las respuestas y si todas fueron NO (boolean:false) devuelve false: No tenes sintomas de COVID. Si hay aunque sea 1 en true devuelve true: tenes sintomas de COVID.
    public boolean resolve(){
        return testAnswers.isDiarrea() || testAnswers.isDificultadRespiratoria() || testAnswers.isDolorCabeza() || testAnswers.isDolorGarganta() || testAnswers.isDolorMuscular() || testAnswers.isGusto() || testAnswers.isOlfato() || testAnswers.isTos() || testAnswers.isVomitos() || !(testAnswers.getTemperaturaCorporal() <= Constantes.UMBRAL_TEMPERATURA_CORPORAL);
    }
}
