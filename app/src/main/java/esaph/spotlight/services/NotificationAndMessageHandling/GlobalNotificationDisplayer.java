/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.services.NotificationAndMessageHandling;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.provider.Settings;

import androidx.core.app.NotificationCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

import esaph.spotlight.R;
import esaph.spotlight.databases.SQLFriends;
import esaph.spotlight.navigation.SwipeNavigation;
import esaph.spotlight.navigation.spotlight.Chats.Messages.ConversationMessage;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.PrivateChat;

import static android.content.Context.ALARM_SERVICE;

public class GlobalNotificationDisplayer
{
    private static ActivityMessageHandlerCallBack activityMessageHandlerCallBack;
    private static final String notifi_channel_id = "esaph.appname.globalMessageHandler.notify.channelid";

    private static final String ACTION_ON_CLICK_NOTIFICATION = "esaph.spotlight.privatechat.notification_pressed";
    private static final String EXTRA_USERNAME_NOTIFICATION = "esaph.spotlight.privatechat.notification_pressed.username";
    private static final String EXTRA_CLASS_REFERENCE_NOTIFICATION = "esaph.spotlight.privatechat.notification_pressed.class.reference";

    public static void setActivityMessageHandlerCallBack(ActivityMessageHandlerCallBack activityMessageHandlerCallBack)
    {
        GlobalNotificationDisplayer.activityMessageHandlerCallBack = activityMessageHandlerCallBack;
    }

    public static void displayNewPrivateMessage(Context context, String Absender, String Nachricht, int notfityID,
                                                int backgroundColor, int textColor)
    {
        try
        {
            if((activityMessageHandlerCallBack == null || !activityMessageHandlerCallBack.isActivityAlive(Absender)))
            {
                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(context.getApplicationContext(), GlobalNotificationDisplayer.notifi_channel_id);
                Intent ii = new Intent(context.getApplicationContext(), SwipeNavigation.class);
                ii.putExtra(GlobalNotificationDisplayer.EXTRA_CLASS_REFERENCE_NOTIFICATION, PrivateChat.class);
                ii.setAction(GlobalNotificationDisplayer.ACTION_ON_CLICK_NOTIFICATION);
                ii.putExtra(GlobalNotificationDisplayer.EXTRA_USERNAME_NOTIFICATION, Absender);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, ii, 0);

                mBuilder.setContentIntent(pendingIntent);

                mBuilder.setSmallIcon(R.drawable.ic_app_logo_notification_icon);
                mBuilder.setContentTitle(Absender);
                mBuilder.setLights(textColor, 1000, 550);
                mBuilder.setVibrate(new long[]{1000,1000});
                mBuilder.setContentText(Nachricht);
                mBuilder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
                mBuilder.setAutoCancel(true);
                mBuilder.setColor(textColor);
                mBuilder.setTicker(Absender);

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                {
                    mBuilder.setPriority(Notification.PRIORITY_HIGH);
                }

                NotificationManager mNotificationManager =
                        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                if(mNotificationManager != null)
                {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    {
                        NotificationChannel channel = new NotificationChannel(GlobalNotificationDisplayer.notifi_channel_id,
                                "MainNotiChannel",
                                NotificationManager.IMPORTANCE_DEFAULT);
                        mNotificationManager.createNotificationChannel(channel);
                    }

                    Notification notification = mBuilder.build();

                    notification.flags = (Notification.FLAG_AUTO_CANCEL | notification.flags);
                    notification.flags = (Notification.FLAG_SHOW_LIGHTS | notification.flags);

                    mNotificationManager.notify(notfityID, mBuilder.build());
                }
            }
        }
        catch (Exception ec)
        {

        }
    }


    public static void createNotificationSingleMessage(Context context,
                                                       String titel,
                                                       String nachricht,
                                                       String Absender,
                                                       int IconId,
                                                       Class<?> classReference)
    {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context.getApplicationContext(), GlobalNotificationDisplayer.notifi_channel_id);

        Intent ii = new Intent(context.getApplicationContext(), SwipeNavigation.class);
        ii.putExtra(GlobalNotificationDisplayer.EXTRA_CLASS_REFERENCE_NOTIFICATION, classReference);

        if(classReference == PrivateChat.class)
        {
            ii.setAction(GlobalNotificationDisplayer.ACTION_ON_CLICK_NOTIFICATION);
            ii.putExtra(GlobalNotificationDisplayer.EXTRA_USERNAME_NOTIFICATION, Absender);
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, ii, 0);

        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setSmallIcon(IconId);
        mBuilder.setContentTitle(titel);
        mBuilder.setLights(Color.CYAN, 1000, 550);
        mBuilder.setVibrate(new long[]{1000,1000});
        mBuilder.setContentText(nachricht);
        mBuilder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
        mBuilder.setAutoCancel(true);
        mBuilder.setTicker(titel);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
        {
            mBuilder.setPriority(Notification.PRIORITY_HIGH);
        }

        Random random = new Random();

        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if(mNotificationManager != null)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            {
                NotificationChannel channel = new NotificationChannel(GlobalNotificationDisplayer.notifi_channel_id,
                        "MainNotiChannel",
                        NotificationManager.IMPORTANCE_DEFAULT);
                mNotificationManager.createNotificationChannel(channel);
            }
            Notification notification = mBuilder.build();

            notification.flags = (Notification.FLAG_AUTO_CANCEL | notification.flags);
            notification.flags = (Notification.FLAG_SHOW_LIGHTS | notification.flags);


            int mr = random.nextInt(9999 - 1000) + 1000;

            mNotificationManager.notify(mr, mBuilder.build());
        }
    }


    public static void createNotificationSingleMessage(Context context,
                                                       String titel,
                                                       String nachricht,
                                                       String Absender,
                                                       int IconId,
                                                       Class<?> classReference,
                                                       int NotifyId)
    {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context.getApplicationContext(), GlobalNotificationDisplayer.notifi_channel_id);

        Intent ii = new Intent(context.getApplicationContext(), SwipeNavigation.class);
        ii.putExtra(GlobalNotificationDisplayer.EXTRA_CLASS_REFERENCE_NOTIFICATION, classReference);

        if(classReference == PrivateChat.class)
        {
            ii.setAction(GlobalNotificationDisplayer.ACTION_ON_CLICK_NOTIFICATION);
            ii.putExtra(GlobalNotificationDisplayer.EXTRA_USERNAME_NOTIFICATION, Absender);
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, ii, 0);

        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setSmallIcon(IconId);
        mBuilder.setContentTitle(titel);
        mBuilder.setLights(Color.CYAN, 1000, 550);
        mBuilder.setVibrate(new long[]{1000,1000});
        mBuilder.setContentText(nachricht);
        mBuilder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
        mBuilder.setAutoCancel(true);
        mBuilder.setTicker(titel);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
        {
            mBuilder.setPriority(Notification.PRIORITY_HIGH);
        }

        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if(mNotificationManager != null)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            {
                NotificationChannel channel = new NotificationChannel(GlobalNotificationDisplayer.notifi_channel_id,
                        "MainNotiChannel",
                        NotificationManager.IMPORTANCE_DEFAULT);
                mNotificationManager.createNotificationChannel(channel);
            }
            Notification notification = mBuilder.build();

            notification.flags = (Notification.FLAG_AUTO_CANCEL | notification.flags);
            notification.flags = (Notification.FLAG_SHOW_LIGHTS | notification.flags);

            mNotificationManager.notify(NotifyId, mBuilder.build());
        }
    }

    public static void createNotificationNewPrivateImage(Context context, String Absender, long millis, int notifyID)
    {
        if((activityMessageHandlerCallBack == null || !activityMessageHandlerCallBack.isActivityAlive(Absender)))
        {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM - HH:mm", Locale.getDefault());
            if(!Absender.isEmpty() && Absender.length() <= 20)
            {
                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(context.getApplicationContext(), GlobalNotificationDisplayer.notifi_channel_id);
                Intent ii = new Intent(context.getApplicationContext(), SwipeNavigation.class);
                ii.putExtra(GlobalNotificationDisplayer.EXTRA_CLASS_REFERENCE_NOTIFICATION, PrivateChat.class);
                ii.setAction(GlobalNotificationDisplayer.ACTION_ON_CLICK_NOTIFICATION);
                ii.putExtra(GlobalNotificationDisplayer.EXTRA_USERNAME_NOTIFICATION, Absender);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, ii, 0);

                mBuilder.setContentIntent(pendingIntent);
                mBuilder.setSmallIcon(R.drawable.ic_app_logo_notification_icon);
                mBuilder.setContentTitle(Absender);
                mBuilder.setLights(Color.CYAN, 1000, 550);
                mBuilder.setVibrate(new long[]{1000,1000});
                mBuilder.setContentText(simpleDateFormat.format(millis));
                mBuilder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
                mBuilder.setAutoCancel(true);
                mBuilder.setTicker(Absender);

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                {
                    mBuilder.setPriority(Notification.PRIORITY_HIGH);
                }

                NotificationManager mNotificationManager =
                        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                if(mNotificationManager != null)
                {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    {
                        NotificationChannel channel = new NotificationChannel(GlobalNotificationDisplayer.notifi_channel_id,
                                "MainNotiChannel",
                                NotificationManager.IMPORTANCE_DEFAULT);
                        mNotificationManager.createNotificationChannel(channel);
                    }
                    Notification notification = mBuilder.build();

                    notification.flags = (Notification.FLAG_AUTO_CANCEL | notification.flags);
                    notification.flags = (Notification.FLAG_SHOW_LIGHTS | notification.flags);

                    mNotificationManager.notify(notifyID, mBuilder.build());
                }
            }
        }
    }


    public static void createLog(Context context, String Message, long millis, boolean ok)
    {
        return;
        /*
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context.getApplicationContext(), GlobalNotificationDisplayer.notifi_channel_id);
        Intent ii = new Intent(context.getApplicationContext(), SwipeNavigation.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, ii, 0);

        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setSmallIcon(R.drawable.ic_spotlight_drawable_notification);
        mBuilder.setContentTitle("INTERN: " + simpleDateFormat.format(millis));
        mBuilder.setContentText(Message);
        mBuilder.setLights(Color.CYAN, 1000, 550);
        if(ok)
        {
            mBuilder.setVibrate(new long[]{100,100,100,100});
        }
        else
        {
            mBuilder.setVibrate(new long[]{1000,1000,2000});
        }
        mBuilder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
        mBuilder.setAutoCancel(true);
        mBuilder.setTicker("INTERN");

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
        {
            mBuilder.setPriority(Notification.PRIORITY_HIGH);
        }

        Random random = new Random();

        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if(mNotificationManager != null)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            {
                NotificationChannel channel = new NotificationChannel(GlobalNotificationDisplayer.notifi_channel_id,
                        "MainNotiChannel",
                        NotificationManager.IMPORTANCE_DEFAULT);
                mNotificationManager.createNotificationChannel(channel);
            }
            Notification notification = mBuilder.build();

            notification.flags = (Notification.FLAG_AUTO_CANCEL | notification.flags);
            notification.flags = (Notification.FLAG_SHOW_LIGHTS | notification.flags);

            int mr = random.nextInt(9999 - 1000) + 1000;

            mNotificationManager.notify(mr, mBuilder.build());
        }*/
    }

    public static void createNotificationRegisterAccount(Context context)
    {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context.getApplicationContext(), GlobalNotificationDisplayer.notifi_channel_id);
        Intent ii = new Intent(context.getApplicationContext(), SwipeNavigation.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, ii, 0);

        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setSmallIcon(R.drawable.ic_new_app_content);
        mBuilder.setTicker(context.getResources().getString(R.string.app_name));
        mBuilder.setContentTitle(context.getResources().getString(R.string.app_name));
        mBuilder.setContentText(context.getResources().getString(R.string.txt_createAccount));
        mBuilder.setLights(Color.WHITE, 1000, 550);
        mBuilder.setAutoCancel(true);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
        {
            mBuilder.setPriority(Notification.PRIORITY_HIGH);
        }

        Random random = new Random();

        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if(mNotificationManager != null)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            {
                NotificationChannel channel = new NotificationChannel(GlobalNotificationDisplayer.notifi_channel_id,
                        "MainNotiChannel",
                        NotificationManager.IMPORTANCE_DEFAULT);
                mNotificationManager.createNotificationChannel(channel);
            }
            Notification notification = mBuilder.build();

            notification.flags = (Notification.FLAG_AUTO_CANCEL | notification.flags);
            notification.flags = (Notification.FLAG_SHOW_LIGHTS | notification.flags);

            int mr = random.nextInt(9999 - 1000) + 1000;

            mNotificationManager.notify(mr, mBuilder.build());
        }
    }

    public static void removeNotification(Context context, int id, ConversationMessage conversationMessage)
    {
        NotificationManager nMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if(nMgr != null)
        {
            nMgr.cancel(id);
        }

        SQLFriends sqlWatcher = new SQLFriends(context);
        sqlWatcher.onNotificationPostRemoved(conversationMessage);
        sqlWatcher.close();
    }


    public static void setReminder(Context context, Class<?> cls, int hour, int min)
    {

        Calendar calendar = Calendar.getInstance();
        Calendar setcalendar = Calendar.getInstance();
        setcalendar.set(Calendar.HOUR_OF_DAY, hour);
        setcalendar.set(Calendar.MINUTE, min);
        setcalendar.set(Calendar.SECOND, 0);
        // cancel already scheduled reminders
        cancelReminder(context,cls);

        if(setcalendar.before(calendar))
            setcalendar.add(Calendar.DATE,1);


        // Enable a receiver

        ComponentName receiver = new ComponentName(context, cls);

        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,

                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,

                PackageManager.DONT_KILL_APP);




        Intent intent1 = new Intent(context, cls);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                DAILY_REMINDER_REQUEST_CODE, intent1,
                PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        am.setInexactRepeating(AlarmManager.RTC_WAKEUP, setcalendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pendingIntent);
    }


    private static final int DAILY_REMINDER_REQUEST_CODE = 1060;
    public static void cancelReminder(Context context, Class<?> cls)

    {
        ComponentName receiver = new ComponentName(context, cls);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);




        Intent intent1 = new Intent(context, cls);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                DAILY_REMINDER_REQUEST_CODE, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        am.cancel(pendingIntent);
        pendingIntent.cancel();
    }



}
