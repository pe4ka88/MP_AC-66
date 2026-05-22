package com.example.myapplication8;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Запускает сервис отслеживания после перезагрузки устройства.
 */
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Intent serviceIntent = new Intent(context, LocationTrackingService.class);
            serviceIntent.setAction(LocationTrackingService.ACTION_START);
            context.startForegroundService(serviceIntent);
        }
    }
}