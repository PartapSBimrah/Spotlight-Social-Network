/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.navigation.navigationLeftSite.VerticalViewPager.Pages;

import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;
import esaph.spotlight.databases.SQLChats;

import android.util.Log;

import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class RunnableLoadMoreStoryViewerTodayFlipper implements Runnable
{
    private static AtomicBoolean obLock = new AtomicBoolean(false);
    private WeakReference<Context> contextWeakReference;
    private SoftReference<LoadingWaiter> loadingWaiterSoftReference;
    private int startFrom;

    public RunnableLoadMoreStoryViewerTodayFlipper(Context context,
                                                   LoadingWaiter loadingWaiter,
                                                   int startFrom)
    {
        this.contextWeakReference = new WeakReference<>(context);
        this.loadingWaiterSoftReference = new SoftReference<>(loadingWaiter);
        this.startFrom = startFrom;
    }


    public interface LoadingWaiter
    {
        void dataLoaded(List<Object> data);
    }

    @Override
    public void run()
    {
        if(!RunnableLoadMoreStoryViewerTodayFlipper.obLock.compareAndSet(false, true))
            return;


        SQLChats sqlChats = null;
        final List<Object> list = new ArrayList<>();
        try
        {
            sqlChats = new SQLChats(this.contextWeakReference.get());
            list.addAll(sqlChats.getTodayMyPosts(startFrom));
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "RunnableLoadMoreStoryViewerTodayFlipper run() failed: " + ec);
        }
        finally
        {
            if(sqlChats != null)
            {
                sqlChats.close();
            }

            ((AppCompatActivity)contextWeakReference.get()).runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    if(loadingWaiterSoftReference.get() != null)
                    {
                        loadingWaiterSoftReference.get().dataLoaded(list);
                    }
                    RunnableLoadMoreStoryViewerTodayFlipper.obLock.set(false);
                }
            });
        }
    }
}
