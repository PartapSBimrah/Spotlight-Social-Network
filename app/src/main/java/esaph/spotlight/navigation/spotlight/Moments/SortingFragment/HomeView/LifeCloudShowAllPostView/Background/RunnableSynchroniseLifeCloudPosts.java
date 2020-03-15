package esaph.spotlight.navigation.spotlight.Moments.SortingFragment.HomeView.LifeCloudShowAllPostView.Background;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import javax.net.ssl.SSLSocket;

import esaph.spotlight.Esaph.EsaphLifeCloudBackup.LifeCloudUpload;
import esaph.spotlight.EsaphSSLSocket;
import esaph.spotlight.SocketResources;
import esaph.spotlight.databases.SQLLifeCloud;
import esaph.spotlight.navigation.kamera.PostEditingFragments.EsaphTagging.EsaphHashtag;
import esaph.spotlight.services.SpotLightMessageConnection.Workers.SpotLightLoginSessionHandler;

public class RunnableSynchroniseLifeCloudPosts implements Runnable
{
    private static final Object obLock = new Object();
    private WeakReference<Context> context;
    private SoftReference<LifeCloudSynchListener> lifeCloudSynchListenerWeakReference;

    public RunnableSynchroniseLifeCloudPosts(Context context,
                                             LifeCloudSynchListener lifeCloudSynchListener)
    {
        this.context = new WeakReference<Context>(context);
        this.lifeCloudSynchListenerWeakReference = new SoftReference<LifeCloudSynchListener>(lifeCloudSynchListener);
    }

    public interface LifeCloudSynchListener
    {
        void onNewData();
        void onFailed();
    }

    private boolean synched = false;
    @Override
    public void run()
    {
        synchronized (RunnableSynchroniseLifeCloudPosts.obLock)
        {
            try
            {
                SQLLifeCloud sqlLifeCloud = new SQLLifeCloud(this.context.get());

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("LCS", "LFGAP");
                jsonObject.put("USRN", SpotLightLoginSessionHandler.getLoggedUID());
                jsonObject.put("SID", SpotLightLoginSessionHandler.getSpotLightSessionId());
                jsonObject.put("SF", sqlLifeCloud.getCountOfAllLifeCloudPosts());

                SocketResources resources = new SocketResources();
                SSLSocket socket = EsaphSSLSocket.getSSLInstance(this.context.get(), resources.getServerAddress(), resources.getServerPortLCServer());
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
                writer.println(jsonObject.toString());
                writer.flush();


                JSONArray jsonArrayLifeCloudPosts = new JSONArray(reader.readLine());

                for(int counter = 0; counter < jsonArrayLifeCloudPosts.length(); counter++)
                {
                    JSONObject object = jsonArrayLifeCloudPosts.getJSONObject(counter);

                    ArrayList<EsaphHashtag> esaphHashtag = new ArrayList<>();
                    JSONArray jsonArrayHashtags = object.getJSONArray("ARR_EHT");
                    for (int counterHashtag = 0; counterHashtag < jsonArrayHashtags.length(); counterHashtag++)
                    {
                        JSONObject jsonObjectHashtag = jsonArrayHashtags.getJSONObject(counterHashtag);
                        esaphHashtag.add(new EsaphHashtag(jsonObjectHashtag.getString("TAG"),
                                null,
                                0));
                    }

                    sqlLifeCloud.insertNewLifeCloudUpload(new LifeCloudUpload(
                            esaphHashtag,
                            object.getString("DESC"),
                            object.getString("PID"),
                            object.getLong("TP"),
                            LifeCloudUpload.LifeCloudStatus.STATE_UPLOADED,
                            (short) object.getInt("DT"),
                            (short) object.getInt("PT")
                    ));
                }

                if(jsonArrayLifeCloudPosts.length() > 0)
                {
                    synched = true;
                }

                sqlLifeCloud.close();
            }
            catch (Exception ec)
            {
                Log.i(getClass().getName(), "RunnableSynchroniseLifeCloudPosts() failed: " + ec);
            }
            finally
            {
                LifeCloudSynchListener lifeCloudSynchListener = lifeCloudSynchListenerWeakReference.get();
                if(lifeCloudSynchListener != null)
                {
                    if(synched)
                    {
                        lifeCloudSynchListener.onNewData();
                    }
                    else
                    {
                        lifeCloudSynchListener.onFailed();
                    }
                }
            }
        }
    }

}
