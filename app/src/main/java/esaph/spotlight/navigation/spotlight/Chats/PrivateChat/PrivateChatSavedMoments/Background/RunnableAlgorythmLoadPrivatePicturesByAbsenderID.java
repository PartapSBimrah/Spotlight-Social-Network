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
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import esaph.spotlight.Esaph.EsaphBackgroundLoadingHandle.EsaphDataLoader;
import esaph.spotlight.databases.SQLChats;
import esaph.spotlight.navigation.spotlight.Chats.ListeChat.ChatPartner;
import esaph.spotlight.navigation.spotlight.Moments.AdapterSorting.EsaphMomentsRecylerView;
import esaph.spotlight.navigation.spotlight.Moments.DataBaseLoadWaiter;

public class RunnableAlgorythmLoadPrivatePicturesByAbsenderID extends EsaphDataLoader
{
    private long _ID_ABSENDER;
    private long KEY_CHAT;
    private WeakReference<Context> context;
    private long lastMillis;

    public RunnableAlgorythmLoadPrivatePicturesByAbsenderID(Context context,
                                                            DataBaseLoadWaiter dataBaseLoadWaiter,
                                                            EsaphMomentsRecylerView esaphMomentsRecylerView,
                                                            AtomicBoolean atomicBoolean,
                                                            long _ID_ABSENDER,
                                                            long KEY_CHAT,
                                                            long lastMillis)
    {
        super(context, dataBaseLoadWaiter, esaphMomentsRecylerView, atomicBoolean);
        this.lastMillis = lastMillis;
        this.context = new WeakReference<Context>(context);
        this._ID_ABSENDER = _ID_ABSENDER;
        this.KEY_CHAT = KEY_CHAT;
    }

    @Override
    public void run()
    {
        super.run();

        final List<Object> toReturn = new ArrayList<>();
        try
        {
            int totalCount = super.getStartFrom()[0];
            SQLChats sqlChats = new SQLChats(this.context.get());
            toReturn.addAll(sqlChats.getPersonalMomentsSavedWithDatumOnlyFromOneAbsender(this._ID_ABSENDER, this.KEY_CHAT, totalCount, this.lastMillis));
            sqlChats.close();
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "RunnableAlgorythmLoadPrivatePicturesByAbsenderID, doinBackground() failed: " + ec);
        }
        finally
        {
            publish(toReturn);
        }
    }

}
