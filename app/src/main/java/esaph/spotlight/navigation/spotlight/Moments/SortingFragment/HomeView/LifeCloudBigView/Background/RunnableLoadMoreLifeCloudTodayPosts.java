/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.navigation.spotlight.Moments.SortingFragment.HomeView.LifeCloudBigView.Background;

import android.content.Context;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.List;

import esaph.spotlight.Esaph.EsaphLifeCloudBackup.LifeCloudUpload;
import esaph.spotlight.databases.SQLLifeCloud;
import esaph.spotlight.navigation.spotlight.BIG_VIEW_MASTER_CLASS.ViewPagerAdapterGetList;

public class RunnableLoadMoreLifeCloudTodayPosts implements Runnable
{
    private WeakReference<Context> contextWeakReference;
    private WeakReference<ViewPagerAdapterGetList> viewPagerAdapterGetListWeakReference;
    private WeakReference<ViewPager> viewPagerWeakReference;
    private WeakReference<ViewPager.OnPageChangeListener> onPageChangeListenerWeakReference;

    public RunnableLoadMoreLifeCloudTodayPosts(Context context,
                                               ViewPagerAdapterGetList masterClassGetList,
                                               ViewPager viewPager,
                                               ViewPager.OnPageChangeListener onPageChangeListener)
    {
        this.contextWeakReference = new WeakReference<>(context);
        this.viewPagerAdapterGetListWeakReference = new WeakReference<>(masterClassGetList);
        this.viewPagerWeakReference = new WeakReference<>(viewPager);
        this.onPageChangeListenerWeakReference = new WeakReference<>(onPageChangeListener);
    }

    @Override
    public void run()
    {
        SQLLifeCloud sqlLifeCloud = null;
        try
        {
            sqlLifeCloud = new SQLLifeCloud(this.contextWeakReference.get());
            final List<LifeCloudUpload> list = sqlLifeCloud.getAllLatestLifeCloudUploadFromTodayLimited(
                    viewPagerAdapterGetListWeakReference.get().getCount());

            if(list.isEmpty())
                return;

            ((AppCompatActivity)contextWeakReference.get()).runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        viewPagerAdapterGetListWeakReference.get().getList().addAll(list);
                        viewPagerAdapterGetListWeakReference.get().notifyDataSetChanged();
                        viewPagerWeakReference.get().post(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                onPageChangeListenerWeakReference.get().onPageSelected(viewPagerWeakReference.get().getCurrentItem());
                            }
                        });
                    }
                    catch (Exception ec)
                    {
                    }
                }
            });
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "RunnableLoadMoreLifeCloudTodayPosts run() failed: " + ec);
        }
        finally
        {
            if(sqlLifeCloud != null)
            {
                sqlLifeCloud.close();
            }
        }
    }
}
