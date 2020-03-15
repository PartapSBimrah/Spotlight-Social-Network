/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.navigation.spotlight.GroupChats.Background;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import esaph.spotlight.databases.SQLGroups;
import esaph.spotlight.navigation.spotlight.GroupChats.GroupConversationMessage;
import esaph.spotlight.navigation.spotlight.Moments.MemoryObjects.DatumList;

public class AsyncLoadMoreFromGroupChatMessages extends AsyncTask<Void, Void, List<Object>>
{
    private WeakReference<Context> contextWeakReference;
    private WeakReference<GroupMessageLoadedListener> groupMessageLoadedListenerWeakReference;
    private String GIID;
    private String MyUsername;
    private int startFrom;
    private long lastMessageTimeMillis;

    public AsyncLoadMoreFromGroupChatMessages(Context context, GroupMessageLoadedListener groupMessageLoadedListener, long lastKarstenMillis, String MyUsername, String GIID, int startFrom)
    {
        this.contextWeakReference = new WeakReference<Context>(context);
        this.groupMessageLoadedListenerWeakReference = new WeakReference<GroupMessageLoadedListener>(groupMessageLoadedListener);
        this.GIID = GIID;
        this.MyUsername = MyUsername;
        this.startFrom = startFrom;
        this.lastMessageTimeMillis = lastKarstenMillis;
    }


    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd", Locale.getDefault());
    private void optimizedBubbleSort(List<Object> arr) throws Exception
    {
        for(int pass = 1; pass <= arr.size() -1; pass++)
        {
            for(int compare = 1; compare <= arr.size() - pass; compare++)
            {
                Object first = arr.get(compare - 1);
                long internMillis = 0;

                if(first instanceof GroupConversationMessage)
                {
                    internMillis = ((GroupConversationMessage) first).getUhrzeit();
                }

                Object second = arr.get(compare);
                long objectMillis = 0;

                if(second instanceof GroupConversationMessage)
                {
                    objectMillis = ((GroupConversationMessage) second).getUhrzeit();
                }

                if(objectMillis < internMillis)
                {
                    Object cache = first;
                    first = second;
                    second = cache;
                    arr.set(compare - 1, first);
                    arr.set(compare, second);
                }
            }
        }

        for(int counter = 0; counter < arr.size();counter++)
        {
            Object first = arr.get(counter);
            if(first instanceof GroupConversationMessage)
            {
                GroupConversationMessage conversationMessage = (GroupConversationMessage) first;

                if(!simpleDateFormat.format(lastMessageTimeMillis).equals(simpleDateFormat.format(conversationMessage.getUhrzeit())))
                {
                    if(lastMessageTimeMillis > -1)
                    {
                        arr.add(counter, new DatumList("",
                                simpleDateFormat.format(conversationMessage.getUhrzeit()),
                                conversationMessage.getUhrzeit()));
                    }
                    else
                    {
                        arr.add(counter, new DatumList("",
                                simpleDateFormat.format(conversationMessage.getUhrzeit()),
                                conversationMessage.getUhrzeit()));
                    }

                    lastMessageTimeMillis = conversationMessage.getUhrzeit();
                }
            }
        }
    }

    @Override
    protected List<Object> doInBackground(Void... voids)
    {
        try
        {
            SQLGroups sqlGroups = new SQLGroups(this.contextWeakReference.get());
            List<Object> list = new ArrayList<>();
            list.addAll(sqlGroups.getAllConversationMessagesFromGroup(this.MyUsername, this.GIID, this.startFrom));
            sqlGroups.close();
            this.optimizedBubbleSort(list);
            return list;
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "AsyncLoadMoreFromGroupChatMessages() failed: " + ec);
        }

        return new ArrayList<>();
    }

    @Override
    protected void onPostExecute(List<Object> groupConversationMessages)
    {
        super.onPostExecute(groupConversationMessages);
        GroupMessageLoadedListener groupMessageLoadedListener = this.groupMessageLoadedListenerWeakReference.get();
        if(groupConversationMessages != null)
        {
            groupMessageLoadedListener.onGroupMessagesLoaded(groupConversationMessages);
        }
    }
}
