package com.example.testlogin;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.testlogin.interfaces.Asyncronable;
import com.example.testlogin.models.Credentials;
import com.example.testlogin.models.Token;
import com.example.testlogin.models.User;
import com.example.testlogin.services.AsyncHttpRequest;
import com.example.testlogin.utils.Configuration;
import com.example.testlogin.utils.SOAAPIallowedMethodsEnum;
import com.example.testlogin.utils.SharedPreferencesManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Pantalla de registración de usuario
 */
public class RegisterActivity extends AppCompatActivity implements Asyncronable<JSONObject> {

    Button btnCancel;
    Button btnRegister;
    ProgressBar pgbRegister;
    EditText txtName;
    EditText txtLastname;
    EditText txtDNI;
    EditText txtEmail;
    EditText txtPassword;
    User user;
    Token token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        btnCancel = findViewById(R.id.btnCancel);
        btnRegister = findViewById(R.id.btnRegister);
        pgbRegister = findViewById(R.id.pgbRegister);
        txtName = findViewById(R.id.txtName);
        txtLastname = findViewById(R.id.txtLastname);
        txtDNI = findViewById(R.id.txtDNI);
        txtEmail = findViewById(R.id.txtEmail);
        txtPassword = findViewById(R.id.txtPassword);

        addTextChangedListeners();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String txtEmailVal = txtEmail.getText().toString();
                String txtPasswordVal = txtPassword.getText().toString();
                String txtDNIVal = txtDNI.getText().toString();
                String txtNameVal = txtName.getText().toString();
                String txtLastnameVal = txtLastname.getText().toString();

                if (Configuration.isNotNullOrEmpty(txtEmailVal) && Configuration.isNotNullOrEmpty(txtPasswordVal) && Configuration.isNotNullOrEmpty(txtDNIVal) && Configuration.isNotNullOrEmpty(txtNameVal) && Configuration.isNotNullOrEmpty(txtLastnameVal)) {

                    user = new User();
                    user.setCredentials(new Credentials(txtEmailVal, txtPasswordVal));
                    user.setDni(Integer.valueOf(txtDNIVal));
                    user.setName(txtNameVal);
                    user.setLastname(txtLastnameVal);

                    if (Configuration.isNetworkConnected(RegisterActivity.this)) {
                        AsyncHttpRequest asyncHttpRequest = new AsyncHttpRequest(RegisterActivity.this, getString(R.string.api_register_url), SOAAPIallowedMethodsEnum.POST, null, user.toJSON());
                        asyncHttpRequest.execute();
                    } else
                        Configuration.showModalMessage(RegisterActivity.this, getString(R.string.titleError), getString(R.string.networkError));
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void showProgress(String msg) {
        toggleClicks(false);
        pgbRegister.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        toggleClicks(true);
        pgbRegister.setVisibility(View.GONE);
    }

    @Override
    public void afterRequest(JSONObject response){

        AlertDialog.Builder dialog;
        dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Bienvenido");
        dialog.setMessage("");

        String msg = getString(R.string.credentialsError);
        boolean success;
        String currentToken = "";
        String tokenRefresh = "";

        if(response == null) {
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

            token = new Token(currentToken, tokenRefresh, new Date());
            dialog.setMessage("Usuario registrado correctamente");
            dialog.setPositiveButton(getString(R.string.acceptButton), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    Intent intent = new Intent(RegisterActivity.this, TwoFactorActivity.class);
                    Credentials credentials = user.getCredentials();
                    intent.putExtra("email", credentials.getEmail());

                    SharedPreferencesManager spm = SharedPreferencesManager.getInstance(RegisterActivity.this);
                    spm.saveTokenInfo(token);

                    startActivity(intent);

                }
            });

            dialog.create().show();
        } else {
            dialog.setTitle(getString(R.string.titleError));
            dialog.setMessage(msg);
            dialog.setPositiveButton(getString(R.string.acceptButton), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {}
            });
            dialog.create().show();
        }
    }

    private void addTextChangedListeners() {

        txtName.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                String txtValue = txtName.getText().toString();

                if(txtValue.length() == 0)
                    txtName.setError(getString(R.string.errorEmptyField));
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        txtLastname.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                String txtValue = txtLastname.getText().toString();

                if(txtValue.length() == 0)
                    txtLastname.setError(getString(R.string.errorEmptyField));
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        txtDNI.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                String txtValue = txtDNI.getText().toString();
                Pattern p = Pattern.compile(Configuration.DNI_PATTERN);
                Matcher m = p.matcher(txtValue);

                if(!m.matches() || txtValue.length() > Configuration.MAX_DNI_LENGTH)
                    txtDNI.setError(getString(R.string.errorDNI));
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        txtEmail.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                String txtValue = txtEmail.getText().toString();
                Pattern p = Patterns.EMAIL_ADDRESS;
                Matcher m = p.matcher(txtValue);

                if(!m.matches() || txtValue.length() == 0)
                    txtEmail.setError(getString(R.string.errorInvalidMail));
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        txtPassword.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                if(s.length() < 8)
                    txtPassword.setError(getString(R.string.errorPasswordLength, Configuration.MINIMUM_PASSWORD_LENGTH));
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });
    }

    public void toggleClicks(boolean status) {
        btnCancel.setClickable(status);
        btnRegister.setClickable(status);
        txtName.setEnabled(status);
        txtLastname.setEnabled(status);
        txtDNI.setEnabled(status);
        txtEmail.setEnabled(status);
        txtPassword.setEnabled(status);
    }
}
