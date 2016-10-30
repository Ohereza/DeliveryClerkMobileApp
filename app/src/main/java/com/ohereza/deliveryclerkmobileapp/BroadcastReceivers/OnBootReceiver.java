package com.ohereza.deliveryclerkmobileapp.BroadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ohereza.deliveryclerkmobileapp.services.LocatorService;

public class OnBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            // on boot, restart locator service
            Intent locationServiceIntent = new Intent(context,LocatorService.class);
            context.startService(locationServiceIntent);
        }
    }
}