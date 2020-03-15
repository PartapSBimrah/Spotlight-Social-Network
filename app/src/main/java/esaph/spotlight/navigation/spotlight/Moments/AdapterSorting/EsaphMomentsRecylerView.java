/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.navigation.spotlight.Moments.AdapterSorting;

import android.os.Handler;
import android.os.Looper;
import androidx.annotation.MainThread;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Filter;
import android.widget.Filterable;

import java.lang.ref.WeakReference;
import java.util.List;

import esaph.spotlight.navigation.spotlight.BIG_VIEW_MASTER_CLASS.ViewPagerDataSetChangedListener;

public abstract class EsaphMomentsRecylerView extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable, ViewPagerDataSetChangedListener
{
    private WeakReference<View>[] views;
    public EsaphMomentsRecylerView(WeakReference<View>[] views)
    {
        this.views = views;
    }

    private final Handler handlerUI = new Handler(Looper.getMainLooper());

    public abstract Filter getFilter();
    public abstract List<Object> getListDataDisplay();

    @MainThread
    public void pushNewDataInAdapter(List<Object> data)
    {
        if(data.isEmpty())
            return;

        pushNewDataInAdapterThreadSafe(data);
        notifyDataSetChangeBypass();
    }

    @MainThread
    public abstract void pushNewDataInAdapterThreadSafe(List<Object> data);

    public abstract void clearAllWithNotify();
    public abstract void removeSinglePostByPID(String PID);

    public int[] getObjectCounts()
    {
        return getObjectCountsThreadSafe();
    }

    public abstract int[] getObjectCountsThreadSafe();
    public abstract boolean isEmpty();

    @MainThread
    public void notifyDataSetChangeBypass()
    {
        notifyDataSetChanged();
        if(isEmpty())
        {
            for(WeakReference<View> viewWeakReference : views)
            {
                View view = viewWeakReference.get();
                if(view != null)
                {
                    view.setVisibility(View.VISIBLE);
                }
            }
        }
        else
        {
            for(WeakReference<View> viewWeakReference : views)
            {
                View view = viewWeakReference.get();
                if(view != null)
                {
                    view.setVisibility(View.GONE);
                }
            }
        }
    }

    public abstract void addFooter();
    public abstract void removeFooter();


    public Handler getHandlerUI() {
        return handlerUI;
    }
}
