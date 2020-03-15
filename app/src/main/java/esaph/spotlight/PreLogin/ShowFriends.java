/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.PreLogin;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLSocket;

import esaph.spotlight.EsaphSSLSocket;
import esaph.spotlight.R;
import esaph.spotlight.SocketResources;
import esaph.spotlight.SpotLightUser;
import esaph.spotlight.databases.SQLFriends;
import esaph.spotlight.einstellungen.CLPreferences;
import esaph.spotlight.navigation.SwipeNavigation;
import esaph.spotlight.navigation.globalActions.ServerPolicy;
import esaph.spotlight.navigation.globalActions.SocialFriendNegotiation;
import esaph.spotlight.services.SpotLightMessageConnection.Workers.SpotLightLoginSessionHandler;

public class ShowFriends extends AsyncTask<String, Void, Boolean>
{
    private WeakReference<Activity> activityWeakReference;
    private WeakReference<ShowFriendsDoneListener> showFriendsDoneListenerWeakReference;

    private boolean Synched = false;
    private ProgressDialog progressDialog;

    public interface ShowFriendsDoneListener
    {
        void onFriendsSynched();
        void onFailed();
    }

    public ShowFriends(Activity activity,
                       ShowFriendsDoneListener showFriendsDoneListener)
    {
        this.activityWeakReference = new WeakReference<Activity>(activity);
        this.showFriendsDoneListenerWeakReference = new WeakReference<ShowFriendsDoneListener>(showFriendsDoneListener);
    }

    private List<SocialFriendNegotiation> startSynchronisationFriendAnfragen()
    {
        try
        {
            SocketResources resources = new SocketResources();
            List<SocialFriendNegotiation> anfragen = new ArrayList<>();
            JSONObject jsonObjectFriends = new JSONObject();
            jsonObjectFriends.put("ISC", "SSMD");
            jsonObjectFriends.put("USRN", SpotLightLoginSessionHandler.getLoggedUID());
            jsonObjectFriends.put("SID", SpotLightLoginSessionHandler.getSpotLightSessionId());

            SSLSocket sidSocket = EsaphSSLSocket.getSSLInstance(activityWeakReference.get(), resources.getServerAddress(), resources.getServerPortFServer());
            BufferedReader reader = new BufferedReader(new InputStreamReader(sidSocket.getInputStream()));
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(sidSocket.getOutputStream()));
            writer.println(jsonObjectFriends.toString());
            writer.flush();

            JSONArray jsonObjectUsers = new JSONArray(reader.readLine());
            for(int counter = 0; counter < jsonObjectUsers.length(); counter++)
            {
                JSONObject object = jsonObjectUsers.getJSONObject(counter);
                anfragen.add(new SocialFriendNegotiation(
                        object.getLong("UID"),
                        object.getString("USRN"),
                        object.getString("VORN"),
                        (short) object.getInt("FST"),
                        object.getString("REG")));
            }

            return anfragen;
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "startSynchronisationFriendAnfragen() failed: " + ec);
            return null;
        }
    }

    private ArrayList<SpotLightUser> startSynchronisationFriends()
    {
        try
        {
            SocketResources resources = new SocketResources();
            ArrayList<SpotLightUser> tenChatUsers = new ArrayList<>();
            JSONObject jsonObjectFriends = new JSONObject();
            jsonObjectFriends.put("ISC", "FRIENDS");
            jsonObjectFriends.put("USRN", SpotLightLoginSessionHandler.getLoggedUID());
            jsonObjectFriends.put("SID", SpotLightLoginSessionHandler.getSpotLightSessionId());

            SSLSocket sidSocket = EsaphSSLSocket.getSSLInstance(activityWeakReference.get(), resources.getServerAddress(), resources.getServerPortFServer());
            BufferedReader reader = new BufferedReader(new InputStreamReader(sidSocket.getInputStream()));
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(sidSocket.getOutputStream()));
            writer.println(jsonObjectFriends.toString());
            writer.flush();

            JSONArray jsonObjectUsers = new JSONArray(reader.readLine());
            for(int counter = 0; counter < jsonObjectUsers.length(); counter++)
            {
                JSONObject object = jsonObjectUsers.getJSONObject(counter);

                short WatchingStatus = ServerPolicy.POLICY_DETAIL_CASE_NOTHING;
                if(!object.isNull("WS"))
                {
                    WatchingStatus = (short) object.getInt("WS");
                }

                tenChatUsers.add(new SpotLightUser(
                        object.getLong("UID"),
                        object.getString("Benutzername"),
                        object.getString("Vorname"),
                        object.getLong("Geburtstag"),
                        object.getString("Region"),
                        object.getInt("WF") > 0,
                        WatchingStatus,
                        object.getJSONObject("DESCPL").toString()));
            }

            return tenChatUsers;
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "startSynchronisationFriends() failed: " + ec);
            return null;
        }
    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();

        Activity activity = activityWeakReference.get();
        if(activity != null)
        {
            this.progressDialog = new ProgressDialog(activity);
            this.progressDialog.setCanceledOnTouchOutside(false);
            this.progressDialog.setTitle(activity.getResources().getString(R.string.txt__alertLogIn));
            this.progressDialog.setMessage(activity.getResources().getString(R.string.txt_alertLogInDatenbankAbgleich));
            this.progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            this.progressDialog.show();
        }
    }

    @Override
    protected Boolean doInBackground(String... params)
    {
        try
        {
            SQLFriends sqlWatcher = new SQLFriends(activityWeakReference.get());
            sqlWatcher.dropTableWatcher();

            ArrayList<SpotLightUser> friends = this.startSynchronisationFriends();
            List<SocialFriendNegotiation> anfrageList = this.startSynchronisationFriendAnfragen();
            if(friends != null)
            {
                this.Synched = sqlWatcher.updateWatcher(friends);

                if(anfrageList != null && this.Synched)
                {
                    this.Synched = sqlWatcher.updateAllSocialFriendAnfragen(anfrageList);
                }
                else
                {
                    this.Synched = false;
                }
            }
            else
            {
                this.Synched = false;
            }
            sqlWatcher.close();
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "Exception: " + ec);
            this.Synched = false;
        }
        return Boolean.TRUE;
    }

    @Override
    protected void onPostExecute(Boolean v)
    {
        try
        {
            this.progressDialog.cancel();

            Activity activity = activityWeakReference.get();
            if(activity != null)
            {
                CLPreferences preferencesSy = new CLPreferences(activity);
                preferencesSy.setNeedSynchronisation();

                if(!this.Synched)
                {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
                    dialog.setTitle(activity.getResources().getString(R.string.txt_failedSynchronisationTitel));
                    dialog.setMessage(activity.getResources().getString(R.string.txt_failedSynchronisationDetails));
                    dialog.show();
                }
                else
                {
                    Intent mainIntent = new Intent(activity, SwipeNavigation.class);
                    mainIntent.setAction(LoginActivity.extra_ActionDontNeedLogin);
                    activity.startActivity(mainIntent);
                    activity.finish();
                }
            }
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "ShowFriends onPostExecute() failed: " + ec);
        }
    }
}