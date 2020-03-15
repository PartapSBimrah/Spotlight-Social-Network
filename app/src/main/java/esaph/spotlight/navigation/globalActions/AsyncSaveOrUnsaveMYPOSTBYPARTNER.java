/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.navigation.globalActions;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;

import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import javax.net.ssl.SSLSocket;
import esaph.spotlight.EsaphSSLSocket;
import esaph.spotlight.databases.SQLChats;
import esaph.spotlight.SocketResources;
import esaph.spotlight.navigation.spotlight.Moments.UserSeenOrSavedMoment;
import esaph.spotlight.services.SpotLightMessageConnection.Workers.SpotLightLoginSessionHandler;

public class AsyncSaveOrUnsaveMYPOSTBYPARTNER extends AsyncTask<Void, Integer, Boolean>
{
    private WeakReference<SaveOrUnsaveMYPOSTBYPARTNERListener> saveOrUnsaveMYPOSTBYPARTNERListenerWeakReference;
    private WeakReference<Context>context;
    private UserSeenOrSavedMoment userSeenOrSavedMoment;
    private WeakReference<ProgressBar> progressBarWeakReference;
    private int saveStatus = -1;

    public AsyncSaveOrUnsaveMYPOSTBYPARTNER(Context context,
                                            SaveOrUnsaveMYPOSTBYPARTNERListener saveOrUnsaveMYPOSTBYPARTNERListener,
                                            UserSeenOrSavedMoment userSeenOrSavedMoment,
                                            ProgressBar progressBar)
    {
        this.progressBarWeakReference = new WeakReference<ProgressBar>(progressBar);
        this.context = new WeakReference<Context>(context);
        this.userSeenOrSavedMoment = userSeenOrSavedMoment;
        this.saveOrUnsaveMYPOSTBYPARTNERListenerWeakReference = new WeakReference<SaveOrUnsaveMYPOSTBYPARTNERListener>(saveOrUnsaveMYPOSTBYPARTNERListener);
    }


    public interface SaveOrUnsaveMYPOSTBYPARTNERListener
    {
        void onUpdate(UserSeenOrSavedMoment userSeenOrSavedMoment);
        void onFatalError(UserSeenOrSavedMoment userSeenOrSavedMoment);
    }


    @Override
    protected void onProgressUpdate(final Integer... values)
    {
        super.onProgressUpdate(values);

        new Handler(Looper.getMainLooper()).post(new Runnable()
        {
            @Override
            public void run()
            {
                ProgressBar progressBar = progressBarWeakReference.get();
                if(progressBar != null)
                {
                    ObjectAnimator animation = ObjectAnimator.ofInt(progressBar, "progress", progressBar.getProgress(), values[0]);
                    animation.setDuration(100);
                    animation.setInterpolator(new DecelerateInterpolator());
                    animation.start();
                }
            }
        });
    }


    @Override
    protected Boolean doInBackground(Void... params)
    {
        try
        {
            onProgressUpdate(progressBarWeakReference.get().getProgress() + 25);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("PLSC", "PLGFIB");
            jsonObject.put("USRN", SpotLightLoginSessionHandler.getLoggedUID());
            jsonObject.put("SID", SpotLightLoginSessionHandler.getSpotLightSessionId());
            jsonObject.put("REC_ID", this.userSeenOrSavedMoment.getKEY_CHAT());
            jsonObject.put("PPID", this.userSeenOrSavedMoment.getMESSAGE_ID_SERVER());

            SocketResources resources = new SocketResources();
            SSLSocket socket = EsaphSSLSocket.getSSLInstance(this.context.get(), resources.getServerAddress(), resources.getServerPortPServer());
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            writer.println(jsonObject.toString());
            writer.flush();
            onProgressUpdate(progressBarWeakReference.get().getProgress() + 25);

            String result = reader.readLine();
            onProgressUpdate(progressBarWeakReference.get().getProgress() + 25);
            if(result.equals("1")) //ENTSPEICHERT
            {
                this.saveStatus = 1;
                SQLChats sqlChats = new SQLChats(this.context.get());
                sqlChats.removePostSaved(this.userSeenOrSavedMoment.getID_SAVED());
                sqlChats.close();
                return Boolean.TRUE;
            }
            else if(result.equals("2")) //Wieder freigegeben.
            {
                this.saveStatus = 2;
                SQLChats sqlChats = new SQLChats(this.context.get());
                sqlChats.insertPostSaved(
                        this.userSeenOrSavedMoment.getKEY_CHAT(),
                        this.userSeenOrSavedMoment.getID_POST());
                sqlChats.close();
                return Boolean.TRUE;
            }
            return Boolean.FALSE;
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "AsyncSaveOrUnsaveMYPOSTBYPARTNER() failed: " + ec);
            return Boolean.FALSE;
        }
    }


    @Override
    protected void onPostExecute(Boolean aBoolean)
    {
        super.onPostExecute(aBoolean);
        try
        {
            onProgressUpdate(progressBarWeakReference.get().getProgress() + 25);

            if(this.saveStatus > -1 && aBoolean)
            {
                if(saveStatus == 2)
                {
                    SaveOrUnsaveMYPOSTBYPARTNERListener saveOrUnsaveMYPOSTBYPARTNERListener = saveOrUnsaveMYPOSTBYPARTNERListenerWeakReference.get();
                    if(saveOrUnsaveMYPOSTBYPARTNERListener != null)
                    {
                        userSeenOrSavedMoment.setPartnerSaved(true);
                        saveOrUnsaveMYPOSTBYPARTNERListener.onUpdate(this.userSeenOrSavedMoment);
                    }
                }
                else if(this.saveStatus == 1)
                {
                    SaveOrUnsaveMYPOSTBYPARTNERListener saveOrUnsaveMYPOSTBYPARTNERListener = saveOrUnsaveMYPOSTBYPARTNERListenerWeakReference.get();
                    if(saveOrUnsaveMYPOSTBYPARTNERListener != null)
                    {
                        userSeenOrSavedMoment.setPartnerSaved(false);
                        saveOrUnsaveMYPOSTBYPARTNERListener.onUpdate(this.userSeenOrSavedMoment);
                    }
                }
            }
            else
            {
                this.saveOrUnsaveMYPOSTBYPARTNERListenerWeakReference.get().onFatalError(this.userSeenOrSavedMoment);
            }
        }
        catch (Exception ec)
        {
        }
    }
}
