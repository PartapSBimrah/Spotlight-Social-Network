/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.navigation.spotlight.PublicPostsNavigation.PublicWorld.Background;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.net.ssl.SSLSocket;

import esaph.spotlight.EsaphSSLSocket;
import esaph.spotlight.SocketResources;
import esaph.spotlight.navigation.kamera.PostEditingFragments.EsaphTagging.EsaphHashtag;
import esaph.spotlight.navigation.spotlight.PublicPostsNavigation.Model.PublicConversationMessage;
import esaph.spotlight.services.SpotLightMessageConnection.Workers.SpotLightLoginSessionHandler;

public class LoadMorePublicWorldFeed implements Runnable
{
    private static AtomicBoolean obLock = new AtomicBoolean(false);
    private WeakReference<Context> contextWeakReference ;
    private SoftReference<PublicWorldFeedWaiter> publicWorldFeedWaiterSoftReference;
    private int startFrom;

    public LoadMorePublicWorldFeed(Context context, PublicWorldFeedWaiter publicWorldFeedWaiter,
                                   int startFrom)
    {
        this.contextWeakReference = new WeakReference<>(context);
        this.publicWorldFeedWaiterSoftReference = new SoftReference<>(publicWorldFeedWaiter);
        this.startFrom = startFrom;
    }

    public interface PublicWorldFeedWaiter
    {
        void onDataFetched(List<PublicConversationMessage> publicConversationMessageList);
    }


    @Override
    public void run()
    {
        if(!LoadMorePublicWorldFeed.obLock.compareAndSet(false, true))
            return;


        final List<PublicConversationMessage> list = new ArrayList<>();

        try
        {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("PLSC", "PLLMPP");
            jsonObject.put("USRN", SpotLightLoginSessionHandler.getLoggedUID());
            jsonObject.put("SID", SpotLightLoginSessionHandler.getSpotLightSessionId());
            jsonObject.put("ST", this.startFrom);

            if(Thread.interrupted())
            {
                return;
            }

            SocketResources resources = new SocketResources();
            SSLSocket socket = EsaphSSLSocket.getSSLInstance(this.contextWeakReference.get(), resources.getServerAddress(), resources.getServerPortPServer());
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            writer.println(jsonObject.toString());
            writer.flush();

            JSONArray jsonArray = new JSONArray(reader.readLine());
            int length = jsonArray.length();
            for(int counter = 0; counter < length; counter++)
            {
                JSONObject object = jsonArray.getJSONObject(counter);

                ArrayList<EsaphHashtag> esaphHashtag = new ArrayList<>();
                JSONArray jsonArrayHashtags = object.getJSONArray("ARR_EHT");
                for (int counterHashtag = 0; counterHashtag < jsonArrayHashtags.length(); counterHashtag++)
                {
                    JSONObject jsonObjectHashtag = jsonArrayHashtags.getJSONObject(counterHashtag);
                    esaphHashtag.add(new EsaphHashtag(jsonObjectHashtag.getString("TAG"),
                            null,
                            0));
                }

                PublicConversationMessage publicConversationMessage
                        = new PublicConversationMessage(
                        object.getLong("UID"),
                        object.getLong("PPID"),
                        object.getString("USRN"),
                        esaphHashtag,
                        object.getString("DESC"),
                        object.getString("PID"),
                        object.getLong("TI"),
                        (short)object.getInt("TY"),
                        object.getString("CC"),
                        object.getString("CS"),
                        object.getString("CSH"),
                        object.getBoolean("ISAV")
                );

                list.add(publicConversationMessage);
            }
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "LoadMorePublicWorldFeed() failed: " + ec);
        }
        finally
        {
            new Handler()
                    .post(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            PublicWorldFeedWaiter publicWorldFeedWaiter = publicWorldFeedWaiterSoftReference.get();
                            if(publicWorldFeedWaiter != null)
                            {
                                publicWorldFeedWaiter.onDataFetched(list);
                            }
                            obLock.set(false);
                        }
                    });
        }
    }
}
