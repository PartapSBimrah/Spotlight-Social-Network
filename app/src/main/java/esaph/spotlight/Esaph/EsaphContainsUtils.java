/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.Esaph;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.nio.file.StandardWatchEventKinds;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import esaph.spotlight.databases.SQLChats;
import esaph.spotlight.navigation.spotlight.Chats.Messages.SavedInfo;

public class EsaphContainsUtils
{
    private static ExecutorService executorServiceContains = Executors.newFixedThreadPool(3);
    public static void hasSaved(Context context,
                                   SaverCodeExecutionCallback saverCodeExecutionCallback,
                                   long UID_POST,
                                   long SAVER_ID)
    {
        executorServiceContains.submit(new LookingUpSaver(context,
                saverCodeExecutionCallback,
                UID_POST,
                SAVER_ID));
    }


    public static void hasSaved(Context context,
                                SaverCodeExecutionCallback saverCodeExecutionCallback,
                                long UID_POST,
                                long SAVER_ID,
                                Handler handler)
    {
        handler.post(new LookingUpSaver(context,
                saverCodeExecutionCallback,
                UID_POST,
                SAVER_ID,
                handler));
    }


    public interface SaverCodeExecutionCallback
    {
        void onExecute(boolean hasSaved);
    }

    private static Handler handlerUI = new Handler(Looper.getMainLooper());

    private static class LookingUpSaver implements Runnable
    {
        private WeakReference<Context> contextWeakReference;
        private WeakReference<SaverCodeExecutionCallback> saverCodeExecutionCallbackWeakReference;
        private long MESSAGE_ID;
        private long SAVER_ID;
        private Handler handler; //To post message on calling thread.

        public LookingUpSaver(Context context,
                              SaverCodeExecutionCallback saverCodeExecutionCallbackWeakReference,
                              long MESSAGE_ID,
                              long SAVER_ID)
        {
            this.contextWeakReference = new WeakReference<Context>(context);
            this.saverCodeExecutionCallbackWeakReference = new WeakReference<SaverCodeExecutionCallback>(saverCodeExecutionCallbackWeakReference);
            this.MESSAGE_ID = MESSAGE_ID;
            this.SAVER_ID = SAVER_ID;
        }

        public LookingUpSaver(Context context,
                              SaverCodeExecutionCallback saverCodeExecutionCallbackWeakReference,
                              long MESSAGE_ID,
                              long SAVER_ID,
                              Handler handler)
        {
            this.contextWeakReference = new WeakReference<Context>(context);
            this.saverCodeExecutionCallbackWeakReference = new WeakReference<SaverCodeExecutionCallback>(saverCodeExecutionCallbackWeakReference);
            this.MESSAGE_ID = MESSAGE_ID;
            this.SAVER_ID = SAVER_ID;
            this.handler = handler;
        }

        @Override
        public void run()
        {
            SQLChats sqlChats = null;
            try
            {
                sqlChats = new SQLChats(this.contextWeakReference.get());
                final boolean hasSaved = sqlChats.hasSaved(MESSAGE_ID, SAVER_ID);

                if(handler != null)
                {
                    handler.post(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            SaverCodeExecutionCallback saverCodeExecutionCallback = saverCodeExecutionCallbackWeakReference.get();
                            if(saverCodeExecutionCallback != null)
                            {
                                saverCodeExecutionCallback.onExecute(hasSaved);
                            }
                        }
                    });
                }
                else
                {
                    handlerUI.post(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            SaverCodeExecutionCallback saverCodeExecutionCallback = saverCodeExecutionCallbackWeakReference.get();
                            if(saverCodeExecutionCallback != null)
                            {
                                saverCodeExecutionCallback.onExecute(hasSaved);
                            }
                        }
                    });
                }
            }
            catch (Exception ec)
            {
                Log.i(getClass().getName(), "LookingUpSaver run() failed: " + ec);
            }
            finally
            {
                if(sqlChats != null)
                {
                    sqlChats.close();
                }
            }
        }
    }
}
