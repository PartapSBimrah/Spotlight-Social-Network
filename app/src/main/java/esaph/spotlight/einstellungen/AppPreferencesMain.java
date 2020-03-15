/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.einstellungen;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import esaph.spotlight.Esaph.EsaphAndroidTopBar.EsaphAndroidTopBarHelper;
import esaph.spotlight.R;
import esaph.spotlight.StorageManagment.StorageHandler;
import esaph.spotlight.StorageManagment.StorageHandlerProfilbild;
import esaph.spotlight.databases.SQLChats;
import esaph.spotlight.databases.SQLFeed;
import esaph.spotlight.databases.SQLFriends;
import esaph.spotlight.databases.SQLGroups;
import esaph.spotlight.databases.SQLHashtags;
import esaph.spotlight.databases.SQLLifeCloud;
import esaph.spotlight.databases.SQLSticker;
import esaph.spotlight.databases.SQLUploads;
import esaph.spotlight.rechtliches.Datenschutz;
import esaph.spotlight.rechtliches.Haftungsauschluss;
import esaph.spotlight.rechtliches.Impressum;
import esaph.spotlight.rechtliches.mSupport;

public class AppPreferencesMain extends AppCompatActivity
{
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        EsaphAndroidTopBarHelper.setTopBarContentFitInSystemUILightStatus(AppPreferencesMain.this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_preferences_main);

        ImageView imageViewBack = (ImageView) findViewById(R.id.imageViewPreferencesBack);
        imageViewBack.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                finish();
            }
        });

        TextView textViewAusloggen = (TextView) findViewById(R.id.textViewAusloggenOnClick);
        TextView textViewAccountDelete = (TextView) findViewById(R.id.textViewAccountDeleteOnClick);
        TextView textViewSupport = (TextView) findViewById(R.id.textViewSupport);
        TextView textViewImpressum = (TextView) findViewById(R.id.textViewImpressum);
        TextView textViewDatenschutz = (TextView) findViewById(R.id.textViewDatenschutz);
        TextView textViewHaftungsauschluss = (TextView) findViewById(R.id.textViewHaftungsausschluss);
        TextView textViewStorageOptions = (TextView) findViewById(R.id.textViewSpotLightStorageOptions);

        textViewAusloggen.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                try
                {
                    CLPreferences preferences = new CLPreferences(getApplicationContext());
                    preferences.logOut();

                    StorageHandler.dropAllFiles(getApplicationContext());
                    StorageHandlerProfilbild.dropAllFiles(getApplicationContext());

                    SQLFriends friends = new SQLFriends(getApplicationContext());
                    friends.dropTableWatcher();
                    friends.close();

                    SQLChats chats = new SQLChats(getApplicationContext());
                    chats.dropTableChats();
                    chats.close();

                    SQLSticker sqlSticker = new SQLSticker(getApplicationContext());
                    sqlSticker.dropTableStickers();
                    sqlSticker.close();

                    SQLHashtags sqlHashtags = new SQLHashtags(getApplicationContext());
                    sqlHashtags.dropTableHashtags();
                    sqlHashtags.close();

                    SQLLifeCloud sqlLifeCloud = new SQLLifeCloud(getApplicationContext());
                    sqlLifeCloud.dropTableLifeCloud();
                    sqlLifeCloud.close();

                    SQLGroups sqlMemorys = new SQLGroups(getApplicationContext());
                    sqlMemorys.dropAllDataMoments();
                    sqlMemorys.close();

                    SQLUploads sqlUploads = new SQLUploads(getApplicationContext());
                    sqlUploads.dropTables();
                    sqlUploads.close();

                    SQLFeed sqlFeed = new SQLFeed(getApplicationContext());
                    sqlFeed.dropAllData();
                    sqlFeed.close();

                    Intent data = new Intent();
                    data.setAction("LOGOUT");
                    setResult(1325, data);
                    finish();
                }
                catch (Exception ec)
                {
                    Log.i(getClass().getName(), "textViewAusloggen_onClick() failed: " + ec);
                }
            }
        });


        textViewAccountDelete.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                DialogAccountDelete dialogAccountDelete = new DialogAccountDelete(AppPreferencesMain.this);
                TextView textViewDeleteReally = (TextView) dialogAccountDelete.findViewById(R.id.textViewAccountDeleteOnClick);
                textViewDeleteReally.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(final View v)
                    {
                        asyncDeleteAccountComplete = new AsyncDeleteAccountComplete(AppPreferencesMain.this);
                        asyncDeleteAccountComplete.execute();
                    }
                });
                dialogAccountDelete.show();
            }
        });

        textViewSupport.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                startActivity(new Intent(AppPreferencesMain.this, mSupport.class));
            }
        });

        textViewImpressum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v)
            {
                startActivity(new Intent(AppPreferencesMain.this, Impressum.class));
            }
        });

        textViewHaftungsauschluss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v)
            {
                startActivity(new Intent(AppPreferencesMain.this, Haftungsauschluss.class));
            }
        });

        textViewDatenschutz.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                startActivity(new Intent(AppPreferencesMain.this, Datenschutz.class));
            }
        });

        textViewStorageOptions.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                startActivity(new Intent(AppPreferencesMain.this, SpotLightStoragePreferences.class));
            }
        });
    }

    private AsyncDeleteAccountComplete asyncDeleteAccountComplete;

    @Override
    protected void onStop()
    {
        super.onStop();
        if(asyncDeleteAccountComplete != null && asyncDeleteAccountComplete.getStatus() == AsyncTask.Status.RUNNING)
        {
            asyncDeleteAccountComplete.cancel(true);
        }
    }
}
