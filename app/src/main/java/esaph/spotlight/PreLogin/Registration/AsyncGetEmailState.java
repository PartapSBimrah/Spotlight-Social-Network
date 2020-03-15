package esaph.spotlight.PreLogin.Registration;

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
import esaph.spotlight.SocketResources;

public class AsyncGetEmailState extends AsyncTask<Void, Void, Boolean>
{
    private ProgressDialog progressDialog;
    private WeakReference<Context> contextWeakReference;
    private WeakReference<EmailStateListener> emailStateListenerWeakReference;

    public AsyncGetEmailState(Context context, EmailStateListener emailStateListener)
    {
        this.contextWeakReference = new WeakReference<Context>(context);
        this.emailStateListenerWeakReference = new WeakReference<EmailStateListener>(emailStateListener);
    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();
        this.progressDialog = new ProgressDialog(this.contextWeakReference.get());
        this.progressDialog.setCanceledOnTouchOutside(false);
        this.progressDialog.setTitle(this.contextWeakReference.get().getResources().getString(R.string.txt__alertCheckMailTitle));
        this.progressDialog.setMessage(this.contextWeakReference.get().getResources().getString(R.string.txt__alertCheckMailDetails));
        this.progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        this.progressDialog.show();
    }

    @Override
    protected Boolean doInBackground(Void... voids)
    {
        try
        {
            JSONObject jsonObjectRegister = new JSONObject();
            jsonObjectRegister.put("LRC", "LRCEA");

            SocketResources resources = new SocketResources();
            SSLSocket registerSocket = EsaphSSLSocket.getSSLInstance(this.contextWeakReference.get(), resources.getServerAddress(), resources.getServerPortLRServer());
            BufferedReader reader = new BufferedReader(new InputStreamReader(registerSocket.getInputStream()));
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(registerSocket.getOutputStream()));
            writer.println(jsonObjectRegister.toString());
            writer.flush();
            String result = reader.readLine();

            if(result.equals("1"))
            {
                return Boolean.TRUE;
            }
            else if(result.equals("0"))
            {
                return Boolean.FALSE;
            }
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "AsyncGetEmailState() failed: " + ec);
        }
        return null;
    }


    @Override
    protected void onPostExecute(Boolean aBoolean)
    {
        super.onPostExecute(aBoolean);
        if(progressDialog != null)
        {
            this.progressDialog.cancel();
        }

        EmailStateListener emailStateListener = this.emailStateListenerWeakReference.get();
        if(aBoolean != null && emailStateListener != null)
        {
            if(aBoolean)
            {
                this.emailStateListenerWeakReference.get().onEmailCanBeSent();
            }
            else
            {
                this.emailStateListenerWeakReference.get().onEmailLimitReached();
            }
        }
    }
}
