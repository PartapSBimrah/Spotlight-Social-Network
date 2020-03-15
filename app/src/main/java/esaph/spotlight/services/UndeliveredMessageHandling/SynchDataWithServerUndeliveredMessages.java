/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.services.UndeliveredMessageHandling;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import javax.net.ssl.SSLSocket;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;
import esaph.spotlight.EsaphSSLSocket;
import esaph.spotlight.SocketResources;
import esaph.spotlight.databases.SQLChats;
import esaph.spotlight.navigation.globalActions.PostSeenUntransmitted;
import esaph.spotlight.navigation.spotlight.Chats.Messages.ConversationMessage;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.Background.RunnableChatOpenened;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.ChatObjects.AudioMessage;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.ChatObjects.ChatTextMessage;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.ChatObjects.EsaphAndroidSmileyChatObject;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.ChatObjects.EsaphStickerChatObject;
import esaph.spotlight.navigation.spotlight.GroupChats.GroupConversationMessage;
import esaph.spotlight.services.SpotLightMessageConnection.Workers.SpotLightLoginSessionHandler;
import esaph.spotlight.services.SpotLightMessageConnection.Workers.SendAudioMessageToServer;
import esaph.spotlight.services.SpotLightMessageConnection.Workers.SendEmojie;
import esaph.spotlight.services.SpotLightMessageConnection.Workers.SendTextualMessageToServer;
import esaph.spotlight.services.SpotLightMessageConnection.Workers.SendSticker;

public class SynchDataWithServerUndeliveredMessages implements Runnable
{
    private Context context;

    public SynchDataWithServerUndeliveredMessages(Context appCompatActivity)
    {
        this.context = appCompatActivity;
    }


    @Override
    public void run()
    {
        try
        {
            SQLChats sqlChats = new SQLChats(context);

            List<ConversationMessage> listUnsentChatTextMessages = sqlChats.getAllUnsentMessages();

            List<PostSeenUntransmitted> listUntrasmittedOpenedPosts = sqlChats.getPostThatNeedToBeTransmitedToServer(getTimeMinus24Hours());
            sqlChats.close();


            List<UndeliveredChatSeenMessage> undeliveredChatSeenMessages = sqlChats.getUndeliveredChatSeenMessages();

            transmitteUnsentMessages(listUnsentChatTextMessages);
            transmittePostSeenInChat(listUntrasmittedOpenedPosts);
            transmitteChatOpenedMessages(undeliveredChatSeenMessages);
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "SendInformations: run() failed: " + ec);
        }
    }

    private long getTimeMinus24Hours()
    {
        final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.HOUR_OF_DAY, -24);
        return calendar.getTimeInMillis();
    }

    private void transmitteChatOpenedMessages(List<UndeliveredChatSeenMessage> list)
    {
        for(int counter = 0; counter < list.size(); counter++)
        {
            try
            {
                Thread t1= new Thread(new RunnableChatOpenened(context, list.get(counter)));
                t1.start();
                t1.join();
            }
            catch (Exception e)
            {

            }
        }
    }

    private void transmitteUnsentMessages(List<ConversationMessage> list)
    {
        try
        {
            for(int counter = 0; counter < list.size(); counter++)
            {
                ConversationMessage conversationMessage = list.get(counter);
                if(conversationMessage instanceof ChatTextMessage)
                {
                    sendMessageToServer((ChatTextMessage)conversationMessage);
                }
                else if(conversationMessage instanceof AudioMessage)
                {
                    sendMessageToServer((AudioMessage)conversationMessage);
                }
                else if(conversationMessage instanceof EsaphAndroidSmileyChatObject)
                {
                    sendMessageToServer((EsaphAndroidSmileyChatObject)conversationMessage);
                }
                else if(conversationMessage instanceof EsaphStickerChatObject)
                {
                    sendMessageToServer((EsaphStickerChatObject)conversationMessage);
                }
            }
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "transmitteUnsentMessages() failed: " + ec);
        }
    }


    public void sendMessageToServer(final ChatTextMessage chatTextMessage)
    {
        Constraints constraints = new Constraints.Builder()
                .setRequiresCharging(false)
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        Data data = new Data.Builder()
                .putLong(SendTextualMessageToServer.EXTRA_MSG_ID, chatTextMessage.getMESSAGE_ID())
                .build();

        OneTimeWorkRequest simpleRequest = new OneTimeWorkRequest.Builder(SendTextualMessageToServer.class)
                .setInputData(data)
                .setConstraints(constraints)
                .setBackoffCriteria(BackoffPolicy.LINEAR,
                        WorkRequest.MIN_BACKOFF_MILLIS,
                        TimeUnit.MILLISECONDS)
                .build();

        WorkManager.getInstance().beginUniqueWork(""+chatTextMessage.getMESSAGE_ID(),
                ExistingWorkPolicy.KEEP,
                simpleRequest).enqueue();
    }


    public void sendMessageToServer(final AudioMessage audioMessage)
    {
        Constraints constraints = new Constraints.Builder()
                .setRequiresCharging(false)
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        Data data = new Data.Builder()
                .putLong(SendAudioMessageToServer.EXTRA_MSG_ID, audioMessage.getMESSAGE_ID())
                .build();

        OneTimeWorkRequest simpleRequest = new OneTimeWorkRequest.Builder(SendAudioMessageToServer.class)
                .setInputData(data)
                .setConstraints(constraints)
                .setBackoffCriteria(BackoffPolicy.LINEAR,
                        WorkRequest.MIN_BACKOFF_MILLIS,
                        TimeUnit.MILLISECONDS)
                .build();

        WorkManager.getInstance().beginUniqueWork(""+audioMessage.getMESSAGE_ID(),
                ExistingWorkPolicy.KEEP,
                simpleRequest).enqueue();
    }


    public void sendMessageToServer(final EsaphStickerChatObject esaphStickerChatObject)
    {
        Constraints constraints = new Constraints.Builder()
                .setRequiresCharging(false)
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        Data data = new Data.Builder()
                .putLong(SendSticker.EXTRA_STICKER_ID, esaphStickerChatObject.getEsaphSpotLightSticker().getSTICKER_ID())
                .putLong(SendSticker.EXTRA_STICKER_PACK_ID, esaphStickerChatObject.getEsaphSpotLightSticker().getSTICKER_PACK_ID())
                .putLong(SendSticker.EXTRA_MSG_ID, esaphStickerChatObject.getMESSAGE_ID())
                .build();

        OneTimeWorkRequest simpleRequest = new OneTimeWorkRequest.Builder(SendSticker.class)
                .setInputData(data)
                .setConstraints(constraints)
                .setBackoffCriteria(BackoffPolicy.LINEAR,
                        WorkRequest.MIN_BACKOFF_MILLIS,
                        TimeUnit.MILLISECONDS)
                .build();

        WorkManager.getInstance().beginUniqueWork("" + esaphStickerChatObject.getMESSAGE_ID(),
                ExistingWorkPolicy.KEEP,
                simpleRequest).enqueue();
    }


    public void sendMessageToServer(final EsaphAndroidSmileyChatObject esaphAndroidSmileyChatObject)
    {
        Constraints constraints = new Constraints.Builder()
                .setRequiresCharging(false)
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        Data data = new Data.Builder()
                .putLong(SendEmojie.EXTRA_MSG_ID, esaphAndroidSmileyChatObject.getMESSAGE_ID())
                .build();

        OneTimeWorkRequest simpleRequest = new OneTimeWorkRequest.Builder(SendEmojie.class)
                .setInputData(data)
                .setConstraints(constraints)
                .setBackoffCriteria(BackoffPolicy.LINEAR,
                        WorkRequest.MIN_BACKOFF_MILLIS,
                        TimeUnit.MILLISECONDS)
                .build();

        WorkManager.getInstance().beginUniqueWork(""+esaphAndroidSmileyChatObject.getMESSAGE_ID(),
                ExistingWorkPolicy.KEEP,
                simpleRequest).enqueue();
    }



    private void transmittePostSeenInChat(List<PostSeenUntransmitted> listData)
    {
        for(int counter = 0; counter < listData.size(); counter++)
        {
            try
            {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("PLSC", "PLSPU");
                jsonObject.put("USRN", SpotLightLoginSessionHandler.getLoggedUID());
                jsonObject.put("SID", SpotLightLoginSessionHandler.getSpotLightSessionId());
                jsonObject.put("FUSRN", listData.get(counter).getConversationMessage().getABS_ID());
                jsonObject.put("PPID", listData.get(counter).getConversationMessage().getMESSAGE_ID_SERVER());

                SocketResources resources = new SocketResources();
                SSLSocket socket = EsaphSSLSocket.getSSLInstance(context, resources.getServerAddress(), resources.getServerPortPServer());
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
                writer.println(jsonObject.toString());
                writer.flush();

                String result = reader.readLine();
                if(result.equals("1"))
                {
                    SQLChats sqlChats = new SQLChats(context);
                    sqlChats.removePostThatWasSeen(listData.get(counter));
                    sqlChats.close();
                }
            }
            catch (Exception ec)
            {
                Log.i(getClass().getName(), "transmittePostSeenInChat() failed: " + ec);
            }
        }
    }

    private static final String actionGroupChat_GROUP_MESSAGE_SENT = "esaph.action.groupchat.MSGSENT";
    private static final String extraGroupChat_MsgHash = "esaph.extra.groupchat.MSGHASH";
    private static final String extraGroupChat_MSG_HASH_OLD = "esaph.extra.groupchat.MSG_HASH_OLD";
    private static final String ACTION_UPDATE_MOMENT = "esaph.livetemp.services.s.Messaging.UPDATE.MOMENT";
    private static final String ID_MOMENT_MIID = "esaph.livetemp.services.s.Messaging.FCM_MOMENT_MIID";

    private void sentBroadCastGroupTextMessageSent(long newHash, GroupConversationMessage groupConversationMessage)
    {
        Intent intent = new Intent();
        intent.setAction(SynchDataWithServerUndeliveredMessages.actionGroupChat_GROUP_MESSAGE_SENT);
        intent.putExtra(SynchDataWithServerUndeliveredMessages.extraGroupChat_MsgHash, newHash);
        intent.putExtra(SynchDataWithServerUndeliveredMessages.extraGroupChat_MSG_HASH_OLD, groupConversationMessage.getMessageHash());

        Intent intentChatList = new Intent();
        intentChatList.setAction(SynchDataWithServerUndeliveredMessages.ACTION_UPDATE_MOMENT);
        intentChatList.putExtra(SynchDataWithServerUndeliveredMessages.ID_MOMENT_MIID, groupConversationMessage.getGIID());

        context.sendBroadcast(intent);
        context.sendBroadcast(intentChatList);
    }

}
