package esaph.spotlight.navigation.spotlight.Moments.LoadingTasks;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import esaph.spotlight.Esaph.EsaphBackgroundLoadingHandle.EsaphDataLoader;
import esaph.spotlight.databases.SQLHashtags;
import esaph.spotlight.navigation.spotlight.Moments.AdapterSorting.EsaphMomentsRecylerView;
import esaph.spotlight.navigation.spotlight.Moments.DataBaseLoadWaiter;

public class AsyncLoadMoreAllByHashtag extends EsaphDataLoader
{
    public AsyncLoadMoreAllByHashtag(Context context, DataBaseLoadWaiter dataBaseLoadWaiter, EsaphMomentsRecylerView esaphMomentsRecylerView,
                                     AtomicBoolean atomicBoolean) {
        super(context, dataBaseLoadWaiter, esaphMomentsRecylerView, atomicBoolean);
    }

    @Override
    public void run()
    {
        super.run();

        List<Object> esaphHashtagList = new ArrayList<>();
        try
        {
            SQLHashtags sqlHashtags = new SQLHashtags(super.getSoftReferenceContext().get());
            esaphHashtagList = sqlHashtags.getAllHashtagLimited(super.getStartFrom()[0]);
            sqlHashtags.close();
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "AsyncLoadMoreAllByHashtag, background() failed: " + ec);
        }
        finally
        {
            publish(esaphHashtagList);
        }
    }
}
