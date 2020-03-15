/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.navigation.spotlight.PublicPostsNavigation.UserProfile;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;

import javax.net.ssl.SSLSocket;

import esaph.spotlight.EsaphSSLSocket;
import esaph.spotlight.SocketResources;
import esaph.spotlight.navigation.spotlight.PublicPostsNavigation.UserProfile.Model.UserAccountProfile;
import esaph.spotlight.services.SpotLightMessageConnection.Workers.SpotLightLoginSessionHandler;

public class RunnableLoadUserProfile implements Runnable
{
    private WeakReference<Context> contextWeakReference;
    private SoftReference<ProfilLoaderListener> profilLoaderListenerSoftReference;
    private long UID;

    public RunnableLoadUserProfile(Context context, long UID, ProfilLoaderListener profilLoaderListener)
    {
        this.UID = UID;
        this.contextWeakReference = new WeakReference<>(context);
        this.profilLoaderListenerSoftReference = new SoftReference<>(profilLoaderListener);
    }

    public interface ProfilLoaderListener
    {
        void onProfileLoaded(UserAccountProfile userAccountProfile);
        void onFailed();
    }

    private UserAccountProfile userAccountProfile;

    @Override
    public void run()
    {
        try
        {
            SocketResources resources = new SocketResources();
            JSONObject jsonObjectFriends = new JSONObject();
            jsonObjectFriends.put("ISC", "PBGU");
            jsonObjectFriends.put("USRN", SpotLightLoginSessionHandler.getLoggedUID());
            jsonObjectFriends.put("FUSRN", UID);
            jsonObjectFriends.put("SID", SpotLightLoginSessionHandler.getSpotLightSessionId());

            SSLSocket sidSocket = EsaphSSLSocket.getSSLInstance(contextWeakReference.get(), resources.getServerAddress(), resources.getServerPortFServer());
            BufferedReader reader = new BufferedReader(new InputStreamReader(sidSocket.getInputStream()));
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(sidSocket.getOutputStream()));
            writer.println(jsonObjectFriends.toString());
            writer.flush();

            JSONObject jsonObject = new JSONObject(reader.readLine());
            userAccountProfile = new UserAccountProfile(
                    UID,
                    jsonObject.getString("USRN"),
                    jsonObject.getString("PBPID"),
                    jsonObject.getInt("PBPolice"),
                    jsonObject.getString("Vorname"),
                    jsonObject.getString("PDesc"),
                    jsonObject.getString("FOW"),
                    jsonObject.getString("FOL"),
                    "0K"
            );
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "RunnableLoadUserProfile() failed: " + ec);
        }
        finally
        {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run()
                {
                    ProfilLoaderListener profilLoaderListener = profilLoaderListenerSoftReference.get();

                    if(userAccountProfile != null)
                    {
                        if(profilLoaderListener != null)
                        {
                            profilLoaderListener.onProfileLoaded(userAccountProfile);
                        }
                    }
                    else
                    {
                        if(profilLoaderListener != null)
                        {
                            profilLoaderListener.onFailed();
                        }
                    }
                }
            });
        }
    }
}
