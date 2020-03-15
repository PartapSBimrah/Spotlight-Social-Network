/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.navigation.globalActions;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.sql.SQLWarning;

import javax.net.ssl.SSLSocket;
import esaph.spotlight.EsaphSSLSocket;
import esaph.spotlight.SocketResources;
import esaph.spotlight.databases.SQLFriends;
import esaph.spotlight.services.SpotLightMessageConnection.Workers.SpotLightLoginSessionHandler;

public class AsyncDeclineFriendAnfrage extends AsyncTask<Void, Void, Short>
{
    private WeakReference<FriendStatusListener> friendStatusListenerWeakReference;
    private WeakReference<Context> context;
    private long UID;

    public AsyncDeclineFriendAnfrage(Context context, long UID, FriendStatusListener friendStatusListener)
    {
        this.context = new WeakReference<Context>(context);
        this.UID = UID;
        this.friendStatusListenerWeakReference = new WeakReference<FriendStatusListener>(friendStatusListener);
    }

    @Override
    protected Short doInBackground(Void... params)
    {
        try
        {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("PLSC", "PLDFA");
            jsonObject.put("USRN", SpotLightLoginSessionHandler.getLoggedUID());
            jsonObject.put("SID", SpotLightLoginSessionHandler.getSpotLightSessionId());
            jsonObject.put("FUSRN", this.UID);

            SocketResources resources = new SocketResources();
            SSLSocket socket = EsaphSSLSocket.getSSLInstance(this.context.get(), resources.getServerAddress(), resources.getServerPortPServer());
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            writer.println(jsonObject.toString());
            writer.flush();

            short result = Short.parseShort(reader.readLine());
            if(result >= 0)
            {
                SQLFriends sqlFriends = new SQLFriends(context.get());
                sqlFriends.updateFollowNegotiation(UID, result);
                sqlFriends.close();
            }
            else
            {
                socket.close();
                writer.close();
                reader.close();
                return null;
            }

            socket.close();
            writer.close();
            reader.close();
            return result;
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "AsyncDeclineFriendAnfrage.background() failed: " + ec);
            return null;
        }
    }



    @Override
    protected void onPostExecute(Short aShort)
    {
        super.onPostExecute(aShort);
        FriendStatusListener friendStatusListener = friendStatusListenerWeakReference.get();
        if(friendStatusListener != null)
        {
            if(aShort != null)
            {
                friendStatusListener.onStatusReceived(this.UID, aShort);
            }
            else
            {
                friendStatusListener.onStatusFailed(this.UID);
            }
        }
    }

}
