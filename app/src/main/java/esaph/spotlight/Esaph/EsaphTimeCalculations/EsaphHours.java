package esaph.spotlight.Esaph.EsaphTimeCalculations;

import java.util.Date;

public class EsaphHours
{
    public static long hoursBetween(Date date, Date second)
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
        return minutes / 60;
    }
}
