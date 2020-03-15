package esaph.spotlight.navigation.spotlight.Moments.LoadingTasks;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import esaph.spotlight.Esaph.EsaphBackgroundLoadingHandle.EsaphDataLoader;
import esaph.spotlight.R;
import esaph.spotlight.databases.SQLChats;
import esaph.spotlight.navigation.spotlight.Chats.Messages.ConversationMessage;
import esaph.spotlight.navigation.spotlight.Moments.AdapterSorting.EsaphMomentsRecylerView;
import esaph.spotlight.navigation.spotlight.Moments.AdapterSorting.YearTime;
import esaph.spotlight.navigation.spotlight.Moments.DataBaseLoadWaiter;
import esaph.spotlight.navigation.spotlight.Moments.MemoryObjects.DatumList;

public class RunnableAlgorythmLoadAll extends EsaphDataLoader
{
    private long lastTime;

    public RunnableAlgorythmLoadAll(Context context, DataBaseLoadWaiter dataBaseLoadWaiter, EsaphMomentsRecylerView esaphMomentsRecylerView,
            AtomicBoolean atomicBoolean, long lastTime) {
        super(context, dataBaseLoadWaiter, esaphMomentsRecylerView, atomicBoolean);
        this.lastTime = lastTime;
    }

    private static final YearTime.Season seasons[] = {
            YearTime.Season.WINTER, YearTime.Season.WINTER,
            YearTime.Season.SPRING, YearTime.Season.SPRING, YearTime.Season.SPRING,
            YearTime.Season.SUMMER, YearTime.Season.SUMMER, YearTime.Season.SUMMER,
            YearTime.Season.FALL, YearTime.Season.FALL, YearTime.Season.FALL,
            YearTime.Season.WINTER
    };

    public YearTime.Season getSeason(long time)
    {
        Calendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(time);
        return seasons[calendar.get(Calendar.MONTH)];
    }

    private static long lastTimeSeasons = 0;

    @Override
    public void run()
    {
        super.run();

        final List<Object> list = new ArrayList<>();

        try
        {
            int st = super.getStartFrom()[0];
            SQLChats sqlChats = new SQLChats(super.getSoftReferenceContext().get());
            list.addAll(sqlChats.getByAllWithDatum(st, this.lastTime));
            sqlChats.close();

            int size = list.size();
            for(int counter = 0; counter < size; counter++)
            {
                if(list.get(counter) instanceof DatumList)
                {
                    long millisCached = ((DatumList)list.get(counter)).getMillis();
                    YearTime.Season newSeason = getSeason(millisCached);
                    if(lastTimeSeasons == 0 || getSeason(lastTimeSeasons) != newSeason)
                    {
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(millisCached);
                    }
                    lastTimeSeasons = millisCached;
                }
            }
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "RunnableAlgorythmLoadAll, background() failed: " + ec);
        }
        finally
        {
            System.out.println("Startgin from other side_ FINISHED LOADING: " + list.size());
            publish(list);
        }
    }
}
