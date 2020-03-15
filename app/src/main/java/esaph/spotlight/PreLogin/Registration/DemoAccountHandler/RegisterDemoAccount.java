package esaph.spotlight.PreLogin.Registration.DemoAccountHandler;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import androidx.appcompat.app.AlertDialog;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.UUID;

import javax.net.ssl.SSLSocket;

import esaph.spotlight.EsaphSSLSocket;
import esaph.spotlight.R;
import esaph.spotlight.SocketResources;
import esaph.spotlight.einstellungen.CLPreferences;
import esaph.spotlight.services.SpotLightMessageConnection.Workers.SpotLightLoginSessionHandler;

public class RegisterDemoAccount extends AsyncTask<String, Integer, String>
{
    private ProgressDialog progressDialog;
    private WeakReference<Context> contextWeakReference;
    private SoftReference<RegisterDemoAccountStateListener> registerDemoAccountStateListenerWeakReference;
    private String Username;
    private String Password;
    private long UID;

    public RegisterDemoAccount(Context context,
                               RegisterDemoAccountStateListener registerDemoAccountStateListener)
    {
        this.contextWeakReference = new WeakReference<>(context);
        this.registerDemoAccountStateListenerWeakReference = new SoftReference<>(registerDemoAccountStateListener);
    }

    public interface RegisterDemoAccountStateListener
    {
        void onSuccess();
        void onFailed();
    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();
        Context context = this.contextWeakReference.get();
        if(context != null)
        {
            this.progressDialog = new ProgressDialog(context);
            this.progressDialog.setCanceledOnTouchOutside(false);
            this.progressDialog.setTitle(context.getResources().getString(R.string.txt__alertRegister));
            this.progressDialog.setMessage(context.getResources().getString(R.string.txt__alertUnoMomento));
            this.progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            this.progressDialog.show();
        }
    }

    @Override
    protected String doInBackground(String... params)
    {
        String register = null;
        try
        {
            SocketResources socketResources = new SocketResources();
            JSONObject jsonObjectRegister = new JSONObject();
            jsonObjectRegister.put("LRC", "LRRDA");
            this.Username = UUID.randomUUID().toString().substring(0, 10);
            this.Password = UUID.randomUUID().toString().substring(0, 10);
            jsonObjectRegister.put("USRN", this.Username);
            jsonObjectRegister.put("PW", this.Password);
            jsonObjectRegister.put("FCMT", FirebaseInstanceId.getInstance().getToken());

            SSLSocket registerSocket = EsaphSSLSocket.getSSLInstance(this.contextWeakReference.get(), socketResources.getServerAddress(), socketResources.getServerPortLRServer());
            BufferedReader reader = new BufferedReader(new InputStreamReader(registerSocket.getInputStream()));
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(registerSocket.getOutputStream()));

            writer.println(jsonObjectRegister.toString());
            writer.flush();
            register = reader.readLine();
            JSONObject jsonObject = new JSONObject(reader.readLine());
            SpotLightLoginSessionHandler.setSpotLightUserLogged(jsonObject.getString("SID"),
                    jsonObject.getString("USRN"),
                    jsonObject.getLong("UID"),
                    true);

            UID = jsonObject.getLong("UID");

            writer.close();
            reader.close();
            registerSocket.close();
            return register;
        }
        catch(EOFException eof)
        {
            return register;
        }
        catch(Exception ec)
        {
            Log.e(getClass().getName(), "RegisterDemoAccount doInBackground() failed: " + ec);
            return register;
        }
    }

    @Override
    protected void onPostExecute(String result)
    {
        try
        {
            this.progressDialog.cancel();
        }
        catch (Exception ec)
        {
        }

        RegisterDemoAccountStateListener registerDemoAccountStateListener = registerDemoAccountStateListenerWeakReference.get();
        if(registerDemoAccountStateListener != null)
        {
            if(result != null)
            {
                if (result.equals("LRRT"))
                {
                    Context context = contextWeakReference.get();
                    if(context != null && this.Username != null && this.Password != null)
                    {
                        CLPreferences preferences = new CLPreferences(context);
                        preferences.setUpUser(this.UID, this.Username, this.Password, true);
                        registerDemoAccountStateListener.onSuccess();
                    }
                }
                else
                {
                    registerDemoAccountStateListener.onFailed();
                }
            }
            else
            {
                Context context = this.contextWeakReference.get();
                if(context != null)
                {

                    AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                    dialog.setTitle(context.getResources().getString(R.string.txt__alertRegister));
                    dialog.setMessage(context.getResources().getString(R.string.txt__alertNeverHappens));
                    dialog.show();
                }
            }
        }
    }
}

