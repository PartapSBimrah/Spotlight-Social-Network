package esaph.spotlight.navigation.kamera;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;
import esaph.spotlight.R;
import esaph.spotlight.databases.SQLFriends;
import esaph.spotlight.navigation.Posting.ArrayAdapterListFriends;

public class RunLoadDataForSending implements Runnable
{
    private static final Object lock = new Object();
    private SoftReference<Context> context;
    private SoftReference<ArrayAdapterListFriends> arrayAdapterListWatcherWeakReference;

    public RunLoadDataForSending(Context context, ArrayAdapterListFriends arrayAdapterListWatcher)
    {
        this.context = new SoftReference<Context>(context);
        this.arrayAdapterListWatcherWeakReference = new SoftReference<ArrayAdapterListFriends>(arrayAdapterListWatcher);
    }

    private void subListByFife(List<Object> arr)
    {
        if(arr.size() > 5)
        {
            arr.subList(5, arr.size()).clear();
        }
    }

    @Override
    public void run()
    {
        synchronized (RunLoadDataForSending.lock)
        {
            try
            {
                final List<Object> toReturn = new ArrayList<>();

                SQLFriends sqlFriends = new SQLFriends(this.context.get());
                toReturn.addAll(sqlFriends.getAllWatchers());
                sqlFriends.close();

                if(!toReturn.isEmpty() && this.arrayAdapterListWatcherWeakReference.get().getChatCount() == 0)
                {
                    toReturn.add(0, new ArrayAdapterListFriends.TopSchriftPlaceholder(this.context.get().getResources().getString(R.string.txt_title_top_person_posts)));
                }

                new Handler(Looper.getMainLooper()).post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        ArrayAdapterListFriends arrayAdapterListWatcher = arrayAdapterListWatcherWeakReference.get();
                        if(arrayAdapterListWatcher != null)
                        {
                            arrayAdapterListWatcher.pushData(toReturn);
                        }
                    }
                });
            }
            catch (Exception ec)
            {
                Log.i(getClass().getName(), "RunLoadDataForSending() failed: " + ec);
            }
        }
    }
}

