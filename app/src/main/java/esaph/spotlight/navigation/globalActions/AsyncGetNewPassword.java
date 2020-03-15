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

import javax.net.ssl.SSLSocket;

import esaph.spotlight.PreLogin.Dialogs.DialogPasswortVergessen;
import esaph.spotlight.EsaphSSLSocket;
import esaph.spotlight.SocketResources;

public class AsyncGetNewPassword extends AsyncTask<Void, Void, String>
{
    private WeakReference<Context> weakReferenceContext;
    private WeakReference<DialogPasswortVergessen.OnNewPasswordRequestedListener> callback;
    private String Username;

    public AsyncGetNewPassword(Context context, String Username, DialogPasswortVergessen.OnNewPasswordRequestedListener callback)
    {
        this.weakReferenceContext = new WeakReference<Context>(context);
        this.Username = Username;
        this.callback = new WeakReference<DialogPasswortVergessen.OnNewPasswordRequestedListener>(callback);
    }

    @Override
    protected String doInBackground(Void... voids)
    {
        try
        {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("LRC", "LRNPR");
            jsonObject.put("USRN", this.Username);

            SocketResources resources = new SocketResources();
            SSLSocket socket = EsaphSSLSocket.getSSLInstance(this.weakReferenceContext.get(), resources.getServerAddress(), resources.getServerPortLRServer());
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            writer.println(jsonObject.toString());
            writer.flush();

            String Reply = reader.readLine();

            socket.close();
            writer.close();
            reader.close();
            return Reply;
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "AsyncGetNewPassword() failed: " + ec);
            return "-1";
        }
    }

    @Override
    protected void onPostExecute(String s)
    {
        super.onPostExecute(s);
        if(s != null && !s.isEmpty())
        {
            DialogPasswortVergessen.OnNewPasswordRequestedListener onNewPasswordRequestedListener = this.callback.get();
            if(onNewPasswordRequestedListener != null)
            {
                onNewPasswordRequestedListener.onRequestedResult(s);
            }
        }
        else
        {
            DialogPasswortVergessen.OnNewPasswordRequestedListener onNewPasswordRequestedListener = this.callback.get();
            if(onNewPasswordRequestedListener != null)
            {
                onNewPasswordRequestedListener.onRequestedResult("-2");
            }
        }
    }
}
