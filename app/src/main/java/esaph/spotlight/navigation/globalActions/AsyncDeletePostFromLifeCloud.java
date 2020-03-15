package esaph.spotlight.navigation.globalActions;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;

import javax.net.ssl.SSLSocket;

import esaph.spotlight.Esaph.EsaphLifeCloudBackup.LifeCloudUpload;
import esaph.spotlight.EsaphSSLSocket;
import esaph.spotlight.SocketResources;
import esaph.spotlight.databases.SQLLifeCloud;
import esaph.spotlight.services.SpotLightMessageConnection.Workers.SpotLightLoginSessionHandler;

public class AsyncDeletePostFromLifeCloud extends AsyncTask<Void, Void, Boolean>
{
    private WeakReference<Context> context;
    private SoftReference<LifeCloudDeleteListener> cloudDeleteListenerSoftReference;
    private LifeCloudUpload lifeCloudUpload;

    public interface LifeCloudDeleteListener
    {
        void onDeletedPost(LifeCloudUpload lifeCloudUpload);
        void onFailedDeletingPost(LifeCloudUpload lifeCloudUpload);
    }

    public AsyncDeletePostFromLifeCloud(Context context,
                                        LifeCloudDeleteListener lifeCloudDeleteListener,
                                        LifeCloudUpload lifeCloudUpload)
    {
        this.cloudDeleteListenerSoftReference = new SoftReference<>(lifeCloudDeleteListener);
        this.context = new WeakReference<>(context);
        this.lifeCloudUpload = lifeCloudUpload;
    }

    @Override
    protected Boolean doInBackground(Void... params)
    {
        try
        {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("LCS", "LCDLP");
            jsonObject.put("USRN", SpotLightLoginSessionHandler.getLoggedUID());
            jsonObject.put("SID", SpotLightLoginSessionHandler.getSpotLightSessionId());
            jsonObject.put("PID", this.lifeCloudUpload.getCLOUD_PID());

            SocketResources resources = new SocketResources();
            SSLSocket socket = EsaphSSLSocket.getSSLInstance(this.context.get(), resources.getServerAddress(), resources.getServerPortLCServer());
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            writer.println(jsonObject.toString());
            writer.flush();

            String result = reader.readLine();
            if(result.equals("1")) //Deleted
            {
                SQLLifeCloud sqlLifeCloud = new SQLLifeCloud(this.context.get());
                sqlLifeCloud.deletePostLifeCloud(lifeCloudUpload);
                sqlLifeCloud.close();
                return Boolean.TRUE;
            }

            return Boolean.FALSE;
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "AsyncDeletePostFromLifeCloud() failed: " + ec);
            return Boolean.FALSE;
        }
    }


    @Override
    protected void onPostExecute(Boolean aBoolean)
    {
        super.onPostExecute(aBoolean);
        LifeCloudDeleteListener lifeCloudDeleteListener = cloudDeleteListenerSoftReference.get();
        if(lifeCloudDeleteListener != null)
        {
            if(aBoolean != null)
            {
                if(aBoolean)
                {
                    lifeCloudDeleteListener.onDeletedPost(this.lifeCloudUpload);
                }
                else
                {
                    lifeCloudDeleteListener.onFailedDeletingPost(this.lifeCloudUpload);
                }
            }
            else
            {
                lifeCloudDeleteListener.onFailedDeletingPost(this.lifeCloudUpload);
            }
        }
    }
}
