package esaph.spotlight.navigation.navigationLeftSite.VerticalViewPager.Pages;

import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;

import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import esaph.spotlight.databases.SQLChats;

public class RunnableLoadMoreStoryViewerTodayFlipperPublic implements Runnable
{
    private static AtomicBoolean obLock = new AtomicBoolean(false);
    private WeakReference<Context> contextWeakReference;
    private SoftReference<LoadingWaiter> loadingWaiterSoftReference;
    private int startFrom;

    public RunnableLoadMoreStoryViewerTodayFlipperPublic(Context context,
                                                         LoadingWaiter loadingWaiter,
                                                         int startFrom)
    {
        this.contextWeakReference = new WeakReference<>(context);
        this.loadingWaiterSoftReference = new SoftReference<>(loadingWaiter);
        this.startFrom = startFrom;
    }


    public interface LoadingWaiter
    {
        void dataLoaded(List<Object> data);
    }

    @Override
    public void run()
    {
        if(!RunnableLoadMoreStoryViewerTodayFlipperPublic.obLock.compareAndSet(false, true))
            return;


        final List<Object> list = new ArrayList<>();
        SQLChats sqlChats = null;
        try
        {
            sqlChats = new SQLChats(this.contextWeakReference.get());
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "RunnableLoadMoreStoryViewerTodayFlipper run() failed: " + ec);
        }
        finally
        {
            ((AppCompatActivity)contextWeakReference.get()).runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    if(loadingWaiterSoftReference.get() != null)
                    {
                        loadingWaiterSoftReference.get().dataLoaded(list);
                    }
                    RunnableLoadMoreStoryViewerTodayFlipperPublic.obLock.set(false);
                }
            });

            if(sqlChats != null)
            {
                sqlChats.close();
            }
        }
    }
}
