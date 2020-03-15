/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.navigation.spotlight.Chats.PrivateChat.Background;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import esaph.spotlight.databases.SQLChats;

public class AsyncLoadAllPrivateMessages implements Runnable
{
    private WeakReference<Context> contextWeakReference;
    private WeakReference<LoadedConversationMessagesListener> loadedConversationMessagesListenerWeakReference;
    private AtomicBoolean atomicLock;
    private int startFrom;
    private long UID_CHAT_PARTNER;

    public AsyncLoadAllPrivateMessages(Context context,
                                       int startFrom,
                                       long UID_CHAT_PARTNER,
                                       LoadedConversationMessagesListener loadedConversationMessagesListener,
                                       AtomicBoolean atomicLock)
    {
        this.atomicLock = atomicLock;
        this.contextWeakReference = new WeakReference<Context>(context);
        this.startFrom = startFrom;
        this.UID_CHAT_PARTNER = UID_CHAT_PARTNER;
        this.loadedConversationMessagesListenerWeakReference = new WeakReference<LoadedConversationMessagesListener>(loadedConversationMessagesListener);
    }

    @Override
    public void run()
    {
        final List<Object> list = new ArrayList<>();

        try
        {
            SQLChats sqlChats = new SQLChats(this.contextWeakReference.get());
            list.addAll(sqlChats.getAllCurrentTextualMessages(this.UID_CHAT_PARTNER, this.startFrom));
            Collections.reverse(list);
            sqlChats.close();
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "AsyncLoadAllPrivateMessages() failed: " + ec);
        }
        finally
        {
            new Handler(Looper.getMainLooper()).post(new Runnable()
            {
                @Override
                public void run()
                {
                    LoadedConversationMessagesListener loadedConversationMessagesListener = loadedConversationMessagesListenerWeakReference.get();
                    if(loadedConversationMessagesListener != null)
                    {
                        loadedConversationMessagesListener.onMessagesLoaded(list);
                    }
                    atomicLock.set(false);
                }
            });
        }
    }
}
