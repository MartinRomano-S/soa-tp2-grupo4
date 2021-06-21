package com.example.testlogin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.testlogin.utils.SharedPreferencesManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Date;

/**
 * Pantalla de home, donde está el acceso a todas las opciones de la app
 */
public class HomeActivity extends AppCompatActivity {

    Button btnGoToTest;
    Button btnGoToEmergencyContacts;
    Button btnGoToEventList;
    Button btnGoToLogin;
    SharedPreferencesManager spm;
    ImageView iconLastTestResult;
    TextView txtLastTestResult;
    TextView txtDateLastTestResult;
    ConstraintLayout testResultData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        btnGoToTest = findViewById(R.id.btnGoToTest);
        btnGoToEmergencyContacts = findViewById(R.id.btnGoToEmergencyContacts);
        btnGoToEventList = findViewById(R.id.btnGoToEventList);
        btnGoToLogin = findViewById(R.id.btnGoToLogin);
        iconLastTestResult = findViewById(R.id.iconLastTestResult);
        txtLastTestResult = findViewById(R.id.txtLastTestResult);
        txtDateLastTestResult = findViewById(R.id.txtDateLastTestResult);
        testResultData = findViewById(R.id.testResultData);

        spm = SharedPreferencesManager.getInstance(HomeActivity.this);

        putLastTestResult();

        btnGoToTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, TestActivity.class));
            }
        });

        btnGoToEmergencyContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, EmergencyContactsActivity.class));
            }
        });

        btnGoToEventList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, EventsActivity.class));
            }
        });

        btnGoToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferencesManager spm = SharedPreferencesManager.getInstance(HomeActivity.this);
                spm.delete();
                Intent i = new Intent(HomeActivity.this, LoginActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK |
                        Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        putLastTestResult();
    }

    private void putLastTestResult() {

        try {
            JSONObject lastTestResult = spm.getLastTestResult();

            if(lastTestResult != null) {
                boolean result = lastTestResult.getBoolean("lastTestResult");
                Date date = new Date(lastTestResult.getLong("lastTestResultDate"));
                DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getApplicationContext());
                String fullDate = dateFormat.format(date) + " ";
                dateFormat = android.text.format.DateFormat.getTimeFormat(getApplicationContext());
                fullDate += dateFormat.format(date);

                txtDateLastTestResult.setText(getString(R.string.lastTestDate, fullDate));

                //Si tiene sintomas..
                if(result) {
                    iconLastTestResult.setImageDrawable(getDrawable(R.drawable.ic_covid));
                    testResultData.setBackground(getDrawable(R.drawable.last_test_result_not_ok));
                    txtLastTestResult.setText(getString(R.string.resultNotOk));
                } else {
                    iconLastTestResult.setImageDrawable(getDrawable(R.drawable.ic_check));
                    testResultData.setBackground(getDrawable(R.drawable.last_test_result_ok));
                    txtLastTestResult.setText(getString(R.string.resultOk));
                }
            }

        } catch (JSONException e) {
            txtDateLastTestResult.setText(getString(R.string.lastTestDatePlaceholder));
        }
    }
}
