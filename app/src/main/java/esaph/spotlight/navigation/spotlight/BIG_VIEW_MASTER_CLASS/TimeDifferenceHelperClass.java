package esaph.spotlight.navigation.spotlight.BIG_VIEW_MASTER_CLASS;

import android.content.res.Resources;
import java.util.Date;

import esaph.spotlight.Esaph.EsaphTimeCalculations.EsaphDays;
import esaph.spotlight.Esaph.EsaphTimeCalculations.EsaphHours;
import esaph.spotlight.Esaph.EsaphTimeCalculations.EsaphMinutes;
import esaph.spotlight.Esaph.EsaphTimeCalculations.EsaphWeeks;
import esaph.spotlight.R;

public class TimeDifferenceHelperClass
{
    public static String getDateDiff(Resources resources, long date1, long date2)
    {
        StringBuilder stringBuilder = new StringBuilder();
        Date dateTime = new Date(date1);
        Date dateTime2 = new Date(date2);

        long hours = EsaphHours.hoursBetween(dateTime, dateTime2);
        long minutes = EsaphMinutes.minutesBetween(dateTime, dateTime2);
        long days = EsaphDays.daysBetween(dateTime, dateTime2);
        long weeks = EsaphWeeks.weeksBetween(dateTime, dateTime2);

        if(hours <= 0 && minutes <= 0)
        {
            stringBuilder.append(resources.getString(R.string.txt_gerade));
            return stringBuilder.toString();
        }

        if(weeks >= 1)
        {
            stringBuilder.append(resources.getString(R.string.txt_sWeeks, weeks));
        }
        else if(days >= 1)
        {
            stringBuilder.append(resources.getString(R.string.txt_sDays, days));
        }
        else
        {
            if(hours > 0)
            {
                if(minutes > 0)
                {
                    stringBuilder.append(resources.getString(R.string.txt_sHoursSingle, hours));
                }
                else
                {
                    stringBuilder.append(resources.getString(R.string.txt_sHoursWithMinutes, hours, minutes));
                }
            }
            else
            {
                stringBuilder.append(resources.getString(R.string.txt_sMinutes, minutes));
            }
        }
        return stringBuilder.toString();
    }

}
