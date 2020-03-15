/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.navigation.spotlight.Chats.PrivateChat.PrivateChatSavedMoments.Background;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import esaph.spotlight.Esaph.EsaphBackgroundLoadingHandle.EsaphDataLoader;
import esaph.spotlight.databases.SQLChats;
import esaph.spotlight.navigation.spotlight.Moments.AdapterSorting.EsaphMomentsRecylerView;
import esaph.spotlight.navigation.spotlight.Moments.DataBaseLoadWaiter;

public class RunnableLoadMoreFromPrivateUserMomentPosts extends EsaphDataLoader
{
    private long chatPartner;
    private long lastMillis;
    private boolean withDate;

    public RunnableLoadMoreFromPrivateUserMomentPosts(Context context, DataBaseLoadWaiter dataBaseLoadWaiter, EsaphMomentsRecylerView esaphMomentsRecylerView,
                                                      long chatPartner,
                                                      long lastMillis,
                                                      boolean withDate,
                                                      AtomicBoolean atomicBoolean)
    {
        super(context, dataBaseLoadWaiter, esaphMomentsRecylerView, atomicBoolean);
        this.lastMillis = lastMillis;
        this.withDate = withDate;
        this.chatPartner = chatPartner;
    }
    private int startFromTotalCount = 0;

    @Override
    public void run()
    {
        super.run();

        final List<Object> toReturn = new ArrayList<>();
        try
        {
            startFromTotalCount = super.getStartFrom()[0];

            SQLChats sqlChats = new SQLChats(super.getSoftReferenceContext().get());
            if(this.withDate)
            {
                toReturn.addAll(sqlChats.getPersonalMomentsSavedWithDatum(this.chatPartner, this.startFromTotalCount, this.lastMillis));
            }
            else
            {
                toReturn.addAll(sqlChats.getPersonalMomentsSaved(this.chatPartner, this.startFromTotalCount));
            }
            sqlChats.close();
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "RunnableLoadMoreFromPrivateUserMomentPosts, doinBackground() failed: " + ec);
        }
        finally {
            publish(toReturn);
        }
    }

}
