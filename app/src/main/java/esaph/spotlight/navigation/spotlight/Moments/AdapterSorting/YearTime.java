/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.navigation.spotlight.Moments.AdapterSorting;

import java.util.Calendar;
import java.util.GregorianCalendar;

import esaph.spotlight.navigation.spotlight.Chats.Messages.ConversationMessage;

public class YearTime
{
    private Season season;
    private String yeartime;
    private String BestPersons;
    private ConversationMessage conversationMessageLast;

    public YearTime(String Yeartime, String BestPersons, ConversationMessage conversationMessageLast,
                    Season season)
    {
        this.season = season;
        this.yeartime = Yeartime;
        this.BestPersons = BestPersons;
        this.conversationMessageLast = conversationMessageLast;
    }

    public ConversationMessage getConversationMessageLast()
    {
        return conversationMessageLast;
    }

    public enum Season //DO NOT CHANGE ORDER!
    {
        SPRING, //Frühling
        SUMMER, //Sommer
        FALL, //Herbst
        WINTER, //Winter
    }

    public static long[] getSeasonSpacing(Season season, int Year)
    {
        Calendar calendarBegin = new GregorianCalendar();
        calendarBegin.set(Calendar.YEAR, Year);

        Calendar calendarEnd = new GregorianCalendar();
        calendarEnd.set(Calendar.YEAR, Year);

        long[] spacing = new long[2];
        switch (season)
        {
            case SPRING:
                calendarBegin.set(Calendar.MONTH, 3);
                calendarBegin.set(Calendar.DAY_OF_MONTH, 20);
                calendarBegin.set(Calendar.HOUR_OF_DAY, 0);
                calendarBegin.set(Calendar.MINUTE, 0);
                calendarBegin.set(Calendar.MILLISECOND, 0);

                calendarEnd.set(Calendar.MONTH, 6);
                calendarEnd.set(Calendar.DAY_OF_MONTH, 21);
                calendarEnd.set(Calendar.HOUR_OF_DAY, 0);
                calendarEnd.set(Calendar.MINUTE, 0);
                calendarEnd.set(Calendar.MILLISECOND, 0);
                break;

            case SUMMER:
                calendarBegin.set(Calendar.MONTH, 6);
                calendarBegin.set(Calendar.DAY_OF_MONTH, 21);
                calendarBegin.set(Calendar.HOUR_OF_DAY, 0);
                calendarBegin.set(Calendar.MINUTE, 0);
                calendarBegin.set(Calendar.MILLISECOND, 0);

                calendarEnd.set(Calendar.MONTH, 9);
                calendarEnd.set(Calendar.DAY_OF_MONTH, 23);
                calendarEnd.set(Calendar.HOUR_OF_DAY, 0);
                calendarEnd.set(Calendar.MINUTE, 0);
                calendarEnd.set(Calendar.MILLISECOND, 0);
                break;

            case FALL:
                calendarBegin.set(Calendar.MONTH, 9);
                calendarBegin.set(Calendar.DAY_OF_MONTH, 23);
                calendarBegin.set(Calendar.HOUR_OF_DAY, 0);
                calendarBegin.set(Calendar.MINUTE, 0);
                calendarBegin.set(Calendar.MILLISECOND, 0);

                calendarEnd.set(Calendar.MONTH, 12);
                calendarEnd.set(Calendar.DAY_OF_MONTH, 21);
                calendarEnd.set(Calendar.HOUR_OF_DAY, 0);
                calendarEnd.set(Calendar.MINUTE, 0);
                calendarEnd.set(Calendar.MILLISECOND, 0);
                break;

            case WINTER:
                if(calendarBegin.get(Calendar.MONTH) != 12
                        && calendarBegin.get(Calendar.DAY_OF_MONTH) != 21)
                {
                    calendarBegin.add(Calendar.YEAR, -1); //Weil Jahresübergang
                }

                calendarBegin.set(Calendar.MONTH, 12);
                calendarBegin.set(Calendar.DAY_OF_MONTH, 21);
                calendarBegin.set(Calendar.HOUR_OF_DAY, 0);
                calendarBegin.set(Calendar.MINUTE, 0);
                calendarBegin.set(Calendar.MILLISECOND, 0);

                calendarEnd.set(Calendar.MONTH, 3);
                calendarEnd.set(Calendar.DAY_OF_MONTH, 20);
                calendarEnd.set(Calendar.HOUR_OF_DAY, 0);
                calendarEnd.set(Calendar.MINUTE, 0);
                calendarEnd.set(Calendar.MILLISECOND, 0);
                break;
        }

        spacing[0] = calendarBegin.getTimeInMillis();
        spacing[1] = calendarEnd.getTimeInMillis();
        return spacing;
    }

    public String getYeartime() {
        return yeartime;
    }

    public String getBestPersons() {
        return BestPersons;
    }

    public Season getSeason() {
        return season;
    }

    public static class YearTimeDatesMillis
    {
    }
}
