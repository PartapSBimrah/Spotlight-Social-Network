/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.services.SpotLightMessageConnection;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import androidx.core.app.NotificationCompat;
import android.util.Log;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import javax.net.ssl.SSLSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;
import esaph.spotlight.EsaphSSLSocket;
import esaph.spotlight.R;
import esaph.spotlight.SocketResources;
import esaph.spotlight.einstellungen.CLPreferences;
import esaph.spotlight.navigation.spotlight.Chats.ListeChat.ChatPartner;
import esaph.spotlight.navigation.spotlight.Chats.Messages.ChatInfoStateMessage;
import esaph.spotlight.navigation.spotlight.Chats.Messages.ConversationMessage;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.ChatObjects.AudioMessage;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.ChatObjects.ChatTextMessage;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.ChatObjects.EsaphAndroidSmileyChatObject;
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.ChatObjects.EsaphStickerChatObject;
import esaph.spotlight.services.SpotLightMessageConnection.Workers.SpotLightLoginSessionHandler;
import esaph.spotlight.services.SpotLightMessageConnection.Workers.SendAudioMessageToServer;
import esaph.spotlight.services.SpotLightMessageConnection.Workers.SendEmojie;
import esaph.spotlight.services.SpotLightMessageConnection.Workers.SendTextualMessageToServer;
import esaph.spotlight.services.SpotLightMessageConnection.Workers.SendSticker;
import esaph.spotlight.services.NotificationAndMessageHandling.GlobalNotificationDisplayer;
import esaph.spotlight.services.NotificationAndMessageHandling.MessageHandler;
import static androidx.core.app.NotificationCompat.PRIORITY_MIN;

public class MsgServiceConnection extends Service
{
    private static ThreadPoolExecutor threadPoolExecutorSentMessage = new ThreadPoolExecutor(2,
            2,
            15,
            TimeUnit.SECONDS,
            new LinkedBlockingDeque<Runnable>(20),
            new ThreadPoolExecutor.CallerRunsPolicy());

    private static final String wakeLockName = "esaph.spotlight:MSCW";
    private static AtomicBoolean connectionAlive = new AtomicBoolean(false);
    private static AtomicBoolean currentWaiting = new AtomicBoolean(false);
    private static int reconCount = 0;

    public MsgServiceConnection()
    {
    }

    private boolean isOnline(Context context)
    {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(cm != null)
        {
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            return (netInfo != null && netInfo.isConnected());
        }
        return false;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        startServiceOreoCondition();
    }

    private void startServiceOreoCondition()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            String CHANNEL_ID = "MsgServiceConnectionChannelID";
            String CHANNEL_NAME = "Spotlight Message Service";

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    CHANNEL_NAME, NotificationManager.IMPORTANCE_NONE);
            NotificationManager notificationManager = ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE));
            if(notificationManager != null)
            {
                notificationManager.createNotificationChannel(channel);
            }

            Random random = new Random();
            int mr = random.nextInt(9999 - 1000) + 1000;


            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setCategory(Notification.CATEGORY_SERVICE).setSmallIcon(R.drawable.ic_app_logo_notification_icon).setPriority(PRIORITY_MIN).build();

            startForeground(mr, notification);
        }
    }

    @Override
    public void onDestroy()
    {
        MsgServiceConnection.connectionAlive.set(false);
        MsgServiceConnection.currentWaiting.set(false);
        if(MsgServiceConnection.messageConnectionWaiter != null)
        {
            MsgServiceConnection.messageConnectionWaiter.interrupt();
            MsgServiceConnection.messageConnectionWaiter = null;
        }

        if(threadPoolExecutorSentMessageToServer != null)
        {
            threadPoolExecutorSentMessageToServer.shutdownNow();
        }

        if(threadPoolExecutorSentMessage != null)
        {
            threadPoolExecutorSentMessage.shutdownNow();
        }

        threadPoolExecutorSentMessageToServer = null;
        threadPoolExecutorSentMessage = null;
        super.onDestroy();
    }

    private final IBinder binder = new MyLocalBinder();
    private static List<MessageServiceCallBacks> messageServiceCallbacks = new ArrayList<>();
    public class MyLocalBinder extends Binder
    {
        public MsgServiceConnection getService(MessageServiceCallBacks messageServiceCallBacks)
        {
            if(messageServiceCallBacks != null) //Worker register a connection without callbacks.
            {
                MsgServiceConnection.messageServiceCallbacks.add(messageServiceCallBacks);
            }
            return MsgServiceConnection.this;
        }
    }


    public void removeMsgServiceCallback(MessageServiceCallBacks messageServiceCallBacks)
    {
        MsgServiceConnection.messageServiceCallbacks.remove(messageServiceCallBacks);
    }


    @Override
    public IBinder onBind(Intent intent)
    {
        return binder;
    }


    private ThreadPoolExecutor threadPoolExecutorSentMessageToServer = new ThreadPoolExecutor(1,
            1,
            5,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>(5),
            new ThreadPoolExecutor.CallerRunsPolicy());

    public void sendMessageToServer(ConversationMessage conversationMessage)
    {
        if(conversationMessage instanceof ChatTextMessage)
        {
            sendMessageToServer((ChatTextMessage) conversationMessage);
        }
        else if(conversationMessage instanceof AudioMessage)
        {
            sendMessageToServer((AudioMessage) conversationMessage);
        }
        else if(conversationMessage instanceof EsaphAndroidSmileyChatObject)
        {
            sendMessageToServer((EsaphAndroidSmileyChatObject) conversationMessage);
        }
        else if(conversationMessage instanceof EsaphStickerChatObject)
        {
            sendMessageToServer((EsaphStickerChatObject) conversationMessage);
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
                .putString(SendSticker.EXTRA_STID, esaphStickerChatObject.getEsaphSpotLightSticker().getIMAGE_ID())
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


    public void broadCastToAllCallbacksConversationMessageUpdate(final ChatTextMessage chatTextMessage)
    {
        for(int counter = 0; counter < messageServiceCallbacks.size(); counter++)
        {
            final MessageServiceCallBacks myCallback = messageServiceCallbacks.get(counter);
            if(myCallback != null)
            {
                handlerMainThread.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        myCallback.onMessageUpdate(chatTextMessage);
                    }
                });
            }
        }
    }

    public void broadCastToAllCallbacksConversationMessageUpdate(final ConversationMessage conversationMessage,
                                                                 final ChatInfoStateMessage chatInfoStateMessage)
    {
        for(int counter = 0; counter < messageServiceCallbacks.size(); counter++)
        {
            final MessageServiceCallBacks myCallback = messageServiceCallbacks.get(counter);
            if(myCallback != null)
            {
                handlerMainThread.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        myCallback.onMessageUpdate(conversationMessage,
                                chatInfoStateMessage);
                    }
                });
            }
        }
    }

    public void broadCastToAllCallbacksConversationMessageUpdate(final EsaphAndroidSmileyChatObject esaphAndroidSmileyChatObject)
    {
        for(int counter = 0; counter < messageServiceCallbacks.size(); counter++)
        {
            final MessageServiceCallBacks myCallback = messageServiceCallbacks.get(counter);
            if(myCallback != null)
            {
                handlerMainThread.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        myCallback.onMessageUpdate(esaphAndroidSmileyChatObject);
                    }
                });
            }
        }
    }

    public void broadCastToAllCallbacksConversationMessageUpdate(final AudioMessage audioMessage)
    {
        for(int counter = 0; counter < messageServiceCallbacks.size(); counter++)
        {
            final MessageServiceCallBacks myCallback = messageServiceCallbacks.get(counter);
            if(myCallback != null)
            {
                handlerMainThread.post(new Runnable() {
                    @Override
                    public void run()
                    {
                        myCallback.onMessageUpdate(audioMessage);
                    }
                });
            }
        }
    }

    public void broadCastToAllCallbacksConversationMessageUpdate(final EsaphStickerChatObject esaphStickerChatObject)
    {
        for(int counter = 0; counter < messageServiceCallbacks.size(); counter++)
        {
            final MessageServiceCallBacks myCallback = messageServiceCallbacks.get(counter);
            if(myCallback != null)
            {
                handlerMainThread.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        myCallback.onMessageUpdate(esaphStickerChatObject);
                    }
                });
            }
        }
    }

    public void broadCastToAllCallbacksNewContentReceived(final ChatPartner chatPartner)
    {
        for(int counter = 0; counter < messageServiceCallbacks.size(); counter++)
        {
            final MessageServiceCallBacks myCallback = messageServiceCallbacks.get(counter);
            if(myCallback != null)
            {
                handlerMainThread.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        myCallback.onUserUpdateInsertNewContent(chatPartner, chatPartner.getLastConversationMessage());
                    }
                });
            }
        }
    }

    public void broadCastToAllCallbacksOnUserAllowToSeePost(final ChatPartner chatPartner,
                                                            final ChatInfoStateMessage chatInfoStateMessage,
                                                            final long MESSAGE_ID)
    {
        for(int counter = 0; counter < messageServiceCallbacks.size(); counter++)
        {
            final MessageServiceCallBacks myCallback = messageServiceCallbacks.get(counter);
            if(myCallback != null)
            {
                handlerMainThread.post(new Runnable() {
                    @Override
                    public void run() {
                        myCallback.onUserAllowedToSeePostAgain(chatPartner, chatInfoStateMessage, MESSAGE_ID);
                    }
                });
            }
        }
    }

    public void broadCastToAllCallbacksOnUserDisallowToSeePost(final ChatPartner chatPartner,
                                                               final ChatInfoStateMessage chatInfoStateMessage,
                                                               final long MESSAGE_ID)
    {
        for(int counter = 0; counter < messageServiceCallbacks.size(); counter++)
        {
            final MessageServiceCallBacks myCallback = messageServiceCallbacks.get(counter);
            if(myCallback != null)
            {
                handlerMainThread.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        myCallback.onUserDisallowedToSeePost(chatPartner, chatInfoStateMessage, MESSAGE_ID);
                    }
                });
            }
        }
    }

    public void broadCastToAllCallbacksOnUserRemovedPost(final ChatPartner chatPartner,
                                                               final long MESSAGE_ID)
    {
        for(int counter = 0; counter < messageServiceCallbacks.size(); counter++)
        {
            final MessageServiceCallBacks myCallback = messageServiceCallbacks.get(counter);
            if(myCallback != null)
            {
                handlerMainThread.post(new Runnable() {
                    @Override
                    public void run() {
                        myCallback.onUserRemovedPost(chatPartner, MESSAGE_ID);
                    }
                });
            }
        }
    }

    public void broadCastToAllCallbacksFriendStatusUpdate(final short FRIEND_STATUS, final ChatPartner chatPartner)
    {
        for(int counter = 0; counter < messageServiceCallbacks.size(); counter++)
        {
            final MessageServiceCallBacks myCallback = messageServiceCallbacks.get(counter);
            if(myCallback != null)
            {
                handlerMainThread.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        myCallback.onFriendUpdate(FRIEND_STATUS, chatPartner);
                    }
                });
            }
        }
    }

    private Handler handlerMainThread = new Handler(Looper.myLooper());

    private static List<UserTypingWithTimer> userTypingWithTimerList = new ArrayList<>(); //Contains users that were typing.

    public class UserTypingWithTimer
    {
        private long USER_ID;
        private CountDownTimer timerTask;
        private boolean isValid = true;

        public UserTypingWithTimer(long USER_ID)
        {
            this.USER_ID = USER_ID;
            timerTask = new CountDownTimer(50*1000, 1000)
            {
                @Override
                public void onTick(long millisUntilFinished)
                {
                    if(!isValid)
                    {
                        cancel();
                    }
                    System.out.println("WRITING STATE: " + millisUntilFinished + "     " + this);
                }

                @Override
                public void onFinish()
                {
                    System.out.println("WRITING STATE: Finished");

                    if(!isValid)
                    {
                        cancel();
                        return;
                    }

                    handlerMainThread.post(new Runnable() {
                        @Override
                        public void run() {
                            broadCastToAllCallbacks_UserTypingState(UserTypingWithTimer.this.USER_ID, false);
                            userTypingWithTimerList.remove(UserTypingWithTimer.this);
                        }
                    });
                }
            }.start();
        }

        public long getUSER_ID() {
            return USER_ID;
        }

        public void cancelTimer()
        {
            this.isValid = false;
        }
    }


    public boolean isUserTyping(long USER_ID)
    {
        for(UserTypingWithTimer userTypingWithTimer : userTypingWithTimerList)
        {
            if(userTypingWithTimer.getUSER_ID() == USER_ID)
            {
                return true;
            }
        }

        return false;
    }


    public void broadCastToAllCallbacks_UserTypingState(final long USER_ID, final boolean typing)
    {
        for(UserTypingWithTimer userTypingWithTimer : userTypingWithTimerList) //Every update need to remove running timertasks!!
        {
            System.out.println("WRITING STATE: " + userTypingWithTimer.getUSER_ID() + " == " + USER_ID);
            if(userTypingWithTimer.getUSER_ID() == USER_ID)
            {
                System.out.println("WRITING STATE: CANCELING");
                userTypingWithTimer.cancelTimer();
            }
        }

        if(typing)
        {
            handlerMainThread.post(new Runnable()
            {
                @Override
                public void run()
                {
                    userTypingWithTimerList.add(new UserTypingWithTimer(USER_ID));
                }
            });
        }

        for(int counter = 0; counter < messageServiceCallbacks.size(); counter++)
        {
            final MessageServiceCallBacks myCallback = messageServiceCallbacks.get(counter);
            if(myCallback != null)
            {
                handlerMainThread.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        myCallback.onUpdateUserTyping(USER_ID, typing);
                    }
                });
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        try
        {
            CLPreferences preferences = new CLPreferences(getApplicationContext());
            if(!SpotLightLoginSessionHandler.getSpotLightSessionId().equals("") && !preferences.getUsername().equals(""))
            {
                applyNewConnection();
            }
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "service msg exception, kann passieren bei app neuinstallation wenn der preference nicht gesetzt wurde: " + ec);
        }

        return START_STICKY;
    }


    private void applyNewConnection()
    {
        if(MsgServiceConnection.messageConnectionWaiter != null)
        {
            try
            {
                MsgServiceConnection.messageConnectionWaiter.interrupt();
            }
            catch (Exception ec)
            {
                Log.i(getClass().getName(), "applyNewConnection() failed: " + ec);
            }
        }

        MsgServiceConnection.messageConnectionWaiter = new MessageConnectionWaiter();
        new Thread(MsgServiceConnection.messageConnectionWaiter).start();
    }

    private class ResetConnectionHelper implements Runnable
    {
        public ResetConnectionHelper()
        {
        }

        @Override
        public void run()
        {
            if(!currentWaiting.compareAndSet(false, true)) return;

            GlobalNotificationDisplayer.createLog(getApplicationContext(), "Resetting connection: " + reconCount, System.currentTimeMillis(), true);
            currentWaiting.set(true);
            try
            {
                if(isOnline(getApplicationContext()) && isAppRunning(getApplicationContext(), getPackageName())) //Prevent background, energy consumptions.
                {
                    Log.i(getClass().getName(), "Message Connection is waiting...");

                    if(reconCount >= 10)
                    {
                        reconCount = 0;
                        stopSelf();
                        return;
                    }
                    reconCount++;
                    Thread.sleep(10000);
                    if(!connectionAlive.get()) //KANN SEIN DAS DIE VERBINDUNG IN DER ZEITSPANNE WIEDER AUFGEBAUT WURDE. MUSS ÜBERPRÜFT WERDEN.
                    {
                        Log.i(getClass().getName(), "Message Connection waited: Dead");
                        applyNewConnection();
                    }
                    else
                    {
                        Log.i(getClass().getName(), "Message Connection waited: Alive");
                    }
                }
            }
            catch (Exception ec)
            {
                Log.e(getClass().getName(), "Reset Connection helper failed: " + ec);
            }
            finally
            {
                currentWaiting.set(false);
            }
        }
    }

    private static MessageConnectionWaiter messageConnectionWaiter = null;
    private class MessageConnectionWaiter implements Runnable
    {
        private SSLSocket socketMsgServer;
        private PrintWriter writer;
        private BufferedReader reader;

        private MessageConnectionWaiter()
        {
        }

        private List<JSONObject> getUnreceivedMessages() throws Exception
        {
            try
            {
                List<JSONObject> chatMessages = new ArrayList<>();

                while(true)
                {
                    String result = reader.readLine();
                    System.out.println("MESSAGE READED: " + result);
                    if(result.equals("EOF")) //Ist schon in ordnung, diese daten kann ein nutzer niemals manipulieren. Die werden vom server verschickt.
                    {
                        break;
                    }
                    JSONObject jsonObject = new JSONObject(result);
                    chatMessages.add(jsonObject);
                    writer.println("1");
                    writer.flush();
                }
                Log.i(getClass().getName(), "Unreceived messages count: " + chatMessages.size());
                return chatMessages;
            }
            catch (Exception ec)
            {
                Log.i(getClass().getName(), "getUnreceivedMessages(): " + ec);
                throw ec;
            }
        }

        public void interrupt()
        {
            try
            {
                Thread.currentThread().interrupt();
            }
            catch (Exception ec)
            {
                Log.i(getClass().getName(), "interrupt() msg connection failed: " + ec);
            }
        }

        @Override
        public void run()
        {
            try
            {
                GlobalNotificationDisplayer.createLog(getApplicationContext(), "Connecting", System.currentTimeMillis(), true);
                PowerManager powerManagerPrepare = (PowerManager) getSystemService(POWER_SERVICE);
                PowerManager.WakeLock wakeLockPrepare = null;

                if(powerManagerPrepare != null)
                {
                    wakeLockPrepare = powerManagerPrepare.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, wakeLockName);
                    if(!wakeLockPrepare.isHeld())
                    {
                        wakeLockPrepare.acquire(5*60*1000L /*10 minutes*/);
                    }
                }

                try
                {
                    try
                    {
                        if(socketMsgServer != null)
                        {
                            socketMsgServer.close();
                        }
                    }
                    catch (Exception ec)
                    {
                        Log.i(getClass().getName(), "Normal closing connection error: " + ec);
                    }

                    JSONObject jsonObject = new JSONObject();
                    LOGGED_UID = SpotLightLoginSessionHandler.getLoggedUID();
                    LOGGED_USERNAME = SpotLightLoginSessionHandler.getLoggedUsername();
                    jsonObject.put("USRN", LOGGED_UID);
                    jsonObject.put("SID", SpotLightLoginSessionHandler.getSpotLightSessionId());

                    SocketResources resources = new SocketResources();
                    socketMsgServer = EsaphSSLSocket.getSSLInstance(getApplicationContext(), resources.getServerAddress(), resources.getServerPortMsgServer());
                    writer = new PrintWriter(socketMsgServer.getOutputStream());
                    reader = new BufferedReader(new InputStreamReader(socketMsgServer.getInputStream()));
                    writer.println(jsonObject.toString());
                    writer.flush();

                    if(reader.readLine().equals("1")) //Session ok
                    {
                        List<JSONObject> listUnhandler = this.getUnreceivedMessages();
                        if(listUnhandler != null)
                        {
                            if(!listUnhandler.isEmpty())
                            {
                                MsgServiceConnection.threadPoolExecutorSentMessage.submit(new MessageHandler(listUnhandler, getBaseContext(), MsgServiceConnection.this));
                            }
                        }

                        if(reader.readLine().equals("COK"))
                        {
                            Log.i(getClass().getName(), "CONNECTED TO MSG SERVER SUCCESFULLY");
                            GlobalNotificationDisplayer.createLog(getApplicationContext(), "Connected.", System.currentTimeMillis(), true);
                            socketMsgServer.setKeepAlive(true);
                            socketMsgServer.setTcpNoDelay(true);
                            connectionAlive.set(true);
                        }
                        else
                        {
                            Log.i(getClass().getName(), "FAILED TO CONNECTED MSG SERVER");
                            GlobalNotificationDisplayer.createLog(getApplicationContext(), "Connected, but dropped.", System.currentTimeMillis(), true);
                            socketMsgServer.close();
                            writer.close();
                            reader.close();
                            socketMsgServer = null;
                            connectionAlive.set(false);
                        }
                    }
                    else
                    {
                        Log.i(getClass().getName(), "FAILED TO CONNECTED MSG SERVER");
                        socketMsgServer.close();
                        writer.close();
                        reader.close();
                        socketMsgServer = null;
                        connectionAlive.set(false);
                    }
                }
                catch (Exception ec)
                {
                    Log.i(getClass().getName(), "Cant setup connection to msg server: " + ec);
                    connectionAlive.set(false);
                }

                if(wakeLockPrepare != null && wakeLockPrepare.isHeld())
                {
                    wakeLockPrepare.release();
                }

                while(connectionAlive.get()) //SOLANGE VERBINDUNG ALIVE IST
                {
                    if(Thread.interrupted())
                    {
                        throw new InterruptedException();
                    }
                    socketMsgServer.setTcpNoDelay(true);
                    socketMsgServer.setKeepAlive(true);
                    socketMsgServer.setSoTimeout(310000);
                    Log.i(getClass().getName(), "Waiting for data");
                    GlobalNotificationDisplayer.createLog(getApplicationContext(), "Waiting for data", System.currentTimeMillis(), true);

                    String command = reader.readLine();

                    if(command != null && command.equals("H"))
                    {
                        //Heartbeat
                        Log.i(getClass().getName(), "HEARTBEAT FROM SERVER");
                        writer.println("H");
                        writer.flush();
                        if(!isAppRunning(getApplicationContext(), getPackageName()))
                        {
                            socketMsgServer.close();
                            writer.close();
                            reader.close();
                            stopForeground(true);
                            stopSelf();
                        }
                    }
                    else
                    {
                        JSONObject jsonObject = new JSONObject(command);
                        List<JSONObject> jsonObjectsList = new ArrayList<>();
                        jsonObjectsList.add(jsonObject);

                        Log.i(getClass().getName(), "NEW MESSAGE");
                        if(MsgServiceConnection.threadPoolExecutorSentMessage != null
                                && !MsgServiceConnection.threadPoolExecutorSentMessage.isShutdown())
                        {
                            MsgServiceConnection.threadPoolExecutorSentMessage.submit(new MessageHandler(jsonObjectsList, getBaseContext(), MsgServiceConnection.this));
                            writer.println("1");
                            writer.flush();
                            Log.i(getClass().getName(), "BESTÄTIGUNG VERSCHICKT");
                        }
                    }
                }
            }
            catch (Exception ec)
            {
                Log.i(getClass().getName(), "Error MsgServiceConnection: " + ec);
                GlobalNotificationDisplayer.createLog(getApplicationContext(), "Error MsgServiceConnection: " + ec, System.currentTimeMillis(), false);
            }
            finally
            {
                try
                {
                    if(this.socketMsgServer != null)
                    {
                        this.socketMsgServer.close();
                    }

                    if(this.writer != null)
                    {
                        this.writer.close();
                    }

                    if(this.reader != null)
                    {
                        this.reader.close();
                    }
                }
                catch (Exception ec)
                {

                }

                GlobalNotificationDisplayer.createLog(getApplicationContext(), "Connection died.", System.currentTimeMillis(), false);
                connectionAlive.set(false);
                try
                {
                    new Thread(new ResetConnectionHelper()).start();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    new Thread(new ResetConnectionHelper()).start();
                }
            }
        }
    }


    private boolean isAppRunning(final Context context, final String packageName)
    {
        // TODO: 05.05.2019 warning i deativated this, so test if the service willl run better.
        return true;


        /*
        final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if(activityManager != null)
        {
            final List<ActivityManager.RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
            if (procInfos != null)
            {
                for (final ActivityManager.RunningAppProcessInfo processInfo : procInfos)
                {
                    if (processInfo.processName.equals(packageName))
                    {
                        return true;
                    }
                }
            }
        }
        return false;
        */
    }


    private long LOGGED_UID;
    public long getBoundServiceUID()
    {
        return LOGGED_UID;
    }

    private String LOGGED_USERNAME;
    public String getBoundServiceUsername()
    {
        return LOGGED_USERNAME;
    }

}
