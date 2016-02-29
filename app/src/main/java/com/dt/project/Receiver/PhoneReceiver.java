package com.dt.project.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
//checking if the phone or tablet is connected to power
public class PhoneReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

            if (intent.getAction() != null) {

                String state = intent.getAction();

                if (state.equals(Intent.ACTION_POWER_CONNECTED)) {

                    Toast.makeText(context,"Connected", Toast.LENGTH_LONG).show();
                } else if (state.equals(Intent.ACTION_POWER_DISCONNECTED)) {
                    Toast.makeText(context,"Disconnected", Toast.LENGTH_LONG).show();

                }
            }

        }
    }

