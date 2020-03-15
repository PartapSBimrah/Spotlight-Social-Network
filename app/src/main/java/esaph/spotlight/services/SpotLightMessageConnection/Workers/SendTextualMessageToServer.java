/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.services.SpotLightMessageConnection.Workers;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import androidx.annotation.NonNull;
import android.util.Log;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.net.ssl.SSLSocket;

import androidx.work.Worker;
import androidx.work.WorkerParameters;
import esaph.spotlight.EsaphSSLSocket;
import esaph.spotlight.SocketResources;
import esaph.spotlight.databases.SQLChats;
import esaph.spotlight.einstellungen.CLPreferences;
import esaph.spotlight.navigation.globalActions.ConversationStatusHelper;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.ChatObjects.ChatTextMessage;
import esaph.spotlight.services.SpotLightMessageConnection.MsgServiceConnection;

import static android.content.Context.BIND_AUTO_CREATE;

public class SendTextualMessageToServer extends Worker
{
    public static final String EXTRA_MSG_ID = "esaph.worker.sendmessagetoserver.MSGID";
    private Context context;

    public SendTextualMessageToServer(@NonNull Context context, @NonNull WorkerParameters workerParams)
    {
        super(context, workerParams);
        this.context = context;
    }

    private MsgServiceConnection msgServiceConnection;
    private boolean isBoundSendingConnection = false;

    private ServiceConnection myConnectionSendingText = new ServiceConnection()
    {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service)
        {
            MsgServiceConnection.MyLocalBinder binder = (MsgServiceConnection.MyLocalBinder) service;
            msgServiceConnection = binder.getService(null);
            isBoundSendingConnection = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0)
        {
            isBoundSendingConnection = false;
        }
    };

    @NonNull
    @Override
    public Result doWork()
    {
        try
        {
            if(myConnectionSendingText != null && context != null)
            {
                Intent intent = new Intent(context, MsgServiceConnection.class);
                context.bindService(intent, myConnectionSendingText, BIND_AUTO_CREATE);
            }

            long POST_ID = getInputData().getLong(SendTextualMessageToServer.EXTRA_MSG_ID, -1);

            SQLChats sqlChats = new SQLChats(context);
            ChatTextMessage chatTextMessage = (ChatTextMessage) sqlChats.getPostByInternId(POST_ID);

            try
            {
                System.out.println("POOL DEBUG PRIVATE CHAT: TASK STARTED " + this);
                SocketResources resources = new SocketResources();
                CLPreferences preferences = new CLPreferences(context);

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("USRN", preferences.getUID());
                jsonObject.put("SID", SpotLightLoginSessionHandler.getSpotLightSessionId());
                jsonObject.put("PLSC","PLSPTM");
                jsonObject.put("MSG", chatTextMessage.getTextMessage());
                jsonObject.put("EMPF", chatTextMessage.getID_CHAT());
                jsonObject.put("PLINF", chatTextMessage.getEsaphPloppInformationsJSON());

                SSLSocket sslSocketSendMessage = EsaphSSLSocket.getSSLInstance(context, resources.getServerAddress(), resources.getServerPortPServer());
                BufferedReader reader = new BufferedReader(new InputStreamReader(sslSocketSendMessage.getInputStream()));
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(sslSocketSendMessage.getOutputStream()));
                writer.println(jsonObject.toString());
                writer.flush();

                long newHash = Long.parseLong(reader.readLine());

                chatTextMessage.setMessageStatus(ConversationStatusHelper.STATUS_SENT);
                sqlChats.updateStatusByID(chatTextMessage);

                if(isBoundSendingConnection && msgServiceConnection != null)
                {
                    msgServiceConnection.broadCastToAllCallbacksConversationMessageUpdate(chatTextMessage);
                }

                sslSocketSendMessage.close();
                writer.close();
                reader.close();

                return Result.success();
            }
            catch (Exception ec)
            {
                try
                {
                    chatTextMessage.setMessageStatus(ConversationStatusHelper.STATUS_FAILED_TO_SEND_OR_RECEIVE);
                    sqlChats.updateStatusByID(chatTextMessage);
                }
                catch (Exception e)
                {
                }
                Log.i(getClass().getName(), "SendTextualMessageToServer() failed: " + ec);
                System.out.println("POOL DEBUG PRIVATE CHAT: TASK FAILED " + this + ", " + ec);
            }
            finally {
                sqlChats.close();
                System.out.println("POOL DEBUG PRIVATE CHAT: TASK " + this + " FINISHED.");
            }
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "SendTextualMessageToServer() failed sleeping clausel: " + ec);
        }
        finally
        {
            try
            {
                if(myConnectionSendingText != null
                        && context != null
                        && isBoundSendingConnection)
                {
                    context.unbindService(myConnectionSendingText);
                }
            }
            catch (Exception ec)
            {

            }
        }

        return Result.retry();
    }
}
