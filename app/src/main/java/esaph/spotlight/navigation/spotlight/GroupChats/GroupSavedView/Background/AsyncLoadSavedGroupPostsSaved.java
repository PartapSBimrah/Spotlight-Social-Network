package esaph.spotlight.navigation.spotlight.GroupChats.GroupSavedView.Background;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import esaph.spotlight.databases.SQLGroups;

public class AsyncLoadSavedGroupPostsSaved extends AsyncTask<Void, Void, List<Object>>
{
    private String GIID;
    private WeakReference<ProgressBar> progressBar;
    private WeakReference<Context> context;
    private SavedPostInternLoadListener savedPostInternLoadListener;
    private int totalCount;

    public AsyncLoadSavedGroupPostsSaved(Context context, String GIID, int totalCount, SavedPostInternLoadListener savedPostInternLoadListener, ProgressBar progressBar)
    {
        this.savedPostInternLoadListener = savedPostInternLoadListener;
        this.totalCount = totalCount;
        this.context = new WeakReference<Context>(context);
        this.progressBar = new WeakReference<ProgressBar>(progressBar);
        this.GIID = GIID;
    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();
        if(this.progressBar.get() != null)
        {
            this.progressBar.get().setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected List<Object> doInBackground(Void... params)
    {
        try
        {
            SQLGroups sqlGroups = new SQLGroups(this.context.get());
            List<Object> toReturn = sqlGroups.getAllSavedGroupPostsLimited(this.totalCount, this.GIID);
            return toReturn;
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "AsyncLoadSavedGroupPostsSaved, doinBackground() failed: " + ec);
            return new ArrayList<>();
        }
    }

    @Override
    protected void onPostExecute(List<Object> objects)
    {
        super.onPostExecute(objects);
        if(this.progressBar.get() != null)
        {
            this.progressBar.get().setVisibility(View.INVISIBLE);
        }

        savedPostInternLoadListener.onInternDataLoad(objects);
    }
}
