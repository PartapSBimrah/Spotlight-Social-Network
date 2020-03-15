package esaph.spotlight.Esaph.EsaphTimeCalculations;

import java.util.Date;

public class EsaphDays
{
    public static long daysBetween(Date date, Date second)
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
        long minutes = seconds / 60;
        long hours = minutes / 60;
        return hours / 24;
    }
}
