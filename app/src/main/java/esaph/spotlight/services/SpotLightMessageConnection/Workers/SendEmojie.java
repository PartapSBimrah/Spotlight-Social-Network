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
import esaph.spotlight.navigation.spotlight.Chats.PrivateChat.ChatObjects.EsaphAndroidSmileyChatObject;
import esaph.spotlight.services.SpotLightMessageConnection.MsgServiceConnection;

import static android.content.Context.BIND_AUTO_CREATE;

public class SendEmojie extends Worker
{
    public static final String EXTRA_MSG_ID = "esaph.worker.sendmessagetoserver.MSGID";
    private Context context;

    public SendEmojie(@NonNull Context context, @NonNull WorkerParameters workerParams)
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

            long POST_ID = getInputData().getLong(SendEmojie.EXTRA_MSG_ID, -1);

            SQLChats sqlChats = new SQLChats(context);
            EsaphAndroidSmileyChatObject esaphAndroidSmileyChatObject = (EsaphAndroidSmileyChatObject) sqlChats.getPostByInternId(POST_ID);

            if(esaphAndroidSmileyChatObject == null)
                return Result.failure();

            try
            {
                SocketResources resources = new SocketResources();
                CLPreferences preferences = new CLPreferences(context);

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("USRN", preferences.getUID());
                jsonObject.put("SID", SpotLightLoginSessionHandler.getSpotLightSessionId());
                jsonObject.put("PLSC",  "PLSEM");
                jsonObject.put("EMJ",  esaphAndroidSmileyChatObject.getEsaphEmojie().getEMOJIE());
                jsonObject.put("EMPF",  esaphAndroidSmileyChatObject.getID_CHAT());
                jsonObject.put("PLINF", esaphAndroidSmileyChatObject.getEsaphPloppInformationsJSON());

                SSLSocket sslSocketSendMessage = EsaphSSLSocket.getSSLInstance(context, resources.getServerAddress(), resources.getServerPortPServer());
                BufferedReader reader = new BufferedReader(new InputStreamReader(sslSocketSendMessage.getInputStream()));
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(sslSocketSendMessage.getOutputStream()));
                writer.println(jsonObject.toString());
                writer.flush();

                long newHash = Long.parseLong(reader.readLine());

                esaphAndroidSmileyChatObject.setMessageStatus(ConversationStatusHelper.STATUS_SENT);
                sqlChats.updateStatusByID(esaphAndroidSmileyChatObject);

                if(isBoundSendingConnection && msgServiceConnection != null)
                {
                    msgServiceConnection.broadCastToAllCallbacksConversationMessageUpdate(esaphAndroidSmileyChatObject);
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
                    esaphAndroidSmileyChatObject.setMessageStatus(ConversationStatusHelper.STATUS_FAILED_TO_SEND_OR_RECEIVE);
                    sqlChats.updateStatusByID(esaphAndroidSmileyChatObject);
                }
                catch (Exception e)
                {
                }
                Log.i(getClass().getName(), "SendEmojie() failed: " + ec);
            }
            finally
            {
                sqlChats.close();
            }
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "SendEmojie() failed sleeping clausel: " + ec);
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
