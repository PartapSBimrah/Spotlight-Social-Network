package esaph.spotlight.navigation.spotlight.Moments.SortingFragment.SpotLightShowHashtags.Background;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONArray;
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
import esaph.spotlight.databases.SQLHashtags;
import esaph.spotlight.einstellungen.CLPreferences;
import esaph.spotlight.navigation.kamera.PostEditingFragments.EsaphTagging.EsaphHashtag;
import esaph.spotlight.services.SpotLightMessageConnection.Workers.SpotLightLoginSessionHandler;

public class RunnableSyncHashtags implements Runnable
{
    private static final Object obLock = new Object();
    private WeakReference<Context> contextWeakReference;
    private SoftReference<HashtagsSynchFinished> hashtagsSynchFinishedWeakReference;

    public RunnableSyncHashtags(Context context, HashtagsSynchFinished hashtagsSynchFinished)
    {
        this.contextWeakReference = new WeakReference<>(context);
        this.hashtagsSynchFinishedWeakReference = new SoftReference<>(hashtagsSynchFinished);
    }

    public interface HashtagsSynchFinished
    {
        void onNewDataSynched();
        void onFailed();
    }

    private int synchState = -1;

    @Override
    public void run()
    {
        synchronized (RunnableSyncHashtags.obLock)
        {
            try
            {
                SQLHashtags sqlHashtags = new SQLHashtags(this.contextWeakReference.get());
                int count = sqlHashtags.getCountHashtags();
                sqlHashtags.close();

                CLPreferences preferences = new CLPreferences(this.contextWeakReference.get());
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("PLSC", "PLSAH");
                jsonObject.put("USRN", SpotLightLoginSessionHandler.getLoggedUID());
                jsonObject.put("SID", SpotLightLoginSessionHandler.getSpotLightSessionId());
                jsonObject.put("BEH", count);

                SocketResources resources = new SocketResources();
                SSLSocket socket = EsaphSSLSocket.getSSLInstance(this.contextWeakReference.get(), resources.getServerAddress(), resources.getServerPortPServer());
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
                writer.println(jsonObject.toString());
                writer.flush();

                JSONArray jsonArray = new JSONArray(reader.readLine());

                if(jsonArray.length() > 0)
                {
                    synchState = 0;
                }

                SQLHashtags sqlHashtagsAdd = new SQLHashtags(this.contextWeakReference.get());

                for(int counter = 0; counter < jsonArray.length(); counter++)
                {
                    JSONObject jsonObjectLoaded = jsonArray.getJSONObject(counter);
                    EsaphHashtag esaphHashtag = new EsaphHashtag(
                            jsonObjectLoaded.getString("TN"),
                            null,
                            0);

                        /*
                        sqlHashtags.addNewHashtag(jsonObjectLoaded.getString("PF"),
                                jsonObjectLoaded.getString("PID"),
                                esaphHashtag);*/
                }
                sqlHashtagsAdd.close();

            }
            catch (Exception ec)
            {
                Log.i(getClass().getName(), "RunnableSyncHashtags run() failed: " + ec);
            }
            finally
            {
                new Handler(Looper.getMainLooper()).post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        HashtagsSynchFinished hashtagsSynchFinished = hashtagsSynchFinishedWeakReference.get();
                        if(hashtagsSynchFinished != null)
                        {
                            if(synchState == -1)
                            {
                                hashtagsSynchFinished.onFailed();
                            }
                            else if(synchState == 0)
                            {
                                hashtagsSynchFinished.onNewDataSynched();
                            }
                        }
                    }
                });
            }
        }
    }
}
