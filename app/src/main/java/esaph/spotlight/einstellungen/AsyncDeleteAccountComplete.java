/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.einstellungen;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;

import javax.net.ssl.SSLSocket;

import esaph.spotlight.EsaphSSLSocket;
import esaph.spotlight.R;
import esaph.spotlight.SocketResources;
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
import esaph.spotlight.services.SpotLightMessageConnection.Workers.SpotLightLoginSessionHandler;

public class AsyncDeleteAccountComplete extends AsyncTask<Void, Void, Boolean>
{
    private ProgressDialog progressDialog;
    private WeakReference<AppCompatActivity> appCompatActivityWeakReference;

    public AsyncDeleteAccountComplete(AppCompatActivity appCompatActivity)
    {
        this.appCompatActivityWeakReference = new WeakReference<AppCompatActivity>(appCompatActivity);
    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();
        if(this.appCompatActivityWeakReference.get() != null)
        {
            this.progressDialog = new ProgressDialog(this.appCompatActivityWeakReference.get());
            this.progressDialog.setCanceledOnTouchOutside(false);
            this.progressDialog.setTitle(this.appCompatActivityWeakReference.get().getResources().getString(R.string.txt_Account));
            this.progressDialog.setMessage(this.appCompatActivityWeakReference.get().getResources().getString(R.string.txt_AccountDeletingProgress));
            this.progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            this.progressDialog.show();
        }
    }

    @Override
    protected Boolean doInBackground(Void... voids)
    {
        boolean returnMe = false;
        try
        {
            CLPreferences preferences = new CLPreferences(this.appCompatActivityWeakReference.get());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("PLSC", "LRRA");
            jsonObject.put("USRN", SpotLightLoginSessionHandler.getLoggedUID());
            jsonObject.put("SID", SpotLightLoginSessionHandler.getSpotLightSessionId());

            SocketResources resources = new SocketResources();
            SSLSocket socket = EsaphSSLSocket.getSSLInstance(this.appCompatActivityWeakReference.get(), resources.getServerAddress(), resources.getServerPortPServer());
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            writer.println(jsonObject.toString());
            writer.flush();

            String result = reader.readLine();
            if(result.equals("1"))
            {
                returnMe = true;
                preferences.logOut();

                StorageHandler.dropAllFiles(this.appCompatActivityWeakReference.get());
                StorageHandlerProfilbild.dropAllFiles(this.appCompatActivityWeakReference.get());

                SQLFriends friends = new SQLFriends(this.appCompatActivityWeakReference.get());
                friends.dropTableWatcher();
                friends.close();

                SQLChats chats = new SQLChats(this.appCompatActivityWeakReference.get());
                chats.dropTableChats();
                chats.close();

                SQLSticker sqlSticker = new SQLSticker(this.appCompatActivityWeakReference.get());
                sqlSticker.dropTableStickers();
                sqlSticker.close();

                SQLHashtags sqlHashtags = new SQLHashtags(this.appCompatActivityWeakReference.get());
                sqlHashtags.dropTableHashtags();
                sqlHashtags.close();

                SQLGroups sqlMemorys = new SQLGroups(this.appCompatActivityWeakReference.get());
                sqlMemorys.dropAllDataMoments();
                sqlMemorys.close();

                SQLLifeCloud sqlLifeCloud = new SQLLifeCloud(this.appCompatActivityWeakReference.get());
                sqlLifeCloud.dropTableLifeCloud();
                sqlLifeCloud.close();

                SQLUploads sqlUploads = new SQLUploads(this.appCompatActivityWeakReference.get());
                sqlUploads.dropTables();
                sqlUploads.close();

                SQLFeed sqlFeed = new SQLFeed(this.appCompatActivityWeakReference.get());
                sqlFeed.dropAllData();
                sqlFeed.close();
            }
            socket.close();
            writer.close();
            reader.close();
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "AsyncDeleteAccountComplete run() failed: " + ec);
        }

        return returnMe;
    }


    @Override
    protected void onPostExecute(Boolean aBoolean)
    {
        super.onPostExecute(aBoolean);
        this.progressDialog.cancel();

        if(aBoolean)
        {
            if(this.appCompatActivityWeakReference.get() != null)
            {
                Intent data = new Intent();
                data.setAction("LOGOUT");
                appCompatActivityWeakReference.get().setResult(1325, data);
                appCompatActivityWeakReference.get().finish();
            }
        }
        else
        {
            if(this.appCompatActivityWeakReference.get() != null)
            {
                AlertDialog.Builder dialog = new AlertDialog.Builder(this.appCompatActivityWeakReference.get());
                dialog.setTitle(this.appCompatActivityWeakReference.get().getResources().getString(R.string.txt_Account));
                dialog.setMessage(this.appCompatActivityWeakReference.get().getResources().getString(R.string.txt_AccountDeletingFailed));
                dialog.show();
            }
        }
    }
}
