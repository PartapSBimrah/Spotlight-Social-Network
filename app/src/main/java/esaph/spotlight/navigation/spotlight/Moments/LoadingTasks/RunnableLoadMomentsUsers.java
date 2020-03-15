/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.navigation.spotlight.Moments.LoadingTasks;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import esaph.spotlight.Esaph.EsaphBackgroundLoadingHandle.EsaphDataLoader;
import esaph.spotlight.databases.SQLChats;
import esaph.spotlight.navigation.spotlight.Moments.AdapterSorting.EsaphMomentsRecylerView;
import esaph.spotlight.navigation.spotlight.Moments.DataBaseLoadWaiter;

public class RunnableLoadMomentsUsers extends EsaphDataLoader
{
    public RunnableLoadMomentsUsers(Context context,
                                    DataBaseLoadWaiter dataBaseLoadWaiter,
                                    EsaphMomentsRecylerView esaphMomentsRecylerView,
                                    AtomicBoolean atomicBoolean)
    {
        super(context, dataBaseLoadWaiter, esaphMomentsRecylerView, atomicBoolean);
    }

    @Override
    public void run()
    {
        super.run();

        final List<Object> list = new ArrayList<>();
        try
        {
            SQLChats sqlChats = new SQLChats(super.getSoftReferenceContext().get());
            list.addAll(sqlChats.getMomentsListWithUser());
            sqlChats.close();
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "RunnableLoadMomentsUsers() failed: " + ec);
        }
        finally
        {
            publish(list);
        }
    }
}
