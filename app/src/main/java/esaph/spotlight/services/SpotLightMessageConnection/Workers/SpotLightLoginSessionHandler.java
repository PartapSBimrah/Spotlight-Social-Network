package esaph.spotlight.services.SpotLightMessageConnection.Workers;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.net.ssl.SSLSocket;

import esaph.spotlight.EsaphSSLSocket;
import esaph.spotlight.SocketResources;
import esaph.spotlight.einstellungen.CLPreferences;
import esaph.spotlight.services.SpotLightMessageConnection.MsgServiceConnection;
import esaph.spotlight.services.UndeliveredMessageHandling.SynchDataWithServerUndeliveredMessages;

public class SpotLightLoginSessionHandler extends Worker
{
    private Context context;
    protected static String SPOTLIGHT_SESSION = "";
    protected static String LOGGED_USERNAME = "";
    protected static long LOGGED_UID = -1;
    protected static boolean isDemoMode = false;
    protected final static Object oS = new Object();

    public static void setSpotLightUserLogged(String NEW_SID, String Username, long LOGGED_UID, boolean isDemoMode)
    {
        synchronized (SpotLightLoginSessionHandler.oS)
        {
            SpotLightLoginSessionHandler.SPOTLIGHT_SESSION = NEW_SID;
            SpotLightLoginSessionHandler.LOGGED_USERNAME = Username;
            SpotLightLoginSessionHandler.LOGGED_UID = LOGGED_UID;
            SpotLightLoginSessionHandler.isDemoMode = isDemoMode;
        }
    }

    public static void setSpotLightUser(String Username, long LOGGED_UID, boolean isDemoMode) //This must be set, before the user can use the app.
            //To prevent that uid, or the username is null.
    {
        synchronized (SpotLightLoginSessionHandler.oS)
        {
            SpotLightLoginSessionHandler.LOGGED_USERNAME = Username;
            SpotLightLoginSessionHandler.LOGGED_UID = LOGGED_UID;
            SpotLightLoginSessionHandler.isDemoMode = isDemoMode;
        }
    }

    public static void setIsDemoMode(boolean isDemoMode)
    {
        synchronized (SpotLightLoginSessionHandler.oS)
        {
            SpotLightLoginSessionHandler.isDemoMode = isDemoMode;
        }
    }

    public static synchronized String getSpotLightSessionId() throws UserNotLoggedException
    {
        synchronized (SpotLightLoginSessionHandler.oS)
        {
            if(SPOTLIGHT_SESSION == null || SPOTLIGHT_SESSION.isEmpty()) throw new SpotLightLoginSessionHandler.UserNotLoggedException();
            return SpotLightLoginSessionHandler.SPOTLIGHT_SESSION;
        }
    }

    public static boolean isDemoMode()
    {
        synchronized (SpotLightLoginSessionHandler.oS)
        {
            return SpotLightLoginSessionHandler.isDemoMode;
        }
    }

    public static synchronized String getLoggedUsername()
    {
        synchronized (SpotLightLoginSessionHandler.oS)
        {
            return SpotLightLoginSessionHandler.LOGGED_USERNAME;
        }
    }

    public static synchronized long getLoggedUID()
    {
        synchronized (SpotLightLoginSessionHandler.oS)
        {
            return SpotLightLoginSessionHandler.LOGGED_UID;
        }
    }

    public SpotLightLoginSessionHandler(@NonNull Context context, @NonNull WorkerParameters workerParams)
    {
        super(context, workerParams);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork()
    {
        CLPreferences preferences = new CLPreferences(context);
        try
        {
            SocketResources resources = new SocketResources();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("LRC", "LRL");
            jsonObject.put("USRN", preferences.getUsername());
            jsonObject.put("PW", preferences.getPasswordEncrypted());
            jsonObject.put("FCMT", "");

            SSLSocket loginSocket = EsaphSSLSocket.getSSLInstance(context, resources.getServerAddress(), resources.getServerPortLRServer());
            BufferedReader reader = new BufferedReader(new InputStreamReader(loginSocket.getInputStream()));
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(loginSocket.getOutputStream()));
            writer.println(jsonObject.toString());
            writer.flush();

            String reply = reader.readLine();
            if(reply.equals("LRLT"))
            {
                Log.i(getClass().getName(), "Login Succesfull.");

                JSONObject jsonObjectSessionPayload = new JSONObject(reader.readLine());

                SpotLightLoginSessionHandler.setSpotLightUserLogged(jsonObjectSessionPayload.getString("SID"),
                        preferences.getUsername(),
                        jsonObjectSessionPayload.getLong("UID"),
                        preferences.isDemoMode());
            }

            reader.close();
            writer.close();
            loginSocket.close();
            new Thread(new SynchDataWithServerUndeliveredMessages(context)).start();
            context.startService(new Intent(context, MsgServiceConnection.class));
            return Result.success();
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "Login runnable Exception: " + ec);
            preferences.setFCMToken("NT");
            return Result.retry();
        }
    }


    public static class UserNotLoggedException extends Exception
    {
        public UserNotLoggedException() {
        }

        @Override
        public String getMessage()
        {
            return "Operation districted: User not successfully logged in.";
        }

        @Override
        public String getLocalizedMessage()
        {
            return "Operation districted: User not successfully logged in.";
        }
    }
}
