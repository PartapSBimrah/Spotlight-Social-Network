/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.navigation.spotlight.Moments.SortingFragment.HomeView.Background;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import esaph.spotlight.Esaph.EsaphLifeCloudBackup.LifeCloudUpload;
import esaph.spotlight.databases.SQLHashtags;
import esaph.spotlight.databases.SQLLifeCloud;
import esaph.spotlight.navigation.spotlight.Moments.AdapterSorting.EsaphMomentsRecylerView;

public class AsyncAlgorythmLoadHomeView extends AsyncTask<Void, Void, List<Object>>
{
    private WeakReference<Context> context;
    private WeakReference<EsaphMomentsRecylerView> esaphMomentsRecylerViewWeakReference;

    public AsyncAlgorythmLoadHomeView(Context context,
                                      EsaphMomentsRecylerView esaphMomentsRecylerView)
    {
        this.context = new WeakReference<Context>(context);
        this.esaphMomentsRecylerViewWeakReference = new WeakReference<EsaphMomentsRecylerView>(esaphMomentsRecylerView);
    }

    @Override
    protected List<Object> doInBackground(Void... params)
    {
        try
        {
            SQLHashtags sqlHashtags = new SQLHashtags(context.get());
            SQLLifeCloud sqlLifeCloud = new SQLLifeCloud(context.get());
            List<Object> listAllDonte = new ArrayList<>();

            List<LifeCloudUpload> listCloudObjects = sqlLifeCloud.getLatestLifeCloudPost();
            if(listCloudObjects.isEmpty())
            {
                listCloudObjects.add(new LifeCloudUpload());
            }
            listAllDonte.addAll(listCloudObjects);


            List<Object> listHashtags = new ArrayList<>();
            listHashtags.addAll(sqlHashtags.getLatestHashtag());

            if(listHashtags.isEmpty())
            {
               // listHashtags.add(new EsaphHashtag());
            }

            listAllDonte.addAll(listHashtags);
            sqlLifeCloud.close();
            sqlHashtags.close();
            return listAllDonte;
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "AsyncAlgorythmLoadHomeView, background() failed: " + ec);
            return new ArrayList<>();
        }
    }


    @Override
    protected void onPostExecute(List<Object> objects)
    {
        super.onPostExecute(objects);
        EsaphMomentsRecylerView esaphMomentsRecylerView = (EsaphMomentsRecylerView) esaphMomentsRecylerViewWeakReference.get();
        if(esaphMomentsRecylerView != null)
        {
            esaphMomentsRecylerView.pushNewDataInAdapter(objects);
            esaphMomentsRecylerView.notifyDataSetChanged();
        }
    }
}
