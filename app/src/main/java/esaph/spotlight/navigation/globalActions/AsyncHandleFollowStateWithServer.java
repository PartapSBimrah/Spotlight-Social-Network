package esaph.spotlight.navigation.globalActions;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import javax.net.ssl.SSLSocket;
import esaph.spotlight.EsaphSSLSocket;
import esaph.spotlight.SpotLightUser;
import esaph.spotlight.databases.SQLChats;
import esaph.spotlight.databases.SQLFriends;
import esaph.spotlight.SocketResources;
import esaph.spotlight.services.SpotLightMessageConnection.Workers.SpotLightLoginSessionHandler;

public class AsyncHandleFollowStateWithServer implements Runnable
{
    private WeakReference<FriendStatusListener> friendStatusListenerWeakReference;
    private Context context;
    private WeakReference<View> viewPressed;
    private SocialFriendNegotiation socialFriendNegotiation;

    public AsyncHandleFollowStateWithServer(Context context, SocialFriendNegotiation socialFriendNegotiation, FriendStatusListener friendStatusListener, View viewPressed)
    {
        this.context = context;
        this.socialFriendNegotiation = socialFriendNegotiation;
        this.viewPressed = new WeakReference<View>(viewPressed);
        this.friendStatusListenerWeakReference = new WeakReference<FriendStatusListener>(friendStatusListener);
    }

    private short STATUS_FROM_SERVER = -1;

    @Override
    public void run()
    {
        try
        {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("PLSC", "PLFS");
            jsonObject.put("USRN", SpotLightLoginSessionHandler.getLoggedUID());
            jsonObject.put("SID", SpotLightLoginSessionHandler.getSpotLightSessionId());
            jsonObject.put("FUSRN", this.socialFriendNegotiation.getUID());

            if(Thread.interrupted())
            {
                return;
            }

            SocketResources resources = new SocketResources();
            SSLSocket socket = EsaphSSLSocket.getSSLInstance(this.context, resources.getServerAddress(), resources.getServerPortPServer());
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            writer.println(jsonObject.toString());
            writer.flush();


            if(Thread.interrupted())
            {
                return;
            }
            final JSONObject jsonObjectResult = new JSONObject(reader.readLine());
            STATUS_FROM_SERVER = (short) jsonObjectResult.getInt("FRT");

            socialFriendNegotiation.setRegion(jsonObjectResult.optString("REG", "")); //Region is getting lost in some cases in application.

            socialFriendNegotiation.setAnfragenStatus(STATUS_FROM_SERVER);

            if(STATUS_FROM_SERVER == ServerPolicy.POLICY_DETAIL_CASE_FRIENDS)
            {
                JSONObject jsonObjectUser = jsonObjectResult.getJSONObject("USR");
                try
                {
                    SQLFriends sqlWatcher = new SQLFriends(context);
                    sqlWatcher.insertWatcher(new SpotLightUser(
                            jsonObjectUser.getLong("UID"),
                            jsonObjectUser.getString("Benutzername"),
                            jsonObjectUser.getString("Vorname"),
                            jsonObjectUser.getLong("Geburtstag"),
                            jsonObjectUser.getString("Region"),
                            sqlWatcher.isFriendshipDied(jsonObjectUser.getLong("UID")),
                            jsonObjectUser.getJSONObject("DESCPL").toString()));
                    sqlWatcher.close();
                }
                catch (Exception ec)
                {
                    Log.i(getClass().getName(), "AsyncHandleFollowStateWithServer() WSS: failed: " + ec);
                }
            }
            socket.close();
            writer.close();
            reader.close();

            if(this.viewPressed != null && STATUS_FROM_SERVER != -1)
            {
                handleStatusUpdateInDatabase();

                ((Activity)this.context).runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if(Thread.interrupted())
                        {
                            return;
                        }

                        viewPressed.get().setClickable(true);
                        viewPressed.get().setFocusable(true);

                        FriendStatusListener friendStatusListener = friendStatusListenerWeakReference.get();
                        if(friendStatusListener != null)
                        {
                            friendStatusListener.onStatusReceived(socialFriendNegotiation.getUID(), STATUS_FROM_SERVER);
                        }
                    }
                });
            }
            else
            {
                ((Activity)this.context).runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if(Thread.interrupted())
                        {
                            return;
                        }

                        FriendStatusListener friendStatusListener = friendStatusListenerWeakReference.get();
                        if(friendStatusListener != null)
                        {
                            friendStatusListener.onStatusFailed(socialFriendNegotiation.getUID());
                        }
                    }
                });
            }
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "AsyncHandleFollowStateWithServer.background() failed: " + ec);
            ((Activity)this.context).runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    if(Thread.interrupted())
                    {
                        return;
                    }

                    FriendStatusListener friendStatusListener = friendStatusListenerWeakReference.get();
                    if(friendStatusListener != null)
                    {
                        friendStatusListener.onStatusFailed(socialFriendNegotiation.getUID());
                    }
                }
            });
        }
    }

    private void handleStatusUpdateInDatabase()
    {
        short Status = socialFriendNegotiation.getAnfragenStatus();
        SQLFriends sqlFriends = new SQLFriends(context);

        if(Status == ServerPolicy.POLICY_DETAIL_CASE_I_SENT_ANFRAGE
        || Status == ServerPolicy.POLICY_DETAIL_CASE_I_WAS_ANGEFRAGT)
        {
            sqlFriends.insertNewFollowNegotiation(socialFriendNegotiation);
        }
        else
        {
            sqlFriends.updateFollowNegotiation(socialFriendNegotiation.getUID(), Status);
        }



        if(Status == ServerPolicy.POLICY_DETAIL_CASE_NOTHING) //KEINE VERBINDUNG.
        {
            SQLChats sqlChats = new SQLChats(context);
            sqlChats.removeAllUserData(socialFriendNegotiation.getUID());
            sqlChats.close();

            sqlFriends.removeAllUserData(socialFriendNegotiation.getUID());
        }
        else if(Status == ServerPolicy.POLICY_DETAIL_CASE_I_BLOCKED_SOMEONE) //ICH HAB JEMANDEN GEBLOCKT.
        {
            SQLChats sqlChats = new SQLChats(context);
            sqlChats.removeAllUserData(socialFriendNegotiation.getUID());
            sqlChats.close();
        }
        else if(Status == ServerPolicy.POLICY_DETAIL_CASE_I_WAS_BLOCKED) //ICH WURDE GEBLOCKT.
        {
            SQLChats sqlChats = new SQLChats(context);
            sqlChats.removeAllUserData(socialFriendNegotiation.getUID());
            sqlChats.close();

            sqlFriends.removeAllUserData(socialFriendNegotiation.getUID());
        }
    }
}
