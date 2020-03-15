package esaph.spotlight.navigation.spotlight.Moments.InternViews.SavedViews.BigViewAll.Season;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import esaph.spotlight.Esaph.EsaphBackgroundLoadingHandle.EsaphDataLoader;
import esaph.spotlight.databases.SQLChats;
import esaph.spotlight.navigation.spotlight.Moments.AdapterSorting.EsaphMomentsRecylerView;
import esaph.spotlight.navigation.spotlight.Moments.AdapterSorting.YearTime;
import esaph.spotlight.navigation.spotlight.Moments.DataBaseLoadWaiter;

public class RunnableAlgorythmLoadMoreYeartimeSeason extends EsaphDataLoader
{
    private YearTime yearTime;
    private int currentYear;

    public RunnableAlgorythmLoadMoreYeartimeSeason(Context context, DataBaseLoadWaiter dataBaseLoadWaiter, EsaphMomentsRecylerView esaphMomentsRecylerView,
                                                   AtomicBoolean atomicBoolean,
                                                   YearTime yearTime,
                                                   int currentYear) {
        super(context, dataBaseLoadWaiter, esaphMomentsRecylerView, atomicBoolean);
        this.currentYear = currentYear;
        this.yearTime = yearTime;
    }

    private int imageCount = 0;
    @Override
    public void run()
    {
        super.run();

        final List<Object> list = new ArrayList<>();
        try
        {
            SQLChats sqlChats = new SQLChats(super.getSoftReferenceContext().get());
            long[] spacings = YearTime.getSeasonSpacing(yearTime.getSeason(), currentYear);
            list.addAll(sqlChats.getConversationMessagesFromSeasonLimited(super.getStartFrom()[0], spacings[0], spacings[1]));
            sqlChats.close();
            imageCount = list.size();
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "RunnableAlgorythmLoadMoreYeartimeSeason, background() failed: " + ec);
        }
        finally
        {
            publish(list);
        }
    }
}
