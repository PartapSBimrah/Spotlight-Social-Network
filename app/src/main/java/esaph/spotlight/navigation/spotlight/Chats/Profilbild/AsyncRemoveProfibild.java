package esaph.spotlight.navigation.spotlight.Chats.Profilbild;

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

import esaph.spotlight.StorageManagment.StorageHandlerProfilbild;
import esaph.spotlight.EsaphSSLSocket;
import esaph.spotlight.SocketResources;
import esaph.spotlight.services.SpotLightMessageConnection.Workers.SpotLightLoginSessionHandler;

public class AsyncRemoveProfibild extends AsyncTask<Void, Void, Boolean>
{
    private WeakReference<Context> contextWeakReference;
    private WeakReference<OnRemovingPbListener> onRemovingPbListenerWeakReference;

    public AsyncRemoveProfibild(Context context, OnRemovingPbListener onRemovingPbListener)
    {
        this.contextWeakReference = new WeakReference<Context>(context);
        this.onRemovingPbListenerWeakReference = new WeakReference<OnRemovingPbListener>(onRemovingPbListener);
    }

    public interface OnRemovingPbListener
    {
        void onSuccess();
        void onFailed();
    }

    @Override
    protected Boolean doInBackground(Void... voids)
    {
        boolean success = false;
        try
        {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("PLSC", "PLDPB");
            jsonObject.put("USRN", SpotLightLoginSessionHandler.getLoggedUID());
            jsonObject.put("SID", SpotLightLoginSessionHandler.getSpotLightSessionId());


            SocketResources resources = new SocketResources();
            SSLSocket socket = EsaphSSLSocket.getSSLInstance(this.contextWeakReference.get(), resources.getServerAddress(), resources.getServerPortPServer());
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            writer.println(jsonObject.toString());
            writer.flush();

            if(reader.readLine().equals("1"))
            {
                StorageHandlerProfilbild.deleteFile(contextWeakReference.get(), SpotLightLoginSessionHandler.getLoggedUID());
                success = true;
            }
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "AsyncRemoveProfilbild, doInBackground() failed: " + ec);
        }


        return success;
    }


    @Override
    protected void onPostExecute(Boolean aBoolean)
    {
        super.onPostExecute(aBoolean);
        try
        {
            OnRemovingPbListener onRemovingPbListener = onRemovingPbListenerWeakReference.get();
            if(onRemovingPbListener != null)
            {
                if(aBoolean)
                {
                    onRemovingPbListener.onSuccess();
                }
                else
                {
                    onRemovingPbListener.onFailed();
                }
            }
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "AsyncRemoveProfilbild() onPostExecute() failed: " + ec);
        }
    }
}
