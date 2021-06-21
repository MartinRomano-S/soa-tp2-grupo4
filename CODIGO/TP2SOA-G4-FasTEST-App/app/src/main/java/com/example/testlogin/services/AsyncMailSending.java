package com.example.testlogin.services;

import android.os.AsyncTask;

import com.example.testlogin.interfaces.Asyncronable;
import com.example.testlogin.utils.Configuration;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * AsyncHttpRequest
 * Clase genérica para gestionar el envío de mails para la
 * autenticación doble factor en 2do plano
 */
public class AsyncMailSending extends AsyncTask<Void, Void, String> {

    private Asyncronable<String> asyncronable;
    private String email;
    private String subject;
    private String message;

    public AsyncMailSending(Asyncronable<String> asyncronable, String email, String subject, String message) {
        this.asyncronable = asyncronable;
        this.email = email;
        this.subject = subject;
        this.message = message;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        asyncronable.showProgress("Enviando mensaje...");
    }

    @Override
    protected void onPostExecute(String msg) {
        super.onPostExecute(msg);
        asyncronable.hideProgress();
        asyncronable.afterRequest(msg);
    }

    @Override
    protected String doInBackground(Void... params) {

        //Properties fijas para los Jars que gestionan el envío
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        Session mSession = Session.getDefaultInstance(props,
            new javax.mail.Authenticator() {
                //Valida la contraseña ingresada
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(Configuration.VERIFICATION_EMAIL, Configuration.VERIFICATION_PASSWORD);
                }
            });

        //Envío de mail
        try {
            MimeMessage mm = new MimeMessage(mSession);
            mm.setFrom(new InternetAddress(Configuration.VERIFICATION_EMAIL));
            mm.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
            mm.setSubject(subject);
            mm.setText(message);
            Transport.send(mm);

        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
