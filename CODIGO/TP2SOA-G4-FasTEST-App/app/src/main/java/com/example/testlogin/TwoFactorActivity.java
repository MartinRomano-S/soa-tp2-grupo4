package com.example.testlogin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.testlogin.interfaces.Asyncronable;
import com.example.testlogin.services.AsyncMailSending;
import com.example.testlogin.utils.Configuration;
import com.example.testlogin.utils.SharedPreferencesManager;

import java.util.Date;

/**
 * Pantalla que se encarga de enviar el código y de la verificación del código de doble factor
 */
public class TwoFactorActivity extends AppCompatActivity implements Asyncronable<String> {

    Button btnCancel;
    Button btnResendVerificationCode;
    Button btnVerify;
    TextView txtLblVerificationCode;
    EditText txtVerificationCode;
    ProgressBar pgbVerify;
    String email;
    SharedPreferencesManager spm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_twofactor);

        btnCancel = findViewById(R.id.btnCancelVerification);
        txtLblVerificationCode = findViewById(R.id.lblVerificationCodeSent);
        txtVerificationCode = findViewById(R.id.txtVerificationCode);
        btnResendVerificationCode = findViewById(R.id.btnResendVerification);
        pgbVerify = findViewById(R.id.pgbVerify);
        btnVerify = findViewById(R.id.btnVerify);

        spm = SharedPreferencesManager.getInstance(this);

        Intent i = getIntent();
        email = i.getStringExtra("email");

        txtLblVerificationCode.setText(getString(R.string.lblVerificationCodeSent, email));

        sendEmailVerificationCode(email);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btnResendVerificationCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendEmailVerificationCode(email);
            }
        });
        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String inputCode = txtVerificationCode.getText().toString();

                if(inputCode.length() > 0 && inputCode.equals(spm.getCurrentVerificationCode())) {
                    spm.saveLastLoginDate(new Date().getTime());
                    Intent i = new Intent(TwoFactorActivity.this, HomeActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK |
                            Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                } else
                    txtVerificationCode.setError(getString(R.string.errorInvalidVerificationCode));
            }
        });

    }

    public void sendEmailVerificationCode(String email) {

        String generatedCode = Configuration.generateRandomCode();
        spm.saveCurrentVerificationCode(generatedCode);
        AsyncMailSending asyncMailSending = new AsyncMailSending(this, email, getString(R.string.verificationCodeMailSubject), getString(R.string.verificationCodeMailBody, generatedCode));
        asyncMailSending.execute();
    }

    @Override
    public void showProgress(String msg) {
        pgbVerify.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        pgbVerify.setVisibility(View.GONE);
    }

    @Override
    public void afterRequest(String response) {
        Toast.makeText(this, "Email enviado.", Toast.LENGTH_SHORT).show();
    }
}
