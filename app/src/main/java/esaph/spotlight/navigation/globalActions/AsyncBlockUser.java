package esaph.spotlight.navigation.globalActions;

import android.app.ProgressDialog;
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
import esaph.spotlight.R;
import esaph.spotlight.databases.SQLChats;
import esaph.spotlight.databases.SQLFriends;
import esaph.spotlight.SocketResources;
import esaph.spotlight.navigation.spotlight.Chats.ListeChat.ChatPartner;
import esaph.spotlight.services.SpotLightMessageConnection.Workers.SpotLightLoginSessionHandler;

public class AsyncBlockUser extends AsyncTask<Void, Void, Boolean>
{
    private ProgressDialog progressDialog;
    private WeakReference<FriendStatusListener> friendStatusListenerWeakReference;
    private WeakReference<Context> context;
    private ChatPartner chatPartner;
    private int eraseAll; //1 == yes 0 == no

    public AsyncBlockUser(Context context, ChatPartner chatPartner, int eraseAll, FriendStatusListener friendStatusListener)
    {
        this.chatPartner = chatPartner;
        this.context = new WeakReference<Context>(context);
        this.eraseAll = eraseAll;
        this.friendStatusListenerWeakReference = new WeakReference<FriendStatusListener>(friendStatusListener);
    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();
        if(this.context.get() != null)
        {
            this.progressDialog = new ProgressDialog(context.get());
            this.progressDialog.setCanceledOnTouchOutside(false);
            this.progressDialog.setTitle(context.get().getString(R.string.txt_blockUser));
            this.progressDialog.setMessage(context.get().getString(R.string.txt__alertUnoMomento));
            this.progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            this.progressDialog.show();
        }
    }

    @Override
    protected Boolean doInBackground(Void... params)
    {
        SQLFriends sqlWatcher = null;
        try
        {
            sqlWatcher = new SQLFriends(context.get());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("PLSC", "PLBF");
            jsonObject.put("USRN", SpotLightLoginSessionHandler.getLoggedUID());
            jsonObject.put("SID", SpotLightLoginSessionHandler.getSpotLightSessionId());
            jsonObject.put("FUSRN", chatPartner.getUID_CHATPARTNER());
            jsonObject.put("ERA", eraseAll);

            SocketResources resources = new SocketResources();
            SSLSocket socket = EsaphSSLSocket.getSSLInstance(this.context.get(), resources.getServerAddress(), resources.getServerPortPServer());
            socket.setSoTimeout(10000);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            writer.println(jsonObject.toString());
            writer.flush();

            String result = reader.readLine();
            if(result.equals("1"))
            {
                socket.close();
                writer.close();
                reader.close();

                SQLChats sqlChats = new SQLChats(this.context.get());
                sqlChats.removeAllUserData(chatPartner.getUID_CHATPARTNER());
                sqlChats.close();

                sqlWatcher.removeAllUserData(chatPartner.getUID_CHATPARTNER());
                sqlWatcher.updateFollowNegotiation(
                        chatPartner.getUID_CHATPARTNER(),
                        ServerPolicy.POLICY_DETAIL_CASE_I_BLOCKED_SOMEONE);

                sqlWatcher.close();
                return Boolean.TRUE;
            }
            return Boolean.FALSE;
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "AsyncBlockUser.background() failed: " + ec);
            return null;
        }
        finally
        {
            if(sqlWatcher != null)
            {
                sqlWatcher.close();
            }
        }
    }

    @Override
    protected void onPostExecute(Boolean aBoolean)
    {
        super.onPostExecute(aBoolean);
        FriendStatusListener friendStatusListener = friendStatusListenerWeakReference.get();
        if(friendStatusListener != null)
        {
            this.progressDialog.cancel();
            if(aBoolean != null)
            {
                friendStatusListener.onStatusReceived(chatPartner.getUID_CHATPARTNER(), ServerPolicy.POLICY_DETAIL_CASE_I_BLOCKED_SOMEONE);
            }
            else
            {
                friendStatusListener.onStatusFailed(chatPartner.getUID_CHATPARTNER());
            }
        }
    }
}
