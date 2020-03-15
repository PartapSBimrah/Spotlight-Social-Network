package esaph.spotlight.services.FirebaseMessaging;

import android.os.PowerManager;
import android.util.Log;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.net.ssl.SSLSocket;
import esaph.spotlight.EsaphSSLSocket;
import esaph.spotlight.SocketResources;
import esaph.spotlight.einstellungen.CLPreferences;

public class FCMIDService extends FirebaseInstanceIdService
{
    private static final String wakeLock = "esaph.spotlight:FirebaseTokenLock";
    @Override
    public void onTokenRefresh()
    {
        SocketResources resources = new SocketResources();
        CLPreferences preferences = new CLPreferences(getApplicationContext());
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        PowerManager.WakeLock wakeLockA = null;
        if(powerManager != null)
        {
            wakeLockA = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, wakeLock);
            if(!wakeLockA.isHeld())
            {
                wakeLockA.acquire(10*60*1000L /*10 minutes*/);
            }
        }

        String myToken = FirebaseInstanceId.getInstance().getToken();


        try
        {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("LRC", "LRFC");
            jsonObject.put("USRN", preferences.getUID());
            jsonObject.put("PW", preferences.getPasswordEncrypted());
            jsonObject.put("FCMT", myToken);
            SSLSocket loginSocket = EsaphSSLSocket.getSSLInstance(getApplicationContext(), resources.getServerAddress(), resources.getServerPortLRServer());

            BufferedReader reader = new BufferedReader(new InputStreamReader(loginSocket.getInputStream()));
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(loginSocket.getOutputStream()));
            writer.println(jsonObject.toString());
            writer.flush();

            String reply = reader.readLine();

            if (reply.equals("LRLT"))
            {
                Log.i(getClass().getName(), "FMM Update Successfull.");
                preferences.setFCMToken(myToken);
            }
            else
            {
                preferences.setFCMToken("NT-1");
            }
            reader.close();
            writer.close();
            loginSocket.close();
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "Cant upload new token to server: " + ec);
        }
        finally
        {
            if(wakeLockA != null && wakeLockA.isHeld())
            {
                wakeLockA.release();
            }
        }
    }
}
