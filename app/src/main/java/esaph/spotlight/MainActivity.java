/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.work.WorkManager;

import esaph.spotlight.Esaph.EsaphAndroidTopBar.EsaphAndroidTopBarHelper;
import esaph.spotlight.einstellungen.CLPreferences;
import esaph.spotlight.navigation.SwipeNavigation;
import esaph.spotlight.services.SpotLightMessageConnection.Workers.SpotLightLoginSessionHandler;

public class MainActivity extends AppCompatActivity
{
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private void setupSystemUI()
    {
        EsaphAndroidTopBarHelper.setTopBarFullScreenTransparent(MainActivity.this);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        setupSystemUI();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                WorkManager.getInstance().cancelAllWorkByTag("SESSION_KILL");

                NotificationManager nManager = ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE));
                if(nManager != null)
                {
                    nManager.cancelAll();
                }
            }
        }).start();


        CLPreferences preferences = new CLPreferences(getApplicationContext());



        /*
        try
        {
            if(preferences.getUsername().equals("") || preferences.getUsername().isEmpty())
            {
                startActivity(new Intent(MainActivity.this, WelcomeActivity.class));
                finish();
            }
            else
            {
                Log.i(getClass().getName(), "Login in....");
                if(!preferences.getUsername().equals("") && !preferences.getUsername().isEmpty())
                {
                    grantAccessToApp(preferences.getUsername(),
                            preferences.getUID(),
                            preferences.isDemoMode());
                }
            }
        }
        catch(Exception ec)
        {
            Log.i(getClass().getName(), "MainActivity: " + ec);
            startActivity(new Intent(MainActivity.this, WelcomeActivity.class));
            finish();
        }*/

        setupForTrafo2GmbH();
    }


    private void setupForTrafo2GmbH()
    {
        // TODO: 09.03.2020 remove this when finishing app
        CLPreferences preferences = new CLPreferences(getApplicationContext());
        preferences.setUpUser(0, "Subrim", "loleinpasswort",false);

        grantAccessToApp(preferences.getUsername(),
                preferences.getUID(),
                preferences.isDemoMode());
    }

    private void grantAccessToApp(String Username, long LOGGED_UID, boolean isDemo)
    {
        SpotLightLoginSessionHandler.setSpotLightUser(Username, LOGGED_UID, isDemo);
        new Thread(new AppStartPutzKolonne(MainActivity.this)).start();

        Intent mainIntent = new Intent(MainActivity.this, SwipeNavigation.class);
        mainIntent.setAction(SwipeNavigation.extra_ActionNeedLogin);
        startActivity(mainIntent);
        finish();
    }


    /*
    private int[] mColors = new int[]{
            Color.parseColor("#F44336"),
            Color.parseColor("#E91E63"),
            Color.parseColor("#9C27B0"),
            Color.parseColor("#673AB7"),
            Color.parseColor("#3F51B5"),
            Color.parseColor("#2196F3"),
            Color.parseColor("#03A9F4"),
            Color.parseColor("#00BCD4"),
            Color.parseColor("#009688"),
            Color.parseColor("#4CAF50"),
            Color.parseColor("#8BC34A"),
            Color.parseColor("#CDDC39"),
            Color.parseColor("#FFEB3B"),
            Color.parseColor("#FFC107"),
            Color.parseColor("#FF9800"),
            Color.parseColor("#FF5722"),
            Color.parseColor("#795548"),
            Color.parseColor("#9E9E9E"),
            Color.parseColor("#607D8B"),
            Color.parseColor("#FFFFFF")
    };


    private void rSpotTemplates()
    {
        try
        {
            System.out.println("WHITE COLOR: " +    Color.parseColor("#FFFFFF"));
            Random random = new Random();
            for(int counter = 0; counter < 30; counter++)
            {
                JSONObject jsonObject = new JSONObject();
                int randomColor = mColors[random.nextInt(mColors.length)];
                int randomFontFamilie = random.nextInt(SpotTextFontFamlie.fontFamilieResourceIDs.length);

                SpotBackgroundDefinitionBuilder.create(jsonObject).resetToInitState(getApplicationContext()).setBackgroundColor(android.R.color.white);

                SpotTextDefinitionBuilder.create(jsonObject).resetToInitState(getApplicationContext()).setTextColor(randomColor)
                        .setFontFamily(getResources().getResourceEntryName(SpotTextFontFamlie.fontFamilieResourceIDs[randomFontFamilie]));

                Log.i(getClass().getName(), jsonObject.toString());
            }
        }
        catch (Exception ec)
        {
            Log.i("CUSTOM SPOT", "failed" + ec);
        }
    }*/
}
