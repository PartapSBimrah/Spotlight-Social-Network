/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.services.NotificationAndMessageHandling;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import esaph.spotlight.R;
import esaph.spotlight.navigation.SwipeNavigation;
import esaph.spotlight.services.UndeliveredMessageHandling.SynchDataWithServerUndeliveredMessages;

public class AlarmReceiverNotification extends BroadcastReceiver
{

    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (intent.getAction() != null && context != null)
        {
            if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED))
            {
                GlobalNotificationDisplayer.setReminder(context, AlarmReceiverNotification.class, 17, 0);
                new Thread(new SynchDataWithServerUndeliveredMessages(context)).start();
                return;
            }

            GlobalNotificationDisplayer.createNotificationSingleMessage(context, "", context.getResources().getString(R.string.txt_notification_not_uploaded_yet),
                    context.getResources().getString(R.string.app_name), R.drawable.ic_new_app_content, SwipeNavigation.class, 900);
        }
    }
}
