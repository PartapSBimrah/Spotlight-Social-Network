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
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.net.ssl.SSLSocket;

import esaph.spotlight.EsaphSSLSocket;
import esaph.spotlight.SocketResources;
import esaph.spotlight.StorageManagment.StorageHandler;
import esaph.spotlight.databases.SQLChats;
import esaph.spotlight.einstellungen.CLPreferences;
import esaph.spotlight.navigation.globalActions.ConversationStatusHelper;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.ChatObjects.AudioMessage;
import esaph.spotlight.services.SpotLightMessageConnection.MsgServiceConnection;

import static android.content.Context.BIND_AUTO_CREATE;

public class SendAudioMessageToServer extends Worker
{
    public static final String EXTRA_MSG_ID = "esaph.worker.sendmessagetoserver.MSGID";
    private Context context;

    public SendAudioMessageToServer(@NonNull Context context, @NonNull WorkerParameters workerParams)
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

            long POST_ID = getInputData().getLong(SendAudioMessageToServer.EXTRA_MSG_ID, -1);

            SQLChats sqlChats = new SQLChats(context);
            AudioMessage audioMessage = (AudioMessage) sqlChats.getPostByInternId(POST_ID);

            try
            {
                File file = StorageHandler.getFile(context,
                        StorageHandler.FOLDER__SPOTLIGHT_AUDIO,
                        audioMessage.getAID(),
                        null, StorageHandler.AUDIO_PREFIX);

                if(!StorageHandler.fileExists(file))
                    return Result.failure();

                SocketResources resources = new SocketResources();
                CLPreferences preferences = new CLPreferences(context);

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("USRN", preferences.getUID());
                jsonObject.put("SID", SpotLightLoginSessionHandler.getSpotLightSessionId());
                jsonObject.put("PLSC",  "PLSNA");
                jsonObject.put("FUSRN",  audioMessage.getID_CHAT());
                jsonObject.put("PLINF", audioMessage.getEsaphPloppInformationsJSON());

                SSLSocket sslSocketSendMessage = EsaphSSLSocket.getSSLInstance(context, resources.getServerAddress(), resources.getServerPortPServer());
                BufferedReader reader = new BufferedReader(new InputStreamReader(sslSocketSendMessage.getInputStream()));
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(sslSocketSendMessage.getOutputStream()));
                writer.println(jsonObject.toString());
                writer.flush();


                if(reader.readLine().equals("1"))
                {
                    long length = StorageHandler.fileLength(file);
                    writer.println(length);
                    writer.flush();

                    if(reader.readLine().equals("1"))
                    {
                        byte[] originalBytes = new byte[(int)length];
                        InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
                        OutputStream outputStream = sslSocketSendMessage.getOutputStream();

                        int count;
                        while ((count = inputStream.read(originalBytes)) > 0)
                        {
                            outputStream.write(originalBytes, 0, count);
                            outputStream.flush();
                        }

                        inputStream.close();

                        String NEW_AID = reader.readLine();
                        if(NEW_AID != null && !NEW_AID.isEmpty())
                        {
                            audioMessage.setAID(NEW_AID);
                            audioMessage.setMessageStatus(ConversationStatusHelper.STATUS_SENT);
                            sqlChats.updateStatusByID(audioMessage);

                            if(isBoundSendingConnection && msgServiceConnection != null)
                            {
                                msgServiceConnection.broadCastToAllCallbacksConversationMessageUpdate(audioMessage);
                            }

                            return Result.success();
                        }
                    }
                }

                sslSocketSendMessage.close();
                writer.close();
                reader.close();
            }
            catch (Exception ec)
            {
                try
                {
                    audioMessage.setMessageStatus(ConversationStatusHelper.STATUS_FAILED_TO_SEND_OR_RECEIVE);
                    sqlChats.updateStatusByID(audioMessage);
                }
                catch (Exception e)
                {
                }
                Log.i(getClass().getName(), "SendAudioMessageToServer() failed: " + ec);
            }
            finally
            {
                sqlChats.close();
            }
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "SendAudioMessageToServer() failed sleeping clausel: " + ec);
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
