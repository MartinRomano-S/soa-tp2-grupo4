package com.example.testlogin;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.testlogin.interfaces.Asyncronable;
import com.example.testlogin.interfaces.Inputable;
import com.example.testlogin.models.Credentials;
import com.example.testlogin.models.Token;
import com.example.testlogin.services.AsyncHttpRequest;
import com.example.testlogin.utils.BatteryReceiver;
import com.example.testlogin.utils.Configuration;
import com.example.testlogin.utils.SOAAPIallowedMethodsEnum;
import com.example.testlogin.utils.SharedPreferencesManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Pantalla de inicio de sesión
 * Contiene también un botón para ir a la pantalla de registración
 * y se puede visualizar el estado actual y porcentaje de batería
 */
public class LoginActivity extends AppCompatActivity implements Asyncronable<JSONObject>, Inputable {

    Button btnLogin;
    Button btnToRegister;
    EditText txtUser;
    EditText txtPasswordLogin;
    Credentials credentials;
    ProgressBar prgLogin;
    private BatteryReceiver batteryReceiver = new BatteryReceiver();
    private IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnLogin = findViewById(R.id.btnLogin);
        btnToRegister = findViewById(R.id.btnToRegister);
        txtUser = findViewById(R.id.txtUser);
        txtPasswordLogin = findViewById(R.id.txtPasswordLogin);
        prgLogin = findViewById(R.id.pgbLogin);

        SharedPreferencesManager spm = SharedPreferencesManager.getInstance(this);
        spm.delete();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String txtUserVal = txtUser.getText().toString();
                String txtPasswordVal = txtPasswordLogin.getText().toString();

                if (Configuration.isNotNullOrEmpty(txtUserVal) && Configuration.isNotNullOrEmpty(txtPasswordVal)) {

                    credentials = new Credentials();
                    credentials.setEmail(txtUserVal);
                    credentials.setPassword(txtPasswordVal);

                    if (Configuration.isNetworkConnected(LoginActivity.this)) {
                        AsyncHttpRequest asyncHttpRequest = new AsyncHttpRequest(LoginActivity.this, getString(R.string.api_login_url), SOAAPIallowedMethodsEnum.POST, null, credentials.toJSON());
                        asyncHttpRequest.execute();
                    } else
                        Configuration.showModalMessage(LoginActivity.this, getString(R.string.titleError), getString(R.string.networkError));
                } else
                    Configuration.showModalMessage(LoginActivity.this, getString(R.string.titleError), getString(R.string.someEmptyField));
            }
        });

        addInputChangedListeners();

        btnToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(batteryReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(batteryReceiver, intentFilter);
    }

    @Override
    public void showProgress(String msg) {
        toggleClicks(false);
        prgLogin.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        toggleClicks(true);
        prgLogin.setVisibility(View.GONE);
    }

    @Override
    public void afterRequest(JSONObject response) {

        String msg = getString(R.string.credentialsError);
        boolean success;
        String currentToken = "";
        String tokenRefresh = "";

        if(response == null) {
            AlertDialog.Builder dialog;
            dialog = new AlertDialog.Builder(this);
            dialog.setTitle(getString(R.string.titleError));
            dialog.setMessage(msg);
            dialog.setPositiveButton(getString(R.string.acceptButton), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {}
            });
            dialog.create().show();
            return;
        }

        try {
            success = response.getBoolean("success");
            currentToken = response.getString("token");
            tokenRefresh = response.getString("token_refresh");

        } catch (JSONException e) {
            msg = getString(R.string.requestError);
            success = false;
        }

        if(success) {
            Intent i = new Intent(LoginActivity.this, TwoFactorActivity.class);
            i.putExtra("email", credentials.getEmail());

            SharedPreferencesManager spm = SharedPreferencesManager.getInstance(LoginActivity.this);
            spm.saveTokenInfo(new Token(currentToken, tokenRefresh, new Date()));

            startActivity(i);
        } else {
            AlertDialog.Builder dialog;
            dialog = new AlertDialog.Builder(this);
            dialog.setTitle(getString(R.string.titleError));
            dialog.setMessage(msg);
            dialog.setPositiveButton(getString(R.string.acceptButton), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {}
            });
            dialog.create().show();
        }
    }

    @Override
    public void addInputChangedListeners() {
        txtUser.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                String txtValue = txtUser.getText().toString();
                Pattern p = Patterns.EMAIL_ADDRESS;
                Matcher m = p.matcher(txtValue);

                if(!m.matches())
                    txtUser.setError(getString(R.string.errorInvalidMail));
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        txtPasswordLogin.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                if(s.length() < Configuration.MINIMUM_PASSWORD_LENGTH)
                    txtPasswordLogin.setError(getString(R.string.errorPasswordLength, Configuration.MINIMUM_PASSWORD_LENGTH));
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });
    }

    public void toggleClicks(boolean status) {
        btnLogin.setClickable(status);
        btnToRegister.setClickable(status);
        txtUser.setEnabled(status);
        txtPasswordLogin.setEnabled(status);
        btnLogin.setClickable(status);
    }
}
