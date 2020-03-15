package esaph.spotlight.Esaph.EsaphTimeCalculations;

import java.util.Date;

public class EsaphMinutes
{
    public static long minutesBetween(Date date, Date second)
    {
        long diff;
        if(date.getTime() > second.getTime())
        {
            diff = date.getTime() - second.getTime();
        }
        else
        {
            diff = second.getTime() - date.getTime();
        }

        long seconds = diff / 1000;
        return seconds / 60;
    }
}
