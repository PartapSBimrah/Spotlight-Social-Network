package esaph.spotlight.navigation.spotlight.Chats.PrivateChat.Background;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;

import javax.net.ssl.SSLSocket;

import esaph.spotlight.EsaphSSLSocket;
import esaph.spotlight.databases.SQLChats;
import esaph.spotlight.databases.SQLFriends;
import esaph.spotlight.SocketResources;
import esaph.spotlight.navigation.globalActions.PostSeenUntransmitted;
import esaph.spotlight.services.SpotLightMessageConnection.Workers.SpotLightLoginSessionHandler;
import esaph.spotlight.services.NotificationAndMessageHandling.GlobalNotificationDisplayer;

public class AsyncSetPostSeen extends AsyncTask<Void, Void, Boolean>
{
    private WeakReference<Context> context;
    private PostSeenUntransmitted postSeenUntransmitted;

    public AsyncSetPostSeen(Context context, PostSeenUntransmitted postSeenUntransmitted)
    {
        this.context = new WeakReference<Context>(context);
        this.postSeenUntransmitted = postSeenUntransmitted;
    }

    @Override
    protected Boolean doInBackground(Void... params)
    {
        try
        {
            SQLFriends sqlWatcher = new SQLFriends(context.get()); //Removing notification.
            int id = sqlWatcher.getPostNotifyId(postSeenUntransmitted.getConversationMessage());
            sqlWatcher.close();
            GlobalNotificationDisplayer.removeNotification(context.get(), id, postSeenUntransmitted.getConversationMessage());

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("PLSC", "PLSPU");
            jsonObject.put("USRN", SpotLightLoginSessionHandler.getLoggedUID());
            jsonObject.put("SID", SpotLightLoginSessionHandler.getSpotLightSessionId());
            jsonObject.put("FUSRN", this.postSeenUntransmitted.getConversationMessage().getABS_ID());
            jsonObject.put("PPID", this.postSeenUntransmitted.getConversationMessage().getMESSAGE_ID_SERVER());

            SocketResources resources = new SocketResources();
            SSLSocket socket = EsaphSSLSocket.getSSLInstance(this.context.get(), resources.getServerAddress(), resources.getServerPortPServer());
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            writer.println(jsonObject.toString());
            writer.flush();

            String result = reader.readLine();
            if(result.equals("1")) //Seen
            {
                SQLChats sqlChats = new SQLChats(this.context.get());
                sqlChats.removePostThatWasSeen(this.postSeenUntransmitted);
                sqlChats.close();
                return Boolean.TRUE;
            }

            return Boolean.FALSE;
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "AsyncSetPostSeen() failed: " + ec);
            return Boolean.FALSE;
        }
    }


    @Override
    protected void onPostExecute(Boolean aBoolean)
    {
        super.onPostExecute(aBoolean);
    }
}
