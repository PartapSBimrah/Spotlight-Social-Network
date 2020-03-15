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

import esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EsaphFaceDectectorCropper.AddStickerToPackManaging.DialogAddStickerToPack;
import esaph.spotlight.EsaphSSLSocket;
import esaph.spotlight.SocketResources;
import esaph.spotlight.databases.SQLChats;
import esaph.spotlight.services.SpotLightMessageConnection.Workers.SpotLightLoginSessionHandler;
import esaph.spotlight.services.UndeliveredMessageHandling.UndeliveredChatSeenMessage;

public class RunnableChatOpenened implements Runnable
{
    private WeakReference<Context> contextWeakReference;
    private UndeliveredChatSeenMessage undeliveredChatSeenMessage;

    public RunnableChatOpenened(Context context, UndeliveredChatSeenMessage undeliveredChatSeenMessage)
    {
        this.contextWeakReference = new WeakReference<>(context);
        this.undeliveredChatSeenMessage = undeliveredChatSeenMessage;
    }

    @Override
    public void run()
    {
        SQLChats sqlChats = null;
        try
        {
            sqlChats = new SQLChats(contextWeakReference.get());
            SocketResources resources = new SocketResources();
            SSLSocket sslSocket = EsaphSSLSocket.getSSLInstance(this.contextWeakReference.get(),
                    resources.getServerAddress(),
                    resources.getServerPortPServer());

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("PLSC", "PLUOC");
            jsonObject.put("USRN", SpotLightLoginSessionHandler.getLoggedUID());
            jsonObject.put("SID", SpotLightLoginSessionHandler.getSpotLightSessionId());
            jsonObject.put("FUSRN", this.undeliveredChatSeenMessage.getUID_CHAT_PARTNER());

            BufferedReader reader = new BufferedReader(new InputStreamReader(sslSocket.getInputStream()));
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(sslSocket.getOutputStream()));
            writer.println(jsonObject.toString());
            writer.flush();

            if(reader.readLine().equals("1"))
            {
                sqlChats.removeChatWasSeen(undeliveredChatSeenMessage.get_ID());
            }
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "RunnableChatOpened failed: " + ec);
        }
        finally
        {
            if(sqlChats != null)
            {
                sqlChats.close();
            }
        }
    }
}
