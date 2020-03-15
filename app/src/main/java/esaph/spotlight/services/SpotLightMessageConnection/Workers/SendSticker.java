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
import esaph.spotlight.databases.SQLSticker;
import esaph.spotlight.navigation.globalActions.ConversationStatusHelper;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.ChatObjects.EsaphStickerChatObject;
import esaph.spotlight.services.SpotLightMessageConnection.MsgServiceConnection;

import static android.content.Context.BIND_AUTO_CREATE;

public class SendSticker extends Worker
{
    public static final String EXTRA_STICKER_ID = "esaph.worker.SendSticker.LSID";
    public static final String EXTRA_STICKER_PACK_ID = "esaph.worker.SendSticker.LSPID";
    public static final String EXTRA_STID = "esaph.worker.SendSticker.STID";
    public static final String EXTRA_MSG_ID = "esaph.worker.sendmessagetoserver.MSGID";
    private Context context;
    private long LSID;
    private long LSPID;
    private String STID;

    public SendSticker(@NonNull Context context, @NonNull WorkerParameters workerParams)
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

            LSID = getInputData().getLong(SendSticker.EXTRA_STICKER_ID, -1);
            LSPID = getInputData().getLong(SendSticker.EXTRA_STICKER_PACK_ID, -1);
            STID = getInputData().getString(SendSticker.EXTRA_STID);

            long POST_ID = getInputData().getLong(SendSticker.EXTRA_MSG_ID, -1);

            SQLSticker sqlSticker = new SQLSticker(context);
            SQLChats sqlChats = new SQLChats(context);
            EsaphStickerChatObject esaphStickerChatObject = (EsaphStickerChatObject) sqlChats.getPostByInternId(POST_ID);
            String mStickerPackName = sqlSticker.getStickerPack(esaphStickerChatObject
                    .getEsaphSpotLightSticker()
                    .getSTICKER_PACK_ID())
                    .getPACK_NAME();

            if(esaphStickerChatObject == null)
                return Result.failure();

            try
            {
                File file = StorageHandler.getFile(context,
                        StorageHandler.FOLDER__SPOTLIGHT_STICKER,
                        STID,
                        null,
                        StorageHandler.STICKER_PREFIX);

                if(!StorageHandler.fileExists(file))
                    return Result.failure();

                SocketResources resources = new SocketResources();

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("USRN", SpotLightLoginSessionHandler.getLoggedUID());
                jsonObject.put("SID", SpotLightLoginSessionHandler.getSpotLightSessionId());
                jsonObject.put("PLSC",  "PLSS");
                jsonObject.put("LSID", LSID);
                jsonObject.put("LSPID", LSPID);
                jsonObject.put("PN", mStickerPackName); // TODO: 29.01.2019 i dont like it, the packname should only be sent, when the server need it.
                jsonObject.put("FUSRN",  esaphStickerChatObject.getID_CHAT());
                jsonObject.put("PLINF", esaphStickerChatObject.getEsaphPloppInformationsJSON());
                jsonObject.put("STID", esaphStickerChatObject.getEsaphSpotLightSticker().getIMAGE_ID());

                SSLSocket sslSocketSendMessage = EsaphSSLSocket.getSSLInstance(context, resources.getServerAddress(), resources.getServerPortPServer());
                BufferedReader reader = new BufferedReader(new InputStreamReader(sslSocketSendMessage.getInputStream()));
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(sslSocketSendMessage.getOutputStream()));
                writer.println(jsonObject.toString());
                writer.flush();

                String resultStickerWasAlreadyUploadedOrNot = reader.readLine();
                if(resultStickerWasAlreadyUploadedOrNot.equals("0")) //Sticker not exists, upload it.
                {
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

                            JSONObject jsonObjectServerReply = new JSONObject(reader.readLine());

                            outputStream.close();
                            inputStream.close();

                            long SERVER_LSID = jsonObjectServerReply.optLong("LSID");
                            long SERVER_LSPID = jsonObjectServerReply.optLong("LSPID");
                            String SERVER_ST_ID = jsonObjectServerReply.optString("STID");

                            if(SERVER_ST_ID.isEmpty())
                            {
                                return Result.failure();
                            }

                            sqlSticker.updateStickerMessageLSIDAndPacket(esaphStickerChatObject,
                                    SERVER_LSID,
                                    SERVER_LSPID);

                            sqlChats.updateStickerMessageLSIDAndPacket(esaphStickerChatObject,
                                    SERVER_LSID,
                                    SERVER_LSPID);

                            StorageHandler.updateInternPidWithServerPid(context,
                                    esaphStickerChatObject.getEsaphSpotLightSticker().getIMAGE_ID(), // TODO: 13.08.2019 need to test this
                                    SERVER_ST_ID,
                                    StorageHandler.FOLDER__SPOTLIGHT_STICKER);

                            esaphStickerChatObject.getEsaphSpotLightSticker().setSTICKER_ID(SERVER_LSID);
                            esaphStickerChatObject.getEsaphSpotLightSticker().setSTICKER_PACK_ID(SERVER_LSPID);
                            esaphStickerChatObject.setMessageStatus(ConversationStatusHelper.STATUS_SENT);

                            sqlChats.updateStatusByID(esaphStickerChatObject);

                            if(isBoundSendingConnection && msgServiceConnection != null)
                            {
                                msgServiceConnection.broadCastToAllCallbacksConversationMessageUpdate(esaphStickerChatObject);
                            }

                            return Result.success();
                        }
                    }
                }
                else if(resultStickerWasAlreadyUploadedOrNot.equals("1")) //Sticker send. was already uplaoded so, dont need anthing just mark it as sent.
                {
                    esaphStickerChatObject.setMessageStatus(ConversationStatusHelper.STATUS_SENT);
                    sqlChats.updateStatusByID(esaphStickerChatObject);

                    if(isBoundSendingConnection && msgServiceConnection != null)
                    {
                        msgServiceConnection.broadCastToAllCallbacksConversationMessageUpdate(esaphStickerChatObject);
                    }
                    return Result.success();
                }

                sslSocketSendMessage.close();
                writer.close();
                reader.close();
            }
            catch (Exception ec)
            {
                try
                {
                    esaphStickerChatObject.setMessageStatus(ConversationStatusHelper.STATUS_FAILED_TO_SEND_OR_RECEIVE);
                    sqlChats.updateStatusByID(esaphStickerChatObject);
                }
                catch (Exception e)
                {
                }
                Log.i(getClass().getName(), "SendSticker() failed: " + ec);
            }
            finally
            {
                sqlChats.close();
                sqlSticker.close();
            }
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "SendSticker() failed sleeping clausel: " + ec);
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
