/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.navigation.spotlight.Moments.InternViews.ViewHashtags.Background;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import esaph.spotlight.Esaph.EsaphBackgroundLoadingHandle.EsaphDataLoader;
import esaph.spotlight.databases.SQLHashtags;
import esaph.spotlight.navigation.spotlight.Moments.AdapterSorting.EsaphMomentsRecylerView;
import esaph.spotlight.navigation.spotlight.Moments.DataBaseLoadWaiter;

public class RunnableLoadMoreSavedConversationsByHashtag extends EsaphDataLoader
{
    private String hashtagName;

    public RunnableLoadMoreSavedConversationsByHashtag(Context context, DataBaseLoadWaiter dataBaseLoadWaiter, EsaphMomentsRecylerView esaphMomentsRecylerView,
                                                       AtomicBoolean atomicBoolean,
                                                       String hashtagName) {
        super(context, dataBaseLoadWaiter, esaphMomentsRecylerView, atomicBoolean);
        this.hashtagName = hashtagName;
    }

    @Override
    public void run()
    {
        super.run();

        final List<Object> toReturn = new ArrayList<>();
        try
        {
            SQLHashtags sqlHashtags = new SQLHashtags(super.getSoftReferenceContext().get());
            toReturn.addAll(sqlHashtags.getAllPostingsContainsHashtag(super.getStartFrom()[0], this.hashtagName));
            sqlHashtags.close();
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "RunnableLoadMoreSavedConversationsByHashtag, doinBackground() failed: " + ec);
        }
        finally
        {
            publish(toReturn);
        }
    }
}
