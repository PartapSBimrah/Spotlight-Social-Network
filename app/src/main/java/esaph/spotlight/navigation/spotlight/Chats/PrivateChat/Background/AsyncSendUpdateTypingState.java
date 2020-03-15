/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.navigation.spotlight.Chats.PrivateChat.Background;

import android.content.Context;
import android.util.Log;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import javax.net.ssl.SSLSocket;

import esaph.spotlight.EsaphSSLSocket;
import esaph.spotlight.SocketResources;
import esaph.spotlight.services.SpotLightMessageConnection.Workers.SpotLightLoginSessionHandler;

public class AsyncSendUpdateTypingState implements Runnable
{
    private WeakReference<Context> context;
    private long UID_chatPartner;
    private boolean typing;

    public AsyncSendUpdateTypingState(Context context,
                                      long UID_chatPartner,
                                      boolean typing)
    {
        this.context = new WeakReference<Context>(context);
        this.UID_chatPartner = UID_chatPartner;
        this.typing = typing;
    }

    @Override
    public void run()
    {
        boolean successFull = false;
        try
        {
            JSONObject jsonObject = new JSONObject();
            if(typing)
            {
                jsonObject.put("PLSC", "PLUTM");
            }
            else
            {
                jsonObject.put("PLSC", "PLUSTM");
            }
            jsonObject.put("USRN", SpotLightLoginSessionHandler.getLoggedUID());
            jsonObject.put("SID", SpotLightLoginSessionHandler.getSpotLightSessionId());
            jsonObject.put("CP", this.UID_chatPartner); //Immer empfänger

            SocketResources resources = new SocketResources();
            SSLSocket socket = EsaphSSLSocket.getSSLInstance(this.context.get(), resources.getServerAddress(), resources.getServerPortPServer());
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            writer.println(jsonObject.toString());
            writer.flush();

            successFull = true;

            reader.close();
            writer.close();
            socket.close();
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "AsyncSendUpdateTypingState() failed: " + ec);
            successFull = false;
        }
        finally
        {
            if(successFull)
            {
                if(typing)
                {
                    System.out.println("ASUTS: Is typing");
                }
                else
                {
                    System.out.println("ASUTS: Not typing");
                }
            }
        }
    }
}
