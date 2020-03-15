package esaph.spotlight.navigation.spotlight.PublicSearch.Background;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLSocket;

import esaph.spotlight.R;
import esaph.spotlight.navigation.spotlight.PublicSearch.Adapter.Model.SearchItemUser;
import esaph.spotlight.navigation.spotlight.PublicSearch.Adapter.Model.SearchStatus;
import esaph.spotlight.navigation.spotlight.Moments.MemoryObjects.TitleList;
import esaph.spotlight.navigation.spotlight.PublicSearch.Adapter.EsaphSearchTabAdapter;

public class AsyncSearchInMode extends AsyncTask<Void, Void, List<Object>>
{
    private String input;
    private WeakReference<Context> contextWeakReference;
    private WeakReference<EsaphSearchTabAdapter> esaphSearchTabAdapterWeakReference;
    private SSLSocket searchSocket;
    private PrintWriter writer;
    private BufferedReader reader;

    public AsyncSearchInMode(String input,
                              Context context,
                              EsaphSearchTabAdapter esaphSearchTabAdapter,
                              SSLSocket searchSocket,
                              PrintWriter writer,
                              BufferedReader reader)
    {
        this.input = input;
        this.contextWeakReference = new WeakReference<Context>(context);
        this.esaphSearchTabAdapterWeakReference = new WeakReference<EsaphSearchTabAdapter>(esaphSearchTabAdapter);
        this.searchSocket = searchSocket;
        this.writer = writer;
        this.reader = reader;
    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();

        EsaphSearchTabAdapter esaphSearchTabAdapter = esaphSearchTabAdapterWeakReference.get();
        if(esaphSearchTabAdapter != null)
        {
            esaphSearchTabAdapter.clearAll();
            esaphSearchTabAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected List<Object> doInBackground(Void... params)
    {
        try
        {
            List<Object> searchItemList = new ArrayList<>();
            searchSocket.setSoTimeout(10000);
            writer.println(this.input);
            writer.flush();

            JSONArray jsonArray = new JSONArray(reader.readLine());

            if(jsonArray.length() > 0)
            {
                searchItemList.add(new TitleList(contextWeakReference.get().getResources().getString(R.string.txt_gefunden)));
            }

            for(int counter = 0; counter < jsonArray.length(); counter++)
            {
                JSONObject jsonObject = jsonArray.getJSONObject(counter);
                searchItemList.add(new SearchItemUser(
                        jsonObject.getLong("UID"),
                        jsonObject.getString("USRN"),
                        jsonObject.getString("VN"),
                        (short) jsonObject.getInt("FS"),
                        jsonObject.getString("RE")));
            }
            searchSocket.setSoTimeout(30000);
            return searchItemList;
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "Searching in task failed(SearchUser): " + ec);
            return new ArrayList<>();
        }
    }

    @Override
    protected void onPostExecute(List<Object> items)
    {
        try
        {
            Log.i(getClass().getName(), "SEARCHING USERS SIZE: " + items.size());
            if(!items.isEmpty())
            {
                EsaphSearchTabAdapter esaphSearchTabAdapter = this.esaphSearchTabAdapterWeakReference.get();
                if(esaphSearchTabAdapter != null)
                {
                    esaphSearchTabAdapter.clearAll();
                    esaphSearchTabAdapter.addAll(items);
                    esaphSearchTabAdapter.notifyDataSetChanged();
                }
            }
            else
            {
                EsaphSearchTabAdapter esaphSearchTabAdapter = this.esaphSearchTabAdapterWeakReference.get();
                if(esaphSearchTabAdapter != null)
                {
                    items.add(0, new SearchStatus(this.contextWeakReference.get().getResources().getString(R.string.txt_searchStatus_KeinErgebniss), 1));
                    esaphSearchTabAdapter.clearAll();
                    esaphSearchTabAdapter.addAll(items);
                    esaphSearchTabAdapter.notifyDataSetChanged();
                }
            }
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "SearchResult in task failed(SearchUser): " + ec);
        }
    }
}