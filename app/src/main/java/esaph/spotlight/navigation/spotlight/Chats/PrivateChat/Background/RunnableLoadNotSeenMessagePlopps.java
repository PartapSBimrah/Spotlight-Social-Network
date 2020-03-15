package esaph.spotlight.navigation.spotlight.Chats.PrivateChat.Background;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import esaph.spotlight.navigation.spotlight.Chats.ListeChat.ChatPartner;
import esaph.spotlight.databases.SQLChats;

public class RunnableLoadNotSeenMessagePlopps implements Runnable
{
    private WeakReference<Context> contextWeakReference;
    private WeakReference<PloppListLoadWaiter> ploppListLoadWaiterWeakReference;
    private ChatPartner chatPartner;

    public RunnableLoadNotSeenMessagePlopps(Context context,
                                            ChatPartner chatPartner,
                                            PloppListLoadWaiter ploppListLoadWaiter)
    {
        this.contextWeakReference = new WeakReference<Context>(context);
        this.chatPartner = chatPartner;
        this.ploppListLoadWaiterWeakReference = new WeakReference<PloppListLoadWaiter>(ploppListLoadWaiter);
    }

    public interface PloppListLoadWaiter
    {
        void onDataLoaded(List<Fragment> list);
    }

    @Override
    public void run()
    {
        final List<Fragment> list = new ArrayList<>();

        try
        {
            SQLChats sqlChats = new SQLChats(this.contextWeakReference.get());
            list.addAll(sqlChats.getAllNewMessagesPlopps(this.chatPartner.getUID_CHATPARTNER()));
            sqlChats.close();
        }
        catch (Exception ec)
        {
            Log.i(getClass().getName(), "RunnableLoadNotSeenMessagePlopps() failed: " + ec);
        }
        finally
        {
            new Handler(Looper.getMainLooper()).post(new Runnable()
            {
                @Override
                public void run()
                {
                    PloppListLoadWaiter ploppListLoadWaiter = ploppListLoadWaiterWeakReference.get();
                    if(ploppListLoadWaiter != null)
                    {
                        ploppListLoadWaiter.onDataLoaded(list);
                    }
                }
            });
        }
    }
}
