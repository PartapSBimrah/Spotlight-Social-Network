package esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EsaphSpotLightStickers.EsaphStickerPickerChat.EsaphStickerPickerViews.Background;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import javax.net.ssl.SSLSocket;

import esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EsaphSpotLightStickers.Model.EsaphSpotLightSticker;
import esaph.spotlight.Esaph.EsaphSpotLightPhotoEditor.EsaphSpotLightStickers.Model.EsaphSpotLightStickerPack;
import esaph.spotlight.EsaphSSLSocket;
import esaph.spotlight.SocketResources;
import esaph.spotlight.databases.SQLSticker;
import esaph.spotlight.services.SpotLightMessageConnection.Workers.SpotLightLoginSessionHandler;

public class AsyncLoadStickersFromServer extends AsyncTask<Void, Void, Boolean>
{
    private WeakReference<Context> contextWeakReference;
    private WeakReference<OnStickerPackSynchronizedListener> onStickerPackSynchronizedListenerWeakReference;

    public AsyncLoadStickersFromServer(Context context, OnStickerPackSynchronizedListener onStickerPackSynchronizedListener)
    {
        this.contextWeakReference = new WeakReference<>(context);
        this.onStickerPackSynchronizedListenerWeakReference = new WeakReference<OnStickerPackSynchronizedListener>(onStickerPackSynchronizedListener);
    }

    public interface OnStickerPackSynchronizedListener
    {
        void onStickerPackSynchronized();
    }

    @Override
    protected Boolean doInBackground(Void... voids)
    {
        try
        {
            SocketResources socketResources = new SocketResources();

            JSONObject jsonObjectLastConv = new JSONObject();
            jsonObjectLastConv.put("PLSC", "PLSOSP");
            jsonObjectLastConv.put("USRN", SpotLightLoginSessionHandler.getLoggedUID());
            jsonObjectLastConv.put("SID", SpotLightLoginSessionHandler.getSpotLightSessionId());

            SQLSticker sqlSticker = new SQLSticker(contextWeakReference.get());
            int countStickerPack = sqlSticker.getCountStickerPacks();
            sqlSticker.close();

            if(countStickerPack < 0) return Boolean.FALSE;

            jsonObjectLastConv.put("POS", countStickerPack);

            SSLSocket sidSocket = EsaphSSLSocket.getSSLInstance(this.contextWeakReference.get(), socketResources.getServerAddress(), socketResources.getServerPortPServer());
            sidSocket.setSoTimeout(15000);
            BufferedReader reader = new BufferedReader(new InputStreamReader(sidSocket.getInputStream()));
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(sidSocket.getOutputStream()));
            writer.println(jsonObjectLastConv.toString());
            writer.flush();

            JSONArray jsonObjectMain = new JSONArray(reader.readLine()); //Contains Stickerpacks with all stickers.
            sidSocket.close();
            reader.close();
            writer.close();
            return insertData(jsonObjectMain);
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "AsyncLoadStickersFromServer() failed: " + ec);
        }
        return Boolean.FALSE;
    }

    private Boolean insertData(JSONArray jsonObjectMain)
    {
        Context context = this.contextWeakReference.get();
        if(context == null)
            return Boolean.FALSE;

        SQLSticker sqlStickers = new SQLSticker(context);

        try
        {
            for(int counter = 0; counter < jsonObjectMain.length(); counter++)
            {
                JSONObject jsonObjectStickerPack = jsonObjectMain.getJSONObject(counter);


                List<EsaphSpotLightSticker> esaphSpotLightStickers = new ArrayList<>();
                JSONArray jsonArrayStickersFromPack = jsonObjectStickerPack.getJSONArray("STA");
                for(int counterStickers = 0; counterStickers < jsonArrayStickersFromPack.length(); counterStickers++)
                {
                    JSONObject jsonObjectSticker = jsonArrayStickersFromPack.getJSONObject(counterStickers);
                    esaphSpotLightStickers.add(new EsaphSpotLightSticker(
                            jsonObjectSticker.getLong("CR"),
                            jsonObjectSticker.getLong("LSID"),
                            jsonObjectStickerPack.getLong("LSPID"),
                            jsonObjectSticker.getString("STID"),
                            jsonObjectSticker.getLong("CT")));
                }


                EsaphSpotLightStickerPack esaphSpotLightStickerPack = new EsaphSpotLightStickerPack(
                        jsonObjectStickerPack.getString("PN"),
                        jsonObjectStickerPack.getLong("LSPID"),
                        jsonObjectStickerPack.getLong("CR"),
                        jsonObjectStickerPack.getLong("CT"),
                        esaphSpotLightStickers);

                sqlStickers.addStickerWithStickerPack(esaphSpotLightStickerPack);
            }
        }
        catch (Exception ec)
        {
            return Boolean.FALSE;
        }
        finally
        {
            sqlStickers.close();
        }

        return jsonObjectMain.length() > 0;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean)
    {
        super.onPostExecute(aBoolean);
        if(aBoolean != null && aBoolean)
        {
            OnStickerPackSynchronizedListener onStickerPackSynchronizedListener = this.onStickerPackSynchronizedListenerWeakReference.get();
            if(onStickerPackSynchronizedListener != null)
            {
                onStickerPackSynchronizedListener.onStickerPackSynchronized();
            }
        }
    }
}
