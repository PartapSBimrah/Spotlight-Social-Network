/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.navigation.kamera.PostEditingFragments.EsaphTagging;

import android.content.Context;
import android.util.Log;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import esaph.spotlight.Esaph.EsaphBackgroundLoadingHandle.EsaphDataLoader;
import esaph.spotlight.databases.SQLHashtags;
import esaph.spotlight.navigation.spotlight.Moments.AdapterSorting.EsaphMomentsRecylerView;
import esaph.spotlight.navigation.spotlight.Moments.DataBaseLoadWaiter;

public class AsyncLoadMoreHashTags extends EsaphDataLoader
{
    public AsyncLoadMoreHashTags(Context context,
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

        List<Object> objectList = null;
        try
        {
            int startFrom = super.getStartFrom()[0];
            SQLHashtags sqlHashtags = new SQLHashtags(super.getSoftReferenceContext().get());

            if(startFrom <= 0)
            {
                objectList = sqlHashtags.getMostUsedHashtags();
            }
            else
            {
                objectList = sqlHashtags.getAllHashtagLimited(startFrom);
            }

            sqlHashtags.close();
            System.out.println("DU BIST DUMM: "  + startFrom + " LOAD: " + objectList.size());
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "AsyncLoadMoreHashTags run() failed: " + ec);
        }
        finally
        {
            publish(objectList);
        }
    }
}
