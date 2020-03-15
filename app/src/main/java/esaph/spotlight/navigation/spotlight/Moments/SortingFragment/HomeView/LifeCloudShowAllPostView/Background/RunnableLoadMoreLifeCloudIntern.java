/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.navigation.spotlight.Moments.SortingFragment.HomeView.LifeCloudShowAllPostView.Background;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import esaph.spotlight.Esaph.EsaphBackgroundLoadingHandle.EsaphDataLoader;
import esaph.spotlight.databases.SQLLifeCloud;
import esaph.spotlight.navigation.spotlight.Moments.AdapterSorting.EsaphMomentsRecylerView;
import esaph.spotlight.navigation.spotlight.Moments.DataBaseLoadWaiter;

public class RunnableLoadMoreLifeCloudIntern extends EsaphDataLoader
{
    private long lastTime;

    public RunnableLoadMoreLifeCloudIntern(Context context,
                                           DataBaseLoadWaiter dataBaseLoadWaiter,
                                           EsaphMomentsRecylerView esaphMomentsRecylerView,
                                           AtomicBoolean atomicBoolean,
                                           long lastTime)
    {
        super(context, dataBaseLoadWaiter, esaphMomentsRecylerView, atomicBoolean);
        this.lastTime = lastTime;
    }

    @Override
    public void run()
    {
        super.run();

        final List<Object> list = new ArrayList<>();

        try
        {
            SQLLifeCloud sqlLifeCloud = new SQLLifeCloud(super.getSoftReferenceContext().get());
            list.addAll(sqlLifeCloud.getAllLifeCloudUploadsWithDatumHolder(super.getStartFrom()[0], this.lastTime));
            sqlLifeCloud.close();
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "RunnableLoadMoreLifeCloudIntern, background() failed: " + ec);
        }
        finally
        {
            publish(list);
        }
    }
}
