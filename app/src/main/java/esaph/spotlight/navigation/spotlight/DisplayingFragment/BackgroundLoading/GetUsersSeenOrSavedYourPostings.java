/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.navigation.spotlight.DisplayingFragment.BackgroundLoading;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import esaph.spotlight.databases.SQLChats;
import esaph.spotlight.navigation.spotlight.Chats.Messages.ConversationMessage;
import esaph.spotlight.navigation.spotlight.Moments.UserSeenOrSavedMoment;


public class GetUsersSeenOrSavedYourPostings implements Runnable
{
    private WeakReference<Context> contextWeakReference;
    private WeakReference<UserSeenOrSavedLoadingListener> userSeenOrSavedLoadingListenerWeakReference;
    private ConversationMessage conversationMessage;
    private int callingPosition;

    public interface UserSeenOrSavedLoadingListener
    {
        void onLoaded(List<UserSeenOrSavedMoment> list, int callingPosition);
    }

    public GetUsersSeenOrSavedYourPostings(Context context, ConversationMessage conversationMessage, UserSeenOrSavedLoadingListener userSeenOrSavedLoadingListener,
                                            int callingPosition)
    {
        this.callingPosition = callingPosition;
        this.contextWeakReference = new WeakReference<Context>(context);
        this.conversationMessage = conversationMessage;
        this.userSeenOrSavedLoadingListenerWeakReference = new WeakReference<UserSeenOrSavedLoadingListener>(userSeenOrSavedLoadingListener);
    }

    @Override
    public void run()
    {
        final List<UserSeenOrSavedMoment> list = new ArrayList<>();

        try
        {
            SQLChats sqlChats = new SQLChats(contextWeakReference.get());
            list.addAll(sqlChats.getUsersSeenOrSavedMemorys(this.conversationMessage.getIMAGE_ID()));
            sqlChats.close();
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "GetUsersSeenOrSavedYourPostings() failed: " + ec);
        }
        finally
        {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    UserSeenOrSavedLoadingListener userSeenOrSavedLoadingListener = (UserSeenOrSavedLoadingListener) userSeenOrSavedLoadingListenerWeakReference.get();
                    if(userSeenOrSavedLoadingListener != null)
                    {
                        userSeenOrSavedLoadingListener.onLoaded(list, callingPosition);
                    }
                }
            });
        }
    }
}