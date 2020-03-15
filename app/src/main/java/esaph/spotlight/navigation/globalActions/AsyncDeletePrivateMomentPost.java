/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.navigation.globalActions;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;

import javax.net.ssl.SSLSocket;

import esaph.spotlight.EsaphSSLSocket;
import esaph.spotlight.SocketResources;
import esaph.spotlight.databases.SQLChats;
import esaph.spotlight.navigation.spotlight.Chats.Messages.ConversationMessage;
import esaph.spotlight.navigation.spotlight.Moments.MomentsFragmentBroadcasts;
import esaph.spotlight.services.SpotLightMessageConnection.Workers.SpotLightLoginSessionHandler;

public class AsyncDeletePrivateMomentPost implements Runnable
{
    private WeakReference<Context> contextWeakReference;
    private SoftReference<PostDeleteListener> postDeleteListenerSoftReference;
    private ConversationMessage conversationMessage;

    public AsyncDeletePrivateMomentPost(Context context,
                                        PostDeleteListener postDeleteListener,
                                        ConversationMessage conversationMessage)
    {
        this.contextWeakReference = new WeakReference<Context>(context);
        this.postDeleteListenerSoftReference = new SoftReference<PostDeleteListener>(postDeleteListener);
        this.conversationMessage = conversationMessage;
    }

    public interface PostDeleteListener extends Serializable
    {
        void onDeletedSuccess(ConversationMessage conversationMessage);
        void onFailedDelete(ConversationMessage conversationMessage);
    }

    private boolean wasDeletedSuccessFully = false;

    @Override
    public void run()
    {
        try
        {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("PLSC", "PLRPP");
            jsonObject.put("USRN", SpotLightLoginSessionHandler.getLoggedUID());
            jsonObject.put("SID", SpotLightLoginSessionHandler.getSpotLightSessionId());
            jsonObject.put("FUID", this.conversationMessage.getID_CHAT());
            jsonObject.put("PPID", this.conversationMessage.getMESSAGE_ID_SERVER());

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

            if(Thread.interrupted())
            {
                return;
            }

            String result = reader.readLine();
            if(result.equals("1")) //DELETED
            {
                SQLChats sqlChats = new SQLChats(this.contextWeakReference.get());
                sqlChats.deletePostInChat(this.conversationMessage.getMESSAGE_ID(), this.conversationMessage.getID_CHAT());
                sqlChats.close();
                wasDeletedSuccessFully = true;
            }
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "AsyncDeletePrivateMomentPost() failed: " + ec);
        }
        finally
        {
            final PostDeleteListener postDeleteListener = postDeleteListenerSoftReference.get();
            ((Activity) contextWeakReference.get()).runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    if(wasDeletedSuccessFully)
                    {
                        Context context = contextWeakReference.get();
                        if(context != null)
                        {
                            Intent intent = new Intent();
                            intent.setAction(MomentsFragmentBroadcasts.ACTION_UPDATE_GALLERY);
                            context.sendBroadcast(intent);
                        }

                        if(postDeleteListener != null)
                        {
                            postDeleteListener.onDeletedSuccess(conversationMessage);
                        }
                    }
                    else
                    {
                        if(postDeleteListener != null)
                        {
                            postDeleteListener.onFailedDelete(conversationMessage);
                        }
                    }
                }
            });
        }
    }
}
