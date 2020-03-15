package esaph.spotlight.navigation.spotlight.Moments.MomentSearching.Background;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import esaph.spotlight.databases.SQLChats;
import esaph.spotlight.databases.SQLHashtags;
import esaph.spotlight.navigation.spotlight.Moments.MomentSearching.Objects.SearchItemMainMoments;

public class SearchDataBase implements Runnable
{
    private WeakReference<Context> contextWeakReference;
    private WeakReference<SearchDataBaseDoneListener> searchDataBaseDoneListenerWeakReference;
    private String searchString;

    public SearchDataBase(Context context, String searchString, SearchDataBaseDoneListener searchDataBaseDoneListener)
    {
        this.contextWeakReference = new WeakReference<Context>(context);
        this.searchString = searchString;
        this.searchDataBaseDoneListenerWeakReference = new WeakReference<SearchDataBaseDoneListener>(searchDataBaseDoneListener);
    }

    public interface SearchDataBaseDoneListener
    {
        void onSearchResult(List<SearchItemMainMoments> list);
    }

    @Override
    public void run()
    {
        SQLHashtags sqlHashtags = null;
        SQLChats sqlChats = null;
        final List<SearchItemMainMoments> listResults = new ArrayList<>();

        try
        {
            sqlHashtags = new SQLHashtags(contextWeakReference.get());
            sqlChats = new SQLChats(contextWeakReference.get());

            listResults.addAll(sqlHashtags.filterHashtags(searchString));
            listResults.addAll(sqlChats.filterChat(searchString));
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "SearchDataBase() failed: " + ec);
        }
        finally
        {
            if(sqlChats != null)
            {
                sqlChats.close();
            }

            if(sqlHashtags != null)
            {
                sqlHashtags.close();
            }

            new Handler(Looper.getMainLooper()).post(new Runnable()
            {
                @Override
                public void run()
                {
                    SearchDataBaseDoneListener searchDataBaseDoneListener = searchDataBaseDoneListenerWeakReference.get();
                    if(searchDataBaseDoneListener != null)
                    {
                        searchDataBaseDoneListener.onSearchResult(listResults);
                    }
                }
            });
        }
    }
}
