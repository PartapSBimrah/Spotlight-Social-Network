package esaph.spotlight;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

import esaph.spotlight.einstellungen.CLPreferences;
import esaph.spotlight.services.NotificationAndMessageHandling.GlobalNotificationDisplayer;

import static android.content.Context.POWER_SERVICE;

public class AppUpdatedListener extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        PowerManager.WakeLock wakeLock = null;
        PowerManager powerManager = (PowerManager) context.getSystemService(POWER_SERVICE);
        if(powerManager != null)
        {
            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "AppUpdatedListener");
            if(!wakeLock.isHeld())
            {
                wakeLock.acquire(10*60*1000L /*10 minutes*/);
            }
        }

        CLPreferences preferences = new CLPreferences(context);
        if(preferences.getUsername() == null || preferences.getUsername().isEmpty()
                || preferences.getUsername().equals(""))
        {
            GlobalNotificationDisplayer.createNotificationRegisterAccount(context);
        }

        if(wakeLock != null && wakeLock.isHeld())
        {
            wakeLock.release();
        }
    }
}
