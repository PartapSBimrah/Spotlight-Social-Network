/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.navigation.spotlight.Chats.PrivateChat.ChatListViewPager;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class ChatListViewPagerHelperClass implements LocationListener
{
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    public static String formatTime(long timeInMillis)
    {
        return ChatListViewPagerHelperClass.simpleDateFormat.format(timeInMillis);
    }

    @Override
    public void onLocationChanged(Location location) {
        simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
