package com.example.testlogin.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.testlogin.LoginActivity;
import com.example.testlogin.R;

/**
 * BatteryReceiver
 *
 * Esta clase se encarga de escuchar los eventos de la batería
 * y mostrarlos en la pantalla del Login
 */
public class BatteryReceiver extends BroadcastReceiver {

    private static final int BATTERY_PERCENTAGE_99 = 99;
    private static final int BATTERY_PERCENTAGE_90 = 90;
    private static final int BATTERY_PERCENTAGE_80 = 80;
    private static final int BATTERY_PERCENTAGE_60 = 60;
    private static final int BATTERY_PERCENTAGE_40 = 40;
    private static final int BATTERY_PERCENTAGE_30 = 30;

    @Override
    public void onReceive(Context context, Intent intent) {

        TextView txtBattery = ((LoginActivity) context).findViewById(R.id.txtBattery);
        ImageView imgBatteryLevel = ((LoginActivity) context).findViewById(R.id.imgBatteryLevel);
        ImageView imgCharging = ((LoginActivity) context).findViewById(R.id.imgCharging);

        String action = intent.getAction();

        //Validamos que la acción sea un cambio en el nivel o estado de la batería
        if(action != null && action.equals(Intent.ACTION_BATTERY_CHANGED)) {

            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            int percentage = level * 100 / scale;
            txtBattery.setText(context.getString(R.string.lblBattery, percentage));

            //Controlamos en que estado se encuentra la batería
            switch (status) {
                case BatteryManager.BATTERY_STATUS_FULL:
                    imgBatteryLevel.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_battery_100));
                    return;
                case BatteryManager.BATTERY_STATUS_UNKNOWN:
                    imgBatteryLevel.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_battery_unknown));
                    return;
                case BatteryManager.BATTERY_STATUS_CHARGING:
                    imgCharging.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_power));
                    break;
                case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                case BatteryManager.BATTERY_STATUS_DISCHARGING:
                    imgCharging.setImageDrawable(null);
                    break;
            }

            //Según el nivel actual de batería cambiamos la imágen que se muestra en la pantalla.
            if(percentage >= BATTERY_PERCENTAGE_99)
                imgBatteryLevel.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_battery_100));
            else if (percentage >= BATTERY_PERCENTAGE_90)
                imgBatteryLevel.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_battery_90));
            else if (percentage >= BATTERY_PERCENTAGE_80)
                imgBatteryLevel.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_battery_80));
            else if (percentage >= BATTERY_PERCENTAGE_60)
                imgBatteryLevel.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_battery_60));
            else if (percentage >= BATTERY_PERCENTAGE_40)
                imgBatteryLevel.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_battery_50));
            else if (percentage >= BATTERY_PERCENTAGE_30)
                imgBatteryLevel.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_battery_30));
            else
                imgBatteryLevel.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_battery_20));
        }
    }
}
