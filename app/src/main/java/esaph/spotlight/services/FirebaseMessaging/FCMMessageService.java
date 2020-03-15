/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.services.FirebaseMessaging;

import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import esaph.spotlight.navigation.spotlight.Chats.ChatsFragment;
import esaph.spotlight.services.OtherWorkers.UploadNewProfilbild;
import esaph.spotlight.services.SpotLightMessageConnection.MsgServiceConnection;
import esaph.spotlight.services.UndeliveredMessageHandling.SynchDataWithServerUndeliveredMessages;

public class FCMMessageService extends FirebaseMessagingService
{
    private static final String ID_COMMAND = "ID_CMD";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage)
    {
        PowerManager powerManagerPrepare = (PowerManager) getSystemService(POWER_SERVICE);
        PowerManager.WakeLock wakeLockPrepare = null;

        if(powerManagerPrepare != null)
        {
            wakeLockPrepare = powerManagerPrepare.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "esaph.spotlight:GHOST_WAKEUP");
            if(!wakeLockPrepare.isHeld())
            {
                wakeLockPrepare.acquire(5*60*1000L /*10 minutes*/);
            }
        }

        Log.i(getClass().getName(), "NEW FCM MESSAGE");
        String COMMAND_FCM = remoteMessage.getData().get(FCMMessageService.ID_COMMAND);
        if(COMMAND_FCM != null && COMMAND_FCM.equals("WUSH")) //Phone wakeUp
        {
            try
            {
                startService(new Intent(FCMMessageService.this, MsgServiceConnection.class));
                new Thread(new SynchDataWithServerUndeliveredMessages(getApplication())).start();
            }
            catch (Exception ec)
            {
                Log.i(getClass().getName(), "FCMMessageService() failed: " + ec);
            }
        }

        if(wakeLockPrepare != null && wakeLockPrepare.isHeld())
        {
            wakeLockPrepare.release();
        }
    }
}
