package com.example.testlogin.interfaces;

/**
 * Asyncronable
 * @param <T>
 *
 * Esta interfaz es para obligar a las Activities que la implementan
 * a utilizar los métodos show & hide progress de la progressBar
 * y a implementar un afterRequest para actualizar la vista luego
 * de finalizar una tarea asíncrona.
 */
public interface Asyncronable<T> {

    void showProgress(String msg);
    void hideProgress();
    void afterRequest(T response);

}
